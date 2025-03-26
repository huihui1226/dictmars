package com.ctsi.common.util;

import com.ctsi.core.common.util.ThrowableUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @version 1.0
 * @author: wang xiao xiang
 * @date: 2022/6/24 8:59
 */
@Slf4j
public class FileTypes {
    private static final Map<FileTypeName, String> FILE_TYPE_MAP = new HashMap<>();

    static {
        initAllFileType();
    }

    private FileTypes() {
    }

    public enum FileTypeName {
        JPG("jpg"),
        PNG("png"),
        GIF("gif");

        private String fileTypeName;

        FileTypeName(String fileTypeName) {
            this.fileTypeName = fileTypeName;
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }

    public static Boolean checkType(FileTypeName fileTypeName, MultipartFile file) {
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            byte[] bytes = readBytes(inputStream);
            String fileCode = bytesToHexString(bytes);
            String fileTypeHead = FILE_TYPE_MAP.get(fileTypeName);
            return fileCode.toUpperCase(Locale.ENGLISH).startsWith(fileTypeHead) || fileTypeHead.startsWith(fileCode.toUpperCase(Locale.ENGLISH));
        } catch (IOException e) {
            log.error(ThrowableUtil.getStackTrace(e));
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error(ThrowableUtil.getStackTrace(e));
                }
            }
        }
        return false;
    }

    public static String getType(MultipartFile file) {
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            byte[] bytes = readBytes(inputStream);
            String fileCode = bytesToHexString(bytes);
            for (Map.Entry<FileTypeName, String> next : FILE_TYPE_MAP.entrySet()) {
                String fileTypeHead = next.getValue();
                if (fileCode.toUpperCase(Locale.ENGLISH).startsWith(fileTypeHead) || fileTypeHead.startsWith(fileCode.toUpperCase(Locale.ENGLISH))) {
                    return next.getKey().toString();
                }
            }
        } catch (IOException e) {
            log.error(ThrowableUtil.getStackTrace(e));
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error(ThrowableUtil.getStackTrace(e));
                }
            }
        }
        return "-1";
    }

    public static byte[] readBytes(InputStream inputStream) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(10);
            int length;
            while ((length = inputStream.read()) != -1) {
                byteArrayOutputStream.write(length);
            }
            byte[] result = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();
            inputStream.close();
            return result;
        } catch (IOException ex) {
            log.error(ThrowableUtil.getStackTrace(ex));
            return null;
        }
    }

    private static void initAllFileType() {
        // JPEG (jpg)
        FILE_TYPE_MAP.put(FileTypeName.JPG, "FFD8FF");

        // PNG (png)
        FILE_TYPE_MAP.put(FileTypeName.PNG, "89504E47");

        // GIF (gif)
        FILE_TYPE_MAP.put(FileTypeName.GIF, "47494638");
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            int v = b & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}