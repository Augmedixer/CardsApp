package com.augmedix.cardsapp;

import android.content.pm.PackageInfo;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class LaunchAugmedixActivity extends AppCompatActivity implements CardsUpdater.CardsUpdaterInterface, CustomPagerAdapter.CardsActionInterface {
    private static final String TAG = LaunchAugmedixActivity.class.getSimpleName();
    private ViewPager mViewPager = null;

    //Prefs
    private static final String PREF_PACKAGE_VERSION = "PACKAGE_VERSION";
    private static final String PREF_JSON_VERSION = "JSON_VERSION";
    private static final String PREF_JSON_CARDS = "JSON_CARDS";

    private String mCardsUpdaterURL = "http://www.droidsdoit.com/augmedix/cards.php";
    private CardsUpdater mCardsUpdater = null;
    private Utils mUtils = null;
    private UpdateApp mUpdateApp = null;
    private CardManager mCardManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUtils = Utils.getInstance(this);
        mCardManager = CardManager.getInstance();

        setContentView(R.layout.activity_main);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                onPageChanged(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        mCardsUpdater = new CardsUpdater(this);

        String packageVersion = "";
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            packageVersion =  packageInfo.versionName;
        } catch (Exception e) {
            onError("onCreate exception: " + e.getMessage());
        }

        String previousPackageVersion = mUtils.getStringPref(PREF_PACKAGE_VERSION, "0.0");
        if (packageVersion.compareTo(previousPackageVersion) != 0) {
            Log.i(TAG, "onCreate detected application upgrade - clearing preferences");
            mUtils.clearAllPrefs();
            mUtils.set(PREF_PACKAGE_VERSION, packageVersion);
        }

        TextView tvAppVersion = findViewById(R.id.app_version);
        if (tvAppVersion != null) tvAppVersion.setText("Version " + packageVersion);

        updateCards(packageVersion);
    }

    private void updateCards(String packageVersion) {
        try {
            mCardsUpdater.execute(mCardsUpdaterURL + "?version=" +  packageVersion);
        } catch (Exception e) {
            onError("updateCards exception: " + e.getMessage());
        }
    }

    private void initCards() {
        try {
            String cardsJSON = mUtils.getStringPref(PREF_JSON_CARDS, "");
            synchronized (mCardManager) {
                boolean loadSucceeded = false;
                if (cardsJSON.isEmpty() == false) {
                    loadSucceeded = mCardManager.load(cardsJSON);
                }

                if (loadSucceeded == false) {
                    mCardManager.add(new Card(Card.CARD_TYPE_HOME, "", ""));
                    mCardManager.add(new Card(Card.CARD_TYPE_STREAMING, "", ""));
                }
            }

            mViewPager.setAdapter(new CustomPagerAdapter(this, mCardManager.getCards(), this));
            onPageChanged(0);
        } catch (Exception ex) {
            Log.e(TAG, "initCards exception: " + ex.getMessage());
        }
    }

    /* ---------------- CardsUpdaterInterface Methods Begin ----------------------*/
    // CardsUpdaterInterface Methods Begin...
    public void onError(String jsonError) {
        Log.e(TAG, "onError: " + jsonError);
        initCards();
    }

    public void onResult(String jsonCards) {
        Log.i(TAG, "onResult: " + jsonCards);
        try {
            mCardManager.clear();
            if (jsonCards.isEmpty()) {
                mUtils.set(PREF_JSON_VERSION, "");
                mUtils.set(PREF_JSON_CARDS, "");
            } else {
                mCardManager.load(jsonCards);
                String version = mCardManager.getVersion();

                if (version.compareTo(mUtils.getStringPref(PREF_JSON_VERSION, "")) != 0) {
                    mUtils.set(PREF_JSON_VERSION, version);
                    mUtils.set(PREF_JSON_CARDS, jsonCards);
                }
            }

            initCards();
        } catch (Exception ex) {
            Log.e(TAG, "onResult exception: " + ex.getMessage());
            onError("onResult exception: " + ex.getMessage());
        }
    }
    /* ---------------- CardsUpdaterInterface Methods End ------------------------*/

    private void upgradeApp() {
        mUpdateApp = new UpdateApp();
        mUpdateApp.setContext(this);
        mUpdateApp.execute("http://www.droidsdoit.com/augmedix/" + UpdateApp.APP_NAME);
    }

    private void remove(int position) {
        try {
            CustomPagerAdapter adapter = (CustomPagerAdapter) mViewPager.getAdapter();
            adapter.remove(position);

            mViewPager.setAdapter(null);
            mViewPager.setAdapter(adapter);
            mViewPager.setCurrentItem(position < adapter.getCount() ? position : 0);
        } catch (Exception ex) {
            Log.e(TAG, "remove exception: " + ex.getMessage());
        }
    }

    // Begin CardsActionInterface methods...
    public void onClick(Card card, int position) {
        try {
            if (card.mTemporary) {
                remove(position);
            } else if (card.mActionType.isEmpty() == false) {
                CustomPagerAdapter adapter = (CustomPagerAdapter) mViewPager.getAdapter();
                switch (card.mActionType) {
                    case Card.ACTION_TYPE_UPDATE_FORCED:
                    case Card.ACTION_TYPE_UPDATE_OPTIONAL:
                        upgradeApp();
                        break;
                    case Card.ACTION_TYPE_AUTO_DISMISS:
                        adapter.stopDismissCountdown();
                        remove(position);
                        break;
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, "onClick exception: " + ex.getMessage());
        }
    }

    public void onAutoDismiss() {
        remove(mViewPager.getCurrentItem());
    }
    // End CardsActionInterface methods.

    public void onPageChanged(int position) {
        try {
            CustomPagerAdapter adapter = (CustomPagerAdapter) mViewPager.getAdapter();
            Card card = adapter.getCard(position);
            if (card == null) return;

            if (card.mActionType.compareTo(Card.ACTION_TYPE_AUTO_DISMISS) == 0) {
                adapter.startDismissCountdown(mViewPager.getChildAt(position), card);
            } else {
                adapter.stopDismissCountdown();
            }
        } catch (Exception ex) {
            Log.e(TAG, "onPageChanged exception: " + ex.getMessage());
        }
    }
}
