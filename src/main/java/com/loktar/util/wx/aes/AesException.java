package com.loktar.util.wx.aes;


public class AesException extends Exception {

    public final static int OK = 0;
    public final static int ValidateSignatureError = -40001;
    public final static int ParseXmlError = -40002;
    public final static int ComputeSignatureError = -40003;
    public final static int IllegalAesKey = -40004;
    public final static int ValidateCorpidError = -40005;
    public final static int EncryptAESError = -40006;
    public final static int DecryptAESError = -40007;
    public final static int IllegalBuffer = -40008;

    private static String getMessage(int code) {
        return switch (code) {
            case ValidateSignatureError -> "签名验证错误";
            case ParseXmlError -> "xml解析失败";
            case ComputeSignatureError -> "sha加密生成签名失败";
            case IllegalAesKey -> "SymmetricKey非法";
            case ValidateCorpidError -> "corpid校验失败";
            case EncryptAESError -> "aes加密失败";
            case DecryptAESError -> "aes解密失败";
            case IllegalBuffer -> "解密后得到的buffer非法";
//		case EncodeBase64Error:
//			return "base64加密错误";
//		case DecodeBase64Error:
//			return "base64解密错误";
//		case GenReturnXmlError:
//			return "xml生成失败";
            default -> null;
        };
    }

    AesException(int code) {
        super(getMessage(code));
    }

}
