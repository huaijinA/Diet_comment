package com.example.diet_comment.config;

import jakarta.annotation.PreDestroy;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.LocalPortForwarder;
import net.schmizz.sshj.connection.channel.direct.Parameters; // 修正 #1: 导入正确的 Parameters 类
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

@Component
public class SshTunnelConfig {
    private final Logger logger = LoggerFactory.getLogger(SshTunnelConfig.class);
    private volatile SSHClient sshClient;
    private volatile LocalPortForwarder forwarder;

    @Value("${ssh.tunnel.host}")
    private String sshHost;
    @Value("${ssh.tunnel.port:22}")
    private int sshPort;
    @Value("${ssh.tunnel.user}")
    private String sshUser;
    @Value("${ssh.tunnel.password:}")
    private String sshPassword;
    @Value("${ssh.tunnel.privateKey:}")
    private String privateKeyProp;
    @Value("${ssh.tunnel.remoteHost:127.0.0.1}")
    private String remoteHost;
    @Value("${ssh.tunnel.remotePort:3306}")
    private int remotePort;
    @Value("${ssh.tunnel.localPort:3307}")
    private int localPort;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "ssh-tunnel-starter");
        t.setDaemon(true);
        return t;
    });
    private final int maxRetries = 6;
    private final Duration retryInterval = Duration.ofSeconds(10);

    @EventListener(ApplicationReadyEvent.class)
    public void startTunnelAsync() {
        scheduler.schedule(new ConnectTask(0), 0, TimeUnit.SECONDS);
    }

    private class ConnectTask implements Runnable {
        private final int attempt;
        ConnectTask(int attempt) { this.attempt = attempt; }

        @Override
        public void run() {
            if (sshClient != null && sshClient.isConnected()) return;
            try {
                sshClient = new SSHClient();
                sshClient.addHostKeyVerifier(new PromiscuousVerifier());
                sshClient.connect(sshHost, sshPort);

                String resolvedKey = resolvePrivateKey();
                if (resolvedKey != null) {
                    logger.info("Using SSH private key: {}", resolvedKey);
                    KeyProvider keyProvider = sshClient.loadKeys(resolvedKey);
                    sshClient.authPublickey(sshUser, keyProvider);
                } else if (sshPassword != null && !sshPassword.isBlank()) {
                    logger.info("Using SSH password authentication.");
                    sshClient.authPassword(sshUser, sshPassword);
                } else {
                    logger.warn("No SSH private key or password provided; attempting public key auth without a specific file.");
                    sshClient.authPublickey(sshUser);
                }

                // 修正 #2: 使用正确的 Parameters 类构造函数
                var params = new Parameters("127.0.0.1", localPort, remoteHost, remotePort);
                var serverSocket = new ServerSocket();
                serverSocket.setReuseAddress(true);
                // 修正 #3: 直接访问字段 localHost 和 localPort
                serverSocket.bind(new InetSocketAddress(params.getLocalHost(), params.getLocalPort()));

                forwarder = sshClient.newLocalPortForwarder(params, serverSocket);

                Thread forwarderThread = new Thread(() -> {
                    try {
                        forwarder.listen();
                    } catch (IOException e) {
                        if (!"Socket closed".equals(e.getMessage())) {
                            logger.warn("SSH port forwarder listening loop terminated with exception.", e);
                        }
                    }
                }, "ssh-forwarder");
                forwarderThread.setDaemon(true);
                forwarderThread.start();

                logger.info("SSH tunnel established: localhost:{} -> {}:{} via {}@{}", localPort, remoteHost, remotePort, sshUser, sshHost);
            } catch (Exception e) {
                int next = attempt + 1;
                logger.warn("SSH tunnel attempt {} failed: {}", attempt + 1, e.getMessage());
                if (sshClient != null) {
                    try { sshClient.disconnect(); } catch (IOException ignored) {}
                }
                if (next < maxRetries) {
                    long delay = retryInterval.toSeconds();
                    logger.info("Retrying SSH tunnel in {}s (attempt {}/{})", delay, next + 1, maxRetries);
                    scheduler.schedule(new ConnectTask(next), delay, TimeUnit.SECONDS);
                } else {
                    logger.error("SSH tunnel failed after {} attempts; giving up.", maxRetries, e);
                }
            }
        }
    }

    private String resolvePrivateKey() {
        if (privateKeyProp != null && !privateKeyProp.isBlank()) {
            File f = new File(privateKeyProp);
            if (f.exists() && f.isFile()) return f.getAbsolutePath();
        }
        String userHome = System.getProperty("user.home");
        if (userHome == null || userHome.isBlank()) {
            userHome = System.getenv("HOME");
            if (userHome == null || userHome.isBlank()) userHome = System.getenv("USERPROFILE");
        }
        if (userHome == null || userHome.isBlank()) return null;
        List<String> candidates = Arrays.asList(
                userHome + File.separator + ".ssh" + File.separator + "id_ed25519",
                userHome + File.separator + ".ssh" + File.separator + "id_rsa",
                userHome + File.separator + ".ssh" + File.separator + "id_dsa"
        );
        Optional<String> found = candidates.stream()
                .map(Path::of)
                .filter(p -> Files.exists(p) && Files.isRegularFile(p))
                .map(Path::toString)
                .findFirst();
        return found.orElse(null);
    }

    @PreDestroy
    public void stopTunnel() {
        try {
            if (forwarder != null) {
                forwarder.close();
                logger.info("SSH port forwarder stopped.");
            }
            if (sshClient != null && sshClient.isConnected()) {
                sshClient.disconnect();
                logger.info("SSH client disconnected.");
            }
        } catch (Exception e) {
            logger.warn("Error while closing SSH tunnel", e);
        } finally {
            scheduler.shutdownNow();
        }
    }
}