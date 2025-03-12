package com.loktar.web.test;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.jcraft.jsch.*;
import lombok.Data;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class VPSInitMain {

    //通过jsch 搭建xray
    //config.json提供3种连接
    //1.vless-xtls-rprx-vision(部分设备不支持)
    //2.trojan(与vless共用443端口)
    //3.vless-ws-tls(加上warp)
    //前期准备：
    //1.购买vps，系统版本ubuntu22.04，域名指向vps的ip，vps如果有安全组，需提前将PORT_CUSTOM加入
    //2.修改下面的TODO配置
    //3.run main

    //TODO 主机绑定的域名
    private final static String HOST = "";
    //TODO 邮箱
    private final static String EMAIL = "";
    //TODO 用户名 root 非root需先修改root权限
    private final static String USER = "root";
    //TODO 密码
    private final static String PASSWORD = "";
    //TODO nginx伪造的重定向地址
    private final static String NGINX_REDIRECT_URL = "https://www.lovelive-anime.jp";
    //TODO 默认的ssh端口
    private final static int PORT = 22;
    //TODO 修改后的登录端口
    private final static int PORT_CUSTOM = 23684;
    //TODO 保存凭证等信息的本地主路径
    private final static String LOCAL_BASE_FOLD_PATH = "F:/loktar/vps";

    private final static String LOCAL_SSHKEY_FILEPATH = LOCAL_BASE_FOLD_PATH + "/" + HOST + "/id_rsa";
    private final static String LOCAL_CLINET_FILEPATH = LOCAL_BASE_FOLD_PATH + "/" + HOST + "/login.txt";
    private final static String NGINX_REDIRECT_DEFAULT_URL = "https://www.baidu.com";
    private final static String TEMPLATE_SSHDCONFIG = "src/main/resources/template/sshd_config";
    private final static String TEMPLATE_NGINXCONFIG = "src/main/resources/template/nginx-vps.conf";
    private final static String TEMPLATE_XRAYCONFIG = "src/main/resources/template/config.json";
    private final static String TEMPLATE_PROXIES = "src/main/resources/template/proxies.yaml";
    private final static String REMOTE_SSHKEY_FILEPATH = "/root/.ssh/id_rsa";
    private final static String REMOTE_SSHDCONFIG_FILEPATH = "/etc/ssh/sshd_config";
    private final static String REMOTE_NGINX_CONF_FILEPATH = "/etc/nginx/conf.d/nginx-vps.conf";
    private final static String REMOTE_WGCF_FILEPATH = "/root/warp.json";
    private final static String REMOTE_XRAY_CONFIG_FILEPATH = "/usr/local/etc/xray/config.json";
    private static String REMOTE_CERTIFICATE_FILEPATH = "/etc/letsencrypt/live/" + HOST + "/fullchain.pem";
    private static String REMOTE_KEY_FILEPATH = "/etc/letsencrypt/live/" + HOST + "/privkey.pem";
    private final static String WARP_ENDPOINT = "engage.cloudflareclient.com:2408";
    private final static ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).setSerializationInclusion(JsonInclude.Include.NON_NULL).enable(SerializationFeature.INDENT_OUTPUT);
    private final static ObjectMapper objectMapper2 = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).setSerializationInclusion(JsonInclude.Include.NON_NULL).enable(SerializationFeature.INDENT_OUTPUT);


    public static void main(String[] args) {
        //1.密码登录vps,配置秘钥登录,下载秘钥
        //step1();
        //2.秘钥登录，更新包,修改时区,开启防火墙80 443端口,删除root密码
        //step2();
        //3.安装certbot,生成证书，配置自动更新
        //step3();
        //TODO 该用Certimate生成的证书则不执行上面的step3，step2后使用Certimate部署一下证书
        //String host = HOST.substring(HOST.indexOf(".") + 1);
        //REMOTE_CERTIFICATE_FILEPATH = "/root/certimate/certs/" + host + "/cert.crt";
        //REMOTE_KEY_FILEPATH = "/root/certimate/certs/" + host + "/cert.key";
        //4.安装nginx,配置nginx
        //step4();
        //5.安装xray,安装warp,配置xray,重启xray
        //step5();
        //6.格式化输出连接信息到本地
        //step6();

    }
    @SneakyThrows
    private static void step6() {
        Session session = getJschKeySession(USER, HOST, PORT_CUSTOM, LOCAL_SSHKEY_FILEPATH);
        session.connect();
        String xrayConfigStr = jschReadFile(session, REMOTE_XRAY_CONFIG_FILEPATH);
        XrayConfig xrayConfig = objectMapper.readValue(xrayConfigStr, XrayConfig.class);
        //输出连接信息
        String clashTemplateStr = new String(Files.readAllBytes(Paths.get(TEMPLATE_PROXIES)));
        String clashStr = MessageFormat.format(clashTemplateStr,
                HOST,
                HOST,
                xrayConfig.getInbounds().get(0).getSettings().getClients().get(0).getId(),
                HOST,
                HOST,
                xrayConfig.getInbounds().get(1).getSettings().getClients().get(0).getPassword(),
                HOST,
                HOST,
                xrayConfig.getInbounds().get(2).getSettings().getClients().get(0).getId(),
                HOST,
                xrayConfig.getInbounds().get(2).getStreamSettings().getWsSettings().getPath(),
                HOST
        );
        System.out.println(clashStr);
        Files.write(Paths.get(LOCAL_CLINET_FILEPATH), clashStr.getBytes());
        session.disconnect();
    }
    @SneakyThrows
    private static void step5() {
        Session session = getJschKeySession(USER, HOST, PORT_CUSTOM, LOCAL_SSHKEY_FILEPATH);
        session.connect();
        String command1 = "bash -c \"$(curl -L https://github.com/XTLS/Xray-install/raw/main/install-release.sh)\" @ install -u root";
        jschExec(session, command1);
        String command2 = "bash -c \"$(curl -L warp-reg.vercel.app)\" > warp.json";
        jschExec(session, command2);
        String wgcfStr = jschReadFile(session, REMOTE_WGCF_FILEPATH);
        Wgcf wgcf = objectMapper2.readValue(wgcfStr, Wgcf.class);
        String xrayConfigTemplateStr = new String(Files.readAllBytes(Paths.get(TEMPLATE_XRAYCONFIG)));
        XrayConfig xrayConfig = objectMapper.readValue(xrayConfigTemplateStr, XrayConfig.class);
        xrayConfig.getInbounds().get(0).getSettings().getClients().get(0).setId(UUID.randomUUID().toString());
        String path = "/" + generateRandomStr(10, false);
        xrayConfig.getInbounds().get(0).getSettings().getFallbacks().get(1).setPath(path);
        xrayConfig.getInbounds().get(0).getStreamSettings().getTlsSettings().getCertificates().get(0).setCertificateFile(REMOTE_CERTIFICATE_FILEPATH);
        xrayConfig.getInbounds().get(0).getStreamSettings().getTlsSettings().getCertificates().get(0).setKeyFile(REMOTE_KEY_FILEPATH);
        xrayConfig.getInbounds().get(1).getSettings().getClients().get(0).setPassword(generateRandomStr(10, true));
        xrayConfig.getInbounds().get(2).getSettings().getClients().get(0).setId(UUID.randomUUID().toString());
        xrayConfig.getInbounds().get(2).getSettings().getClients().get(0).setEmail(EMAIL);
        xrayConfig.getInbounds().get(2).getStreamSettings().getWsSettings().setPath(path);
        xrayConfig.getOutbounds().get(2).getSettings().setSecretKey(wgcf.getPrivateKey());
        List<String> addressList = new ArrayList<>();
        addressList.add(wgcf.getV4());
        addressList.add(wgcf.getV6());
        xrayConfig.getOutbounds().get(2).getSettings().setAddress(addressList);
        xrayConfig.getOutbounds().get(2).getSettings().getPeers().get(0).setPublicKey(wgcf.getPublicKey());
        xrayConfig.getOutbounds().get(2).getSettings().getPeers().get(0).setEndpoint(WARP_ENDPOINT);
        xrayConfig.getOutbounds().get(2).getSettings().setReserved(wgcf.getReservedDec());
        String newXrayConfig = objectMapper.writeValueAsString(xrayConfig);
        jschWriteFile(session, newXrayConfig, REMOTE_XRAY_CONFIG_FILEPATH);
        String command5 = "systemctl restart xray";
        jschExec(session, command5);
        session.disconnect();
    }



    @SneakyThrows
    private static void step4() {
        Session session = getJschKeySession(USER, HOST, PORT_CUSTOM, LOCAL_SSHKEY_FILEPATH);
        session.connect();
        String command1 = "apt install -y nginx";
        jschExec(session, command1);
        String command2 = "systemctl start nginx && systemctl enable nginx";
        jschExec(session, command2);
        String nginxconfigTemplateStr = new String(Files.readAllBytes(Paths.get(TEMPLATE_NGINXCONFIG)));
        String nginxconfigStr = nginxconfigTemplateStr.replace(NGINX_REDIRECT_DEFAULT_URL, NGINX_REDIRECT_URL);
        jschWriteFile(session, nginxconfigStr, REMOTE_NGINX_CONF_FILEPATH);
        String command3 = "nginx -s reload";
        jschExec(session, command3);
        session.disconnect();
    }

    @SneakyThrows
    private static void step3() {
        Session session = getJschKeySession(USER, HOST, PORT_CUSTOM, LOCAL_SSHKEY_FILEPATH);
        session.connect();
        String command1 = "apt -y install certbot";
        jschExec(session, command1);
        String command2 = "certbot certonly --non-interactive --agree-tos --email " + EMAIL + " -d " + HOST + " --standalone --no-eff-email";
        jschExec(session, command2);
        String command3 = "echo '0 0 1 * * root /root/certbot_renew.sh' >> /etc/crontab";
        jschExec(session, command3);
        String command4 = "echo '#!/bin/bash' > /root/certbot_renew.sh && " +
                "echo 'systemctl stop nginx' >> /root/certbot_renew.sh && " +
                "echo '/usr/bin/certbot renew' >> /root/certbot_renew.sh && " +
                "echo 'systemctl start nginx' >> /root/certbot_renew.sh";
        jschExec(session, command4);
        String command5 = "chmod +x /root/certbot_renew.sh";
        jschExec(session, command5);
        session.disconnect();
    }

    @SneakyThrows
    private static void step2() {
        Session session = getJschKeySession(USER, HOST, PORT_CUSTOM, LOCAL_SSHKEY_FILEPATH);
        session.connect();
        String command1 = "apt update";
        jschExec(session, command1);
        String command2 = "timedatectl set-timezone Asia/Shanghai";
        jschExec(session, command2);
        String command3 = "ufw allow 80 && ufw allow 443";
        jschExec(session, command3);
        String command4 = "passwd -d root";
        jschExec(session, command4);
        session.disconnect();
    }


    @SneakyThrows
    private static void step1() {
        Session session = getJschPasswordSession(USER, HOST, PORT, PASSWORD);
        session.connect();
        String command1 = "rm -f ~/.ssh/id_rsa ~/.ssh/id_rsa.pub && ssh-keygen -t rsa -b 2048 -m PEM -N \"\" -f ~/.ssh/id_rsa";
        jschExec(session, command1);
        String command2 = "mv /root/.ssh/id_rsa.pub /root/.ssh/authorized_keys";
        jschExec(session, command2);
        String command3 = "chmod 644 /root/.ssh/authorized_keys";
        jschExec(session, command3);
        File parentDir = new File(LOCAL_SSHKEY_FILEPATH).getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }
        jschDownload(session, REMOTE_SSHKEY_FILEPATH, LOCAL_SSHKEY_FILEPATH);
        String command4 = "ufw allow " + PORT_CUSTOM;
        jschExec(session, command4);
        String sshdconfigTemplateStr = new String(Files.readAllBytes(Paths.get(TEMPLATE_SSHDCONFIG)));
        String sshdconfigStr = MessageFormat.format(sshdconfigTemplateStr, String.valueOf(PORT_CUSTOM));
        jschWriteFile(session, sshdconfigStr, REMOTE_SSHDCONFIG_FILEPATH);
        String command5 = "systemctl restart sshd";
        jschExec(session, command5);
        session.disconnect();
    }

    @SneakyThrows
    private static void jschWriteFile(Session session, String content, String remoteFilepath) {
        Channel channel = session.openChannel("sftp");
        channel.connect();
        ChannelSftp channelSftp = (ChannelSftp) channel;
        try (InputStream inputStream = new ByteArrayInputStream(content.getBytes())) {
            channelSftp.put(inputStream, remoteFilepath, ChannelSftp.OVERWRITE);
        }
        System.out.println("File uploaded successfully");
    }

    @SneakyThrows
    private static String jschReadFile(Session session, String remoteFilePath) {
        String command = "cat " + remoteFilePath;
        Channel channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand(command);
        InputStream in = channel.getInputStream();
        channel.connect();
        StringBuilder outputBuffer = new StringBuilder();
        byte[] tmp = new byte[1024];
        while (true) {
            while (in.available() > 0) {
                int i = in.read(tmp, 0, 1024);
                if (i < 0) {
                    break;
                }
                outputBuffer.append(new String(tmp, 0, i));
            }
            if (channel.isClosed()) {
                if (in.available() > 0) {
                    continue;
                }
                break;
            }
            Thread.sleep(500);
        }
        channel.disconnect();
        return outputBuffer.toString();
    }

    @SneakyThrows
    private static void jschUpload(Session session, String remoteFilePath, String localFilePath) {
        Channel channel = session.openChannel("sftp");
        channel.connect();
        ChannelSftp sftpChannel = (ChannelSftp) channel;
        try {
            sftpChannel.rm(remoteFilePath);
        } catch (Exception e) {
            System.out.println("No need to remove file, it doesn't exist.");
        }
        sftpChannel.put(localFilePath, remoteFilePath);
        System.out.println("File uploaded successfully to host");
    }

    @SneakyThrows
    private static void jschDownload(Session session, String remoteFilePath, String localFilePath) {
        Channel channel = session.openChannel("sftp");
        channel.connect();
        ChannelSftp sftpChannel = (ChannelSftp) channel;
        sftpChannel.get(remoteFilePath, localFilePath);
        sftpChannel.exit();
    }


    @SneakyThrows
    private static Session getJschPasswordSession(String user, String host, int port, String password) {
        JSch jsch = new JSch();
        Session session = jsch.getSession(user, host, port);
        session.setPassword(password);
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        return session;
    }

    @SneakyThrows
    private static Session getJschKeySession(String user, String host, int port, String sshkeyPath) {
        JSch jsch = new JSch();
        Session session = jsch.getSession(user, host, port);
        jsch.addIdentity(sshkeyPath);
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        return session;
    }

    @SneakyThrows
    private static void jschExec(Session session, String command) {
        Channel channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand(command);
        channel.setInputStream(null);
        ((ChannelExec) channel).setErrStream(System.err);
        InputStream in = channel.getInputStream();
        channel.connect();

        byte[] tmp = new byte[1024];
        while (true) {
            while (in.available() > 0) {
                int i = in.read(tmp, 0, 1024);
                if (i < 0) {
                    break;
                }
                System.out.print(new String(tmp, 0, i));
            }
            if (channel.isClosed()) {
                if (in.available() > 0) {
                    continue;
                }
                break;
            }
            Thread.sleep(500);
        }
        channel.disconnect();
    }


    public static String generateRandomStr(int length, boolean needSpecialChar) {
        String baseCharSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        String charSet = baseCharSet;
        if (needSpecialChar) {
            charSet += "!@#$%";
        }
        final String finalCharSet = charSet;
        SecureRandom random = new SecureRandom();
        return IntStream.range(0, length)
                .map(i -> finalCharSet.charAt(random.nextInt(finalCharSet.length())))
                .mapToObj(c -> String.valueOf((char) c))
                .collect(Collectors.joining());
    }

    @Data
    private static class Wgcf {
        private Endpoint endpoint;
        private List<Integer> reservedDec;
        private String reservedHex;
        private String reservedStr;
        private String privateKey;
        private String publicKey;
        private String v4;
        private String v6;

        @Data
        public static class Endpoint {
            private String v4;
            private String v6;
        }
    }

    @Data
    private static class XrayConfig {
        private Log log;
        private List<Inbound> inbounds;
        private List<Outbound> outbounds;
        private Policy policy;
        private Routing routing;

        @Data
        private static class Log {
            private String access;
            private String error;
            private String loglevel;
        }

        @Data
        private static class Inbound {
            private String tag;
            private String listen;
            private int port;
            private String protocol;
            private Setting settings;
            private StreamSetting streamSettings;
            private Sniffing sniffing;

            @Data
            private static class Setting {
                private List<XrayClient> clients;
                private String decryption;
                private List<Fallback> fallbacks;

                @Data
                private static class XrayClient {
                    private String id;
                    private String flow;
                    private Integer level;
                    private String password;
                    private String email;
                }

                @Data
                private static class Fallback {
                    private Integer dest;
                    private Integer xver;
                    private String path;
                    private String alpn;
                }
            }

            @Data
            private static class StreamSetting {
                private String network;
                private String security;
                private TlsSetting tlsSettings;
                private TcpSetting tcpSettings;
                private WsSetting wsSettings;

                @Data
                private static class TlsSetting {
                    private boolean rejectUnknownSni;
                    private List<String> alpn;
                    private String minVersion;
                    private List<Certificate> certificates;

                    @Data
                    private static class Certificate {
                        private Integer ocspStapling;
                        private String certificateFile;
                        private String keyFile;
                    }
                }

                @Data
                private static class TcpSetting {
                    private Boolean acceptProxyProtocol;
                }

                @Data
                private static class WsSetting {
                    private Boolean acceptProxyProtocol;
                    private String path;
                }
            }

            @Data
            private static class Sniffing {
                private Boolean enabled;
                private List<String> destOverride;
            }
        }

        @Data
        private static class Outbound {
            private String tag;
            private String protocol;
            private Setting settings;

            @Data
            private static class Setting {
                private Boolean kernelMode;
                private String secretKey;
                private List<String> address;
                private List<Peer> peers;

                @Data
                private static class Peer {
                    private String publicKey;
                    private String endpoint;
                }

                private List<Integer> reserved;
            }
        }

        @Data
        private static class Policy {
            private Map<String, Level> levels;

            @Data
            private static class Level {
                private Integer handshake;
                private Integer connIdle;
            }
        }

        @Data
        private static class Routing {
            private String domainStrategy;
            private List<Rule> rules;

            @Data
            private static class Rule {
                private String type;
                private String outboundTag;
                private List<String> inboundTag;
                private List<String> domain;
                private List<String> ip;
            }
        }
    }
}
