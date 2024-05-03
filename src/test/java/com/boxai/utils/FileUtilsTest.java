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

    @Test
    void readFile() {
    }

    @Test
    void readFiles() {
    }

    @Test
    void extractFlagsContent() {
    }

    @Test
    void codeExtractor() {
        String input = "代码注释：\n" +
                "```adadada\n" +
                "package com.ybu.common; // 定义包名\n" +
                "\n" +
                "import lombok.Getter; // 导入lombok注解，用于生成getter方法\n" +
                "\n" +
                "/**\n" +
                " * 业务异常类\n" +
                " * \n" +
                " * @Titile: ServiceException\n" +
                " * @Author: Lucky\n" +
                " * @Description: 业务异常\n" +
                " */\n" +
                "public class ServiceException extends RuntimeException { // 继承RuntimeException，创建自定义异常类\n" +
                "    @Getter  // 使用lombok注解生成serviceCode的getter方法\n" +
                "    private ServiceCode serviceCode; // 定义serviceCode属性，用于存储服务异常码\n" +
                "\n" +
                "    // 构造函数，接收服务异常码和服务异常信息\n" +
                "    public ServiceException(ServiceCode serviceCode, String message) {\n" +
                "        super(message); // 调用父类RuntimeException的构造方法，传入异常信息\n" +
                "        this.serviceCode = serviceCode; // 设置服务异常码\n" +
                "    }\n" +
                "}\n" +
                "```";
        System.out.println(FileUtils.codeExtractor(input));
    }
}