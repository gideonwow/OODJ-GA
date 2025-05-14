package POTS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Calendar;

public class DateChooser extends JDialog {
    private JComboBox<String> dayComboBox, monthComboBox, yearComboBox;
    private String selectedDate;

    // Constructor with added title for each date
    public DateChooser(JFrame parent, String title) {
        super(parent, "Select " + title, true);  // Custom title for the dialog
        setLayout(new GridLayout(2, 1));

        // Panel for date selection
        JPanel datePanel = new JPanel(new FlowLayout());

        // Day ComboBox
        dayComboBox = new JComboBox<>();
        for (int i = 1; i <= 31; i++) {
            dayComboBox.addItem(String.format("%02d", i));
        }
        datePanel.add(new JLabel("Day:"));
        datePanel.add(dayComboBox);

        // Month ComboBox
        monthComboBox = new JComboBox<>();
        for (int i = 1; i <= 12; i++) {
            monthComboBox.addItem(String.format("%02d", i));
        }
        datePanel.add(new JLabel("Month:"));
        datePanel.add(monthComboBox);

        // Year ComboBox
        yearComboBox = new JComboBox<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = currentYear; i >= currentYear - 100; i--) {
            yearComboBox.addItem(String.valueOf(i));
        }
        datePanel.add(new JLabel("Year:"));
        datePanel.add(yearComboBox);

        add(datePanel);

        // Panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton confirmButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        confirmButton.addActionListener(e -> {
            String day = dayComboBox.getSelectedItem().toString();
            String month = monthComboBox.getSelectedItem().toString();
            String year = yearComboBox.getSelectedItem().toString();
            selectedDate = year + "-" + month + "-" + day; // Format date as YYYY-MM-DD
            dispose();
        });

        cancelButton.addActionListener(e -> {
            selectedDate = null; // No date selected
            dispose();
        });

        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel);

        pack();
        setLocationRelativeTo(parent);
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    // Static method to call the DateChooser for selecting dates, accepts a custom title
    public static String pickDate(JFrame parent, String title) {
        DateChooser dateChooser = new DateChooser(parent, title);
        dateChooser.setVisible(true);
        return dateChooser.getSelectedDate();
    }
}
