package com.example.administrator.italker.push.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.italker.push.R;
import com.example.administrator.italker.push.frags.media.GalleryFragment;
import com.example.common.app.Application;
import com.example.common.app.PresenterToolbarActivity;
import com.example.common.app.ToolbarActivity;
import com.example.common.widget.PortraitView;
import com.example.common.widget.recycler.RecyclerAdapter;
import com.example.factory.presenter.BaseContract;
import com.example.factory.presenter.group.GroupCreateContract;
import com.example.factory.presenter.group.GroupCreatePresenter;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.zip.Inflater;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class GroupCreateActivity extends PresenterToolbarActivity<GroupCreateContract.Presenter>
implements GroupCreateContract.View{
    @BindView(R.id.recycler)
    RecyclerView mRecycler;

    @BindView(R.id.edit_name)
    EditText mName;

    @BindView(R.id.edit_desc)
    EditText mDesc;

    @BindView(R.id.im_portrait)
    PortraitView mPortrait;

    private Adapter mAdapter;
    private String mPortraitPath;

    public static void show (Context context){
        context.startActivity(new Intent(context,GroupCreateActivity.class));
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_group_create;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setTitle("");

        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(mAdapter = new Adapter());
    }

    @Override
    protected void initData() {
        super.initData();

        mPresenter.start();
    }

    @OnClick(R.id.im_portrait)
    void onPortraitClick(){
        hideOfSoftKeyboard();
        new GalleryFragment()
                .setListener(new GalleryFragment.OnSelectedListener() {
                    @Override
                    public void onSelectedImage(String path) {
                        UCrop.Options options = new UCrop.Options();
                        // 设置图片处理的格式JPEG
                        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
                        // 设置压缩后的图片精度
                        options.setCompressionQuality(96);
                        // 得到头像的缓存地址
                        File dPath = Application.getPortraitTmpFile();

                        // 发起剪切 完成之后回调回来
                        UCrop.of(Uri.fromFile(new File(path)),Uri.fromFile(dPath))
                                .withAspectRatio(1,1) // 1比1的比例
                                .withMaxResultSize(520,520) //返回最大的尺寸
                                .withOptions(options) // 相关参数
                                .start(GroupCreateActivity.this);
                    }
                })
                // show方法的时候建议使用getChildFragment
                // tag GalleryFragment class 名字
                .show(getSupportFragmentManager(),GalleryFragment.class.getName());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 收到从Activity传递过来的回调，然后取出其中的值进行图片加载
        // 如果是我能够处理的类型
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            // 通过UCrop得到对应的Uri
            final Uri resultUri = UCrop.getOutput(data);
            if(resultUri!=null){
                loadPortrait(resultUri);
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            // 提示一个未知错误
            Application.showToast(R.string.data_rsp_error_unknown);
        }
    }

    /**
     * 载入图片路径到当前的头像中
     * @param uri
     */
    private void loadPortrait(Uri uri){
        // 获取头像的地址
        mPortraitPath = uri.getPath();

        Glide.with(this)
                .load(uri)
                .asBitmap()
                .centerCrop()
                .into(mPortrait);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.group_create,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_create){
            // 创建群组
            onCreateClick();
        }
        return super.onOptionsItemSelected(item);
    }

    // 进行创建操作
    private void onCreateClick(){
        hideOfSoftKeyboard();
        String name = mName.getText().toString().trim();
        String desc = mDesc.getText().toString().trim();
        mPresenter.create(name,desc,mPortraitPath);
    }

    // 隐藏软键盘
    private void hideOfSoftKeyboard(){
        View view = getCurrentFocus();
        if(view == null)
            return;

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    @Override
    public void onCreateSucceed() {
        // 提示成功
        hideLoading();
        Application.showToast(R.string.label_group_create_succeed);
        finish();
    }

    @Override
    protected GroupCreateContract.Presenter initPresenter() {
        return new GroupCreatePresenter(this);
    }

    @Override
    public RecyclerAdapter<GroupCreateContract.ViewMode> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        hideLoading();
    }

    private class Adapter extends RecyclerAdapter<GroupCreateContract.ViewMode>{

        @Override
        protected int getItemViewType(int position, GroupCreateContract.ViewMode viewMode) {
            return R.layout.cell_group_create_contact;
        }

        @Override
        protected ViewHolder<GroupCreateContract.ViewMode> onCreateViewHolder(View root, int viewType) {
            return new GroupCreateActivity.ViewHolder(root);
        }
    }

    class ViewHolder extends RecyclerAdapter.ViewHolder<GroupCreateContract.ViewMode>{
        @BindView(R.id.im_portrait)
        PortraitView mPortrait;

        @BindView(R.id.txt_name)
        TextView mName;

        @BindView(R.id.cb_select)
        CheckBox mSelect;


        public ViewHolder(View itemView) {
            super(itemView);
        }

        @OnCheckedChanged(R.id.cb_select)
        void onCheckedChanged(boolean checked){
            // 进行状态更改
            mPresenter.changeSelect(mData,checked);
        }

        @Override
        protected void onBind(GroupCreateContract.ViewMode viewMode) {
            mPortrait.setup(Glide.with(GroupCreateActivity.this),viewMode.author.getPortrait());
            mName.setText(viewMode.author.getName());
            mSelect.setChecked(viewMode.isSelected);
        }
    }
}
