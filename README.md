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
### 定义一个RPC服务提供者  

使用 @ServiceProvider 定义一个服务提供者。标记 @ServiceProvider 后自动注册到Spring容器当中。接下来就全部交给Bullet RPC 自动配置就可以了。  

<img width="451" alt="image" src="https://github.com/TonyYan666/Bullet-RPC/assets/17917997/a39aefed-44d1-4698-9809-095c8abe4cae">

### 定义一个RPC服务消费者

RPC的调用方也非常简单，只需要根据服务提供方的Service定义一个接口即可。值得注意的是，方法的返回值是一个Future对象，代表方法请求以异步的形式请求。当然你也可以不返回Future，那你的请求方法将会以同步的形式调用。  
@ServiceConsumer 标识这是一个Bullet RPC的调用方，其中有一个必填的参数 serverAddress，标识一个RPC 服务提供者地址。值得注意的是如果不写端口号着默认使用Bullet RPC 默认端口 2186。  

<img width="451" alt="image" src="https://github.com/TonyYan666/Bullet-RPC/assets/17917997/cb07e0be-9b12-4eb2-82f3-269a86a90591">

标识了@ServiceConsumer 的接口会自动生成代理类，并且注册到Spring Bean容器当中，在Spring项目中直接注入使用即可。  

<img width="451" alt="image" src="https://github.com/TonyYan666/Bullet-RPC/assets/17917997/a01a4e13-3ff1-4755-b062-4eed9bb8a276">

写到这里Bullet RPC 的提供与调用就完成了，那么我们就启动两个服务测试一下吧！当然 Bullet RPC  远远没有那么简单，跟随这篇文档的节奏你会看到Bullet RPC的很多特性与使用方法。我会尽可能的讲Bullet RPC的方方面面都告诉你。  

## Bullet RPC Provider

刚刚我们快速写了一个最简单的Bullet RPC Provider。其实 Provider 可以交给我们去自定义的东西非常多，当然你可以使用最简单方法来完成一个Provider 这完全取决于你的实际业务场景。下面我们来认真看看Bullet RPC Provider 的全貌吧。  
   
### @ServiceProvider annotation  
@ServiceProvider annotation 由 bullet-rpc 的包提供。下图是@ServiceProvider的源码：
  
<img width="454" alt="image" src="https://github.com/TonyYan666/Bullet-RPC/assets/17917997/a690ee20-8b31-4fb2-afdb-5996a94fdf1d">  

- 服务提供者路径（value）：服务提供者路径是全局定位服务提供者对象的唯一全局标识。如果不指定该参数着使用提供者类名作为提供者路径，这意味着提供者的类名称必须全局唯一，如果有相同名称的提供者必须指定不一样的提供者路径来避免冲突。
      
- 是否暴露所有提供者方法供外部调用（provideAllMethods）：默认情况下提供者的所有 public 的方法都会提供给外部RPC消费者调用。如果设置为 FALSE 则只针对定义了@ServiceProviderMethod的方法对外暴露调用。  

### @ServiceProviderMethod annotation  
  
@ServiceProviderMethod annotation 主要定义提供者所暴露的功能方法。源码也非常简单
  
<img width="454" alt="image" src="https://github.com/TonyYan666/Bullet-RPC/assets/17917997/6549b84e-709f-468e-ba4b-1518752c11bc">

- 提供者的方法功能路径（value）：默认是方法名称，意味着同一个provider当中无法定义多个相同名称的方法（方法的重载不支持）。

<img width="451" alt="image" src="https://github.com/TonyYan666/Bullet-RPC/assets/17917997/5fbe539a-b9b8-49e5-b84f-4ad319d00e7d">

其实一个RPC请求的执行路由是通过定位一个provider，然后定位一个provider的方法。所以执行一个RPC 不一定需要采用 consumer 的代理对象执行。直接使用Bullet RPC的API，一样可以远程调用指定provider方法。所以通过@ServiceProvider 与 @ServiceProviderMethod 能够很好的暴露一个远程调用方法唯一标识。（为什么不采用完全限定类名？因为我们希望将RPC的接口定义包位置更加灵活。）  

###  ProviderDefinitionManager 提供者的定义管理  
  
所有的Bullet RPC Provider 都会生成一个ProviderDefinition来确定一个提供者的定义。而整个应用程序的Provider的定义，将由ProviderDefinitionManager统一管理。  
  
我们可以利用 ProviderDefinitionManager 动态注册一个 RPC Provider，也可以动态删除一个Provider。一切都可以由 ProviderDefinitionManager 动态控制。ProviderDefinition下面会包含多个ProviderMethodDefinition来描述provider所有暴露方法的定义。Bullet RPC 就是使用  findProviderMethodDefinition 方法来通过一个actionURL 获得一个 ProviderMethodDefinition，从而实现远程调用方法的定位。 





