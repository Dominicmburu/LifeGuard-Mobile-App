package com.example.urgentcare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.urgentcare.bot.ChatAdapter;
import com.example.urgentcare.bot.ChatMessage;
import com.example.urgentcare.bot.IntentClass;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;;

public class SecondBotActivity extends AppCompatActivity {
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private OkHttpClient client;
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private Set<String> answeredQuestions = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_bot);

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages, this);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);
        client = new OkHttpClient();

        // Add welcome message
        chatMessages.add(new ChatMessage("Hello there, I hope you're feeling well today. Pick any of the questions below and educate yourself on what to do in case you need first aid is required.", false, false));
        chatAdapter.notifyDataSetChanged();

        loadIntents();

        ImageView backButton = findViewById(R.id.toolbar_firstaid_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new android.content.Intent(SecondBotActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    private void loadIntents() {
        String json = null;
        try {
            InputStream is = getAssets().open("intents.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            chatMessages.add(new ChatMessage("Failed to load intents: " + ex.getMessage(), false, false));
            chatAdapter.notifyDataSetChanged();
            return;
        }

        Gson gson = new Gson();
        Type mapType = new TypeToken<Map<String, List<IntentClass>>>() {}.getType();
        Map<String, List<IntentClass>> intentMap = gson.fromJson(json, mapType);
        List<IntentClass> intents = intentMap.get("intents");
        displayRandomQuestions(intents);
    }

    private void displayRandomQuestions(List<IntentClass> intents) {
        List<String> questions = new ArrayList<>();
        Random random = new Random();

        for (IntentClass intent : intents) {
            List<String> patterns = intent.getPatterns();
            String randomQuestion = patterns.get(random.nextInt(patterns.size()));
            questions.add(randomQuestion);
        }

        for (String question : questions) {
            chatMessages.add(new ChatMessage(question, true, false));
        }

        chatAdapter.notifyDataSetChanged();
    }

    public void fetchResponse(String question, int position) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("message", question);

                    RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
                    Request request = new Request.Builder()
                            .url("https://chat-fsz46m7daa-bq.a.run.app/chat")
                            .post(body)
                            .build();

                    try (Response response = client.newCall(request).execute()) {
                        final String responseData = response.body().string();
                        JsonObject jsonResponse = JsonParser.parseString(responseData).getAsJsonObject();
                        final String botResponse = jsonResponse.get("response").getAsString();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                chatMessages.add(position + 1, new ChatMessage(botResponse, false, true));
                                chatAdapter.notifyItemInserted(position + 1);
                                chatRecyclerView.scrollToPosition(position + 1); // Scroll to the inserted response
                                answeredQuestions.add(question);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SecondBotActivity.this, "Failed to get response from bot", Toast.LENGTH_SHORT).show();
                            answeredQuestions.remove(question);
                        }
                    });
                }
            }
        }).start();
    }

    private static class Intent {
        private List<String> patterns;

        public List<String> getPatterns() {
            return patterns;
        }
    }
}