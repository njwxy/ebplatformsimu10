package com.wxy.ebtestms.message;

import javolution.io.Struct;

import java.nio.ByteOrder;

public class MsgChgStop extends Struct{
    public final Struct.Unsigned32 devAddr = new Struct.Unsigned32();
    public final Struct.Unsigned8 slotNum = new Struct.Unsigned8();

    public MsgChgStop(long devAddr,short slotNum) {
        this.devAddr.set(devAddr);
        this.slotNum.set(slotNum);

    }

    public MsgChgStop(){

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
