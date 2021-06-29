package com.game.gameservermaster.config;

public interface GMConstants {

    String INST_LOG_PATH = "./instlogs/ ";
    String JWT_ROLE_KEY = "role";
    String CLIENT_ROLE = "CLIENT";
    String SUPER_ROLE = "SUPER";
    int CLIENT_TOKEN_EXP_MS_CONNECT = 1000 * 30;
    int CLIENT_TOKEN_EXP_MS_NO_CLAIMS = 1000 * 60 * 60;

    interface JsonKeys {
        String ERROR = "error";
        String ERROR_CODE = "error_code";
        String SERVER_INFO = "server_info";
        String AUTH_TOKEN = "auth_token";
        String CHAR_ID = "char_id";
        String CHAR_NAME = "char_name";
        String CHAR_LIST = "char_list";
        String INST_ID = "inst_id";
        String INST_ALIAS = "inst_alias";
        String SERVER_ADDR = "server_addr";
        String SERVER_PORT = "server_port";
        String USERNAME = "username";
        String PASSWORD = "password";
    }

    interface InstEnvConstants {
        String INST_MAIN_PORT_ENV_KEY = "GAME_INST_PORT";
        String INST_ALIAS_ENV_KEY = "GAME_INST_ALIAS";
        String INST_ID_ENV_KEY = "GAME_INST_ID";
        String INST_TYPE_ENV_KEY = "GAME_INST_TYPE";
        String INST_ACCESS_ENV_KEY = "GAME_INST_ACCESS";
    }
}
