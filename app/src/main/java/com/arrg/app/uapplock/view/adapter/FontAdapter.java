package com.arrg.app.uapplock.view.adapter;

import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;

import com.arrg.app.uapplock.R;
import com.arrg.app.uapplock.model.entity.Font;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class FontAdapter extends BaseQuickAdapter<Font>{

    public FontAdapter(int layoutResId, List<Font> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, Font font) {
        AppCompatTextView textView = baseViewHolder.getView(R.id.tvFontName);

        textView.setText(font.getName());
        textView.setTypeface(Typeface.createFromAsset(mContext.getAssets(), font.getPath()));
    }
}
