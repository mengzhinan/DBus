## DBus一个简易、高效的消息框架。没看过EventBus的源码，但是个人感觉此项目够简洁高效、易懂易用!

## [我的CSDN博客](http://blog.csdn.net/fesdgasdgasdg/article/details/79121783 "文章地址")

## DBus项目突出的特点：
* 1、注册消息事件的类可以是任意类(可能别人的项目也是)，不限定是Activity等特殊的类。<br/>
只要你保证register和unRegister方法成对调用。<br/>
否则DBus会一直持有此对象引用，可能会引起内存泄漏。<br/>

* 2、支持使用特定方法名的函数接收消息；支持通过注解的方式标识某方法，使其变成消息接收的方法。<br/>
如果方法前面有@DBusInject()注解，则此方法被当做DBus的注解方法处理；<br/>
如果没有发现@DBusInject()注解，则在根据方法名限定规则来检查是否符合要求；<br/> 
方法名限定规则为以onUIEvent开头或者onThreadEvent开头，分别表示在主线程和子线程接收消息的方法。<br/>

* 3、使用方法名限定的方式，记住如下限定规则：<br/>
	* a、方法名只能是以onUIEvent或onThreadEvent开头；<br/>
	* b、如果方法名以onUIEvent开头，表明此方法的执行是在UI线程，可以更新UI控件；<br/>
	* c、如果方法名以onThreadEvent开头，表明此方法是在子线程执行的，不可更新UI，但可做耗时操作；<br/>
	* d、方法只能有一个参数。并且方法的参数类型必须是DData类型，不管消息发送处传递的是DData类还是子类对象；<br/>
	* e、父类或接口的方法无效，必须是当前类里面定义的方法。<br/>

示例方法：<br/>
> private void onUIEventXXX(DData data){<br/>

> }<br/>
当然，建议的写法是：<br/>
> public void onUIEventXXX(DData data){<br/>
>>  //公共，无返回值<br/>
> }<br/>

以下是非限定条件：<br/>
	* a、方法的修饰符任意，可以是友好的、public、private、protected、static、final等；<br/>
	* b、方法的返回值任意，可以是void、int、String等；<br/>
	* c、上面说了方法名限定开头，但不限定结尾，比喻可以是onUIEventForVideo(DData data)。<br/>

### 4、使用注解方式，有以下规则：
<br/>
	* a、必须在方法前面设置注解@DBusInject()，注解有两个参数port和thread。<br/>
	port：为必填项，参数值自定义设置。如果此方法的注解port值与发送处DData对象的port值一致，才能收到发送的消息。<br/>
	thread：选填项，参考DThreadType常量值，参数值为0、1和2，分别代表主线程、当前子线程和新的子线程。<br/>
	即此方法是在UI线程还是在子线程执行。默认值为0，在主线程执行，即可以更新UI控件。<br/>
	* b、方法只能有一个参数。并且方法的参数类型必须是DData类型，不管消息发送处传递的是DData类还是子类对象。(同上)<br/>
	* c、父类或接口的方法无效，必须是当前类里面定义的方法。(同上)<br/>
    

以下是非限定条件：<br/>
	* a、[亮点]方法名任意；比喻haha(DData data);<br/>
	* b、方法的修饰符任意，可以是友好的、public、private、protected、static、final等；(同上)<br/>
	* c、方法的返回值任意，可以是void、int、String等；(同上)<br/>

示例方法：<br/>
> @DBusInject(port = 1, thread = DThreadType.CURRENT_CHILD_THREAD)<br/>
> private static int haha(DData data){<br/>
>>  return 23;<br/>
> }<br/>
当然，建议的写法是：<br/>
> @DBusInject(port = 1)<br/>
> public void updateTextView(DData data){<br/>
>>  //公共，无返回值，方法名有意义，默认在UI线程执行<br/>
> }

