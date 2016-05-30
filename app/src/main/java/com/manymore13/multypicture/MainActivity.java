package com.manymore13.multypicture;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.administrator.multypicture.R;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends ActionBarActivity {

    private ListView mListView;

    private final ArrayList<MultyImgBean> mMultyImgBean = new ArrayList<MultyImgBean>();

    private MultyAdapter mMultyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadData();
        mListView = (ListView) findViewById(R.id.lv_list);
        mMultyAdapter = new MultyAdapter(MainActivity.this, mMultyImgBean);
        mListView.setAdapter(mMultyAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void loadData() {
        mMultyImgBean.clear();
        String[] resArray = getResources().getStringArray(R.array.img_url_array);
        Random random = new Random();
        final int len = 50;
        for (int i = 0; i < len; i++) {
            int maxSize = random.nextInt(8);
            String[] urlArray = new String[maxSize];
            for (int j = 0; j < maxSize; j++) {
                urlArray[j] = resArray[j];
            }
            MultyImgBean multyImgBean = new MultyImgBean();
            multyImgBean.setUrl(urlArray);
            mMultyImgBean.add(multyImgBean);
        }
    }


}
