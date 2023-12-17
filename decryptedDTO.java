package org.apache.maven.archetypes;

public class decryptedDTO {
    private final int id;
    private final String date;
    private final String expenseArea;
    private final String expenseType;
    private final String supplier;
    private final String transactionNumber;
    private final String amount;
    private final String description;
    private final String supplierPostcode;
    private final String expenditureType;

    public decryptedDTO(int id, String date,
                        String expenseArea, String expenseType,
                        String supplier, String transactionNumber,
                        String amount, String description,
                        String supplierPostcode, String expenditureType) {

        this.id = id;
        this.date = date;
        this.expenseArea = expenseArea;
        this.expenseType = expenseType;
        this.supplier = supplier;
        this.supplierPostcode = supplierPostcode;
        this.transactionNumber = transactionNumber;
        this.amount = amount;
        this.description = description;
        this.expenditureType = expenditureType;
    }

    public int getID(){
        return id;
    }
    public String getDate() {
        return date;
    }

    public String getExpenseType() {
        return expenseType;
    }
    public String getExpenseArea() {
        return expenseArea;
    }

    public String getSupplier() {
        return supplier;
    }

    public String getSupplierPostcode() {
        return supplierPostcode;
    }

    public String getTransactionNumber() {
        return transactionNumber;
    }

    public String getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }


    public String getExpenditureType() {
        return expenditureType;
    }
}