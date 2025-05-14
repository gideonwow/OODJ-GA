package POTS;

import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class PaymentsManager extends JFrame {
    private String userId;
    private String role;
    private JTable table;
    private List<String[]> purchaseOrders;
    private static final String SUPPLIERS_FILE = "suppliers.txt";
    private static final String ITEMS_FILE = "items.txt";
    private static final String PURCHASE_ORDERS_FILE = "purchaseorders.txt";
    private static final String PAYMENTS_FILE = "payments.txt";
    
    
    public PaymentsManager(String userId, String role) {
        this.userId = userId;
        this.role = role;
        setTitle("Payments Manager");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initializationComponents();
    }

    private void initializationComponents() {
        JPanel panel = new JPanel(new BorderLayout());

        // Load purchase orders
        loadPurchaseOrders();

        // Table for displaying purchase orders
        String[] columnNames = {
                "Purchase Order ID", "Date (YYYY-MM-DD)", "Requisition ID", "Item ID", "Quantity", 
                "Supplier ID", "User ID", "Status", "Shipping Method", 
                "Shipping Cost", "Unit Price", "Total Price"
        };
        String[][] data = purchaseOrders.stream()
                .map(po -> new String[]{
                        po[0], po[1], po[2], po[3], po[4], 
                        po[5], po[6], po[7], po[8], po[9], 
                        po[10], po[11]
                })
                .toArray(String[][]::new);
        table = new JTable(data, columnNames);

        JButton payButton = new JButton("Pay");
        JButton viewPaymentsButton = new JButton("View Payments");

        payButton.addActionListener(e -> makePayment());
        viewPaymentsButton.addActionListener(e -> viewPayments());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(payButton);
        buttonPanel.add(viewPaymentsButton);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);
    }

    private void loadPurchaseOrders() {
        purchaseOrders = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(PURCHASE_ORDERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                purchaseOrders.add(line.split(";"));
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading purchase orders.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void makePayment() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a purchase order to pay.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String poId = (String) table.getValueAt(selectedRow, 0);
        String status = (String) table.getValueAt(selectedRow, 7);

        // Restrict payment to "Approved" status
        if (!"Approved".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this, "Only purchase orders with 'Approved' status can be paid.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String paymentId = JOptionPane.showInputDialog(this, "Enter Payment ID:");
        if (paymentId == null || paymentId.trim().isEmpty()) {
            return;
        }

        try {
            // Check if payment ID already exists
            if (isDuplicatePaymentId(paymentId)) {
                JOptionPane.showMessageDialog(this, "Payment ID already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Make payment
            String[] purchaseOrder = purchaseOrders.get(selectedRow);
            String supplierId = purchaseOrder[5];
            String totalAmount = purchaseOrder[11];

            // Add payment record
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(PAYMENTS_FILE, true))) {
                bw.write(String.join(";", paymentId, poId, supplierId, totalAmount, "Paid", userId));
                bw.newLine();
            }

            // Update purchase order status
            purchaseOrder[7] = "Paid";
            updatePurchaseOrdersFile();

            // Generate receipt
            generateReceipt(paymentId, purchaseOrder);

            JOptionPane.showMessageDialog(this, "Payment successful for " + poId, "Success", JOptionPane.INFORMATION_MESSAGE);
            reloadTable();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error processing payment.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isDuplicatePaymentId(String paymentId) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(PAYMENTS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.split(";")[0].equals(paymentId)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void updatePurchaseOrdersFile() throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PURCHASE_ORDERS_FILE))) {
            for (String[] po : purchaseOrders) {
                bw.write(String.join(";", po));
                bw.newLine();
            }
        }
    }

    private void reloadTable() {
        loadPurchaseOrders();
        String[][] data = purchaseOrders.stream()
                .map(po -> new String[]{
                        po[0], po[1], po[2], po[3], po[4], 
                        po[5], po[6], po[7], po[8], po[9], 
                        po[10], po[11]
                })
                .toArray(String[][]::new);
        table.setModel(new javax.swing.table.DefaultTableModel(data, new String[]{
                "PO ID", "Date", "Requisition ID", "Item ID", "Quantity", 
                "Supplier ID", "PM ID", "Status", "Shipping Method", 
                "Shipping Cost", "Unit Price", "Total Price"
        }));
    }

    private void generateReceipt(String paymentId, String[] purchaseOrder) throws IOException {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        String supplierId = purchaseOrder[5];
        String itemId = purchaseOrder[3];
        String quantity = purchaseOrder[4];
        String unitPrice = purchaseOrder[10];
        String totalPrice = purchaseOrder[11];

        String supplierName = "Unknown";
        String supplierContact = "N/A";

        try (BufferedReader br = new BufferedReader(new FileReader(SUPPLIERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] supplier = line.split(";");
                if (supplier[0].equals(supplierId)) {
                    supplierName = supplier[2];
                    supplierContact = supplier[3];
                    break;
                }
            }
        }

        String itemName = "Unknown";
        try (BufferedReader br = new BufferedReader(new FileReader(ITEMS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] item = line.split(";");
                if (item[0].equals(itemId)) {
                    itemName = item[2];
                    break;
                }
            }
        }

        BufferedImage receipt = new BufferedImage(400, 600, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = receipt.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, 400, 600);

        // Set the border color
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(3));  // Border thickness

        // Draw the border
        g2d.drawRect(10, 10, 380, 580);

        // Set text color
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString("Receipt", 150, 30);

        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        g2d.drawString("Payment ID: " + paymentId, 20, 70); // Changed to "Payment ID"
        g2d.drawString("Date: " + date, 20, 100);
        g2d.drawString("Purchase Order ID: " + purchaseOrder[0], 20, 130);
        g2d.drawString("Supplier Name: " + supplierName, 20, 160);
        g2d.drawString("Supplier Contact: " + supplierContact, 20, 190);
        g2d.drawString("Item Code: " + purchaseOrder[3], 20, 220);
        g2d.drawString("Item Name: " + itemName, 20, 250);
        g2d.drawString("Quantity Ordered: " + quantity, 20, 280);
        g2d.drawString("Unit Price: " + unitPrice, 20, 310);
        g2d.drawString("Total Price: " + totalPrice, 20, 340);

        g2d.dispose();
        ImageIO.write(receipt, "png", new File(paymentId + "_receipt.png"));
    }



    private void viewPayments() {
        JFrame viewPaymentsFrame = new JFrame("Payments Viewer");
        viewPaymentsFrame.setSize(800, 400);
        viewPaymentsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        viewPaymentsFrame.setLocationRelativeTo(null);

        // Load payments from file
        List<String[]> payments = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(PAYMENTS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                payments.add(line.split(";"));
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading payments.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Table for displaying payments
        String[] columnNames = {"Payment ID", "Purchase Order ID", "Supplier ID", "Amount", "Status", "User ID"};
        String[][] data = payments.stream().toArray(String[][]::new);

        JTable paymentsTable = new JTable(data, columnNames);

        JButton viewReceiptButton = new JButton("View Receipt");
        viewReceiptButton.addActionListener(e -> {
            int selectedRow = paymentsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(viewPaymentsFrame, "Please select a payment to view its receipt.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String paymentId = (String) paymentsTable.getValueAt(selectedRow, 0);
            File receiptFile = new File(paymentId + "_receipt.png");

            if (!receiptFile.exists()) {
                JOptionPane.showMessageDialog(viewPaymentsFrame, "Receipt not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                BufferedImage receiptImage = ImageIO.read(receiptFile);
                ImageIcon icon = new ImageIcon(receiptImage);
                JLabel label = new JLabel(icon);
                JFrame receiptFrame = new JFrame("Receipt Viewer");
                receiptFrame.add(new JScrollPane(label));
                receiptFrame.setSize(450, 650);
                receiptFrame.setLocationRelativeTo(viewPaymentsFrame);
                receiptFrame.setVisible(true);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(viewPaymentsFrame, "Error loading receipt image.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(viewReceiptButton);

        JScrollPane scrollPane = new JScrollPane(paymentsTable);
        viewPaymentsFrame.add(scrollPane, BorderLayout.CENTER);
        viewPaymentsFrame.add(buttonPanel, BorderLayout.SOUTH);
        viewPaymentsFrame.setVisible(true);
    }
}
