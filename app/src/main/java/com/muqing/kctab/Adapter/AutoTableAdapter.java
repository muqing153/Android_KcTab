package com.muqing.kctab.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.muqing.BaseAdapter;
import com.muqing.kctab.DataType.TableStyleData;
import com.muqing.kctab.MainActivity;
import com.muqing.kctab.databinding.ItemTableAutoBinding;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class AutoTableAdapter extends BaseAdapter<ItemTableAutoBinding, TableStyleData> {


    public AutoTableAdapter(Context context, List<TableStyleData> dataList) {
        super(context, dataList);
    }

    @Override
    protected ItemTableAutoBinding getViewBindingObject(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return ItemTableAutoBinding.inflate(inflater, parent, false);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onBindView(TableStyleData data, ItemTableAutoBinding viewBinding, ViewHolder<ItemTableAutoBinding> viewHolder, int position) {
        bindView(data, viewBinding.getRoot());
        if (MainActivity.TableStyle == null && position == 0) {
            viewBinding.useButton.setEnabled(false);
        } else {
            viewBinding.useButton.setEnabled(!data.equals(MainActivity.TableStyle));
        }
        viewBinding.useButton.setOnClickListener(v -> {
            if (position == 0) {
                MainActivity.TableStyle = null;
            } else {
                MainActivity.TableStyle = data;
            }
            SharedPreferences sharedPreferences = context.getSharedPreferences("tablestyle", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("tablestyle", new Gson().toJson(data));
            editor.apply();
            notifyDataSetChanged();
            // 跳转页面
        });
    }

    @SuppressLint("SetTextI18n")
    public static void bindView(Object data, View root) {
        if (data == null) return;
        Field[] fields = data.getClass().getFields();
        for (Field field : fields) {
            try {
                Object value = field.get(data);
                if (value instanceof TableStyleData.ViewAttrs) {
                    TableStyleData.ViewAttrs attr = (TableStyleData.ViewAttrs) value;
                    // 找到对应控件
                    int viewId = root.getResources().getIdentifier(field.getName(), "id", root.getContext().getPackageName());
                    View view = root.findViewById(viewId);
                    if (view instanceof TextView) {
                        ((TextView) view).setText(attr.text != null ? attr.text : "");
                        if (attr.textColor != null)
                            ((TextView) view).setTextColor(Color.parseColor(attr.textColor));
                        view.setVisibility(getVisibility(attr.visibility));
                    }
                } else if (value instanceof TableStyleData.ViewGridAttrs) {
                    TableStyleData.ViewGridAttrs gridAttr = (TableStyleData.ViewGridAttrs) value;
                    // 找控件
                    int viewId = root.getResources().getIdentifier(field.getName() + "Layout", "id", root.getContext().getPackageName());
                    View view = root.findViewById(viewId);
                    if (view != null) {
                        // 背景色
                        if (gridAttr.background != null)
                            view.setBackgroundColor(Color.parseColor(gridAttr.background));
                        // 高度 dp → px
                        int height = gridAttr.getHeight(view.getContext());
                        if (height > 0)
                            view.getLayoutParams().height = height;
                        view.requestLayout();
                        // 如果是 MaterialCardView，支持 cardBackgroundColor
                        if (view instanceof MaterialCardView && gridAttr.appAttrs != null) {
                            applyMaterialCardViewAttrs((MaterialCardView) view, gridAttr.appAttrs);
                        }
                    }
                    // 递归绑定子对象
                    bindView(value, root);
                } else {
                    // 递归绑定子对象
                    bindView(value, root);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static void applyMaterialCardViewAttrs(MaterialCardView cardView, Map<String, Object> attrs) {
        for (Map.Entry<String, Object> entry : attrs.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            try {
                switch (key) {
                    case "cardBackgroundColor":
                        if (value instanceof String) {
                            cardView.setCardBackgroundColor(Color.parseColor((String) value));
                        }
                        break;
                    case "cardElevation":
                        if (value instanceof Number) {
                            cardView.setCardElevation(((Number) value).floatValue());
                        }
                        break;
                    case "cardCornerRadius":
                        if (value instanceof Number) {
                            cardView.setRadius(((Number) value).floatValue());
                        }
                        break;
                    case "strokeColor":
                        if (value instanceof String) {
                            cardView.setStrokeColor(Color.parseColor((String) value));
                        }
                        break;
                    case "strokeWidth":
                        if (value instanceof Number) {
                            cardView.setStrokeWidth((int) (((Number) value).floatValue()));
                        }
                        break;
                    default:
                        // 可扩展其他属性
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 字符串可见性转换
     */
    public static int getVisibility(String visibility) {
        if (visibility == null) return View.VISIBLE;
        switch (visibility.toLowerCase()) {
            case "gone":
                return View.GONE;
            case "invisible":
                return View.INVISIBLE;
            default:
                return View.VISIBLE;
        }
    }
}
