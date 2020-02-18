package edu.temple.vs_owlnet_chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.PriorityQueue;

import ch.ethz.inf.vs.a3.message.MessageComparator;
import ch.ethz.inf.vs.a3.message.Message_A;

public class ChatActivity extends AppCompatActivity {
    Button deregister_button;
    Button retrieve_chat_button;

    // server socket
    DatagramSocket datagramSocket;

    // displayable location for server chat
    TextView chatText;

    // handler to display ordered server messages
    Handler response_handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            try {
                // get the PQ for messages
                PriorityQueue<Message_A> priorityQueue = (PriorityQueue<Message_A>) msg.obj;

                // construct a string representation of ordered messages
                StringBuilder sb = new StringBuilder();
                while(!priorityQueue.isEmpty()){
                    Message_A curMessage = priorityQueue.poll();

                    sb.append(curMessage.getTimestamp() + " : " + curMessage.getUsername() + ": [" + curMessage.getContent() + "]\n");
                }

                // display the ordered messages
                chatText.setText(sb.toString());
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "ERROR: Failed to construct server response", Toast.LENGTH_LONG);
            }

            return false;
        }
    });

    // toast to display success of deregistration / chat initiation
    Handler toast_handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            // get the message to send (if error occurred, it is stored here)
            String message = (String) msg.obj;

            if(msg.what == 1){
                // deregestration success
                message = "Deregistration Successful";
            }else if(msg.what == 2){
                // chat inititation success
                message = "Chat Initiated";
            }

            // display the message
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        // set our title to chat
        setTitle(R.string.chat_label);

        // get the displayable textview
        chatText = findViewById(R.id.ChatView);

        // obtain the server socket
        datagramSocket = SocketManager.getSocket();

        // obtain deregister button
        deregister_button = findViewById(R.id.DeregisterButton);

        // get the chat button
        retrieve_chat_button = findViewById(R.id.start_chat_button);

        // set a click listener for deregestration
        deregister_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();

                        // transmit a deregister message
                        String message = SocketManager.transmitMessage("deregister");

                        // obtain a message to send to UI thread
                        Message toast_message = toast_handler.obtainMessage();

                        // default what to indicate failure
                        toast_message.what = -1;
                        if(message == null){
                            // set what to indicate JSON parsing fail
                            toast_message.what = -2;
                            toast_message.obj = "ERROR: Unable to parse JSON";

                            // send message to UI thread
                            toast_handler.sendMessage(toast_message);
                        }else{
                            if(message.equals("success")){
                                // set what to indicate success
                                toast_message.what = 1;

                                // close the socket on deregister success
                                datagramSocket.close();
                            }

                            // construct the message object
                            toast_message.obj = message;

                            // send the message to UI
                            toast_handler.sendMessage(toast_message);
                        }
                    }
                }.start();
            }
        });

        // set a click listener for chat button
        retrieve_chat_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();

                        // create a retrieve chat log message
                        DatagramPacket retrieve_log_message = SocketManager.createOutMessage("retrieve_chat_log",
                                SocketManager.getUserName(), SocketManager.getUuid().toString());

                        // create an input buffer
                        byte in_packet_buf[] = new byte[256];
                        DatagramPacket in_packet = new DatagramPacket(in_packet_buf, in_packet_buf.length);

                        // send the retreive chat message
                        SocketManager.sendMessage(retrieve_log_message);

                        // initialize a PQ with comparator
                        PriorityQueue<Message_A> priorityQueue = new PriorityQueue<>(10, new MessageComparator());

                        // list to hold all responses (DEBUGGING)
                        // ArrayList<Message_A> response_list = new ArrayList<>();
                        String response;
                        do{
                            // get a response from the server
                            response = SocketManager.receiveMessage(in_packet);
                            if(response != null) {
                                // if response successfully received
                                try {
                                    // construct a message object from the response
                                    Message_A message = new Message_A(new JSONObject(response));

                                    // add the response to the PQ
                                    priorityQueue.add(message);

                                    // add the response to the response list (DEBUGGING)
                                    //response_list.add(message);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }while(response != null);

                        // send the sorted messages to UI thread
                        Message message = response_handler.obtainMessage();
                        message.obj = priorityQueue;
                        response_handler.sendMessage(message);
                    }
                }.start();
            }
        });

    }
}
