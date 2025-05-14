package POTS;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ItemsManager extends JFrame {
    private String roleId;
    private String userId;
    private JTable itemsTable;
    private DefaultTableModel tableModel;
    private static final String ITEMS_FILE = "items.txt";
    private static final String SUPPLIERS_FILE = "suppliers.txt";
    private static final String USERS_FILE = "users.txt";
    private static final String REQUISITIONS_FILE = "requisitions.txt";
    private static final String PURCHASE_ORDERS_FILE = "purchaseorders.txt";

    public ItemsManager(boolean canEdit, boolean canDelete, String userId) {
        this.userId = userId;
        this.roleId = determineRole(userId);

        setTitle("Items Manager");
        setSize(800, 525);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initializationComponents(canEdit, canDelete);
        loadItems();
    }

    private void initializationComponents(boolean canEdit, boolean canDelete) {
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Create the top panel for search and filter
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel searchLabel = new JLabel("Search:");
        JTextField searchField = new JTextField(15);

        JLabel filterLabel = new JLabel("Filter By:");
        JComboBox<String> filterComboBox = new JComboBox<>(new String[]{
            "All Fields", "ItemID", "Item Code", "Item Name", "Supplier ID", "Reorder Level", "Stock Level", "User ID"
        });

        JButton searchButton = new JButton("Search");
        JButton resetButton = new JButton("Reset");

        // Add components to the top panel
        topPanel.add(searchLabel);
        topPanel.add(searchField);
        topPanel.add(filterLabel);
        topPanel.add(filterComboBox);
        topPanel.add(searchButton);
        topPanel.add(resetButton);

        // Table configuration
        String[] columns = {"Item ID", "Item Code", "Item Name", "Supplier ID", "Reorder Level", "Stock Level", "User ID"};
        tableModel = new DefaultTableModel(columns, 0);
        itemsTable = new JTable(tableModel);

        // Custom cell renderer for highlighting rows
        itemsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                int stockLevel = Integer.parseInt(table.getValueAt(row, 5).toString());
                int reorderLevel = Integer.parseInt(table.getValueAt(row, 4).toString());
                if (stockLevel < reorderLevel) {
                    c.setBackground(Color.RED);
                } else {
                    c.setBackground(Color.WHITE);
                }
                return c;
            }
        });

        // Buttons panel
        JPanel buttonsPanel = new JPanel();
        JButton addButton = new JButton("Add Item");
        JButton editButton = new JButton("Edit Item");
        JButton deleteButton = new JButton("Delete Item");

        if (canEdit) buttonsPanel.add(addButton);
        if (canEdit) buttonsPanel.add(editButton);
        if (canDelete) buttonsPanel.add(deleteButton);

        // Add panels to the main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(itemsTable), BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        // Add main panel to the frame
        getContentPane().add(mainPanel);

        // Action Listeners
        searchButton.addActionListener(e -> performSearch(searchField.getText().trim(), filterComboBox.getSelectedItem().toString()));
        resetButton.addActionListener(e -> {
            searchField.setText("");
            filterComboBox.setSelectedIndex(0);
            loadItems(); // Reset to show all items
        });

        addButton.addActionListener(e -> addItem());
        editButton.addActionListener(e -> editItem());
        deleteButton.addActionListener(e -> deleteItem());
    }
    
    
    
    private void performSearch(String query, String filter) {
        tableModel.setRowCount(0); // Clear the current table data

        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Search query is empty. Showing all items.", "Search", JOptionPane.INFORMATION_MESSAGE);
            loadItems();
            return;
        }

        int columnIndex = getColumnIndex(filter);
        if (columnIndex == -1 && !filter.equals("All Fields")) {
            JOptionPane.showMessageDialog(this, "Invalid filter selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(ITEMS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] itemData = line.split(";");

                boolean match = false;
                if (filter.equals("All Fields")) {
                    for (String field : itemData) {
                        if (field.toLowerCase().contains(query.toLowerCase())) {
                            match = true;
                            break;
                        }
                    }
                } else if (columnIndex != -1) {
                    String fieldValue = itemData[columnIndex];
                    if (fieldValue.toLowerCase().contains(query.toLowerCase())) {
                        match = true;
                    }
                }

                if (match) {
                    tableModel.addRow(itemData);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading items file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No items match the search criteria.", "Search", JOptionPane.INFORMATION_MESSAGE);
        }
    }





    private String determineRole(String userId) {
        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts[0].equals(userId)) {
                    return parts[4]; // Role is at index 4
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading users file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return "";
    }

    private void loadItems() {
        tableModel.setRowCount(0);
        try (BufferedReader br = new BufferedReader(new FileReader(ITEMS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                tableModel.addRow(line.split(";"));
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading items file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addItem() {
        if (!roleId.equals("IM") && !roleId.equals("Admin")) {
            JOptionPane.showMessageDialog(this, "You do not have permission to add items.", "Permission Denied", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new GridLayout(6, 2));
        JTextField itemIdField = new JTextField();
        JTextField itemCodeField = new JTextField();
        JTextField itemNameField = new JTextField();
        JTextField supplierIdField = new JTextField();
        JTextField reorderLevelField = new JTextField();
        JTextField stockLevelField = new JTextField();

        panel.add(new JLabel("Item ID:"));
        panel.add(itemIdField);
        panel.add(new JLabel("Item Code:"));
        panel.add(itemCodeField);
        panel.add(new JLabel("Item Name:"));
        panel.add(itemNameField);
        panel.add(new JLabel("Supplier ID:"));
        panel.add(supplierIdField);
        panel.add(new JLabel("Reorder Level:"));
        panel.add(reorderLevelField);
        panel.add(new JLabel("Stock Level:"));
        panel.add(stockLevelField);

        int option = JOptionPane.showConfirmDialog(this, panel, "Enter Item Details", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String itemId = itemIdField.getText();
            String itemCode = itemCodeField.getText();
            String itemName = itemNameField.getText();
            String supplierId = supplierIdField.getText();
            String reorderLevel = reorderLevelField.getText();
            String stockLevel = stockLevelField.getText();

            if (itemIdExists(itemId)) {
                JOptionPane.showMessageDialog(this, "ItemID already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!supplierExists(supplierId)) {
                JOptionPane.showMessageDialog(this, "SupplierID does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String newItem = String.join(";", itemId, itemCode, itemName, supplierId, reorderLevel, stockLevel, userId);

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(ITEMS_FILE, true))) {
                bw.write(newItem);
                bw.newLine();
                JOptionPane.showMessageDialog(this, "Item added successfully.");
                loadItems();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error writing to items file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private boolean itemIdExists(String itemId) {
        try (BufferedReader br = new BufferedReader(new FileReader(ITEMS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.split(";")[0].equals(itemId)) {
                    return true;
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading items file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    private boolean supplierExists(String supplierId) {
        try (BufferedReader br = new BufferedReader(new FileReader(SUPPLIERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.split(";")[0].equals(supplierId)) {
                    return true;
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading suppliers file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    private void editItem() {
        int selectedRow = itemsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "No item selected for editing.");
            return;
        }

        String itemId = (String) tableModel.getValueAt(selectedRow, 0);
        String inventoryManagerId = (String) tableModel.getValueAt(selectedRow, 6);

        // Admin can edit any item, but IM can only edit items they are managing
        if (roleId.equals("IM") && !userId.equals(inventoryManagerId)) {
            JOptionPane.showMessageDialog(this, "You can only edit items managed by you.", "Permission Denied", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Display only editable fields (ReorderLevel and StockLevel)
        String reorderLevel = (String) tableModel.getValueAt(selectedRow, 4);
        String stockLevel = (String) tableModel.getValueAt(selectedRow, 5);

        JPanel panel = new JPanel(new GridLayout(2, 2));
        JTextField reorderLevelField = new JTextField(reorderLevel);
        JTextField stockLevelField = new JTextField(stockLevel);

        panel.add(new JLabel("Reorder Level:"));
        panel.add(reorderLevelField);
        panel.add(new JLabel("Stock Level:"));
        panel.add(stockLevelField);

        int option = JOptionPane.showConfirmDialog(this, panel, "Edit Item Details", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String newReorderLevel = reorderLevelField.getText();
            String newStockLevel = stockLevelField.getText();

            // Update the item in the file
            try {
                List<String> lines = Files.readAllLines(Paths.get(ITEMS_FILE));
                for (int i = 0; i < lines.size(); i++) {
                    String[] itemData = lines.get(i).split(";");
                    if (itemData[0].equals(itemId)) {
                        itemData[4] = newReorderLevel;
                        itemData[5] = newStockLevel;
                        lines.set(i, String.join(";", itemData));
                        break;
                    }
                }
                Files.write(Paths.get(ITEMS_FILE), lines);
                // Update the table model
                tableModel.setValueAt(newReorderLevel, selectedRow, 4);
                tableModel.setValueAt(newStockLevel, selectedRow, 5);
                JOptionPane.showMessageDialog(this, "Item updated successfully.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error editing item: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void deleteItem() {
        if (!roleId.equals("IM") && !roleId.equals("Admin")) {
            JOptionPane.showMessageDialog(this, "You do not have permission to delete items.", "Permission Denied", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int selectedRow = itemsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String itemId = (String) tableModel.getValueAt(selectedRow, 0);
        String inventoryManagerId = (String) tableModel.getValueAt(selectedRow, 6);

        // Admin can delete any item, but IM can only delete items they are managing
        if (roleId.equals("IM") && !userId.equals(inventoryManagerId)) {
            JOptionPane.showMessageDialog(this, "You can only delete items managed by you.", "Permission Denied", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check if the item is referenced in requisitions or purchase orders
        if (isItemReferenced(itemId)) {
            JOptionPane.showMessageDialog(this, "Cannot delete item. It is referenced in requisitions or purchase orders.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this item?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        // Delete item from the file
        try {
            List<String> lines = Files.readAllLines(Paths.get(ITEMS_FILE));
            lines.removeIf(line -> line.split(";")[0].equals(itemId));
            Files.write(Paths.get(ITEMS_FILE), lines);

            // Update table model
            tableModel.removeRow(selectedRow);
            JOptionPane.showMessageDialog(this, "Item deleted successfully.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error deleting item: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isItemReferenced(String itemId) {
        // Check requisitions file
        try (BufferedReader br = new BufferedReader(new FileReader(REQUISITIONS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");
                if (data[2].equals(itemId)) {
                    return true;
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading requisitions file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Chek purchase orders file
        try (BufferedReader br = new BufferedReader(new FileReader(PURCHASE_ORDERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");
                if (data[3].equals(itemId)) {
                    return true;
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading purchase orders file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        return false;
    }


    private void searchOrFilterItems() {
        JPanel panel = new JPanel(new GridLayout(2, 2));

        // Dropdown for selecting fields
        JLabel fieldLabel = new JLabel("Select Field:");
        JComboBox<String> fieldDropdown = new JComboBox<>(new String[]{
            "Item ID", "Item Code", "Item Name", "Supplier ID", "Reorder Level", "Stock Level", "User ID"
        });

        // Input for search value
        JLabel valueLabel = new JLabel("Enter Value:");
        JTextField valueField = new JTextField();

        panel.add(fieldLabel);
        panel.add(fieldDropdown);
        panel.add(valueLabel);
        panel.add(valueField);

        int result = JOptionPane.showConfirmDialog(
            this, panel, "Search/Filter Items", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(this, "Search/Filter operation cancelled.", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String field = fieldDropdown.getSelectedItem().toString();
        String value = valueField.getText().trim();

        if (value.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No value entered. Showing all items.", "Information", JOptionPane.INFORMATION_MESSAGE);
            loadItems();
            return;
        }

        int columnIndex = getColumnIndex(field);

        if (columnIndex == -1) {
            JOptionPane.showMessageDialog(this, "Invalid field selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        tableModel.setRowCount(0); // Clear the current table data

        try (BufferedReader br = new BufferedReader(new FileReader(ITEMS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] itemData = line.split(";");
                if (columnIndex < itemData.length && itemData[columnIndex].toLowerCase().contains(value.toLowerCase())) {
                    tableModel.addRow(itemData);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading items file.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No items match the criteria.", "Search/Filter", JOptionPane.INFORMATION_MESSAGE);
        }
    }


    private int getColumnIndex(String field) {
        // Map field names to column indices
        Map<String, Integer> columnMap = new HashMap<>();
        columnMap.put("Item ID", 0);
        columnMap.put("Item Code", 1);
        columnMap.put("Item Name", 2);
        columnMap.put("Supplier ID", 3);
        columnMap.put("Reorder Level", 4);
        columnMap.put("Stock Level", 5);
        columnMap.put("User ID", 6);

        return columnMap.getOrDefault(field, -1); // Return -1 for invalid fields
    }

}
