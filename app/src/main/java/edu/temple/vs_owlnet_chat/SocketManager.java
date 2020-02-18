package edu.temple.vs_owlnet_chat;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.UUID;

// class to manage all socket interaction
public class SocketManager {
    // the client socket
    private static DatagramSocket socket;

    // port number
    private static int port;

    // ip address
    private static InetAddress address;

    // current user uuid
    private static UUID uuid;

    // current connected username
    private static String userName;

    // ------------------------ setters & getters
    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        SocketManager.userName = userName;
    }

    public static UUID getUuid() {
        return uuid;
    }

    public static void setUuid(UUID uuid) {
        SocketManager.uuid = uuid;
    }

    public static synchronized void setPort(int port){
        SocketManager.port = port;
    }

    public static synchronized int getPort(){
        return SocketManager.port;
    }

    public static synchronized void setAddress(InetAddress address){
        SocketManager.address = address;
    }

    public static synchronized InetAddress getAddress(){
        return SocketManager.address;
    }

    public static synchronized void setSocket (DatagramSocket datagramSocket){
        SocketManager.socket = datagramSocket;
    }

    public static synchronized DatagramSocket getSocket(){
        return SocketManager.socket;
    }

    // ----------------------------- end of setters & getters

    // construct a datagram packet to send to server
    public static synchronized DatagramPacket createOutMessage(String type, String userName, String uuid){
        // create the nested JSON message
        JSONObject header = new JSONObject();
        JSONObject register_message = new JSONObject();
        try {
            // construct message header
            header.put("username", userName);
            header.put("uuid", uuid);
            header.put("timestamp", "{}");
            header.put("type", type);
            register_message.put("header", header);

            // construct message body
            register_message.put("body", new JSONObject());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Encode JSON message in datagram packet
        String reg_message = register_message.toString();
        DatagramPacket out_packet = new DatagramPacket(reg_message.getBytes(), reg_message.length(), SocketManager.address, SocketManager.port);

        // return the datagram packet
        return out_packet;
    }

    // sends a message to server. Returns true on success, false on failure
    public static synchronized boolean sendMessage(DatagramPacket message){
        Log.d("TEST", "OUTPACKET: " + message.getData().toString());

        // attempt to send message
        try {
            SocketManager.socket.send(message);
        } catch (IOException e) {
            // message failed to send
            e.printStackTrace();
            return false;
        }

        // message successfully sent
        Log.d("TEST", "Message packet sent");
        return true;
    }

    // converts an error code in message to a string
    public static String getStringError(int errorCode) {
        String error;
        switch(errorCode) {
            case 0:
                error = "Registration failed";
                break;
            case 1:
                error = "Deregistration failed due to the UUID";
                break;
            case 2:
                error = "Deregistration failed due to the username";
                break;
            case 3:
                error = "User authentication fail";
                break;
            case 4:
                error = "Message parsing failed";
                break;
            default:
                error = "Cannot decode error code";
                break;
        }

        // return string representation of error code
        return error;
    }

    // attempt to receive message from server and convert it to a string. Returns
    // null on timeout, otherwise the string representation of the message
    public static synchronized String receiveMessage(DatagramPacket in_packet){
        // receive() method
        try {
            SocketManager.socket.receive(in_packet);
        } catch (SocketTimeoutException e){
            e.printStackTrace();
            Log.d("TEST", "Socket timed out");
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }

        // convert response to string
        String response = new String(in_packet.getData(), 0, in_packet.getLength());
        Log.d("TEST", "RESPONSE [" + response + "]");

        // return the response
        return response;
    }

    // attempt to transmit a message to server. Receives ack after sending message. Allows up to 5
    // message timeouts before indicating failure
    public static synchronized String transmitMessage(String message_type){
        // create a message
        DatagramPacket register_message = SocketManager.createOutMessage(message_type,
                SocketManager.getUserName(), SocketManager.getUuid().toString());

        // create an input buffer
        byte in_packet_buf[] = new byte[256];
        DatagramPacket in_packet = new DatagramPacket(in_packet_buf, in_packet_buf.length);

        // Attempt to send message
        int count = 0;
        boolean success = false;
        String response = null;

        // loop 5 times for ack
        while(count < 5){
            count++;

            // send the message
            SocketManager.sendMessage(register_message);

            // receive the message
            response = SocketManager.receiveMessage(in_packet);

            // check if timeout occured
            if(response != null){
                success = true;
                break;
            }
        }

        // construct the text to display in toast
        String toast_message;
        if(success) {
            // if no timeout occured, review message
            try {
                // get the type of message received
                JSONObject resp = new JSONObject(response);
                String type = (String) ((JSONObject)resp.get("header")).get("type");

                // if an error message received, parse it
                if(type.equals("error")){
                    // obtain string representation of error message received from server
                    String content = (String) ((JSONObject) resp.get("body")).get("content");
                    String error = SocketManager.getStringError(Integer.parseInt(content));

                    Log.d("Test", "ERROR: " + error);

                    // set the toast string
                    toast_message = error;
                }else{
                    // successful ack received
                    Log.d("TEST", "Ack successfully received");

                    // indicate a successful message
                    toast_message = "success";
                }
            } catch (JSONException e) {
                e.printStackTrace();

                // indicate that we failed to parse JSON from server
                toast_message = null;
            }
        }else{
            // construct an error message
            toast_message = "ERROR: Socket timed out too many times";
            Log.d("TEST", "ERROR: Socket timed out too many times");
        }

        // return the constructed toast message
        return toast_message;
    }
}
