package com.augmedix.cardsapp;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

public class LaunchAugmedixActivity extends AppCompatActivity implements CardsUpdater.CardsUpdaterInterface {
    private static final String TAG = LaunchAugmedixActivity.class.getSimpleName();
    private ViewPager mViewPager = null;
    private JSONArray mCardsJSON = new JSONArray();


    private static final String JSON_CARD_TYPE = "type";
    private static final String JSON_CARD_TITLE = "title";
    private static final String JSON_CARD_MESSAGE = "msg";
    private static final String JSON_CARD_ACTION_TYPE = "action_type"; //TODO
    private static final String JSON_CARD_ACTION_MESSAGE = "action_msg";
    private static final String JSON_CARD_SWIPE_RIGHT = "right"; //TODO
    private static final String JSON_CARD_SWIPE_DOWN = "down";

    private static final String CARD_TYPE_HOME = "HOME";
    private static final String CARD_TYPE_STREAMING = "STREAMING";
    private static final String CARD_TYPE_CUSTOM = "CUSTOM";

    private String mCardsUpdaterURL = "http://www.droidsdoit.com/augmedix/cards.php";
    private CardsUpdater mCardsUpdater = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        mCardsUpdater = new CardsUpdater(this);
        updateCardsJSON();
    }

    private void updateCardsJSON() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            mCardsUpdater.execute(mCardsUpdaterURL + "?version=" +  packageInfo.versionName);
        } catch (Exception e) {
            onError("updateCardsJSON exception: " + e.getMessage());
        }
    }

    private void initCards(boolean defaultJSON) {
        try {
            if (defaultJSON) {
                mCardsJSON.put(new JSONObject().put(JSON_CARD_TYPE, CARD_TYPE_HOME));
                mCardsJSON.put(new JSONObject().put(JSON_CARD_TYPE, CARD_TYPE_STREAMING));
                /*mCardsJSON.put(new JSONObject()
                        .put(JSON_CARD_TYPE, CARD_TYPE_CUSTOM)
                        .put(JSON_CARD_TITLE, "Custom 1")
                        .put(JSON_CARD_MESSAGE, "Custom Message...")
                        .put(JSON_CARD_ACTION_MESSAGE, "Tap to ...")
                ); */
            }
            logJSONArray(mCardsJSON);

            mViewPager.setAdapter(new CustomPagerAdapter(this));
        } catch (Exception ex) {
            Log.e(TAG, "initCards exception: " + ex.getMessage());
        }
    }

    private void logJSONArray(JSONArray jsonArray) {
        Log.d(TAG, "JSONArray: " + jsonArray);
    }

    public void onError(String jsonError) {
        Log.e(TAG, "onError: " + jsonError);
        initCards(true);
    }

    public void onResult(String jsonCards) {
        Log.i(TAG, "onResult: " + jsonCards);
        try {
            mCardsJSON = new JSONArray(jsonCards);
            initCards(false);
        } catch (Exception ex) {
            Log.e(TAG, "onResult exception: " + ex.getMessage());
            onError("onResult exception: " + ex.getMessage());
        }
    }

    /* ----------------CustomPagerAdapter ----------------------*/
    public class CustomPagerAdapter extends PagerAdapter {
        private Context mContext;

        public CustomPagerAdapter(Context context) {
            mContext = context;
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            try {
                JSONObject customPagerJSON = mCardsJSON.getJSONObject(position);
                LayoutInflater inflater = LayoutInflater.from(mContext);

                int layoutResId = R.layout.card_home;
                String type = customPagerJSON.getString(JSON_CARD_TYPE);
                if (type.contentEquals(CARD_TYPE_STREAMING))
                    layoutResId = R.layout.card_streaming;
                else if (type.contentEquals(CARD_TYPE_CUSTOM))
                    layoutResId = R.layout.card_custom;

                ViewGroup layout = (ViewGroup) inflater.inflate(layoutResId, collection, false);
                if (layoutResId == R.layout.card_custom) {
                    ((TextView) layout.findViewById(R.id.custom_title)).setText(customPagerJSON.getString(JSON_CARD_TITLE));
                    ((TextView) layout.findViewById(R.id.custom_message)).setText(customPagerJSON.getString(JSON_CARD_MESSAGE));
                    ((TextView) layout.findViewById(R.id.custom_actiontext)).setText(customPagerJSON.getString(JSON_CARD_ACTION_MESSAGE));
                }
                collection.addView(layout);
                return layout;
            } catch (Exception ex) {
                Log.e(TAG, "instantiateItem exception: " + ex.getMessage());
            }
            return null;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return mCardsJSON.length();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            try {
                return mCardsJSON.getJSONObject(position).getString(JSON_CARD_TITLE);
            } catch (Exception ex) {
                Log.e(TAG, "getPageTitle exception: " + ex.getMessage());
            }
            return "" + position;
        }
    }
}
