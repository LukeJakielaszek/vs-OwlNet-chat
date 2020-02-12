package edu.temple.vs_owlnet_chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity {
    Button submitButton;
    EditText serverAddress;
    EditText serverPort;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if(msg.what == -1){
                Log.d("TEST", "ERROR: Failed to set server address / port");
                Toast.makeText(getApplicationContext(), "ERROR: Failed to set server address / port", Toast.LENGTH_SHORT).show();
            }else{
                Log.d("TEST", msg.obj.toString());
                Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
            }

            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(R.string.settings_label);

        submitButton = findViewById(R.id.submitButton);
        serverAddress = findViewById(R.id.ServerAddress);
        serverPort = findViewById(R.id.ServerPort);

        new Thread(){
            @Override
            public void run() {
                super.run();
                SettingsActivity.this.serverPort.setText(Integer.toString(SocketManager.getPort()));
                SettingsActivity.this.serverAddress.setText(SocketManager.getAddress().getHostName());
            }
        }.start();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        Log.d("TEST", "Submitting new server info");

                        Message message = handler.obtainMessage();
                        message.what = 1;
                        try {
                            String new_address = serverAddress.getText().toString();
                            String new_port = serverPort.getText().toString();

                            SocketManager.setAddress(InetAddress.getByName(new_address));
                            SocketManager.setPort(Integer.parseInt(new_port));
                            message.obj = "Server Address [" + new_address +
                                    "] Port [" + new_port + "]";
                        } catch (Exception e){
                            e.printStackTrace();
                            Log.d("TEST", "ERROR: Failed to parse new address/port");
                            message.what = -1;
                        }

                        handler.sendMessage(message);
                    }
                }.start();
            }
        });
    }
}
