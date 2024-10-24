package com.example.urgentcare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.urgentcare.adapter.ChatRVAdapter;
import com.example.urgentcare.helper.ChatsModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FirstAidActivity extends AppCompatActivity {

    private EditText editTextMessage;
    private FloatingActionButton sendMsg;
    private final OkHttpClient client = new OkHttpClient();

    private RecyclerView chatsRV;
    private ArrayList<ChatsModel> chatsModelArrayList;
    private ChatRVAdapter chatRVAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_aid);

        editTextMessage = findViewById(R.id.idEdtMessage);
        sendMsg = findViewById(R.id.idFABSend);
        chatsRV = findViewById(R.id.idRVChats);
        chatsModelArrayList = new ArrayList<>();
        chatRVAdapter = new ChatRVAdapter(chatsModelArrayList, this);
        chatsRV.setAdapter(chatRVAdapter);
        chatsRV.setLayoutManager(new LinearLayoutManager(this));

        editTextMessage.requestFocus();

        chatsModelArrayList.add(new ChatsModel("Hello! I'm your FirstAidBot. How can I help you today?", "bot"));
        chatRVAdapter.notifyDataSetChanged();

        // Set layout animation
//        Context context = chatsRV.getContext();
//        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_slide_right);
//        chatsRV.setLayoutAnimation(controller);

        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = editTextMessage.getText().toString().trim();
                if (!message.isEmpty()) {
                    sendMessageToBot(message);
                    chatsModelArrayList.add(new ChatsModel(message, "user"));
                    chatRVAdapter.notifyDataSetChanged();
                    editTextMessage.setText(""); // Clear input box
                    chatsRV.scrollToPosition(chatsModelArrayList.size()- 1);

                } else {
                    Toast.makeText(FirstAidActivity.this, "Please enter a message", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageView backButton = findViewById(R.id.toolbar_firstaid_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FirstAidActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    private void sendMessageToBot(final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("message", message);

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
                                // Add bot response to RecyclerView
                                chatsModelArrayList.add(new ChatsModel(botResponse, "bot"));
                                chatRVAdapter.notifyDataSetChanged();
                                chatsRV.scrollToPosition(chatsModelArrayList.size() - 1); // Scroll to bottom
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(FirstAidActivity.this, "Failed to get response from bot", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

}