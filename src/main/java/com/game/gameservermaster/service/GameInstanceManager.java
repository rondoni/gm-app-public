package com.game.gameservermaster.service;

import com.game.gameservermaster.model.GameInstance;
import io.kubernetes.client.openapi.models.V1PodList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class GameInstanceManager {

    private Map<String, GameInstance> instanceMap = new Hashtable<>();

    private KubeApiService kubeService;

    @Autowired(required = false)
    public void setKubeService(KubeApiService kubeService) {
        this.kubeService = kubeService;
    }

    public void addOffClusterInstance(GameInstance gameInstance) {
        if(!gameInstance.isOffCluster()) {
            log.error("attempted to add on cluster instance as off cluster");
        } else {
            instanceMap.put(gameInstance.getInstID(), gameInstance);
        }
    }

    public GameInstance getInstByID(String instID) {
        if(instID != null)
            return instanceMap.get(instID);
        return null;
    }

    public List<GameInstance> getAllPublicInstances() {
        List<GameInstance> instList = instanceMap.values()
            .stream()
            .filter(g -> g.isPublic())
            .collect(Collectors.toList());
        return instList;
    }

    @Scheduled(fixedDelay = 5000L)
    private void instRefreshJob() {
        if(kubeService != null) refreshInstanceList();
    }

    protected void refreshInstanceList() {
        try {
            V1PodList podList = kubeService.fetchAllPodInfo().get();
            if(podList.getItems().size() > 1) throw new RuntimeException("1");
            Map<String, GameInstance> updatedMap = podList.getItems().stream()
                .map(p -> kubeService.podToGameInst(p))
                .filter(g -> g != null)
                .collect(Collectors.toMap(GameInstance::getInstID, Function.identity()));
            instanceMap.entrySet().removeIf(
                i -> (!updatedMap.containsKey(i) && !i.getValue().isOffCluster())
            );
            instanceMap.putAll(updatedMap);
        } catch(ExecutionException | InterruptedException e) {
            log.error(e.getCause().getMessage(), e.getCause());
        }
    }

}
