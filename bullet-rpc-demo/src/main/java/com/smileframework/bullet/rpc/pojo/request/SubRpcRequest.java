package com.smileframework.bullet.rpc.pojo.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubRpcRequest extends DemoRpcRequest<String> {

    private int subIndex;

}
