package com.wxy.ebtestms.utility;



import com.wxy.ebtestms.message.FrameHead;

import java.nio.ByteBuffer;

public class PrjFuncs {

    public static final char[] hexa = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    public  static String Hex2Str(byte[] hexByteIn,int len1){
        int len = hexByteIn.length;
        if(len1<len)
            len = len1;

        StringBuffer sb = new StringBuffer(len * 3);

        for(int i = 0; i < len; ++i) {
            int b = hexByteIn[i]  & 255;
            sb.append(hexa[b >> 4]);
            sb.append(hexa[b & 15]);
            sb.append((char)(' '));
        }
        return sb.toString();
    }

    public static byte getSum(byte[] data,int start,int length)
    {
        int ret = 0;
        for(int i = 0;i<length;i++)
        {
            ret = ret + (int)(data[i+start]&0xff);
        }
        byte retb = (byte)((ret)&0xff);
        return retb;
    }



    /*public static byte[] getSendPacket(short cmd, byte[] appData, short datalen)
    {
        FrameHead frameHead = new FrameHead((short) 0x68,cmd,datalen);

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
*/


}

