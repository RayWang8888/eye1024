package com.eye1024.bean;

import com.google.gson.Gson;

/**
 * 版本的json解析类
 * Created by Ray on 2015/8/28.
 */
public class VersionResult extends WebResult {

    public AppVersion data;

    public static VersionResult parse(String json){
        return new Gson().fromJson(json,VersionResult.class);
    }

    public AppVersion getData() {
        return data;
    }

    public void setData(AppVersion data) {
        this.data = data;
    }

    public class AppVersion{
        private int version;

        private String versionname;
        private String url;

        private String des;
        private String size;

        public String getVersionname() {
            return versionname;
        }

        public void setVersionname(String versionname) {
            this.versionname = versionname;
        }

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getDes() {
            return des;
        }

        public void setDes(String des) {
            this.des = des;
        }
    }
}
