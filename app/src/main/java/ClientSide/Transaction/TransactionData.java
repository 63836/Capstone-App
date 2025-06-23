package ClientSide.Transaction;

import java.util.ArrayList;
import java.util.List;

public class TransactionData {
    private static List<Transaction> transactionList = new ArrayList<>();

    public static List<Transaction> getTransactionList() {
        return transactionList;
    }

    public static void addTransaction(Transaction transaction) {
        // Insert at the beginning so the most recent is first.
        transactionList.add(0, transaction);
    }

    // Adds a transaction with a unique code.
    public static void addTransaction(String description, int amount, String date, String uniqueCode) {
        Transaction transaction = new Transaction(description, amount, date, uniqueCode);
        addTransaction(transaction);
    }

    // Overload if no unique code is provided.
    public static void addTransaction(String description, int amount, String date) {
        addTransaction(description, amount, date, "");
    }
}