## DBus项目环境集成：
这个应该不用说，做过android的朋友都知道。<br/>
* 1、把DBus源码搬进你的项目中。<br/>
* 2、把DBus库打包成jar包，方到你项目的libs中，记得添加引用。<br/>
* 3、把DBus库打包成aar，方到你项目的libs中，记得添加引用。<br/>
当然，用aar没意义，因为DBus项目中没有资源文件。<br/>

## DBus使用教程：
## 一、订阅者：
* 1、订阅消息：<br/>
在类的初始化方法注册事件，比喻Activity的onCreate方法中注册：<br/>
> DBus.getBus().register(this);

* 2、取消订阅<br/>
在类的结束方法反注册，比喻Activity的onDestory方法中反注册：<br/>
> DBus.getBus().unRegister(this);<br/>

* 3、在当前类的某位置添加接收消息的方法：<br/>
> public void onUIEventImageView(DData data){<br/>
>>  //示例方法，非标准<br/>
>>  //如果data是你自定义的子类<br/>
>>  YourData yData = (YourData)data;<br/>
> }<br/>
或<br/>
> @DBusInject(port = 23)<br/>
> public void dBusUpdateImageView(DData data){<br/>
>>  //示例方法，非标准<br/>
>>  //如果data是你自定义的子类<br/>
>>  YourData yData = (YourData)data;<br/>
> }

## 二、发布者：
可以在任意线程、任意位置发送消息。只要你确保接收消息的对象没有调用unRegister反注册方法，就能100%接收到消息。注意DData构造函数的port参数，及注解中的port参数。<br/>
发送消息示例：<br/>
* 1、最简方式：<br/>
> DBus.getBus().post(new DData(1));<br/>

* 2、携带参数：<br/>
> DData data = new DData(DData.PORT_RECEIVE_METHOD_NAME, DData.THREAD_UI);<br/>
> data.str1 = "成功";<br/>
> data.int1 = 40;<br/>
> DBus.getBus().post(data);<br/>

* 3、携带自定义类的参数(注意：MyData extends DData)：<br/>
> MyData data = new MyData(DData.PORT_RECEIVE_ALL);<br/>
> data.str1 = "成功";<br/>
> data.int1 = 40;<br/>
> data.myValue = "自定义属性";<br/>
> DBus.getBus().post(data);<br/>


## 注意事项，及哪些订阅者能收到消息：<br/>
* 1、注意消息接收方法的方法名限定方式，有UI线程和子线程两种类型方法；<br/>

* 2、注意消息接收方法的注解方式，有两个参数，端口和线程。port端口值建议大于0，至少不能等于DData.PORT_RECEIVE_METHOD_NAME = -1和DData.PORT_RECEIVE_ALL = 0两个常量值；thread值只建议使用DThreadType接口里面的3个常量值，分别表示0-UI线程，1-当前子线程，2-new子线程。<br/>

* 3、发送方法DBus.getBus().post(data)的data参数，注意DData的构造函数public DData(int port, int thread){...}。<br/>
port参数值：<br/>
DData.PORT_RECEIVE_METHOD_NAME，表示只有方法名限定方式的消息接收方法能收到。<br/>
DData.PORT_RECEIVE_ALL，表示所有的消息接收方法都能收到。<br/>
如果不是以上值，则只有注解的方法，且port相等的才能收到。<br/>

thread参数值：<br/>
DData.THREAD_UI，表示只有UI线程的消息接收方法能收到。<br/>
DData.THREAD_ALL，表示所有的消息接收方法都能收到。<br/>
DData.THREAD_CHILD，表示只有子线程的消息接收方法能收到。<br/>


此处两个参数很关键，可以双重限制已达到特殊的一部分方法能收到消息的目的。<br/>


* 4、已经反注册的订阅者不会收到任何消息，无管是注解方式，还是方法名限定方式。因为DBus已经不再持有反注册的订阅者对象了。<br/>

感谢各位码友支持！<br/>
不要问为什么项目叫DBus，请原谅我自私的用了字母D。


