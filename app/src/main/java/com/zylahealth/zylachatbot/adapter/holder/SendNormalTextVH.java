package com.zylahealth.zylachatbot.adapter.holder;

import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.zylahealth.zylachatbot.databinding.SendNormalTextVHBinding;

public class SendNormalTextVH extends ChatVH {

    private SendNormalTextVHBinding binding;

    public SendNormalTextVH(View itemView) {
        super(itemView);

        binding = DataBindingUtil.bind(itemView);
    }

    public SendNormalTextVHBinding getBinding() {
        return binding;
    }

    public void setBinding(SendNormalTextVHBinding binding) {
        this.binding = binding;
    }
}
