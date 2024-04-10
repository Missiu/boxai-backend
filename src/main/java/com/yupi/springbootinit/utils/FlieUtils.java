package com.yupi.springbootinit.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

//文件读取
public class FlieUtils {
    public static String ReadFiles(MultipartFile multipartFile) {
//        // 带缓冲的流读取，默认缓冲区8k
//        //java 8中这样写也可以
//        try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName))){
//            String line;
//            while ((line = br.readLine()) != null) {
//                System.out.println(line);
//            }
//        }
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
                System.out.println(content);

            }
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(content);
        return content;
    }
}
