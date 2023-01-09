package com.liveQIQI.controller;

import com.liveQIQI.websocket.client.ClientSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/test")
public class TestController {

    @Autowired
    private ClientSocket clientSocket;


    @GetMapping(value = "/")
    public void test(){
        System.out.println("suqgduvbqbvdfjkqbjqkqd");
        System.out.println(clientSocket);
//        clientSocket.init();
    }

}
