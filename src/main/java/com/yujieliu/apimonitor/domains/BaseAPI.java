package com.yujieliu.apimonitor.domains;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public abstract class BaseAPI {

    String name;

    String schema;
}
