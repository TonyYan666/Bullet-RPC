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
  <version>2.0.0-RELEASE</version>
</dependency>
```
### 定义一个RPC服务提供者  

使用 @ServiceProvider 定义一个服务提供者。标记 @ServiceProvider 后自动注册到Spring容器当中。接下来就全部交给Bullet RPC 自动配置就可以了。  

![image](https://github.com/TonyYan666/Bullet-RPC/assets/17917997/a39aefed-44d1-4698-9809-095c8abe4cae)

### 定义一个RPC服务消费者

RPC的调用方也非常简单，只需要根据服务提供方的Service定义一个接口即可。值得注意的是，方法的返回值是一个Future对象，代表方法请求以异步的形式请求。当然你也可以不返回Future，那你的请求方法将会以同步的形式调用。  
@ServiceConsumer 标识这是一个Bullet RPC的调用方，其中有一个必填的参数 serverAddress，标识一个RPC 服务提供者地址。值得注意的是如果不写端口号着默认使用Bullet RPC 默认端口 2186。  

![image](https://github.com/TonyYan666/Bullet-RPC/assets/17917997/cb07e0be-9b12-4eb2-82f3-269a86a90591)

标识了@ServiceConsumer 的接口会自动生成代理类，并且注册到Spring Bean容器当中，在Spring项目中直接注入使用即可。  

![image](https://github.com/TonyYan666/Bullet-RPC/assets/17917997/a01a4e13-3ff1-4755-b062-4eed9bb8a276)

写到这里Bullet RPC 的提供与调用就完成了，那么我们就启动两个服务测试一下吧！当然 Bullet RPC  远远没有那么简单，跟随这篇文档的节奏你会看到Bullet RPC的很多特性与使用方法。我会尽可能的讲Bullet RPC的方方面面都告诉你。  

# Bullet RPC Provider

刚刚我们快速写了一个最简单的Bullet RPC Provider。其实 Provider 可以交给我们去自定义的东西非常多，当然你可以使用最简单方法来完成一个Provider 这完全取决于你的实际业务场景。下面我们来认真看看Bullet RPC Provider 的全貌吧。  
   
### @ServiceProvider annotation  
@ServiceProvider annotation 由 bullet-rpc 的包提供。下图是@ServiceProvider的源码：
  
![image](https://github.com/TonyYan666/Bullet-RPC/assets/17917997/a690ee20-8b31-4fb2-afdb-5996a94fdf1d) 

- 服务提供者路径（value）：服务提供者路径是全局定位服务提供者对象的唯一全局标识。如果不指定该参数着使用提供者类名作为提供者路径，这意味着提供者的类名称必须全局唯一，如果有相同名称的提供者必须指定不一样的提供者路径来避免冲突。
      
- 是否暴露所有提供者方法供外部调用（provideAllMethods）：默认情况下提供者的所有 public 的方法都会提供给外部RPC消费者调用。如果设置为 FALSE 则只针对定义了@ServiceProviderMethod的方法对外暴露调用。  

### @ServiceProviderMethod annotation  
  
@ServiceProviderMethod annotation 主要定义提供者所暴露的功能方法。源码也非常简单
  
![image](https://github.com/TonyYan666/Bullet-RPC/assets/17917997/6549b84e-709f-468e-ba4b-1518752c11bc)

- 提供者的方法功能路径（value）：默认是方法名称，意味着同一个provider当中无法定义多个相同名称的方法（方法的重载不支持）。

![image](https://github.com/TonyYan666/Bullet-RPC/assets/17917997/5fbe539a-b9b8-49e5-b84f-4ad319d00e7d)

其实一个RPC请求的执行路由是通过定位一个provider，然后定位一个provider的方法。所以执行一个RPC 不一定需要采用 consumer 的代理对象执行。直接使用Bullet RPC的API，一样可以远程调用指定provider方法。所以通过@ServiceProvider 与 @ServiceProviderMethod 能够很好的暴露一个远程调用方法唯一标识。（为什么不采用完全限定类名？因为我们希望将RPC的接口定义包位置更加灵活。）  

###  ProviderDefinitionManager 提供者的定义管理  
  
所有的Bullet RPC Provider 都会生成一个ProviderDefinition来确定一个提供者的定义。而整个应用程序的Provider的定义，将由ProviderDefinitionManager统一管理。  
  
我们可以利用 ProviderDefinitionManager 动态注册一个 RPC Provider，也可以动态删除一个Provider。一切都可以由 ProviderDefinitionManager 动态控制。ProviderDefinition下面会包含多个ProviderMethodDefinition来描述provider所有暴露方法的定义。Bullet RPC 就是使用  findProviderMethodDefinition 方法来通过一个actionURL 获得一个 ProviderMethodDefinition，从而实现远程调用方法的定位。 


![image](https://github.com/TonyYan666/Bullet-RPC/assets/17917997/63d1f812-d43c-46a7-b13b-770c4e5ccaa6)

###  ProviderInvokeInterceptor 提供者调用拦截器
  
提供者调用拦截器与SpringMVC的interceptor类似。在远程调用Provider实例方法时可以通过配置自定义拦截器来对RPC请求作出处理，例如调用权限控制、调用日志（默认Bullet RPC 已经有提供日志拦截器）、请求拦截并修改等等。以下是ProviderInvokeInterceptor的接口定义：  
  
![image](https://github.com/TonyYan666/Bullet-RPC/assets/17917997/0bbd6299-4427-4261-9149-9147d322057c)
  
Bullet RPC Provider 的所有拦截器由 ProviderInvokeInterceptorManager 统一托管。ProviderInvokeInterceptorManager 的职责只要负载拦截器的注册，拦截器的统一调用等。 	ProviderInvokeInterceptor 提供了调用前、调用成功后、调用失败后等回调方法，开发者可以任意定义自己的拦截器完成相关业务。拦截器的调用顺序统一由order方法来控制，数字越小越优先。match方法主要确定当前拦截器是否需要拦截当前请求。  

#### 以下是参数的一些解释：

- ProviderMethodDefinition : 被调用的提供者方法定义，里面可以获得Provider对象以及Provider的定义；  
  
- BulletRequest<Object[]> : Bullet RPC的请求实体，里面 payload 包括一个Object 的数组，数组长度取决于的调用的方法签名需要多少个参数。在Interceptor 里面可以对任意参数做出修改。BulletRequest 中的 Header 还包含很多请求头数据，例如认证信息、自定义头数据、请求类型、请求ID、请求的ACTION URL等；  
  
- ClientConnection : Bullet RPC 客户端连接，可以对连接做出任意处理，例如发现异常尝试去关闭它。但一般情况下，我们只需要拿到连接当中的信息，例如什么实例的请求、什么服务的请求等等；  
  
- BulletResponse<?> :  Bullet RPC 调用Provider方法后，会将返回值进行封装。并以BulletResponse的形式向下传递，在拦截器期间我们可以对BulletResponse立面的内容做任意修改；  
  
- costMs : 调用耗时；


### InvokeErrorTranslator 提供者异常翻译器

我们希望远程调用和本地调用一样的体验，调用异常也是一样。我们很希望远端抛出什么异常，本地就抛出什么异常。所以在 OpenFeign 配置上我们也作出同样的尝试，希望把异常能够透过返回信息在本地重现出来，而不是靠返回对象的错误码手动抛出。但是实际上在OpenFeign实现这一点体验并不好。  
  
基于这些原因我们在设计Provider Invoker 的时候就顺便写了一个InvokeErrorTranslator 把错误异常穿透到调用方当中。  

![image](https://github.com/TonyYan666/Bullet-RPC/assets/17917997/36e9663a-a350-45b0-9807-153ef2b044ef)

### ProviderInvoker 提供者执行器
  
ProviderInvoker 提供者执行器。除了负责对 Provider 方法的实际调用，还对 InvokeErrorTranslator 与 ProviderInvokeInterceptorManager 进行一个整合。也是能够在调用过程当中实现错误转换传递、调用拦截的原因。  
  
在 ProviderInvoker 再负责对 ProviderInvokeContext 的生命周期管理。Provider 被调用方法可以通过 ProviderInvokeContextHelper 获得当前RPC调用的上下文信息。ProviderInvokeContext 里面包含你能想到的所有RPC调用信息，包括BulletRequest、BulletResponse、当前调用线程、提供者方法定义（ProviderMethodDefinition）等等。  

- 以下是 ProviderInvoker 的部分核心代码：
  
![image](https://github.com/TonyYan666/Bullet-RPC/assets/17917997/91042b4e-f847-4e4f-bd8a-a8965f49fb77)

存储 ProviderInvokeContext 的ThreadLocal 是 TransmittableThreadLocal 所以支持跨线程的传递。  
在执行过程当中我们还可以通过ProviderInvokeContext的addMeta方法将一些元数据通过Response header 的形式返回给客户端。（目前只在框架层面上使用，业务代码尽可能在返回对象中完成数据的传递会更加方便）  

### Bullet RPC Connection Handshake

Bullet RPC Connection 握手动作一般由框架内部完成，握手动作主要是确认客户端的身份以及连接认证。一般情况下我们只需要关注创建连接之后的客户端认证即可。  
  
ConnectionAuthenticationManager 接口主要实现创建连接后的认证，确定创建连接的主机已通过授权认证。当然默认情况下无需认证即可完成连接握手的动作。我们可以通过配置来让没有认证通过的连接握手失败而断开链接。
  
配置ConnectionAuthenticationManager 非常简单，只需要通过 BulletServerContext.setConnectionAuthenticationManager方法设置。ConnectionAuthenticationManager 接口只有一个authenticate 的方法。参数里面携带ClientConnection和BulletRequest<ConnectionHandshake> 两个对象，一般情况下通过ConnectionHandshake对象当中的authorization字段来判断当前连接的认证票据。  

![image](https://github.com/TonyYan666/Bullet-RPC/assets/17917997/f4e976f6-6f53-4d01-b1a9-f119163d333c)  

## ContentConvertManager 数据转换管理器

Bullet RPC 一般主要涉及到两个对象的转换BulletRequest 和 BulletResponse。通过代码我们可以了解到 BulletRequest 和 BulletResponse 都有一个泛型，其代表是输入和输出的对象类型。  
  
Bullet RPC Request 发出时会构建一个BulletRequest<Object[]> 其中泛型里面的对象正是我们方法的请求参数列表，然后会经过 ContentConvertManager 从BulletRequest<Object[]> 转换为 BulletRequest<byte[]>。到后面就是Bullet RPC 底层的序列化操作了，将BulletRequest<byte[]> 转成 BulletTransportRequest。最后将 BulletTransportRequest 对象通过 JProtobuf 序列化为二进制。  
  
Bullet RPC Response 也是与Bullet RPC Request 类似的，只不过是流程对调罢了。而 ContentConvertManager 最主要的动作主要是完成 BulletRequest<Object[]> 到 BulletRequest<byte[]> 的转换和BulletResponse<byte[]> 到 BulletResponse<T> 的转换。但一般实际完成转换的并不是ContentConvertManager 而是它里面所管理的 ContentConvertor 对象列表。我们只需要实现我们的 ContentConvertor 就可以完成特定格式的对象转换。  
  
默认情况下我们并不需要你去配置 ContentConvertManager。因为我们已经在 ContentConvertManager 里面内置了两个 ContentConvertor 实现，FastJsonContentConvertor 和 TextContentConvertor。这两个转换器已经能够满足最大部分的应用场景。当然如果你希望创建自己的转换器和自己特定的内容格式，你也可以在 ContentConvertManager 注册你自己的 ContentConvertor 来扩展Bullet RPC 的转换能力，例如基于RSA或ASE加密的JSON格式转换器等。  
  
  
![image](https://github.com/TonyYan666/Bullet-RPC/assets/17917997/6059533f-3bd0-4550-9e56-370a40399678)

可以看见 ContentConvertManager 用哪个 ContentConvertor 是通过ContentType来确定的，这个contentType来源于 BulletRequest.Header里面的ContentType字段。目前只定义了JSON与TEXT两个类型格式，用户可以自行扩展。ContentConvertManager 提供了replaceContentConvertors方法来替换默认的转取或者添加新类型的转换器。  

![image](https://github.com/TonyYan666/Bullet-RPC/assets/17917997/00118dc8-b8fe-4d53-8a44-734070bdebaf)


## BulletProviderContext 服务提供者上下文
  
刚刚提到所有管理器、执行器都可以通过BulletProviderContext 替换成自定义的扩展版本。BulletProviderContext 是启动一个Bullet RPC 服务的关键，它主要负责协调所有的配置以及管理器完成整一个 BulletProviderServer 的运行。  
  
BulletProviderContext 是继承于 BulletServerContext，BulletServerContext 主要负责网络部分的配置，而BulletProviderContext更多是RPC 提供者的配置。BulletProviderContext 还有一个子类是SpringBulletProviderContext，主要负责 Spring 环境下的Provider配置。  
  
BulletProviderContext 可以配置的东西非常多，我们就不在这里展开有兴趣的同学可以翻看源码探索更多的可能性。下面我们就简单的配置一下 BulletProviderContext 并启动一个Provider Server吧！  

![image](https://github.com/TonyYan666/Bullet-RPC/assets/17917997/94235ac7-f27b-41bf-b802-e9cbfd8a74d7)  
  
在 Spring 环境下使用一般不会直接操作 BulletProviderContext，而是实现自己 BulletConfigAdapter 去配置。而 Bullet RPC Spring 则会通过用户的BulletConfigAdapter 去动态配置 SpringBulletProviderContext 来完成启动Provider Server 的伟大任务。  

# Bullet RPC Consumer

上面我们详细介绍了 Bullet RPC Provider 的大致工作原理，并且能够顺利启动一个Provider Server。接下来我们来了解一下如何利用Bullet RPC Consumer 去调用Provider Server。我们将会了解怎么定义一个Consumer，怎么利用Consumer去调用一个远程方法等。  
  
  
## @ServiceConsumer annotation  


@ServiceConsumer 的作用主要是定义RPC的调用方，使用ServiceConsumer annotation 定义的接口，可以通过 BulletConsumerContext 的getServiceConsumerProxy 方法来获得一个远程调用代理对象。  

![image](https://github.com/TonyYan666/Bullet-RPC/assets/17917997/5a18c832-a03e-4d76-8b4c-1690bca5084b)  

除了 @ServiceConsumer 还有 @ServiceConsumerMethod，@ServiceConsumerMethod 并不是必要的，但是 @ServiceConsumerMethod 能够让每个远程调用方法有更多的可配置空间。  
  
我们先来简单的看看@ServiceConsumer的源码：  
![image](https://github.com/TonyYan666/Bullet-RPC/assets/17917997/e79f81ba-7f5a-4948-bad2-b826d132af22)  


可以看到必要参数是 serverAddress 服务提供者地址，另外比较重要的配置是providerPath 如果provider定义了路径，Consumer 也同样需要定义，否则调用时会抛出 provider not found 的异常错误。requestTimeoutMills 请求超时时间，这个只针对同步调用有效，对于异步调用而言应用通过 Future.get 来控制超时时间。requestContentType 代表请求的格式类型默认为JSON，当然如果你扩展了你自己的格式可以写入你的自己的格式类型。fallback 参数传入一个类，代表降级处理的实现后面会详细讲解。  
  
当然这些配置应用范围都是以整个 Consumer 接口为维度的，部分配置可以精确到Consumer的方法级别。如果 Consumer 接口级别不作配置以全局配置为准。  

  
接下来我们再看看@BulletConsumerMethod 的代码：  

![image](https://github.com/TonyYan666/Bullet-RPC/assets/17917997/5d150551-b963-43f1-91b2-6543663bdae2)

@ServiceConsumerMethod annotation 并不是必要的。如果不标识ServiceConsumerMethod.name 属性，则使用方法名作为方法的name。当然这个也是取决于你的provider定义。  
其中有很多都是 @ServiceConsumer 一样的参数，我这里就不一一介绍了。具体的参数最终以范围最少的为准。另外比较特殊就是 retry 和 retryIntervalMs 两个参数。重试机制目前只支持同步调用情况下使用，异步调用暂时不支持重试。  
requestMode 是请求模式，一般情况下我们都是使用单播（UNICAST）。广播（BROADCAST）仅支持在微服务场景下使用，而且广播的场景下不能有返回值。一般我们都使用单播（UNICAST）为主，而广播（BROADCAST）一般的场景都是框架基本去使用，例如通知所有服务清空本地缓存等。  

## 调用失败降级 Fallback  

跟 Open Feign 一样，Bullet RPC 同样支持降级操作。实现降级目前支持两种做法，第一种是实现ConsumerFallbackHandler 另外是实现Consumer的接口。如果在Spring环境下，Bullet RPC 会优先从Spring 容器当中寻找 FallbackHandler的Bean，所以你可以在你的Fallback实现里面注入各种你需要的Bean。当然如果我们没有办法通过你的类型在Spring容器找到对应的Fallback实例，那么我们就会帮你实例化一个，但是我们并不会为你将这个Fallback实例注册到Spring容器中。  

![image](https://github.com/TonyYan666/Bullet-RPC/assets/17917997/a97a07d3-4546-489f-9c65-7cb00b541961)  


在Bullet RPC 底层是通过 FallbackRequestInvoker 来实现降级方法的调用。感兴趣的同学可以阅读源码去了解底层原理。  


## Consumer Filter Manager 消费者请求过滤器管理器


有些情况下我们希望将每次的对外RPC访问拦截下来，并对请求对象作出一些修改，例如我们希望在RPC调用前将用户授权信息带到被调用方当中。这就需要使用到 ConsumerPreRequestFilter。相对应的在RPC请求响应的时候我们在返回到Consumer调用者之前作出一些修改，这需要使用到ConsumerPreResponseFilter。  
这些ConsumerPreRequestFilter 和 ConsumerPreResponseFilter 统一都由ConsumerFilterManager 注册管理与分发。同样的我们也不会直接去操作ConsumerFilterManager 而是使用 BulletConsumerContext 去间接注册。  
我们来简单看看ConsumerPreRequestFilter 和 ConsumerPreResponseFilter 的接口定义。  

![image](https://github.com/TonyYan666/Bullet-RPC/assets/17917997/afea8bc6-bf24-4857-9863-bbf1358fb6bf)  
ConsumerPreRequestFilter 定义

![image](https://github.com/TonyYan666/Bullet-RPC/assets/17917997/c0c6b19a-10aa-4363-9c2d-6c4db7049438)
ConsumerPreResponseFilter 定义

## ServerConnectionManager 连接管理器


Bullet RPC 框架是基于TCP长链接的RPC框架。Bullet Client 需要管理所有连接服务器的 Connection 生命周期。ServerConnectionManager 负责对 Bullet Server的连接创建、空闲连接的断开等。 
  
Bullet RPC 每次创建连接后都需要经历一次RPC的握手，来让服务端确认客户端的身份。Bullet Client 通过用户自定义的 HandshakeInfoProvider 来获得传递到 Server 的握手信息。  

![image](https://github.com/TonyYan666/Bullet-RPC/assets/17917997/e22229ba-a921-41e8-b664-40a68098085e)  
![image](https://github.com/TonyYan666/Bullet-RPC/assets/17917997/33e157aa-5150-4b78-93d2-7cb1046f4397)  


同样的HandshakeInfoProvider 也是通过BulletConsumerContext 配置。在SpringCloud 当中，Bullet RPC 已经实现了 SpringCloudHandshakeInfoProvider 来获得 instanceId 和 serviceId 的实例信息。但是如果你需要传入连接的认证信息的话，你仍然需要实现你自己的HandshakeInfoProvider，当然你也可以直接继承 SpringCloudHandshakeInfoProvider 然后重写 authorization 方法。  
  
ServerConnection 是我们服务端连接对象，ServerConnection是一个子类。ServerConnection 是一个多功能Connection类，其继承关系是  SimpleChannelInboundHandler > AbstractConnection > CommunicableConnection > HandshakeConnection > HeartbeatManageableConnection > ServerConnection 。每一层都提供了自己的扩展能力，使得ServerConnection 同样具备了 数据读取、通讯、握手、心跳检测等能力。我们一般RPC应用很少使用这个类，只需要知道ServerConnection是一个服务连接即可。  
  
ServerConnectionManager 把所有连接管理的工作都托管了，所以我们作为使用者并不需要关注 ServerConnectionManager的工作原理。但是值得我们注意的是，ServerConnectionManager 对于每一个远端服务只会创建一个连接，所有线程都是复用同一个连接而已，如果实际使用过程当中可能无法满足吞吐量可能会改造为多连接。  

![image](https://github.com/TonyYan666/Bullet-RPC/assets/17917997/99e80a61-4628-4892-b606-bc2ac39d251e)  


## ResponseErrorHandler 客户端异常处理器  


ResponseErrorHandler 主要是透过 BulletResponse 获得错误信息，从而转换成异常抛出到调用方当中。一般情况下我们并不需要实现自己实现 ResponseErrorHandler Bullet RPC 已经实现了最基础的  BaseResponseErrorHandler ，后续需要扩展更加对接直接继承 BaseResponseErrorHandler 去重写errorHandle 方法。  
  
![image](https://github.com/TonyYan666/Bullet-RPC/assets/17917997/f07160ad-db0f-4e71-8f82-b3bd3f150393)  

## ConsumerRequestInvoker 调用器
  
  
ConsumerRequestInvoker 是一个调用器接口，定义了发起RPC调用的两种模式，同步模式与异步模式。  

```
public interface ConsumerRequestInvoker {

