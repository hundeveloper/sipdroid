package org.sipdroid.sipua.viewmodel;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import org.sipdroid.sipua.MyLogSupport;

import org.sipdroid.sipua.MyConst;
import org.sipdroid.sipua.model.AgentState;
import org.sipdroid.sipua.model.CallBook;
import org.sipdroid.sipua.model.USIMModel;
import org.sipdroid.sipua.retrofit.BaseRetrofit;
import org.sipdroid.sipua.retrofit.RetrofitAPI;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public abstract class BaseViewModel<T> extends ViewModel  implements Callback<T> {

    public RetrofitAPI retrofitAPI = null;
    private Handler handler;
    private int requestCnt = 0;
    public int API_ID = -1;

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public abstract void onRFResponseSuccess(int apiId, Response response);
    public abstract void onRFResponseFail(int apiId, String status);

    public BaseViewModel(Context context) { retrofitAPI = BaseRetrofit.getRetrofitBuilder(); }

    public void commit(int API_ID, String companyId, String hp_number){
        this.API_ID = API_ID;
        requestCnt++;
        switch(API_ID){
            case MyConst.API_Login:
                if(companyId != null && hp_number != null) {
                    RequestBody requestid = RequestBody.create(MediaType.parse("text/plain"), companyId);
                    RequestBody requesthp = RequestBody.create(MediaType.parse("text/plain"), hp_number);
                    retrofitAPI.login(requestid, requesthp).enqueue((Callback<USIMModel>) this);
                }
                break;
            case MyConst.API_NUMBER_CHECK:
                RequestBody requestid = RequestBody.create(MediaType.parse("text/plain"), hp_number);
//                retrofitAPI.call_status("+821057692354").enqueue((Callback<AgentState>) this);
                retrofitAPI.number_check(hp_number).enqueue((Callback<AgentState>) this);
                break;
            case MyConst.API_CALLBOOK_TAKE:
                retrofitAPI.callbook_take().enqueue((Callback<CallBook>) this);
                break;


        }
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        Object dataBody = response.body();
        int responseCode = response.code();

        Log.e("network", "통신성공@@@@@@@@@@@@@@@@@@@");
        if (dataBody == null) {
            MyLogSupport.log_print(String.valueOf(dataBody));
            MyLogSupport.log_print("Check Server!!");
            MyLogSupport.log_print("response errorBody :: "+response.errorBody()+ "");
            MyLogSupport.log_print("response.code :: "+response.code()+ "");
            return;
        }

        if(response.isSuccessful()) {
            requestDelayedSuccess(API_ID, response);
        }else {
            if (responseCode == 401) {
                requestDelayedFail(API_ID, "401");
            }
            Log.d("error", "what code?");
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        MyLogSupport.log_print("onFailure t -> "+t.toString());
        requestDelayedFail(API_ID, t.toString());
    }

    void requestDelayedSuccess(final int apiId, final Response response) {
        MyLogSupport.log_print(apiId+" : Check Server!! : requestDelayedSuccess");
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                requestCnt--;
                onRFResponseSuccess(apiId, response);

                if (requestCnt <= 0) {
                    requestCnt = 0;
                }
            }
        }, 50);
    }

    void requestDelayedFail(final int apiId, final String status) {
        MyLogSupport.log_print("Check Server!! Fail...");
        MyLogSupport.log_print("requestDelayedFail!! status -> "+status);
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                requestCnt--;
                onRFResponseFail(apiId, status);

                if (requestCnt <= 0) {
                    requestCnt = 0;
                }
            }
        }, 50);
    }
}
