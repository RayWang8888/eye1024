package com.eye1024.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.eye1024.R;
import com.eye1024.api.NetApi;
import com.eye1024.bean.WebResult;
import com.raywang.activity.BaseActivity;
import com.raywang.rayutils.HttpCoreThread;
import com.raywang.rayutils.UIHlep;
import com.raywang.rayutils.Util;
import com.rey.material.widget.EditText;
import com.rey.material.widget.ProgressView;

import java.util.HashMap;

/**
 * 意见与建议的activity
 * Created by Ray on 2015/7/21.
 */
public class CommendActivity extends BaseActivity {

    private EditText email;
    private EditText nickname;
    private EditText commend;
    private HttpCoreThread httpCoreThread;
    private HashMap<String,Object> params;
    private HttpCoreThread.HttpListener httpListener = new HttpCoreThread.HttpListener() {
        @Override
        public void onCacheData(int requestCode,String data) {

        }

        @Override
        public void onError(int requestCode,int code, String e) {
            UIHlep.toast(CommendActivity.this, e);
        }

        @Override
        public void onInternet(int requestCode,String data) {
            closeLoading();
            if(data != null) {
                WebResult s = WebResult.parse(data);
                if (s == null) {
                    UIHlep.toast(CommendActivity.this, R.string.connet_error);
                } else if (Util.noNull(s.getErrcode())) {
                    UIHlep.toast(CommendActivity.this, s.getErrmsg());
                } else {
                    UIHlep.toast(CommendActivity.this, R.string.commend_success);
                    finish();
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_commend,true);
    }

    @Override
    protected void iniHead() {
        TextView back = (TextView) findViewById(R.id.back);
        back.setOnClickListener(this);
        back.setText(R.string.commend);
        super.iniHead();
    }

    @Override
    protected void iniView() {
        email = (EditText) findViewById(R.id.email);
        nickname = (EditText) findViewById(R.id.nickname);
        commend = (EditText) findViewById(R.id.commend);
        findViewById(R.id.ok).setOnClickListener(this);
        super.iniView();
    }

    @Override
    protected void click(int id) {
        switch (id){
            case R.id.back:
                finish();
                break;
            case R.id.ok:
                String sEmail = email.getText().toString();
                String sCommend = commend.getText().toString();
                String sNickname = nickname.getText().toString();
                String reg = "^(\\w)+(\\.\\w+)*@(\\w)+((\\.\\w+)+)$";
                if(!sEmail.matches(reg)){
                    UIHlep.toast(this, R.string.email_error);
                    return;
                }
                if(sCommend.isEmpty()){
                    UIHlep.toast(this, R.string.commend_error);
                    return;
                }
                showLoading();
//                if(async != null){
//                    async.cancel(true);
//                }
//                async = new CommendAsync();
//                async.execute(sEmail,sNickname,sCommend);
                if(httpCoreThread != null){
                    httpCoreThread.setStop();
                }
                if(params == null){
                    params = new HashMap<String,Object>();
                }
                params.put("email",sEmail);
                params.put("nickname",sNickname);
                params.put("commend",sCommend);
                httpCoreThread = NetApi.commend(this, httpListener, params);
                break;
            default:
                super.click(id);
                break;
        }
    }

    private AlertDialog dialog;
    private void showLoading(){
        if(dialog == null){
            dialog = new AlertDialog.Builder(this).create();
        }
        dialog.show();
        Window window = dialog.getWindow();
        window.setContentView(R.layout.dialog_loading);
        ((ProgressView)window.findViewById(R.id.progress)).start();
    }

    private void closeLoading(){
        if(dialog != null){
            dialog.dismiss();
        }
    }



    @Override
    protected void onDestroy() {
//        if(async != null){
//            async.cancel(true);
//            async = null;
//        }
        if(httpCoreThread != null){
            httpCoreThread.setStop();
        }
        super.onDestroy();
    }

//    /**
//     * 意见与建议的异步任务
//     */
//    private class CommendAsync extends AsyncTask<String,Void,WebResult>{
//
//        @Override
//        protected WebResult doInBackground(String... params) {
//            return NetApi.commend(params[0],params[1],params[2]);
//        }
//
//        @Override
//        protected void onPostExecute(WebResult s) {
//            closeLoading();
//            if(s == null){
//                UIHlep.toast(CommendActivity.this,R.string.connet_error);
//            }else if(Util.noNull(s.getErrcode())){
//                UIHlep.toast(CommendActivity.this,s.getErrmsg());
//            }else{
//                UIHlep.toast(CommendActivity.this,R.string.commend_success);
//                finish();
//            }
//        }
//    }

    @Override
    protected void onResume() {
        StatService.onResume(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        StatService.onPause(this);
        super.onPause();
    }
}
