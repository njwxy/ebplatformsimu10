package com.wxy.ebtestms.message;

import com.fazecast.jSerialComm.SerialPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static com.wxy.ebtestms.utility.PrjFuncs.Hex2Str;
import static com.wxy.ebtestms.utility.PrjFuncs.getSum;
import static java.lang.Thread.sleep;

@Slf4j
@Component
@PropertySource("classpath:application.yml")
//@ConfigurationProperties(prefix = "application.yml")
@EnableAutoConfiguration
public class EbSerial {

    public BlockingQueue<byte[]> blockingQueue = new ArrayBlockingQueue<>(10);

    private SerialPort serialPort;

    public  void sendData(byte[] msg){
        log.info("send packet"+Hex2Str(msg,msg.length));
        serialPort.writeBytes(msg,msg.length);
    }


    @Value("${syspara.sport}")
    public String sPortNum;

    public void clearBlockQueue(){
        byte [] msg=null;
        do{
            msg = blockingQueue.poll();
            if(msg!=null)
                System.out.println(Hex2Str(msg,msg.length));
        }while(msg!=null);
    }

    public class SPortRecvThread implements Runnable{

        public final static short STATE_START = 0;
        public final static short STATE_VERSION = 1;
        public final static short STATE_LEN = 2;
        public final static short STATE_DATA = 3;

        private short state;
        private byte[] msgbuffer = new byte[512];
        private short pos;
        private short leftlen;

        private SerialPort serialPort;

        public SPortRecvThread(SerialPort serialPort) {
            this.serialPort = serialPort;
            this.state = STATE_START;
        }

        private void MsgHandle(byte[] msgByte) throws InterruptedException {
            String showData = Hex2Str(msgByte,msgByte.length);
            System.out.println(showData);
            int length = msgByte.length;
            /*
             *   start+cmd+len
             * */
            if((msgByte[0] == 0x68) && (msgByte[length-1] == 0x16)){
                byte sum = getSum(msgByte,0,length-2);
                if(sum == msgByte[length-2]){
                    blockingQueue.put(msgByte);
                }
                else{
                    log.error("csum error");
                }
            }else{
                log.error("packet error");
            }
        }

        public void processData(byte[] sdata) throws InterruptedException {
            int length = sdata.length;
            int spos = 0;
            while(length>0){
                switch (state){
                    case STATE_START:{
                        if(sdata[spos]== 0x68)
                        {
                            state = STATE_VERSION;
                            pos = 0;
                            msgbuffer[pos++]=0x68;
                        }
                        break;
                    }
                    case STATE_VERSION:{
                        msgbuffer[pos++] = sdata[spos];
                        state = STATE_LEN;
                        break;
                    }

                    case STATE_LEN:{
                        msgbuffer[pos++] = sdata[spos];
                        state = STATE_DATA;
                        leftlen = (short) (sdata[spos]-3);
                        break;
                    }


                    case STATE_DATA:{
                        msgbuffer[pos++] = sdata[spos];
                        leftlen--;
                        if(leftlen<=0) {
                            byte msgByte[] = new byte[pos];
                            System.arraycopy(msgbuffer,0,msgByte,0,pos);
                            MsgHandle(msgByte);
                            state = STATE_START;
                        }
                        break;
                    }

                    default: {
                        state = STATE_START;
                        break;
                    }
                }
                length--;
                spos++;
            }
        }




        @Override
        public void run() {
            while (true) {
                while (serialPort.bytesAvailable() == 0) {
                    try {
                        sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                int numRead =  serialPort.bytesAvailable();

                if(numRead>0) {
                    byte[] readBuffer = new byte[numRead];
                    serialPort.readBytes(readBuffer, readBuffer.length);

                    try {
                        processData(readBuffer);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    log.error("COM closed");
                }
            }
        }
    }

    public EbSerial() {
        log.info("serverport"+sPortNum);
    }

    public void startSerial(){
        //serialPort = SerialPort.getCommPort("COM10");
        log.info("start serial at port"+sPortNum);
        serialPort = SerialPort.getCommPort(sPortNum);
        serialPort.setBaudRate(115200);
        serialPort.setParity(SerialPort.NO_PARITY);
        serialPort.setNumDataBits(8);
        serialPort.openPort();
        SPortRecvThread sPortRecvThread = new SPortRecvThread(serialPort);
        new Thread(sPortRecvThread).start();
    }

    public void testSport() throws InterruptedException {
    }
}