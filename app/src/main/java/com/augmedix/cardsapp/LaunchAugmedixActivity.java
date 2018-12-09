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

    private static final String JSON_VERSION = "version";
    private static final String JSON_CARDS = "cards";

    private static final String JSON_CARD_TYPE = "type";
    private static final String JSON_CARD_TITLE = "title";
    private static final String JSON_CARD_MESSAGE = "msg";
    private static final String JSON_CARD_TEMPORARY = "temporary"; //Boolean
    private static final String JSON_CARD_ACTION_TYPE = "action_type"; //TODO
    private static final String JSON_CARD_ACTION_MESSAGE = "action_msg";
    private static final String JSON_CARD_SWIPE_RIGHT = "right"; //TODO
    private static final String JSON_CARD_SWIPE_DOWN = "down";

    private static final String JSON_CARD_DISMISSED = "dismissed";

    //Card types
    private static final String CARD_TYPE_HOME = "HOME";
    private static final String CARD_TYPE_STREAMING = "STREAMING";
    private static final String CARD_TYPE_CUSTOM = "CUSTOM";

    //Custom Card Actions
    private static final String ACTION_TYPE_UPDATE_FORCED = "UPDATE_FORCED";
    private static final String ACTION_TYPE_UPDATE_OPTIONAL = "UPDATE_OPTIONAL";
    private static final String ACTION_TYPE_TAP_DISMISS = "TAP_DISMISS";
    private static final String ACTION_TYPE_AUTO_DISMISS = "AUTO_DISMISS";

    //Prefs
    private static final String PREF_PACKAGE_VERSION = "PACKAGE_VERSION";
    private static final String PREF_JSON_VERSION = "JSON_VERSION";
    private static final String PREF_JSON_CARDS = "JSON_CARDS";

    private String mCardsUpdaterURL = "http://www.droidsdoit.com/augmedix/cards.php";
    private CardsUpdater mCardsUpdater = null;
    private Utils mUtils = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUtils = Utils.getInstance(this);
        setContentView(R.layout.activity_main);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        mCardsUpdater = new CardsUpdater(this);

        String packageVersion = "";
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            packageVersion =  packageInfo.versionName;
        } catch (Exception e) {
            onError("onCreate exception: " + e.getMessage());
        }

        String previousPackageVersion = mUtils.getStringPref(PREF_PACKAGE_VERSION, "0.0");
        if (packageVersion.compareTo(packageVersion) < 0) {
            Log.i(TAG, "onCreate detected application upgrade - clearing preferences");
            mUtils.clearAllPrefs();
            mUtils.set(PREF_PACKAGE_VERSION, previousPackageVersion);
        }
