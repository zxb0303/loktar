package com.loktar.dto.docker;

import lombok.Data;

@Data
public class ContainerExecStartReq {
    public boolean detach;
    public boolean tty;
}
