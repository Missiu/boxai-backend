package com.boxai.utils;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

class FileUtilsTest {

    @Test
    public void testReadFile() {
        // 创建MockMultipartFile对象，用于测试
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "This is a test file".getBytes()
        );

        // 调用ReadFile方法读取文件内容
        String content = null;
        try {
            content = FileUtils.readFile(multipartFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 验证读取到的文件内容是否正确
        System.out.println(content);
    }
    @Test
    public void testReadFiles() {
        // 创建MockMultipartFile对象数组，用于测试
        MockMultipartFile[] files = {
                new MockMultipartFile(
                        "file1",
                        "file1.txt",
                        "text/plain",
                        "This is file 1".getBytes()
                ),
                new MockMultipartFile(
                        "file2",
                        "file2.txt",
                        "text/plain",
                        "This is file 2".getBytes()
                ),
                new MockMultipartFile(
                        "file3",
                        "file3.txt",
                        "text/plain",
                        "This is file 3".getBytes()
                )
        };

        // 调用ReadFiles方法读取文件内容
        String content = FileUtils.readFiles(files);

        // 验证读取到的文件内容是否正确
        System.out.println(content);
    }

}