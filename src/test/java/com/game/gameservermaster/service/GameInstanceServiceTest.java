package com.game.gameservermaster.service;

import com.game.gameservermaster.model.GameInstance;
import groovy.util.logging.Slf4j;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class GameInstanceServiceTest {

    private GameInstanceManager instanceManager = new GameInstanceManager();

    private KubeApiService kubeService = mock(KubeApiService.class);

    @BeforeEach()
    void setup() {
        instanceManager.setKubeService(kubeService);
        mockKubePodList(1);
    }

    @Test
    void testRefresh() {
        GameInstance offClusterInst = new GameInstance();
        offClusterInst.setOffCluster(true);
        offClusterInst.setInstID("offcluster");
        offClusterInst.setPublic(true);
        GameInstance onClusterInst = new GameInstance();
        onClusterInst.setOffCluster(false);
        onClusterInst.setInstID("oncluster");
        onClusterInst.setPublic(true);

        when(kubeService.podToGameInst(any())).thenReturn(onClusterInst);
        instanceManager.addOffClusterInstance(offClusterInst);
        instanceManager.refreshInstanceList();
        assertEquals(2, instanceManager.getAllPublicInstances().size());
        assertEquals("oncluster", instanceManager.getInstByID("oncluster").getInstID());
    }

    @Test
    void testRefreshRemoval() {
        GameInstance onClusterInst1 = new GameInstance();
        onClusterInst1.setOffCluster(false);
        onClusterInst1.setInstID("oncluster1");
        onClusterInst1.setPublic(true);
        GameInstance onClusterInst2 = new GameInstance();
        onClusterInst2.setOffCluster(false);
        onClusterInst2.setInstID("oncluster2");
        onClusterInst2.setPublic(true);

        when(kubeService.podToGameInst(any())).thenReturn(onClusterInst1);
        instanceManager.refreshInstanceList();
        assertEquals(1, instanceManager.getAllPublicInstances().size());

        when(kubeService.podToGameInst(any())).thenReturn(onClusterInst2);
        instanceManager.refreshInstanceList();
        assertEquals(1, instanceManager.getAllPublicInstances().size());
        assertEquals("oncluster2", instanceManager.getInstByID("oncluster2").getInstID());
    }

    private void mockKubePodList(int size) {
        V1PodList v1PodList = mock(V1PodList.class);
        List<V1Pod> podList = new ArrayList<>();
        for(int i=0;i<size;i++)
            podList.add(mock(V1Pod.class));
        when(v1PodList.getItems()).thenReturn(podList);
        when(kubeService.fetchAllPodInfo()).thenReturn(CompletableFuture.completedFuture(v1PodList));
    }

}
