package com.sinkovdenis.commentsgeneratorservice.queue;

import org.json.JSONObject;

public interface MessagePublisher {

    void publish(final JSONObject message);
}
