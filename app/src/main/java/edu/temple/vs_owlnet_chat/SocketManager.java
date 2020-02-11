package edu.temple.vs_owlnet_chat;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.UUID;

public class SocketManager {
    private static DatagramSocket socket;
    private static int port;
    private static InetAddress address;
    private static UUID uuid;
    private static String userName;

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

    public static synchronized DatagramPacket createOutMessage(String type, String userName, String uuid){
        JSONObject header = new JSONObject();
        JSONObject register_message = new JSONObject();
        try {
            header.put("username", userName);
            header.put("uuid", uuid);
            header.put("timestamp", "{}");
            header.put("type", type);

            register_message.put("header", header);
            register_message.put("body", new JSONObject());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String reg_message = register_message.toString();
        DatagramPacket out_packet = new DatagramPacket(reg_message.getBytes(), reg_message.length(), SocketManager.address, SocketManager.port);

        return out_packet;
    }

    public static synchronized boolean sendMessage(DatagramPacket message){
        Log.d("TEST", "OUTPACKET: " + message);

        try {
            SocketManager.socket.send(message);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        Log.d("TEST", "Message packet sent");

        return true;
    }

    /*
    public static synchronized String register(){
        int count = 0;
        boolean success = false;
        String response = null;

        while(count < 5){
            count++;

            sendMessage("register");

            response = receiveMessage();

            if(response != null){
                success = true;
                break;
            }
        }

        if(success) {
            try {
                JSONObject resp = new JSONObject(response);

                String error = getStringError((int) ((JSONObject) resp.get("body")).get("content"));

                return error;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            return null;
        }

        return "-1";
    }*/

    public static String getStringError(int errorCode) {
        String error = null;
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
        return error;
    }

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

        String response = new String(in_packet.getData(), 0, in_packet.getLength());

        Log.d("TEST", "RESPONSE [" + response + "]");

        return response;
    }
}
