package com.muqing.kctab.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.muqing.BaseAdapter;
import com.muqing.kctab.DataType.TableStyleData;
import com.muqing.kctab.databinding.GridItemBinding;
import com.muqing.kctab.databinding.ItemTableAutoBinding;
import com.muqing.kctab.databinding.ItemZhouBoxBinding;

import java.util.List;

public class AutoTableAdapter extends BaseAdapter<ItemTableAutoBinding, TableStyleData> {

    public TableStyleData tableStyleData;
    SharedPreferences kebiaosp;
    public AutoTableAdapter(Context context, List<TableStyleData> dataList) {
        super(context, dataList);
        kebiaosp = context.getSharedPreferences("tablestyle", MODE_PRIVATE);
        Gson gson = new Gson();
        tableStyleData = gson.fromJson(kebiaosp.getString("tablestyle", gson.toJson(new TableStyleData())), TableStyleData.class);
    }

    @Override
    protected ItemTableAutoBinding getViewBindingObject(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return ItemTableAutoBinding.inflate(inflater, parent, false);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onBindView(TableStyleData data, ItemTableAutoBinding viewBinding, ViewHolder<ItemTableAutoBinding> viewHolder, int position) {
        viewBinding.card.setUseCompatPadding(data.cardUseCompatPadding);
        viewBinding.card2.setUseCompatPadding(data.cardUseCompatPadding);
        if (data.cardElevation > -1) {
            viewBinding.card.setCardElevation(data.cardElevation);
            viewBinding.card2.setCardElevation(data.cardElevation);
        }
        if (data.cardCornerRadius > -1) {
            viewBinding.card.setRadius(data.cardCornerRadius);
            viewBinding.card2.setRadius(data.cardCornerRadius);
        }
        if (tableStyleData.equals(data)) {
            viewBinding.button.setVisibility(View.GONE);
        } else {
            viewBinding.button.setVisibility(View.VISIBLE);
        }
        viewBinding.button.setOnClickListener(view -> {
            tableStyleData = data;
            kebiaosp.edit().putString("tablestyle", new Gson().toJson(data)).apply();
            notifyDataSetChanged();
        });
    }

}
