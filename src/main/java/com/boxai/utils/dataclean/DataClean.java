package com.boxai.utils.dataclean;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.boxai.common.constants.DataCleanConstant.*;

/**
 * 数据清洗工具类
 */
@Slf4j
public class DataClean {


    /**
     * 提取标记内容
     *
     * @param text 需要处理的文字数据
     * @return 处理后的文字数据
     */
    public static List<String> extractFlagsContent(String text) {
        // 编译正则表达式，用于匹配"【】"之间的内容
        Matcher matcher = Pattern.compile(GET_FLAG_CONTENT).matcher(text);
        List<String> contents = new ArrayList<>();
        while (matcher.find()) {
            String extractedContent = matcher.group(1); // 提取匹配项中的具体内容
            contents.add(extractedContent);
        }
        return contents;
    }

    /**
     * 提取代码
     *
     * @param code 需要处理的文字数据
     * @return 处理后的文字数据
     */
    public static String extractCodeContent(String code) {
        Matcher matcher = Pattern.compile(GET_CODE_CONTENT).matcher(code);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            log.error("extractCodeContent error");
            return code;
        }
    }

    /**
     * 清洗数据，去除多余的import语句和空行
     *
     * @param fileContent 需要处理的文字数据
     * @return 处理后的文字数据
     */
    public static String cleanFileContent(String fileContent) {
        return removeExtraNewLines(removeImports(fileContent));
    }

    /**
     * 去除多余的import语句
     *
     * @param fileContent 需要处理的文字数据
     * @return 处理后的文字数据
     */
    public static String removeImports(String fileContent) {
        // 正则表达式匹配 import 语句
        Matcher matcher = Pattern.compile(REMOVE_EXTRA_IMPORT).matcher(fileContent);
        if (matcher.find()) {
            return matcher.replaceAll("");
        } else {
            log.error("extractCodeContent error");
            return fileContent;
        }
    }

    /**
     * 去除多余的空行
     *
     * @param fileContent 需要处理的文字数据
     * @return 处理后的文字数据
     */
    public static String removeExtraNewLines(String fileContent) {
        // 正则表达式匹配 多余的空行
        Matcher matcher = Pattern.compile(REMOVE_EXTRA_BLANK_LINE).matcher(fileContent);
        if (matcher.find()) {
            return matcher.replaceAll("\n");
        } else {
            log.error("removeExtraNewLines error ");
            return fileContent;
        }
    }

    /**
     * 提取数字
     *
     * @param token 需要处理的文字数据
     * @return 处理后的文字数据
     */
    public static String extractNumbersContent(String token) {
        String numbers = token.replaceAll(MATCH_NUMBER, "");
        if (numbers.isEmpty()) {
            log.error("extractNumbers error");
            return "0.0";
        }
        return numbers;
    }
}
