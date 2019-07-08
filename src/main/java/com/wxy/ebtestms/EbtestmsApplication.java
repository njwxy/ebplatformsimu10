package com.wxy.ebtestms;

import com.wxy.ebtestms.message.EbMessageTest;
import com.wxy.ebtestms.message.EbSerial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableConfigurationProperties

public class EbtestmsApplication {

    @Autowired
    EbSerial ebSerial;

    @Autowired
    EbMessageTest ebMessageTest;

    public static void main(String[] args) {
        SpringApplication.run(EbtestmsApplication.class, args);
    }

    @PostConstruct
    public void startSerial(){
        ebSerial.startSerial();
        ebMessageTest.EbMessageTestSetQueue(ebSerial.blockingQueue);
        Thread ebthread = new Thread(ebMessageTest);
        ebthread.start();
    }

}
