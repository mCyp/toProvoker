package com.example.administrator.italker.push.frags.message;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.italker.push.R;
import com.example.administrator.italker.push.frags.panel.PanelFragment;
import com.example.common.app.Application;
import com.example.common.app.PresenterFragment;
import com.example.common.tools.AudioPlayHelper;
import com.example.common.widget.PortraitView;
import com.example.common.widget.adapter.TextWatcherAdapter;
import com.example.common.widget.recycler.RecyclerAdapter;
import com.example.face.Face;
import com.example.factory.model.db.Message;
import com.example.factory.model.db.User;
import com.example.factory.persistance.Account;
import com.example.factory.presenter.message.ChatContract;
import com.example.factory.utils.FileCache;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;
import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.compat.UiCompat;
import net.qiujuer.genius.ui.widget.Loading;
import net.qiujuer.widget.airpanel.AirPanel;
import net.qiujuer.widget.airpanel.Util;

import java.io.File;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

import static com.example.administrator.italker.push.activities.MessageActivity.KEY_RECEIVER_ID;


/**
 * Created by Administrator on 2017/8/30.
 */

public abstract class ChatFragment<InitModel> extends PresenterFragment<ChatContract.Presenter>
        implements AppBarLayout.OnOffsetChangedListener
        , ChatContract.View<InitModel>
        , PanelFragment.PanelCallback {

    // 接收者Id 可能是用户的id 或者可能是群组的id
    protected String mReceiverId;
    protected Adapter mAdapter;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.collapsingToolbarLayout)
    CollapsingToolbarLayout mCollapsingLayout;

    @BindView(R.id.appbar)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.edit_content)
    EditText mContent;

    @BindView(R.id.btn_submit)
    View mSubmit;

    // 控制底部面板与软键盘Boss的过渡
    private AirPanel.Boss mPanelBoss;
    private PanelFragment mPanelFragment;
    // 语音的基础
    private FileCache<AudioHolder> mAudioFileCache;
    private AudioPlayHelper<AudioHolder> mAudioPlayer;


    @Override
    protected void initargs(Bundle bundle) {
        super.initargs(bundle);
        mReceiverId = bundle.getString(KEY_RECEIVER_ID);
    }

    @Override
    protected final int getContentLayoutId() {
        return R.layout.fragment_chat_common;
    }

    @LayoutRes
    protected abstract int getHeaderLayoutId();

    @Override
    protected void initWidget(View root) {
        // 拿到占位布局
        // 替换布局一定要发生在super之前
        // 防止控件发生异常
        ViewStub stub = (ViewStub) root.findViewById(R.id.view_stub_header);
        stub.setLayoutResource(getHeaderLayoutId());
        stub.inflate();

        // 在这里进行了控件的绑定
        super.initWidget(root);

        mPanelBoss = (AirPanel.Boss) root.findViewById(R.id.lay_content);
        mPanelBoss.setup(new AirPanel.PanelListener() {

            @Override
            public void requestHideSoftKeyboard() {
                // 请求隐藏软键盘
                Util.hideKeyboard(mContent);
            }
        });
        mPanelFragment = (PanelFragment) getChildFragmentManager().findFragmentById(R.id.frag_panel);
        mPanelFragment.setup(this);

        initToolbar();
        initAppBar();
        initEditContent();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new Adapter();
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<Message>(){

            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, Message message) {
                if(message.getType() == Message.TYPE_AUDIO && holder instanceof ChatFragment.AudioHolder){
                    // 开始下载
                    mAudioFileCache.downLoad((ChatFragment.AudioHolder)holder,message.getContent());
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // 进入界面的时候初始化
        mAudioPlayer = new AudioPlayHelper<>(new AudioPlayHelper.RecordPlayListener<AudioHolder>() {
            @Override
            public void onPlayStart(AudioHolder audioHolder) {
                // 范型的好处
                audioHolder.onPlayStart();
            }

            @Override
            public void onPlayStop(AudioHolder audioHolder) {
                // 直接停止
                audioHolder.onPlayStop();
            }

            @Override
            public void onPlayError(AudioHolder audioHolder) {
                // 提示失败
                Application.showToast(R.string.toast_audio_play_error);
            }
        });

        mAudioFileCache = new FileCache<>("audio/cache", "mp3", new FileCache.CacheListener<AudioHolder>() {
            @Override
            public void onDoneLoadSucceed(final AudioHolder holder,final File file) {
                Run.onUiAsync(new Action() {
                    @Override
                    public void call() {
                        // 播放
                        mAudioPlayer.trigger(holder,file.getAbsolutePath());
                    }
                });
            }

            @Override
            public void onDoneFailed(AudioHolder holder) {
                Application.showToast(R.string.toast_download_error);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 界面销毁的时候mAudioPlayer就销毁
        mAudioPlayer.destroy();
    }

    @Override
    public boolean onBackPressed() {
        if(mPanelBoss.isOpen()){
            mPanelBoss.closePanel();
            return true;
        }
        return super.onBackPressed();
    }

    @Override
    protected void initData() {
        super.initData();
        // Presenter开始 开始进行初始化操作
        mPresenter.start();
    }

    // 初始化Toolbar
    protected void initToolbar() {
        Toolbar toolbar = mToolbar;
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    // 初始化文本框的监听
    private void initEditContent() {
        mContent.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                // 去空格
                String content = s.toString().trim();
                boolean needSendMsg = !TextUtils.isEmpty(content);
                // 设置状态 改变对应的icon
                mSubmit.setActivated(needSendMsg);
            }
        });
    }

    // 给appbar设置监听，得到关闭与打开的进度
    private void initAppBar() {
        mAppBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

    }

    @OnClick(R.id.btn_face)
    void onFaceClick() {
        // 仅仅需要请求打开就可以了
        mPanelBoss.openPanel();
        mPanelFragment.showFace();

    }

    @OnClick(R.id.btn_record)
    void onRecordClick() {
        // 语音发送按钮
        mPanelBoss.openPanel();
        mPanelFragment.showRecord();
    }

    @OnClick(R.id.btn_submit)
    void onSubmitClick() {
        // 提交信息的按钮
        if (mSubmit.isActivated()) {
            // 文本里面有字体的时候
            String content = mContent.getText().toString();
            mContent.setText("");
            // 提交发送
            mPresenter.pushText(content);
        } else {
            // 更多的操作
            onMoreClick();
        }
    }

    private void onMoreClick() {
        // TODO
        mPanelBoss.openPanel();
        mPanelFragment.showGallery();
    }

    @Override
    public RecyclerAdapter<Message> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        // 由于没有占位布局，所以revyvlerView会一直显示
        mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount());
    }

    @Override
    public EditText getInputEditText() {
        // 返回输入框
        return mContent;
    }

    // 内容的适配器
    private class Adapter extends RecyclerAdapter<Message> {

        @Override
        protected int getItemViewType(int position, Message message) {

            // 我发送的在右边 收到的在左边
            boolean isRight = Objects.equals(message.getSender().getId(), Account.getUserId());

            switch (message.getType()) {
                case Message.TYPE_STR:
                    // 如果是文字内容的情况下
                    return isRight ? R.layout.cell_chat_text_right : R.layout.cell_chat_text_left;
                case Message.TYPE_AUDIO:
                    // 如果是语音的情况下
                    return isRight ? R.layout.cell_chat_audio_right : R.layout.cell_chat_audio_left;
                case Message.TYPE_PIC:
                    // 如果是图片内容的情况下
                    return isRight ? R.layout.cell_chat_pic_right : R.layout.cell_chat_pic_left;
                default:
                    // 包括文件和其他内容
                    return isRight ? R.layout.cell_chat_text_right : R.layout.cell_chat_text_left;
            }

        }

        @Override
        protected ViewHolder<Message> onCreateViewHolder(View root, int viewType) {
            switch (viewType) {
                // 左右都是同一个类型文字的holder
                case R.layout.cell_chat_text_right:
                case R.layout.cell_chat_text_left:
                    return new TextHolder(root);
                // 左右都是同一个类型语音的holder
                case R.layout.cell_chat_audio_right:
                case R.layout.cell_chat_audio_left:
                    return new AudioHolder(root);
                // 左右都是同一个类型图片的holder
                case R.layout.cell_chat_pic_right:
                case R.layout.cell_chat_pic_left:
                    return new PicHolder(root);

                // TODO 文件的holder
                default:
                    // 默认的情况下 就返回TextHolder进行处理
                    return new TextHolder(root);
            }
        }
    }

    // holder 的基类
    class BaseHolder extends RecyclerAdapter.ViewHolder<Message> {

        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;

        // 左边没有 右边需要 允许为空
        @Nullable
        @BindView(R.id.loading)
        Loading mLoading;

        public BaseHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            User sender = message.getSender();
            // 由于message里面的user是懒加载，所以要重新加载一遍
            sender.load();
            // 头像加载
            mPortraitView.setup(Glide.with(ChatFragment.this), sender);
            if (mLoading != null) {
                int status = message.getStatus();
                if (status == Message.STATUS_DONE) {
                    // 正常的消息状态
                    mLoading.stop();
                    mLoading.setVisibility(View.GONE);
                } else if (status == Message.STATUS_CREATED) {
                    // 正在发送的信息状态
                    mLoading.setVisibility(View.VISIBLE);
                    mLoading.setProgress(0);
                    mLoading.setForegroundColor(UiCompat.getColor(getResources(), R.color.colorAccent));
                    mLoading.start();
                } else if (status == Message.STATUS_FAILED) {
                    // 发送失败状态
                    mLoading.setVisibility(View.VISIBLE);
                    mLoading.stop();
                    mLoading.setProgress(1);
                    mLoading.setForegroundColor(UiCompat.getColor(getResources(), R.color.alertImportant));
                }
                // 只有发送失败的情况下头像才可以点击
                mPortraitView.setEnabled(status == Message.STATUS_FAILED);
            }
        }

        @OnClick(R.id.im_portrait)
        void onRePushClick() {
            // 重新发送
            if (mLoading != null && mPresenter.rePush(mData)) {
                // 必须是右边的才有可能重新发送
                // 重新刷新一下界面
                updateData(mData);
            }
        }
    }

    // 文字的holder
    class TextHolder extends BaseHolder {
        @BindView(R.id.txt_content)
        TextView mContent;

        public TextHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);
            // 把内容设置到布局上
            Spannable spannable = new SpannableString(message.getContent());
            // 解析表情
            Face.decode(mContent, spannable, (int) Ui.dipToPx(getResources(), 20));
            // 把内容设置到布局上
            mContent.setText(spannable);
        }
    }

    // 语音的holder
    class AudioHolder extends BaseHolder {
        @BindView(R.id.txt_content)
        TextView mContent;
        @BindView(R.id.im_audio_track)
        ImageView mAudioTrack;

        public AudioHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);
            //
            String attach = TextUtils.isEmpty(message.getAttach())?"":message.getAttach();
            mContent.setText(formatTime(attach));
        }

        // 开始播放
        void onPlayStart(){
            // 显示
            mAudioTrack.setVisibility(View.VISIBLE);
        }

        // 播放停止的时候
        void onPlayStop(){
            // 占位并隐藏
            mAudioTrack.setVisibility(View.INVISIBLE);
        }

        private String formatTime(String attach){
            float time;
            try {
                time = Float.parseFloat(attach)/1000f;
            }catch (Exception e){
                time = 0;
            }
            // 取小数的一位
            // round 不是取小数的两位？
            String shortTime = String.valueOf(Math.round(time*10f)/10f);
            // 如果是1.0就变为1 如果是1.1 就不变
            shortTime = shortTime.replaceAll("[.]0+?$|0+?$", "");
            return String.format("%s",shortTime);
        }
    }

    // 图片的holder
    class PicHolder extends BaseHolder {
        @BindView(R.id.im_image)
        ImageView mContent;

        public PicHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);
            // 当是图片类型的时候 content 就是具体的图片的地址
            String content = message.getContent();
            Glide.with(ChatFragment.this)
                    .load(content)
                    .fitCenter()
                    .into(mContent);
        }
    }

    @Override
    public void onSendGallery(String[] paths) {
        // 图片回调回来
        mPresenter.pushImages(paths);
    }

    @Override
    public void onRecordDone(File file, Long time) {
        // 语音回调回来
        mPresenter.pushAudio(file.getAbsolutePath(),time);
    }
}
