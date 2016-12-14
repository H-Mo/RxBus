package moe.hmo.myrxbus;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import java.io.BufferedReader;

import moe.rxbus.RxBus;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText text_Et;
    private Button post_Bt;
    private FrameLayout fl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        text_Et = (EditText)findViewById(R.id.text_Et);
        post_Bt = (Button)findViewById(R.id.post_Bt);
        fl = (FrameLayout)findViewById(R.id.fl);
    }

    private void initData() {
        MyFragment fragment = new MyFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fl,fragment);
        transaction.commit();
    }

    private void initEvent() {
        post_Bt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // 获取编辑框的文本
        String text = text_Et.getText().toString();
        // 投递事件
        RxBus.get().post(text);
    }
}
