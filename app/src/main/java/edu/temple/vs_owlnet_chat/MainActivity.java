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

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    //button to register with server
    Button join_button;

    // button to set ip / port
    Button settings_button;

    // server socket
    DatagramSocket datagramSocket;

    // determines if first launch of app
    boolean isFirstStart = true;

    // displays registration success / fail to user
    Handler toast_handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            String message = (String) msg.obj;

            if(msg.what == 1){
                // if successful, notify user and launch chat activity
                message = "Registration Successful";
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                MainActivity.this.startActivity(intent);
            }

            // display toast (error by default)
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // set title to vs owlnet chat
        setTitle(R.string.main_label);

        // find the join button
        this.join_button = findViewById(R.id.JoinButton);

        // find the settings button
        this.settings_button = findViewById(R.id.SettingsButton);

        // if its first start, set the port to default
        if (this.isFirstStart) {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    MainActivity.this.isFirstStart = false;
                    try {
                        SocketManager.setPort(4446);
                        SocketManager.setAddress(InetAddress.getByName("169.254.108.101"));
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                }

            }.start();
        }

        // set click listener to attempt to register with server
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
                            datagramSocket.setSoTimeout(2000);

                            // create socket with port and address
                            SocketManager.setSocket(datagramSocket);

                            SocketManager.setUserName(((EditText)findViewById(R.id.UserName)).getText().toString());

                            // generate a UUID for this instance
                            SocketManager.setUuid(UUID.randomUUID());

                            // attempt to register
                            String message = SocketManager.transmitMessage("register");

                            // obtain message to UI thread
                            Message toast_message = toast_handler.obtainMessage();

                            // set what to fail by default
                            toast_message.what = -1;

                            if(message == null){
                                // indicate JSON parsing error in message
                                toast_message.what = -2;
                                toast_message.obj = "ERROR: Unable to parse JSON";

                                // send message to UI thread for toast
                                toast_handler.sendMessage(toast_message);
                            }else{
                                if(message.equals("success")){
                                    // indicate successful message
                                    toast_message.what = 1;
                                }

                                // store message (success or an error)
                                toast_message.obj = message;

                                // send the message to UI thread for toast
                                toast_handler.sendMessage(toast_message);
                            }
                        } catch (SocketException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });

        // launch the settings activity
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
