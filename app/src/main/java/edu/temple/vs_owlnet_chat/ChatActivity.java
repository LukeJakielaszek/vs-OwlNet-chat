package edu.temple.vs_owlnet_chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.net.DatagramSocket;

public class ChatActivity extends AppCompatActivity {
    Button deregister_button;
    DatagramSocket datagramSocket;

    Handler toast_handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            String message = (String) msg.obj;

            if(msg.what == 1){
                message = "Deregistration Successful";
            }

            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

            return false;
        }
    });

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

                        String message = SocketManager.transmitMessage("deregister");

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

                        datagramSocket.close();
                    }
                }.start();
            }
        });

    }
}
