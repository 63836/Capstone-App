package com.example.myapplication;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class GroqChatClient {

    // Adjust this URL if Groq's API endpoint changes
    private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final MediaType JSON_TYPE = MediaType.get("application/json; charset=utf-8");
    private OkHttpClient client;
    private String apiKey;

    public GroqChatClient(String apiKey) {
        this.apiKey = apiKey;
        client = new OkHttpClient();
    }

    public void sendChatMessage(String userMessage) {
        JSONObject jsonBody = new JSONObject();
        try {
            // Use the Groq model specified in your reference
            jsonBody.put("model", "deepseek-r1-distill-llama-70b");
            // Although the Python snippet uses an empty messages list,
            // here we include the user message so that the API receives a prompt.
            JSONArray messages = new JSONArray();
            JSONObject messageObj = new JSONObject();
            messageObj.put("role", "user");
            messageObj.put("content", userMessage);
            messages.put(messageObj);
            jsonBody.put("messages", messages);
            jsonBody.put("temperature", 0.6);
            jsonBody.put("max_completion_tokens", 4096);
            jsonBody.put("top_p", 0.95);
            jsonBody.put("stream", true);
            jsonBody.put("stop", JSONObject.NULL);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), JSON_TYPE);
        Request request = new Request.Builder()
                .url(GROQ_URL)
                .header("Authorization", "Bearer " + apiKey)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.err.println("HTTP request failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    System.err.println("Response error: " + response.code() + " " + response.message());
                    return;
                }
                String responseBody = response.body().string();
                System.out.println("Response: " + responseBody);
                try {
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    JSONArray choices = jsonResponse.getJSONArray("choices");
                    for (int i = 0; i < choices.length(); i++) {
                        JSONObject choice = choices.getJSONObject(i);
                        JSONObject message = choice.getJSONObject("message");
                        String content = message.optString("content");
                        System.out.print(content);
                    }
                } catch (JSONException e) {
                    System.err.println("JSON parsing error: " + e.getMessage());
                }
            }
        });
    }

    public static void main(String[] args) {
        // Retrieve the API key from BuildConfig (or hard-code for testing, but DO NOT do that in production)
        String apiKey = BuildConfig.GROQ_API_KEY;
        GroqChatClient client = new GroqChatClient(apiKey);
        client.sendChatMessage("hi");
    }
}
