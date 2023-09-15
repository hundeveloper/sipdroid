package org.sipdroid.sipua.retrofit;



import org.sipdroid.sipua.MyConst;
import org.sipdroid.sipua.model.AgentState;
import org.sipdroid.sipua.model.CallBook;
import org.sipdroid.sipua.model.USIMModel;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface RetrofitAPI {

    @Multipart
    @POST(MyConst.login)
    Call<USIMModel> login(@Part("COMPANY_ID") RequestBody companyId, @Part("HP") RequestBody usimNo);

    @GET(MyConst.status)
    Call<AgentState> number_check(@Query("HP") String hp);

    @GET(MyConst.callbook)
    Call<CallBook> callbook_take();
}

