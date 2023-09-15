package org.sipdroid.sipua.di;

import android.content.Context;

import org.sipdroid.sipua.viewmodel.ReadViewModel;

public class HandlerBase {
    public HandlerBase(Context context) {
        this.context = context;
    }

    private Context context;
    private static ReadViewModel readviewmodel = null;

    public ReadViewModel getReadViewModel() {
        if (readviewmodel == null) {
            readviewmodel = new ReadViewModel(context);
        }
        return readviewmodel;
    }


}
