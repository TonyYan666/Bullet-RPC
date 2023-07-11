package com.smileframework.bullet.transport.client.connection.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
public class AddressAndPort {

    public AddressAndPort(String address, int port) {
        this.address = address;
        this.port = port;
    }

    private String address;

    private int port;


    @Override
    public String toString() {
        return address + ":" + port;
    }
}
