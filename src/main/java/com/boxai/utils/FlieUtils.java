package com.boxai.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

//文件读取
public class FlieUtils {
    public static String readFile(MultipartFile multipartFile) {
        String content = null;
        if (multipartFile.isEmpty()) return null;
        InputStream inputStream = null;
        try {
            inputStream = multipartFile.getInputStream();
            int len;
            byte[] buffer = new byte[8192];
            while ((len = inputStream.read(buffer)) != -1){
                // 将读取的字节转换为字符串，并输出到控制台
                content = new String(buffer, 0, len, "UTF-8");
//                System.out.println(content);

            }
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        System.out.println(content);
        return content;
    }

    public static String readFiles(MultipartFile[] files) {
        StringBuffer sb = new StringBuffer();
//        String content = null;
        for(MultipartFile file : files){
            String fileName = file.getOriginalFilename(); // 获取文件名
            String content = readFile(file); // 读取文件内容
            sb.append("\n").append("文件名称为： ").append(fileName).append("\n"); // 文件路径或文件名
            sb.append("文件内容为：").append(content).append("\n"); // 文件内容
        }
//        System.out.println(content);
        return sb.toString();
    }
}
