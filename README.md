# captcha_bukkit
一个适用于bukkit服务器的人机验证
<br>
***所有尝试进入服务器的玩家都会要求写一道数学题，如果多次回答错误将会被封禁一段时间后才可继续尝试***
<br>
和别的插件不同的是，他的数学题显示在motd（正常情况下不会复盖您美丽的motd），靠点击刷新按钮的填写答案，所以假人无法进入服务器
<br>
↓点击图片查看视频演示↓
<br>
[![image](https://github.com/cyf112233/captcha_bukkit/blob/main/image/download.jpg)](https://www.bilibili.com/video/BV1bpdfYtEYw/?share_source=copy_web&vd_source=d50aca3fe1490e0f795ef9b07b0acff8)
<br>
如果基岩版玩家尝试连接，他将会识别 Floodgate api ，并对基岩版玩家进行绕过
如果回答错误到一定次数，就会对那个ip进行短时间的封禁
<br>
如果你认为每个玩家进来都要答题过于麻烦，可以搭配[gun_bot插件](https://github.com/cyf112233/gun_bot "一个简单的检测假人攻击的插件")使用，这样就会在防御期间开启算术问答，正常情况下玩家可以直接进入
<br>
以下为配置文件
<br>
```
# 最大尝试次数
max-attempts: 3

# 封禁时长（秒）
ban-duration: 300

# 答案范围
answer-min: 2
answer-max: 10
```
