package com.example.administrator.italker.push.frags.user;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import android.support.annotation.StringRes;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.administrator.italker.push.R;
import com.example.administrator.italker.push.activities.MainActivity;
import com.example.administrator.italker.push.frags.media.GalleryFragment;
import com.example.common.app.Application;
import com.example.common.app.PresenterFragment;
import com.example.common.widget.PortraitView;
import com.example.factory.Factory;
import com.example.factory.net.UploadHelper;
import com.example.factory.presenter.user.UpdateInfoContract;
import com.example.factory.presenter.user.UpdateInfoPresenter;
import com.yalantis.ucrop.UCrop;

import net.qiujuer.genius.ui.widget.Button;
import net.qiujuer.genius.ui.widget.Loading;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * 用户更新信息的界面
 */
public class UpdateInfoFragment extends PresenterFragment<UpdateInfoContract.Presenter>
    implements UpdateInfoContract.View{

    @BindView(R.id.im_sex)
    ImageView mSex;

    @BindView(R.id.edit_desc)
    EditText mDesc;

    @BindView(R.id.im_portrait)
    PortraitView mPortrait;

    @BindView(R.id.loading)
    Loading mLoading;

    @BindView(R.id.btn_submit)
    Button mSubmit;

    // 头像的本地路径
    private String mPortraitPath;

    // 默认为男性
    private boolean isMan = true;


    public UpdateInfoFragment() {
        // Required empty public constructor
    }


    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_update_info;
    }





    @OnClick(R.id.im_portrait)
    void onPortraitClick(){
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
                            .start(getActivity());
                    }
                })
                // show方法的时候建议使用getChildFragment
                // tag GalleryFragment class 名字
                .show(getChildFragmentManager(),GalleryFragment.class.getName());
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

    @OnClick(R.id.im_sex)
    void onSexClick(){
        isMan = !isMan;
        // 取出资源id
        Drawable drawable = getResources().getDrawable(isMan?R.drawable.ic_sex_man
                :R.drawable.ic_sex_woman);
        mSex.setImageDrawable(drawable);
        // 设置背景层级
        mSex.getBackground().setLevel(isMan?0:1);
    }

    @OnClick(R.id.btn_submit)
    void onSubmitClick(){
        String desc = mDesc.getText().toString();
        mPresenter.update(mPortraitPath,desc,isMan);
    }

    @Override
    public void showError(@StringRes int str) {
        super.showError(str);

        // 载入框停止运行
        mLoading.stop();
        // 电话密码姓名输入框可以输入
        mPortrait.setEnabled(true);
        mDesc.setEnabled(true);
        mSex.setEnabled(true);
        // 注册按钮可以提交
        mSubmit.setEnabled(true);
    }

    @Override
    public void showLoading() {
        super.showLoading();

        // 载入框开始运行
        mLoading.start();
        // 电话密码姓名输入框不可以输入
        mPortrait.setEnabled(false);
        mDesc.setEnabled(false);
        mSex.setEnabled(false);
        // 注册按钮不可以提交
        mSubmit.setEnabled(false);
    }

    @Override
    public void updateSucceed() {
        // 注册成功 我们需要跳转到MainActivity里面 账户已经登录
        MainActivity.show(getContext());
        // 关闭当前界面
        getActivity().finish();
    }

    @Override
    protected UpdateInfoContract.Presenter initPresenter() {
        return new UpdateInfoPresenter(this);
    }
}
