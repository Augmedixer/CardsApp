package com.augmedix.cardsapp;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CustomPagerAdapter  extends PagerAdapter {
    private static final String TAG = CustomPagerAdapter.class.getSimpleName();
    private final int MAX_COUNT_DOWN = 5;

    private Context mContext;
    private List<Card> mCards = null;
    private CardsActionInterface mCardsActionInterface = null;

    private int mTimeRemaining = MAX_COUNT_DOWN;
    private View mCurrentView = null;
    private Card mAutoDismissCard = null;


    public interface CardsActionInterface {
        public abstract void onClick(Card card, int position);

        public abstract void onAutoDismiss();
    }

    public CustomPagerAdapter(Context context, List<Card> cards, CardsActionInterface cardsActionInterface) {
        mContext = context;
        try {
            mCards = new ArrayList<Card>(cards);
            mCardsActionInterface = cardsActionInterface;
        } catch (Exception ex) {
            Log.e(TAG, "CustomPagerAdapter exception: " + ex.getMessage());
        }
    }

    @Override
    public Object instantiateItem(final ViewGroup collection, final int position) {
        try {
            final Card card = mCards.get(position);
            LayoutInflater inflater = LayoutInflater.from(mContext);

            int layoutResId = R.layout.card_home;
            String type = card.mType;
            if (type.contentEquals(Card.CARD_TYPE_STREAMING))
                layoutResId = R.layout.card_streaming;
            else if (type.contentEquals(Card.CARD_TYPE_CUSTOM))
                layoutResId = R.layout.card_custom;

            ViewGroup layout = (ViewGroup) inflater.inflate(layoutResId, collection, false);
            if (layoutResId == R.layout.card_custom) {
                layout.findViewById(R.id.custom_container).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (mCardsActionInterface != null) mCardsActionInterface.onClick(card, position);
                        } catch (Exception ex) {
                            Log.e(TAG, "instantiateItem onClick exception: " + ex.getMessage());
                        }
                    }
                });
                ((TextView) layout.findViewById(R.id.custom_title)).setText(card.mTitle);
                ((TextView) layout.findViewById(R.id.custom_message)).setText(card.mMessage);
                ((TextView) layout.findViewById(R.id.custom_actiontext)).setText(card.mActionMessage);
                updateCountdown(layout, R.id.custom_actiontext, card, MAX_COUNT_DOWN);
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
        return mCards.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        try {
            return mCards.get(position).mTitle;
        } catch (Exception ex) {
            Log.e(TAG, "getPageTitle exception: " + ex.getMessage());
        }
        return "" + position;
    }

    public void remove(int position) {
        try {
            mCards.remove(position);
        } catch (Exception ex) {
            Log.e(TAG, "remove exception: " + ex.getMessage());
        }
    }

    public Card getCard(int position) {
        return position < mCards.size() ? mCards.get(position) : null;
    }

    public void startDismissCountdown(View pageView, Card card) {
        try {
            mTimeRemaining = 5;
            mCurrentView = pageView;
            mAutoDismissCard = card;
            updateCountdown((ViewGroup)pageView, R.id.custom_actiontext, mAutoDismissCard, mTimeRemaining);
            pageView.postDelayed(mUpdateRunnage, 1000);
        } catch (Exception ex) {
            Log.e(TAG, "remove exception: " + ex.getMessage());
        }
    }

    public void stopDismissCountdown() {
        mCurrentView = null;
    }

    private Runnable mUpdateRunnage = new Runnable() {
        @Override
        public void run() {
            Log.i(TAG, "mUpdateRunnage");
            try {
                if (mCurrentView != null) {
                    updateCountdown((ViewGroup) mCurrentView, R.id.custom_actiontext, mAutoDismissCard, --mTimeRemaining);
                    if (mTimeRemaining > 0) {
                        mCurrentView.postDelayed(mUpdateRunnage, 1000);
                    } else {
                        mCardsActionInterface.onAutoDismiss();;
                    }
                }
            } catch (Exception ex) {
                Log.e(TAG, "mUpdateRunnage exception: " + ex.getMessage());
            }
        }
    };

    private void updateCountdown(ViewGroup viewGroup, @IdRes int resId, Card card, int countdown) {
        try {
            if (viewGroup != null && card.mActionMessage.contains("__COUNTDOWN__")) {
                TextView actionMessage = ((ViewGroup) viewGroup).findViewById(resId);
                actionMessage.setText(card.mActionMessage.replace("__COUNTDOWN__", "" + mTimeRemaining));
            }
        } catch (Exception ex) {
            Log.e(TAG, "updateCountdown exception: " + ex.getMessage());
        }
    }
}
