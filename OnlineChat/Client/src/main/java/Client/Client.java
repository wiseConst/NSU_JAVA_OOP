package Client;

import Connection.*;
import DTO.*;
import Log.Log;
import Callback.*;

import java.io.IOException;
import java.net.Socket;

public class Client {
    private Connection clientConnection = null;
    private String username = null;
    private boolean bConnectionApproved = false;
    private Object clientConnectionEstablishedLock = null;
    private Thread clientListener = null;
    private Callback messageResponseCallback = null;
    private Callback userListResponseCallback = null;
    private Callback logoutResponseCallback = null;

    public Client(String name, String address, int port) {
        this.username = name;

        clientConnection = ConnectionFactory.create();
        clientConnectionEstablishedLock = new Object();
        listenForMessage();

        try {
            clientConnection.connect(new Socket(address, port));
            clientConnection.send(DTO.getLoginRequest(username));

            synchronized (clientConnectionEstablishedLock) {
                clientConnectionEstablishedLock.notify();
            }
        } catch (IOException e) {
            Log.GetLogger().warn(e.getMessage());
            shutdown();
        }
    }

    public final String getUsername() {
        return username;
    }

    public void send(DTO dto) throws IOException {
        if (dto == null) {
            Log.GetLogger().warn("DTO is null! Can't send it.");
            return;
        }

        clientConnection.send(dto);
    }

    public void setMessageResponseCallback(Callback messageResponseCallback) {
        this.messageResponseCallback = messageResponseCallback;
    }

    public void setUserListResponseCallback(Callback userListResponseCallback) {
        this.userListResponseCallback = userListResponseCallback;
    }

    public void setLogoutResponseCallback(Callback logoutResponseCallback) {
        this.logoutResponseCallback = logoutResponseCallback;
    }

    public void shutdown() {
        if (clientConnection != null) {
            try {
                clientConnection.close();
                clientConnection = null;
            } catch (IOException e) {
                Log.GetLogger().warn(e.getMessage());
            }
        }

        synchronized (clientConnectionEstablishedLock) {
            clientConnectionEstablishedLock.notify();
        }

        if (clientListener != null) {
            clientListener.interrupt();
            clientListener = null;
        }
        messageResponseCallback = null;
        userListResponseCallback = null;
        logoutResponseCallback = null;
    }

    private void listenForMessage() {
        clientListener = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        // Wait a bit until connection establishes.
                        if (clientConnection.getSocket() == null) {
                            synchronized (clientConnectionEstablishedLock) {
                                clientConnectionEstablishedLock.wait();
                            }
                        }

                        var dto = clientConnection.receive();
                        if (dto.isLoginResponse()) {
                            bConnectionApproved = dto.isSuccessResult();

                            if (!bConnectionApproved) {
                                Log.GetLogger().error(dto.getMessage());
                                shutdown();
                                return;
                            } else {
                                Log.GetLogger().info("Connection approved!");
                            }
                        } else if (dto.isMessageResponse()) {
                            if (messageResponseCallback != null)
                                messageResponseCallback.call(dto);
                        } else if (dto.isLogoutResponse()) {
                            if (logoutResponseCallback != null)
                                logoutResponseCallback.call(dto);

                            shutdown();
                            return;
                        } else if (dto.isUserListResponse()) {
                            if (userListResponseCallback != null) userListResponseCallback.call(dto);
                        }
                    } catch (ClassNotFoundException | InterruptedException | IOException e) {
                        Log.GetLogger().warn("Connection dropped!");
                        shutdown();
                        return;
                    }

                }
            }
        });
        clientListener.start();
    }
}
