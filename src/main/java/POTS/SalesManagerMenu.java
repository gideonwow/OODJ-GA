package POTS;

import javax.swing.*;
import java.awt.*;

public class SalesManagerMenu extends JFrame {
    private String userId;
    private String fullName;

    public SalesManagerMenu(String userId, String fullName) {
        this.userId = userId;
        this.fullName = fullName;
        setTitle("Sales Manager Menu");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout());

        initializationComponents();
    }

    private void initializationComponents() {
        // Background Image
        JLabel background = new JLabel(new ImageIcon("backgroundimages/salesmanagermenubackground.png")); // Update with your background image path
        background.setLayout(null); // Enable free placement of components
        getContentPane().add(background);

        // Box Panel
        JPanel boxPanel = new JPanel();
        boxPanel.setLayout(null);
        boxPanel.setBounds(200, 150, 400, 300); // Center box panel within the window
        boxPanel.setBackground(new Color(255, 255, 255, 200)); // Semi-transparent background
        

        // Welcome Message
        JLabel welcomeLabel = new JLabel("Welcome, Sales Manager " + fullName);
        welcomeLabel.setBounds(50, 10, 300, 25);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        boxPanel.add(welcomeLabel);

        JLabel userIdLabel = new JLabel("User ID: " + userId);
        userIdLabel.setBounds(50, 40, 300, 25);
        userIdLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userIdLabel.setHorizontalAlignment(SwingConstants.CENTER);
        boxPanel.add(userIdLabel);
        
        JButton viewItemsButton = new JButton("View Items");
        viewItemsButton.setBounds(50, 72, 300, 30);
        viewItemsButton.addActionListener(e -> ViewItems());

        JButton DailySalesManagerButton = new JButton("Manage Daily Sales (View/Add/Edit/Delete)");
        DailySalesManagerButton.setBounds(50, 112, 300, 30);
        DailySalesManagerButton.addActionListener(e -> DailySalesManager());

        JButton RequisitionsManagerButton = new JButton("Manage Requisitions (View/Add/Edit/Cancel)");
        RequisitionsManagerButton.setBounds(50, 152, 300, 30);
        RequisitionsManagerButton.addActionListener(e -> RequisitionsManager());

        JButton viewPurchaseOrdersButton = new JButton("View Purchase Orders");
        viewPurchaseOrdersButton.setBounds(50, 192, 300, 30);
        viewPurchaseOrdersButton.addActionListener(e -> viewPurchaseOrders());

        // Add buttons to the box panel
        boxPanel.add(viewItemsButton);
        boxPanel.add(DailySalesManagerButton);
        boxPanel.add(RequisitionsManagerButton);
        boxPanel.add(viewPurchaseOrdersButton);

        // Add box panel to background
        background.add(boxPanel);
        
        JLabel titleLabel = new JLabel("Sales Manager Menu");
        getContentPane().add(titleLabel, BorderLayout.NORTH);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private void ViewItems() {
        new ItemsManager(false, false, userId).setVisible(true); // Read-only access
    }

    private void DailySalesManager() {
        new DailySalesManager(userId).setVisible(true);
    }

    private void RequisitionsManager() {
        new RequisitionsManager(userId, "SM").setVisible(true);
    }

    private void viewPurchaseOrders() {
        new PurchaseOrdersManager(userId, "SM").setVisible(true);
    }
}
