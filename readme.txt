gradle build的问题

project/gradle/wrapper/gradle-wrapper.properties

distributionUrl=https\://services.gradle.org/distributions/gradle-3.3-all.zip
对应\wangweijun\.gradle\wrapper\dists\底下的版本


top level build.gradle

com.android.tools.build:gradle:2.3.3  对应 studio\gradle\m2repository\com\android\tools\build\gradle


Rxjava 基于事件驱动的框架

1，注册完成自动登陆 使用 flatMap，查看ApiService

2, 一个界面多个请求，只有当全部请求完之后才显示UI，zip 函数完成


Android平台开源框架，了解EventBus，Retrofit，Volley，OKHTTP，Picasso，Dagger，RxJava第三方开源工具；

MVP 解耦

rxjava 数据的流向清晰

上游  ----中间处理map---> 下游

非常打脸的一句话，5年工作经验，其实一年经验4次copy,很危险

学习一个技术，为什么要学习它，好处是什么，更加深一点的是，怎么实现的(大牛实现的),善于总结

Observer, Observer
rxjava 核心思想是观察者(observer)与被观察者模式(observable),

rxjava 很NB的地方，就是线程自由的控制，

1, 观察者模式--(扩展的)
2, Scheduler (线程控制，线程调度)
3, 变换 (map函数操作)

1, 链式调用
2, 线程切换
3, 数据转换

observalbe observer rxjava是扩展得观察者模式,线程切换非常方便, map, zip 函数等等操作
非常方便, 有上游下游的概率

map 转化操作(String ----> Integer)

backpress (Flowable) 下游告诉上游自己能处理多少个事件，超过设定的值收不到

zip 合并两个请求(并发),适合在一个界面需要多个请求的时候，只有当所有请求成功后才会有调用下游的observer.onNext()方法

flatmap --->窜行,两个api有依赖，比如注册并自动登陆(登陆需要用到注册返回的参数)

concatMap 请求一个列表list,可以一个一个发送到observer,一个一个的显示










