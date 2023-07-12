# Bullet-RPC

简体中文：  
Bullet RRC 是基于Netty NIO 的私有协议RPC框架。Bullet RPC 能够独立或 者集成到SpringCloud当中使用。通过基准测试 Bullet RPC 性能效率远超基 于HTTP的RPC请求，甚至性能比gRPC与Dubbo还要更快。Bullet RPC 不仅仅对SpringCloud与SpringBoot集成友好，对于RPC请求的安全性上，基于握手请求扩展点能够做到连接的握手时的安全校验，避免未授权的设备接入并访问未授权的系统资源。Bullet RPC 对于SpringCloud的集成也是⾮常全⾯，包括⽀持 SpringCloud 所有的服务注册与发现中间件以及LoadBalancer的完美接⼊。为什么我们要开发 Bullet RPC 和使⽤ Bullet RPC ？ 在RPC选型的过程当中我们尝试了很多框架包括 gRPC、Dubbo、 OpenFeign 等。 OpenFeign ⽬前已经在睿本云系统上⼴泛使⽤，优点是SpringCloud的集成 性好，使⽤简单。其通讯是基于HTTP实现，相对简单与通⽤。但是缺点也⾮常明 显，就是慢。由于HTTP的特性问题，每次请求都需要建⽴TCP连接，对于频繁的 RPC请求显然⼒不从⼼。 gRPC 与 Dubbo 性能接近，gRPC使⽤的是基于HTTP2.0作为底层通讯协 议，⽽dubbo则使⽤dubbo协议作为底层通讯协议（dubbo 3.x 已经开始推⼴使⽤ tuple协议其协议是基于HTTP2.0，相对dubbo协议⽽⾔性能稍差）。gRPC由于使⽤了HTTP2.0通⽤性⽽⾔会⽐Dubbo好。但是两者都是同样的问题，对Spring Cloud的集成不够友好（例如不⽀持Eureka），扩展也相对麻烦。由于gRPC的序列化与接⼝定义都 是通过protobuf来定义，使⽤上对于JAVA⽽⾔会不够友好（对于夸语⾔来说是⼀个 不错的选择）。 最后⽆论是什么RPC框架都没有办法提供很好的灵活性，例如你没有办法对 ⼀个RPC接⼝修改他的超时时间与重试次数。⽽且你没有办法让Dubbo 或者 gRPC 很好的兼容Eureka（因为SpringCloud的老项目都深度使用了Eureka）。虽然OpenFeign 能够提供很好的扩展能⼒，但是基于HTTP的 RPC实在是太慢了，在⾯对⽇益增加的业务量每次都优化都显得⾮常⽆⼒。 Bullet RPC 就是基于以上的种种原因提上了开发⽇程。Bullet RPC 不仅仅解决了 上述的所有问题，⽽且同时满⾜了“快”这个必然需求。Bullet RPC 不仅仅⽐ OpenFeign 要快，甚⾄性能也明显超越了gRPC 与 Dubbo。

English:  
Bullet RPC is a private protocol RPC framework based on Netty NIO. It can be used independently or integrated into SpringCloud. Through benchmark testing, Bullet RPC has shown significantly higher performance efficiency compared to HTTP-based RPC requests, even faster than gRPC and Dubbo. Bullet RPC is not only friendly for integration with SpringCloud and SpringBoot, but also ensures the security of RPC requests through handshake request extension points, preventing unauthorized devices from accessing and accessing unauthorized system resources. Bullet RPC provides comprehensive integration with SpringCloud, including support for all service registration and discovery middleware as well as seamless integration with LoadBalancer. Why do we develop and use Bullet RPC? During the RPC selection process, we tried many frameworks including gRPC, Dubbo, and OpenFeign. OpenFeign is widely used in the Raybon Cloud system, with the advantages of good integration with SpringCloud and simplicity of use. It communicates based on HTTP, which is relatively simple and universal. However, its drawback is that it is slow. Due to the characteristics of HTTP, establishing a TCP connection is required for each request, which is not efficient for frequent RPC requests. gRPC and Dubbo have similar performance, with gRPC using HTTP2.0 as the underlying communication protocol and Dubbo using the Dubbo protocol (dubbo 3.x has started to use the tuple protocol, which is based on HTTP2.0 and slightly inferior in performance compared to the Dubbo protocol). gRPC has better compatibility due to its use of HTTP2.0. However, both of them are not very friendly for integration with SpringCloud (for example, they do not support Eureka) and have relatively complicated extensions. Since gRPC's serialization and interface definition are defined through protobuf, it is not very user-friendly for Java (it is a good choice for cross-language scenarios). Finally, no matter what RPC framework is used, it cannot provide good flexibility. For example, you cannot modify the timeout and retry times of an RPC interface, and you cannot make Dubbo or gRPC compatible with Eureka (because SpringCloud's old projects extensively use Eureka). Although OpenFeign can provide good extensibility, RPC based on HTTP is too slow, and it becomes very difficult to optimize with the increasing business volume. Bullet RPC was developed for all these reasons. It not only solves all the above-mentioned problems but also meets the requirement of "fast". Bullet RPC is not only faster than OpenFeign, but also significantly outperforms gRPC and Dubbo in terms of performance.

![image](https://github.com/TonyYan666/Bullet-RPC/assets/17917997/20376e82-7853-47b1-98ca-d32553698719)  

# Bullet RPC Quick start  

### 将Bullet RPC 接入SpringBoot项目  
SpringBoot 项目基于Maven 依赖接入  
```
<dependency>
  <groupId>com.smileframework.bullet</groupId>
  <artifactId>bullet-rpc-spring-boot-starter</artifactId>
  <version>2.0.0-SNAPSHOT</version>
</dependency>
```
定义一个RPC服务提供者  
使用 @ServiceProvider 定义一个服务提供者。标记 @ServiceProvider 后自动注册到Spring容器当中。接下来就全部交给Bullet RPC 自动配置就可以了。  
<img width="451" alt="image" src="https://github.com/TonyYan666/Bullet-RPC/assets/17917997/a39aefed-44d1-4698-9809-095c8abe4cae">





