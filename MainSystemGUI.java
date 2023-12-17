package org.apache.maven.archetypes;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static org.apache.maven.archetypes.LoginGUI.username;


public class MainSystemGUI extends JFrame {
    private JButton addButton;
    private JButton searchButton;
    private JButton editButton;
    private JButton deleteButton;
    private JTextField dateField;
    private JTextField expenseTypeField;
    private JTextField expenseAreaField;
    private JTextField supplierField;
    private JTextField transactionNumberField;
    private JTextField amountField;
    private JTextField descriptionField;
    private JTextField supplierPostcodeField;
    private JTextField expenditureTypeField;
    private JPanel systemPanel;
    private JTable showTable;
    private JTextField searchField;
    private JComboBox<String> searchDropDown;
    private JButton closeSystemButton;
    private DefaultTableModel table;

    public void createMainGUI() {
        setTitle("Main System");
        setContentPane(systemPanel);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        new MainSystemGUI(searchDropDown);
        new MainSystemGUI();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    public MainSystemGUI() {

        searchButton.addActionListener(event -> {
            String searchText = searchField.getText();
            String searchColumn = (String) searchDropDown.getSelectedItem();
            BlindIndexSearch search = new BlindIndexSearch();
            List<decryptedDTO> results = search.searchBlindIndex(searchText, searchColumn);
            createTable(results);
            String logMessage = "User '" + username + "' has searched: '" + searchText + "'";
            MainSystemGUI.writeLogsToFile(logMessage);
        });

        addButton.addActionListener(event -> {
            // get the values from the form fields

            String date = dateField.getText();
            String expenseType = expenseTypeField.getText();
            String expenseArea = expenseAreaField.getText();
            String supplier = supplierField.getText();
            String transactionNumber = transactionNumberField.getText();
            String amount = amountField.getText();
            String description = descriptionField.getText();
            String supplierPostcode = supplierPostcodeField.getText();
            String expenditureType = expenditureTypeField.getText();

            if (date.isEmpty() || expenseArea.isEmpty() || expenseType.isEmpty() || supplier.isEmpty() || transactionNumber.isEmpty() || amount.isEmpty() || description.isEmpty() || supplierPostcode.isEmpty() || expenditureType.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter form correctly");
                return;
            }

            // call the addToDatabase class to add the information
            addToDatabase add = new addToDatabase();
            add.addInformation(date, expenseArea, expenseType, supplier, transactionNumber, amount, description, supplierPostcode, expenditureType);
            JOptionPane.showMessageDialog(null, "Added Successfully");
            dateField.setText("");
            expenseTypeField.setText("");
            expenseAreaField.setText("");
            supplierField.setText("");
            transactionNumberField.setText("");
            amountField.setText("");
            descriptionField.setText("");
            supplierPostcodeField.setText("");
            expenditureTypeField.setText("");

        });

        deleteButton.addActionListener(event -> {
            int[] selectedRows = showTable.getSelectedRows();
            if (selectedRows.length != 0) {
                int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the selected rows?", "Confirmation", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    List<Integer> rowsToDelete = new ArrayList<>();
                    for (int row : selectedRows) {
                        rowsToDelete.add(row);
                        int id = (int) table.getValueAt(row, 1);
                        removeFromDatabase remover = new removeFromDatabase();
                        remover.removeRow(id);
                        String logMessage = "User '" + username + "' has deleted ID: '" + id + "'";
                        MainSystemGUI.writeLogsToFile(logMessage);
                    }
                    for (int row : rowsToDelete) {
                        table.removeRow(row);
                    }
                    table.fireTableDataChanged();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please select a row to delete.");
            }
        });

        editButton.addActionListener(event -> {
            int row = showTable.getSelectedRow();
            if (row != -1) { // Check if a row is selected
                // Retrieve the values from the selected row

                int id = (int) table.getValueAt(row, 1);
                String date = table.getValueAt(row, 2).toString();
                String expenseType = table.getValueAt(row, 3).toString();
                String expenseArea = table.getValueAt(row, 4).toString();
                String supplier = table.getValueAt(row, 5).toString();
                String transactionNumber = table.getValueAt(row, 6).toString();
                String amount = table.getValueAt(row, 7).toString();
                String description = table.getValueAt(row, 8).toString();
                String supplierPostcode = table.getValueAt(row, 9).toString();
                String expenditureType = table.getValueAt(row, 10).toString();
                encryptedDTO encryptData = new encryptedDTO(id, date, expenseType, expenseArea, supplier, transactionNumber, amount, description, supplierPostcode, expenditureType);
                try {
                    System.out.print("TYpe: ");
                    System.out.println(encryptData.toString());


                    UpdateDatabase updateDatabase = new UpdateDatabase();
                    updateDatabase.updateInformation(encryptData);
                    String logMessage = "User '" + username + "' has edited ID: '" + id + "'";
                    MainSystemGUI.writeLogsToFile(logMessage);
                } catch (ClassNotFoundException | IllegalBlockSizeException | IOException | BadPaddingException | InvalidKeyException ex) {
                    ex.printStackTrace(); // or handle the exceptions in a way that suits your application
                } catch (NoSuchPaddingException | NoSuchAlgorithmException | NoSuchProviderException e) {
                    throw new RuntimeException(e);
                }

            } else {
                JOptionPane.showMessageDialog(null, "Please select a row to edit.");
            }
        });

        closeSystemButton.addActionListener(event -> {
            String logMessage = "User '" + username + "' has logged out!";
            MainSystemGUI.writeLogsToFile(logMessage);
            System.exit(0);
        });
    }

    public MainSystemGUI(JComboBox<String> searchDropDown) {
        this.searchDropDown = searchDropDown;

        String[] columnNames = {"Date", "Expense_Type",
                "Expense_Area", "Supplier",
                "Transaction_Number", "Amount",
                "Supplier_Postcode",
                "Expenditure_Type"};

        DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>(columnNames);
        searchDropDown.setModel(comboBoxModel);
    }

    public void createTable(List<decryptedDTO> results) {
        String[] columnNames = {"Edit", "ID",
                "Date", "Expense Type",
                "Expense Area", "Supplier",
                "Transaction Number", "Amount",
                "Description", "Supplier Postcode",
                "Expenditure Type"};

        table = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class getColumnClass(int column) {
                if (column == 0) {
                    return Boolean.class;
                }
                return String.class;
            }
        };
        table.setRowCount(results.size()); // Set the row count to the number of results
        for (int i = 0; i < results.size(); i++) {
            decryptedDTO decryptedData = results.get(i);
            table.setValueAt(null, i, 0);
            table.setValueAt(decryptedData.getID(), i, 1);
            table.setValueAt(decryptedData.getDate(), i, 2);
            table.setValueAt(decryptedData.getExpenseType(), i, 3);
            table.setValueAt(decryptedData.getExpenseArea(), i, 4);
            table.setValueAt(decryptedData.getSupplier(), i, 5);
            table.setValueAt(decryptedData.getTransactionNumber(), i, 6);
            table.setValueAt(decryptedData.getAmount(), i, 7);
            table.setValueAt(decryptedData.getDescription(), i, 8);
            table.setValueAt(decryptedData.getSupplierPostcode(), i, 9);
            table.setValueAt(decryptedData.getExpenditureType(), i, 10);
        }
        showTable.setModel(table);
    }

    public static void writeLogsToFile(String logMessage) {
        Logger logger = Logger.getLogger("Cipherist Log");
        FileHandler fileHandler = null;
        try {
            fileHandler = new FileHandler("C:\\Users\\Dan_J\\Documents\\Year 3 - Northumbria University\\Individual Computing Project\\VIVA\\KeyLocation\\log.txt", true);
            logger.addHandler(fileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            logger.info(logMessage);
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        } finally {
            if (fileHandler != null) {
                fileHandler.close();
            }
        }
    }
}