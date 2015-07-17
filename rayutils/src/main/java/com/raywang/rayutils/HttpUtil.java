package com.raywang.rayutils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Http请求的工具类
 * @author Ray Wang
 * @date 2015年4月18日16:01:05
 * @version 1.0
 */
public class HttpUtil {

    private static final boolean ISDEBUG = true;
    private static final String TAG = HttpUtil.class.getName();
	/** 超时时间*/
	private static final int TIMEOUT = 3000;
	/** 读取数据的超时时间*/
	private static final int READTIMEOUT = 60000;
	/** 编码格式*/
	private static final String CHARSET = "utf-8";
	/**
	 * post请求
	 * @param url 请求的地址
	 * @param params 请求的参数
	 * @return 返回值
	 */
	public static String post(String url,HashMap<String, Object> params){
		URL httpUrl = null;
		HttpURLConnection conn = null;
		OutputStreamWriter osw;
        InputStreamReader isr;
		try {
            if(ISDEBUG){
                if(params != null){

                    Util.logi(TAG,"post request url:"+url+" data:"+params.toString());
                }else{
                    Util.logi(TAG,"post request url:"+url);
                }
            }
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
            int code = conn.getResponseCode();
            Util.logi(TAG,"response code:"+code);
			if(code == 200){
                isr = new InputStreamReader(conn.getInputStream());
                StringBuffer sb = new StringBuffer();
                char[] buf = new char[1024];
                int len = 0;
                while((len=isr.read(buf,0,1024)) > 0){
                    sb.append(buf,0,len);
                }
                isr.close();
                Util.logi(TAG,sb.toString());
                return sb.toString();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}catch (Exception e){
            e.printStackTrace();
        }finally{
			if(conn != null) {
				conn.disconnect();
			}
		}
		return null;
	}
	
	/**
	 * GET请求
	 * @param url 请求的地址（不用自己拼接参数）
	 * @param params 请求的参数
	 * @return
	 */
	public static String get(String url,HashMap<String, Object> params){
		URL httpUrl = null;
		HttpURLConnection conn = null;
        InputStreamReader isr;
		try {
			if(url.indexOf("?") < 0){
				url += "?";
			}
            url += paramsToString(params);
			httpUrl = new URL(url);

            if(ISDEBUG){

                Util.logi(TAG,"get request url:"+url);
            }
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

            int code = conn.getResponseCode();
            Util.logi(TAG,"response code:"+code);
            if(code == 200){
                isr = new InputStreamReader(conn.getInputStream());
                StringBuffer sb = new StringBuffer();
                char[] buf = new char[1024];
                int len = 0;
                while((len=isr.read(buf,0,1024)) > 0){
                    sb.append(buf,0,len);
                }
                isr.close();
                Util.logi(TAG,sb.toString());
                return sb.toString();
            }
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}catch (Exception e){
            e.printStackTrace();
        }finally{
			if(conn != null) {
				conn.disconnect();
			}
		}
		return null;
	}
	
	/**
	 * 将参数转换为GET请求格式的字符串
	 * @param params 请求的参数
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	private static String paramsToString(HashMap<String, Object> params) throws UnsupportedEncodingException{
		if(params == null){
			return "";
		}
		String str = "";
		for(Entry<String, Object> entry : params.entrySet()){
			str +=entry.getKey()+"="+URLEncoder.encode(entry.getValue().toString(),CHARSET)+"&";
		}
		str = str.substring(0,str.length() - 1);
		return str;
	}
}
