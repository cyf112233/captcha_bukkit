package byd.cxkcxkckx.captcha.func;

import org.bukkit.configuration.file.FileConfiguration;
import byd.cxkcxkckx.captcha.captcha;

public class Config {
    private static int maxAttempts = 3;
    private static int banDuration = 300; // 5分钟，单位：秒
    private static int answerMin = 2;
    private static int answerMax = 10;

    public static void loadConfig(captcha plugin) {
        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();
        
        maxAttempts = config.getInt("max-attempts", 3);
        banDuration = config.getInt("ban-duration", 300);
        answerMin = config.getInt("answer-min", 2);
        answerMax = config.getInt("answer-max", 10);
        
        // 确保最小值不大于最大值
        if (answerMin > answerMax) {
            int temp = answerMin;
            answerMin = answerMax;
            answerMax = temp;
        }
    }

    public static int getMaxAttempts() {
        return maxAttempts;
    }

    public static int getBanDuration() {
        return banDuration;
    }

    public static int getAnswerMin() {
        return answerMin;
    }

    public static int getAnswerMax() {
        return answerMax;
    }
} 