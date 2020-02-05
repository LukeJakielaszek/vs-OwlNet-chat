package edu.temple.vs_owlnet_chat;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
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

    public static synchronized String createJSONMessage(String type, String userName, String uuid){
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

        return register_message.toString();
    }

    public static synchronized boolean sendMessage(String type){
        String message = createJSONMessage(type, SocketManager.userName, SocketManager.uuid.toString());

        DatagramPacket out_packet = new DatagramPacket(message.getBytes(), message.length(), SocketManager.address, SocketManager.port);

        try {
            SocketManager.socket.send(out_packet);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("Chat", "Register packet sent");

        return true;
    }

    public static synchronized String receiveMessage(){
        byte in_packet_buf[] = new byte[256];
        DatagramPacket in_packet = new DatagramPacket(in_packet_buf, in_packet_buf.length);

        // receive() method
        try {
            SocketManager.socket.receive(in_packet);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String response = new String(in_packet.getData(), 0, in_packet.getLength());

        Log.d("Chat", "RESPONSE [" + response + "]");

        return response;
    }
}
