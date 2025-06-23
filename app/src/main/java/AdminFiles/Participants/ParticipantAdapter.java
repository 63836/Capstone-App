package AdminFiles.Participants;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantAdapter.ParticipantViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Participant participant);
    }

    private List<Participant> participants;
    private OnItemClickListener listener;

    public ParticipantAdapter(List<Participant> participants, OnItemClickListener listener) {
        this.participants = participants;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ParticipantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_participant, parent, false);
        return new ParticipantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParticipantViewHolder holder, int position) {
        Participant participant = participants.get(position);
        holder.participantNameTextView.setText(participant.getName());
        holder.itemView.setOnClickListener(v -> listener.onItemClick(participant));
    }

    @Override
    public int getItemCount() {
        return participants.size();
    }

    static class ParticipantViewHolder extends RecyclerView.ViewHolder {
        TextView participantNameTextView;

        public ParticipantViewHolder(@NonNull View itemView) {
            super(itemView);
            participantNameTextView = itemView.findViewById(R.id.participantNameTextView);
        }
    }
}
