package com.zylahealth.zylachatbot.smack;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import com.zylahealth.zylachatbot.R;
import com.zylahealth.zylachatbot.databinding.ActivityLoginBinding;
import com.zylahealth.zylachatbot.viewmodel.LoginActivityVM;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        LoginActivityVM activityVM = new LoginActivityVM(this, binding, getLifecycle());
        binding.setViewModel(activityVM);

    }
}
