package com.haocai.app.network.base.presenter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * Created by jslsxu on 16/12/22.
 */

public abstract class BaseRecyclerAdapter<T> extends BaseQuickAdapter<T, BaseViewHolder> {

    public BaseRecyclerAdapter(int layoutResID){
        super(layoutResID, null);
    }

    public void clear(){
        setNewData(null);
    }

    public void setData(List<T> data, boolean isRefresh){
        if(isRefresh){
            setNewData(data);
        }
        else {
            addData(data);
        }
    }
}
