package POTS;

import javax.swing.*;
import java.awt.*;

public class AdministratorMenu extends JFrame {
    private String userId;
    private String fullName;

    public AdministratorMenu(String userId, String fullName) {
        this.userId = userId;
        this.fullName = fullName;
        setTitle("Administrator Menu");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout());

        initializationComponents();
    }

    private void initializationComponents() {
        // Background Image
        JLabel background = new JLabel(new ImageIcon("backgroundimages/administratormenubackground.png"));
        background.setLayout(null);
        getContentPane().add(background);

        // Box Panel
        JPanel boxPanel = new JPanel();
        boxPanel.setLayout(null);
        boxPanel.setBounds(200, 150, 400, 400);
        boxPanel.setBackground(new Color(255, 255, 255, 200));

        // Welcome Message
        JLabel welcomeLabel = new JLabel("Welcome, Administrator " + fullName);
        welcomeLabel.setBounds(50, 10, 300, 25);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        boxPanel.add(welcomeLabel);

        JLabel userIdLabel = new JLabel("User ID: " + userId);
        userIdLabel.setBounds(50, 40, 300, 25);
        userIdLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userIdLabel.setHorizontalAlignment(SwingConstants.CENTER);
        boxPanel.add(userIdLabel);

        JButton UsersManagerButton = new JButton("Users Manager (View/Add/Edit/Delete)");
        UsersManagerButton.setBounds(50, 69, 300, 30);
        UsersManagerButton.addActionListener(e -> UsersManager());

        JButton SuppliersManagerButton = new JButton("Suppliers Manager (View/Add/Edit/Delete)");
        SuppliersManagerButton.setBounds(50, 109, 300, 30);
        SuppliersManagerButton.addActionListener(e -> SuppliersManager());

        JButton ItemsManagerButton = new JButton("Items Manager (View/Add/Edit/Delete)");
        ItemsManagerButton.setBounds(50, 149, 300, 30);
        ItemsManagerButton.addActionListener(e -> ItemsManager());

        JButton DailySalesManagerButton = new JButton("Daily Sales Manager (View/Add/Edit/Delete)");
        DailySalesManagerButton.setBounds(50, 189, 300, 30);
        DailySalesManagerButton.addActionListener(e -> DailySalesManager());

        JButton RequisitionsManagerButton = new JButton("Requisitions Manager (View/Add/Edit/Cancel)");
        RequisitionsManagerButton.setBounds(50, 229, 300, 30);
        RequisitionsManagerButton.addActionListener(e -> RequisitionsManager());

        JButton PurchaseOrdersManagerButton = new JButton("Purchase Orders Manager (View/Add/Edit)");
        PurchaseOrdersManagerButton.setBounds(50, 269, 300, 30);
        PurchaseOrdersManagerButton.addActionListener(e -> PurchaseOrdersManager());

        JButton VerifyPurchaseOrdersButton = new JButton("Verify Purchase Orders");
        VerifyPurchaseOrdersButton.setBounds(50, 309, 300, 30);
        VerifyPurchaseOrdersButton.addActionListener(e -> VerifyPurchaseOrders());

        JButton PaymentsManagerButton = new JButton("Payments Manager");
        PaymentsManagerButton.setBounds(50, 346, 300, 30);
        PaymentsManagerButton.addActionListener(e -> PaymentsManager());

        boxPanel.add(UsersManagerButton);
        boxPanel.add(SuppliersManagerButton);
        boxPanel.add(ItemsManagerButton);
        boxPanel.add(DailySalesManagerButton);
        boxPanel.add(RequisitionsManagerButton);
        boxPanel.add(PurchaseOrdersManagerButton);
        boxPanel.add(VerifyPurchaseOrdersButton);
        boxPanel.add(PaymentsManagerButton);

        background.add(boxPanel);

        JLabel titleLabel = new JLabel("Administrator Menu");
        getContentPane().add(titleLabel, BorderLayout.NORTH);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private void UsersManager() {
        new UsersManager().setVisible(true);
    }

    private void SuppliersManager() {
        new SuppliersManager("Admin").setVisible(true);
    }

    private void ItemsManager() {
        new ItemsManager(true, true, userId).setVisible(true);
    }

    private void DailySalesManager() {
        new DailySalesManager(userId).setVisible(true);
    }

    private void RequisitionsManager() {
        new RequisitionsManager(userId, "Admin").setVisible(true);
    }

    private void PurchaseOrdersManager() {
        new PurchaseOrdersManager(userId, "Admin").setVisible(true);
    }

    private void VerifyPurchaseOrders() {
        new VerifyPurchaseOrders(userId).setVisible(true);
    }

    private void PaymentsManager() {
        new PaymentsManager(userId, "Admin").setVisible(true);
    }
}
