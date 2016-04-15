package com.curious.donkey.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.curious.donkey.R;
import com.curious.donkey.data.MetaConst;

import java.util.ArrayList;

public class ListShowCaseActivity extends AppCompatActivity {

    private ListView mListView;
    private ArrayList<ActivityInfo> mActivityInfoArray;
    private ArrayList<String> mItemData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListView = new ListView(this);
        setContentView(mListView);

        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        PackageManager pm = getPackageManager();
        try {
            String packageName = getApplicationInfo().packageName;
            PackageInfo packageInfo = pm.getPackageInfo(packageName,
                    PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA);

            ActivityInfo[] tempActivityInfoArray = packageInfo.activities;
            if (tempActivityInfoArray == null) {
                finish();
                return;
            }

            int size = tempActivityInfoArray.length - 1;
            int index = 0;
            mItemData = new ArrayList<>();
            mActivityInfoArray = new ArrayList<ActivityInfo>();
            for (int i = 0; i < tempActivityInfoArray.length; i++) {
                if (tempActivityInfoArray[i].name.contains(getLocalClassName())) {
                    continue;
                }

                Bundle meta = tempActivityInfoArray[i].metaData;
                if (meta != null && meta.getBoolean(MetaConst.ACTIVITY_SHOULD_BE_HIDDEN, false)) {
                    continue;
                }

                mActivityInfoArray.add(tempActivityInfoArray[i]);
                int resId = tempActivityInfoArray[i].labelRes;

                if (resId == 0) {
                    mItemData.add(tempActivityInfoArray[i].name);
                    index++;
                    continue;
                }

                mItemData.add(getResources().getString(resId));
                index++;
            }

            mListView.setAdapter(new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, mItemData));
            mListView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    ActivityInfo activityInfo = mActivityInfoArray.get(position);
                    Intent intent = new Intent();
                    intent.setClassName(activityInfo.packageName,
                            activityInfo.name);

                    startActivity(intent);
                }
            });

        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

    }
}
