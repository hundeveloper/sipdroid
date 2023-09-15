package org.sipdroid.sipua.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.View;

import androidx.lifecycle.Observer;


import org.sipdroid.sipua.MyLogSupport;

import org.sipdroid.sipua.NtApplication;
import org.sipdroid.sipua.R;
import org.sipdroid.sipua.databinding.ActivityAfterBinding;
import org.sipdroid.sipua.model.CallBook;
import org.sipdroid.sipua.support.MyToastSupport;
import org.sipdroid.sipua.viewmodel.ReadViewModel;

import java.util.Timer;
import java.util.TimerTask;


public class AfterActivity extends BaseActivity {

    private ReadViewModel readViewModel = NtApplication.handlerViewModel.getReadViewModel();
    private ActivityAfterBinding binding;
    private SharedPreferences auto;
    private Intent it;
    private MyToastSupport toastSupport = new MyToastSupport(this);
    private Timer timer = new Timer();

    @Override
    protected void createActivity() {
        binding = ActivityAfterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Init();

    }

    private void Init() {
        binding.logoutButton.setOnClickListener(this);
        binding.buttonAutoCall.setOnClickListener(this);

        auto = getSharedPreferences("autoLogin", MODE_PRIVATE);
        it = new Intent(this, MainActivity.class);

        // 현재 이 전화번호가 auto_call을 돌려도되는건지 체크?
        toastSupport.showToast("5초 뒤에 자동오토콜 프로그램이 동작됩니다.");

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                readViewModel.number_check(auto.getString("HpNum", ""));
            }
        }, 0, 5000); // 0초 후에 시작하고 5초마다 반복 실행





//        new Handler().postDelayed(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                readViewModel.number_check(auto.getString("HpNum", ""));
//            }
//        }, 5000);


        readViewModel.auto_progress.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    MyLogSupport.log_print("autocall : true 승인받았으므로 실행합니다.");
                    readViewModel.callbook_take();// 전화번호부 가져오기

                } else {
                    MyLogSupport.log_print("autocall : false 거절");
                }
            }
        });

        readViewModel.callbook.observe(this, new Observer<CallBook>() {
            @Override
            public void onChanged(CallBook callBook) {
                if (callBook != null) {
                    MyLogSupport.log_print("전화번호 -> " + callBook.getHpRe());

                    Performdial(callBook.getHpRe());
                }
            }
        });

        readViewModel.autocall_start_and_stop.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    MyLogSupport.log_print("autocall_start_and_stop -> true");
                    readViewModel.number_check(auto.getString("HpNum", ""));
                } else {
                    MyLogSupport.log_print("autocall_start_and_stop -> false");
                }
            }
        });
    }

    public void Performdial(String phone_hp) {

        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.setData(Uri.parse("tel:" + phone_hp));
        try {
            this.startActivity(intent);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void resumeActivity() {

    }

    @Override
    protected void startActivity() {

    }

    @Override
    protected void pauseActivity() {

    }

    @Override
    protected void onRestartActivity() {

    }

    @Override
    protected void destroyActivity() {
        binding = null;
        timer.cancel();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.logout_button){
            SharedPreferences.Editor autoLoginEdit = auto.edit();
            autoLoginEdit.clear();
            autoLoginEdit.commit();
            startActivity(it);
            finish();
        } else if( v.getId() == R.id.button_auto_call){

        }
    }
}
