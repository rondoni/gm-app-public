package com.game.gameservermaster.controller;

import com.game.gameservermaster.config.GMConstants;
import com.game.gameservermaster.config.GMErrorCode;
import com.game.gameservermaster.service.GameInstanceManager;
import com.game.gameservermaster.utils.JSON;
import lombok.extern.slf4j.Slf4j;

import com.game.gameservermaster.service.PlayerActionHandler;
import com.game.gameservermaster.service.KubeApiService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;


import static com.game.gameservermaster.utils.GMUtils.addError;

@Slf4j
@RestController
@RequestMapping("/api")
public class ApiController
{
    @Autowired
    private PlayerActionHandler playerActionHandler;

    @Autowired(required = false)
    private KubeApiService kubeService;

    @Autowired
    private GameInstanceManager instManager;

    private static final String hasRoleSuper = "hasRole('" + GMConstants.SUPER_ROLE + "')";
    private static final String hasRoleClient = "hasRole('" + GMConstants.CLIENT_ROLE + "')";

    @PreAuthorize(hasRoleSuper)
    @PostMapping(value = "/spawngi", produces = "application/json")
    public String spawnGameInstance(@RequestBody String payload) {
        if(kubeService != null) {
            return kubeService.spawnInstancePod(payload).toJsonString();
        } else {
            JSON response = new JSON();
            addError(response, "service not enabled", GMErrorCode.INTERNAL_ERROR);
            return response.toJsonString();
        }
    }

    @PreAuthorize(hasRoleClient)
    @PostMapping(path = "/joininstance", produces = "application/json")
    public String joinInstance(@RequestBody String payload) {
        String username = getUsernameFromContext();
        return playerActionHandler.handleInstanceJoin(username, payload).toJsonString();
    }

    @PreAuthorize(hasRoleClient)
    @GetMapping("/charlist")
    public String charList() {
        JSON response = new JSON();
        playerActionHandler.addCharList(getUsernameFromContext(), response);
        return response.toJsonString();
    }

    @PreAuthorize(hasRoleSuper)
    @GetMapping("/serverinfo")
    public String serverInfo() {
        StringBuilder response = new StringBuilder();
        instManager.getAllPublicInstances().stream()
            .map(i -> response.append(i.toString() + "<br /><br />"));
        return response.toString();
    }

    private String getUsernameFromContext() {
        try {
            return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            log.error("error getting username from context", e);
            return null;
        }
    }

}