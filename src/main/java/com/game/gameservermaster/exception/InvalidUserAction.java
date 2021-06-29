package com.game.gameservermaster.exception;

import com.game.gameservermaster.config.GMErrorCode;
import lombok.Getter;

@Getter
public class InvalidUserAction extends RuntimeException {

    private final GMErrorCode errorCode;

	public InvalidUserAction(GMErrorCode errorCode) {
        super();
        this.errorCode = errorCode;
    }

	public InvalidUserAction(GMErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
