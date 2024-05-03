package com.boxai.utils;

import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// 文件读取工具类
public class FileUtils {
    /**
     * 读取单一文件内容
     * @param multipartFile 需要读取的文件
     * @return 文件内容字符串
     * @throws IOException 当读取文件时发生错误
     * @throws MultipartException 当文件为空时抛出
     */
    public static String readFile(MultipartFile multipartFile) throws IOException, MultipartException {
        if (multipartFile.isEmpty()) {
            throw new MultipartException("文件不能为空");
        }
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(multipartFile.getInputStream(), StandardCharsets.UTF_8))) {
            // 目前单线程读取文件，多线程建议StringBuffer
            StringBuilder content = new StringBuilder();
            // 逐行读取文件内容
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString();
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException("处理文件时发生意外错误", e);
        }
    }

    /**
     * 读取多个文件内容
     * @param files 需要读取的文件数组
     * @return 包含所有文件内容的字符串
     */
    public static String readFiles(MultipartFile[] files) {
        StringBuilder sb = new StringBuilder();
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename(); // 获取文件名
            try {
                String content = readFile(file); // 读取文件内容
                sb.append("文件名称为： ").append(fileName).append("\n"); // 文件名
                sb.append("文件内容为：").append(content).append("\n"); // 文件内容
            } catch (IOException | MultipartException e) {
                sb.append("文件名称为： ").append(fileName).append("\n")
                        .append("读取文件失败：").append(e.getMessage()).append("\n");
            }
        }
        return sb.toString();
    }


    /**
     * 从给定的文本中提取所有位于"【【标志】】"之间的内容。
     *
     * @param text 包含一个或多个"【【标志】】"标记的原始文本。
     * @return 包含所有提取内容的字符串列表，每个字符串代表一个"【【标志】】"标记间的内容。
     */
    public static List<String> extractFlagsContent(String text) {
        // 编译正则表达式，用于匹配"【【标志】】"之间的内容
        Pattern pattern = Pattern.compile("(?s)【【标志】】(.*?)(?=【【标志】】|$)");
        Matcher matcher = pattern.matcher(text);
        List<String> contents = new ArrayList<>();
        // 遍历文本，找到所有匹配项并添加到列表中
        while (matcher.find()) {
            String extractedContent = matcher.group(1); // 提取匹配项中的具体内容
            contents.add(extractedContent);
        }
        return contents;
    }

    public static String codeExtractor(String code) {
        Pattern pattern = Pattern.compile("```[a-z]*\\s*([\\s\\S]*?)\\s*```");
        Matcher matcher = pattern.matcher(code);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }
}
