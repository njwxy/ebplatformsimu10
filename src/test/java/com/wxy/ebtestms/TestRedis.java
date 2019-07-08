package com.wxy.ebtestms;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wxy.ebtestms.message.EbConfig;
import com.wxy.ebtestms.message.FrameHead;
import com.wxy.ebtestms.message.MsgRegAck;
import com.wxy.ebtestms.utility.EntityGson;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.wxy.ebtestms.message.FrameHead.getSendPacket;
import static com.wxy.ebtestms.utility.ConstData.APPCMD_REG_ACK;
import static com.wxy.ebtestms.utility.PrjFuncs.Hex2Str;

//@RunWith(SpringRunner.class)
//@SpringBootTest
@Slf4j

public class TestRedis {
    @Value("${syspara.gwcfg}")
    private String gwcfg;



    @Autowired
    EbConfig ebConfig;


    @Test
    public void testPacket(){
        MsgRegAck msgRegAck = new MsgRegAck();
        List<Long> listAddr = new ArrayList<>();
        for(long i=0;i<20;i++)
        {
            listAddr.add(i);
        }
        byte [] appret = msgRegAck.getAckData(listAddr,1);
        String appstr =  Hex2Str(appret,msgRegAck.getPacketLength());
        log.info(appstr);
        byte [] packetsend = FrameHead.getSendPacket(APPCMD_REG_ACK,msgRegAck.getPacketLength(),appret,11000005);
        String packetStr = Hex2Str(packetsend,packetsend.length);
        log.info(packetStr);
    }


    @Test
    public  void testGson() throws IOException {
        // log.info("teststr"+gwcfg);
        List<Long> listAddr = new ArrayList<Long>();
        for (long i = 0; i < 6; i++) {
            listAddr.add(i);
        }
        listAddr.stream().forEach(d -> System.out.print(" " + d));
        System.out.println();

        List<Long> listnew = listAddr.stream().sorted().collect(Collectors.toList());

        Gson gson = new GsonBuilder().setDateFormat("yy-MM-dd HH:mm:ss").create();
        EntityGson<List<Long>> entityGson = new EntityGson<List<Long>>("cfgAddr", listnew);
        String retstr = entityGson.getGsonString();
        log.info(retstr);
        String type = EntityGson.getType(retstr);
        log.info("type"+type);
        List<Long> addrList = EntityGson.getData(type,retstr);


        if(addrList!=null){
            addrList.stream().forEach(d->System.out.print(" "+d));
            System.out.println();
        }
        else{
            System.out.println("error read list");
        }

    }

    @Test
    public void TestFileCfg() throws IOException {
        testGson();
        String configStr = ebConfig.readCfg();
        log.info(configStr);
        String type = EntityGson.getType(configStr);
        List<Long> addrList = EntityGson.getData(type,configStr);
        if(addrList!=null){
            addrList.stream().forEach(d->System.out.print(" "+d));
            System.out.println();
        }
        else{
            System.out.println("error read list");
        }


       // getSendPacket(APPCMD_REG_ACK,)
        //ebConfig.writeCfg("helloconfig");
    }




}