    /**
     * 执行远程调用
     *
     * @return
     */
    BulletResponse<Object> invoke(ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request);

    /**
     * 异步执行远程调用
     *
     * @param methodDefinition
     * @param request
     * @param <T>
     * @return
     */
    BulletResponseFuture<?, Object> asyncInvoke(ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request, List<GenericFutureListener<Future<? super BulletResponse<Object>>>> listeners);


    /**
     * 响应式调用
     *
     * @param methodDefinition
     * @param request
     * @return
     */
    Mono<BulletResponse<Object>> reactiveInvoke(ConsumerMethodDefinition methodDefinition, BulletRequest<Object[]> request);
}
```


实际上我们不需要实现任何Invoker调用器的实现，Bullet RPC 已经提供了最基础的BaseConsumerRequestInvoker，完成最基本的RPC调用工作。这里我们只需要明白原理即可。 
  
BaseConsumerRequestInvoker 只是提供了最基础的RPC请求能力，里面包括ConsumerFilterManager的调用，ServerConnectionManager的连接获取，还有ResponseErrorHandler的异常处理调用等。但是除了这些最基础的RPC能力外，我们还需要实现负载均衡、调用降级、调用重试等能力。所以使用装饰者模式，通过对BaseConsumerRequestInvoker进行装饰实现上述能力的扩展。  
  
FallbackRequestInvoker 对 BaseConsumerRequestInvoker 装饰提供了调用异常降级；RetryRequestInvoker 的装饰提供了重试的调用能力；LoadBalancerRequestInvoker 的装饰提供了 SpringCloud 的微服务负载均衡调用能力。当然我们还可以实现自己的装饰者提供其他Invoker的增强，具体怎么可以参考 LoadBalancerRequestInvoker 的配置方式。  


## 整合一切的 ConsumerInvokeController 调用控制器  


ConsumerInvokeController 整合了一个Consumer 所需的任何组件，包括一个Consumer 的 ConsumerDefinition 定义，ServerConnectionManager 连接管理器，ConsumerFilterManager 请求过滤器管理器，当然还有ConsumerRequestInvoker。
  
ConsumerInvokeController 的执行流程 通过调用的Method反射对象获得一个ConsumerMethodDefinition，通过请求参数与ConsumerMethodDefinition 生成 BulletRequest，最后利用 ConsumerRequestInvokerFactory 生成一个被装饰好的 ConsumerRequestInvoker 实例，最后使用这个ConsumerRequestInvoker 实例调用invoke 方法发送一个RPC请求并返回一个BulletResponse，并使用ResponseErrorHandler 将错误抛出，或者直接将response的body对象返回出去。  


## ServiceConsumerProxyFactory 代理对象工厂  
  
ServiceConsumerProxyFactory 代码相当简单，利用JDK的 java.lang.reflect.Proxy类生个一个代理类。其中InvocationHandler的实现是ServiceConsumerProxyInvocationHandler。  
  
ServiceConsumerProxyFactory 和 ServiceConsumerProxyInvocation-Handler 都有一个共同的实例化参数，就是ConsumerInvokerController 这个是整个RPC调用的关键。代理对象只是一个漂亮的“壳”而已。  


## BulletConsumerContext RPC调用者上下文句柄  

  
配置一个Consumer 也没有那么复杂，大部分的工作都由BulletConsumerContext帮你实现了，对于使用者来说，只需要将自己需要增强的组件配置更换，其他的东西都交由BulletConsumerContext替你完成。  
我们最常用的只有一个方法 getServiceConsumerProxy。这个方法只需要将你的Consumer接口class对象作为参数即可，你将会得到一个完全配置好的Consumer代理类实例。  


![image](https://github.com/TonyYan666/Bullet-RPC/assets/17917997/1015ee35-4eae-4d2a-885a-f2879f1f80a4)

除生成一个接口代理类去远程调用，我们还可以通过BulletConsumerContext.createDynamicConsumerInvoker 方法去创建一个 DynamicConsumerInvoker 对象去动态调用任何Provider 的方法。以下是调用的例子

```
    public static void dynamicInvoker() {
        BulletConsumerContext context = DefaultBulletConsumerContextBuilder.create().build();
        DynamicConsumerInvoker consumer = context.createDynamicConsumerInvoker();
        SubRpcRequest request = new SubRpcRequest();
        request.setDemoContent("test");
        request.setIndex(1);
        request.setSubIndex(2);
        request.setOther("BBB");
        for (int i = 0; i < 100; i++) {
            DemoRpcResponse response = (DemoRpcResponse) consumer.setServerAddress("bullet://localhost")
                    .setPath("/DemoServiceProvider", "request")
                    .setReturnType(DemoRpcResponse.class)
                    .setPreferResponseActualType(true)
                    .setTransportArgumentsTypes(true)
                    .setRetry(3)
                    .setRetryIntervalMs(1000)
                    .reactiveInvoke(request, "aaa", i).block();
            System.out.println(response);
        }
```

说到这里基本上Bullet RPC Consumer 部分算是讲清楚了。但是我们实际的开发环境上都是基于Spring的基础框架上去开发的，所有Bullet RPC 对SpringBoot 和 SpringCloud 都做了大量的自动配置与功能整合。  


# Bullet RPC Spring-boot-starter  


Bullet RPC 从设计之初就注重对Spring的支持。Bullet RPC 所有组件都是可以通过Spring 配置的方式重新增强，给了使用者非常大的自由度。其实 bullet-rpc-spring-boot-starter 主要是对配置做了一次封装，添加大量的 AutoConfig 自动配置类。  
bullet-rpc-spring-boot-starter 首先对 BulletConsumerContext 和 BulletProviderContext 分别做了增强，分别是 SpringBulletConsumerContext 和 SpringBulletProviderContext。  
另外 bullet-rpc-spring-boot-starter，对标记 @ServiceProvider 的类实现了自动 Provider 注册同时将Provider 的类全部注册Spring IOC 容器当中，这意味着可以将标记 @ServiceProvider 的类看做Spring Bean 来处理。对标记了@ServiceConsumer 的接口，会自动生成代理类并注册到Bullet Consumer 当中。而且生成的代理对象也会将其注册到Spring IOC 容器当中，这也意味着你可以在所有的Spring Bean 里面依赖注入打了 @ServiceProvider 的接口。  
但是这些都不需要我们处理，所有的东西只需要关注 BulletConfigAdapter接口即可。  


## BulletConfigAdapter 配置适配器	  


BulletConfigAdapter 是一个配置适配器，所有 Bullet RPC 的配置都可以通过实现该接口的config方法进行配置。我们先简单看看BulletConfigAdapter的源码：  

```
/**
 * Bullet RPC 框架配置器
 */
public interface BulletConfigAdapter {

    /**
     * 配置rpc框架
     * @param config
     */
    void config(BulletRpcConfig config);

}
```

里面非常简单只有一个config方法，config方法里面有一个参数BulletRpcConfig 的对象。通过修改BulletRpcConfig的参数即可完成Bullet RPC 所有配置。  
一般情况下允许我们不止一个 BulletRpcConfig 的实现，Bullet RPC 框架会按照 order 排序逐一调用来完成整个配置。下面截取BulletCommonAutoConfig的一段源码来深入理解 BulletRpcConfig 是如何完成配置。  

![image](https://github.com/TonyYan666/Bullet-RPC/assets/17917997/9e086e75-0682-4cb8-915c-faf0e126d4e4)  


可以看出 BulletCommonAutoConfig 是通过获得所有实现 BulletConfigAdapter 接口的Bean 然后逐一调用来完成 BulletRpcConfig 对象的生成。  
Bullet RPC SpringCloud 框架内部也是通过 BulletConfigAdapter 来实现SpringCloud支持的配置。  


![image](https://github.com/TonyYan666/Bullet-RPC/assets/17917997/93a5f7a2-38b1-4264-8ef3-4c85c50c5bfb)


## @ScanBulletConsumer & @ScanBulletProvider  


默认情况下，Bullet RPC Spring 环境下会自动扫描 Application 包范围内的所有类，来实现自动 Consumer 和 Provider 的注册。但是有时我们的提供的Consumer是由其他module 或者是 maven 提供的，由于跟当前应用的包不一致可能会导致无法扫描，此时我们可以通过@ScanBulletConsumer 和 @ScanBulletProvider来指定 Consumer 和 Provider的位置。  
当然 @ScanBulletConsumer 和 @ScanBulletProvider 也是支持多次配置的，也就是说Bullet RPC 会取所有 @ScanBulletConsumer 和 @ScanBulletProvider 的 packages 的并集作为扫描的目标位置。  


## 关键参数配置  

- 提供者配置

```
smile.framework.bullet.server.port = 端口号 模式 2186
smile.framework.bullet.server.workCorePoolSize =  工作线程数（默认15）
smile.framework.bullet.server.workMaxPoolSize = 最大工作线程数（默认40）
smile.framework.bullet.server.workBlockingQueueSize = 工作任务队列大小 默认 10
smile.framework.bullet.server.enableTransportAuthentication = 是否开始连接认证 默认false
smile.framework.bullet.server.shutdownUnauthenticationConnection = 是否自动断开认证失败的连接 默认false
smile.framework.bullet.server.enableInvokeLog = 是否启用调用日志 默认true
```

  
- 消费者配置  

```
smile.framework.bullet.client.handshakeTimeout = 握手超时时间默认3秒
smile.framework.bullet.client.connectionTimeout = 连接创建超时时间 默认1秒
smile.framework.bullet.client.idleTimeout = 连接空闲超时时间 默认半小时
smile.framework.bullet.client.heartbeatTimeout = 心跳超时时间 默认1秒
smile.framework.bullet.client.enableRequestLog = 是否开启请求日志 默认true
```

# Bullet RPC Spring-cloud-starter  

Bullet RPC Spring-cloud-starter 主要支持了SpringCloud服务注册与发现，能够实现利用服务名调用RPC提供者。另外 Bullet RPC Spring-cloud-starter 还能直接支持 Spring-cloud 的负载均衡能力。  
  
Bullet RPC SpringCloud 的支持非常简单，并不需要我们做什么配置。只需要依赖相关maven模块即可。由于SpringCloud 新旧版本在负载均衡部分代码差异比较大，所以我们提供了两个不同版本的maven依赖来支持新旧版本的SpringCloud 项目  


# Bullet RPC maven 依赖

```
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>bullet-parent</artifactId>
        <groupId>com.smileframework.bullet</groupId>
        <relativePath>../pom.xml</relativePath>
        <version>2.0.0-RELEASE</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>
    <packaging>pom</packaging>
    <artifactId>bullet-dependencies</artifactId>

    <properties>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>com.smileframework.bullet</groupId>
                <artifactId>bullet-transport</artifactId>
                <version>${project.version}</version>
            </dependency>
            <dependency>
                <groupId>com.smileframework.bullet</groupId>
                <artifactId>bullet-transport-common</artifactId>
                <version>${project.version}</version>
            </dependency>
            <dependency>
                <groupId>com.smileframework.bullet</groupId>
                <artifactId>bullet-transport-server</artifactId>
                <version>${project.version}</version>
            </dependency>
            <dependency>
                <groupId>com.smileframework.bullet</groupId>
                <artifactId>bullet-transport-client</artifactId>
                <version>${project.version}</version>
            </dependency>
            <dependency>
                <groupId>com.smileframework.bullet</groupId>
                <artifactId>bullet-rpc</artifactId>
                <version>${project.version}</version>
            </dependency>
            <dependency>
                <groupId>com.smileframework.bullet</groupId>
                <artifactId>bullet-rpc-spring-boot-starter</artifactId>
                <version>${project.version}</version>
            </dependency>
            <dependency>
                <groupId>com.smileframework.bullet</groupId>
                <artifactId>bullet-rpc-spring-cloud-starter</artifactId>
                <version>${project.version}</version>
            </dependency>
            <dependency>
                <groupId>com.smileframework.bullet</groupId>
                <artifactId>bullet-rpc-old-version-spring-cloud-starter</artifactId>
                <version>${project.version}</version>
            </dependency>
        </dependencies>
    </dependencyManagement>

</project>

```














