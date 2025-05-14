// Updated Login.java
package POTS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Login extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public Login() {
        setTitle("Login");
        setSize(1000, 600); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout()); 
        
        initializationComponents();
    }

    private void initializationComponents() {
        // Background Image
        JLabel background = new JLabel(new ImageIcon("backgroundimages/loginbackground.jpg")); 
        background.setLayout(new GridBagLayout()); 
        add(background, BorderLayout.CENTER); 

        // Title Label at the top (Center aligned)
        JLabel titleLabel = new JLabel("Welcome to Procurement Order Tracking System (POTS)");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24)); 
        titleLabel.setForeground(Color.BLACK); 
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER); 
        titleLabel.setPreferredSize(new Dimension(1000, 80)); 
        add(titleLabel, BorderLayout.NORTH); 

        // Box Panel for the login form
        JPanel boxPanel = new JPanel();
        boxPanel.setLayout(null); 
        boxPanel.setPreferredSize(new Dimension(350, 250)); 
        boxPanel.setBackground(new Color(255, 255, 255, 200)); 

        // Username Label and Field
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(30, 30, 80, 25);
        boxPanel.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(120, 30, 150, 25);
        boxPanel.add(usernameField);

        // Password Label and Field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(30, 70, 80, 25);
        boxPanel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(120, 70, 150, 25);
        boxPanel.add(passwordField);

        // Login Button
        JButton loginButton = new JButton("Login");
        loginButton.setBounds(100, 120, 100, 30);
        loginButton.addActionListener(e -> authenticateUser());
        boxPanel.add(loginButton);

        // Add boxPanel to background
        background.add(boxPanel, new GridBagConstraints());
    }

    // User authentication
    private void authenticateUser() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userDetails = line.split(";");
                String userId = userDetails[0];
                String storedUsername = userDetails[1];
                String fullName = userDetails[2];
                String storedPassword = userDetails[3];
                String role = userDetails[4];

                if (storedUsername.equals(username) && storedPassword.equals(password)) {
                    openMenu(role, userId, fullName);
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "Invalid username or password.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to open the corresponding menu based on user role
    private void openMenu(String role, String userId, String fullName) {
        switch (role) {
            case "SM":
                new SalesManagerMenu(userId, fullName).setVisible(true);
                break;
            case "PM":
                new PurchaseManagerMenu(userId, fullName).setVisible(true);
                break;
            case "IM":
                new InventoryManagerMenu(userId, fullName).setVisible(true);
                break;
            case "FM":
                new FinancialManagerMenu(userId, fullName).setVisible(true);
                break;
            case "Admin":
                new AdministratorMenu(userId, fullName).setVisible(true);
                break;
            default:
                JOptionPane.showMessageDialog(this, "Unsupported role: " + role);
        }
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}