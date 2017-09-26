package com.example.common.widget.adapter;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by Administrator on 2017/8/31.
 */

public abstract class TextWatcherAdapter implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
