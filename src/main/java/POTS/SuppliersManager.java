package POTS;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.List;

public class SuppliersManager extends JFrame {
    private String role;
    private ArrayList<String[]> suppliers;
    private JTable suppliersTable;
    private DefaultTableModel tableModel;
    private JTextField supplierIdField;
    private JTextField supplierCodeField;
    private JTextField supplierNameField;
    private JTextField contactInfoField;
    private static final String SUPPLIERS_FILE = "suppliers.txt";
    private static final String REQUISITIONS_FILE = "requisitions.txt";
    private static final String PURCHASE_ORDERS_FILE = "purchaseorders.txt";
    

    public SuppliersManager(String role) {
        this.role = role;
        setTitle("Suppliers Manager");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        loadSuppliers();
        initializationComponents();
    }

    private void loadSuppliers() {
        suppliers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(SUPPLIERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                suppliers.add(line.split(";"));
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading suppliers file!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveSuppliers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SUPPLIERS_FILE))) {
            for (String[] supplier : suppliers) {
                writer.write(String.join(";", supplier));
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving suppliers file!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initializationComponents() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table Setup
        String[] columns = {"Supplier ID", "Supplier Code", "Supplier Name", "Contact Info"};
        tableModel = new DefaultTableModel(columns, 0);
        suppliers.forEach(supplier -> tableModel.addRow(supplier));
        suppliersTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(suppliersTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Use FlowLayout for alignment

        String[] filterOptions = {"All", "Supplier ID", "Supplier Code", "Supplier Name", "Contact Info"};
        JComboBox<String> filterComboBox = new JComboBox<>(filterOptions);
        JTextField searchField = new JTextField(20); 

        JButton searchButton = new JButton("Search");
        JButton resetButton = new JButton("Reset");

        // Add components to searchPanel
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Filter by:"));
        searchPanel.add(filterComboBox);
        searchPanel.add(searchButton);
        searchPanel.add(resetButton);

        // Add Search Panel to the Main Panel
        panel.add(searchPanel, BorderLayout.NORTH);

        // Controls for Admin Role
        if (!role.equals("PM")) { // Only Inventory Manager and Admin can delete suppliers
            JPanel adminControls = new JPanel(new FlowLayout());

            JButton addButton = new JButton("Add Supplier");
            JButton editButton = new JButton("Edit Supplier");
            JButton deleteButton = new JButton("Delete Supplier");

            addButton.addActionListener(e -> addSupplier());
            editButton.addActionListener(e -> editSupplier());
            deleteButton.addActionListener(e -> deleteSupplier());

            adminControls.add(addButton);
            adminControls.add(editButton);
            adminControls.add(deleteButton);

            panel.add(adminControls, BorderLayout.SOUTH);
        }

        // Action Listeners for Search and Reset Buttons
        searchButton.addActionListener(e -> filterSuppliers(searchField.getText(), filterComboBox.getSelectedIndex()));
        resetButton.addActionListener(e -> resetFilters(searchField, filterComboBox));

        add(panel);
    }

    private void filterSuppliers(String searchText, int filterIndex) {
        String searchLower = searchText.trim().toLowerCase();

        ArrayList<String[]> filteredSuppliers = (ArrayList<String[]>) suppliers.stream()
            .filter(supplier -> {
                if (filterIndex == 0) { // "All" option
                    return supplier[0].toLowerCase().contains(searchLower) ||
                           supplier[1].toLowerCase().contains(searchLower) ||
                           supplier[2].toLowerCase().contains(searchLower) ||
                           supplier[3].toLowerCase().contains(searchLower);
                } else {
                    return supplier[filterIndex - 1].toLowerCase().contains(searchLower);
                }
            })
            .collect(Collectors.toList());

        updateTable(filteredSuppliers);
    }

    private void resetFilters(JTextField searchField, JComboBox<String> filterComboBox) {
        searchField.setText("");
        filterComboBox.setSelectedIndex(0); // Reset to "All"
        updateTable(suppliers);
    }


    private void updateTable(ArrayList<String[]> filteredSuppliers) {
        tableModel.setRowCount(0); // Clear the table
        filteredSuppliers.forEach(supplier -> tableModel.addRow(supplier));
    }

    private void addSupplier() {
        JTextField supplierIdField = new JTextField();
        JTextField supplierCodeField = new JTextField();
        JTextField supplierNameField = new JTextField();
        JTextField contactInfoField = new JTextField();

        Object[] fields = {
            "Supplier ID:", supplierIdField,
            "Supplier Code:", supplierCodeField,
            "Supplier Name:", supplierNameField,
            "Contact Info:", contactInfoField,
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Add Supplier", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String[] newSupplier = {
                supplierIdField.getText(),
                supplierCodeField.getText(),
                supplierNameField.getText(),
                contactInfoField.getText(),

            };
            suppliers.add(newSupplier);
            tableModel.addRow(newSupplier);
            saveSuppliers();
            
            JOptionPane.showMessageDialog(this, "The supplier added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void editSupplier() {
        int selectedRow = suppliersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a supplier to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Fetch the selected supplier's details
        String[] supplier = suppliers.get(selectedRow);

        // Restrict editable fields
        JTextField supplierCodeField = new JTextField(supplier[1]); // Editable
        JTextField supplierNameField = new JTextField(supplier[2]); // Editable
        JTextField contactInfoField = new JTextField(supplier[3]); // Editable

        // Keep non-editable fields fixed
        JLabel supplierIdLabel = new JLabel(supplier[0]); // Non-editable

        // Create the input dialog
        Object[] fields = {
            "Supplier ID (non-editable):", supplierIdLabel,
            "Supplier Code:", supplierCodeField,
            "Supplier Name:", supplierNameField,
            "Contact Info:", contactInfoField,
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Edit Supplier", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            // Update editable fields
            supplier[1] = supplierCodeField.getText(); // Update Supplier Code
            supplier[2] = supplierNameField.getText(); // Update Supplier Name
            supplier[3] = contactInfoField.getText(); // Update Contact Info

            // Update the table model to reflect changes
            tableModel.setValueAt(supplier[1], selectedRow, 1); // Update Supplier Code in table
            tableModel.setValueAt(supplier[2], selectedRow, 2); // Update Supplier Name in table
            tableModel.setValueAt(supplier[3], selectedRow, 3); // Update Contact Info in table

            // Save changes to the file
            saveSuppliers();
            
            JOptionPane.showMessageDialog(this, "The supplier was edited successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }


    private void deleteSupplier() {
        int selectedRow = suppliersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a supplier to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String supplierId = (String) tableModel.getValueAt(selectedRow, 0);

        // Check for references in items, requisitions, and purchase orders
        List<String> activeItems = findReferencesInItems(supplierId);
        List<String> activeRequisitions = findReferences(REQUISITIONS_FILE, 5, supplierId);
        List<String> activePurchaseOrders = findReferences(PURCHASE_ORDERS_FILE, 5, supplierId);

        if (!activeItems.isEmpty() || !activeRequisitions.isEmpty() || !activePurchaseOrders.isEmpty()) {
            StringBuilder message = new StringBuilder("Cannot delete supplier '").append(supplierId).append("' because it is referenced in:\n");

            if (!activeItems.isEmpty()) {
                message.append("Active Items: ").append(String.join(", ", activeItems)).append("\n");
            }
            if (!activeRequisitions.isEmpty()) {
                message.append("Active Purchase Requisitions: ").append(String.join(", ", activeRequisitions)).append("\n");
            }
            if (!activePurchaseOrders.isEmpty()) {
                message.append("Active Purchase Orders: ").append(String.join(", ", activePurchaseOrders));
            }

            JOptionPane.showMessageDialog(this, message.toString(), "Cannot Delete Supplier", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this supplier?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            suppliers.remove(selectedRow);
            tableModel.removeRow(selectedRow);
            saveSuppliers();

            JOptionPane.showMessageDialog(this, "The supplier was deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // method to check for references in items.txt
    private List<String> findReferencesInItems(String supplierId) {
        List<String> references = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("items.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts[3].equals(supplierId)) {
                    references.add(parts[0]); // Add the ItemID referencing this supplier
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading items.txt!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return references;
    }


    private List<String> findReferences(String fileName, int supplierColumnIndex, String supplierId) {
        List<String> references = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts[supplierColumnIndex].equals(supplierId)) {
                    references.add(parts[0]); // Add the ID of the referencing entity (RequisitionID or PurchaseOrderID)
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading " + fileName + "!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return references;
    }
        
}