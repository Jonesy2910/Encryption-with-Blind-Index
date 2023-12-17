package org.apache.maven.archetypes;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;

import java.io.IOException;

import java.security.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;


public class BlindIndexSearch {

    HashAllIndexes getHMAC = new HashAllIndexes();
    public List<decryptedDTO> searchBlindIndex(String searchText, String searchColumn) {
        List<decryptedDTO> results = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            //Adding bouncy castle
            Security.addProvider(new BouncyCastleProvider());
            SecretKey key = SystemManagement.readKeyFromFile();

            // Use blind index to find matching rows
            String selectQuery = "SELECT * FROM information WHERE " + searchColumn + "_index = ?";

            PreparedStatement selectStatement = conn.prepareStatement(selectQuery);

            // Hash search term to use as blind index
            String searchTermIndex = getHMAC.getHmacValue(searchText);

            //Testing hashed searchTerm
            System.out.println("This is the index value found for search term: " + searchTermIndex);
            // Setting search term so it can be used
            selectStatement.setString(1, searchTermIndex);

            ResultSet resultSet = selectStatement.executeQuery();

            // Display matching rows
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                byte[] encryptedDate = resultSet.getBytes("date");
                byte[] encryptedExpenseArea = resultSet.getBytes("expense_area");
                byte[] encryptedExpenseType = resultSet.getBytes("expense_type");
                byte[] encryptedSupplier = resultSet.getBytes("supplier");
                byte[] encryptedTransactionNumber = resultSet.getBytes("transaction_number");
                byte[] encryptedAmount = resultSet.getBytes("amount");
                byte[] encryptedDescription = resultSet.getBytes("description");
                byte[] encryptedSupplierPostcode = resultSet.getBytes("supplier_postcode");
                byte[] encryptedExpenditureType = resultSet.getBytes("expenditure_type");
//                String hashedExpenditureIndex = resultSet.getString("expenditure_type_index");
                byte[] iv = resultSet.getBytes("iv");

//                String encryptedExpenditureTypeHex = bytesToHex(encryptedExpenditureType);
//                System.out.println(encryptedExpenditureTypeHex);

                //Getting cipher
                Cipher cipher = getCipher(key, iv);

                //Decryption
                String date = decryptField(cipher, encryptedDate).trim();
                String expenseArea = decryptField(cipher, encryptedExpenseArea).trim();
                String expenseType = decryptField(cipher, encryptedExpenseType).trim();
                String supplier = decryptField(cipher, encryptedSupplier).trim();
                String transactionNumber = decryptField(cipher, encryptedTransactionNumber).trim();
                String amount = decryptField(cipher, encryptedAmount).trim();
                String description = decryptField(cipher, encryptedDescription).trim();
                String supplierPostcode = decryptField(cipher, encryptedSupplierPostcode).trim();
                String expenditureType = decryptField(cipher, encryptedExpenditureType).trim();


//                System.out.println(expenditureType);

                //Data transfer
                decryptedDTO row = new decryptedDTO(id, date,
                        expenseArea, expenseType,
                        supplier, transactionNumber,
                        amount, description,
                        supplierPostcode, expenditureType);

                //Adding row
                results.add(row);
//                Testing to see value of hashed index
//                System.out.println("This is the index value found for expenditure: " + hashedExpenditureIndex);
            }

        } catch (SQLException e) {
            System.err.println("SQL error: ");
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            System.err.println("No such class: ");
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.err.println("IO Exception:");
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            System.err.println("No Algorithm: " + e.getMessage());
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            System.err.println("Invalid key: " + e.getMessage());
            throw new RuntimeException(e);
        } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException |
                 BadPaddingException e) {
            throw new RuntimeException(e);
        }
        return results;
    }

    public static Cipher getCipher(SecretKey key, byte[] iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, NoSuchProviderException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "BC");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        return cipher;
    }

    //Decrypting then removing the extra characters at the end
    public String decryptField(Cipher cipher, byte[] encryptedField) throws IllegalBlockSizeException, BadPaddingException {
        byte[] decryptedText = cipher.doFinal(encryptedField);
        return new String(decryptedText);
    }
}