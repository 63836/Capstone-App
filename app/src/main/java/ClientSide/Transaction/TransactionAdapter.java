package ClientSide.Transaction;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    public interface OnTransactionClickListener {
        void onTransactionClick(Transaction transaction);
    }

    private Context context;
    private List<Transaction> transactionList;
    private OnTransactionClickListener listener;

    public TransactionAdapter(Context context, List<Transaction> transactionList, OnTransactionClickListener listener) {
        this.context = context;
        this.transactionList = transactionList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);
        holder.descriptionTextView.setText(transaction.getDescription());
        if (transaction.getAmount() < 0) {
            holder.amountTextView.setText(transaction.getAmount() + " Points");
            holder.amountTextView.setTextColor(Color.RED);
        } else {
            holder.amountTextView.setText("+" + transaction.getAmount() + " Points");
            holder.amountTextView.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        }
        holder.dateTextView.setText(transaction.getDate());
        // (Optional) You can show the unique code in a hidden view or tooltip if desired.
        // For now, we rely on the details page to show it.

        // Set click listener to notify the callback.
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTransactionClick(transaction);
            }
        });
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView descriptionTextView, amountTextView, dateTextView;
        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }

    public static class ProofOfClaimActivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_proof_of_claim);

            MaterialToolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Proof of Claim");

            String description = getIntent().getStringExtra("TRANSACTION_DESCRIPTION");
            int amount = getIntent().getIntExtra("TRANSACTION_AMOUNT", 0);
            String date = getIntent().getStringExtra("TRANSACTION_DATE");
            String uniqueCode = getIntent().getStringExtra("TRANSACTION_CODE");

            TextView descriptionTextView = findViewById(R.id.descriptionTextView);
            TextView amountTextView = findViewById(R.id.amountTextView);
            TextView dateTextView = findViewById(R.id.dateTextView);
            TextView uniqueCodeTextView = findViewById(R.id.uniqueCodeTextView);

            descriptionTextView.setText(description);
            amountTextView.setText(amount + " Points");
            dateTextView.setText(date);
            uniqueCodeTextView.setText(uniqueCode);
        }

        @Override
        public boolean onSupportNavigateUp() {
            onBackPressed();
            return true;
        }
    }
}
