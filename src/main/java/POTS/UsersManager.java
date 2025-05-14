package POTS;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UsersManager extends JFrame {
    private JTable usersTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> filterComboBox;
    private static final String USERS_FILE = "users.txt";
    private static final String REQUISITIONS_FILE = "requisitions.txt";
    private static final String PURCHASE_ORDERS_FILE = "purchaseorders.txt";

    public UsersManager() {
        setTitle("Manage Users");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initializationComponents();
        loadUsersFromFile();
    }

    private void initializationComponents() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table Setup
        tableModel = new DefaultTableModel(new String[]{
                "UserID", "Username", "Full Name", "Password", "Role", "ContactInfo", "Branch"
        }, 0);
        usersTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(usersTable);

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchLabel = new JLabel("Search:");
        searchField = new JTextField(15);
        JLabel filterLabel = new JLabel("Filter by:");
        filterComboBox = new JComboBox<>(new String[]{
                "All", "UserID", "Username", "Full Name", "Role", "Branch"
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

        // Button Panel
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add User");
        JButton editButton = new JButton("Edit User");
        JButton deleteButton = new JButton("Delete User");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener(e -> addUser());
        editButton.addActionListener(e -> editUser());
        deleteButton.addActionListener(e -> deleteUser());
        refreshButton.addActionListener(e -> loadUsersFromFile());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        // Add panels to main panel
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        add(panel);
    }

    private void loadUsersFromFile() {
        tableModel.setRowCount(0);
        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] user = line.split(";");
                tableModel.addRow(user);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchAndFilter() {
        String searchText = searchField.getText().trim();
        String filterBy = (String) filterComboBox.getSelectedItem();
        List<String[]> filteredRows = new ArrayList<>();

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            boolean matches = false;
            String userId = tableModel.getValueAt(i, 0).toString();
            String username = tableModel.getValueAt(i, 1).toString();
            String fullName = tableModel.getValueAt(i, 2).toString();
            String role = tableModel.getValueAt(i, 4).toString();
            String branch = tableModel.getValueAt(i, 6).toString();

            // Determine if the row matches the search criteria
            switch (filterBy) {
                case "All":
                    matches = userId.contains(searchText) || username.contains(searchText) ||
                              fullName.contains(searchText) || role.contains(searchText) ||
                              branch.contains(searchText);
                    break;
                case "UserID":
                    matches = userId.contains(searchText);
                    break;
                case "Username":
                    matches = username.contains(searchText);
                    break;
                case "Full Name":
                    matches = fullName.contains(searchText);
                    break;
                case "Role":
                    matches = role.contains(searchText);
                    break;
                case "Branch":
                    matches = branch.contains(searchText);
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
        loadUsersFromFile();
    }

    private void refreshTable(List<String[]> rows) {
        tableModel.setRowCount(0); // Clear table
        for (String[] row : rows) {
            tableModel.addRow(row);
        }
    }


    private void addUser() {
        JPanel panel = new JPanel(new GridLayout(7, 2, 5, 5));
        JTextField userIdField = new JTextField();
        JTextField usernameField = new JTextField();
        JTextField fullNameField = new JTextField();
        JTextField passwordField = new JTextField();

        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"SM", "PM", "IM", "FM", "Admin"});
        JTextField contactInfoField = new JTextField();
        JTextField branchField = new JTextField();

        panel.add(new JLabel("User ID:"));
        panel.add(userIdField);
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Full Name:"));
        panel.add(fullNameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Role (SM/PM/IM/FM/Admin):"));
        panel.add(roleComboBox);
        panel.add(new JLabel("Contact Info:"));
        panel.add(contactInfoField);
        panel.add(new JLabel("Branch:"));
        panel.add(branchField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New User", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String userId = userIdField.getText().trim();
            String username = usernameField.getText().trim();
            String fullName = fullNameField.getText().trim();
            String password = passwordField.getText().trim();
            String role = (String) roleComboBox.getSelectedItem();
            String contactInfo = contactInfoField.getText().trim();
            String branch = branchField.getText().trim();

            if (!userId.isEmpty()) {
                if (isDuplicateUserID(userId)) {
                    JOptionPane.showMessageDialog(this, "User ID already exists. Please use a different User ID.", 
                            "Duplicate User ID", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try (BufferedWriter bw = new BufferedWriter(new FileWriter(USERS_FILE, true))) {
                    bw.write(String.join(";", userId, username, fullName, password, role, contactInfo, branch));
                    bw.newLine();
                    JOptionPane.showMessageDialog(this, "User added successfully!");
                    loadUsersFromFile();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Error adding user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "User ID cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            }
        }
    }


    private void editUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String originalUserId = (String) tableModel.getValueAt(selectedRow, 0);

        JPanel panel = new JPanel(new GridLayout(7, 2, 5, 5));
        JTextField userIdField = new JTextField(originalUserId);
        userIdField.setEditable(false); // User ID cannot be changed during editing
        JTextField usernameField = new JTextField((String) tableModel.getValueAt(selectedRow, 1));
        JTextField fullNameField = new JTextField((String) tableModel.getValueAt(selectedRow, 2));
        JTextField passwordField = new JTextField((String) tableModel.getValueAt(selectedRow, 3));
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"SM", "PM", "IM", "FM", "Admin"});
        roleComboBox.setSelectedItem(tableModel.getValueAt(selectedRow, 4));
        JTextField contactInfoField = new JTextField((String) tableModel.getValueAt(selectedRow, 5));
        JTextField branchField = new JTextField((String) tableModel.getValueAt(selectedRow, 6));

        panel.add(new JLabel("User ID:"));
        panel.add(userIdField);
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Full Name:"));
        panel.add(fullNameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Role (SM/PM/IM/FM/Admin):"));
        panel.add(roleComboBox);
        panel.add(new JLabel("Contact Info:"));
        panel.add(contactInfoField);
        panel.add(new JLabel("Branch:"));
        panel.add(branchField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit User", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String fullName = fullNameField.getText().trim();
            String password = passwordField.getText().trim();
            String role = (String) roleComboBox.getSelectedItem();
            String contactInfo = contactInfoField.getText().trim();
            String branch = branchField.getText().trim();

            List<String> users = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (!line.startsWith(originalUserId)) {
                        users.add(line);
                    } else {
                        users.add(String.join(";", originalUserId, username, fullName, password, role, contactInfo, branch));
                    }
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error editing user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(USERS_FILE))) {
                for (String user : users) {
                    bw.write(user);
                    bw.newLine();
                }
                JOptionPane.showMessageDialog(this, "User updated successfully!");
                loadUsersFromFile();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving users: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Updated deleteUser method
    private void deleteUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String userId = (String) tableModel.getValueAt(selectedRow, 0);

        // Check if user is associated with requisitions or purchase orders
        if (isUserInRequisitions(userId) || isUserInPurchaseOrders(userId)) {
            JOptionPane.showMessageDialog(this, "Cannot delete user. The user is associated with a requisition or purchase order.", 
                                      "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Proceed with deletion if no association
        List<String> users = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith(userId)) {
                    users.add(line);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error deleting user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USERS_FILE))) {
            for (String user : users) {
                bw.write(user);
                bw.newLine();
            }
            JOptionPane.showMessageDialog(this, "User deleted successfully!");
            loadUsersFromFile();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving users: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    
    private boolean isDuplicateUserID(String userId) {
        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length > 0 && parts[0].equals(userId)) {
                    return true;
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error checking for duplicate User ID: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
    
    
    
    private boolean isUserInRequisitions(String userId) {
        try (BufferedReader br = new BufferedReader(new FileReader(REQUISITIONS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length > 6 && parts[6].equals(userId)) { // Check SalesManagerID
                    return true;
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error checking requisitions: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
}

    private boolean isUserInPurchaseOrders(String userId) {
        try (BufferedReader br = new BufferedReader(new FileReader(PURCHASE_ORDERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length > 6 && parts[6].equals(userId)) { // Check PurchaseManagerID
                    return true;
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error checking purchase orders: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

}
