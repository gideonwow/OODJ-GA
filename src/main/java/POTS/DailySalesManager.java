package POTS;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;

public class DailySalesManager extends JFrame {
    private String userId;
    private JTable salesTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> filterComboBox;
    private static final String ITEMS_FILE = "items.txt";
    private static final String DAILY_SALES_FILE = "dailysales.txt";

    public DailySalesManager(String userId) {
        this.userId = userId;
        setTitle("Daily Sales Manager");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initializationComponents();
        loadSales();
    }

    private void initializationComponents() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columnNames = {"Sales ID", "Date (YYYY-MM-DD)", "Item ID", "Quantity Sold", "User ID"};
        tableModel = new DefaultTableModel(columnNames, 0);
        salesTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(salesTable);

        JPanel searchPanel = new JPanel(new FlowLayout());
        JLabel searchLabel = new JLabel("Search:");
        searchField = new JTextField(20);

        filterComboBox = new JComboBox<>(new String[]{"All", "Sales ID", "Date", "Item ID", "User ID"});
        JButton searchButton = new JButton("Search");
        JButton resetButton = new JButton("Reset");

        searchButton.addActionListener(e -> searchSales());
        resetButton.addActionListener(e -> resetSearch());

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(filterComboBox);
        searchPanel.add(searchButton);
        searchPanel.add(resetButton);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add Sale");
        JButton editButton = new JButton("Edit Sale");
        JButton deleteButton = new JButton("Delete Sale");
        JButton sortButton = new JButton("Sort by Date");

