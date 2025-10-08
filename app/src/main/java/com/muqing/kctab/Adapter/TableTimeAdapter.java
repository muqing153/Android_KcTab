package com.muqing.kctab.Adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.muqing.BaseAdapter;
import com.muqing.gj;
import com.muqing.kctab.DataType.TableTimeData;
import com.muqing.kctab.MainActivity;
import com.muqing.kctab.databinding.ItemTableTimeBinding;

import java.util.List;

public class TableTimeAdapter extends BaseAdapter<ItemTableTimeBinding, TableTimeData> {
    public boolean showJie = true;

    public TableTimeAdapter(Context context, List<TableTimeData> dataList) {
        super(context, dataList);
    }

    @Override
    protected ItemTableTimeBinding getViewBindingObject(LayoutInflater inflater, ViewGroup parent, int viewType) {
        ItemTableTimeBinding inflate = ItemTableTimeBinding.inflate(inflater, parent, false);
        if (MainActivity.TableStyle != null) {
            AutoTableAdapter.bindView(MainActivity.TableStyle, inflate.getRoot(), true);
        }
        return inflate;
    }
    @Override
    protected void onBindView(TableTimeData data, ItemTableTimeBinding viewBinding, ViewHolder<ItemTableTimeBinding> viewHolder, int position) {
//        viewBinding.getRoot().getLayoutParams().height = (int) MainActivity.TableStyle.getHeight(context);

        viewBinding.getRoot().getLayoutParams().height = gj.dp2px(context, 70);
        viewBinding.getRoot().requestLayout();

        viewBinding.title.setVisibility(showJie ? View.VISIBLE : View.GONE);
        viewBinding.title.setText(data.title);
        viewBinding.starttime.setText(data.starttime);
        viewBinding.endtime.setText(data.endtime);
    }
}