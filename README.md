# captcha_bukkit
一个适用于bukkit服务器的人机验证
<br>
<iframe src="//player.bilibili.com/player.html?isOutside=true&aid=114342263525425&bvid=BV1bpdfYtEYw&cid=29432679399&p=1" scrolling="no" border="0" frameborder="no" framespacing="0" allowfullscreen="true"></iframe>
***所有尝试进入服务器的玩家都会要求写一道数学题，如果多次回答错误将会被封禁一段时间后才可继续尝试***
<br>
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
