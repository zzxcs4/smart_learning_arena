package com.zzx.exam.utils;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

/**
 * 邀请码生成工具类
 * 生成格式示例：A3B9CX（6位，大写字母+数字）
 */
public class InviteCodeGenerator {

    // 定义邀请码字符源（排除0/O、1/I，避免混淆）
    private static final String CHARACTERS = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";
    // 邀请码长度（可根据需求调整）
    private static final int CODE_LENGTH = 6;
    // 安全随机数生成器（比Random更安全）
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * 生成单个邀请码
     * @return 6位邀请码（如A3B9CX）
     */
    public static String generateInviteCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        int charLength = CHARACTERS.length();
        for (int i = 0; i < CODE_LENGTH; i++) {
            // 随机获取字符源中的索引
            int randomIndex = RANDOM.nextInt(charLength);
            // 拼接字符
            sb.append(CHARACTERS.charAt(randomIndex));
        }
        return sb.toString();
    }

    /**
     * 批量生成不重复的邀请码
     * @param count 生成数量
     * @return 不重复的邀请码集合
     */
    public static Set<String> generateBatchInviteCode(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("生成数量必须大于0");
        }
        // 用Set保证唯一性
        Set<String> codeSet = new HashSet<>();
        while (codeSet.size() < count) {
            codeSet.add(generateInviteCode());
        }
        return codeSet;
    }

    // 测试方法
    public static void main(String[] args) {
        // 生成单个邀请码
        String singleCode = generateInviteCode();
        System.out.println("单个邀请码：" + singleCode);

        // 批量生成10个不重复的邀请码
        Set<String> batchCodes = generateBatchInviteCode(10);
        System.out.println("\n批量邀请码（10个）：");
        batchCodes.forEach(code -> System.out.println(code));
    }
}