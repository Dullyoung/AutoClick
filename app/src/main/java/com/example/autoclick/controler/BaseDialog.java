package com.example.autoclick.controler;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.autoclick.R;
import com.example.autoclick.Utils.ScreenUtil;

import butterknife.ButterKnife;

public abstract class BaseDialog extends Dialog {



    public BaseDialog(Context context) {
        super(context, R.style.DialogTheme);

        View view = LayoutInflater.from(context).inflate(
                getLayoutId(), null);
        ButterKnife.bind(this, view);
        setContentView(view);

        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = ScreenUtil.getWidth(context);
        layoutParams.height = ScreenUtil.getHeight(context);
        view.setLayoutParams(layoutParams);

        setCancelable(true);
        initViews();
    }

    protected abstract int getLayoutId();

    protected abstract void initViews();




}
