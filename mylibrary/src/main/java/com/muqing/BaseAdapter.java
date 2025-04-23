package com.muqing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import java.util.List;

public abstract class BaseAdapter<ViewBindingType extends ViewBinding,DataType> extends RecyclerView.Adapter<BaseAdapter.ViewHolder<ViewBindingType>>  {

    protected final Context context;
    public final List<DataType> dataList;
    private final LayoutInflater layoutInflater;
    public BaseAdapter(Context context, List<DataType> dataList) {
        this.context = context;
        this.dataList = dataList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    /**
     * 创建 ViewHolder
     */
    @NonNull
    @Override
    public ViewHolder<ViewBindingType> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        Log.i("打印", "onCreateViewHolder: "+viewType);
        return new ViewHolder<>(getViewBindingObject(layoutInflater, parent, viewType));
    }
    /**
     * 绑定 ViewHolder
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder<ViewBindingType> holder, int position) {
        DataType data = dataList.get(position);
        ViewBindingType viewBinding = holder.viewBinding;
        onBindView(data, viewBinding, holder, position);
    }

    /**
     * 获取 ViewBinding 实例（由子类实现）
     */
    protected abstract ViewBindingType getViewBindingObject(LayoutInflater inflater, ViewGroup parent, int viewType);

    /**
     * 绑定视图（由子类实现）
     */
    protected abstract void onBindView(DataType data, ViewBindingType viewBinding, ViewHolder<ViewBindingType> viewHolder, int position);


    /**
     * 获取列表项数量
     */
    @Override
    public int getItemCount() {
        return dataList.size();
    }

    /**
     * ViewHolder 类
     */
    public static class ViewHolder<T extends ViewBinding> extends RecyclerView.ViewHolder {
        public final T viewBinding;

        public ViewHolder(T viewBinding) {
            super(viewBinding.getRoot());
            this.viewBinding = viewBinding;
        }
    }
}
