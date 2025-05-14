package POTS;

import javax.swing.*;
import java.awt.*;

public class PurchaseManagerMenu extends JFrame {
    private String userId;
    private String fullName;

    public PurchaseManagerMenu(String userId, String fullName) {
        this.userId = userId;
        this.fullName = fullName;
        setTitle("Purchase Manager Menu");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout());

        initializationComponents();
    }

    private void initializationComponents() {
        // Background Image
        JLabel background = new JLabel(new ImageIcon("backgroundimages/purchasemanagermenubackground.png")); // Update with your background image path
        background.setLayout(null); // Enable free placement of components
        getContentPane().add(background);

        // Box Panel
        JPanel boxPanel = new JPanel();
        boxPanel.setLayout(null);
        boxPanel.setBounds(200, 150, 400, 350); // Adjust size as needed
        boxPanel.setBackground(new Color(255, 255, 255, 200)); // Semi-transparent background
        
        JLabel welcomeLabel = new JLabel("Welcome, Purchase Manager " + fullName);
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
        viewItemsButton.setBounds(50, 84, 300, 30);
        viewItemsButton.addActionListener(e -> ViewItems());

        JButton requisitionsButton = new JButton("View Requisitions");
        requisitionsButton.setBounds(50, 124, 300, 30);
        requisitionsButton.addActionListener(e -> viewRequisitions());

        JButton managePurchaseOrdersButton = new JButton("Manage Purchase Orders (View/Add/Edit)");
        managePurchaseOrdersButton.setBounds(50, 164, 300, 30);
        managePurchaseOrdersButton.addActionListener(e -> PurchaseOrdersManager());

        JButton viewSuppliersButton = new JButton("View Suppliers");
        viewSuppliersButton.setBounds(50, 204, 300, 30);
        viewSuppliersButton.addActionListener(e -> viewSuppliers());

        // Add buttons to the box panel
        boxPanel.add(viewItemsButton);
        boxPanel.add(requisitionsButton);
        boxPanel.add(managePurchaseOrdersButton);
        boxPanel.add(viewSuppliersButton);

        // Add box panel to background
        background.add(boxPanel);
        
                
                JLabel titleLabel = new JLabel("Purchase Manager Menu");
                getContentPane().add(titleLabel, BorderLayout.NORTH);
                titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
                titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private void ViewItems() {
        new ItemsManager(false, false, userId).setVisible(true); // View only
    }

    private void viewRequisitions() {
        new RequisitionsManager(userId, "PM").setVisible(true);
    }

    private void PurchaseOrdersManager() {
        new PurchaseOrdersManager(userId, "PM").setVisible(true);
    }

    private void viewSuppliers() {
        new SuppliersManager("PM").setVisible(true);
    }
}
