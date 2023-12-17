package org.apache.maven.archetypes;

public class encryptedDTO {

    private final int id;
    private final String encryptDate;
    private final String encryptExpenseArea;
    private final String encryptExpenseType;
    private final String encryptSupplier;
    private final String encryptTransactionNumber;
    private final String encryptAmount;
    private final String encryptDescription;
    private final String encryptSupplierPostcode;
    private final String expenditureType;

    public encryptedDTO(int id, String encryptDate, String encryptExpenseArea, String encryptExpenseType, String encryptSupplier,
                        String encryptTransactionNumber, String encryptAmount, String encryptDescription, String encryptSupplierPostcode,
                        String encryptExpenditureType) {

        this.id = id;
        this.encryptDate = encryptDate;
        this.encryptExpenseArea = encryptExpenseArea;
        this.encryptExpenseType = encryptExpenseType;
        this.encryptSupplier = encryptSupplier;
        this.encryptSupplierPostcode = encryptSupplierPostcode;
        this.encryptTransactionNumber = encryptTransactionNumber;
        this.encryptAmount = encryptAmount;
        this.encryptDescription = encryptDescription;
        this.expenditureType = encryptExpenditureType;
    }

    @Override
    public String toString() {
        return "ID='" + id + '|' + "Date='" + encryptDate + '|' +
                ", Expense Area='" + encryptExpenseArea + '|' + ", Expense Type='" + encryptExpenseType + '|' +
                ", Supplier='" + encryptSupplier + '|' + ", Transaction Number='" + encryptTransactionNumber + '|' +
                ", Amount='" + encryptAmount + '|' + ", Description='" + encryptDescription + '|' +
                ", Supplier Postcode='" + encryptSupplierPostcode + '|' + ", Expenditure Type='" + expenditureType + '|';
    }

    public int getID(){
        return id;
    }

    public String getDate() {
        return encryptDate;
    }

    public String getExpenseType() {
        return encryptExpenseType;
    }
    public String getExpenseArea() {
        return encryptExpenseArea;
    }

    public String getSupplier() {
        return encryptSupplier;
    }

    public String getSupplierPostcode() {
        return encryptSupplierPostcode;
    }

    public String getTransactionNumber() {
        return encryptTransactionNumber;
    }

    public String getAmount() {
        return encryptAmount;
    }

    public String getDescription() {
        return encryptDescription;
    }

    public String getExpenditureType() {
        return expenditureType;
    }
}