package com.raywang.rayutils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ray on 2015/8/21.
 */
public class HttpCoreThread extends Thread {
    private static final String TAG = HttpCoreThread.class.getName();
    /** 缓存数据返回*/
    public static final int ONCACHEDATA = 1;
    /** 网络数据返回*/
    public static final int ONINTERNET = 2;
    /** 当有错误产生*/
    public static final int ONERROR = 3;
    /** get请求*/
    public static final int REQUESTGET = 1;
    /** post请求*/
    public static final int REQUESTPOST = 2;
    /** 请求的网址，不含page,否则缓存删除会有数据删除不掉*/
    private String url;
    /** 请求的参数*/
    private HashMap<String,Object> params;
    /** 数据返回的监听*/
    private HttpListener listener;
    private CacheDB cacheDB;

    /** 是否是刷新数据*/
    private boolean isRef = false;
    /** 缓存需要移除的值*/
    private ArrayList<String> removeKeys;
    /** 分页的params关键字*/
    private String pageKey;
    /** 缓存有效期（为0每次请求会从服务器拿去数据，但是也会先返回本地缓存）*/
    private long overdueTime;
    /** 主线程的handler*/
    private Handler handler;
    /** 请求的方式,默认是get*/
    private int requestType = REQUESTGET;
    /** 是否停止线程*/
    private boolean isStop = false;
    /** 请求码，默认为0，用于多个请求使用同一个回调方法，用于区分请求*/
    private int requestCode = 0;

    ///////////////////////////////////http请求相关参数//////////////////////////////
    /** 超时时间*/
    private static final int TIMEOUT = 3000;
    /** 读取数据的超时时间*/
    private static final int READTIMEOUT = 60000;
    /** 编码格式*/
    private static final String CHARSET = "utf-8";

