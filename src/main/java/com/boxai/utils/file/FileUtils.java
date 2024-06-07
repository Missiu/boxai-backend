package com.boxai.utils.file;

import com.boxai.exception.customize.CustomizeFileException;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static com.boxai.utils.dataclean.DataClean.cleanFileContent;

// 文件读取工具类
public class FileUtils {
    public static String readFile(MultipartFile multipartFile) {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(multipartFile.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder content = new StringBuilder();
            char[] buffer = new char[1024]; // 1KB 缓冲区
            int bytesRead;
            while ((bytesRead = bufferedReader.read(buffer)) != -1) {
                content.append(buffer, 0, bytesRead);
            }
            return cleanFileContent(content.toString());
        } catch (IOException e) {
            throw new CustomizeFileException();
        }
    }
    public static String readMultipleFiles(MultipartFile[] files) {
        StringBuilder sb = new StringBuilder();
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename(); // 获取文件名
            String content = readFile(file); // 读取文件内容
            sb.append("文件名称为： ").append(fileName).append("\n"); // 文件名
            sb.append("文件内容为：").append(content).append("\n"); // 文件内容
        }
        return sb.toString();
    }
}
