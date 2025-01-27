package com.terminal.ssh_terminal.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Component
public class SshWebsocketHandler implements WebSocketHandler {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(SshWebsocketHandler.class);

    private final Map<String, Session> sshSessions = new ConcurrentHashMap<>();
    private final Map<String, Boolean> isAuthenticated = new ConcurrentHashMap<>();
    private final Map<String, OutputStream> shellOutputStreams = new ConcurrentHashMap<>();




    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
        String payload = webSocketMessage.getPayload().toString().trim();
        payload = payload.replaceAll("^\"|\"$", "");
        logger.info("WebSocket message received: {}", payload);

        logger.info("Payload starts with: {}", payload.substring(0, Math.min(payload.length(), 12)));

//        for (char c : payload.toCharArray()) {
//            logger.info("Char: {} Code: {}", c, (int) c);
//        }



        try {
            if (payload.startsWith("credentials")) {
                handleCredentials(webSocketSession, payload.substring("credentials:".length()).trim());
                startShellSession(webSocketSession); // Initialize shell session after authentication
            }else{
//            if (payload.startsWith("credentials")) {
//                handleCredentials(webSocketSession, payload.substring("credentials:".length()).trim());
//            } else {
                Boolean authenticated = isAuthenticated.getOrDefault(webSocketSession.getId(), false);
                if (Boolean.TRUE.equals(authenticated)) {
                    executeCommand(webSocketSession, payload.trim());
                } else {
                    logger.warn("Session {} is not authenticated.", webSocketSession.getId());
                    webSocketSession.sendMessage(new TextMessage("Error: SSH not authenticated. Provide credentials first."));
                }
            }
        } catch (IOException | JSchException e) {
            logger.error("Error handling message for session {}: {}", webSocketSession.getId(), e.getMessage(), e);
            webSocketSession.sendMessage(new TextMessage("Error: " + e.getMessage()));
        }
    }

    private void handleCredentials(WebSocketSession webSocketSession, String credentials) throws IOException {
        logger.info("Processing credentials for session: {}", webSocketSession.getId());

        String[] parts = credentials.split(":");
        System.out.println("-------->"+credentials);
        if (parts.length != 3) {
            logger.warn("Invalid credentials format provided by session: {}", webSocketSession.getId());
            webSocketSession.sendMessage(new TextMessage("Error: Invalid credentials format. Expected format: host:username:password"));
            return;
        }

        String host = parts[0].trim();
        String username = parts[1].trim();
        String password = parts[2].trim();

        try {
            logger.info("Attempting SSH connection for session {}: host={}, username={}", webSocketSession.getId(), host, username);

            JSch jsch = new JSch();
            Session sshSession = jsch.getSession(username, host, 22);
            sshSession.setPassword(password);
            sshSession.setConfig("StrictHostKeyChecking", "no");
            sshSession.connect();

            logger.info("SSH session connected for session ID: {}", webSocketSession.getId());

            sshSessions.put(webSocketSession.getId(), sshSession);
            isAuthenticated.put(webSocketSession.getId(), true); // Mark as authenticated

            webSocketSession.sendMessage(new TextMessage("Info: SSH connection established."));
        } catch (JSchException e) {
            logger.error("SSH connection failed for session {}: {}", webSocketSession.getId(), e.getMessage(), e);
            webSocketSession.sendMessage(new TextMessage("Error: Failed to connect to SSH. Invalid credentials."));
        }
    }


    private void startShellSession(WebSocketSession webSocketSession) throws Exception {
        Session sshSession = sshSessions.get(webSocketSession.getId());
        if (sshSession == null || !sshSession.isConnected()) {
//            webSocketSession.sendMessage(new TextMessage("Error: SSH session not connected."));
            return;
        }

        ChannelShell channelShell = (ChannelShell) sshSession.openChannel("shell");
        InputStream inputStream = channelShell.getInputStream();
        OutputStream outputStream = channelShell.getOutputStream();

        // Start the shell
        channelShell.connect();

        // Start a thread to handle shell output
        new Thread(() -> {
            try {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    String output = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
                    webSocketSession.sendMessage(new TextMessage("Command output: " + output));
                }
            } catch (IOException e) {
                logger.error("Error reading shell output for session {}: {}", webSocketSession.getId(), e.getMessage());
            }
        }).start();

        // Store the shell session's output stream for command execution
        shellOutputStreams.put(webSocketSession.getId(), outputStream);
    }

    private void executeCommand(WebSocketSession webSocketSession, String command) throws Exception {
        OutputStream outputStream = shellOutputStreams.get(webSocketSession.getId());
        if (outputStream == null) {
            webSocketSession.sendMessage(new TextMessage("Error: Shell session not initialized."));
            return;
        }
        // Trim and normalize the command for comparison
        String normalizedCommand = command.trim().toLowerCase();

        // Handle commands like 'exit' or 'logout'
        if ("exit".equals(normalizedCommand) || "logout".equals(normalizedCommand)) {
            Session sshSession = sshSessions.get(webSocketSession.getId());
            if (sshSession != null && sshSession.isConnected()) {
                sshSession.disconnect();
            }
            shellOutputStreams.remove(webSocketSession.getId());
            sshSessions.remove(webSocketSession.getId());

            webSocketSession.sendMessage(new TextMessage("Info: Session closed successfully. "));

            webSocketSession.close();
            return;
        }


        // Write the command to the shell
        command += "\n"; // Ensure the command ends with a newline
        outputStream.write(command.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

//    private void executeCommand(WebSocketSession webSocketSession, String command) throws Exception {
//        if (command == null || command.trim().isEmpty()) {
//            webSocketSession.sendMessage(new TextMessage("Error: Command cannot be empty."));
//            return;
//        }
//
//        Session sshSession = sshSessions.get(webSocketSession.getId());
//        if (sshSession == null || !sshSession.isConnected()) {
//            webSocketSession.sendMessage(new TextMessage("Error: SSH session not connected."));
//            return;
//        }
//
//        ChannelExec channel = null;
//        try {
//            channel = (ChannelExec) sshSession.openChannel("exec");
//            System.out.println("-->>#### "+command);
//            channel.setCommand(command);
//
//            InputStream inputStream = channel.getInputStream();
//            InputStream errorStream = channel.getErrStream();
//
//            channel.connect();
//            System.out.println("channel"+channel.isConnected());
//
//            // Reading command output
//            StringBuilder output = new StringBuilder();
//
//            byte[] buffer = new byte[1024];
//            int bytesRead;
//
//            while ((bytesRead = inputStream.read(buffer)) != -1) {
//
//                output.append(new String(buffer, 0, bytesRead, StandardCharsets.UTF_8));
//                System.out.println("------>in check"+output);
//            }
//
//            // Reading error stream (if any)
//            StringBuilder errorOutput = new StringBuilder();
//            while ((bytesRead = errorStream.read(buffer)) != -1) {
//                errorOutput.append(new String(buffer, 0, bytesRead, StandardCharsets.UTF_8));
//            }
//
//            // Send output back to the client
//            if (output.length() > 0) {
//                String commandOutput = output.toString().trim();
////                System.out.println("----->>>"+output);
//                webSocketSession.sendMessage(new TextMessage("Command output: " + output.toString().trim()));
//
//            }
//
//            if (errorOutput.length() > 0) {
//                webSocketSession.sendMessage(new TextMessage("Command error: " + errorOutput.toString().replace("\n", "\\n")));
//            }
//
//        } catch (Exception e) {
//            logger.error("Command execution failed for session {}: {}", webSocketSession.getId(), e.getMessage(), e);
//            webSocketSession.sendMessage(new TextMessage("Error: Command execution failed: " + e.getMessage()));
//        } finally {
//            if (channel != null) {
//                channel.disconnect();
//            }
//        }
//    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Session sshSession = sshSessions.remove(session.getId());
        isAuthenticated.remove(session.getId());
        if (sshSession != null) {
            sshSession.disconnect();
        }
        logger.info("WebSocket and SSH session closed for: {}", session.getId());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        logger.info("WebSocket connection established: {}", session.getId());
        isAuthenticated.put(session.getId(), false); // Default to unauthenticated
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        logger.error("WebSocket transport error for session {}: {}", session.getId(), exception.getMessage(), exception);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}



