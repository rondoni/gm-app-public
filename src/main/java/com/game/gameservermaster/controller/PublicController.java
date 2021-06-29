package com.game.gameservermaster.controller;

import com.game.gameservermaster.config.GMConstants.JsonKeys;
import com.game.gameservermaster.model.GameInstance;
import com.game.gameservermaster.service.PlayerActionHandler;
import com.game.gameservermaster.service.GameInstanceManager;
import com.game.gameservermaster.service.LoginHandler;
import com.game.gameservermaster.utils.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class PublicController {

    @Value("${game-file.path:}")
    private String gameFilePath;

    @Value("${game-file.name:}")
    private String gameFileName;

    @Autowired
    private LoginHandler loginHandler;

    @Autowired
    private GameInstanceManager instManager;


    @CrossOrigin
    @PostMapping(path = "/login")
    public ResponseEntity login(@RequestBody String payload) {
        return loginHandler.handleLogin(payload);
    }

    @GetMapping(path = "/download")
    public ResponseEntity download() throws IOException {
        try{
            HttpHeaders headers = new HttpHeaders(); headers.add(
                HttpHeaders.CONTENT_DISPOSITION, "filename=" + gameFileName);
            File file = new File(gameFilePath + "/" + gameFileName);
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

            return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File not found");
        }

    }

    @CrossOrigin
    @GetMapping(path = "/serverbrowse", produces = "application/json")
    public String serverBrowse() {
        JSON response = new JSON();
        List<GameInstance> instList = instManager.getAllPublicInstances();
        response.put(JsonKeys.SERVER_INFO, instList
            .stream().map(g -> g.toClientJson()).collect(Collectors.toList()));
        return response.toJsonString();
    }

}
