//package com.loktar.backup;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.PropertyNamingStrategies;
//import com.loktar.conf.LokTarConstant;
//import com.loktar.dto.docker.ContainerExecCreateReq;
//import com.loktar.dto.docker.ContainerExecCreateRsp;
//import com.loktar.dto.docker.ContainerExecStartReq;
//import lombok.SneakyThrows;
//
//import java.net.URI;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//import java.text.MessageFormat;
//import java.time.Duration;
//
//public class DockerEngineApiUtilBackup {
//    public final static String CONTAINER_EXEC_CREATE_URL = "/containers/{0}/exec";
//    public final static String CONTAINER_EXEC_START_URL = "/exec/{0}/start";
//    public static String FFMPEG_COMTAINER_NAME = "jrottenberg-ffmpeg";
//
//    @SneakyThrows
//    public static ContainerExecCreateRsp containerExecCreate(String containerName, String command) {
//        ContainerExecCreateReq containerExecCreateReq = new ContainerExecCreateReq();
//        containerExecCreateReq.setAttachStdin(false);
//        containerExecCreateReq.setAttachStdout(true);
//        containerExecCreateReq.setAttachStderr(true);
//        containerExecCreateReq.setDetachKeys("ctrl-p,ctrl-q");
//        containerExecCreateReq.setTty(false);
//        containerExecCreateReq.setCmd(command.split(" "));
//        //打印可刪
//        System.out.println("containerExecCreate request:" + new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.UPPER_CAMEL_CASE).writeValueAsString(containerExecCreateReq));
//        HttpClient httpClient = HttpClient.newHttpClient();
//        HttpRequest httpRequest = HttpRequest.newBuilder()
//                .uri(URI.create(MessageFormat.format(CONTAINER_EXEC_CREATE_URL, containerName)))
//                .timeout(Duration.ofSeconds(60))
//                .header(LokTarConstant.HTTP_HEADER_CONTENT_TYPE_NAME, LokTarConstant.HTTP_HEADER_CONTENT_TYPE_VALUE_JSON)
//                .header(LokTarConstant.HTTP_HEADER_ACCEPT_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_VALUE_JSON)
//                .POST(HttpRequest.BodyPublishers.ofString(new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.UPPER_CAMEL_CASE).writeValueAsString(containerExecCreateReq)))
//                .build();
//        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
//        //打印可刪
//        System.out.println("containerExecCreate response:" + response.body());
//        ContainerExecCreateRsp containerExecCreateRsp = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.UPPER_CAMEL_CASE).readValue(response.body(), ContainerExecCreateRsp.class);
//        return containerExecCreateRsp;
//    }
//
//
//    public static boolean containerExecStart(ContainerExecCreateRsp containerExecCreateRsp) {
//        ContainerExecStartReq containerExecStartReq = new ContainerExecStartReq();
//        containerExecStartReq.setDetach(false);
//        containerExecStartReq.setTty(false);
//        HttpClient httpClient = HttpClient.newHttpClient();
//        HttpRequest httpRequest = null;
//        try {
//            httpRequest = HttpRequest.newBuilder()
//                    .uri(URI.create(MessageFormat.format(CONTAINER_EXEC_START_URL, containerExecCreateRsp.id)))
//                    .timeout(Duration.ofSeconds(60))
//                    .header(LokTarConstant.HTTP_HEADER_CONTENT_TYPE_NAME, LokTarConstant.HTTP_HEADER_CONTENT_TYPE_VALUE_JSON)
//                    .header(LokTarConstant.HTTP_HEADER_ACCEPT_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_VALUE_JSON)
//                    .POST(HttpRequest.BodyPublishers.ofString(new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.UPPER_CAMEL_CASE).writeValueAsString(containerExecStartReq)))
//                    .build();
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
////        HttpResponse<String> response = null;
//        try {
//            httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
////        System.out.println("containerExecStart response:"+response.body());
////        if (StringUtils.isEmpty(response.body())) {
////            return true;
////        }
//        return false;
//    }
//
//    @SneakyThrows
//    public static boolean containerExec(String containerName, String command) {
//        return containerExecStart(containerExecCreate(containerName, command));
//    }
//
//
//    public static void main(String[] args) {
//        String containerName = "jrottenberg-ffmpeg";
//        //String command = "ffmpeg -y -loglevel error -i /fun/voice/test.wav -c:a libopencore_amrnb -ar 8000 -ac 1 /fun/voice/test.amr";
//        String command = "ffmpeg -y -loglevel error -i /fun/voice/test.amr -ac 1 -ar 16000 -b:a 256k /fun/voice/test.wav";
//
//        ContainerExecCreateRsp containerExecCreateRsp = containerExecCreate(containerName, command);
//        containerExecStart(containerExecCreateRsp);
//    }
//}
