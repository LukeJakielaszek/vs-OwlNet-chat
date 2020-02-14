package ch.ethz.inf.vs.a3.message;

import ch.ethz.inf.vs.a3.clock.Clock;

class Message {
    private Clock timestamp;
    private String username;
    private String uuid;
    private String type;
    private String content;

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
