package com.eye1024.ui.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eye1024.R;
import com.eye1024.bean.ArticleResult;
import com.eye1024.ui.WebActivity;
import com.eye1024.util.ImageLoadIni;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.raywang.interfaces.ImageLoaderAdapter;
import com.raywang.rayutils.SharedPreferencesUtil;
import com.raywang.rayutils.Util;
import com.raywang.view.EnhanceOnClickListener;

import java.util.ArrayList;

/**
 * 文章的数据适配器器
 * Created by Ray on 2015/6/30.
 */
public class ArticleAdapter extends BaseAdapter implements ImageLoaderAdapter{

    private LayoutInflater inflater;

    private ArrayList<String> readLog;

    private ArrayList<ArticleResult.Article> datas;

    private ImageLoader loader;

    private DisplayImageOptions options;
    /** 是否显示图片*/
    private boolean isShowImg = true;

    private Context context;

    private int mainTextColor;
    private int readTextColor;
    private OnArticleClickListener listener = new OnArticleClickListener();

    public ArticleAdapter(Context context,ArrayList<ArticleResult.Article> datas){
        this.inflater = LayoutInflater.from(context);
        this.datas = datas;
        loader = ImageLoadIni.getImageLoad(context);
        options = ImageLoadIni.getOption();
        this.context = context;
        isShowImg = SharedPreferencesUtil.newInstance(context).getBoolean("showImg",true);

        TypedArray array = context.getTheme().obtainStyledAttributes(new int[]{R.attr.main_text_color,
                R.attr.read_text_color});
        mainTextColor = array.getColor(0,context.getResources().getColor(R.color.main_text_color));
        readTextColor = array.getColor(1,context.getResources().getColor(R.color.read_main_text_color));
    }

    @Override
    public int getCount() {
        if(datas != null){
            return datas.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder view;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.item_article,parent,false);
            view = new ViewHolder();
            view.desc = (TextView) convertView.findViewById(R.id.desc);
            view.from = (TextView) convertView.findViewById(R.id.from);
            view.img = (ImageView) convertView.findViewById(R.id.img);
            view.title = (TextView) convertView.findViewById(R.id.title);
            convertView.setTag(view);
        }else{
            view = (ViewHolder) convertView.getTag();
        }

        ArticleResult.Article article = datas.get(position);
        view.title.setText(article.getTitle());
        view.desc.setText(Html.fromHtml(article.getDesc1()));
        view.from.setText(article.getParam1());
        if(isShowImg) {
            view.img.setImageResource(R.drawable.ic_empty);
            if (Util.noNull(article.getImg())) {
                view.img.setVisibility(View.VISIBLE);
                loader.displayImage(article.getImg(), view.img, options);
            } else {
                view.img.setVisibility(View.GONE);
            }
        }else{
            view.img.setVisibility(View.GONE);
        }
        if(readLog.contains(article.getUrl())){
            setTextColor(view,readTextColor);
        }else{
            setTextColor(view,mainTextColor);
        }
        convertView.setId(position);
        convertView.setOnClickListener(listener);
        return convertView;
    }

    private void setTextColor(ViewHolder views,int color){
        views.title.setTextColor(color);
        views.desc.setTextColor(color);
        views.from.setTextColor(color);
    }

    public void addData(ArrayList<ArticleResult.Article> datas){
        if(this.datas == null){
            this.datas = datas;
        }else{
            this.datas.addAll(datas);
        }
        notifyDataSetChanged();
    }

    public void setData(ArrayList<ArticleResult.Article> datas){
        this.datas = datas;
        notifyDataSetChanged();
    }

    @Override
    public void onScroll() {
        //在这里暂停
        loader.pause();
    }

    @Override
    public void onScrollEnd() {
        //恢复获取图片
        loader.resume();
    }


    private static class ViewHolder{
        TextView title;
        TextView desc;
        TextView from;
        ImageView img;
    }

    public void setShowImg(boolean isShowImg){
        this.isShowImg = isShowImg;
        notifyDataSetChanged();
    }

    private class OnArticleClickListener extends EnhanceOnClickListener{

        @Override
        public void click(int id) {
            Intent intent = new Intent(context, WebActivity.class);
            intent.putExtra("article",datas.get(id));
            ((Activity)context).startActivityForResult(intent,1);
        }
    }

    public void setReadLog(ArrayList<String> readLog){
        this.readLog = readLog;
    }

    public void addReadLog(ArrayList<String> readLog){
        if(this.readLog == null){
            this.readLog = readLog;
        }
        this.readLog.addAll(readLog);
    }

    public void addReadUrl(String url){
        this.readLog.add(url);
        notifyDataSetChanged();
    }
}
