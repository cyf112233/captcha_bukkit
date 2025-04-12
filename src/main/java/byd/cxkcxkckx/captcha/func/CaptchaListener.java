package byd.cxkcxkckx.captcha.func;

import byd.cxkcxkckx.captcha.captcha;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class CaptchaListener implements Listener {
    private final captcha plugin;
    private boolean isUnderAttack = false;
    private long attackEndTime = 0;
    private boolean alwaysVerify = true; // 默认总是验证

    public CaptchaListener(captcha plugin) {
        this.plugin = plugin;
        // 检查gun_bot插件是否存在
        Plugin gunBotPlugin = plugin.getServer().getPluginManager().getPlugin("gun_bot");
        if (gunBotPlugin != null && gunBotPlugin.isEnabled()) {
            alwaysVerify = false; // 如果gun_bot存在，则只在攻击时验证
            plugin.getLogger().info("检测到gun_bot插件，将在攻击时进行验证");
        }
    }

    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        String ip = event.getAddress().getHostAddress();
        
        // 检查IP是否被封禁
        if (MathProblem.isIPBanned(ip)) {
            long remainingTime = MathProblem.getBanRemainingTime(ip);
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED,
                "§c由于多次验证失败，你的IP已被临时封禁\n§e剩余时间: " + remainingTime + " 秒");
            return;
        }

        // 检查是否在攻击期间
        if (isUnderAttack) {
            if (System.currentTimeMillis() > attackEndTime) {
                isUnderAttack = false;
                plugin.getLogger().info("攻击已结束，停止验证模式");
                if (!alwaysVerify) return; // 如果不在总是验证模式，则返回
            }
        }

        // 如果不在攻击期间且不是总是验证模式，则直接允许连接
        if (!isUnderAttack && !alwaysVerify) {
            return;
        }

        // 进行验证
        MathProblem.Problem problem = MathProblem.getPlayerProblem(ip);
        if (problem == null) {
            // 新玩家，生成题目
            problem = MathProblem.generateProblem();
            MathProblem.setPlayerProblem(ip, problem);
            // 初始化尝试次数为0
            MathProblem.setCurrentAttempt(ip, 0);
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, 
                "§c请计算以下算术题：\n§e" + problem.getQuestion() + " = ?\n§7提示：在服务器列表中刷新，尝试不同的答案");
        } else {
            // 已有题目的玩家，检查当前尝试次数
            int currentAttempt = MathProblem.getCurrentAttempt(ip);
            if (currentAttempt == 0) {
                // 第一次尝试，直接增加尝试次数并返回
                MathProblem.incrementAttempt(ip);
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                    "§c请计算以下算术题：\n§e" + problem.getQuestion() + " = ?\n§7提示：在服务器列表中刷新，尝试不同的答案");
                return;
            }

            if (MathProblem.checkAnswer(ip, currentAttempt)) {
                // 答案正确，允许进入
                MathProblem.removePlayerProblem(ip);
                MathProblem.resetErrorCount(ip);
            } else {
                // 答案错误，增加错误计数并生成新题目
                MathProblem.incrementErrorCount(ip);
                int errorCount = MathProblem.getErrorCount(ip);
                
                if (errorCount >= Config.getMaxAttempts()) {
                    // 超过最大错误次数，封禁IP
                    MathProblem.banIP(ip);
                    MathProblem.removePlayerProblem(ip);
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED,
                        "§c由于多次验证失败，你的IP已被临时封禁\n§e封禁时长: " + Config.getBanDuration() + " 秒");
                } else {
                    // 生成新题目
                    problem = MathProblem.generateProblem();
                    MathProblem.setPlayerProblem(ip, problem);
                    // 重置尝试次数为0
                    MathProblem.setCurrentAttempt(ip, 0);
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                        "§c答案错误！请重新计算：\n§e" + problem.getQuestion() + " = ?\n§7提示：在服务器列表中刷新，尝试不同的答案\n§c剩余错误次数: " + (Config.getMaxAttempts() - errorCount));
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String ip = event.getPlayer().getAddress().getAddress().getHostAddress();
        MathProblem.removePlayerProblem(ip);
    }

    // 这个方法会被gun_bot插件调用
    public void startAttackMode(long endTime) {
        isUnderAttack = true;
        attackEndTime = endTime;
        plugin.getLogger().info("检测到机器人攻击，开始验证模式");
    }
} 