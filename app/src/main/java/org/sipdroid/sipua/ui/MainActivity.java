package org.sipdroid.sipua.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Toast;


import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;

import com.gun0912.tedpermission.normal.TedPermission;
import org.sipdroid.sipua.MyLogSupport;

import org.sipdroid.sipua.NtApplication;
import org.sipdroid.sipua.PermissionListener;
import org.sipdroid.sipua.ProgressDialog;
import org.sipdroid.sipua.R;
import org.sipdroid.sipua.databinding.ActivityMainBinding;
import org.sipdroid.sipua.service.Alarm;
import org.sipdroid.sipua.support.MyToastSupport;
import org.sipdroid.sipua.viewmodel.ReadViewModel;

import java.util.Calendar;

public class MainActivity extends BaseActivity {
    private ActivityMainBinding binding;
    private TelephonyManager telephonyManager;
    private ReadViewModel readViewModel = NtApplication.handlerViewModel.getReadViewModel();
    private Intent it;
    private MyToastSupport myToastSupport = new MyToastSupport(this);
    private SharedPreferences auto;
    private org.sipdroid.sipua.ProgressDialog progressDialog;
    private AlarmManager alarmManager;
//    private TimePicker timePicker;

    @Override
    protected void createActivity() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.companyId.setText("USIM1");

        permission_check();
        Go_Settings();

    }

    private void permission_check() {
        String[] PERMISSIONS;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PERMISSIONS = new String[]{
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_PHONE_NUMBERS,
                    Manifest.permission.SYSTEM_ALERT_WINDOW
            };
        }else{
            PERMISSIONS = new String[]{
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_PHONE_STATE
            };
        }

        PermissionListener permissionListener = new org.sipdroid.sipua.PermissionListener();


        ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // TODO : API 30 이상
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED)
            TedPermission.create()
                    .setPermissionListener(permissionListener)
                    .setRationaleMessage("앱 실행을 위해 필요한 권한을 요청합니다.")
                    .setDeniedMessage("권한을 설정하지않을시 앱이 종료됩니다.")
                    .setPermissions(Manifest.permission.CALL_PHONE)
                    .setPermissions(Manifest.permission.READ_PHONE_NUMBERS)
                    .check();

        } else { // TODO : API 30 이하

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED)
            TedPermission.create()
                    .setPermissionListener(permissionListener)
                    .setRationaleMessage("앱 실행을 위해서 필요한 권한을 요청합니다.")
                    .setDeniedMessage("권한을 설정하지않을시 앱이 종료됩니다.")
                    .setPermissions(Manifest.permission.CALL_PHONE)
                    .setPermissions(Manifest.permission.READ_PHONE_STATE)
                    .check();
        }

    }

    private void Go_Settings() {
        // TODO : 로그인설정
        auto = getSharedPreferences("autoLogin", Activity.MODE_PRIVATE);
        String checkbox = auto.getString("checkbox", "");
        String companyId = auto.getString("CompanyId", "");
        String HpNum = auto.getString("HpNum", "");

        if (checkbox.equals("y")) {
            readViewModel.login(companyId, HpNum);
        }

        // TODO : 기능 동작 초기 설정
        it = new Intent(this, Sipdroid.class);
        telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        timePicker = binding.tpTimepicker;
        progressDialog = new ProgressDialog(this);
        binding.loginButton.setOnClickListener(this);
//        binding.alarmStart.setOnClickListener(this);


        // TODO : REST API 관찰호선
        readViewModel.loginsuccess.observe(this, new Observer<Boolean>() {
            @SuppressLint("MissingPermission")
            @Override
            public void onChanged(Boolean aBoolean) {
                MyLogSupport.log_print("onchange 작동중!");
                if (aBoolean) {
                    SharedPreferences.Editor autoLoginEdit = auto.edit();

                    autoLoginEdit.putString("CompanyId", binding.companyId.getText().toString());
                    autoLoginEdit.putString("HpNum", telephonyManager.getLine1Number());

                    // TODO : 자동로그인 체크 여부
                    if (binding.checkbox.isChecked()) {
                        autoLoginEdit.putString("checkbox", "y");
                        MyLogSupport.log_print("자동로그인활성화");

                    } else {
                        autoLoginEdit.putString("checkbox", "n");
                        MyLogSupport.log_print("자동로그인비활성화");
                    }
                    autoLoginEdit.commit();

                    // TODO : 화면 넘기기
                    startActivity(it);
                    progressDialog.closeDialog();
                    finish();
                } else {
                    myToastSupport.showToast("로그인에 실패하였습니다. ");
                    progressDialog.closeDialog();
                    // 자동로그인 초기화
                }

            }
        });

        readViewModel.server_online.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean == true){
                    myToastSupport.showToast("현재 서버가 off상태입니다.");
                }
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("HardwareIds")
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.login_button) {
            MyLogSupport.log_print("로그인버튼클릭!");
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            if(telephonyManager.getLine1Number() == null){
                myToastSupport.showToast("유심을 장착해 주세요.");
                return;
            }
            readViewModel.login(binding.companyId.getText().toString(), telephonyManager.getLine1Number());
            myToastSupport.showToast("로그인중입니다.");
            progressDialog.showDialog();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.closeDialog();
                }
            },2000);

        }
//        else if(v.getId() == R.id.alarm_start){
//            regist();
//        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    public void regist() {
        Intent intent = new Intent(this, Alarm.class);
        intent.putExtra("app", "restart");
        PendingIntent pIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        MyLogSupport.log_print("regist실행");

        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
//        calendar.set(Calendar.MINUTE, timePicker.getMinute());
//        calendar.set(Calendar.SECOND, 0);

        MyLogSupport.log_print("Alarm On : " + calendar.getTime());
        MyLogSupport.log_print("Alarm On : " + calendar.getTimeInMillis());

        // 지정한 시간에 매일 알림
        alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), pIntent), pIntent);

    }

    @Override
    protected void resumeActivity () {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(!Settings.canDrawOverlays(this)){
                Toast.makeText(this,"(autocall)다른 앱 위에 표시 -> 허용을 해주세요.",Toast.LENGTH_SHORT).show();
                Intent it = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + this.getPackageName()));
                startActivity(it);
            }
        }
    }

    @Override
    protected void startActivity () {

    }

    @Override
    protected void pauseActivity () {

    }

    @Override
    protected void onRestartActivity () {

    }

    @Override
    protected void destroyActivity () {
        progressDialog.closeDialog();
    }


    }
