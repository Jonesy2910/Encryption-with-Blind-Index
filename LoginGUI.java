package org.apache.maven.archetypes;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class LoginGUI extends JFrame{

    private JPanel loginPanel;
    private JButton loginButton;
    private JTextField usernameTextField;
    private JPasswordField passwordTextField;
    public static String username;

    public LoginGUI() {
        loginButton.addActionListener(e -> {

            try (Connection conn = DBConnection.getConnection()) {
                ResultSet rs = null;
                PreparedStatement prepareStatement = null;
                try {
                    prepareStatement = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
                    prepareStatement.setString(1, usernameTextField.getText());
                    prepareStatement.setString(2, new String(passwordTextField.getPassword()));
                    rs = prepareStatement.executeQuery();
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(null, "Login Successful");
                        openMainMenu();
                    } else {
                        JOptionPane.showMessageDialog(null, "Login Unsuccessful");
                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                } finally {
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                        if (prepareStatement != null) {
                            prepareStatement.close();
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            } catch (SQLException | ClassNotFoundException err) {
                System.out.println(err.getMessage());
            }
        });
    }

    public void createLoginGUI() {
        // create your loginPanel here
        setTitle("Login System");
        setContentPane(loginPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        pack();
        setVisible(true);
    }


    private void openMainMenu() {
        username = usernameTextField.getText();
        String logMessage = "User '" + username + "' logged in successfully";
        MainSystemGUI.writeLogsToFile(logMessage);
        MainSystemGUI mainSystemGUI = new MainSystemGUI();
        mainSystemGUI.createMainGUI();
    }
}