package byd.cxkcxkckx.captcha.func;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MathProblem {
    private static final Random random = new Random();
    private static final Map<String, Problem> playerProblems = new HashMap<>();
    private static final Map<String, Integer> playerAttempts = new HashMap<>();
    private static final Map<String, Integer> playerErrorCounts = new HashMap<>();
    private static final Map<String, Long> bannedIPs = new HashMap<>();

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

        return new Problem(question, answer);
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