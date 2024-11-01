package com.example.listener;

import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RabbitListener(queues="mail")
public class MailQueueListener {

    @Resource
    JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    String username;

    @RabbitHandler
    public void sendMailMessage(Map<String,Object> data) {
        String email=data.get("email").toString();
        Integer code=(Integer)data.get("code");
        String type=(String)data.get("type");
        SimpleMailMessage message=switch(type){
            case "register" ->
                    createMailMessage("welcome to register",
                            "your code is "+code
                            ,email);
            case "reset" ->
                    createMailMessage("welcome to reset",
                            "your code is "+code
                            ,email);
            default -> null;
        };
        if(message==null){
            return;
        }
        mailSender.send(message);
    }

    private SimpleMailMessage createMailMessage(String title,
                                                String content,String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(title);
        message.setText(content);
        message.setTo(email);
        message.setFrom(username);
        return message;
    }
}
