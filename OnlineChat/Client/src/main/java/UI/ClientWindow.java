package UI;

import Connection.*;
import DTO.DTO;
import Log.Log;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.Socket;

public class ClientWindow extends JFrame {
    // Client
    private Connection clientConnection = null;
    private String username = null;
    private boolean bConnectionApproved = false;
    private Object clientConnectionEstablishedLock = null;
    private Thread clientListener = null;

    // UI
    private UserList userList = null;
    private JPanel contentPane;
    private JTextField txtMessage;
    private JTextPane history;
    private DefaultCaret caret;
    private JMenuBar menuBar;
    private JMenu mnFile;
    private JMenuItem mntmOnlineUsers;
    private JMenuItem mntmExit;


    public ClientWindow(String name, String address, int port) {
        setupWindow();
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
            shutdown();
            Log.GetLogger().warn(e.getMessage());
            return;
        }

        userList = new UserList();
    }


    private void setupWindow() {
        setTitle("NSUGram Client");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e1) {
            Log.GetLogger().warn("Failed to sync Swing with OS UI." + e1.getMessage());
        }
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(880, 550);
        setLocationRelativeTo(null);

        // Menu bar
        {
            menuBar = new JMenuBar();
            setJMenuBar(menuBar);

            mnFile = new JMenu("File");
            menuBar.add(mnFile);

            mntmOnlineUsers = new JMenuItem("Online Users");
            mntmOnlineUsers.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        clientConnection.send(DTO.getUserListRequest(username));
                    } catch (IOException ex) {
                        Log.GetLogger().warn(ex.getMessage());
                        shutdown();
                    }

                    userList.setVisible(true);
                }
            });
            mnFile.add(mntmOnlineUsers);

            mntmExit = new JMenuItem("Exit");
            mntmExit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        clientConnection.send(DTO.getLogoutRequest(username));
                    } catch (IOException ex) {
                        Log.GetLogger().warn(ex.getMessage());
                    }
                }
            });
            mnFile.add(mntmExit);
        }

        // Main panel.
        {
            contentPane = new JPanel();
            contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
            setContentPane(contentPane);

            GridBagLayout gbl_contentPane = new GridBagLayout();
            gbl_contentPane.columnWidths = new int[]{28, 815, 30, 7}; // SUM = 880
            gbl_contentPane.rowHeights = new int[]{25, 485, 40}; // SUM = 550
            contentPane.setLayout(gbl_contentPane);
        }

        // Main canvas with all messages.
        {
            history = new JTextPane();
            history.setEditable(false);
            JScrollPane scroll = new JScrollPane(history);
            caret = (DefaultCaret) history.getCaret();
            caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
            GridBagConstraints scrollConstraints = new GridBagConstraints();
            scrollConstraints.insets = new Insets(0, 0, 5, 5);
            scrollConstraints.fill = GridBagConstraints.BOTH;
            scrollConstraints.gridx = 0;
            scrollConstraints.gridy = 0;
            scrollConstraints.gridwidth = 3;
            scrollConstraints.gridheight = 2;
            scrollConstraints.weightx = 1;
            scrollConstraints.weighty = 1;
            scrollConstraints.insets = new Insets(0, 5, 0, 0);
            contentPane.add(scroll, scrollConstraints);
        }

        // Send message field.
        {
            txtMessage = new JTextField();
            txtMessage.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        try {
                            sendMessage(txtMessage.getText());
                            txtMessage.setText(null);
                        } catch (IOException ex) {
                            Log.GetLogger().error("Failed to send message!" + ex.getMessage());
                            throw new RuntimeException(ex);
                        }
                    }
                }
            });
            GridBagConstraints gbc_txtMessage = new GridBagConstraints();
            gbc_txtMessage.insets = new Insets(0, 0, 0, 5);
            gbc_txtMessage.fill = GridBagConstraints.HORIZONTAL;
            gbc_txtMessage.gridx = 0;
            gbc_txtMessage.gridy = 2;
            gbc_txtMessage.gridwidth = 2;
            gbc_txtMessage.weightx = 1;
            gbc_txtMessage.weighty = 0;
            contentPane.add(txtMessage, gbc_txtMessage);
        }

        // Send message button.
        {
            JButton btnSend = new JButton("Send");
            btnSend.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        sendMessage(txtMessage.getText());
                        txtMessage.setText(null);
                    } catch (IOException ex) {
                        Log.GetLogger().error("Failed to send message!" + ex.getMessage());
                        throw new RuntimeException(ex);
                    }
                }
            });
            GridBagConstraints gbc_btnSend = new GridBagConstraints();
            gbc_btnSend.insets = new Insets(0, 0, 0, 5);
            gbc_btnSend.gridx = 2;
            gbc_btnSend.gridy = 2;
            gbc_btnSend.weightx = 0;
            gbc_btnSend.weighty = 0;
            contentPane.add(btnSend, gbc_btnSend);
        }

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                try {
                    clientConnection.send(DTO.getLogoutRequest(username));
                } catch (IOException ex) {
                    Log.GetLogger().warn(ex.getMessage());
                    shutdown();
                }
            }
        });

        setVisible(true);
        txtMessage.requestFocusInWindow();
    }


    private void sendMessage(String message) throws IOException {
        if (message.isEmpty()) return;

        Style style = history.addStyle("ColoredText", null);
        StyleConstants.setForeground(style, new Color(username.hashCode() % 16777216));

        console(username + ": " + message, style);
        clientConnection.send(DTO.getNewMessageRequest(username, message));
    }

    public void shutdown() {
        dispose();

        if (clientConnection != null) {
            try {
                clientConnection.close();
            } catch (IOException e) {
                Log.GetLogger().warn(e.getMessage());
            }
        }

        synchronized (clientConnectionEstablishedLock) {
            clientConnectionEstablishedLock.notify();
        }

        if (userList != null) userList.dispose();
        if (clientListener != null) clientListener.interrupt();
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
                            var message = dto.getMessage();

                            Style style = history.addStyle("ColoredText", null);
                            StyleConstants.setForeground(style, new Color(dto.getUsername().hashCode() % 16777216));

                            console(dto.getUsername() + ": " + message, style);
                        } else if (dto.isLogoutResponse()) {
                            var bLogoutSuccess = dto.isSuccessResult();
                            if (!bLogoutSuccess) Log.GetLogger().error("Logout fail: " + dto.getMessage());
                            else Log.GetLogger().info("Successfully disconnected!");

                            shutdown();
                            return;
                        } else if (dto.isUserListResponse()) {
                            userList.update(dto.getMessage().split("\n"));
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

    private void console(String message, Style style) {
        StyledDocument doc = history.getStyledDocument();
        try {
            doc.insertString(doc.getLength(), message + "\n", style);
        } catch (BadLocationException e) {
            Log.GetLogger().warn(e.getMessage());
        }
    }

}
