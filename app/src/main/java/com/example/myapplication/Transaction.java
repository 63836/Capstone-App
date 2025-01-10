package com.example.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

public class Transaction implements Parcelable {
    private String description;
    private int amount;
    private String date;

    public Transaction(String description, int amount, String date) {
        this.description = description;
        this.amount = amount;
        this.date = date;
    }

    // Parcelable implementation
    protected Transaction(Parcel in) {
        description = in.readString();
        amount = in.readInt();
        date = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(description);
        dest.writeInt(amount);
        dest.writeString(date);
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
}
