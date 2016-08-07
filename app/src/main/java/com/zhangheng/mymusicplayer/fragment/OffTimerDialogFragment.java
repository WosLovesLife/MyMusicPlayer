package com.zhangheng.mymusicplayer.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.project.myutilslibrary.Dp2Px;
import com.zhangheng.mymusicplayer.MusicApp;
import com.zhangheng.mymusicplayer.R;

/**
 * Created by zhangH on 2016/6/6.
 */
public class OffTimerDialogFragment extends DialogFragment implements View.OnClickListener {

    public static OffTimerDialogFragment newInstance() {
        return  new OffTimerDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View view = View.inflate(getActivity(), R.layout.dialog_off_timer, null);
        view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                ViewGroup.LayoutParams params = view.getLayoutParams();
                params.height = Dp2Px.toPX(getActivity(), 160);
                view.setLayoutParams(params);
                return true;
            }
        });

        setItem(view);

        return new AlertDialog.Builder(getActivity()).setView(view).create();
    }

    public void setItem(View v) {
        TextView timer5 = (TextView) v.findViewById(R.id.off_timer_5);
        timer5.setOnClickListener(this);
        TextView timer10 = (TextView) v.findViewById(R.id.off_timer_10);
        timer10.setOnClickListener(this);
        TextView timer15 = (TextView) v.findViewById(R.id.off_timer_15);
        timer15.setOnClickListener(this);
        TextView timer20 = (TextView) v.findViewById(R.id.off_timer_20);
        timer20.setOnClickListener(this);
        TextView timer30 = (TextView) v.findViewById(R.id.off_timer_30);
        timer30.setOnClickListener(this);
        TextView timer45 = (TextView) v.findViewById(R.id.off_timer_45);
        timer45.setOnClickListener(this);
        TextView timer60 = (TextView) v.findViewById(R.id.off_timer_60);
        timer60.setOnClickListener(this);
        TextView timer120 = (TextView) v.findViewById(R.id.off_timer_120);
        timer120.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        long minute = 0;
        switch (v.getId()) {
            case R.id.off_timer_5:
                minute = (int) (5 * DateUtils.MINUTE_IN_MILLIS);
                break;
            case R.id.off_timer_10:
                minute = (int) (10 * DateUtils.MINUTE_IN_MILLIS);
                break;
            case R.id.off_timer_15:
                minute = (int) (15 * DateUtils.MINUTE_IN_MILLIS);
                break;
            case R.id.off_timer_20:
                minute = (int) (20 * DateUtils.MINUTE_IN_MILLIS);
                break;
            case R.id.off_timer_30:
                minute = (int) (30 * DateUtils.MINUTE_IN_MILLIS);
                break;
            case R.id.off_timer_45:
                minute = (int) (45 * DateUtils.MINUTE_IN_MILLIS);
                break;
            case R.id.off_timer_60:
                minute = (int) (60 * DateUtils.MINUTE_IN_MILLIS);
                break;
            case R.id.off_timer_120:
                minute = (int) (120 * DateUtils.MINUTE_IN_MILLIS);
                break;
        }
        if (minute >= 5) {
            ((MusicApp) getActivity().getApplication()).setOffTimer(minute);
        }
        dismiss();
    }
}
