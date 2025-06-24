package ClientSide.Reports;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class ReportsActivity extends AppCompatActivity {

    private Spinner filterSpinner;
    private RecyclerView reportsRecyclerView;
    private ReportAdapter reportAdapter;
    private List<Report> filteredReports;

    public static List<Report> reportList = new ArrayList<>();

    public static void addReport(Report report) {
        reportList.add(0, report);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        filterSpinner = findViewById(R.id.filterSpinner);
        reportsRecyclerView = findViewById(R.id.reportsRecyclerView);
        reportsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (reportList.isEmpty()) {
            reportList.add(new Report("Lost Wallet", "Wallet lost near the market.", "Market Area", "Lost and Found", null));
            reportList.add(new Report("Broken Street Light", "Street light is broken on Main St.", "Main St", "Hazards", null));
        }

        filteredReports = new ArrayList<>(reportList);

        reportAdapter = new ReportAdapter(filteredReports, report -> {
            Intent intent = new Intent(ReportsActivity.this, ReportDetailActivity.class);
            intent.putExtra("title", report.getTitle());
            intent.putExtra("description", report.getDescription());
            intent.putExtra("location", report.getLocation());
            intent.putExtra("type", report.getType());
            intent.putExtra("imageUri", report.getImageUri());
            startActivity(intent);
        });
        reportsRecyclerView.setAdapter(reportAdapter);

        String[] filterOptions = {"All", "Lost and Found", "Hazards", "Borrow", "General Report"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, filterOptions);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(spinnerAdapter);
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterReports(filterOptions[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (reportAdapter != null) {
            filterReports(filterSpinner.getSelectedItem().toString());
        }
    }

    private void filterReports(String filter) {
        filteredReports.clear();
        if (filter.equals("All")) {
            filteredReports.addAll(reportList);
        } else {
            for (Report report : reportList) {
                if (report.getType().equalsIgnoreCase(filter)) {
                    filteredReports.add(report);
                }
            }
        }
        reportAdapter.notifyDataSetChanged();
    }

    public static class Report {
        private String title;
        private String description;
        private String location;
        private String type;
        private String imageUri;

        public Report(String title, String description, String location, String type, String imageUri) {
            this.title = title;
            this.description = description;
            this.location = location;
            this.type = type;
            this.imageUri = imageUri;
        }

        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getLocation() { return location; }
        public String getType() { return type; }
        public String getImageUri() { return imageUri; }
    }

    public static class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {
        public interface OnItemClickListener {
            void onItemClick(Report report);
        }

        private List<Report> reports;
        private OnItemClickListener listener;

        public ReportAdapter(List<Report> reports, OnItemClickListener listener) {
            this.reports = reports;
            this.listener = listener;
        }

        @Override
        public ReportViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
            return new ReportViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ReportViewHolder holder, int position) {
            holder.bind(reports.get(position), listener);
        }

        @Override
        public int getItemCount() {
            return reports.size();
        }

        public static class ReportViewHolder extends RecyclerView.ViewHolder {
            ImageView reportImageView;
            TextView reportTitleTextView;
            TextView reportTypeTextView;
            TextView reportLocationTextView;

            public ReportViewHolder(View itemView) {
                super(itemView);
                reportImageView = itemView.findViewById(R.id.reportImageView);
                reportTitleTextView = itemView.findViewById(R.id.reportTitleTextView);
                reportTypeTextView = itemView.findViewById(R.id.reportTypeTextView);
                reportLocationTextView = itemView.findViewById(R.id.reportLocationTextView);
            }

            public void bind(Report report, OnItemClickListener listener) {
                reportTitleTextView.setText(report.getTitle());
                reportTypeTextView.setText(report.getType());
                reportLocationTextView.setText(report.getLocation());

                if (report.getImageUri() != null) {
                    Glide.with(itemView.getContext())
                            .load(Uri.parse(report.getImageUri()))
                            .placeholder(R.drawable.ic_placeholder_item)
                            .into(reportImageView);
                } else {
                    reportImageView.setImageResource(R.drawable.ic_placeholder_item);
                }
                itemView.setOnClickListener(v -> listener.onItemClick(report));
            }
        }
    }
}