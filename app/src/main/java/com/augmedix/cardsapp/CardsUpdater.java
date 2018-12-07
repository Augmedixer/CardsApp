package com.augmedix.cardsapp;

import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CardsUpdater extends AsyncTask<String,Void,String> {
    interface CardsUpdaterInterface {
        void onResult(String jsonCards);
        void onError(String error);
    }

    private CardsUpdaterInterface mCardsUpdaterInterface = null;
    private String mError = "";

    public CardsUpdater(CardsUpdaterInterface cardsUpdaterInterface) {
        mCardsUpdaterInterface = cardsUpdaterInterface;
    }

    @Override
    protected String doInBackground(String... args) {
        String jsonCards = "";
        mError = "";
        try {
            URL url = new URL(args[0]);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();

            InputStream is = c.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);

            int numCharsRead;
            char[] charArray = new char[1024];
            StringBuffer sb = new StringBuffer();
            while ((numCharsRead = isr.read(charArray)) > 0) {
                sb.append(charArray, 0, numCharsRead);
            }
            jsonCards = sb.toString();
        } catch (Exception e) {
            mError = e.getMessage();
            Log.e("CardsUpdate", "Update error! " + e.getMessage());
        }
        return jsonCards;
    }

    @Override
    protected void onPostExecute(String jsonCards) {
        if (mCardsUpdaterInterface != null) {
            if (mError.isEmpty() == false) {
                mCardsUpdaterInterface.onError(mError);
            } else {
                mCardsUpdaterInterface.onResult(jsonCards);
            }
        }
    }
}
