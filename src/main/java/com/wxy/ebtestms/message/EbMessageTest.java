package com.wxy.ebtestms.message;

import com.wxy.ebtestms.utility.EntityGson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static com.wxy.ebtestms.utility.ConstData.*;
import static com.wxy.ebtestms.utility.PrjFuncs.Hex2Str;
import static java.lang.Thread.sleep;

@Component
@Slf4j
public class EbMessageTest implements Runnable {



    @Autowired
    EbConfig ebConfig;

    @Autowired
    EbSerial ebSerial;

    BlockingQueue<byte[]> blockingQueue;

    public void EbMessageTestSetQueue(BlockingQueue<byte[]> blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    @Override
    public void run() {
        int waitAckTime = 3000;
        log.info("waitAck time is"+waitAckTime);

        while (true) {
            byte[] msgret = new byte[0];
            try {
                msgret = blockingQueue.poll(waitAckTime, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (msgret != null) {

                String showData = Hex2Str(msgret,msgret.length);
                log.info("recv"+showData);

                FrameHead frameHead = new FrameHead();
                frameHead.getByteBuffer().put(msgret, 0, frameHead.size());
                switch (frameHead.cmd.get()) {
                     case APPCMD_REG_REQ:{
                         log.info("recv APPCMD_REG_REQ from "+frameHead.devAddr.get());
                         // 读取配置文件
                         String cfgData;
                         try {
                             cfgData = ebConfig.readCfg();
                             /* 从配置文件中读取节点地址*/
                             String type = EntityGson.getType(cfgData);
                             List<Long> addrList = EntityGson.getData(type,cfgData);

                             /* 回应APP包 */
                             MsgRegAck msgRegAck = new MsgRegAck();
                             byte [] appdata = msgRegAck.getAckData(addrList,0);
                             long devAddr = frameHead.devAddr.get();
                             /* 回应数据包*/
                             byte[] packetdata = FrameHead.getSendPacket(APPCMD_REG_ACK,msgRegAck.getPacketLength(),
                                     appdata,devAddr);

                             ebSerial.sendData(packetdata);
                             sleep(100);

                             msgRegAck = new MsgRegAck();
                             appdata = msgRegAck.getAckData(addrList,1);
                             packetdata = FrameHead.getSendPacket(APPCMD_REG_ACK,msgRegAck.getPacketLength(),
                                     appdata,devAddr);
                             ebSerial.sendData(packetdata);
                         } catch (IOException e) {
                             e.printStackTrace();
                         } catch (InterruptedException e) {
                             e.printStackTrace();
                         }


                         //  byte[] appData = msgRegAck.getAckData()

                         // 产生回应包

                         break;
                     }

                    default:
                        break;
                }
            }
        }
    }
}
