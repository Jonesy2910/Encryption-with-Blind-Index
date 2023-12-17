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

public class UpdateDatabase {

    EncryptAllFields encrypt = new EncryptAllFields();
    BlindIndexSearch index = new BlindIndexSearch();
    HashAllIndexes getHMAC = new HashAllIndexes();

    public void updateInformation(encryptedDTO dto) throws ClassNotFoundException, IllegalBlockSizeException, IOException, BadPaddingException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException {
        try (Connection conn = DBConnection.getConnection()) {
            //Selects row with matching ID
            String selectQuery = "SELECT * FROM information WHERE id = ?";
            PreparedStatement selectStatement = conn.prepareStatement(selectQuery);
            selectStatement.setInt(1, dto.getID());
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                // Retrieve the existing values that need compared to
                String existingDateIndex = resultSet.getString("date_index");
                String existingExpenseTypeIndex = resultSet.getString("expense_type_index");
                String existingExpenseAreaIndex = resultSet.getString("expense_area_index");
                String existingSupplierIndex = resultSet.getString("supplier_index");
                String existingTransactionNumberIndex = resultSet.getString("transaction_number_index");
                String existingAmountIndex = resultSet.getString("amount_index");
                String existingSupplierPostcodeIndex = resultSet.getString("supplier_postcode_index");
                String existingExpenditureTypeIndex = resultSet.getString("expenditure_type_index");

                //Gets bytes and description from Datanase
                byte[] existingDescription = resultSet.getBytes("description");
                byte[] iv = resultSet.getBytes("iv");

                //Checks to see if it doesn't equal the value retrieved from the database
                if (!existingDateIndex.equals(getHMAC.getHmacValue(dto.getDate()))) {
                    this.updateRow(conn, iv, "date", dto.getID(), dto.getDate());
                }

                if (!existingExpenseTypeIndex.equals(getHMAC.getHmacValue(dto.getExpenseType()))) {
                    this.updateRow(conn, iv, "expense_type", dto.getID(), dto.getExpenseType());
                }

                if (!existingExpenseAreaIndex.equals(getHMAC.getHmacValue(dto.getExpenseArea()))) {
                    this.updateRow(conn, iv, "expense_area", dto.getID(), dto.getExpenseArea());
                }

                if (!existingSupplierIndex.equals(getHMAC.getHmacValue(dto.getSupplier()))) {
                    this.updateRow(conn, iv, "supplier", dto.getID(), dto.getSupplier());
                }

                if (!existingTransactionNumberIndex.equals(getHMAC.getHmacValue(dto.getTransactionNumber()))) {
                    this.updateRow(conn, iv, "transaction_number", dto.getID(), dto.getTransactionNumber());
                }

                if (!existingAmountIndex.equals(getHMAC.getHmacValue(dto.getAmount()))) {
                    this.updateRow(conn, iv, "amount", dto.getID(), dto.getAmount());
                }

                if (this.hasDescriptionChanged(dto.getDescription(), existingDescription, iv)) {
                    this.updateRowWithoutIndex(conn, iv, dto.getID(), dto.getDescription());
                }

                if (!existingSupplierPostcodeIndex.equals(getHMAC.getHmacValue(dto.getSupplierPostcode()))) {
                    this.updateRow(conn, iv, "supplier_postcode", dto.getID(), dto.getSupplierPostcode());
                }

                if (!existingExpenditureTypeIndex.equals(getHMAC.getHmacValue(dto.getExpenditureType()))) {
                    this.updateRow(conn, iv, "expenditure_type", dto.getID(), dto.getExpenditureType());
                }
            }
        } catch (SQLException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateRow(Connection conn, byte[] iv, String column, Integer id, String value) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, IOException, NoSuchAlgorithmException, SQLException, InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchProviderException {
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        Security.addProvider(new BouncyCastleProvider());
        SecretKey key = SystemManagement.readKeyFromFile();
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "BC");

        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);

        byte[] encryptedValue = encrypt.encryptField(cipher, value);

        // Generate blind index for updated value
        String encryptedValueIndex = getHMAC.getHmacValue(value);

        // Updating existing row
        String updateQuery = "UPDATE information SET " + column + " = ?, " + column + "_index = ? WHERE id = ?";
        PreparedStatement updateStatement = conn.prepareStatement(updateQuery);
        updateStatement.setBytes(1, encryptedValue);
        updateStatement.setString(2, encryptedValueIndex);
        updateStatement.setInt(3, id);
        updateStatement.executeUpdate();
    }

    private void updateRowWithoutIndex(Connection conn, byte[] iv, Integer id, String value) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, IOException, NoSuchAlgorithmException, SQLException, InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchProviderException {
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        Security.addProvider(new BouncyCastleProvider());
        SecretKey key = SystemManagement.readKeyFromFile();

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        byte[] encryptedValue = encrypt.encryptField(cipher, value);

        String updateQuery = "UPDATE information SET " + "description" + " = ? WHERE id = ?";
        PreparedStatement updateStatement = conn.prepareStatement(updateQuery);
        updateStatement.setBytes(1, encryptedValue);
        updateStatement.setInt(2, id);
        updateStatement.executeUpdate();
    }

    private Boolean hasDescriptionChanged(String newDescription, byte[] existingDescription, byte[] iv) throws BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException, IOException, NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException {
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        Security.addProvider(new BouncyCastleProvider());
        SecretKey key = SystemManagement.readKeyFromFile();

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "BC");
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);

        String decryptedDescription = index.decryptField(cipher, existingDescription);

        return !newDescription.equals(decryptedDescription);
    }
}