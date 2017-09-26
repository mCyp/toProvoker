package com.example.administrator.italker.push.frags.panel;

import android.view.View;

import com.example.administrator.italker.push.R;
import com.example.common.widget.recycler.RecyclerAdapter;
import com.example.face.Face;

import java.util.List;

/**
 * 表情的适配器
 * Created by Administrator on 2017/9/15.
 */

public class FaceAdapter extends RecyclerAdapter<Face.Bean> {

    public FaceAdapter(List<Face.Bean> been, AdapterListener<Face.Bean> listener) {
        super(been, listener);
    }

    @Override
    protected int getItemViewType(int position, Face.Bean bean) {
        return R.layout.cell_face;
    }

    @Override
    protected ViewHolder<Face.Bean> onCreateViewHolder(View root, int viewType) {
        return new FaceHolder(root);
    }
}
