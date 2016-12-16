package moe.hmo.myrxbus;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import moe.rxbus.RxBus;
import moe.rxbus.Subscribe;
import moe.rxbus.ThreadMode;
import rx.Subscription;

/**
 * @author H-Mo
 * time 16/12/14  10:21
 * desc ${TODD}
 */
public class MyFragment extends Fragment implements View.OnClickListener {

    private TextView text_Tv;
    private Button handler_Bt;

    private boolean hasSubscriber;    // 是否订阅，true为订阅，默认false
    private Subscription mSubscribe;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_one, container, false);
        initView(view);
        return view;
    }


    private void initView(View view) {
        text_Tv = (TextView) view.findViewById(R.id.text_Tv);
        handler_Bt = (Button) view.findViewById(R.id.handler_Bt);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initEvent();
    }

    private void initEvent() {
        handler_Bt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(hasSubscriber){
            // 当前状态为订阅，需要取消订阅
            RxBus.unRegister(this);
            handler_Bt.setText("暂未订阅，点击订阅");
        }else{
            // 当前状态为未订阅，需要订阅
            RxBus.register(this);
            handler_Bt.setText("已经订阅，点击取消");
        }
        // 改变当前状态
        hasSubscriber = !hasSubscriber;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        // 取消订阅，防止内存泄漏
        RxBus.unRegister(MyFragment.class);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void subscribeString(String msg){
        // 处理事件
        text_Tv.setText(msg);
    }

}
