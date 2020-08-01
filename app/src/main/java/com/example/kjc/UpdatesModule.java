package com.example.kjc;

public class UpdatesModule
{
    public String body,heading;

    public UpdatesModule() {
    }

    public UpdatesModule(String body, String heading) {
        this.body = body;
        this.heading = heading;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }
}
