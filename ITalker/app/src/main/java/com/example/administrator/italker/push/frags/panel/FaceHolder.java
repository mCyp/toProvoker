package com.example.administrator.italker.push.frags.panel;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.example.administrator.italker.push.R;
import com.example.common.widget.recycler.RecyclerAdapter;
import com.example.face.Face;

import butterknife.BindView;

/**
 * ViewHolder
 * Created by Administrator on 2017/9/15.
 */

public class FaceHolder extends RecyclerAdapter.ViewHolder<Face.Bean>{

    @BindView(R.id.im_face)
    ImageView mFace;

    public FaceHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void onBind(Face.Bean bean) {
        if(bean!=null &&
                // drawabke 资源id
                (bean.preview instanceof Integer
                        // face zip 资源包路径
                        || bean.preview instanceof String)){
            Glide.with(mFace.getContext())
                    .load(bean.preview)
                    .asBitmap()
                    .format(DecodeFormat.PREFER_ARGB_8888) // 设置解码的格式是8888 保证清晰度
                    .into(mFace);
        }
    }
}
