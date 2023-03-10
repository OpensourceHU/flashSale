WingStudio二轮考核项目,  参考MOOC课程: [Java秒杀系统方案优化 高性能高并发实战-慕课网实战 (imooc.com)](https://coding.imooc.com/class/168.html)实现

2023更新:  采用DDD调整代码结构 现项目的目录结构大致如下:

```
└── main
    ├── java
       └── com
           └── jesper
               └── flashSale
                   ├── application   //应用层
                   ├── domain        //领域层
                   │   ├── config
                   │   │   └── limit
                   │   └── vo
                   └── infra         //基础设施层
                       ├── db
                       │   ├── enums
                       │   │   ├── exception
                       │   │   └── result_code
                       │   ├── mapper
                       │   └── pojos
                       ├── rabbitmq
                       ├── redis
                       │   └── keys
                       └── utils
                           ├── util
                           └── validator
```



## 系统介绍

本系统是使用SpringBoot开发的并发环境下限时抢购秒杀系统，除了实现基本的登录、查看商品列表、秒杀、下单等功能，项目中还针对高并发情况实现了系统缓存和限流。

## 开发技术

前端技术 ：Bootstrap框架 + jQuery(发送ajax)

后端技术 ：SpringBoot + MyBatis + MySQL(Druid连接)

中间件技术 : Redis + RabbitMQ



## 关于项目运行的步骤

1. 环境准备:  dockerFile与sql脚本都在script文件夹中, 生成数据需要先将sql脚本使用`docker cp` 复制到容器内再`source seckill.sql`生成.
    中间件环境已使用dockerFile配置完成 `docker compose up`启动即可
2. 运行启动类MainApplication.java
3. 访问localhost:8080/login/to_login    登录的用户名是18181818181，密码是12345
4. 优化前的接口都是 xxx1, 优化后的是 xxx2



## 针对并发场景的优化:

### **优化思路:**

1. 将请求尽量拦截在系统上游,避免打到数据库：大量请求都压到MySQL，数据读写锁冲突严重(不做读写分离的情况下)，几乎所有请求都超时，流量虽大，下单成功的有效流量甚小，我们可以通过限流、降级等措施来最大化减少对数据库的访问，从而保护系统。

2. 充分利用缓存：秒杀商品是一个典型的读多写少的应用场景，充分利用缓存以提高并发量


- ### app层:令牌桶限流    

    使用RateLimiter来实现限流，RateLimiter是Google guava提供的基于令牌桶算法的限流实现类，通过调整生成token的速率来限制用户频繁访问秒杀页面，从而达到防止超大流量冲垮系统。

- ### MQ削峰 解耦

    解耦: 对响应时间不敏感的任务, 比如下单成功后发送邮件  交由RabbitMQ去唤醒, 能确保完成即可

    削峰: DB写入时需要去连接池里获取连接, 是一个性能瓶颈;  如果写入DB失败而Redis写入成功则造成不一致,  考虑在DB前用MQ造一个死信队列; 其好处有1.削峰, 通过控制队列消费速度达到压平曲线的效果  2.保证写入成功, 在DB崩溃后, 死信队列里的消息未被消费 重新消费一次即可

- ### Redis缓存预热

    1. 活动开始前, 所有秒杀商品先加载到Redis中预热
    2. 页面缓存：通过在手动渲染得到的html页面缓存到Redis,这部分网页不需要每次都动态生成  可以减少部分浏览器负担
    3. 对象缓存：包括对用户信息、订单信息和token等数据进行缓存，加快查询速度。

- ### Mysql实现乐观锁

​		更理想的方案应该是做读写分离...  效果应该会好得多 而且数据冗余也有了; 不过这只是一个学习项目, 主要精力放在编码上 系统设计方面后续再补充吧

​		订单写入时不加写锁, 改用一个版本号实现乐观锁

## 其它亮点

### 1. 两次MD5加密

将用户输入的密码和固定Salt通过MD5加密生成第一次加密后的密码，再讲该密码和随机生成的Salt通过MD5进行第二次加密，最后将第二次加密后的密码和第一次的固定Salt存数据库

好处：

1. 第一次作用：防止用户明文密码在网络进行传输
2. 第二次作用：防止数据库被盗，避免通过MD5反推出密码，双重保险

### 2. session共享

验证用户账号密码都正确情况下，通过UUID生成唯一id作为token，再将token作为key、用户信息作为value模拟session存储到redis，同时将token存储到cookie，保存登录状态

好处： 在分布式集群情况下，服务器间需要同步，定时同步各个服务器的session信息，会因为延迟到导致session不一致，使用redis把session数据集中存储起来，解决session不一致问题。

### 3. JSR303自定义参数验证

使用JSR303自定义校验器，实现对用户账号、密码的验证，使得验证逻辑从业务代码中脱离出来。

### 4. 全局异常统一处理

通过拦截所有异常，对各种异常进行相应的处理，当遇到异常就逐层上抛，一直抛到最终由一个统一的、专门负责异常处理的地方处理，这有利于对异常的维护。

### 5. 页面静态化

对商品详情和订单详情进行页面静态化处理，页面是存在html，动态数据是通过接口从服务端获取(Ajax)，实现前后端分离

### 6. 本地标记 + redis预处理 + RabbitMQ异步下单

描述：通过三级缓冲保护，1、本地标记 2、redis预处理 3、RabbitMQ异步下单，最后才会访问数据库，这样做是为了最大力度减少对数据库的访问。

实现：

1. 在秒杀阶段使用本地标记对用户秒杀过的商品做标记，若被标记过直接返回重复秒杀，未被标记才查询redis，通过本地标记来减少对redis的访问
2. 抢购开始前，将商品和库存数据同步到redis中，所有的抢购操作都在redis中进行处理，通过Redis预减少库存减少数据库访问
3. 为了保护系统不受高流量的冲击而导致系统崩溃的问题，使用RabbitMQ用异步队列处理下单，实际做了一层缓冲保护

### 7. 使用数学公式验证码

描述：点击秒杀前，先让用户输入数学公式验证码，验证正确才能进行秒杀。(坏处是用户体验要差不少)

好处：

1. 防爬
2. 分散用户的请求

实现：

1. 前端通过把商品id作为参数调用服务端创建验证码接口
2. 服务端根据前端传过来的商品id和用户id生成验证码，并将商品id+用户id作为key，生成的验证码作为value存入redis，同时将生成的验证码输入图片写入imageIO让前端展示
3. 将用户输入的验证码与根据商品id+用户id从redis查询到的验证码对比，相同就返回验证成功，进入秒杀；不同或从redis查询的验证码为空都返回验证失败，刷新验证码重试



## 问题与解决方案

### 解决超卖

描述：

​	比如某商品的库存为1，此时用户1和用户2并发购买该商品，用户1提交订单后该商品的库存被修改为0，而此时用户2并不知道的情况下提交订单，该商品的库存再次被修改为-1，这就是超卖现象

实现：

1. 对库存更新时，先对库存判断，只有当库存大于0才能更新库存
2. 实现乐观锁，给商品信息表增加一个version字段，为每一条数据加上版本。每次更新的时候version+1，并且更新时候带上版本号，当提交前版本号等于更新前版本号，说明此时没有被其他线程影响到，正常更新，如果冲突了则不会进行提交更新。当库存是足够的情况下发生乐观锁冲突就进行一定次数的重试。



## 压测效果

优化前 ：开启1000个线程循环10次同时访问，QPS = 423 优化后：QPS = 2501
