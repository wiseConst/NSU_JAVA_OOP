package UI;


import Callback.Callback;
import Log.Log;
import Client.Client;
import DTO.DTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class ClientWindow extends JFrame {
    private Client client = null;
    private UserList userList = null;
    private JPanel contentPane;
    private JTextField txtMessage;
    private JTextPane history;
    private DefaultCaret caret;
    private JMenuBar menuBar;
    private JMenu mnFile;
    private JMenuItem mntmOnlineUsers;
    private JMenuItem mntmExit;

    private class MessageResponseCallback implements Callback {
        @Override
        public void call(DTO dto) {
            var message = dto.getMessage();

            Style style = history.addStyle("ColoredText", null);
            StyleConstants.setForeground(style, new Color(dto.getUsername().hashCode() % 16777216));

            console(dto.getUsername() + ": " + message, style);
        }
    }

    private class UserListResponseCallback implements Callback {
        @Override
        public void call(DTO dto) {
            userList.update(dto.getMessage().split("\n"));
        }
    }

    private class LogoutResponseCallback implements Callback {
        @Override
        public void call(DTO dto) {
            var bLogoutSuccess = dto.isSuccessResult();
            if (!bLogoutSuccess) Log.GetLogger().error("Logout fail: " + dto.getMessage());
            else Log.GetLogger().info("Successfully disconnected!");

            shutdown();
        }
    }

    public ClientWindow(Client client) {
        setupWindow();
        this.client = client;
        this.client.setMessageResponseCallback(new MessageResponseCallback());
        this.client.setUserListResponseCallback(new UserListResponseCallback());
        this.client.setLogoutResponseCallback(new LogoutResponseCallback());

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
                        client.send(DTO.getUserListRequest(client.getUsername()));
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
                        client.send(DTO.getLogoutRequest(client.getUsername()));
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
                    client.send(DTO.getLogoutRequest(client.getUsername()));
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
        StyleConstants.setForeground(style, new Color(client.getUsername().hashCode() % 16777216));

        console(client.getUsername() + ": " + message, style);
        client.send(DTO.getNewMessageRequest(client.getUsername(), message));
    }

    public void shutdown() {
        if (userList != null) userList.dispose();

        dispose();
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
