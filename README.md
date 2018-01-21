## DBus一个简易、高效的消息框架。没看过EventBus的源码，但是个人感觉此项目够简洁高效、易懂易用!

## [我的CSDN博客](http://blog.csdn.net/fesdgasdgasdg/article/details/79121783 "文章地址")

## DBus项目突出的特点：
* 1、注册消息事件的类可以是任意类(可能别人的项目也是)，不限定是Activity等特殊的类。
只要你保证调用register和unRegister一对方法。<br/>
否则DBus会一直持有此对象引用，可能会引起内存泄漏。<br/>

* 2、支持使用特定方法名的函数接收消息；支持通过注解的方式标识某方法，使其变成消息接收方法。
使用下面方法设置是否使用方法名限定模式的方法，默认值为false，即使用注解方式。<br/>
DBus.isUseMethodNameFind(true);//默认值为false<br/>
注意：使用特定方法名或注解方式，二选一，不可兼得。一旦设置，立即生效。<br/>

* 3、使用方法名限定的方式，记住如下限定规则：
* a、记得打开方法名限定开关：DBus.isUseMethodNameFind(true);<br/>
* b、方法名只能是以onUIEvent或onThreadEvent开头；<br/>
* c、如果方法名以onUIEvent开头，表明此方法的执行是在UI线程，可以更新UI控件；<br/>
* d、如果方法名以onThreadEvent开头，表明此方法是在子线程执行的，不可更新UI，但可做耗时操作；<br/>
* e、方法只能有一个参数。并且方法的参数类型必须是DData类型，不管消息发送处传递的是DData类还是子类对象；<br/>
* f、父类或接口的方法无效，必须是当前类里面定义的方法。<br/>

示例方法：<br/>
private void onUIEventXXX(DData data){<br/>

}<br/>
当然，建议的写法是：<br/>
public void onUIEventXXX(DData data){<br/>
	//公共，无返回值<br/>
}<br/>

以下是非限定条件：<br/>
* a、方法的修饰符任意，可以是友好的、public、private、protected、static、final等；<br/>
* b、方法的返回值任意，可以是void、int、String等；<br/>
* c、上面说了方法名限定开头，但不限定结尾，比喻可以是onUIEventForVideo(DData data)。<br/>

### 4、使用注解方式，有以下规则：
* a、关闭方法名限定开关：DBus.isUseMethodNameFind(false);其实默认就是false的。<br/>
* b、必须在方法前面设置注解@DBusInject()，注解有两个参数port和thread。<br/>
	port：为必填项，参数值自定义设置。如果此方法的注解port值与发送处DData对象的port值一致，才能收到发送的消息。<br/>
	thread：选填项，参考DThreadType常量值，参数值为0或1，分别代表主线程和子线程。即此方法是在UI线程还是在子线程执行。默认值为0，在主线程执行。即可以更新UI控件。<br/>
* c、方法只能有一个参数。并且方法的参数类型必须是DData类型，不管消息发送处传递的是DData类还是子类对象。(同上)<br/>
* d、父类或接口的方法无效，必须是当前类里面定义的方法。(同上)<br/>

以下是非限定条件：<br/>
* a、[亮点]方法名任意；比喻haha(DData data);<br/>
* b、方法的修饰符任意，可以是友好的、public、private、protected、static、final等；(同上)<br/>
* c、方法的返回值任意，可以是void、int、String等；(同上)<br/>

示例方法：<br/>
@DBusInject(port = 1, thread = DThreadType.CHILD_THREAD)<br/>
private static int haha(DData data){<br/>
	return 23;<br/>
}<br/>
当然，建议的写法是：<br/>
@DBusInject(port = 1)<br/>
public void updateTextView(DData data){<br/>
	//公共，无返回值，方法名有意义，默认在UI线程执行<br/>
}

## DBus项目环境集成：
这个应该不用说，做过android的朋友都知道。
* 1、把DBus源码搬进你的项目中。
* 2、把DBus库打包成jar包，方到你项目的libs中，记得添加引用。
* 3、把DBus库打包成aar，方到你项目的libs中，记得添加引用。
当然，用aar没意义，因为DBus项目中没有资源文件。

## DBus使用教程：
## 一、订阅者：
* 1、订阅消息：
在类的初始化方法注册事件，比喻Activity的onCreate方法中注册：<br/>
DBus.getBus().register(this);

* 2、取消订阅
在类的结束方法反注册，比喻Activity的onDestory方法中反注册：<br/>
DBus.getBus().unRegister(this);

* 3、在当前类的某位置添加接收消息的方法：
根据你对开关DBus.isUseMethodNameFind(boolean)的设置，选择合适的方法：<br/>
public void onUIEventImageView(DData data){<br/>
	//示例方法，非标准<br/>
	//如果data是你自定义的子类<br/>
	YourData yData = (YourData)data;<br/>
}<br/>
或<br/>
@DBusInject(port = 23)<br/>
public void dBusUpdateImageView(DData data){<br/>
	//示例方法，非标准<br/>
	//如果data是你自定义的子类<br/>
	YourData yData = (YourData)data;<br/>
}

## 二、发布者：
可以在任意线程、任意位置发送消息。只要你确保接收消息的对象没有调用unRegister反注册方法，就能100%接收到消息。注意DData构造函数的port参数，及注解中的port参数。<br/>
发送消息示例：
* 1、最简方式：
DBus.getBus().post(new DData(0));

* 2、携带参数：
DData data = new DData(0);<br/>
data.str1 = "成功";<br/>
data.int1 = 40;<br/>
DBus.getBus().post(data);

* 3、携带自定义类的参数(注意：MyData extends DData)：
MyData data = new MyData(54);<br/>
data.str1 = "成功";<br/>
data.int1 = 40;<br/>
data.myValue = "自定义属性";<br/>
DBus.getBus().post(data);


## 哪些订阅者能收到消息：
* 1、如果是注解方式，即DBus.isUseMethodNameFind(false)
则@DBusInject(port)和DBus.getBus().post(new DData(port));两处port值相等的地方才能够收到消息。

* 2、如果是方法名限定方式，即DBus.isUseMethodNameFind(true)
则所有的订阅者类中，以onUIEvent和onThreadEvent开头的所有方法都能够收到消息。<br/>
此时，你可以在具体的方法里面判断自定义的port值规则或DData里面的值，以决定哪些具体的方法才能够处理此事件。

* 3、已经反注册的订阅者不会收到任何消息，无管是注解方式，还是方法名限定方式。因为DBus已经不再持有反注册的订阅者对象了。

感谢各位码友支持！<br/>
不要问为什么项目叫DBus，请原谅我自私的用了字母D。


