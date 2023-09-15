package org.sipdroid.sipua.model;

import com.google.gson.annotations.SerializedName;

public class USIMModel {

    @SerializedName("login") Boolean success;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

}
