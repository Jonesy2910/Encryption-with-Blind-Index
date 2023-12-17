# Encryption-with-Blind-Index
Create a two tables in MySQL and call them users and information

CREATE TABLE `information` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `date` varbinary(255) NOT NULL,
  `date_index` varchar(255) NOT NULL,
  `expense_type` varbinary(255) NOT NULL,
  `expense_type_index` varchar(255) NOT NULL,
  `expense_area` varbinary(255) NOT NULL,
  `expense_area_index` varchar(255) NOT NULL,
  `supplier` varbinary(255) NOT NULL,
  `supplier_index` varchar(255) NOT NULL,
  `transaction_number` varbinary(255) NOT NULL,
  `transaction_number_index` varchar(255) NOT NULL,
  `amount` varbinary(255) NOT NULL,
  `amount_index` varchar(255) NOT NULL,
  `description` varbinary(255) NOT NULL,
  `supplier_postcode` varbinary(255) NOT NULL,
  `supplier_postcode_index` varchar(255) NOT NULL,
  `expenditure_type` varbinary(255) NOT NULL,
  `expenditure_type_index` varchar(255) NOT NULL,
  `iv` varbinary(255) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=116 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `users` (
  `username` varchar(30) NOT NULL,
  `password` varchar(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


Add a record to the users table that is shorter than 30 characters for username and password

Use the excel spreadsheet that has the information for the database to be imported directly into the file using table wizard in MySQL.

Instructions to setup encryption system:

1. In the RunSystem class file. 

Uncomment and select the correct .txt file that you would like to save the key to.

SecretKey key = SystemManagement.generateKey();
String filePath = "C:\\Users\\Dan_J\\OneDrive\\Desktop\\SecureKeyLocation\\key.txt";
SystemManagement.writeKeyToFile(key, filePath); 

and press click run

2.

Comment 


SecretKey key = SystemManagement.generateKey(); // generates key
String filePath = "C:\\Users\\Dan_J\\OneDrive\\Desktop\\SecureKeyLocation\\key.txt";
SystemManagement.writeKeyToFile(key, filePath); // writing key to file


3. 

Uncomment and select the correct .txt file that you would like to save the key to.

SecretKey HMACkey = SystemManagement.generateHMACKey();
String filePathHMAC = "C:\\Users\\Dan_J\\OneDrive\\Desktop\\SecureKeyLocation\\HMACkey.txt";
SystemManagement.writeKeyToFile(HMACkey, filePathHMAC);

and click run

4. 

Comment

SecretKey HMACkey = SystemManagement.generateHMACKey();
String filePathHMAC = "C:\\Users\\Dan_J\\OneDrive\\Desktop\\SecureKeyLocation\\HMACkey.txt";
SystemManagement.writeKeyToFile(HMACkey, filePathHMAC);


5. In the RunSystem class file. 

Uncomment

HashAllIndexes hashAllIndexes = new HashAllIndexes();
hashAllIndexes.hashAllIndexes();

and press click run

6.

Comment 


HashAllIndexes hashAllIndexes = new HashAllIndexes();
hashAllIndexes.hashAllIndexes();


7. 

Uncomment

EncryptAllFields EncryptAllFields = new EncryptAllFields();
EncryptAllFields.encryptAllFields();

and click run

8. 

Comment

EncryptAllFields EncryptAllFields = new EncryptAllFields();
EncryptAllFields.encryptAllFields();


9. 

Uncomment 

LoginGUI loginGUI = new LoginGUI();
loginGUI.createLoginGUI();

and click run

This should full create the system so that the program can be used.
