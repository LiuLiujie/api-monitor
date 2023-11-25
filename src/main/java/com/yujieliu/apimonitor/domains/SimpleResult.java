package com.yujieliu.apimonitor.domains;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class SimpleResult extends BaseResult{

    public SimpleResult(boolean connection, Date time) {
        super(connection, time);
    }
}
