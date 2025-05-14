package POTS;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class VerifyPurchaseOrders extends JFrame {
    private String userId;
    private JTable table;
    private DefaultTableModel tableModel;
    private static final String PURCHASE_ORDERS_FILE = "purchaseorders.txt";

    public VerifyPurchaseOrders(String userId) {
        this.userId = userId;
        setTitle("Verify Purchase Orders");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initializationComponents();
    }

    private void initializationComponents() {
        JPanel panel = new JPanel(new BorderLayout());
        tableModel = new DefaultTableModel(
            new String[]{"Purchase Order ID", "Date (YYYY-MM-DD)", "Requisition ID", "Item ID", "Quantity Ordered", "Supplier ID", 
                         "Purchase Manager ID", "Status", "Shipping Method", "Shipping Cost"}, 0
        );

        table = new JTable(tableModel);
        loadPurchaseOrders();

        JScrollPane scrollPane = new JScrollPane(table);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton approveButton = new JButton("Approve");
        JButton rejectButton = new JButton("Reject");

        approveButton.addActionListener(e -> ApprovePurchaseOrder());
        rejectButton.addActionListener(e -> RejectPurchaseOrder());

        buttonPanel.add(approveButton);
        buttonPanel.add(rejectButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);
    }

    private void loadPurchaseOrders() {
        try (BufferedReader br = new BufferedReader(new FileReader(PURCHASE_ORDERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                tableModel.addRow(parts);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading purchase orders: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ApprovePurchaseOrder() {
        int confirmed = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to approve this?", 
                "Confirm Approve", JOptionPane.YES_NO_OPTION);

        if (confirmed == JOptionPane.YES_OPTION) {
            updateStatus("Approved");
        }
    }

    private void RejectPurchaseOrder() {
        int confirmed = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to reject this?", 
                "Confirm Reject", JOptionPane.YES_NO_OPTION);

        if (confirmed == JOptionPane.YES_OPTION) {
            updateStatus("Rejected");
        }
    }

    private void updateStatus(String status) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a purchase order to update.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String currentStatus = (String) tableModel.getValueAt(selectedRow, 7);
        if (!"Pending".equals(currentStatus)) {
            JOptionPane.showMessageDialog(this, "Only Pending purchase orders can be updated.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String purchaseOrderID = (String) tableModel.getValueAt(selectedRow, 0);

        // Update the status in the table
        tableModel.setValueAt(status, selectedRow, 7);

        // Update the status in the text file
        List<String> fileContent = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(PURCHASE_ORDERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts[0].equals(purchaseOrderID)) {
                    parts[7] = status; // Update the status
                    line = String.join(";", parts);
                }
                fileContent.add(line);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error updating purchase order status: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PURCHASE_ORDERS_FILE))) {
            for (String line : fileContent) {
                bw.write(line);
                bw.newLine();
            }
            JOptionPane.showMessageDialog(this, "Purchase order status updated successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving purchase order status: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
