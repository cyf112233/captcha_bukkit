package byd.cxkcxkckx.captcha;
        
import org.jetbrains.annotations.NotNull;
import top.mrxiaom.pluginbase.BukkitPlugin;
import top.mrxiaom.pluginbase.EconomyHolder;
import byd.cxkcxkckx.captcha.func.CaptchaListener;
import byd.cxkcxkckx.captcha.func.MathProblem;
import byd.cxkcxkckx.captcha.func.Config;
import org.bukkit.Bukkit;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import java.net.InetAddress;

public class captcha extends BukkitPlugin {
    private CaptchaListener captchaListener;

    public static captcha getInstance() {
        return (captcha) BukkitPlugin.getInstance();
    }

    public captcha() {
        super(options()
                .bungee(true)
                .adventure(false)
                .database(false)
                .reconnectDatabaseWhenReloadConfig(false)
                .vaultEconomy(false)
                .scanIgnore("top.mrxiaom.example.libs")
        );
    }

    @Override
    protected void afterEnable() {
        // 加载配置
        Config.loadConfig(this);
        
        // 创建并注册监听器
        captchaListener = new CaptchaListener(this);
        getServer().getPluginManager().registerEvents(captchaListener, this);
        
        // 注册MOTD事件
        getServer().getPluginManager().registerEvents(new org.bukkit.event.Listener() {
            @EventHandler(priority = EventPriority.HIGHEST)
            public void onServerPing(ServerListPingEvent event) {
                String ip = event.getAddress().getHostAddress();
                
                // 如果IP被封禁，完全拒绝连接
                if (MathProblem.isIPBanned(ip)) {
                    // 设置一个无效的端口，这样客户端会认为服务器不可用
                    event.setServerIcon(null);
                    event.setMaxPlayers(0);
                    event.setMotd("§c你的IP已被封禁");
                    return;
                }

                MathProblem.Problem problem = MathProblem.getPlayerProblem(ip);
                if (problem != null) {
                    // 增加尝试次数
                    MathProblem.incrementAttempt(ip);
                    int currentAttempt = MathProblem.getCurrentAttempt(ip);
                    int errorCount = MathProblem.getErrorCount(ip);
                    event.setMotd("§c当前题目: §e" + problem.getQuestion() + " = ?\n§c当前输入的答案: §e" + currentAttempt + "\n§7刷新服务器列表继续尝试\n§c剩余错误次数: " + (Config.getMaxAttempts() - errorCount));
                }
            }
        }, this);

        getLogger().info("captcha 加载完毕");
    }

    public CaptchaListener getCaptchaListener() {
        return captchaListener;
    }
}
