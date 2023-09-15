package org.sipdroid.sipua.model;

import com.google.gson.annotations.SerializedName;

public class AgentState {
    @SerializedName("check") Boolean status;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
