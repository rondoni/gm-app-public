package com.game.gameservermaster.service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

import com.game.gameservermaster.config.GMErrorCode;
import com.game.gameservermaster.config.GameInstType;
import com.game.gameservermaster.config.GMConstants;
import com.game.gameservermaster.config.KubeConfig;
import static com.game.gameservermaster.utils.GMUtils.addError;

import com.game.gameservermaster.model.GameInstance;

import com.game.gameservermaster.utils.JSON;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;

import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Config;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "kube", name = "service-enabled", havingValue = "true")
public class KubeApiService {

    private ApiClient client;
    private CoreV1Api api;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Autowired
    private KubeConfig kubeConfig;

    public KubeApiService() throws IOException {
        client = Config.fromConfig(new ClassPathResource(
                "kube.yml", KubeApiService.class.getClassLoader()).getInputStream());
        Configuration.setDefaultApiClient(client);
        api = new CoreV1Api();
    }

    public Future<V1PodList> fetchAllPodInfo() {
        return executorService.submit(() -> api.listNamespacedPod(
            "default", null, null,
            null, null, null, null,
            null, null, null));
    }

    //TODO: clean this up
    public JSON spawnInstancePod(String spawnInfoStr) {
        JSON response = new JSON();

        String instanceTypeStr;
        boolean isPublic;
        String instAlias;
        try {
            JSON spawnInfo = JSON.parse(spawnInfoStr);
            instanceTypeStr = spawnInfo.get("inst_type");
            isPublic = spawnInfo.<String>get("is_public").equalsIgnoreCase("true");
            instAlias = spawnInfo.get("inst_alias");
        } catch (Exception e) {
            return instSpawnErrorResponse("malformed request");
        }

        GameInstType instType = GameInstType.find(instanceTypeStr);
        if (instType == null)
            return instSpawnErrorResponse("invalid instance type: \"" + instanceTypeStr + "\"");

        String imgName = kubeConfig.getImgLoc() + instType.imgName();
        String podID = instType.val() + "-" + UUID.randomUUID().toString();

        try {
            V1ObjectMeta meta = new V1ObjectMeta();
            meta.name(podID);

            List<V1EnvVar> envVars = new ArrayList<V1EnvVar>() {{
                add(new V1EnvVar().name(GMConstants.InstEnvConstants.INST_ALIAS_ENV_KEY).value(instAlias));
                add(new V1EnvVar().name(GMConstants.InstEnvConstants.INST_ID_ENV_KEY).value(String.valueOf(podID)));
                add(new V1EnvVar().name(GMConstants.InstEnvConstants.INST_TYPE_ENV_KEY).value(instType.val()));
                add(new V1EnvVar().name(GMConstants.InstEnvConstants.INST_ACCESS_ENV_KEY).value(String.valueOf(isPublic)));
            }};

            V1ResourceRequirements resourceRequirements = new V1ResourceRequirements();
            Map<String, Quantity> resourceLimits = new HashMap<>();
            resourceLimits.put("cpu", new Quantity("0.25"));
            resourceRequirements.setLimits(resourceLimits);

            V1Container container = new V1Container()
                .name(podID)
                .image(imgName)
                .env(envVars)
                .resources(resourceRequirements);

            V1LocalObjectReference imagePullSecret = new V1LocalObjectReference()
                .name(kubeConfig.getImgSecret());

            V1PodSpec spec = new V1PodSpec()
                .containers(Arrays.asList(container))
                .hostNetwork(true)
                .restartPolicy("Never")
                .addImagePullSecretsItem(imagePullSecret);

            V1Pod podBody = new V1Pod()
                .apiVersion("v1")
                .kind("Pod")
                .metadata(meta)
                .spec(spec);

            api.createNamespacedPod("default", podBody, null, null, null);

            response.put("inst_id", podID);
        } catch (Exception e) {
            return instSpawnErrorResponse("error spawning pod: " + e.getMessage());
        }
        return response;
    }

    public JSON instSpawnErrorResponse(String errorStr) {
        JSON response = new JSON();
        log.error("failed to spawn instance: " + errorStr);
        addError(response, errorStr, GMErrorCode.INTERNAL_ERROR);
        return response;
    }


    public GameInstance podToGameInst(V1Pod pod) {
        GameInstance gameInstance = null;
        try {
            Map<String, String> envVars = getPodEnvVars(pod);
            gameInstance = new GameInstance(
                envVars.get(GMConstants.InstEnvConstants.INST_ALIAS_ENV_KEY),
                pod.getMetadata().getName(),
                kubeConfig.getDefaultAddr(),
                Integer.parseInt(envVars.get(GMConstants.InstEnvConstants.INST_MAIN_PORT_ENV_KEY)),
                envVars.get(GMConstants.InstEnvConstants.INST_ACCESS_ENV_KEY).equalsIgnoreCase("true"),
                pod,
                false);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return gameInstance;
    }

    private Map<String, String> getPodEnvVars(V1Pod pod) {
        Map<String, String> envVars = new HashMap<>();
        pod.getSpec().getContainers().get(0).getEnv().stream().forEach(
                (v) -> envVars.put(v.getName(), v.getValue())
        );
        if (!envVars.containsKey(GMConstants.InstEnvConstants.INST_ALIAS_ENV_KEY)
                || !envVars.containsKey(GMConstants.InstEnvConstants.INST_MAIN_PORT_ENV_KEY)
                || !envVars.containsKey(GMConstants.InstEnvConstants.INST_ACCESS_ENV_KEY)) {
            throw new RuntimeException("pod missing environment variable");
        }
        return envVars;
    }

    private void dumpPodLogs(V1Pod pod, String podID) {
        log.info("dumping logs for pod " + podID);
        try{
            String inst_logs = api.readNamespacedPodLog(podID, "default",
                pod.getSpec().getContainers().get(0).getName(), null, null,
                100000, null, null, null, null, null);
            FileWriter log_writer = new FileWriter(GMConstants.INST_LOG_PATH + podID + ".log");
            log_writer.write(inst_logs);
            log_writer.close();
        }catch (Exception e){
            log.error(e.getMessage());
        }

    }

    private void destroyPod(String podID)
    {
        try {
            api.deleteNamespacedPod(podID, "default", null, null, null, null, null, null);
        } catch(Exception e) {
            log.error(e.getMessage());
        }
    }

}