    private Handler.Callback callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if(listener != null) {
                switch (msg.what) {
                    case ONCACHEDATA:
                        listener.onCacheData(requestCode,msg.getData().getString("data"));
                        break;
                    case ONINTERNET:
                        listener.onInternet(requestCode,msg.getData().getString("data"));
                        break;
                    case ONERROR:
                        listener.onError(requestCode,msg.getData().getInt("code"),msg.getData().getString("e"));
                        break;
                }
            }
            return true;
        }
    };

    public void setStop(){
        this.isStop = true;
    }

    /**
     * 创建http请求实例
     * @param context 程序上下文
     * @param url 请求的网址
     * @param listener 回调监听
     * @param params 请求的参数
     * @param removeKeys 需要被移除的参数（如分页关键字和随机数），主要用户缓存
     * @param pageKey 分页的关键字
     */
    public HttpCoreThread(Context context,String url,HttpListener listener,
                          HashMap<String,Object> params,ArrayList<String> removeKeys,
                          String pageKey){
        handler = new Handler(context.getMainLooper(),callback);
        cacheDB = CacheDB.newInstance(context);
        this.url = url;
        this.listener = listener;
        this.pageKey = pageKey;
        this.removeKeys = removeKeys;
        this.params = params;
    }

    /**
     * 创建http请求实例
     * @param context 程序上下文
     * @param url 请求的网址
     * @param listener 回调监听
     * @param params 请求的参数
     * @param removeKeys 需要被移除的参数（如分页关键字和随机数），主要用户缓存
     * @param pageKey 分页的关键字
     * @param overdueTime 缓存过期的时间
     */
    public HttpCoreThread(Context context,String url,HttpListener listener,
                          HashMap<String,Object> params,ArrayList<String> removeKeys,
                          String pageKey,long overdueTime){
        handler = new Handler(context.getMainLooper(),callback);
        cacheDB = CacheDB.newInstance(context);
        this.url = url;
        this.listener = listener;
        this.pageKey = pageKey;
        this.removeKeys = removeKeys;
        this.params = params;
        this.overdueTime = overdueTime;
    }

    /** 发起post请求*/
    public HttpCoreThread post(){
        this.requestType = REQUESTPOST;
        start();
        return this;
    }

    /** 发起post请求*/
    public HttpCoreThread post(int requestCode){
        this.requestType = REQUESTPOST;
        this.requestCode = requestCode;
        start();
        return this;
    }

    /** 发起get请求*/
    public HttpCoreThread get(){
        this.requestType = REQUESTGET;
        start();
        return this;
    }

    /** 发起get请求*/
    public HttpCoreThread get(int requestCode,boolean isRef){
        this.isRef = isRef;
        this.requestCode = requestCode;
        this.requestType = REQUESTGET;
        start();
        return this;
    }

    @Override
    public void run() {
        switch (requestType){
            case REQUESTGET:
                requestGet();
                break;
            case REQUESTPOST:
                requestPost();
                break;
            default:
                requestGet();
                break;
        }
    }

    /**
     * 发送get请求，只有get请求才会有缓存
     */
    private void requestGet(){
        try {

            url +="?" + paramsToString(params,removeKeys);
            String cacheData = null;

            int page = 0;
            if(pageKey != null && params.containsKey(pageKey)){
                page = Integer.parseInt(params.get(pageKey).toString());
            }
            if(!isRef) {
                //从缓存中拿取数据
                cacheData = cacheDB.findByUrl(url, page);
                if (cacheData != null) {
                    //已经拿到缓存数据了
                    Message m = handler.obtainMessage(ONCACHEDATA);
                    m.getData().putString("data", cacheData);
                    sendMessage(m);
                }
            }
            if(!cacheDB.findByUrl(url,page,overdueTime) || isRef){
                //缓存已经过期，需要重新获取
                String tempUrl = url;
                if(removeKeys != null && removeKeys.size() > 0 && params != null){
                    for(String str : removeKeys){
                        tempUrl += "&"+str+"="+params.get(str);
                    }
                }
                String data = get(tempUrl);

                if(data != null){
                    cacheDB.insert(data,url,page);
                    Message m = handler.obtainMessage(ONINTERNET);
                    m.getData().putString("data",data);
                    sendMessage(m);
                }

            }
        }catch (Exception e){
            Message m = handler.obtainMessage(ONERROR);
            m.getData().putInt("code",1);
            m.getData().putString("e", e.getMessage());
            sendMessage(m);
            e.printStackTrace();
        }
    }
    /** post请求*/
    private void requestPost(){
        String data = post(url);
        if(data != null){
            Message m = handler.obtainMessage(ONINTERNET);
            m.getData().putString("data",data);
            sendMessage(m);
        }


    }

    /**
     * 发送handler消息，如果线程已经被要求停止，则不发送
     * @param msg 消息对象
     */
    private void sendMessage(Message msg){
        if(!isStop){
            handler.sendMessage(msg);
        }
    }

    /**
     * post请求
     * @param url 请求的地址
     * @return 返回值,返回null时代表出错了
     */
    public String post(String url){
        URL httpUrl = null;
        HttpURLConnection conn = null;
        OutputStreamWriter osw;
        InputStreamReader isr;
        int code = 0;
        try {

            httpUrl = new URL(url);
            conn = (HttpURLConnection) httpUrl.openConnection();
            //设置可以接收数据
            conn.setDoInput(true);
            //设置可以输出数据
            conn.setDoOutput(true);
            //设置连接超时时间
            conn.setConnectTimeout(TIMEOUT);
            //设置获取数据的超时时间
            conn.setReadTimeout(READTIMEOUT);
            //设置请求方式
            conn.setRequestMethod("POST");
            //设置请求的编码格式
            conn.setRequestProperty("Accept-Charset", CHARSET);
            osw = new OutputStreamWriter(conn.getOutputStream());

            osw.write(paramsToString(params));
            osw.close();
            code = conn.getResponseCode();
            if (code == 200 && !isStop) {
                isr = new InputStreamReader(conn.getInputStream());
                StringBuffer sb = new StringBuffer();
                char[] buf = new char[1024];
                int len = 0;
                while ((len = isr.read(buf, 0, 1024)) > 0 && !isStop) {
                    sb.append(buf, 0, len);
                }
                isr.close();
                return sb.toString();
            }else{
                Message m = handler.obtainMessage(ONERROR);
                m.getData().putInt("code",1);
                m.getData().putString("e", "responseCode" + code);
                sendMessage(m);
            }
        }catch (Exception e){
            Message m = handler.obtainMessage(ONERROR);
            m.getData().putInt("code",1);
            m.getData().putInt("responseCode",code);
            m.getData().putString("e", e.getMessage());
            sendMessage(m);
            e.printStackTrace();
        }finally {
            if(conn != null){
                conn.disconnect();
            }
        }
        return null;
    }


    public String get(String url){
        URL httpUrl = null;
        HttpURLConnection conn = null;
        InputStreamReader isr;
        int code = 0;
        try {
            httpUrl = new URL(url);
            Util.logi(TAG, "get request url:" + url);
            conn = (HttpURLConnection) httpUrl.openConnection();
            //设置可以接收数据
            conn.setDoInput(true);
            //设置可以输出数据
            conn.setDoOutput(true);
            //设置连接超时时间
            conn.setConnectTimeout(TIMEOUT);
            //设置获取数据超时时间
            conn.setReadTimeout(READTIMEOUT);
            //设置请求方式
            conn.setRequestMethod("GET");
            //设置请求的编码格式
            conn.setRequestProperty("Accept-Charset", CHARSET);

            code = conn.getResponseCode();
            if(code == 200 && !isStop){
                isr = new InputStreamReader(conn.getInputStream());
                StringBuffer sb = new StringBuffer();
                char[] buf = new char[1024];
                int len = 0;
                while((len=isr.read(buf,0,1024)) > 0 && !isStop){
                    sb.append(buf,0,len);
                }
                isr.close();
                return sb.toString();
            }else{
                Message m = handler.obtainMessage(ONERROR);
                m.getData().putInt("code",1);
                m.getData().putString("e","responseCode"+code);
                sendMessage(m);
            }
        }catch (Exception e){
            Message m = handler.obtainMessage(ONERROR);
            m.getData().putInt("code",1);
            m.getData().putInt("responseCode",code);
            m.getData().putString("e", e.getMessage());
            sendMessage(m);
            e.printStackTrace();
        }finally{
            if(conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }

    public String paramsToString(HashMap<String, Object> params) throws UnsupportedEncodingException {
        return paramsToString(params,null);
    }

    /**
     * 将参数转换为GET请求格式的字符串
     * @param params 请求的参数
     * @return
     * @throws java.io.UnsupportedEncodingException
     */
    public String paramsToString(HashMap<String, Object> params,ArrayList<String> removeKey) throws UnsupportedEncodingException{
        if(params == null){
            return "";
        }
        String str = "";
        for(Map.Entry<String, Object> entry : params.entrySet()){
            if(removeKey != null && removeKey.contains(entry.getKey())){
                continue;
            }
            str +=entry.getKey()+"="+ URLEncoder.encode(entry.getValue().toString(), CHARSET)+"&";
        }
        str = str.substring(0,str.length() - 1);
        return str;
    }
    /**
     * 网络监听
     */
    public interface HttpListener{
        /** 缓存的数据发生改变*/
        public void onCacheData(int requestCode, String data);

        /**
         * 有错误发生
         * @param code 错误码
         * @param e 异常报告
         */
        public void onError(int requestCode, int code, String e);

        /**
         * 网络数据换回
         * @return
         */
        public void onInternet(int requestCode, String data);
    }
}
