package com.yujieliu.apimonitor.services;

import com.yujieliu.apimonitor.domains.BaseAPI;
import com.yujieliu.apimonitor.domains.BaseResult;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class BaseMonitoringService {

    private final ArrayList<BaseAPI> apis = new ArrayList<>();

    public abstract void loadAPIs();

    public abstract List<BaseResult> monitoring();
}
