package com.sashakhyzhun.locationmocker.utils;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class CommonUtil {

    public static void forceCloseKeyboard(EditText editText, Context ctx) {
        editText.setCursorVisible(false);
        final InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

}
