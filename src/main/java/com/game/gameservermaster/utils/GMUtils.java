package com.game.gameservermaster.utils;

import com.game.gameservermaster.config.GMConstants.JsonKeys;
import com.game.gameservermaster.config.GMErrorCode;
import java.security.SecureRandom;

public class GMUtils {

    private static final String ANChars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final SecureRandom secureRandom = new SecureRandom();

    private static String randomAlphanumeric(int len)
    {
        StringBuilder sb = new StringBuilder( len );
        for(int i = 0; i < len; i++)
            sb.append(ANChars.charAt(secureRandom.nextInt(ANChars.length())));
        return sb.toString();
    }

    public static void addError(JSON json, String errorStr, GMErrorCode errorCode) {
        json.put(JsonKeys.ERROR, errorStr);
        json.put(JsonKeys.ERROR_CODE, errorCode.code);
    }

}
