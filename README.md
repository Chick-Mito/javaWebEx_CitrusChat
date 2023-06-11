# javaWebEx_CitrusChat
# JavaSocket编程实现聊天室（类qq样式）


改进思路：
1.Server维护一个列表，提供给Client接口（显示xx在线）
  列表记录全部数据信息，其中有一个值判断是否在线
2.Server端可支持多个聊天室
3.Client可发送表情、~~文件~~、震动、截图
4.用文本文件记录聊天记录
5.Server显示聊天记录
6.Server记录操作日志

功能：
+ 用Java图形用户界面编写聊天室服务器端和客户端， 支持多个客户端连接到一个服务器。每个客户端能够输入账号。
+ 可以实现群聊（聊天记录显示在所有客户端界面）。
+ 完成好友列表在各个客户端上显示。
+ 可以实现私人聊天，用户可以选择某个其他用户，单独发送信息。
+ 服务器能够群发系统消息，能够强行让某些用户下线。
+ 客户端的上线下线要求能够在其他客户端上面实时刷新。

Server：
![image](https://github.com/Chick-Mito/javaWebEx_CitrusChat/assets/63236457/8971f393-395e-4d08-b60e-c8dbd84ff57c)

Client：
![image](https://github.com/Chick-Mito/javaWebEx_CitrusChat/assets/63236457/7a590128-3a86-45f2-ad98-f72bcae7754a)
![image](https://github.com/Chick-Mito/javaWebEx_CitrusChat/assets/63236457/ae0f4837-869f-4429-ac60-9284efb4b619)
