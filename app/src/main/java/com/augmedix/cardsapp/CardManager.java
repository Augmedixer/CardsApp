package com.augmedix.cardsapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CardManager {
    private static final String TAG = CardManager.class.getSimpleName();

    public static final String JSON_VERSION = "version";
    public static final String JSON_CARDS = "cards";

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
                    if (card.mTemporary == false || card.isDismissed() == false)
                        mCards.add(card);
                }
            }

            if (containsAction(Card.ACTION_TYPE_UPDATE_FORCED) == false) {
                if (containsType(Card.CARD_TYPE_HOME) == false)
                    mCards.add(new Card(Card.CARD_TYPE_HOME));
                if (containsType(Card.CARD_TYPE_STREAMING) == false)
                    mCards.add(new Card(Card.CARD_TYPE_STREAMING));
            }
            return true;
        } catch (Exception ex) {
            Log.e(TAG, "load exception: " + ex.getMessage());
        }
        return false;
    }

    private boolean containsType(String cardType) {
        for(int c=0; c<mCards.size(); c++) {
            if (mCards.get(c).mType.equals(cardType))
                return true;
        }
        return false;
    }

    private boolean containsAction(String cardAction) {
        for(int c=0; c<mCards.size(); c++) {
            if (mCards.get(c).mActionType.equals(cardAction))
                return true;
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
        Card.cleanIndex();
    }

    public void add(Card card) {
        mCards.add(card);
    }

    public String getVersion() {
        return mVersion;
    }

    public JSONObject toJSON() {
        if (mVersion.isEmpty()) return null;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(JSON_VERSION, mVersion);

            JSONArray jsonArray = new JSONArray();
            for(int c=0; c<mCards.size(); c++) {
                jsonArray.put(mCards.get(c).toJSON());
            }
            jsonObject.put(JSON_CARDS, jsonArray);
            return jsonObject;
        } catch (Exception ex) {
            Log.e(TAG, "save exception: " + ex.getMessage());
        }
        return null;
    }

    public List<Card> getCards() {
        return mCards;
    }

    public Card getCard(int index) {
        return (index < mCards.size()) ? mCards.get(index) : null;
    }

    public void setDismissed(Card cardToFind) {
        for(int c=0; c<mCards.size(); c++) {
            Card card = mCards.get(c);
            if (card.mIndex == cardToFind.mIndex) {
                card.setDismissed();
                break;
            }
        }
    }
}
