package edu.temple.vs_owlnet_chat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    Button join_button;
    Button settings_button;
    DatagramSocket datagramSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.main_label);

        this.join_button = findViewById(R.id.JoinButton);

        this.settings_button = findViewById(R.id.SettingsButton);

        this.join_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Chat", "Button Clicked");
                new Thread(){
                    @Override
                    public void run() {
                        Log.d("Chat", "Thread started");
                        try {
                            datagramSocket = new DatagramSocket();

                            // create socket with port and address
                            SocketManager.setSocket(datagramSocket);
                            SocketManager.setPort(4446);
                            SocketManager.setAddress(InetAddress.getByName("169.254.108.101"));

                            // generate a UUID for this instance
                            SocketManager.setUuid(UUID.randomUUID());

                            // send the message
                            SocketManager.sendMessage("register");

                            // receive the response
                            String response = SocketManager.receiveMessage();
                        } catch (SocketException e) {
                            e.printStackTrace();
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        }

                        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                        MainActivity.this.startActivity(intent);
                    }
                }.start();
            }
        });

        this.settings_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

    }
}
