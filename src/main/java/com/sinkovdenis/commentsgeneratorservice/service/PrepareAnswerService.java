package com.sinkovdenis.commentsgeneratorservice.service;

import com.sinkovdenis.commentsgeneratorservice.queue.MessagePublisher;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PrepareAnswerService {

    private MessagePublisher redisMessagePublisher;
    @Value("${commentator.name}")
    String commentatorName;

    @Autowired
    public PrepareAnswerService(MessagePublisher redisMessagePublisher) {
        this.redisMessagePublisher = redisMessagePublisher;
    }

    public void createAnswer(String data) {

        JSONObject request = prepareRequestData(data);
        JSONObject response = new JSONObject();

        response.put("author", commentatorName);
        response.put("post", request.get("id"));
        if(!request.isNull("filename")) {
            response.put("text", "So nice picture");
            redisMessagePublisher.publish(response);
        } else if(request.get("text") != null) {
            response.put("text", "It is very curious");
            redisMessagePublisher.publish(response);
        }
    }

    public JSONObject prepareRequestData(String data) {
        data = data.replace("\\", "");
        return new JSONObject(data.substring(1, data.length()-1));
    }
}
