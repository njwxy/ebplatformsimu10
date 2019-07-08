package com.wxy.ebtestms.ctrl;


import com.wxy.ebtestms.message.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

import static com.wxy.ebtestms.utility.ConstData.*;

@Controller
@Slf4j
public class EbControl {
    public String enrollEbNode=null;

    @Autowired
    EbSerial ebSerial;

    public EbSetRelay ebSetRelay = new EbSetRelay();
 //   @Value("${syspara.gwcfg}")
  //  private String gwcfg;

    @Autowired
    EbConfig ebConfig;

    @RequestMapping(value="/setGwPara")
    public String setGwPara(ModelMap map) throws IOException {
        //enrollEbNode = gwcfg;
        EbAddrCfg ebAddrCfg=new EbAddrCfg();
        String gwcfg =  ebConfig.readCfg();
        ebAddrCfg.setJsonData(gwcfg);
        map.put("enrollPvnode",ebAddrCfg);
        return "SetGwPara";
    }

    @PostMapping(value = "/addGwCfg")
    public String addGwCfg(Model model, @ModelAttribute EbAddrCfg nodeDate) throws IOException {
        //public String burnIdSubmit(Model model,@ModelAttribute EbBoxChk ebBoxChk)
       // enrollEbNode = nodeDate;
        ebConfig.writeCfg(nodeDate.getJsonData());
        log.info("return "+nodeDate.toString());
        model.addAttribute("msg",nodeDate.toString());
        return "addGwCfgResult";
    }

    @RequestMapping(value="/setRelay")
    public String setRelay(ModelMap map) throws IOException {
        //enrollEbNode = gwcfg;
        //EbAddrCfg ebAddrCfg=new EbAddrCfg();
        //String gwcfg =  ebConfig.readCfg();
        //ebAddrCfg.setJsonData(gwcfg);
        map.put("ebSetRelay",ebSetRelay);
        return "setRelay";
    }

    @PostMapping(value = "/setRelayPost")
    public String setRelayPost(Model model, @ModelAttribute EbSetRelay ebSetRelay1){
        if(ebSetRelay1.rState == 1){/* 打开继电器 */
            MsgChgStart msgChgStart = new MsgChgStart(ebSetRelay1.devAddr,ebSetRelay1.slotNum,1,(short)3,(short)1);
            byte [] appdata = msgChgStart.getAppData();
            byte[] packetdata = FrameHead.getSendPacket(APPCMD_CHG_START,(short) appdata.length,
                    appdata,ebSetRelay1.devAddr);
            ebSerial.sendData(packetdata);
        }else{ /* 结束充电 */
            MsgChgStop msgChgStop = new MsgChgStop(ebSetRelay1.devAddr,ebSetRelay1.slotNum);
            byte [] appdata = msgChgStop.getAppData();
            byte[] packetdata = FrameHead.getSendPacket(APPCMD_CHG_STOP_SERVER,(short) appdata.length,
                    appdata,ebSetRelay1.devAddr);
            ebSerial.sendData(packetdata);

        }

        return "setRelayResult";
    }
}
