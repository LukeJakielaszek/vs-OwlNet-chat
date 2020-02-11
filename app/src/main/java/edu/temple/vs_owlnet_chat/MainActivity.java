package edu.temple.vs_owlnet_chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

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

    Handler toast_handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            String message = (String) msg.obj;

            if(msg.what == 1){
                message = "Registration Successful";
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                MainActivity.this.startActivity(intent);
            }
            
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

            return false;
        }
    });

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
                Log.d("TEST", "Button Clicked");
                new Thread(){
                    @Override
                    public void run() {
                        Log.d("TEST", "Thread started");
                        try {
                            // initialize our datagram socket connection
                            datagramSocket = new DatagramSocket();

                            // set the socket timeout to 2
                            datagramSocket.setSoTimeout(2);

                            // create socket with port and address
                            SocketManager.setSocket(datagramSocket);
                            SocketManager.setPort(4446);
                            SocketManager.setAddress(InetAddress.getByName("169.254.108.101"));

                            SocketManager.setUserName(((EditText)findViewById(R.id.UserName)).getText().toString());

                            // generate a UUID for this instance
                            SocketManager.setUuid(UUID.randomUUID());

                            String message = SocketManager.transmitMessage("register");

                            Message toast_message = toast_handler.obtainMessage();
                            toast_message.what = -1;
                            if(message == null){
                                toast_message.what = -2;
                                toast_message.obj = "ERROR: Unable to parse JSON";
                                toast_handler.sendMessage(toast_message);
                            }else{
                                if(message.equals("success")){
                                    toast_message.what = 1;
                                }

                                toast_message.obj = message;

                                toast_handler.sendMessage(toast_message);
                            }

                            /*
                            // create a registration message
                            DatagramPacket register_message = SocketManager.createOutMessage("register",
                                    SocketManager.getUserName(), SocketManager.getUuid().toString());

                            byte in_packet_buf[] = new byte[256];
                            DatagramPacket in_packet = new DatagramPacket(in_packet_buf, in_packet_buf.length);

                            // Attempt to register
                            int count = 0;
                            boolean success = false;
                            String response = null;

                            while(count < 5){
                                count++;

                                SocketManager.sendMessage(register_message);

                                response = SocketManager.receiveMessage(in_packet);

                                if(response != null){
                                    success = true;
                                    break;
                                }
                            }

                            Message toast_message = toast_handler.obtainMessage();
                            toast_message.what = -1;

                            if(success) {
                                try {
                                    JSONObject resp = new JSONObject(response);
                                    String type = (String) ((JSONObject)resp.get("header")).get("type");
                                    if(type.equals("error")){
                                        // error message received from server
                                        String content = (String) ((JSONObject) resp.get("body")).get("content");
                                        String error = SocketManager.getStringError(Integer.parseInt(content));

                                        Log.d("Test", "ERROR: " + error);

                                        toast_message.obj = error;
                                        toast_handler.sendMessage(toast_message);
                                    }else{
                                        // successful ack received
                                        Log.d("TEST", "Ack successfully received");

                                        toast_message.obj = "Registration Successful";
                                        toast_message.what = 1;
                                        toast_handler.sendMessage(toast_message);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }else{

                                toast_message.obj = "ERROR: Socket timed out too many times";
                                toast_handler.sendMessage(toast_message);
                                Log.d("TEST", "ERROR: Socket timed out too many times");
                            }

                             */
                        } catch (SocketException e) {
                            e.printStackTrace();
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("TEST", "Destroy");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("TEST", "Stop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("TEST", "Pause");
    }
}
