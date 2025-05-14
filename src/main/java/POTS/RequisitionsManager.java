package POTS;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class RequisitionsManager extends JFrame {
    private String userId;
    private String role;
    private JTable requisitionsTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> filterComboBox;
    private static final String ITEMS_FILE = "items.txt";
    private static final String SUPPLIERS_FILE = "suppliers.txt";
    private static final String USERS_FILE = "users.txt";
    private static final String REQUISITIONS_FILE = "requisitions.txt";
    

    public RequisitionsManager(String userId, String role) {
        this.userId = userId;
        this.role = role;
        setTitle("Requisitions Manager");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initializationComponents();
        loadRequisitions();
    }

    private void initializationComponents() {
        JPanel panel = new JPanel(new BorderLayout());
        tableModel = new DefaultTableModel(new String[] {
                "Requisition ID", "Date (YYYY-MM-DD)", "Item ID", "Quantity", "Required By", "Supplier ID",
                "User ID", "Status", "Purchase Order ID"
        }, 0);
        requisitionsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(requisitionsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Search and Filter Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchLabel = new JLabel("Search:");
        searchField = new JTextField(15);
        JLabel filterLabel = new JLabel("Filter by:");
        filterComboBox = new JComboBox<>(new String[] {
                "All", "Requisition ID", "Date", "Item ID", "Supplier ID", "Status"
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

        if ("SM".equals(role) || "Admin".equals(role)) {
            JPanel buttonPanel = new JPanel();
            JButton addButton = new JButton("Add");
            JButton editButton = new JButton("Edit");
            JButton cancelButton = new JButton("Cancel");

            addButton.addActionListener(e -> addRequisition());
            editButton.addActionListener(e -> editRequisition());
            cancelButton.addActionListener(e -> cancelRequisition());

            buttonPanel.add(addButton);
            buttonPanel.add(editButton);
            buttonPanel.add(cancelButton);
            panel.add(buttonPanel, BorderLayout.SOUTH);
        }

        getContentPane().add(panel);
    }

    private void loadRequisitions() {
        try (BufferedReader reader = new BufferedReader(new FileReader(REQUISITIONS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(";");
                tableModel.addRow(data);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading requisitions: " + e.getMessage());
        }
    }

    private void searchAndFilter() {
        String searchText = searchField.getText().trim();
        String filterBy = (String) filterComboBox.getSelectedItem();
        List<String[]> filteredRows = new ArrayList<>();

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            boolean matches = false;
            String requisitionId = tableModel.getValueAt(i, 0).toString();
            String date = tableModel.getValueAt(i, 1).toString();
            String itemId = tableModel.getValueAt(i, 2).toString();
            String supplierId = tableModel.getValueAt(i, 5).toString();
            String status = tableModel.getValueAt(i, 7).toString();

            switch (filterBy) {
                case "All":
                    matches = requisitionId.contains(searchText) || date.contains(searchText) ||
                              itemId.contains(searchText) || supplierId.contains(searchText) ||
                              status.contains(searchText);
                    break;
                case "Requisition ID":
                    matches = requisitionId.contains(searchText);
                    break;
                case "Date":
                    matches = date.contains(searchText);
                    break;
                case "Item ID":
                    matches = itemId.contains(searchText);
                    break;
                case "Supplier ID":
                    matches = supplierId.contains(searchText);
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
        tableModel.setRowCount(0);
        loadRequisitions();
    }

    private void refreshTable(List<String[]> rows) {
        tableModel.setRowCount(0);
        for (String[] row : rows) {
            tableModel.addRow(row);
        }
    }

    private void addRequisition() {
        // Panel for collecting requisition details from the user
        JPanel panel = new JPanel(new GridLayout(6, 2));
        
        JTextField requisitionIdField = new JTextField();
        
        String date = DateChooser.pickDate(this, "Select Date issued");
        if (date == null) {
            JOptionPane.showMessageDialog(this, "No date selected. Please select a date.");
            return; // Exit if no date is selected
        }
        
        
        JTextField itemIdField = new JTextField();
        
        
        JTextField quantityField = new JTextField();
        
        
        String requiredByDate = DateChooser.pickDate(this, "Select RequiredByDate");
        if (requiredByDate == null) {
            JOptionPane.showMessageDialog(this, "No required-by date selected. Please select a date.");
            return; // Exit if no date is selected
        }
        
        
        JTextField supplierIdField = new JTextField();

        
        
        panel.add(new JLabel("Requisition ID:"));
        panel.add(requisitionIdField);
    
  
        panel.add(new JLabel("Item ID:"));
        panel.add(itemIdField);
        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);
 
  
        panel.add(new JLabel("Supplier ID:"));
        panel.add(supplierIdField);

        // Display the panel for the user to input requisition details
        int option = JOptionPane.showConfirmDialog(this, panel, "Enter Requisition Details", JOptionPane.OK_CANCEL_OPTION);

        // If the user pressed "OK"
        if (option == JOptionPane.OK_OPTION) {
            String requisitionId = requisitionIdField.getText().trim();
 
            String itemId = itemIdField.getText().trim();
            String quantity = quantityField.getText().trim();
      
            String supplierId = supplierIdField.getText().trim();

            // Load existing Requisition IDs from the file into a Set
            Set<String> existingRequisitionIds = new HashSet<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(REQUISITIONS_FILE))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(";");
                    existingRequisitionIds.add(data[0]);  // Add Requisition ID to the set
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error loading requisitions: " + e.getMessage());
                return;
            }

            // Check for duplicate Requisition ID
            if (existingRequisitionIds.contains(requisitionId)) {
                JOptionPane.showMessageDialog(this, "Requisition ID already exists. Please enter a unique ID.");
                return;  // Exit the method if duplicate ID is found
            }

            // Validate if Item ID exists in items.txt
            boolean itemExists = false;
            try (BufferedReader itemReader = new BufferedReader(new FileReader(ITEMS_FILE))) {
                String line;
                while ((line = itemReader.readLine()) != null) {
                    String[] itemData = line.split(";");
                    if (itemData[0].equals(itemId)) {
                        itemExists = true;
                        break;
                    }
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error loading items: " + e.getMessage());
                return;
            }

            if (!itemExists) {
                JOptionPane.showMessageDialog(this, "Item ID does not exist. Please enter a valid Item ID.");
                return;
            }

            // validate if Supplier ID exists in suppliers.txt
            boolean supplierExists = false;
            try (BufferedReader supplierReader = new BufferedReader(new FileReader(SUPPLIERS_FILE))) {
                String line;
                while ((line = supplierReader.readLine()) != null) {
                    String[] supplierData = line.split(";");
                    if (supplierData[0].equals(supplierId)) {
                        supplierExists = true;
                        break;
                    }
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error loading suppliers: " + e.getMessage());
                return;
            }

            if (!supplierExists) {
                JOptionPane.showMessageDialog(this, "Supplier ID does not exist. Please enter a valid Supplier ID.");
                return;
            }

            // Validate the inputs (check if any field is empty)
            if (requisitionId.isEmpty() || date.isEmpty() || itemId.isEmpty() || quantity.isEmpty() ||
                    requiredByDate.isEmpty() || supplierId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields must be filled out.");
                return;
            }
            
            
         // validate Item ID and Supplier ID pair
            boolean isValidPair = false;
            try (BufferedReader itemReader = new BufferedReader(new FileReader(ITEMS_FILE))) {
                String line;
                while ((line = itemReader.readLine()) != null) {
                    String[] itemData = line.split(";");
                    if (itemData[0].equals(itemId) && itemData[3].equals(supplierId)) {
                        isValidPair = true;
                        break;
                    }
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error loading items: " + e.getMessage());
                return;
            }

            if (!isValidPair) {
                JOptionPane.showMessageDialog(this, "The Item ID and Supplier ID do not match. Please enter a valid pair.");
                return;
            }
            

            //Add the new requisition to the table
            String[] requisition = {
                    requisitionId, date, itemId, quantity, requiredByDate, supplierId, userId, "Pending", "-"
            };
            tableModel.addRow(requisition);

            // Save the new requisition to the file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(REQUISITIONS_FILE, true))) {
                writer.write(String.join(";", requisition));
                writer.newLine();
                JOptionPane.showMessageDialog(this, "The requisition is added successfully.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving requisition: " + e.getMessage());
            }
        }
    }



    private void editRequisition() {
        int selectedRow = requisitionsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "No requisition selected.");
            return;
        }

        String status = tableModel.getValueAt(selectedRow, 7).toString();
        if ("Approved/Converted to PO".equals(status) || "Cancelled".equals(status)) {
            JOptionPane.showMessageDialog(this, "Cannot edit requisitions with status 'Approved/Converted to PO' or 'Cancelled'.");
            return;
        }

        // Get current values from the selected row
        String requisitionId = tableModel.getValueAt(selectedRow, 0).toString();
        String date = tableModel.getValueAt(selectedRow, 1).toString();
        String itemId = tableModel.getValueAt(selectedRow, 2).toString();
        String quantity = tableModel.getValueAt(selectedRow, 3).toString();
        String requiredByDate = tableModel.getValueAt(selectedRow, 4).toString();
        String supplierId = tableModel.getValueAt(selectedRow, 5).toString();

        // Create a panel for editing the requisition
        JPanel panel = new JPanel(new GridLayout(6, 2));

        JTextField requisitionIdField = new JTextField(requisitionId);
        String newDate = DateChooser.pickDate(this,"Select Date issued");
        if (newDate == null) {
            JOptionPane.showMessageDialog(this, "No date selected. Please select a date.");
            return; // Exit if no date is selected
        }
        JTextField itemIdField = new JTextField(itemId);
        JTextField quantityField = new JTextField(quantity);
        String newRequiredByDate = DateChooser.pickDate(this, "Select RequiredByDate");
        if (newRequiredByDate == null) {
            JOptionPane.showMessageDialog(this, "No required-by date selected. Please select a date.");
            return; // Exit if no date is selected
        }
        JTextField supplierIdField = new JTextField(supplierId);

        panel.add(new JLabel("Requisition ID:"));
        panel.add(requisitionIdField);
        
        
        panel.add(new JLabel("Item ID:"));
        panel.add(itemIdField);
        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);
        
        
        panel.add(new JLabel("Supplier ID:"));
        panel.add(supplierIdField);

        int option = JOptionPane.showConfirmDialog(this, panel, "Edit Requisition Details", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            // Get updated values from the fields
            String newRequisitionId = requisitionIdField.getText().trim();
            
            String newItemId = itemIdField.getText().trim();
            String newQuantity = quantityField.getText().trim();
            
            String newSupplierId = supplierIdField.getText().trim();

            // Validate the inputs
            if (newRequisitionId.isEmpty() || newDate.isEmpty() || newItemId.isEmpty() || newQuantity.isEmpty() ||
                    newRequiredByDate.isEmpty() || newSupplierId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields must be filled out.");
                return;
            }
            
            
         //  Validate Item ID and Supplier ID pair
            boolean isValidPair = false;
            try (BufferedReader itemReader = new BufferedReader(new FileReader(ITEMS_FILE))) {
                String line;
                while ((line = itemReader.readLine()) != null) {
                    String[] itemData = line.split(";");
                    if (itemData[0].equals(newItemId) && itemData[3].equals(newSupplierId)) {
                        isValidPair = true;
                        break;
                    }
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error loading items: " + e.getMessage());
                return;
            }

            if (!isValidPair) {
                JOptionPane.showMessageDialog(this, "The Item ID and Supplier ID do not match. Please enter a valid pair.");
                return;
            }

            // Update the table model with new values
            tableModel.setValueAt(newRequisitionId, selectedRow, 0);
            tableModel.setValueAt(newDate, selectedRow, 1);
            tableModel.setValueAt(newItemId, selectedRow, 2);
            tableModel.setValueAt(newQuantity, selectedRow, 3);
            tableModel.setValueAt(newRequiredByDate, selectedRow, 4);
            tableModel.setValueAt(newSupplierId, selectedRow, 5);

            // Save the updated data to the file
            JOptionPane.showMessageDialog(this, "The requisition is edited successfully.");
            saveAllRequisitions();
        }
    }

 
    private void cancelRequisition() {
        int selectedRow = requisitionsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "No requisition selected.");
            return;
        }

        String status = tableModel.getValueAt(selectedRow, 7).toString();
        
        // Check if the requisition is already cancelled
        if ("Cancelled".equals(status)) {
            JOptionPane.showMessageDialog(this, "The requisition is cancelled already.");
            return;
        }

        if ("Approved/Converted to PO".equals(status)) {
            JOptionPane.showMessageDialog(this, "Cannot cancel requisitions with status 'Approved/Converted to PO'.");
            return;
        }

        // Show confirmation dialog for cancellation
        int confirmation = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to cancel this requisition?", 
            "Confirm Cancellation", 
            JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            tableModel.setValueAt("Cancelled", selectedRow, 7);
            JOptionPane.showMessageDialog(this, "The requisition is cancelled successfully!");
            saveAllRequisitions();
        }
    }

    
    

    private void saveRequisitionToFile(String[] requisition) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(REQUISITIONS_FILE, true))) {
            writer.write(String.join(";", requisition));
            writer.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving requisition: " + e.getMessage());
        }
    }

    private void saveAllRequisitions() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(REQUISITIONS_FILE))) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                List<String> requisition = new ArrayList<>();
                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                    requisition.add(tableModel.getValueAt(i, j).toString());
                }
                writer.write(String.join(";", requisition));
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving requisitions: " + e.getMessage());
        }
    }
}
