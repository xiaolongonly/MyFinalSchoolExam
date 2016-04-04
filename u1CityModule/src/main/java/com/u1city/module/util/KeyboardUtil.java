package com.u1city.module.util;

import android.app.Activity;
import android.graphics.Color;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.u1city.module.R;

import java.util.List;

public class KeyboardUtil
{
    private Activity context;
    private KeyboardView keyboardView;
    private Keyboard k2;// 数字键盘
    private Keyboard k1;// 数字键盘
    private boolean isnun = true;// 是否数据键盘
    public boolean isupper = false;// 是否大写
    private InputListener inputListener;
    private TextView[] tvs = new TextView[6];
    private ViewGroup viewGroup;
    private int keyBoardHeight;

    public KeyboardUtil(Activity context, ViewGroup viewGroup, InputListener inputListener)
    {
        this.context = context;
        this.viewGroup = viewGroup;
        inittvs();
        k2 = new Keyboard(context, R.xml.symbols_num);
        k1 = new Keyboard(context, R.xml.qwerty);
        keyboardView = (KeyboardView) context.findViewById(R.id.keyboard_view);
        keyboardView.setEnabled(false);
        keyboardView.setKeyboard(k2);
        keyboardView.setPreviewEnabled(false);
        keyboardView.setOnKeyboardActionListener(listener);
        this.inputListener = inputListener;

        List<Key> keys = keyboardView.getKeyboard().getKeys();
        for (Key key : keys)
        {
            if (key.codes[0] == Keyboard.KEYCODE_MODE_CHANGE || key.codes[0] == Keyboard.KEYCODE_DELETE)
            {
                key.onPressed();
            }
        }
    }
    
    public boolean isShow(){
        return keyboardView.isShown();
    }
    
    public void dismiss(){
        keyboardView.setVisibility(View.GONE);
    }

    private void inittvs()
    {
        for (int i = 0; i < tvs.length; i++)
        {
            tvs[i] = new TextView(context);
            LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            params.weight = 1;
            tvs[i].setLayoutParams(params);
            viewGroup.addView(tvs[i]);
            tvs[i].setGravity(Gravity.CENTER);
            tvs[i].setTextSize(30);
            tvs[i].setTextColor(Color.parseColor("#333333"));
            if (i < tvs.length - 1)
            {
                View view = new View(context);
                LinearLayout.LayoutParams viewParams = new LayoutParams((int) context.getResources().getDimension(R.dimen.width_input_tv_cutline), LayoutParams.MATCH_PARENT);
                viewParams.topMargin = 15;
                viewParams.bottomMargin = 15;
                view.setLayoutParams(viewParams);
                view.setBackgroundColor(Color.parseColor("#dbdbdb"));
                viewGroup.addView(view);
            }

        }
    }

    public void setCodeViewBgColor(int color)
    {
        int bgColor = context.getResources().getColor(color);

        if (viewGroup != null)
        {
            for (int i = 0; i < viewGroup.getChildCount(); i++)
            {
                viewGroup.getChildAt(i).setBackgroundColor(bgColor);
            }
        }
    }

    public void setCodeViewBgColor(String color)
    {
        int bgColor = Color.parseColor(color);

        if (viewGroup != null)
        {
            for (int i = 0; i < viewGroup.getChildCount(); i++)
            {
                viewGroup.getChildAt(i).setBackgroundColor(bgColor);
            }
        }
    }

    private OnKeyboardActionListener listener = new OnKeyboardActionListener()
    {
        @Override
        public void swipeUp()
        {
        }

        @Override
        public void swipeRight()
        {
        }

        @Override
        public void swipeLeft()
        {
        }

        @Override
        public void swipeDown()
        {
        }

        @Override
        public void onText(CharSequence text)
        {
        }

        @Override
        public void onRelease(int primaryCode)
        {
        }

        @Override
        public void onPress(int primaryCode)
        {
        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes)
        {
            if (primaryCode == Keyboard.KEYCODE_CANCEL)
            {// 完成
                hideKeyboard();
            }
            else if (primaryCode == Keyboard.KEYCODE_DELETE)
            {// 回退
                deleteTextView();
            }
            else if (primaryCode == Keyboard.KEYCODE_SHIFT)
            {// 大小写切换
                changeKey();
                keyboardView.setKeyboard(k1);

            }
            else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE)
            {// 数字键盘切换
                if (isnun)
                {
                    isnun = false;
                    keyboardView.setKeyboard(k1);
                }
                else
                {
                    isnun = true;
                    keyboardView.setKeyboard(k2);
                }
            }
            else if (primaryCode == 57419)
            { // go left
            }
            else if (primaryCode == 57421)
            { // go right
            }
            else if (primaryCode == 32)
            {
            }
            else
            {
                inputTextView(Character.toString((char) primaryCode));
            }
        }

    };

    /**
     * 键盘大小写切换
     */
    private void changeKey()
    {
        List<Key> keylist = k1.getKeys();
        if (isupper)
        {// 大写切换小写
            isupper = false;
            for (Key key : keylist)
            {
                if (key.label != null && isword(key.label.toString()))
                {
                    key.label = key.label.toString().toLowerCase();
                    key.codes[0] = key.codes[0] + 32;
                }
            }
        }
        else
        {// 小写切换大写
            isupper = true;
            for (Key key : keylist)
            {
                if (key.label != null && isword(key.label.toString()))
                {
                    key.label = key.label.toString().toUpperCase();
                    key.codes[0] = key.codes[0] - 32;
                }
            }
        }
    }

    public void hideKeyboard()
    {
        int visibility = keyboardView.getVisibility();
        if (visibility == View.VISIBLE)
        {
            keyboardView.setVisibility(View.INVISIBLE);
        }
    }

    public void showKeyboard()
    {
        int visibility = keyboardView.getVisibility();
        if (visibility == View.GONE || visibility == View.INVISIBLE)
        {
            keyboardView.setVisibility(View.VISIBLE);
        }
    }

    public void setTvsText(String str)
    {
        if (TextUtils.isEmpty(str))
            return;
        for (int i = 0; i < str.length(); i++)
        {
            tvs[i].setText("" + str.charAt(i));
        }
    }

    public void setTvxTextEmpty()
    {
        for (int i = 0; i < tvs.length; i++)
        {
            tvs[i].setText("");
        }
    }

    private boolean isword(String str)
    {
        String wordstr = "abcdefghijklmnopqrstuvwxyz";
        if (wordstr.indexOf(str.toLowerCase()) > -1)
        {
            return true;
        }
        return false;
    }

    private void inputTextView(String code)
    {
        for (int i = 0; i < tvs.length; i++)
        {
            TextView tv = tvs[i];
            if (tv.getText().toString().equals(""))
            {
                tv.setText(code);
                if (i == tvs.length - 1)
                {
                    inputListener.inputHasOver(getInputText());
                }
                return;
            }
        }
    }

    private void deleteTextView()
    {
        for (int i = tvs.length - 1; i >= 0; i--)
        {
            TextView tv = tvs[i];
            if (!tv.getText().toString().equals(""))
            {
                tv.setText("");
                viewGroup.invalidate();
                return;
            }
        }
    }

    public String getInputText()
    {
        StringBuffer sb = new StringBuffer();
        for (TextView tv : tvs)
        {
            sb.append(tv.getText().toString());
        }
        return sb.toString();
    }

    public interface InputListener
    {
        public void inputHasOver(String text);

        public void inputIng(String text);
    }

}
