package com.zylahealth.zylachatbot.adapter.holder;

import android.view.View;

import androidx.databinding.DataBindingUtil;
import com.zylahealth.zylachatbot.databinding.ReceiveNormalTextVHBinding;

public class ReceiveNormalTextVH extends ChatVH {

    private ReceiveNormalTextVHBinding binding;

    public ReceiveNormalTextVH(View itemView) {
        super(itemView);

        binding = DataBindingUtil.bind(itemView);
    }

    public ReceiveNormalTextVHBinding getBinding() {
        return binding;
    }

    public void setBinding(ReceiveNormalTextVHBinding binding) {
        this.binding = binding;
    }
}
