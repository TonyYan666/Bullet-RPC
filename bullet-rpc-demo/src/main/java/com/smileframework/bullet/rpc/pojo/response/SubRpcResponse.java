package com.smileframework.bullet.rpc.pojo.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubRpcResponse extends DemoRpcResponse<String> {

    private int subIndex;

}
