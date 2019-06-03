package com.zylahealth.zylachatbot.smack;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import com.zylahealth.zylachatbot.R;
import com.zylahealth.zylachatbot.databinding.ActivitySmackBinding;
import com.zylahealth.zylachatbot.viewmodel.SmackActivityVM;

public class SmackActivity extends AppCompatActivity {

    private ActivitySmackBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_smack);
        SmackActivityVM activityVM = new SmackActivityVM(this, binding, getLifecycle());
        binding.setViewModel(activityVM);

    }
}
