package com.nayarsystems.nexus;

public enum NexusError {
    Parse(-32700),
    InvalidRequest(-32600),
    Internal(-32603),
    InvalidParams(-32602),
    MethodNotFound(-32601),
    TtlExpired(-32011),
    PermissionDenied(-32010),
    LockNotOwned(-32006),
    UserExists(-32005),
    InvalidUser(-32004),
    InvalidPipe(-32003),
    InvalidTask(-32002),
    Cancel(-32001),
    Timeout(-32000),
    NoError(0);

    private final int code;

    NexusError(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}