        addButton.addActionListener(e -> addSale());
        editButton.addActionListener(e -> editSale());
        deleteButton.addActionListener(e -> deleteSale());
        sortButton.addActionListener(e -> loadSales()); // Sorts and reloads the table

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(sortButton);

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);
    }

    private void loadSales() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(DAILY_SALES_FILE));

            // Sort lines by date (second field in the entry)
            lines.sort(Comparator.comparing(line -> line.split(";")[1]));

            tableModel.setRowCount(0); // Clear existing data
            for (String line : lines) {
                String[] parts = line.split(";");
                tableModel.addRow(parts); // Add each line as a row
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading sales: " + e.getMessage());
        }
    }

    private void addSale() {
        JTextField salesIdField = new JTextField();
        JTextField itemIdField = new JTextField();
        JTextField quantityField = new JTextField();

        JPanel inputPanel = new JPanel(new GridLayout(5, 2));
        inputPanel.add(new JLabel("Sales ID:"));
        inputPanel.add(salesIdField);
        String date = DateChooser.pickDate(this, "Select Date");
        if (date == null) {
            JOptionPane.showMessageDialog(this, "No date selected. Please select a date.");
            return;
        }
        inputPanel.add(new JLabel("Item ID:"));
        inputPanel.add(itemIdField);
        inputPanel.add(new JLabel("Quantity Sold:"));
        inputPanel.add(quantityField);

        int result = JOptionPane.showConfirmDialog(this, inputPanel, "Add Sale", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String salesId = salesIdField.getText();
            String itemId = itemIdField.getText();
            String quantitySold = quantityField.getText();

            try {
                if (isDuplicateSalesId(salesId)) {
                    JOptionPane.showMessageDialog(this, "Duplicate Sales ID. Sale not added.");
                    return;
                }

                if (!isItemIdExists(itemId)) {
                    JOptionPane.showMessageDialog(this, "Item ID does not exist. Sale not added.");
                    return;
                }

                String newEntry = String.format("%s;%s;%s;%s;%s", salesId, date, itemId, quantitySold, userId);
                Files.write(Paths.get(DAILY_SALES_FILE), (newEntry + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
                tableModel.addRow(newEntry.split(";"));
                JOptionPane.showMessageDialog(this, "The daily sale is added successfully!");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error adding sale: " + e.getMessage());
            }
        }
    }

    private void editSale() {
        int selectedRow = salesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "No sale selected for editing.");
            return;
        }

        String salesId = (String) tableModel.getValueAt(selectedRow, 0);
        String date = (String) tableModel.getValueAt(selectedRow, 1);
        String itemId = (String) tableModel.getValueAt(selectedRow, 2);
        String quantitySold = (String) tableModel.getValueAt(selectedRow, 3);
        String existingUserId = (String) tableModel.getValueAt(selectedRow, 4);

        if (!existingUserId.equals(userId)) {
            JOptionPane.showMessageDialog(this, "You can only edit sales that you recorded.");
            return;
        }

        String newDate = DateChooser.pickDate(this, "Select Date");
        if (newDate == null) {
            JOptionPane.showMessageDialog(this, "No date selected. Please select a date.");
            return;
        }

        JTextField itemIdField = new JTextField(itemId);
        JTextField quantityField = new JTextField(quantitySold);

        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        inputPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        inputPanel.add(new JLabel(newDate));
        inputPanel.add(new JLabel("Item ID:"));
        inputPanel.add(itemIdField);
        inputPanel.add(new JLabel("Quantity Sold:"));
        inputPanel.add(quantityField);

        int result = JOptionPane.showConfirmDialog(this, inputPanel, "Edit Sale", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String newEntry = String.format("%s;%s;%s;%s;%s", salesId, newDate, itemIdField.getText(), quantityField.getText(), userId);

            try {
                if (!isItemIdExists(itemIdField.getText())) {
                    JOptionPane.showMessageDialog(this, "Item ID does not exist. Sale not updated.");
                    return;
                }

                List<String> lines = Files.readAllLines(Paths.get(DAILY_SALES_FILE));
                String oldEntry = String.join(";", salesId, date, itemId, quantitySold, userId);
                int index = lines.indexOf(oldEntry);
                lines.set(index, newEntry);
                Files.write(Paths.get(DAILY_SALES_FILE), lines);
                tableModel.setValueAt(newDate, selectedRow, 1);
                tableModel.setValueAt(itemIdField.getText(), selectedRow, 2);
                tableModel.setValueAt(quantityField.getText(), selectedRow, 3);

                JOptionPane.showMessageDialog(this, "The daily sale is edited successfully!");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error editing sale: " + e.getMessage());
            }
        }
    }

    private void deleteSale() {
        int selectedRow = salesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "No sale selected for deletion.");
            return;
        }

        String salesId = (String) tableModel.getValueAt(selectedRow, 0);
        String date = (String) tableModel.getValueAt(selectedRow, 1);
        String itemId = (String) tableModel.getValueAt(selectedRow, 2);
        String quantitySold = (String) tableModel.getValueAt(selectedRow, 3);

        String entryToDelete = String.join(";", salesId, date, itemId, quantitySold, userId);

        try {
            List<String> lines = Files.readAllLines(Paths.get(DAILY_SALES_FILE));
            lines.remove(entryToDelete);
            Files.write(Paths.get(DAILY_SALES_FILE), lines);
            tableModel.removeRow(selectedRow);
            JOptionPane.showMessageDialog(this, "The daily sale is deleted successfully!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error deleting sale: " + e.getMessage());
        }
    }

    private boolean isDuplicateSalesId(String salesId) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(DAILY_SALES_FILE));
        return lines.stream().anyMatch(line -> line.split(";")[0].equals(salesId));
    }

    private boolean isItemIdExists(String itemId) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(ITEMS_FILE));
        return lines.stream().anyMatch(line -> line.split(";")[0].equals(itemId));
    }

    private void searchSales() {
        String query = searchField.getText().toLowerCase();
        String filter = (String) filterComboBox.getSelectedItem();

        try {
            List<String> lines = Files.readAllLines(Paths.get(DAILY_SALES_FILE));
            tableModel.setRowCount(0);

            for (String line : lines) {
                String[] parts = line.split(";");
                boolean matches = filter.equals("All") || parts[filterComboBox.getSelectedIndex() - 1].toLowerCase().contains(query);
                if (matches) {
                    tableModel.addRow(parts);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error searching sales: " + e.getMessage());
        }
    }

    private void resetSearch() {
        searchField.setText("");
        loadSales();
    }
}
