package com.ctsi.common.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class RSAUtils {
    private static final String DEFAULT_CHARSET_NAME = "UTF-8";

    public static final String KEY_ALGORITHM = "RSA";

    public static final String TRANSFORMATION_NAME = "RSA/ECB/PKCS1Padding";

    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    private static final String PUBLIC_KEY = "RSAPublicKey";

    private static final String PRIVATE_KEY = "RSAPrivateKey";

    private static final int MAX_ENCRYPT_BLOCK = 117;

    private static final int MAX_DECRYPT_BLOCK = 128;

    public static Map<String, Object> genKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey)keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey)keyPair.getPrivate();
        Map<String, Object> keyMap = new HashMap<>(2);
        keyMap.put("RSAPublicKey", publicKey);
        keyMap.put("RSAPrivateKey", privateKey);
        return keyMap;
    }

//    public static String sign(byte[] data, String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
//        byte[] keyBytes = Base64.getDecoder().decode(privateKey);
//        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
//        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//        PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
//        Signature signature = Signature.getInstance("MD5withRSA");
//        signature.initSign(privateK);
//        signature.update(data);
//        return Base64.getEncoder().encodeToString(signature.sign());
//    }
//
//    public static boolean verify(byte[] data, String publicKey, String sign) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
//        byte[] keyBytes = Base64.getDecoder().decode(publicKey);
//        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
//        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//        PublicKey publicK = keyFactory.generatePublic(keySpec);
//        Signature signature = Signature.getInstance("MD5withRSA");
//        signature.initVerify(publicK);
//        signature.update(data);
//        return signature.verify(Base64.getDecoder().decode(sign));
//    }

    public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
        byte[] keyBytes = Base64.getDecoder().decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        keyFactory.getAlgorithm();
//        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        int i = 0;
        while (inputLen - offSet > 0) {
            byte[] cache;
            if (inputLen - offSet > 128) {
                cache = cipher.doFinal(encryptedData, offSet, 128);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * 128;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    public static String decryptByPrivateKeyStr(String encryptedDataStr, String privateKey) throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
        byte[] encryptedData = hexStr2ByteArr(encryptedDataStr);
        String str = new String(decryptByPrivateKey(encryptedData, privateKey), "UTF-8");
        return str;
    }

//    public static byte[] decryptByPublicKey(byte[] encryptedData, String publicKey) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
//        byte[] keyBytes = Base64.getDecoder().decode(publicKey);
//        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
//        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//        Key publicK = keyFactory.generatePublic(x509KeySpec);
//        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
//        cipher.init(Cipher.DECRYPT_MODE, publicK);
//        int inputLen = encryptedData.length;
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        int offSet = 0;
//        int i = 0;
//        while (inputLen - offSet > 0) {
//            byte[] cache;
//            if (inputLen - offSet > 128) {
//                cache = cipher.doFinal(encryptedData, offSet, 128);
//            } else {
//                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
//            }
//            out.write(cache, 0, cache.length);
//            i++;
//            offSet = i * 128;
//        }
//        byte[] decryptedData = out.toByteArray();
//        out.close();
//        return decryptedData;
//    }

//    public static String decryptByPublicKeyStr(String encryptedDataStr, String publicKey) throws InvalidKeyException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
//        byte[] encryptedData = hexStr2ByteArr(encryptedDataStr);
//        String str = new String(decryptByPublicKey(encryptedData, publicKey), "UTF-8");
//        return str;
//    }
//
//    public static byte[] encryptByPublicKey(byte[] data, String publicKey) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
//        byte[] keyBytes = Base64.getDecoder().decode(publicKey);
//        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
//        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//        Key publicK = keyFactory.generatePublic(x509KeySpec);
//        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
//        cipher.init(Cipher.ENCRYPT_MODE, publicK);
//        int inputLen = data.length;
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        int offSet = 0;
//        int i = 0;
//        while (inputLen - offSet > 0) {
//            byte[] cache;
//            if (inputLen - offSet > 117) {
//                cache = cipher.doFinal(data, offSet, 117);
//            } else {
//                cache = cipher.doFinal(data, offSet, inputLen - offSet);
//            }
//            out.write(cache, 0, cache.length);
//            i++;
//            offSet = i * 117;
//        }
//        byte[] encryptedData = out.toByteArray();
//        out.close();
//        return encryptedData;
//    }
//
//    public static String encryptByPublicKeyStr(String dataStr, String publicKey) throws InvalidKeyException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
//        byte[] data = dataStr.getBytes("UTF-8");
//        String encryptedDateStr = byteArr2HexStr(encryptByPublicKey(data, publicKey));
//        return encryptedDateStr;
//    }
//
//    public static byte[] encryptByPrivateKey(byte[] data, String privateKey) throws IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException {
//        byte[] keyBytes = Base64.getDecoder().decode(privateKey);
//        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
//        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
//        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
//        cipher.init(Cipher.ENCRYPT_MODE, privateK);
//        int inputLen = data.length;
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        int offSet = 0;
//        int i = 0;
//        while (inputLen - offSet > 0) {
//            byte[] cache;
//            if (inputLen - offSet > 117) {
//                cache = cipher.doFinal(data, offSet, 117);
//            } else {
//                cache = cipher.doFinal(data, offSet, inputLen - offSet);
//            }
//            out.write(cache, 0, cache.length);
//            i++;
//            offSet = i * 117;
//        }
//        byte[] encryptedData = out.toByteArray();
//        out.close();
//        return encryptedData;
//    }
//
//    public static String encryptByPrivateKeyStr(String dataStr, String privateKey) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, IOException {
//        byte[] data = dataStr.getBytes("UTF-8");
//        String encryptedDateStr = byteArr2HexStr(encryptByPrivateKey(data, privateKey));
//        return encryptedDateStr;
//    }
//
//    public static String getPrivateKey(Map<String, Object> keyMap) {
//        Key key = (Key)keyMap.get("RSAPrivateKey");
//        return Base64.getEncoder().encodeToString(key.getEncoded());
//    }
//
//    public static String getPublicKey(Map<String, Object> keyMap) {
//        Key key = (Key)keyMap.get("RSAPublicKey");
//        return Base64.getEncoder().encodeToString(key.getEncoded());
//    }
//
    public static byte[] hexStr2ByteArr(String strIn) throws UnsupportedEncodingException {
        byte[] arrB = strIn.getBytes();
        int iLen = arrB.length;
        byte[] arrOut = new byte[iLen / 2];
        int i;
        for (i = 0; i < iLen; i += 2) {
            String strTmp = new String(arrB, i, 2, "UTF-8");
            arrOut[i / 2] = (byte)Integer.parseInt(strTmp, 16);
        }
        return arrOut;
    }
//
//    public static String byteArr2HexStr(byte[] arrB) {
//        int iLen = arrB.length;
//        StringBuffer sb = new StringBuffer(iLen * 2);
//        for (int i = 0; i < iLen; i++) {
//            int intTmp = arrB[i];
//            while (intTmp < 0)
//                intTmp += 256;
//            if (intTmp < 16)
//                sb.append("0");
//            sb.append(Integer.toString(intTmp, 16));
//        }
//        String result = sb.toString();
//        return result;
//    }
}

