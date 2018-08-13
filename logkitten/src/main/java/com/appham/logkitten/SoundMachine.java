package com.appham.logkitten;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

public class SoundMachine {

    private SoundPool sounds;

    private final int meow;

    SoundMachine(Context context) {
        sounds = createSoundPool();
        meow = sounds.load(context, R.raw.meow, 1);
    }

    public void meow() {
        sounds.play(meow, 1, 1, 1, 0, 1);
    }

    public void release() {
        sounds.release();
        sounds = null;
    }

    private SoundPool createSoundPool() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return createNewSoundPool();
        } else {
            return createOldSoundPool();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
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
    private SoundPool createOldSoundPool() {
        return new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
    }
}
