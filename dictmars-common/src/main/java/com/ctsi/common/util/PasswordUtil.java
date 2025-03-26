package com.ctsi.common.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.AES;
import lombok.experimental.UtilityClass;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;

/**
 * 密码生成工具类
 *
 * @version 1.0
 * @author: wang xiao xiang
 * @date: 2021/8/9 16:11
 */
@UtilityClass
public class PasswordUtil {

    public final PasswordEncoder ENCODER = new BCryptPasswordEncoder();

    /**
     * 将前端传递过来的密文解密为明文
     * @param aesPass AES加密后的密文
     * @param secretKey 密钥
     * @return 明文密码
     */
//    public static byte[] decodeAes(String aesPass, String secretKey) {
//        byte[] secretKeyBytes = secretKey.getBytes();
//        AES aes = new AES(Mode.CBC, Padding.PKCS5Padding, secretKeyBytes, secretKeyBytes);
//        byte[] result = aes.decrypt(Base64.decode(aesPass.getBytes(StandardCharsets.UTF_8)));
//        // 删除byte数组中补位产生的\u0000, 否则密码校验时会有问题
//        String password = new String(result, StandardCharsets.UTF_8).replaceAll("[\u0000]", "");
//        byte[] res = password.getBytes(StandardCharsets.UTF_8);
//        Arrays.fill(password.getBytes(), (byte)' ');
//        return res;
//
//    }

    public static String decodeAesAndEncode(String aesPass, String secretKey) {
        byte[] secretKeyBytes = secretKey.getBytes();
        AES aes = new AES(Mode.CBC, Padding.PKCS5Padding, secretKeyBytes, secretKeyBytes);
        byte[] result = aes.decrypt(Base64.decode(aesPass.getBytes(StandardCharsets.UTF_8)));
        return ENCODER.encode(CryptoUtil.encodeMD5(result));
    }

    /**
     * 将明文密码加密为密文
     * @param password 明文密码
     * @param secretKey 密钥
     * @return AES加密后的密文
     */
    public static String encodeAesBase64(String password, String secretKey) {
        byte[] secretKeyBytes = secretKey.getBytes();
        AES aes = new AES(Mode.CBC, Padding.PKCS5Padding, secretKeyBytes, secretKeyBytes);
        return aes.encryptBase64(password, StandardCharsets.UTF_8);
    }

    /**
     * 使用BCrypt加密密码
     * @param password 明文密码
     * @return BCrypt加密后的密码
     */
    public static String encodeBcrypt(String password) {
        return ENCODER.encode(password);
    }

}