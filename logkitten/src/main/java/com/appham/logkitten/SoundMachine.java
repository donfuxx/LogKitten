package com.appham.logkitten;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

public class SoundMachine {

    private final SharedPreferences prefs;
    private final String soundPrefKey;
    private SoundPool sounds;

    private final int meow;

    public SoundMachine(Context context) {
        sounds = createSoundPool();
        meow = sounds.load(context, R.raw.logkitten_meow, 1);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        soundPrefKey = context.getString(R.string.logkitten_pref_sound_key);
    }

    public void meow() {
        if (prefs.getBoolean(soundPrefKey, true)) {
            sounds.play(meow, 1, 1, 1, 0, 1);
        }
    }

    public void release() {
        sounds.release();
        sounds = null;
    }

    @NonNull
    private SoundPool createSoundPool() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return createNewSoundPool();
        } else {
            return createOldSoundPool();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @NonNull
    private SoundPool createNewSoundPool() {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        return new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .build();
    }

    @SuppressWarnings("deprecation")
    @NonNull
    private SoundPool createOldSoundPool() {
        return new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
    }
}