//mUtils.clearAllPrefs();

        updateCardsJSON(packageVersion);
    }

    private void updateCardsJSON(String packageVersion) {
        try {
            mCardsUpdater.execute(mCardsUpdaterURL + "?version=" +  packageVersion);
        } catch (Exception e) {
            onError("updateCardsJSON exception: " + e.getMessage());
        }
    }

    private void initCards(boolean defaultJSON) {
        try {
            String cardsJSON = mUtils.getStringPref(PREF_JSON_CARDS, "");
            synchronized (mCardsJSON) {
                if (defaultJSON && cardsJSON.isEmpty()) {
                    mCardsJSON = new JSONArray();
                } else {
                    mCardsJSON = new JSONArray(mUtils.getStringPref(PREF_JSON_CARDS, ""));
                }

                if (mCardsJSON.length() == 0) {
                    mCardsJSON.put(new JSONObject().put(JSON_CARD_TYPE, CARD_TYPE_HOME));
                    mCardsJSON.put(new JSONObject().put(JSON_CARD_TYPE, CARD_TYPE_STREAMING));
                }
                logJSONArray(mCardsJSON);
            }

            mViewPager.setAdapter(new CustomPagerAdapter(this, mCardsJSON));
        } catch (Exception ex) {
            Log.e(TAG, "initCards exception: " + ex.getMessage());
        }
    }

    private void logJSONArray(JSONArray jsonArray) {
        Log.d(TAG, "JSONArray: " + jsonArray);
    }

    /* ---------------- CardsUpdaterInterface Methods Begin ----------------------*/
    // CardsUpdaterInterface Methods Begin...
    public void onError(String jsonError) {
        Log.e(TAG, "onError: " + jsonError);
        initCards(true);
    }

    public void onResult(String jsonCards) {
        Log.i(TAG, "onResult: " + jsonCards);
        try {
            if (jsonCards.isEmpty()) {
                mUtils.set(PREF_JSON_VERSION, "");
                mUtils.set(PREF_JSON_CARDS, "");
            } else {
                JSONObject jsonCardsObject = new JSONObject(jsonCards);
                String version = jsonCardsObject.getString(JSON_VERSION);
                JSONArray jsonCardsArray = jsonCardsObject.getJSONArray(JSON_CARDS);

                if (version.compareTo(mUtils.getStringPref(PREF_JSON_VERSION, "")) != 0) {
                    mUtils.set(PREF_JSON_VERSION, version);
                    mUtils.set(PREF_JSON_CARDS, jsonCardsArray.toString());
                }
            }

            initCards(false);
        } catch (Exception ex) {
            Log.e(TAG, "onResult exception: " + ex.getMessage());
            onError("onResult exception: " + ex.getMessage());
        }
    }
    /* ---------------- CardsUpdaterInterface Methods End ------------------------*/

    /* ---------------- CustomPagerAdapter Methods Begin -------------------------*/
    public class CustomPagerAdapter extends PagerAdapter {
        private Context mContext;
        private JSONArray mJSONArray = null;

        public CustomPagerAdapter(Context context, JSONArray jsonArray) {
            mContext = context;
            try {
                mJSONArray = new JSONArray(jsonArray.toString());
            } catch (Exception ex) {
                Log.e(TAG, "CustomPagerAdapter exception: " + ex.getMessage());
            }
        }

        @Override
        public Object instantiateItem(final ViewGroup collection, final int position) {
            try {
                final JSONObject customPagerJSON = mJSONArray.getJSONObject(position);
                LayoutInflater inflater = LayoutInflater.from(mContext);

                int layoutResId = R.layout.card_home;
                String type = customPagerJSON.getString(JSON_CARD_TYPE);
                if (type.contentEquals(CARD_TYPE_STREAMING))
                    layoutResId = R.layout.card_streaming;
                else if (type.contentEquals(CARD_TYPE_CUSTOM))
                    layoutResId = R.layout.card_custom;

                ViewGroup layout = (ViewGroup) inflater.inflate(layoutResId, collection, false);
                if (layoutResId == R.layout.card_custom) {
                    layout.findViewById(R.id.custom_container).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                if (customPagerJSON.has(JSON_CARD_TEMPORARY)) {
/*                                    synchronized (mCardsJSON) {
                                        mCardsJSON.remove(position);
                                        mUtils.set(PREF_JSON_CARDS, mCardsJSON.toString());
                                    }
*/
                                    remove(position);
                                } else if (customPagerJSON.has(JSON_CARD_ACTION_TYPE)) {
                                    String actionType = customPagerJSON.getString(JSON_CARD_ACTION_TYPE);
                                    switch (actionType) {
                                        case ACTION_TYPE_UPDATE_FORCED:
                                        case ACTION_TYPE_UPDATE_OPTIONAL:
                                            upgradeApp();
                                            break;
                                    }
                                }
                            } catch (Exception ex) {
                                Log.e(TAG, "instantiateItem onClick exception: " + ex.getMessage());
                            }
                        }
                    });
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
            return mJSONArray.length();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            try {
                return mJSONArray.getJSONObject(position).getString(JSON_CARD_TITLE);
            } catch (Exception ex) {
                Log.e(TAG, "getPageTitle exception: " + ex.getMessage());
            }
            return "" + position;
        }

        private void remove(int position) {
            try {
                mJSONArray.remove(position);
                CustomPagerAdapter adapter = (CustomPagerAdapter) mViewPager.getAdapter();
                mViewPager.setAdapter(null);
                mViewPager.setAdapter(adapter);
                mViewPager.setCurrentItem(position < mCardsJSON.length() ? position : 0);
            } catch (Exception ex) {
                Log.e(TAG, "remove exception: " + ex.getMessage());
            }
        }
    }
    /* ---------------- CustomPagerAdapter Methods End ---------------------------*/

    private void upgradeApp() {
        //TODO
    }
}
