package com.manymore13.multypicture;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.example.administrator.multypicture.R;

import java.util.List;

/**
 * @author manymore13
 * @version V1.0
 * @Description MultyAdapter
 * @date 2016/3/22
 */
public class MultyAdapter extends BaseAdapter {

    private List<MultyImgBean> mMultyImgBeanList;

    private Context mContext;

    public MultyAdapter(Context context, List<MultyImgBean> beanList) {
        mMultyImgBeanList = beanList;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mMultyImgBeanList == null ? 0 : mMultyImgBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_layout, null);
            MultyClickCallback multyClickCallback = new MultyClickCallback();
            vh.multyPicView = (MultyPicView) convertView.findViewById(R.id.multy_pic_view);
            vh.multyPicView.setMaxChildCount(MultyPicView.MAX_IMG_COUNT);
            vh.callback = multyClickCallback;
            convertView.setTag(vh);

        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        MultyImgBean multyImgBean = mMultyImgBeanList.get(position);
        if (multyImgBean.getUrl().length == 1) {
            vh.multyPicView.setSingleImg(multyImgBean.getUrl()[0], 600, 600);
        } else {
            vh.multyPicView.setImgs(multyImgBean.getUrl());
        }
        vh.multyPicView.setClickCallback(vh.callback);
        return convertView;
    }

    private final class ViewHolder {
        private MultyPicView multyPicView;
        private MultyPicView.ClickCallback callback;
    }

    class MultyClickCallback implements MultyPicView.ClickCallback {
        @Override
        public void callback(int index, String[] str) {
            Toast.makeText(mContext, "索引是" + index, Toast.LENGTH_SHORT).show();
        }
    }
}
