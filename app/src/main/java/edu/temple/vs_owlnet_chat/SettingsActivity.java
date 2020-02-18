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
    // submission button
    Button submitButton;

    // server IP text field
    EditText serverAddress;

    // server port text field
    EditText serverPort;

    // handler for user toast
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if(msg.what == -1){
                // notify user that processing of server address / port failed
                Log.d("TEST", "ERROR: Failed to set server address / port");
                Toast.makeText(getApplicationContext(), "ERROR: Failed to set server address / port", Toast.LENGTH_SHORT).show();
            }else{
                // notify user of successful address/port setting
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
        // update title to settings
        setTitle(R.string.settings_label);

        // obtain user input sections
        submitButton = findViewById(R.id.submitButton);
        serverAddress = findViewById(R.id.ServerAddress);
        serverPort = findViewById(R.id.ServerPort);

        // display default or selected ip and port combination
        new Thread(){
            @Override
            public void run() {
                super.run();
                SettingsActivity.this.serverPort.setText(Integer.toString(SocketManager.getPort()));
                SettingsActivity.this.serverAddress.setText(SocketManager.getAddress().getHostName());
            }
        }.start();

        // create a click listener for the submission button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        Log.d("TEST", "Submitting new server info");

                        // get a message to our UI thread
                        Message message = handler.obtainMessage();

                        // set what to indicate success
                        message.what = 1;
                        try {
                            // get the IP text
                            String new_address = serverAddress.getText().toString();

                            // get the port text
                            String new_port = serverPort.getText().toString();

                            // set the IP for socketmanager
                            SocketManager.setAddress(InetAddress.getByName(new_address));

                            // set the port for socket manager
                            SocketManager.setPort(Integer.parseInt(new_port));

                            // set the message object for toast to user
                            message.obj = "Server Address [" + new_address +
                                    "] Port [" + new_port + "]";
                        } catch (Exception e){
                            e.printStackTrace();
                            Log.d("TEST", "ERROR: Failed to parse new address/port");

                            // set what to indicate failure
                            message.what = -1;
                        }

                        // send the message to UI thread
                        handler.sendMessage(message);
                    }
                }.start();
            }
        });
    }
}
