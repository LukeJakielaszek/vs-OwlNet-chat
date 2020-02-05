package edu.temple.vs_owlnet_chat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
                            DatagramSocket sock = new DatagramSocket();

                            // set address and port
                            InetAddress address = InetAddress.getByName("169.254.108.101");
                            int port = 4446;

                            JSONObject header = new JSONObject();
                            header.put("username", "Luke");


                            header.put("uuid", "d7f0de5b-042a-4632-b5f6-a80e08b46d85");
                            header.put("timestamp", "{}");
                            header.put("type", "register");

                            JSONObject register_message = new JSONObject();
                            register_message.put("header", header);
                            register_message.put("body", new JSONObject());

                            DatagramPacket out_packet = new DatagramPacket(register_message.toString().getBytes(), register_message.toString().length(), address, port);

                            // send() method
                            sock.send(out_packet);
                            Log.d("Chat", "Register packet sent");

                            byte in_packet_buf[] = new byte[256];
                            DatagramPacket in_packet = new DatagramPacket(in_packet_buf, in_packet_buf.length);

                            // receive() method
                            sock.receive(in_packet);
                            String response = new String(in_packet.getData(), 0, in_packet.getLength());
                            Log.d("Chat", "RESPONSE [" + response + "]");
                        } catch (SocketException e) {
                            e.printStackTrace();
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();

                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                MainActivity.this.startActivity(intent);
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
