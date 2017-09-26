package com.example.common.widget;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.common.R;
import com.example.common.widget.recycler.RecyclerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class GalleryView extends RecyclerView {
    private static final int LOADER_ID = 0X0100;
    private static final int MAX_IMAGE_COUNT = 3;//最大的选中图片数量
    private static final int MIN_IMAGE_FILE_SIZE = 10 * 1024; // 最小的图片大小
    private Adapter mAdapter = new Adapter();
    private LoaderCallback mLoaderCallback = new LoaderCallback();
    private List<Image> mSelectImages = new LinkedList<>();
    private SelectChangedListener mListener;

    public GalleryView(Context context) {
        super(context);
        init();
    }

    public GalleryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GalleryView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setLayoutManager(new GridLayoutManager(getContext(), 4));
        setAdapter(mAdapter);
        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<Image>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, Image image) {
                //cell点击操作，如果说我们的点击是允许的，那么更新对应的cell状态
                // 然后更新界面，同理：如果说不能允许点击（已经达到最大的选中数量）那么就不刷新界面
                if(onItemSelectClick(image)){
                    //noinspection unchecked
                    holder.updateData(image);
                }
            }
        });
    }

    /**
     * 初始化方法
     * @param loaderManager Loader管理器
     * @return 返回一个LOADER_ID,可以用于销毁Loader
     */
    public int setup(LoaderManager loaderManager,SelectChangedListener listener){
        mListener = listener;
        loaderManager.initLoader(LOADER_ID,null,mLoaderCallback);
        return LOADER_ID;
    }

    /**
     * cell点击具体的逻辑
     * @param image Image
     * @return True,代表我进行了数据更改，你需要刷新，反之，你不需要刷新
     */
    private boolean onItemSelectClick(Image image){
        //是否需要进行刷新
        boolean notifyRefresh;
        if(mSelectImages.contains(image)){
            // 如果之前在 现在就移除
            mSelectImages.remove(image);
            image.isSelect=false;
            // 状态已经改变 则需要更新
            notifyRefresh = true;
        }else{
            if(mSelectImages.size()>=MAX_IMAGE_COUNT){
                // 得到提示文字
                String str = getResources().getString(R.string.label_gallery_select_max_size);
                // 格式化提示文字
                str = String.format(str,MAX_IMAGE_COUNT);
                Toast.makeText(getContext(),str,Toast.LENGTH_SHORT).show();
                notifyRefresh = false;
            }else{
                mSelectImages.add(image);
                image.isSelect = true;
                notifyRefresh = true;
            }
        }
        // 如果数据有更改，那么我们需要通知外面的监听者我们的数组选中改变了
        if(notifyRefresh)
            notifySelectChanged();
        return true;
    }

    /**
     * 用于实际的数据加载的Loader Callback
     */
    private class LoaderCallback implements LoaderManager.LoaderCallbacks<Cursor>{
        private final  String[] IMAGE_PROJECTION = new String[]{
                MediaStore.Images.Media._ID, //ID
                MediaStore.Images.Media.DATA, //图片路径
                MediaStore.Images.Media.DATE_ADDED  //图片的创建时间
        };


        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            // 创建一个Loader
            if(id==LOADER_ID){
                // 如果是我们的ID 则可以进行初始化
                return new CursorLoader(getContext(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        IMAGE_PROJECTION,
                        null,
                        null,
                        IMAGE_PROJECTION[2] + " DESC"); //倒序查询
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            // 当loader加载完成时
            List<Image> images = new ArrayList<>();
            if(data!=null){
                // 判断是否有数据
                int count = data.getCount();
                if(count>0){
                    // 移动游标到开始
                    data.moveToFirst();

                    // 得到对应的列的Index坐标
                    int indexId = data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]);
                    int indexPath = data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]);
                    int indexDate = data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]);
                    do{
                        // 循环读取直到没有下一条数据
                        int id =data.getInt(indexId);
                        String path =data.getString(indexPath);
                        long dateTime = data.getLong(indexDate);

                        File file = new File(path);
                        if(!file.exists()||file.length()<MIN_IMAGE_FILE_SIZE){
                            //如果没有图片 或者图片太小，则跳过
                            continue;
                        }

                        // 添加一条新的数据
                        Image image = new Image();
                        image.id = id;
                        image.path = path;
                        image.date = dateTime;
                        images.add(image);

                    }while (data.moveToNext());
                }
            }

            updateSource(images);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            //当Loader销毁或者重置了,界面清空
            updateSource(null);
        }
    }

    /**
     * 得到选中图片的所有地址
     * @return 返回一个数组
     */
    public String[] getSelectedPath(){
        String[] paths = new String[mSelectImages.size()];
        int index = 0;
        for(Image image: mSelectImages){
            paths[index++] = image.path;
        }
        return paths;
    }

    /**
     * 可以进行清空选中的图片
     */
    public void clear(){
        for(Image image : mSelectImages){
            // 一定要先重置状态
            image.isSelect=false;
        }
        mSelectImages.clear();
        //通知更新
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 通知选中状态改变
     */
    private void notifySelectChanged(){
        //得到监听者，并判断是否有监听者，然后进行回调数量变化
        SelectChangedListener listener = mListener;
        if(listener!=null){
            listener.onSelectCountChanged(mSelectImages.size());
        }
    }

    /**
     * 通知adapter数据更改的方法
     * @param images 新的数据
     */
    private void updateSource(List<Image> images){
        mAdapter.replace(images);
    }


    /**
     * 内部的数据结构
     */
    private static class Image {
        // 数据ID
        int id;
        String path; //图片路径
        long date; //图片的创建日期
        boolean isSelect; //是否选中

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Image image = (Image) o;

            return path != null ? path.equals(image.path) : image.path == null;

        }

        @Override
        public int hashCode() {
            return path != null ? path.hashCode() : 0;
        }
    }

    /**
     * 适配器
     */
    private class Adapter extends RecyclerAdapter<Image> {

        @Override
        protected int getItemViewType(int position, Image image) {
            return R.layout.cell_galley;
        }

        @Override
        protected ViewHolder<Image> onCreateViewHolder(View root, int viewType) {
            return new GalleryView.ViewHolder(root);
        }
    }

    /**
     * cell对应的Holder
     */
    private class ViewHolder extends RecyclerAdapter.ViewHolder<Image> {

        private ImageView mPic;
        private View mShade;
        private CheckBox mSelcted;

        public ViewHolder(View itemView) {
            super(itemView);
            mPic = (ImageView) itemView.findViewById(R.id.im_image);
            mShade = (View) itemView.findViewById(R.id.img_shade);
            mSelcted = (CheckBox) itemView.findViewById(R.id.cb_select);
        }

        @Override
        protected void onBind(Image image) {
            Glide.with(getContext())
                    .load(image.path) // 加载路径
                    .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用缓存，直接从原图加载
                    .centerCrop()   // 居中剪切
                    .placeholder(R.color.grey_200) //默认颜色
                    .into(mPic);

            mShade.setVisibility(image.isSelect?VISIBLE:INVISIBLE);
            mSelcted.setChecked(image.isSelect);
            mSelcted.setVisibility(VISIBLE);
        }
    }

    /**
     * 对外的一个监听器
     */
    public interface SelectChangedListener{
        void onSelectCountChanged(int count);
    }

}
