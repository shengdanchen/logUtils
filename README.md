# logUtils
将日志生成到本地存储的工具，可用于复杂问题的排查

<h3>  添加仓库<h3/>
<font>maven { url 'https://jitpack.io' }<font/>
<h3>  设置依赖<h3/>
implementation 'com.github.shengdanchen:logUtils:$version'<br/>
<br/>
<h3>  两种方式:<h3/>
1、/storage/emulated/0(sdcard1)/Android/data/"packageName"/files/Logs 目录下指定日志内容写入文件<br/>
<p><p/>
LogUtils.init(this);//在Application中初始化，传入上下文 <br/>
LogUtils.info("This is tag","This is content");//写入文件<br/>

2、开启子线程执行shell命令监听应用系统的日志并写入指定目录下<br/>
<p><p/>
LogThread.startLogThread(getExternalFilesDir("/myLogFile/").getPath());//指定输出的目录并开启日志监听<br/>
<br/>
<h3>详见如上demo<h3/>
