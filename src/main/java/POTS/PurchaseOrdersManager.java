package POTS;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class PurchaseOrdersManager extends JFrame {
    private String userId;
    private String role;
    private JTable purchaseOrderTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> filterComboBox;
    private static final String PURCHASE_ORDERS_FILE = "purchaseorders.txt";
    private static final String REQUISITIONS_FILE = "requisitions.txt";

    public PurchaseOrdersManager(String userId, String role) {
        this.userId = userId;
        this.role = role;
        setTitle("Purchase Order Manager");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initializationComponents();
    }

    private void initializationComponents() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table to display purchase orders
        tableModel = new DefaultTableModel(new String[]{
                "Purchase Order ID", "Date (YYYY-MM-DD)", "Requisition ID", "Item ID", "Quantity Ordered", "Supplier ID",
                "User ID", "Status", "Shipping Method", "Shipping Cost", "Unit Price", "Total Price"
        }, 0);
        purchaseOrderTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(purchaseOrderTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Search and Filter Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchLabel = new JLabel("Search:");
        searchField = new JTextField(15);
        JLabel filterLabel = new JLabel("Filter by:");
        filterComboBox = new JComboBox<>(new String[]{
                "All", "PurchaseOrderID", "Date", "RequisitionID", "ItemID", "SupplierID", "Status"
        });
        JButton searchButton = new JButton("Search");
        JButton resetButton = new JButton("Reset");

        searchButton.addActionListener(e -> searchAndFilter());
        resetButton.addActionListener(e -> resetTable());

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(filterLabel);
        searchPanel.add(filterComboBox);
        searchPanel.add(searchButton);
        searchPanel.add(resetButton);

        panel.add(searchPanel, BorderLayout.NORTH);

        // Buttons for operations
        JButton addButton = new JButton("Add");
        JButton editButton = new JButton("Edit");
 

        if (role.equals("SM") || role.equals("FM")) {
            addButton.setEnabled(false);
            editButton.setEnabled(false);
  
        }

        // Add action listeners
        addButton.addActionListener(e -> addPurchaseOrder());
        editButton.addActionListener(e -> editPurchaseOrder());
 

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        getContentPane().add(panel);
        loadPurchaseOrders();
    }

    private void loadPurchaseOrders() {
        tableModel.setRowCount(0);
        try (BufferedReader br = new BufferedReader(new FileReader(PURCHASE_ORDERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                tableModel.addRow(parts);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading purchase orders: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchAndFilter() {
        String searchText = searchField.getText().trim();
        String filterBy = (String) filterComboBox.getSelectedItem();
        List<String[]> filteredRows = new ArrayList<>();

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            boolean matches = false;
            String purchaseOrderID = tableModel.getValueAt(i, 0).toString();
            String date = tableModel.getValueAt(i, 1).toString();
            String requisitionID = tableModel.getValueAt(i, 2).toString();
            String itemID = tableModel.getValueAt(i, 3).toString();
            String supplierID = tableModel.getValueAt(i, 5).toString();
            String status = tableModel.getValueAt(i, 7).toString();

            switch (filterBy) {
                case "All":
                    matches = purchaseOrderID.contains(searchText) || date.contains(searchText) ||
                            requisitionID.contains(searchText) || itemID.contains(searchText) ||
                            supplierID.contains(searchText) || status.contains(searchText);
                    break;
                case "PurchaseOrderID":
                    matches = purchaseOrderID.contains(searchText);
                    break;
                case "Date":
                    matches = date.contains(searchText);
                    break;
                case "RequisitionID":
                    matches = requisitionID.contains(searchText);
                    break;
                case "ItemID":
                    matches = itemID.contains(searchText);
                    break;
                case "SupplierID":
                    matches = supplierID.contains(searchText);
                    break;
                case "Status":
                    matches = status.contains(searchText);
                    break;
            }

            if (matches) {
                String[] row = new String[tableModel.getColumnCount()];
                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                    row[j] = tableModel.getValueAt(i, j).toString();
                }
                filteredRows.add(row);
            }
        }

        refreshTable(filteredRows);
    }

    private void resetTable() {
        searchField.setText("");
        filterComboBox.setSelectedIndex(0);
        loadPurchaseOrders();
    }

    private void refreshTable(List<String[]> rows) {
        tableModel.setRowCount(0);
        for (String[] row : rows) {
            tableModel.addRow(row);
        }
    }

    private void addPurchaseOrder() {
        try {
            // Load requisitions to select from
            List<String[]> pendingRequisitions = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(REQUISITIONS_FILE))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(";");
                    if (parts[7].equalsIgnoreCase("Pending")) {
                        pendingRequisitions.add(parts);
                    }
                }
            }

            if (pendingRequisitions.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No pending requisitions available to convert to purchase orders.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Let the user select a requisition
            String requisitionID = (String) JOptionPane.showInputDialog(this, "Select a requisition to convert to a Purchase Order:",
                    "Select Requisition", JOptionPane.QUESTION_MESSAGE, null,
                    pendingRequisitions.stream().map(r -> r[0]).toArray(String[]::new), null);

            if (requisitionID == null) return; // User cancelled

            // Find selected requisition
            String[] selectedRequisition = pendingRequisitions.stream().filter(r -> r[0].equals(requisitionID)).findFirst().orElse(null);
            if (selectedRequisition == null) {
                JOptionPane.showMessageDialog(this, "Requisition not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Prompt the user for Purchase Order ID
            String purchaseOrderID = JOptionPane.showInputDialog(this, "Enter the Purchase Order ID:");
            if (purchaseOrderID == null || purchaseOrderID.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Purchase Order ID is required.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Check if the entered Purchase Order ID is unique
            if (!isPurchaseOrderIDUnique(purchaseOrderID)) {
                JOptionPane.showMessageDialog(this, "Purchase Order ID already exists. Please enter a unique ID.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Prompt for additional details
            String date = DateChooser.pickDate(this, "Select Date issued");
            if (date == null) {
                JOptionPane.showMessageDialog(this, "No date selected. Please select a date.");
                return; // Exit if no date is selected
            }

            String[] shippingMethods = {"Air", "Ground", "Express"};
            String shippingMethod = (String) JOptionPane.showInputDialog(this, "Select the shipping method:",
                    "Shipping Method", JOptionPane.QUESTION_MESSAGE, null, shippingMethods, shippingMethods[0]);
            if (shippingMethod == null) {
                JOptionPane.showMessageDialog(this, "Shipping method is required.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String shippingCostStr = JOptionPane.showInputDialog(this, "Enter the shipping cost:");
            if (shippingCostStr == null || shippingCostStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Shipping cost is required.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            double shippingCost = Double.parseDouble(shippingCostStr);

            String unitPriceStr = JOptionPane.showInputDialog(this, "Enter the Unit Price:");
            if (unitPriceStr == null || unitPriceStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Unit Price is required.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            double unitPrice = Double.parseDouble(unitPriceStr);

            int quantityOrdered = Integer.parseInt(selectedRequisition[3]);

            // Calculate Total Price
            double totalPrice = (unitPrice * quantityOrdered) + shippingCost;

            // Write to the purchase order file
            String newPurchaseOrder = String.join(";", purchaseOrderID, date, selectedRequisition[0],
                    selectedRequisition[2], String.valueOf(quantityOrdered), selectedRequisition[5], userId,
                    "Pending", shippingMethod, String.valueOf(shippingCost), String.valueOf(unitPrice), String.valueOf(totalPrice));

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(PURCHASE_ORDERS_FILE, true))) {
                bw.write(newPurchaseOrder);
                bw.newLine();
            }

            // Update the requisition status and link the Purchase Order ID
            List<String> updatedRequisitions = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(REQUISITIONS_FILE))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.startsWith(selectedRequisition[0])) {
                        String[] parts = line.split(";");
                        // Update the status and assign the new Purchase Order ID
                        parts[7] = "Approved/Converted to PO";
                        parts[8] = purchaseOrderID; // Ensure only the new PO ID is added
                        line = String.join(";", parts);
                    }
                    updatedRequisitions.add(line);
                }
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(REQUISITIONS_FILE))) {
                for (String req : updatedRequisitions) {
                    bw.write(req);
                    bw.newLine();
                }
            }

            JOptionPane.showMessageDialog(this, "Purchase order created successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadPurchaseOrders();
        } catch (IOException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error adding purchase order: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    // Check if Purchase Order ID is unique
    private boolean isPurchaseOrderIDUnique(String purchaseOrderID) {
        try (BufferedReader br = new BufferedReader(new FileReader(PURCHASE_ORDERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts[0].equals(purchaseOrderID)) {
                    return false;
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error checking Purchase Order ID uniqueness: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return true;
    }




    private void editPurchaseOrder() {
        int selectedRow = purchaseOrderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a purchase order to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String status = (String) tableModel.getValueAt(selectedRow, 7);
        if (status.equalsIgnoreCase("Approved") || status.equalsIgnoreCase("Paid")) {
            JOptionPane.showMessageDialog(this, "Approved or Paid purchase orders cannot be edited.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String purchaseOrderID = (String) tableModel.getValueAt(selectedRow, 0);

        // Prompt for new details
        String newDate = JOptionPane.showInputDialog(this, "Enter the new date (YYYY-MM-DD):");
        if (newDate == null || newDate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Date is required.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] shippingMethods = {"Air", "Ground", "Express"};
        String newShippingMethod = (String) JOptionPane.showInputDialog(this, "Select the new shipping method:",
                "Shipping Method", JOptionPane.QUESTION_MESSAGE, null, shippingMethods, shippingMethods[0]);
        if (newShippingMethod == null) {
            JOptionPane.showMessageDialog(this, "Shipping method is required.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String newShippingCostStr = JOptionPane.showInputDialog(this, "Enter the new shipping cost:");
        if (newShippingCostStr == null || newShippingCostStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Shipping cost is required.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double newShippingCost = Double.parseDouble(newShippingCostStr);

        String newUnitPriceStr = JOptionPane.showInputDialog(this, "Enter the new Unit Price:");
        if (newUnitPriceStr == null || newUnitPriceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Unit Price is required.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double newUnitPrice = Double.parseDouble(newUnitPriceStr);

        int quantityOrdered = Integer.parseInt((String) tableModel.getValueAt(selectedRow, 4));

        // Calculate new Total Price
        double newTotalPrice = (newUnitPrice * quantityOrdered) + newShippingCost;

        // Update the file
        try {
            List<String> updatedOrders = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(PURCHASE_ORDERS_FILE))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.startsWith(purchaseOrderID)) {
                        String[] parts = line.split(";");
                        parts[1] = newDate;
                        parts[8] = newShippingMethod;
                        parts[9] = String.valueOf(newShippingCost);
                        parts[10] = String.valueOf(newUnitPrice);
                        parts[11] = String.valueOf(newTotalPrice);
                        line = String.join(";", parts);
                    }
                    updatedOrders.add(line);
                }
            }
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(PURCHASE_ORDERS_FILE))) {
                for (String order : updatedOrders) {
                    bw.write(order);
                    bw.newLine();
                }
            }
            JOptionPane.showMessageDialog(this, "Purchase order updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadPurchaseOrders();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error editing purchase order: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}