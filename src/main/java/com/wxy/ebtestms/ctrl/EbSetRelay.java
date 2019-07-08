package com.wxy.ebtestms.ctrl;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Setter
@Getter
public class EbSetRelay {
    public long devAddr;
    public short rState;
    public short slotNum;
}
