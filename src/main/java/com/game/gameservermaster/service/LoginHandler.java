package com.game.gameservermaster.service;

import com.game.gameservermaster.config.GMConstants;
import com.game.gameservermaster.config.GMErrorCode;
import com.game.gameservermaster.exception.MalformedJsonException;
import com.game.gameservermaster.utils.JSON;
import com.game.gameservermaster.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import static com.game.gameservermaster.utils.GMUtils.addError;

@Component
public class LoginHandler {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private PlayerActionHandler playerActionHandler;

    private static final String INVALID_CREDS_STR = "invalid credentials";

    public ResponseEntity handleLogin(String payload) throws AuthenticationException {
        JSON response = new JSON();
        try {
            JSON json = JSON.parse(payload);
            if(hasLoginParams(json, response)) {
                String username = json.get(GMConstants.JsonKeys.USERNAME);
                String password = json.get(GMConstants.JsonKeys.PASSWORD);

                if(authenticateLogin(username, password, response)) {
                    playerActionHandler.addCharList(username, response);
                    return ResponseEntity.ok(response.toJsonString());
                }
            }
        } catch (MalformedJsonException e) {
            addError(response, "bad input", GMErrorCode.MALFORMED_JSON);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response.toJsonString());
    }

    private boolean authenticateLogin(String username, String password, JSON response) throws MalformedJsonException{
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            String tokenString = jwtUtils.buildClientToken(username,
                GMConstants.CLIENT_TOKEN_EXP_MS_NO_CLAIMS).compact();
            response.put(GMConstants.JsonKeys.AUTH_TOKEN, tokenString);
            return true;
        } catch(AuthenticationException e) {
            addError(response, INVALID_CREDS_STR, GMErrorCode.INVALID_CREDS);
        }
        return false;
    }

    private boolean hasLoginParams(JSON json, JSON response) {
        if(json.has(GMConstants.JsonKeys.USERNAME) && json.has(GMConstants.JsonKeys.PASSWORD)){
            return true;
        } else {
            addError(response, INVALID_CREDS_STR, GMErrorCode.MISSING_REQUIRED_PARAM);
            return false;
        }
    }


}
