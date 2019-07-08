package com.wxy.ebtestms.message;

import javolution.io.Struct;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static com.wxy.ebtestms.utility.PrjFuncs.getSum;

public class FrameHead extends Struct {
    public final Unsigned8 start = new Unsigned8();
    public final Unsigned8 ver = new Unsigned8();
    public final Unsigned8 len = new Unsigned8();
    public final Unsigned8 encrypt = new Unsigned8();
    public final Unsigned32 devAddr = new Unsigned32();
    public final Unsigned8 cmd = new Unsigned8();
    public final Unsigned8 dlen = new Unsigned8();


    public FrameHead(short start, short cmd, short dlen,long devAddr) {
        this.start.set(start);
        this.ver.set((short) 0x01);
        short plen = (short) (dlen + 12);
        this.len.set(plen);
        this.encrypt.set((short) 0);
        this.devAddr.set(devAddr);
        this.cmd.set(cmd);
        this.dlen.set(dlen);
    }


    public static byte[] getSendPacket(short cmd,short datalen,byte[] appData,long devAddr){
        FrameHead frameHead = new FrameHead((short) 0x68,cmd,datalen,devAddr);
        byte[] packet = new byte[frameHead.size()+datalen+2]; // csum+16h
        ByteBuffer byteBuffer= frameHead.getByteBuffer();
        byteBuffer.get(packet,0,frameHead.size());
        if(datalen>0) {
            System.arraycopy(appData, 0, packet, frameHead.size(), datalen);
        }
        int pos = frameHead.size()+datalen;
        packet[pos++] = getSum(packet,0,pos);
        packet[pos] = 0x16;
        return packet;
    }

    @Override
    public boolean isPacked() {
        return true;
    }

    @Override
    public ByteOrder byteOrder() {
        return ByteOrder.LITTLE_ENDIAN;
    }
    //little endian

    public FrameHead() {

    }
}
