package com.augmedix.cardsapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class UpgradeReceiver extends BroadcastReceiver {
    private static final String TAG = UpgradeReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Upgrade completed. Restarting activity");
        context.startActivity(new Intent(context, LaunchAugmedixActivity.class));
    }
}
