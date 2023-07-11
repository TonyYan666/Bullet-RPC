package com.smileframework.bullet.rpc.pojo.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DemoRpcResponse<T> {

    public String result;

    public int resultCode;

    private T other;

}
