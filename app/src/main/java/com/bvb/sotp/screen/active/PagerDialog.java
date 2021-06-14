package com.bvb.sotp.screen.active;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.ViewPager;

import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import com.bvb.sotp.R;
import com.bvb.sotp.screen.active.AddUserEvent;import com.bvb.sotp.screen.active.ScreenSlidePagerAdapter;import com.bvb.sotp.view.RegularTextView;

public class PagerDialog extends DialogFragment {

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.dialog_active_success, container, false);

        ScreenSlidePagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
        ViewPager pager = rootView.findViewById(R.id.pager);
        RegularTextView skip = rootView.findViewById(R.id.skip);
        ImageView close = rootView.findViewById(R.id.close);

        skip.setOnClickListener(v -> {
            dismiss();
            EventBus.getDefault().post(new AddUserEvent());

        });

        close.setOnClickListener(v -> {
            EventBus.getDefault().post(new AddUserEvent());

            dismiss();
        });
        DotsIndicator dots = rootView.findViewById(R.id.dots_indicator);
        pager.setAdapter(pagerAdapter);
        dots.setViewPager(pager);

        return rootView;
    }

}