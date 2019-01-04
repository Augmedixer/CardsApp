package com.augmedix.cardsapp;

import android.util.Log;

import org.json.JSONObject;

public class Card {
    private static final String TAG = Card.class.getSimpleName();

    //Card types
    public static final String CARD_TYPE_HOME = "HOME";
    public static final String CARD_TYPE_STREAMING = "STREAMING";
    public static final String CARD_TYPE_CUSTOM = "CUSTOM";

    //Card Info
    private static final String JSON_CARD_TYPE = "type";
    private static final String JSON_CARD_TITLE = "title";
    private static final String JSON_CARD_MESSAGE = "msg";
    private static final String JSON_CARD_TEMPORARY = "temporary"; //Boolean
    private static final String JSON_CARD_ACTION_TYPE = "action"; //TODO
    private static final String JSON_CARD_ACTION_MESSAGE = "action_msg";
    private static final String JSON_CARD_SWIPE_RIGHT = "right"; //TODO
    private static final String JSON_CARD_SWIPE_DOWN = "down";

    //Card states
    private static final String JSON_CARD_DISMISSED = "dismissed";

    //Custom Card Actions
    public static final String ACTION_TYPE_UPDATE_FORCED = "UPDATE_FORCED";
    public static final String ACTION_TYPE_UPDATE_OPTIONAL = "UPDATE_OPTIONAL";
    public static final String ACTION_TYPE_TAP_DISMISS = "TAP_DISMISS";
    public static final String ACTION_TYPE_AUTO_DISMISS = "AUTO_DISMISS";

    public int mIndex = 0; //TODO

    public String mType = "";
    public String mTitle = "";
    public String mMessage = "";
    public boolean mTemporary = false;
    public String mActionType = "";
    public String mActionMessage = "";

    private boolean mDismissed = false;

    Card(String type, String title, String message) {
        mType = type;
        mTitle = title;
        mMessage = message;
    }

    Card(JSONObject jsonCard) {
        try {
            mType = jsonCard.has(JSON_CARD_TYPE) ? (String) jsonCard.get(JSON_CARD_TYPE) : "";
            mTitle = jsonCard.has(JSON_CARD_TITLE) ? (String) jsonCard.get(JSON_CARD_TITLE) : "";
            mMessage = jsonCard.has(JSON_CARD_MESSAGE) ? (String) jsonCard.get(JSON_CARD_MESSAGE) : "";

            mTemporary = jsonCard.has(JSON_CARD_TEMPORARY) ? (Boolean) jsonCard.getBoolean(JSON_CARD_TEMPORARY) : false;

            mActionType = jsonCard.has(JSON_CARD_ACTION_TYPE) ? (String) jsonCard.get(JSON_CARD_ACTION_TYPE) : "";
            mActionMessage = jsonCard.has(JSON_CARD_ACTION_MESSAGE) ? (String) jsonCard.get(JSON_CARD_ACTION_MESSAGE) : "";

            mDismissed = jsonCard.has(JSON_CARD_DISMISSED) ? (Boolean) jsonCard.getBoolean(JSON_CARD_DISMISSED) : false;
        } catch (Exception ex) {
            Log.e(TAG, "Card JSONObject constructor exception: " + ex.getMessage());
        }
    }

    JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(JSON_CARD_TYPE, mType);
            if (mTitle.isEmpty() == false) jsonObject.put(JSON_CARD_TITLE, mTitle);
            if (mMessage.isEmpty() == false) jsonObject.put(JSON_CARD_MESSAGE, mMessage);
            if (mTemporary) jsonObject.put(JSON_CARD_TEMPORARY, mTemporary);

            if (mActionType.isEmpty() == false) jsonObject.put(JSON_CARD_ACTION_TYPE, mActionType);
            if (mActionMessage.isEmpty() == false) jsonObject.put(JSON_CARD_ACTION_MESSAGE, mActionMessage);

            if (mDismissed) jsonObject.put(JSON_CARD_DISMISSED, mDismissed);

            jsonObject.put(JSON_CARD_TEMPORARY, mTemporary);
        } catch (Exception ex) {
            Log.e(TAG, "toJSON exception: " + ex.getMessage());
        }
        return jsonObject;
    }
}

