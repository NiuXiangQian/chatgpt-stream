## 🔥你的chatgpt🔥

`` OpenAi最简洁的Java流式返回接入方式，没有第三方依赖，只需要使用Spring Boot即可!轻松构建你的chatgpt
``

## 特点

* 无第三方依赖
* 流式返回

采用sse技术，感兴趣的可以先了解一下

### 👀效果

![截图1](docs/demo1.png)
![截图2](docs/demo2.gif)

### 重要配置

```yaml
authorization: 你的key
```

### 关于中国大陆访问不通问题

* 使用vpn代理
    - 此功能已经实现 请查看 OpenAiWebClient.java
* 部署到海外
    - 取消如下配置即可
  ```yaml
    env: test
    ```

### 启动

启动springboot 然后访问 http://127.0.0.1:8080/

### 后续功能

- [X] 流式返回
- [ ] 聊天记忆
- [ ] 图片接入
- [ ] ....

### 交流方式

qq: 1603565290
wx: 16601371505

