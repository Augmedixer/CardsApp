package com.augmedix.cardsapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class UpdateApp extends AsyncTask<String,String,Void> {
    private static String TAG = UpdateApp.class.getSimpleName();

    private Context context;
    public final static String APP_NAME = "app-debug.apk";
    private static long RESTART_APP_DELAY = 15000L;

    public void setContext(Context contextf){
        context = contextf;
    }

    @Override
    protected Void doInBackground(String... args) {
        try {
            URL url = new URL(args[0]);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();

            File file = context.getExternalFilesDir("Download");
            file.mkdirs();
            File outputFile = new File(file, APP_NAME);
            if(outputFile.exists()){
                outputFile.delete();
            }
            FileOutputStream fos = new FileOutputStream(outputFile);

            InputStream is = c.getInputStream();

            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len1);
            }
            fos.close();
            is.close();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri apkUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", outputFile);
                Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                intent.setData(apkUri);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(intent);
            } else {
                Uri apkUri = Uri.fromFile(outputFile);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }

            //restartApp();
        } catch (Exception e) {
            Log.e("UpdateAPP", "Update error! " + e.getMessage());
        }
        return null;
    }

    private void restartApp() {
        try {
            Calendar calendar = Calendar.getInstance();
            Intent intent = new Intent(context, LaunchAugmedixActivity.class);
            intent.putExtra("upgraded", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

            ((AlarmManager) context.getSystemService(ALARM_SERVICE)).set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + RESTART_APP_DELAY, pendingIntent);
            Log.i(TAG,"restartApp scheduled app to relaunch in " + RESTART_APP_DELAY + "ms");
        } catch (Exception e) {
            Log.e("restartApp", "Update error! " + e.getMessage());
        }
    }
}
