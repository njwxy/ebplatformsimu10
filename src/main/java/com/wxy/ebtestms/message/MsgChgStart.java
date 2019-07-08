package com.wxy.ebtestms.message;

import javolution.io.Struct;

import java.nio.ByteOrder;
import java.util.Date;
import java.util.List;

public class MsgChgStart extends Struct{

    public final Struct.Unsigned32 devAddr = new Struct.Unsigned32();
    public final Struct.Unsigned8 slotNum = new Struct.Unsigned8();
    public final Struct.Unsigned16 duration = new Struct.Unsigned16();
    public final Struct.Unsigned8 level = new Struct.Unsigned8();
    public final Struct.Unsigned8 ruler = new Struct.Unsigned8();

    public MsgChgStart(long devAddr,short slotNum,int duration,short level,short ruler) {
        this.devAddr.set(devAddr);
        this.slotNum.set(slotNum);
        this.duration.set(duration);
        this.level.set(level);
        this.ruler.set(ruler);
    }

    public MsgChgStart(){

    }


    public  byte [] getAppData(){
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
