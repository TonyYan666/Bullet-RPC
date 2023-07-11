package com.smileframework.bullet.rpc.pojo.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DemoRpcRequest<T> {

    private String demoContent;

    private Integer index;

    private T other;

}
