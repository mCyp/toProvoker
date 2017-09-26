package com.example.factory.model.db;

import com.example.factory.presenter.BasePresenter;
import com.example.factory.utils.DiffUiDataCallback;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * 基础的BaseModel的封装 为了实现isSame()方法
 * Created by Administrator on 2017/8/25.
 */

public abstract class BaseDbModel<Model> extends BaseModel implements DiffUiDataCallback.UiDataDiffer<Model>{

}
