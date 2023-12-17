package org.apache.maven.archetypes;

import org.bouncycastle.jce.provider.BouncyCastleProvider;



import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.security.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.apache.maven.archetypes.EncryptAllFields.createIV;
import static org.apache.maven.archetypes.LoginGUI.username;

public class addToDatabase {

    public void addInformation(String date, String expenseArea, String expenseType, String supplier, String transactionNumber, String amount, String description, String supplierPostcode, String expenditureType) {

        HashAllIndexes getHMAC = new HashAllIndexes();
        try (Connection conn = DBConnection.getConnection()) {
            //Add Bouncy Castle
            Security.addProvider(new BouncyCastleProvider());
            //Reads Key
            SecretKey key = SystemManagement.readKeyFromFile();
            //Gets Cipher using Bouncy Castle
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "BC");

            System.out.println("Entered Supplier: " + supplier);

            // Creating blind index by hashing field
            String dateIndex = getHMAC.getHmacValue(date);
            String expenseAreaIndex = getHMAC.getHmacValue(expenseArea);
            String expenseTypeIndex = getHMAC.getHmacValue(expenseType);
            String supplierIndex = getHMAC.getHmacValue(supplier);
            String amountIndex = getHMAC.getHmacValue(amount);
            String transactionNumberIndex = getHMAC.getHmacValue(transactionNumber);
            String supplierPostcodeIndex = getHMAC.getHmacValue(supplierPostcode);
            String expenditureTypeIndex = getHMAC.getHmacValue(expenditureType);

            System.out.println("Created Index: " + supplierIndex);

            //Create new IV
            byte[] iv = createIV();
            //Initalising IV
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);

            //Encrypts all fields
            EncryptAllFields encrypt = new EncryptAllFields();
            byte[] encryptedDate = encrypt.encryptField(cipher, date);
            byte[] encryptedExpenseArea = encrypt.encryptField(cipher, expenseArea);
            byte[] encryptedExpenseType = encrypt.encryptField(cipher, expenseType);
            byte[] encryptedTransactionNumber = encrypt.encryptField(cipher, transactionNumber);
            byte[] encryptedAmount = encrypt.encryptField(cipher, amount);
            byte[] encryptedDescription = encrypt.encryptField(cipher, description);
            byte[] encryptedSupplierPostcode = encrypt.encryptField(cipher, supplierPostcode);
            byte[] encryptedExpenditureType = encrypt.encryptField(cipher, expenditureType);
            byte[] encryptedSupplier = encrypt.encryptField(cipher,supplier);

            //Testing to see encryption
            String encryptedDateHex = bytesToHex(encryptedSupplier);
            System.out.println("Created Encryption Converted To Hexadecimal " + encryptedDateHex);

            // Inserting new row into the table
            String insertQuery = "INSERT INTO information (date, expense_area, " +
                    "expense_type, supplier, " +
                    "transaction_number, amount, " +
                    "description, supplier_postcode, " +
                    "expenditure_type, date_index, " +
                    "expense_area_index, expense_type_index, " +
                    "supplier_index, transaction_number_index, " +
                    "amount_index, supplier_postcode_index, " +
                    "expenditure_type_index, iv) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement insertStatement = conn.prepareStatement(insertQuery);

            //Performing Query
            insertStatement.setBytes(1, encryptedDate);
            insertStatement.setBytes(2, encryptedExpenseArea);
            insertStatement.setBytes(3, encryptedExpenseType);
            insertStatement.setBytes(4, encryptedSupplier);
            insertStatement.setBytes(5, encryptedTransactionNumber);
            insertStatement.setBytes(6, encryptedAmount);
            insertStatement.setBytes(7, encryptedDescription);
            insertStatement.setBytes(8, encryptedSupplierPostcode);
            insertStatement.setBytes(9, encryptedExpenditureType);
            insertStatement.setString(10, dateIndex);
            insertStatement.setString(11, expenseAreaIndex);
            insertStatement.setString(12, expenseTypeIndex);
            insertStatement.setString(13, supplierIndex);
            insertStatement.setString(14, transactionNumberIndex);
            insertStatement.setString(15, amountIndex);
            insertStatement.setString(16, supplierPostcodeIndex);
            insertStatement.setString(17, expenditureTypeIndex);
            insertStatement.setBytes(18, iv);
            //Executes Query
            insertStatement.executeUpdate();

            //Logging
            String selectQuery = "SELECT id FROM information WHERE iv = ?";
            PreparedStatement selectStatement = conn.prepareStatement(selectQuery);
            selectStatement.setBytes(1, iv);
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String logMessage = "User '" + username + "' has added ID: '" + id + "'";
                MainSystemGUI.writeLogsToFile(logMessage);
            } else {
                System.out.println("Error retrieving ID");
            }


        } catch (SQLException | IOException | NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException |
                 InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException |
                 ClassNotFoundException | NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
    }

    //Converting bytes to hexadecimal
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}
