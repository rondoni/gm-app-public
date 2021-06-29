package com.game.gameservermaster.config;

public enum GMErrorCode {

    INTERNAL_ERROR(5000),
    CHAR_NOT_FOUND(5001),
    CHAR_INVALID_GAME_INST(5002),
    GAME_INST_NOT_FOUND(5003),
    MISSING_REQUIRED_PARAM(5004),
    MALFORMED_JSON(5005),
    INVALID_CREDS(5006),
    ;

    public final int code;

    GMErrorCode(int code){
        this.code = code;
    }

}
