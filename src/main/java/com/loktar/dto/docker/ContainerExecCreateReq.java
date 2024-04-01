package com.loktar.dto.docker;

import lombok.Data;

@Data
public class ContainerExecCreateReq {
    public boolean attachStdin;
    public boolean attachStdout;
    public boolean attachStderr;
    public String detachKeys;
    public boolean tty;
    public String[] cmd;
}
