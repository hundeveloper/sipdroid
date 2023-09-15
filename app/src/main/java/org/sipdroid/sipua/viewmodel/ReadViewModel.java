package org.sipdroid.sipua.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;


import org.sipdroid.sipua.MyLogSupport;

import org.sipdroid.sipua.MyConst;
import org.sipdroid.sipua.model.AgentState;
import org.sipdroid.sipua.model.CallBook;
import org.sipdroid.sipua.model.USIMModel;

import retrofit2.Response;

public class ReadViewModel extends BaseViewModel {

    public ReadViewModel(Context context) {
        super(context);
    }

    public final SingleLiveEvent<Boolean> loginsuccess = new SingleLiveEvent<>();
    public final SingleLiveEvent<Boolean> server_online = new SingleLiveEvent<>();
    public final SingleLiveEvent<Boolean> auto_progress = new SingleLiveEvent<>();
    public final MutableLiveData<CallBook> callbook = new MutableLiveData<>();
    public final SingleLiveEvent<Boolean> autocall_start_and_stop = new SingleLiveEvent<>();

    public void login(String companyId, String hp_number) { commit(MyConst.API_Login, companyId, hp_number);}
    public void number_check(String hp_number) { commit(MyConst.API_NUMBER_CHECK, null, hp_number); }
    public void callbook_take() { commit(MyConst.API_CALLBOOK_TAKE, "", ""); }

    @Override
    public void onRFResponseSuccess(int apiId, Response response) {

        if(apiId == MyConst.API_Login){
           USIMModel usimModel = (USIMModel) response.body();
           MyLogSupport.log_print("API_LOGIN : "+usimModel.getSuccess().toString());
           loginsuccess.setValue(usimModel.getSuccess());

        }else if(apiId == MyConst.API_NUMBER_CHECK){
            AgentState agentState = (AgentState) response.body();
            MyLogSupport.log_print("API_NUMBER_CHECK : "+agentState.getStatus().toString());
            auto_progress.setValue(agentState.getStatus());

        }else if(apiId == MyConst.API_CALLBOOK_TAKE) {
            CallBook callBook = (CallBook) response.body();
            MyLogSupport.log_print("API_CALLBOOK_TAKE ");
            MyLogSupport.log_print("callbook 전화번호 -> "+callBook.getHpRe());
            callbook.setValue(callBook);

        }
    }

    @Override
    public void onRFResponseFail(int apiId, String status) {
        if(apiId == MyConst.API_Login){
            server_online.setValue(true);
        }
    }

}
