package com.augmedix.cardsapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CardManager {
    private static final String TAG = CardManager.class.getSimpleName();

    private static final String JSON_VERSION = "version";
    private static final String JSON_CARDS = "cards";

    private static CardManager sCardManagerInstance = null;

    private List<Card> mCards = new ArrayList<Card>();
    private String mVersion = "";

    public static CardManager getInstance() {
        if (sCardManagerInstance == null) sCardManagerInstance = new CardManager();

        return sCardManagerInstance;
    }

    private CardManager() {

    }

    public boolean load(JSONObject jsonCardsInfo) {
        try {
            clear();
            mVersion = jsonCardsInfo.has(JSON_VERSION) ? (String) jsonCardsInfo.get(JSON_VERSION) : "";

            JSONArray jsonCardsArray = jsonCardsInfo.has(JSON_CARDS) ? jsonCardsInfo.getJSONArray(JSON_CARDS) : null;
            logJSONArray(jsonCardsArray);

            if (jsonCardsArray != null) {
                for(int c=0; c<jsonCardsArray.length(); c++) {
                    Card card = new Card((JSONObject) jsonCardsArray.get(c));
                    mCards.add(card);
                }
            }
            return true;
        } catch (Exception ex) {
            Log.e(TAG, "load exception: " + ex.getMessage());
        }
        return false;
    }

    public boolean load(String jsonCardsInfo) {
        try {
            return load(new JSONObject(jsonCardsInfo));
        } catch (Exception ex) {
            Log.e(TAG, "load from string exception: " + ex.getMessage());
        }
        return false;
    }

    private void logJSONArray(JSONArray jsonArray) {
        Log.d(TAG, "JSONArray: " + jsonArray);
    }

    public void clear() {
        mVersion = "";
        mCards.clear();
    }

    public void add(Card card) {
        mCards.add(card);
    }

    public String getVersion() {
        return mVersion;
    }

    public void save() {
        try {
        } catch (Exception ex) {
            Log.e(TAG, "save exception: " + ex.getMessage());
        }
    }

    public List<Card> getCards() {
        return mCards;
    }

    public Card getCard(int index) {
        return (index < mCards.size()) ? mCards.get(index) : null;
    }
}
