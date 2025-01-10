package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private EditText inputEditText;
    private ImageButton sendButton;
    private OkHttpClient client;
    private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final MediaType JSON_TYPE = MediaType.get("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Use the new layout which follows the Android-ChatBot design
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.recyclerViewChat);
        inputEditText = findViewById(R.id.editTextMessage);
        sendButton = findViewById(R.id.buttonSend);

        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

        client = new OkHttpClient();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = inputEditText.getText().toString().trim();
                if (!message.isEmpty()) {
                    addMessage(new ChatMessage("user", message));
                    inputEditText.setText("");
                    sendChatMessage(message);
                }
            }
        });
    }

    private void addMessage(ChatMessage message) {
        chatMessages.add(message);
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        recyclerView.scrollToPosition(chatMessages.size() - 1);
    }

    private void sendChatMessage(String userMessage) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", "deepseek-r1-distill-llama-70b");
            JSONArray messages = new JSONArray();
            JSONObject userObj = new JSONObject();
            userObj.put("role", "user");
            userObj.put("content", userMessage);
            messages.put(userObj);
            jsonBody.put("messages", messages);
        } catch (JSONException e) {
            addMessage(new ChatMessage("assistant", "JSON error: " + e.getMessage()));
            return;
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), JSON_TYPE);
        Request request = new Request.Builder()
                .url(GROQ_URL)
                .header("Authorization", "Bearer " + BuildConfig.GROQ_API_KEY)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback(){
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        addMessage(new ChatMessage("assistant", "Error: " + e.getMessage()))
                );
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    String responseBody = response.body().string();
                    try{
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        JSONArray choices = jsonResponse.getJSONArray("choices");
                        if(choices.length() > 0){
                            JSONObject choice = choices.getJSONObject(0);
                            JSONObject message = choice.getJSONObject("message");
                            String content = message.getString("content");
                            runOnUiThread(() ->
                                    addMessage(new ChatMessage("assistant", content))
                            );
                        }
                    } catch (JSONException e){
                        runOnUiThread(() ->
                                addMessage(new ChatMessage("assistant", "Response error: " + e.getMessage()))
                        );
                    }
                } else {
                    runOnUiThread(() ->
                            addMessage(new ChatMessage("assistant", "Response error: " + response.code()))
                    );
                }
            }
        });
    }
}
