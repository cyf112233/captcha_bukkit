package byd.cxkcxkckx.captcha.func;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class MathProblem {
    private static final Random random = new Random();
    private static final Map<String, Problem> playerProblems = new HashMap<>();
    private static final Map<String, Integer> playerAttempts = new HashMap<>();
    private static final Map<String, Integer> playerErrorCounts = new HashMap<>();
    private static final Map<String, Long> bannedIPs = new HashMap<>();
    private static Map<String, String> numberReplacements = new HashMap<>();
    private static Map<String, String> operatorReplacements = new HashMap<>();
    private static boolean enableTextReplacement = true;
    private static int replacementProbability = 50;
    private static List<String> noiseCharacters;
    private static int noiseProbability = 30;
    private static int maxTotalNoise = 10;
    private static int maxSpaces = 10;
    private static int maxPrefixNoise = 5;
    private static int maxSuffixNoise = 5;

    public static void loadConfig(FileConfiguration config) {
        // 加载数字替换配置
        if (config.contains("number-replacements")) {
            for (String key : config.getConfigurationSection("number-replacements").getKeys(false)) {
                numberReplacements.put(key, config.getString("number-replacements." + key));
            }
        }

        // 加载运算符替换配置
        if (config.contains("operator-replacements")) {
            for (String key : config.getConfigurationSection("operator-replacements").getKeys(false)) {
                operatorReplacements.put(key, config.getString("operator-replacements." + key));
            }
        }

        // 加载是否启用文字替换
        enableTextReplacement = config.getBoolean("enable-text-replacement", true);
        
        // 加载替换概率
        replacementProbability = config.getInt("replacement-probability", 50);
        
        // 加载干扰字符配置
        noiseCharacters = config.getStringList("noise-characters");
        noiseProbability = config.getInt("noise-probability", 30);
        maxTotalNoise = config.getInt("max-total-noise", 10);
        maxSpaces = config.getInt("max-spaces", 10);
        maxPrefixNoise = config.getInt("max-prefix-noise", 5);
        maxSuffixNoise = config.getInt("max-suffix-noise", 5);
    }

    private static String addRandomSpaces() {
        StringBuilder spaces = new StringBuilder();
        int spaceCount = random.nextInt(maxSpaces) + 1;
        for (int i = 0; i < spaceCount; i++) {
            spaces.append(" ");
        }
        return spaces.toString();
    }

    private static String addNoise(String text) {
        StringBuilder result = new StringBuilder();
        
        // 随机决定是否添加干扰字符
        if (random.nextInt(100) < noiseProbability) {
            // 随机决定总干扰字符数
            int totalNoise = random.nextInt(maxTotalNoise) + 1;
            
            // 随机分配干扰字符到前后
            int prefixNoise = random.nextInt(totalNoise + 1);
            int suffixNoise = totalNoise - prefixNoise;
            
            // 在算式前添加连续的干扰字符
            StringBuilder prefix = new StringBuilder();
            for (int i = 0; i < prefixNoise; i++) {
                if (!noiseCharacters.isEmpty()) {
                    prefix.append(noiseCharacters.get(random.nextInt(noiseCharacters.size())));
                }
            }
            if (prefix.length() > 0) {
                result.append(prefix);
                result.append(addRandomSpaces());
            }
        }
        
        // 添加算式
        result.append(text);
        
        // 在算式后添加干扰字符
        if (random.nextInt(100) < noiseProbability) {
            // 随机决定总干扰字符数
            int totalNoise = random.nextInt(maxTotalNoise) + 1;
            
            // 随机分配干扰字符到前后
            int prefixNoise = random.nextInt(totalNoise + 1);
            int suffixNoise = totalNoise - prefixNoise;
            
            // 在算式后添加连续的干扰字符
            StringBuilder suffix = new StringBuilder();
            for (int i = 0; i < suffixNoise; i++) {
                if (!noiseCharacters.isEmpty()) {
                    suffix.append(noiseCharacters.get(random.nextInt(noiseCharacters.size())));
                }
            }
            if (suffix.length() > 0) {
                result.append(addRandomSpaces());
                result.append(suffix);
            }
        }
        
        return result.toString();
    }

    private static String addRandomSpaces(String text) {
        StringBuilder result = new StringBuilder();
        String[] parts = text.split(" ");
        
        for (int i = 0; i < parts.length; i++) {
            result.append(parts[i]);
            if (i < parts.length - 1) {
                // 在运算符前后添加随机数量的空格
                int spaces = random.nextInt(maxSpaces) + 1;
                for (int j = 0; j < spaces; j++) {
                    result.append(" ");
                }
            }
        }
        
        return result.toString();
    }

    private static String replaceNumbersAndOperators(String text) {
        if (!enableTextReplacement) {
            return addNoise(addRandomSpaces(text));
        }

        StringBuilder result = new StringBuilder();
        String[] parts = text.split(" ");
        
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            // 检查是否是数字
            if (part.matches("\\d+")) {
                if (random.nextInt(100) < replacementProbability) {
                    // 随机决定是否替换这个数字
                    result.append(numberReplacements.getOrDefault(part, part));
                } else {
                    result.append(part);
                }
            }
            // 检查是否是运算符
            else if (operatorReplacements.containsKey(part)) {
                if (random.nextInt(100) < replacementProbability) {
                    // 随机决定是否替换这个运算符
                    result.append(operatorReplacements.getOrDefault(part, part));
                } else {
                    result.append(part);
                }
            }
            // 其他字符（如空格）保持不变
            else {
                result.append(part);
            }
            
            // 在运算符前后添加随机空格
            if (i < parts.length - 1) {
                result.append(addRandomSpaces());
            }
        }
        
        return addNoise(result.toString().trim());
    }

    public static class Problem {
        private final String question;
        private final int answer;

        public Problem(String question, int answer) {
            this.question = question;
            this.answer = answer;
        }

        public String getQuestion() {
            return question;
        }

        public int getAnswer() {
            return answer;
        }
    }

    public static Problem generateProblem() {
        int num1 = random.nextInt(9) + 1; // 1-9
        int num2 = random.nextInt(9) + 1; // 1-9
        int operator = random.nextInt(4); // 0:+, 1:-, 2:*, 3:/
        String question;
        int answer;

        switch (operator) {
            case 0: // 加法
                question = num1 + " + " + num2;
                answer = num1 + num2;
                break;
            case 1: // 减法
                if (num1 < num2) {
                    int temp = num1;
                    num1 = num2;
                    num2 = temp;
                }
                question = num1 + " - " + num2;
                answer = num1 - num2;
                break;
            case 2: // 乘法
                question = num1 + " × " + num2;
                answer = num1 * num2;
                break;
            case 3: // 除法
                answer = num1;
                num1 = num2 * answer;
                question = num1 + " ÷ " + num2;
                break;
            default:
                throw new IllegalStateException("Unexpected operator: " + operator);
        }

        // 确保结果在配置的范围内
        if (answer < Config.getAnswerMin() || answer > Config.getAnswerMax()) {
            return generateProblem();
        }

        // 应用文字替换
        String replacedQuestion = replaceNumbersAndOperators(question);

        return new Problem(replacedQuestion, answer);
    }

    public static void setPlayerProblem(String ip, Problem problem) {
        playerProblems.put(ip, problem);
        playerAttempts.put(ip, 0);
    }

    public static Problem getPlayerProblem(String ip) {
        return playerProblems.get(ip);
    }

    public static void removePlayerProblem(String ip) {
        playerProblems.remove(ip);
        playerAttempts.remove(ip);
    }

    public static int getCurrentAttempt(String ip) {
        return playerAttempts.getOrDefault(ip, 0);
    }

    public static void setCurrentAttempt(String ip, int attempt) {
        playerAttempts.put(ip, attempt);
    }

    public static void incrementAttempt(String ip) {
        int currentAttempt = playerAttempts.getOrDefault(ip, 0);
        playerAttempts.put(ip, currentAttempt + 1);
    }

    public static int getErrorCount(String ip) {
        return playerErrorCounts.getOrDefault(ip, 0);
    }

    public static void incrementErrorCount(String ip) {
        int currentErrorCount = playerErrorCounts.getOrDefault(ip, 0);
        playerErrorCounts.put(ip, currentErrorCount + 1);
        
        // 检查是否超过最大错误次数
        if (currentErrorCount + 1 >= Config.getMaxAttempts()) {
            banIP(ip);
        }
    }

    public static void resetErrorCount(String ip) {
        playerErrorCounts.remove(ip);
    }

    public static boolean checkAnswer(String ip, int attempt) {
        Problem problem = getPlayerProblem(ip);
        if (problem == null) return false;
        return attempt == problem.getAnswer();
    }

    public static void banIP(String ip) {
        bannedIPs.put(ip, System.currentTimeMillis() + (Config.getBanDuration() * 1000));
    }

    public static boolean isIPBanned(String ip) {
        Long banEndTime = bannedIPs.get(ip);
        if (banEndTime == null) return false;
        
        if (System.currentTimeMillis() > banEndTime) {
            bannedIPs.remove(ip);
            return false;
        }
        return true;
    }

    public static long getBanRemainingTime(String ip) {
        Long banEndTime = bannedIPs.get(ip);
        if (banEndTime == null) return 0;
        return Math.max(0, (banEndTime - System.currentTimeMillis()) / 1000);
    }
} 