package ClientSide.Transaction;

import android.os.Parcel;
import android.os.Parcelable;

public class Transaction implements Parcelable {
    private String description;
    private int amount;
    private String date;
    private String uniqueCode;

    public Transaction(String description, int amount, String date, String uniqueCode) {
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.uniqueCode = uniqueCode;
    }

    // Fallback constructor if no unique code is provided.
    public Transaction(String description, int amount, String date) {
        this(description, amount, date, "");
    }

    // Parcelable implementation.
    protected Transaction(Parcel in) {
        description = in.readString();
        amount = in.readInt();
        date = in.readString();
        uniqueCode = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(description);
        dest.writeInt(amount);
        dest.writeString(date);
        dest.writeString(uniqueCode);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };

    // Getters
    public String getDescription() { return description; }
    public int getAmount() { return amount; }
    public String getDate() { return date; }
    public String getUniqueCode() { return uniqueCode; }
}
