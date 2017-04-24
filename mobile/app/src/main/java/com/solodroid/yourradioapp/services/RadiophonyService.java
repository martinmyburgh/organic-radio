package com.solodroid.yourradioapp.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecSelector;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.ext.okhttp.OkHttpDataSource;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;
import com.google.android.exoplayer.util.Util;
import com.solodroid.yourradioapp.R;
import com.solodroid.yourradioapp.activities.ActivityFavorite;
import com.solodroid.yourradioapp.activities.ActivityRadioByCategory;
import com.solodroid.yourradioapp.activities.MainActivity;
import com.solodroid.yourradioapp.models.ItemListRadio;

import okhttp3.CacheControl;
import okhttp3.OkHttpClient;

public class RadiophonyService extends Service {

    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 256;
    private static MediaCodecAudioTrackRenderer audioRenderer;
    private static final int NOTIFICATION_ID = 1;
    private static RadiophonyService service;
    private static Context context;
    private static ExoPlayer exoPlayer;
    private static ItemListRadio station;
    static public int list;
    private WifiLock wifiLock;
    static ProgressDialog dialog;
    static ProgressTask task;
    private static int inwhich;

    static public void initialize(Context context, ItemListRadio station, int inwhich) {
        RadiophonyService.context = context;
        RadiophonyService.station = station;
        RadiophonyService.inwhich = inwhich;
        Log.e("inwhich", "" + inwhich);
    }

    static public RadiophonyService getInstance() {
        if (service == null) {
            service = new RadiophonyService();
        }
        return service;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        exoPlayer = ExoPlayer.Factory.newInstance(1);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        task = new ProgressTask();
        task.execute();
        return START_NOT_STICKY;
    }

    public void onDestroy() {
        stop();
        Log.e("Destroyed", "Called");
    }

    public void onStop() {
        if (dialog != null && dialog.isShowing()) {
            task.cancel(true);
        }
    }

    public void stop() {
        if (exoPlayer != null && exoPlayer.getPlayWhenReady()) {
            exoPlayer.stop();
            exoPlayer.seekTo(0);
            exoPlayer.release();
            exoPlayer = null;
            wifiLock.release();
            stopForeground(true);
        }
    }

    public static void StopExoPlayer() {
        exoPlayer.setPlayWhenReady(false);
    }

    public static void PlayExoPlayer() {
        exoPlayer.setPlayWhenReady(true);
    }

    public boolean isPlaying() {
        return (exoPlayer != null && exoPlayer.getPlayWhenReady());
    }

    private class ProgressTask extends AsyncTask<String, Void, Boolean> {

        public ProgressTask() {
            dialog = new ProgressDialog(context);
        }

        protected void onPreExecute() {
            dialog.setMessage(context.getString(R.string.radio_connection) + " " + station.getRadioName() + "...");
            dialog.setCancelable(false);
            dialog.show();
            dialog.setOnCancelListener(new OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    ProgressTask.this.cancel(true);
                    RadiophonyService.this.stopSelf();
                }
            });
        }

        protected Boolean doInBackground(final String... args) {

            try {
                Uri uri;
                if (station.getRadiourl().endsWith(".m3u")) {
                    uri = Uri.parse(Parser.parse(station.getRadiourl()));
                } else {
                    uri = Uri.parse(station.getRadiourl());
                }

                Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
                String userAgent = Util.getUserAgent(context, "ExoPlayerDemo");
                OkHttpClient okHttpClient = new OkHttpClient();

                DataSource dataSource = new DefaultUriDataSource(context, null,
                        new OkHttpDataSource(okHttpClient, userAgent, null, null, CacheControl.FORCE_NETWORK));

                ExtractorSampleSource sampleSource = new ExtractorSampleSource(uri, dataSource, allocator,
                        BUFFER_SEGMENT_COUNT * BUFFER_SEGMENT_SIZE);

                MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource,
                        MediaCodecSelector.DEFAULT, null, true, null, null,
                        AudioCapabilities.getCapabilities(context), AudioManager.STREAM_MUSIC);

                exoPlayer.prepare(audioRenderer);
                return true;

            } catch (IllegalArgumentException e1) {
            } catch (SecurityException e1) {
            } catch (IllegalStateException e1) {
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            dialog.dismiss();
            if (success) {
                wifiLock = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, "RadiophonyLock");
                wifiLock.acquire();
                exoPlayer.setPlayWhenReady(true);

                if (RadiophonyService.inwhich == 2) {
                    ((ActivityRadioByCategory) context).notifyShowBar();
                } else if (RadiophonyService.inwhich == 3) {
                    ((ActivityFavorite) context).notifyShowBar();
                } else {
                    ((MainActivity) context).notifyShowBar();
                }
                createNotification();

            } else {
                Toast.makeText(context, context.getString(R.string.internet_disabled), Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void createNotification() {
        Notification notification;
        Bundle bundle = new Bundle();
        bundle.putInt("list", list);
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(getApplicationContext(), MainActivity.class).putExtras(bundle),
                PendingIntent.FLAG_UPDATE_CURRENT);

        notification = new NotificationCompat.Builder(context)
                .setContentTitle(getResources().getString(R.string.app_name)).setContentText(station.getRadioName())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setOngoing(true).setContentIntent(pi).build();


        startForeground(NOTIFICATION_ID, notification);
    }

    public ItemListRadio getPlayingRadioStation() {
        return station;
    }
}
