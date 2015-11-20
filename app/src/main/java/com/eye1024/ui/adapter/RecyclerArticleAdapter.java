package com.eye1024.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * Created by Administrator on 2015/11/19.
 */
public class RecyclerArticleAdapter extends RecyclerView.Adapter<RecyclerArticleAdapter.ViewHolder>
        implements ImageLoaderAdapter {
    private ArrayList<String> readLog;

    private ArrayList<ArticleResult.Article> datas;

    private ImageLoader loader;

    private DisplayImageOptions options;
    /** 是否显示图片*/
    private boolean isShowImg = true;
    private int mainTextColor;
    private int readTextColor;
    private OnArticleClickListener listener = new OnArticleClickListener();
    private Context context;

    public RecyclerArticleAdapter(Context context, ArrayList<ArticleResult.Article> datas){
        this.datas = datas;
        this.context = context;
        loader = ImageLoadIni.getImageLoad(context);
        options = ImageLoadIni.getOption();
        isShowImg = SharedPreferencesUtil.newInstance(context).getBoolean("showImg",true);

        TypedArray array = context.getTheme().obtainStyledAttributes(new int[]{R.attr.main_text_color,
                R.attr.read_text_color});
        mainTextColor = array.getColor(0,context.getResources().getColor(R.color.main_text_color));
        readTextColor = array.getColor(1,context.getResources().getColor(R.color.read_main_text_color));
    }

    @Override
    public RecyclerArticleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerArticleAdapter.ViewHolder holder, int position) {
        ArticleResult.Article article = datas.get(position);
        holder.title.setText(article.getTitle());
        holder.desc.setText(Html.fromHtml(article.getDesc1()));
        holder.from.setText(article.getParam1());
        if(isShowImg) {
            holder.img.setImageResource(R.drawable.ic_empty);
            if (Util.noNull(article.getImg())) {
                holder.img.setVisibility(View.VISIBLE);
                loader.displayImage(article.getImg(), holder.img, options);
            } else {
                holder.img.setVisibility(View.GONE);
            }
        }else{
            holder.img.setVisibility(View.GONE);
        }
        if(readLog.contains(article.getUrl())){
            setTextColor(holder,readTextColor);
        }else{
            setTextColor(holder,mainTextColor);
        }
        holder.layout.setId(position);
        holder.layout.setOnClickListener(listener);
    }

    private void setTextColor(ViewHolder views,int color){
        views.title.setTextColor(color);
        views.desc.setTextColor(color);
        views.from.setTextColor(color);
    }

    @Override
    public int getItemCount() {
        if(datas != null){
            return datas.size();
        }
        return 0;
    }

    private class OnArticleClickListener extends EnhanceOnClickListener {

        @Override
        public void click(int id) {
            Intent intent = new Intent(context, WebActivity.class);
            intent.putExtra("article",datas.get(id));
            ((Activity)context).startActivityForResult(intent,1);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView desc;
        TextView from;
        ImageView img;
        View layout;

        public ViewHolder(View itemView) {
            super(itemView);
            title = getView(itemView, R.id.title);
            desc = getView(itemView, R.id.desc);
            from = getView(itemView, R.id.from);
            img = getView(itemView, R.id.img);
            layout = getView(itemView, R.id.layout);
        }
    }

    public void addData(ArrayList<ArticleResult.Article> datas){
        if(this.datas == null){
            this.datas = datas;
            notifyDataSetChanged();
        }else{
            for(ArticleResult.Article article : datas){
                this.datas.add(article);
                notifyItemInserted(this.datas.size() - 1);
            }
        }
    }

    public void setData(ArrayList<ArticleResult.Article> datas){
        this.datas = datas;
        notifyDataSetChanged();
    }

    /**
     * 设置是否显示图片
     * @param isShowImg
     */
    public void setShowImg(boolean isShowImg){
        this.isShowImg = isShowImg;
        notifyDataSetChanged();
    }

    /**
     * 设置阅读记录
     * @param readLog
     */
    public void setReadLog(ArrayList<String> readLog){
        this.readLog = readLog;
    }

    /**
     * 添加阅读记录
     * @param readLog
     */
    public void addReadLog(ArrayList<String> readLog){
        if(this.readLog == null){
            this.readLog = readLog;
        }
        this.readLog.addAll(readLog);
    }


    /**
     * 添加一条阅读记录
     * @param url
     */
    public void addReadUrl(String url){
        this.readLog.add(url);
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

    private static <T extends View>T getView(View view,int id){
        return (T) view.findViewById(id);
    }
}
