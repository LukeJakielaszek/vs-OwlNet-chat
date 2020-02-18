package ch.ethz.inf.vs.a3.message;

import org.json.JSONException;
import org.json.JSONObject;

import ch.ethz.inf.vs.a3.clock.Clock;
import edu.temple.vs_owlnet_chat.VectorClock;

public class Message_A {
    //timestamp of message
    private Clock timestamp;

    // all other items converted to string
    private String username;
    private String uuid;
    private String type;
    private String content;

    // initialize all fields from JSON message, storing timestamp as a vectorClock
    public Message_A(JSONObject message) throws JSONException {

        // retrieve containers within message
        JSONObject header = message.getJSONObject("header");
        JSONObject body = message.getJSONObject("body");

        // set the content of the message
        this.content = body.getString("content");

        // construct the clock
        VectorClock timestamp = new VectorClock();
        timestamp.setClockFromString(header.getString("timestamp"));
        this.timestamp = timestamp;

        // set meta data for message
        this.username = header.getString("username");
        this.type = header.getString("type");
        this.uuid = header.getString("uuid");
    }

    public Clock getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Clock timestamp) {
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
