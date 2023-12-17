package org.apache.maven.archetypes;


import java.io.IOException;
import java.security.*;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class EncryptAllFields {

    public static void encryptAllFields() throws IOException {
        try (Connection connection = DBConnection.getConnection()) {
            Security.addProvider(new BouncyCastleProvider());
            SecretKey key = SystemManagement.readKeyFromFile();
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "BC");

            // Select all rows from the class table
            String selectQuery = "SELECT * FROM information";
            PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
            ResultSet resultSet = selectStatement.executeQuery();

            try {

                //Getting all rows from database
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String date = resultSet.getString("date");
                    String expenseArea = resultSet.getString("expense_area");
                    String expenseType = resultSet.getString("expense_type");
                    String supplier = resultSet.getString("supplier");
                    String transactionNumber = resultSet.getString("transaction_number");
                    String amount = resultSet.getString("amount");
                    String description = resultSet.getString("description");
                    String supplierPostcode = resultSet.getString("supplier_postcode");
                    String expenditureType = resultSet.getString("expenditure_type");

                    //Creating IV
                    byte[] iv = createIV();
                    //Initalising Cipher to encrypt
                    IvParameterSpec ivSpec = new IvParameterSpec(iv);
                    cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);

                    // Encrypts the fields
                    EncryptAllFields encrypt = new EncryptAllFields();
                    byte[] encryptedDate = encrypt.encryptField(cipher, date);
                    byte[] encryptedExpenseArea = encrypt.encryptField(cipher, expenseArea);
                    byte[] encryptedExpenseType = encrypt.encryptField(cipher, expenseType);
                    byte[] encryptedSupplier = encrypt.encryptField(cipher, supplier);
                    byte[] encryptedTransactionNumber = encrypt.encryptField(cipher, transactionNumber);
                    byte[] encryptedAmount = encrypt.encryptField(cipher, amount);
                    byte[] encryptedDescription = encrypt.encryptField(cipher, description);
                    byte[] encryptedSupplierPostcode = encrypt.encryptField(cipher, supplierPostcode);
                    byte[] encryptedExpenditureType = encrypt.encryptField(cipher, expenditureType);

                    // Updating the database
                    String updateQuery = "UPDATE information SET " +
                            "date = ?, expense_area = ?, " +
                            "expense_type = ?, " + "supplier = ?, " +
                            "transaction_number = ?," + "amount = ?, " +
                            "description = ?," + "supplier_postcode = ?, " +
                            "expenditure_type = ?, iv = ? " + "WHERE id = ?";

                    PreparedStatement stmt = connection.prepareStatement(updateQuery);
                    stmt.setBytes(1, encryptedDate);
                    stmt.setBytes(2, encryptedExpenseArea);
                    stmt.setBytes(3, encryptedExpenseType);
                    stmt.setBytes(4, encryptedSupplier);
                    stmt.setBytes(5, encryptedTransactionNumber);
                    stmt.setBytes(6, encryptedAmount);
                    stmt.setBytes(7, encryptedDescription);
                    stmt.setBytes(8, encryptedSupplierPostcode);
                    stmt.setBytes(9, encryptedExpenditureType);
                    stmt.setBytes(10, ivSpec.getIV());
                    stmt.setInt(11, id);

                    stmt.executeUpdate();
                }
            } catch (InvalidAlgorithmParameterException | SQLException |
                     IllegalBlockSizeException | NoSuchAlgorithmException | IOException | BadPaddingException |
                     InvalidKeyException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException |
                 NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] createIV() throws NoSuchAlgorithmException {
        SecureRandom random = new SecureRandom();
        byte[] iv = new byte[16];
        random.nextBytes(iv);
        return iv;
    }

    //Encrypting the fields
    public byte[] encryptField(Cipher cipher, String plaintext) throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
        byte[] plaintextBytes = plaintext.getBytes();
        int blockSize = cipher.getBlockSize();
        int paddedLength = (plaintextBytes.length / blockSize + 1) * blockSize;
        byte[] paddedPlaintext = Arrays.copyOf(plaintextBytes, paddedLength);
        return cipher.doFinal(paddedPlaintext);
    }
}