package ClientSide.Reports;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class ReportsActivity extends AppCompatActivity {

    private Spinner filterSpinner;
    private RecyclerView reportsRecyclerView;
    private ReportAdapter reportAdapter;
    private List<Report> filteredReports;

    // Static list to hold all reports (dummy and user-submitted)
    public static List<Report> reportList = new ArrayList<>();

    // Public method to add a new report
    public static void addReport(Report report) {
        reportList.add(report);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        filterSpinner = findViewById(R.id.filterSpinner);
        reportsRecyclerView = findViewById(R.id.reportsRecyclerView);
        reportsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // If the list is empty, add some dummy reports
        if (reportList.isEmpty()) {
            reportList.add(new Report("Lost Wallet", "Wallet lost near the market.", "Market Area", "Lost and Found"));
            reportList.add(new Report("Broken Street Light", "Street light is broken on Main St.", "Main St", "Hazards"));
            reportList.add(new Report("Borrow Request", "Need to borrow a ladder.", "Community Center", "Borrow"));
            reportList.add(new Report("Found Keys", "Keys found in the park.", "City Park", "Lost and Found"));
        }

        filteredReports = new ArrayList<>(reportList);

        reportAdapter = new ReportAdapter(filteredReports, new ReportAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Report report) {
                // When a report is clicked, show the report details
                Intent intent = new Intent(ReportsActivity.this, ReportDetailActivity.class);
                intent.putExtra("title", report.getTitle());
                intent.putExtra("description", report.getDescription());
                intent.putExtra("location", report.getLocation());
                intent.putExtra("type", report.getType());
                startActivity(intent);
            }
        });
        reportsRecyclerView.setAdapter(reportAdapter);

        // Setup filter spinner with options
        String[] filterOptions = {"All", "Lost and Found", "Hazards", "Borrow"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, filterOptions);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(spinnerAdapter);
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedFilter = filterOptions[position];
                filterReports(selectedFilter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
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

    // Model class for a report.
    public static class Report {
        private String title;
        private String description;
        private String location;
        private String type;

        public Report(String title, String description, String location, String type) {
            this.title = title;
            this.description = description;
            this.location = location;
            this.type = type;
        }

        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getLocation() { return location; }
        public String getType() { return type; }
    }

    // RecyclerView Adapter for displaying reports.
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
            Report report = reports.get(position);
            holder.bind(report, listener);
        }

        @Override
        public int getItemCount() {
            return reports.size();
        }

        public static class ReportViewHolder extends RecyclerView.ViewHolder {
            TextView reportTitleTextView;
            TextView reportTypeTextView;

            public ReportViewHolder(View itemView) {
                super(itemView);
                reportTitleTextView = itemView.findViewById(R.id.reportTitleTextView);
                reportTypeTextView = itemView.findViewById(R.id.reportTypeTextView);
            }

            public void bind(Report report, OnItemClickListener listener) {
                reportTitleTextView.setText(report.getTitle());
                reportTypeTextView.setText(report.getType());
                itemView.setOnClickListener(v -> listener.onItemClick(report));
            }
        }
    }
}
