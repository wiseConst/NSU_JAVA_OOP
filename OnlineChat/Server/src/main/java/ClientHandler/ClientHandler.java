package ClientHandler;

import Connection.*;
import DTO.DTO;
import Log.*;
import MessageHistory.MessageHistory;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientHandler implements Runnable {
    private static ConcurrentHashMap<String, ClientHandler> clientHandlers = new ConcurrentHashMap<>();
    private static MessageHistory messageHistory = new MessageHistory(3);
    private Connection clientConnection = null;
    private boolean bRunning = false;
    private long clientLastActivityTime = 0;
    private Integer clientTimeout = 0;

    public ClientHandler(Socket socket, Integer clientTimeout) {
        this.clientTimeout = clientTimeout;
        clientConnection = ConnectionFactory.create();
        try {
            clientConnection.connect(socket);
            bRunning = true;
        } catch (IOException e) {
            Log.logWarn(e.getMessage());
        }
    }

    @Override
    public void run() {
        while (bRunning) {
            try {
                var dto = clientConnection.receive();
                clientLastActivityTime = System.currentTimeMillis();

                if (dto.isLoginRequest()) {
                    handleLoginRequest(dto);
                } else if (dto.isMessageRequest()) {
                    handleMessageRequest(dto);
                } else if (dto.isLogoutRequest()) {
                    handleLogoutRequest(dto);
                } else if (dto.isUserListRequest()) {
                    handleUserListRequest(dto);
                }

            } catch (SocketTimeoutException e) {
                handleSocketTimeout(e);

            } catch (IOException | ClassNotFoundException e) {
                Log.logWarn(e.getMessage());

                String clientName = null;
                for (var entry : clientHandlers.entrySet()) {
                    if (this.equals(entry.getValue())) {
                        clientName = entry.getKey();
                        break;
                    }
                }

                closeEverything(clientName);
                break;
            }
        }
    }

    private void handleSocketTimeout(SocketTimeoutException e) {
        var diff = System.currentTimeMillis() - clientLastActivityTime;
        if (diff <= clientTimeout) return;

        try {
            String username = null;
            Connection userConnection = null;
            for (var entry : clientHandlers.entrySet()) {
                if (this.equals(entry.getValue())) {
                    username = entry.getKey();
                    userConnection = clientHandlers.get(username).clientConnection;
                    break;
                }
            }

            if (username == null || userConnection == null) {
                Log.logInfo("SERVER LOCAL: " + username + " is left out.");
                return;
            }

            userConnection.send(DTO.getLogoutResponse(DTO.Result.SUCCESS_RESULT, "SERVER", "success"));
            closeEverything(username);

            messageHistory.update("SERVER", username + " has disconnected! (timeout expired)");
            var userList = new StringBuilder();
            for (var key : clientHandlers.keySet()) {
                userList.append(key).append("\n");
            }
            broadcastUserList(null, "SERVER", userList.toString()); // Update to others
        } catch (IOException ex) {
            Log.logWarn(e.getMessage());
        }
    }

    private void handleUserListRequest(DTO dto) throws IOException {
        var userList = new StringBuilder();
        for (var key : clientHandlers.keySet()) {
            userList.append(key).append("\n");
        }

        clientConnection.send(DTO.getUserListResponse("SERVER", userList.toString())); // Update to one who requested
        broadcastUserList(this, "SERVER", userList.toString()); // Update to others
    }

    private void handleLoginRequest(DTO dto) throws IOException {
        var username = dto.getUsername();

        AtomicBoolean bUniqueName = new AtomicBoolean(true);
        clientHandlers.forEach((clientName, clientHandler) -> {
            if (clientName.equals(username)) {
                bUniqueName.set(false);
            }
        });

        if (!bUniqueName.get()) {
            clientConnection.send(DTO.getLoginResponse(DTO.Result.ERROR_RESULT, username, "Nickname is already occupied!"));
            clientConnection.close();
            bRunning = false;

            Log.logInfo("SERVER LOCAL: " + username + " already exists!");
            return;
        } else {
            clientConnection.send(DTO.getLoginResponse(DTO.Result.SUCCESS_RESULT, username, ""));

            clientConnection.getSocket().setSoTimeout(clientTimeout);
            clientLastActivityTime = System.currentTimeMillis();

            for (var entry : messageHistory.getHistory()) {
                clientConnection.send(DTO.getNewMessageResponse(entry.getUsername(), entry.getMessage()));
            }
        }

        Log.logInfo("SERVER LOCAL: " + username + " has connected!");
        clientHandlers.put(username, this);
        broadcastMessage(null/*this*/, "SERVER", username + " has entered the chat!");
        messageHistory.update("SERVER", username + " has entered the chat!");

        var userList = new StringBuilder();
        for (var key : clientHandlers.keySet()) {
            userList.append(key).append("\n");
        }
        broadcastUserList(null, "SERVER", userList.toString()); // Update to others
    }

    private void handleMessageRequest(DTO dto) {
        var message = dto.getMessage();
        System.out.println("SERVER LOCAL: user(" + dto.getUsername() + ") says: " + message);
        broadcastMessage(this, dto.getUsername(), message);
        messageHistory.update(dto.getUsername(), message);
    }

    private void handleLogoutRequest(DTO dto) throws IOException {
        clientConnection.send(DTO.getLogoutResponse(DTO.Result.SUCCESS_RESULT, "SERVER", "success"));
        closeEverything(dto.getUsername());
        broadcastMessage(this, "SERVER", dto.getUsername() + " disconnected!");

        messageHistory.update("SERVER", dto.getUsername() + " disconnected!");

        var userList = new StringBuilder();
        for (var key : clientHandlers.keySet()) {
            userList.append(key).append("\n");
        }
        System.out.println(userList);
        broadcastUserList(null, "SERVER", userList.toString()); // Update to others
    }

    public static int getCount() {
        return clientHandlers.size();
    }

    private void broadcastMessage(ClientHandler sourceClientHandler, String sourceUsername, String message) {
        clientHandlers.forEach((clientName, clientHandler) -> {
            if (clientHandler == sourceClientHandler) return; // No need to broadcast to yourself.

            try {
                clientHandler.clientConnection.send(DTO.getNewMessageResponse(sourceUsername, message));
            } catch (IOException e) {
                closeEverything(clientName);
            }
        });
    }

    private void broadcastUserList(ClientHandler sourceClientHandler, String sourceUsername, String message) {
        clientHandlers.forEach((clientName, clientHandler) -> {
            if (clientHandler == sourceClientHandler) return; // No need to broadcast to yourself.

            try {
                clientHandler.clientConnection.send(DTO.getUserListResponse(sourceUsername, message));
            } catch (IOException e) {
                closeEverything(clientName);
            }
        });
    }

    public void closeEverything(String clientName) {
        try {
            if (clientName == null || clientName.isEmpty() || !clientHandlers.containsKey(clientName)) return;

            ClientHandler clientHandler = clientHandlers.get(clientName);
            if (clientHandler.clientConnection != null) clientHandler.clientConnection.close();
            clientHandler.bRunning = false;

            broadcastMessage(clientHandlers.get(clientName), "SERVER", clientName + " has left the chat!");
            clientHandlers.remove(clientName);
        } catch (IOException e) {
            Log.logWarn(e.getMessage());
        }
    }

}
