[程序员之眼](http://www.1024eye.com)的Android客户端，界面使用了[material](https://github.com/rey5137/material)框架，以及[LDrawer](https://github.com/ikimuhendis/LDrawer)由于服务器流量有限，所以此源码和上线的正式版本调用的接口有差别，开放的接口每天只接受500次请求，超过500就不会返回数据，目前只有最基本的功能，因为时间比较少（空闲时间）
目前拥有功能
1.文章列表（优先从缓存中获取，再获取网络，如果在缓存时间内，则不会从网络获取），且区分已读和未读
2.文章阅读
3.夜间模式
4.意见与建议
5.自动更新（多线程断点更新）
主界面
![主界面](http://git.oschina.net/uploads/images/2015/0909/171506_9517591a_108170.png "主界面")