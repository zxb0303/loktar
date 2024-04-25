package com.loktar.util;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.loktar.conf.LokTarConfig;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;


@Component
public class DockerEngineApiUtil {

    public final static String FFMPEG_COMTAINER_NAME = "jrottenberg-ffmpeg";

    public static DockerClientConfig config;

    public static DockerHttpClient httpClient;

    public static DockerClient dockerClient;

    public DockerEngineApiUtil(LokTarConfig lokTarConfig) {
        config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(lokTarConfig.getDocker().getTcpUrl())
                .withDockerTlsVerify(false)
                .withApiVersion(lokTarConfig.getDocker().getApiVersion())
                .build();
        httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();
        dockerClient = DockerClientImpl.getInstance(config, httpClient);
    }

    public static String containerExec(String containerName, String command) {
        String containerId = getContainerIdByName(containerName);
        ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                .withCmd(command.split(" "))
                .withAttachStdin(false)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withTty(false)
                .exec();
        final StringBuilder output = new StringBuilder();
        dockerClient.execStartCmd(execCreateCmdResponse.getId())
                .withDetach(false)
                .withTty(true)
                .exec(new ResultCallback.Adapter<Frame>() {
                    @Override
                    public void onNext(Frame object) {
                        output.append(new String(object.getPayload(), UTF_8));
                        super.onNext(object);
                    }

                });
        //TODO 内容返回了 但是文件还未生成
//        try {
//            Thread.sleep(4000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        try {
//            dockerClient.execStartCmd(execCreateCmdResponse.getId())
//                    .withDetach(false)
//                    .withTty(true)
//                    .exec(new ExecStartResultCallback(byteArrayOutputStream, System.err)).awaitCompletion();
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        return output.toString();
    }

    public static String getContainerIdByName(String containerName) {
        List<Container> containers = dockerClient.listContainersCmd().exec();
        for (Container container : containers) {
            if (container.getNames()[0].contains(containerName)) {
                return container.getId();
            }
        }
        return null;
    }

    public static void main(String[] args) {
//        System.out.println(getContainerIdbyName(FFMPEG_COMTAINER_NAME));
        String cmd = "ffmpeg -y -loglevel error -i /loktar/voice/111.wav -c:a libopencore_amrnb -ar 8000 -ac 1 /loktar/voice/222.amr";
        String output = containerExec(FFMPEG_COMTAINER_NAME, cmd);
    }


}
