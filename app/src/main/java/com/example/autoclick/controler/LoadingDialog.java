package com.example.autoclick.controler;

import android.content.Context;
import android.widget.TextView;


import com.example.autoclick.R;

import butterknife.BindView;

public class LoadingDialog extends BaseDialog {

    @BindView(R.id.message)
    TextView mMessageTv;

    private boolean canCancel;

    public void setCanCancel(boolean canCancel) {
        this.canCancel = canCancel;
    }

    public LoadingDialog(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_loading;
    }

    public void show(String message) {
        mMessageTv.setText(message);
        if(!isShowing()){
            show();
        }
    }

    public void setMessageTv(String msg){
        mMessageTv.setText(msg);
    }

    @Override
    public void onBackPressed() {
        if(canCancel){
            super.onBackPressed();
        }
    }

    @Override
    protected void initViews() {
        canCancel = true;
    }
}
