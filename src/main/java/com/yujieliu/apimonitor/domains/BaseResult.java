package com.yujieliu.apimonitor.domains;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public abstract class BaseResult {

    boolean connection;

    Date time;

    BaseResult(boolean connection, Date time){
        this.connection = connection;
        this.time = time;
    }
}
