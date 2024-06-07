package com.boxai.utils.encrypt;

import org.bouncycastle.crypto.generators.BCrypt;
import org.bouncycastle.util.encoders.Hex;

import java.security.SecureRandom;

/**
 * 密码加密工具类。
 * 提供加密密码的方法，使用BCrypt算法生成密码的加密版本。
 *
 */
public class EncryptPasswordUtil {

    // 成本因子,是一个2的指数，它决定了计算哈希所需的迭代次数
    private static final int BCRYPT_COST = 4;

    /**
     * 加密密码。
     * 使用BCrypt算法生成一个密码的加密版本。
     * 盐值和加密后的哈希密码以字符串形式返回，格式为“盐值:哈希密码”。
     *
     * @param rawPassword 需要加密的原始密码。
     * @return 加密后的密码字符串，格式为“盐值:哈希密码”。
     */
    public static String encryptPassword(String rawPassword) {
        // 生成一个随机的盐值
        byte[] salt = generateSalt();
        // 使用BCrypt算法，以指定的计算成本加密密码
        byte[] hash = BCrypt.generate(rawPassword.getBytes(), salt, BCRYPT_COST);
        // 将盐值和加密后的哈希密码转换为字符串，以“:”分隔，返回结果
        return Hex.toHexString(salt) + ":" + Hex.toHexString(hash);
    }

    /**
     * 验证提供的原始密码是否与存储的密码匹配。
     *
     * @param rawPassword    用户输入的原始密码。
     * @param storedPassword 存储的密码，格式为 salt:hash，其中salt是用于加密的盐值，hash是加密后的密码。
     * @return 如果原始密码与存储的密码匹配，则返回true；否则返回false。
     */
    public static boolean matchesPassword(String rawPassword, String storedPassword) {
        // 分解存储的密码字符串，获取盐值和加密后的密码
        String[] parts = storedPassword.split(":");
        byte[] salt = Hex.decode(parts[0]); // 解码盐值
        byte[] hash = Hex.decode(parts[1]); // 解码加密后的密码

        // 使用BCrypt算法，根据提供的原始密码和解码的盐值生成新的加密密码
        byte[] newHash = BCrypt.generate(rawPassword.getBytes(), salt, BCRYPT_COST);

        // 使用常量时间数组比较方法，安全地比较新生成的加密密码与存储的加密密码
        return constantTimeArrayComparison(hash, newHash);
    }

    /**
     * 生成一个随机的盐值数组。
     * <p>
     * 盐值是用来加强密码安全性的，通过为每个密码生成一个随机的盐值，使得攻击者不能通过预计算的哈希表（如彩虹表）来破解密码。
     * 每次生成的盐值长度为16字节。
     *
     * @return 一个长度为16字节的随机盐值数组。
     */
    private static byte[] generateSalt() {
        // 使用安全随机数生成器
        SecureRandom random = new SecureRandom();
        // 盐值数组
        byte[] salt = new byte[16];
        // 为盐值数组填充随机字节
        random.nextBytes(salt);
        return salt;
    }

    /**
     * 以常量时间比较两个字节数组是否相等。
     * <p>
     * 这个方法用于比较敏感信息（如密码哈希）时，防止时序攻击。时序攻击是一种通过分析算法执行时间来获取信息的攻击方式。
     * 该方法无论两个数组是否相等，都会遍历完所有元素，且仅使用位运算来检查两个数组是否完全相等。
     *
     * @param a 第一个要比较的字节数组。
     * @param b 第二个要比较的字节数组。
     * @return 如果两个数组完全相等，则返回true；否则返回false。
     */
    private static boolean constantTimeArrayComparison(byte[] a, byte[] b) {
        // 如果数组长度不相等，直接返回false
        if (a.length != b.length) return false;

        int result = 0;
        // 通过位异或操作比较每个元素，并将结果累积到result中
        for (int i = 0; i < a.length; i++) {
            result |= a[i] ^ b[i];
        }
        // 如果result为0，则表示两个数组完全相等
        return result == 0;
    }

}
