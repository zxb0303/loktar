package com.loktar.conf;

public class LokTarConstant {
    public static final String HTTP_HEADER_USER_AGENT_NAME = "User-Agent";
    public static final String HTTP_HEADER_USER_AGENT_VALUE = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36";
    public static final String HTTP_HEADER_ACCEPT_NAME = "Accept";
    public static final String HTTP_HEADER_ACCEPT_VALUE_JSON = "application/json";
    public static final String HTTP_HEADER_ACCEPT_VALUE_HTML = "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9";
    public static final String HTTP_HEADER_CONTENT_TYPE_NAME = "Content-type";
    public static final String HTTP_HEADER_CONTENT_TYPE_VALUE_JSON = "application/json; charset=utf-8";
    public static final String HTTP_HEADER_CONTENT_TYPE_VALUE_FORM = "application/x-www-form-urlencoded; charset=UTF-8";
    public static final String HTTP_HEADER_CONTENT_TYPE_VALUE_HTML = "text/html; charset=UTF-8";
    public static final String HTTP_HEADER_CONTENT_TYPE_VALUE_MULTIPART = "multipart/form-data; boundary=";
    public static final String HTTP_HEADER_CONTENT_TYPE_VALUE_MULTIPART_PREFIX = "--------------------------";
    public static final String HTTP_HEADER_ACCEPT_LANGUAGE_NAME = "Accept-Language";
    public static final String HTTP_HEADER_ACCEPT_LANGUAGE_VALUE_CN = "zh-CN,zh;q=0.9,en;q=0.8";
    public static final String HTTP_HEADER_ACCEPT_ENCODING_NAME = "Accept-Encoding";
    public static final String HTTP_HEADER_ACCEPT_ENCODING_VALUE_GZIP = "gzip, deflate, br, zstd";
    public static final String HTTP_HEADER_REFERER = "Referer";
    public static final String HTTP_HEADER_CONTENT_ENCODING_NAME = "Content-Encoding";
    public static final String HTTP_HEADER_CONTENT_ENCODING_VALUE_GZIP = "gzip";
    public static final String HTTP_HEADER_COOKIE_NAME = "Cookie";


    public static final String REDIS_KEY_PREFIX_OPENAI_REQUEST = "openai_request_";
    public static final String REDIS_KEY_JELLYFIN_REMOTE_PLAYING_SET = "jellyfin_remote_playing_set";
    public static final String REDIS_KEY_NEWHOUSE_COOKIE = "newhouse_cookie";
    public static final String REDIS_KEY_TRANSMISSION_SESSIONID = "transmission_sessionid";
    public static final String REDIS_KEY_QYWX_PATENT_MSG_TASK_LOCK = "qywx_patent_msg_task";
    public static final String REDIS_KEY_PATENT_MONITOR_SWITCH = "qywx_patent_monitor_switch";
    public static final String REDIS_KEY_PATENT_MONITOR_COUNT = "qywx_patent_monitor_count";


    public final static String NOTICE_TITLE_GITHUB = "GitHub项目更新通知";
    public final static String NOTICE_TITLE_LOTTERY = "杭州新房摇号通知";
    public final static String NOTICE_TITLE_IP = "家庭宽带IP通知";
    public final static String NOTICE_TITLE_CAR_VERSION = "沃尔沃车机系统更新通知";
    public final static String NOTICE_TITLE_WORK = "干活通知";
    public final static String NOTICE_TITLE_CONTRACT = "合作合同到期通知";
    public final static String NOTICE_TITLE_EMPLOYEE = "劳动合同到期通知";
    public final static String NOTICE_CERT_UPDATE = "证书更新通知";
    public final static String NOTICE_JELLYFIN = "Jellyfin常规通知";
    public final static String NOTICE_JELLYFIN_START = "Jellyfin开始播放通知";
    public final static String NOTICE_JELLYFIN_STOP = "Jellyfin停止播放通知";

    public final static String WX_RECEIVE_MSGTYPE = "MsgType";

    public final static String JELLYFIN_NOT_NOTIFY = "adult";
    public final static String VOICE_SUFFIX_WAV = ".wav";
    public final static String VOICE_SUFFIX_AMR = ".amr";

    public final static String PIC_SUFFIX_PNG = ".png";
    public final static String PIC_SUFFIX_JPG = ".jpg";

    public final static String PIC_TYPE_JPG = "jpg";

    public final static String AZURE_DOCINTELLIGENCE_MODEL_ID = "newhouse";

    public final static String ENV_PRO = "pro";

    public final static String QYWX_PATENT_MSG_STATUS_RECEIVED = "00";
    public final static String QYWX_PATENT_MSG_STATUS_CREATED = "01";
    public final static String QYWX_PATENT_MSG_STATUS_SENDED = "02";

    public final static String QYWX_PATENT_MSG_TYPE_QUOTATION = "01";
    public final static String QYWX_PATENT_MSG_TYPE_CONTRACT = "02";

    public final static String PATENT_DEFAULT_PRICE = "650";
    public final static String PATENT_NOTICE_MSG_QUOTATION = "正在生成【{0}】报价单，单价：{1}，请稍等...";
    public final static String PATENT_NOTICE_MSG_CONTRACT = "正在生成【{0}】合同及协议，总价：{1}，请稍等...";

    public final static String PATENT_QUOTATION_FILE_PATH = "quotation/{0}.xlsx";

    public final static String PATENT_CONTRACT_FILE_PATH = "contract/收购合同-{0}.doc";
    public final static String PATENT_AGREEMENT_FILE_PATH = "contract/转让协议-{0}.doc";


}
