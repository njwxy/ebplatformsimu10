package com.wxy.ebtestms.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;

/*
*   通过硬盘文件配置测试节点地址
* */
@Component
@Slf4j
public class EbConfig {
    @Value("${syspara.gwcfg}")
    private String gwcfg;

    public String  readCfg() throws IOException {
        File file = new File("gwcfg.txt");
        if(!file.exists()){
            file.createNewFile();
            Writer writer = new FileWriter(file);
            writer.write(gwcfg);
            writer.flush();
            writer.close();
        }
        Reader reader = new FileReader(file);
        long filelen = file.length();
        char[] bfiledata = new char[(int)filelen];
        reader.read(bfiledata,0,(int)file.length());
        String readString = new String(bfiledata);
        log.info("read data:"+readString);
        reader.close();
        return readString;
    }

    public void writeCfg(String writeStr) throws IOException {
        File file = new File("gwcfg.txt");
        if(!file.exists()){
            file.createNewFile();
        }

        //Writer writer = new FileWriter(file,true); 追加方式
        Writer writer = new FileWriter(file);
        writer.write(writeStr);
        writer.flush();
        writer.close();
    }


}
