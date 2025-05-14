package POTS;

import javax.swing.*;
import java.awt.*;

public class InventoryManagerMenu extends JFrame {
    private String userId;
    private String fullName;

    public InventoryManagerMenu(String userId, String fullName) {
        this.userId = userId;
        this.fullName = fullName;
        setTitle("Inventory Manager Menu");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout());

        initializationComponents();
    }

    private void initializationComponents() {
        // Background Image
        JLabel background = new JLabel(new ImageIcon("backgroundimages/inventorymanagermenubackground.png")); // Update with your background image path
        background.setLayout(null); // Enable free placement of components
        getContentPane().add(background);

        // Box Panel
        JPanel boxPanel = new JPanel();
        boxPanel.setLayout(null);
        boxPanel.setBounds(200, 150, 400, 200); // Adjust size as needed
        boxPanel.setBackground(new Color(255, 255, 255, 200)); // Semi-transparent background
        
        JLabel welcomeLabel = new JLabel("Welcome, Inventory Manager " + fullName);
        welcomeLabel.setBounds(50, 10, 300, 25);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        boxPanel.add(welcomeLabel);
        
        JLabel userIdLabel = new JLabel("User ID: " + userId);
        userIdLabel.setBounds(50, 40, 300, 25);
        userIdLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userIdLabel.setHorizontalAlignment(SwingConstants.CENTER);
        boxPanel.add(userIdLabel);
        
        JButton ViewItemsButton = new JButton("View Items");
        ViewItemsButton.setBounds(50, 80, 300, 30); // Adjusted y-coordinate for proper spacing
        ViewItemsButton.addActionListener(e -> ViewItems());
        
        JButton AddEditDeleteItemsButton = new JButton("Add/Edit/Delete Items");
        AddEditDeleteItemsButton.setBounds(50, 120, 300, 30); // Adjusted y-coordinate for proper spacing
        AddEditDeleteItemsButton.addActionListener(e -> AddEditDeleteItems());

        JButton SuppliersManagerButton = new JButton("Manage Suppliers (View/Add/Edit/Delete)");
        SuppliersManagerButton.setBounds(50, 160, 300, 30); // Adjusted y-coordinate for proper spacing
        SuppliersManagerButton.addActionListener(e -> SuppliersManager());

        boxPanel.add(ViewItemsButton);
        boxPanel.add(AddEditDeleteItemsButton);
        boxPanel.add(SuppliersManagerButton);

        background.add(boxPanel);
        
        JLabel titleLabel = new JLabel("Inventory Manager Menu");
        getContentPane().add(titleLabel, BorderLayout.NORTH);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private void ViewItems() {
        new ItemsManager(false, false, userId).setVisible(true);
    }
    
    private void AddEditDeleteItems() {
        new ItemsManager(true, true, userId).setVisible(true);
    }

    private void SuppliersManager() {
        new SuppliersManager("IM").setVisible(true);
    }
}
