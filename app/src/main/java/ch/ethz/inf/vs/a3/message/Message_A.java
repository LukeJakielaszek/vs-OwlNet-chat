package ch.ethz.inf.vs.a3.message;

import org.json.JSONException;
import org.json.JSONObject;

import ch.ethz.inf.vs.a3.clock.Clock;
import edu.temple.vs_owlnet_chat.VectorClock;

public class Message_A {
    private Clock timestamp;
    private String username;
    private String uuid;
    private String type;
    private String content;

    public Message_A(JSONObject message) throws JSONException {
        JSONObject header = message.getJSONObject("header");
        JSONObject body = message.getJSONObject("body");

        this.content = body.getString("content");

        VectorClock timestamp = new VectorClock();
        timestamp.setClockFromString(header.getString("timestamp"));
        this.timestamp = timestamp;

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
