package com.example.factory.data;

import android.support.annotation.NonNull;

import com.example.factory.data.helper.DbHelper;
import com.example.factory.model.db.BaseDbModel;
import com.example.factory.model.db.User;
import com.example.utils.CollectionUtil;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import net.qiujuer.genius.kit.reflect.Reflector;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

/**
 * 基础的数据仓库，实现基本的监听事件
 * Created by Administrator on 2017/8/25.
 */

public abstract class BaseDbRepository<Data extends BaseDbModel<Data>> implements DbDataSource<Data>
        ,QueryTransaction.QueryResultListCallback<Data>
        ,DbHelper.ChangedListener<Data>{
    // 和Presenter交互的回调
    private SucceedCallback<List<Data>> callback;

    // 当前缓存的数据 LinkedList对于数据的插取消耗更小
    protected final LinkedList<Data> dataList = new LinkedList<>();

    private Class<Data> dataClass; //当前范型对应的真实的Class信息

    @Override
    public void load(SucceedCallback<List<Data>> callback) {
        this.callback =callback;
        // 进行数据库监听操作
        registerDbChangedListener();
    }

    @SuppressWarnings("unchecked")
    public BaseDbRepository(){
        // 拿当前类的范型的数组信息
        Type[] types = Reflector.getActualTypeArguments(BaseDbRepository.class,this.getClass());
        dataClass = (Class<Data>) types[0];
    }

    @Override
    public void dispose() {
        this.callback = null;
        // 取消对应的监听
        DbHelper.removeChangedListener(dataClass,this);
        // 清空数据集里面的数据
        dataList.clear();
    }

    // 数据库统一通知的地方 增加或者更改
    @Override
    public void onDataSave(Data... list) {
        boolean isChanged =false;
        // 当数据库变更的操作
        for (Data data : list) {
            if(isRequired(data)){
                insertOrUpdate(data);
                isChanged =true;
            }
        }

        if(isChanged)
            notifyDataChanged();
    }

    // 数据库统一通知的地方， 删除
    @Override
    public void onDataDelete(Data... list) {
        // 删除的情况下不用判断
        // 当数据库删除的操作
        boolean isChanged = false;
        for (Data data : list) {
            if(dataList.remove(data))
                isChanged = true;
        }
        // 有数据变更进行界面刷新
        if(isChanged)
            notifyDataChanged();
    }


    // Db Flow框架通知的回调
    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Data> tResult) {
        // 数据加载成功
        if(tResult.size() == 0){
            dataList.clear();
            notifyDataChanged();
            return;
        }

        Data[] datas = CollectionUtil.toArray(tResult,dataClass);
        // 回到数据集更新的操作中
        onDataSave(datas);
    }

    // 更新或者插入操作
    protected void insertOrUpdate(Data data){
        int index = indexOf(data);
        if(index >= 0){
            // 更新操作
            replace(index,data);
        }else {
            // 插入操作
            insert(data);
        }
    }

    // 是否是必须的
    protected abstract boolean isRequired(Data data);

    /**
     * 添加监听器
     */
    protected void registerDbChangedListener(){
        DbHelper.addChangedListener(dataClass,this);
    }

    // 通知界面刷新的方法
    protected void notifyDataChanged(){
        if(callback != null){
            callback.onDataLoaded(dataList);
        }
    }

    // 插入方法
    protected void insert(Data data){
        dataList.add(data);
    }

    // 替换方法
    protected void replace(int index,Data data){
        dataList.remove(index);
        dataList.add(data);
    }

    protected int indexOf(Data newData){
        int index = -1;
        for (Data data : dataList) {
            index ++;
            if(data.isSame(newData)){
                return index;
            }
        }
        return -1;
    }
}
