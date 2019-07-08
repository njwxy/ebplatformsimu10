package com.wxy.ebtestms.message;

import javolution.io.Struct;

import java.nio.ByteOrder;
import java.util.Date;
import java.util.List;

public class MsgRegAck extends Struct{
    public final Struct.Unsigned8 ack = new Struct.Unsigned8();
    public final Struct.Unsigned8 hh = new Struct.Unsigned8();
    public final Struct.Unsigned8 mm = new Struct.Unsigned8();
    public final Struct.Unsigned8 ss = new Struct.Unsigned8();
    public final Struct.Unsigned8 freq = new Struct.Unsigned8();
    public final Struct.Unsigned8 nodeNum = new Struct.Unsigned8();

    public final Unsigned16[] nodeAddr = array(new Unsigned16[101]); //max 100+ random AAAA

    public short getPacketLength()
    {
        return (short) (8 + nodeNum.get()*2);
    }

    /*
    *   even == 0 -> 低16bit  even == 1 高16bit
    * */
    public  byte [] getAckData( List<Long> listdata,int even){
        // 获取回应数据包
        Date date = new Date();
        int hh = date.getHours();
        int mm = date.getMinutes();
        int ss = date.getSeconds();

        this.ack.set((short) 1);
        this.hh.set((short) hh);
        this.mm.set((short) mm);
        this.ss.set((short) ss);
        this.freq.set((short) 1);
        int itemnum = listdata.size();
        this.nodeNum.set((short) itemnum);
        int pos = 0;
        for (Long d:listdata
        ) {
            short value;
            if(even == 0)
              value = (short)( d & 0xffff);
            else
              value = (short)((d>>16)&0xffff);
            nodeAddr[pos++].set(value);
        }
       nodeAddr[pos].set(even);
       byte[] appdata = new byte[this.size()];
       getByteBuffer().get(appdata);
       return appdata;
    }

    @Override
    public boolean isPacked() {
        return true;
    }

    @Override
    public ByteOrder byteOrder() {
        return ByteOrder.LITTLE_ENDIAN;
    }




}
