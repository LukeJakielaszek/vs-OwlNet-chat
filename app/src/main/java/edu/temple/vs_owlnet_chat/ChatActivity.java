package edu.temple.vs_owlnet_chat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.net.DatagramSocket;

public class ChatActivity extends AppCompatActivity {
    Button deregister_button;
    DatagramSocket datagramSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setTitle(R.string.chat_label);

        datagramSocket = SocketManager.getSocket();

        deregister_button = findViewById(R.id.DeregisterButton);

        deregister_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        /*SocketManager.sendMessage("deregister");
                        String response = SocketManager.receiveMessage();
                        SocketManager.getSocket().close();
                         */
                    }
                }.start();
            }
        });

    }
}
