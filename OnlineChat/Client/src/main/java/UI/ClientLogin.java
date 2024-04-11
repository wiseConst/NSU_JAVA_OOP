package UI;

import Log.Log;
import Client.Client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientLogin extends JFrame {

    private JPanel contentPane;
    private JTextField txtName;
    private JTextField txtAddress;
    private JTextField txtPort;
    private JLabel lblIpAddress;
    private JLabel lblPort;

    private String name;
    private String ipAddress;
    private Integer port;

    public ClientLogin() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e1) {
            Log.GetLogger().warn("Failed to sync Swing with OS UI." + e1.getMessage());
        }
        setTitle("Login");
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(300, 360);
        setLocationRelativeTo(null);
        contentPane = new JPanel();
        contentPane.setBackground(new Color(0.4f, 0.4f, 0.5f));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(null); // absolute layout

        // User name label
        {
            JLabel lblName = new JLabel("Name:");
            lblName.setBounds(127, 34, 45, 16);
            contentPane.add(lblName);

            txtName = new JTextField();
            txtName.setBounds(67, 50, 165, 28);
            contentPane.add(txtName);
        }

        // IP label
        {
            lblIpAddress = new JLabel("IP Address:");
            lblIpAddress.setBounds(111, 96, 77, 16);
            lblIpAddress.setToolTipText("(eg. 192.168.0.2)");
            contentPane.add(lblIpAddress);

            txtAddress = new JTextField();
            txtAddress.setBounds(67, 116, 165, 28);
            contentPane.add(txtAddress);
        }

        // Port label
        {
            lblPort = new JLabel("Port:");
            lblPort.setBounds(133, 171, 34, 16);
            lblPort.setToolTipText("Valid ports are: [1024, 41951]. (eg. 8192)");
            contentPane.add(lblPort);

            txtPort = new JTextField();
            txtPort.setBounds(67, 191, 165, 28);
            contentPane.add(txtPort);
        }

        var btnLogin = new JButton("Login");
        btnLogin.setBounds(91, 251, 117, 30);
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                name = txtName.getText();
                ipAddress = txtAddress.getText();

                if (txtPort.getText().isEmpty() || name.isEmpty() || ipAddress.isEmpty()) return;

                port = Integer.parseInt(txtPort.getText());
                if (port < 1024 || port > 49151) {
                    Log.GetLogger().error("Port is not in valid range! [1024, 49151].");
                    return;
                }

                try {
                    login(name, ipAddress, port);
                } catch (IOException ex) {
                    Log.GetLogger().warn(ex.getMessage());
                }
            }
        });
        contentPane.add(btnLogin);

        setContentPane(contentPane);
        setVisible(true);
    }

    private void login(String name, String address, int port) throws IOException {
        dispose();
        new ClientWindow(new Client(name, address, port));
    }
}
