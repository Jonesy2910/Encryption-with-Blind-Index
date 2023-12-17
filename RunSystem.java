package org.apache.maven.archetypes;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.sql.SQLException;


public class RunSystem {

    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException, IOException, InvalidKeyException, SQLException, ClassNotFoundException {

/*        Generating key*/
//        SecretKey key = SystemManagement.generateKey(); // generates key
//        String filePath = "C:\\Users\\Dan_J\\Documents\\Year 3 - Northumbria University\\Individual Computing Project\\VIVA\\KeyLocation\\key.txt";
//        SystemManagement.writeKeyToFile(key, filePath); // writing key to file
//
//        SecretKey HMACkey = SystemManagement.generateHMACKey();
//        String filePathHMAC = "C:\\Users\\Dan_J\\Documents\\Year 3 - Northumbria University\\Individual Computing Project\\VIVA\\KeyLocation\\HMACkey.txt";
//        SystemManagement.writeKeyToFile(HMACkey, filePathHMAC);

/*        Set up for blind indexes for database*/
//        HashAllIndexes hashAllIndexes = new HashAllIndexes();
//        hashAllIndexes.hashAllIndexes();
//
/*        Set up for encrypted files for database*/
//        EncryptAllFields EncryptAllFields = new EncryptAllFields();
//        EncryptAllFields.encryptAllFields();

        LoginGUI loginGUI = new LoginGUI();
        loginGUI.createLoginGUI();

//        MainSystemGUI MainSystemGUI = new MainSystemGUI();
//        MainSystemGUI.createMainGUI();
    }
}