package POTS;

import javax.swing.*;
import java.awt.*;

public class FinancialManagerMenu extends JFrame {
    private String userId;
    private String fullName;

    public FinancialManagerMenu(String userId, String fullName) {
        this.userId = userId;
        this.fullName = fullName;
        setTitle("Financial Manager Menu");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout());

        initializationComponents();
    }

    private void initializationComponents() {
        // Background Image
        JLabel background = new JLabel(new ImageIcon("backgroundimages/financialmanagermenubackground.png"));
        background.setLayout(null);
        getContentPane().add(background);

        // Box Panel
        JPanel boxPanel = new JPanel();
        boxPanel.setLayout(null);
        boxPanel.setBounds(200, 150, 400, 350); // Adjust size as needed
        boxPanel.setBackground(new Color(255, 255, 255, 200)); // Semi-transparent background
        
        JLabel welcomeLabel = new JLabel("Welcome, Financial Manager " + fullName);
        welcomeLabel.setBounds(50, 11, 300, 25);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        boxPanel.add(welcomeLabel);

        JLabel userIdLabel = new JLabel("User ID: " + userId);
        userIdLabel.setBounds(50, 40, 300, 25);
        userIdLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userIdLabel.setHorizontalAlignment(SwingConstants.CENTER);
        boxPanel.add(userIdLabel);
        
        JButton viewPurchaseOrdersButton = new JButton("View Purchase Orders");
        viewPurchaseOrdersButton.setBounds(50, 95, 300, 30);
        viewPurchaseOrdersButton.addActionListener(e -> viewPurchaseOrders());

        JButton verifyPurchaseOrdersButton = new JButton("Verify Purchase Orders");
        verifyPurchaseOrdersButton.setBounds(50, 136, 300, 30);
        verifyPurchaseOrdersButton.addActionListener(e -> verifyPurchaseOrders());

        JButton viewMakePaymentsButton = new JButton("View/Make Payments");
        viewMakePaymentsButton.setBounds(50, 176, 300, 30);
        viewMakePaymentsButton.addActionListener(e -> viewMakePayments());

        JButton viewItemsButton = new JButton("View Items");
        viewItemsButton.setBounds(50, 216, 300, 30);
        viewItemsButton.addActionListener(e -> viewItems());

        // Add buttons to the box panel
        boxPanel.add(viewPurchaseOrdersButton);
        boxPanel.add(verifyPurchaseOrdersButton);
        boxPanel.add(viewMakePaymentsButton);
        boxPanel.add(viewItemsButton);

        // Add box panel to background
        background.add(boxPanel);
        
        JLabel titleLabel = new JLabel("Financial Manager Menu");
        getContentPane().add(titleLabel, BorderLayout.NORTH);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private void viewPurchaseOrders() {
        new PurchaseOrdersManager(userId, "FM").setVisible(true);
    }

    private void verifyPurchaseOrders() {
        new VerifyPurchaseOrders(userId).setVisible(true);
    }

    private void viewMakePayments() {
        new PaymentsManager(userId, "FM").setVisible(true);
    }

    private void viewItems() {
        new ItemsManager(false, false, userId).setVisible(true); // View only
    }
}
