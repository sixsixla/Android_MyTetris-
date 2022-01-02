package com.example.mytetrisbyme;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;

public class MediaMusci {
    MediaPlayer media;
    int position;

    public void startVoice(Context context) {

        if (media != null && media.isPlaying()) {return;}
        media = MediaPlayer.create(context, R.raw.bgmusic2);
        media.start();
        media.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (media == null) {return;}
                media.start();
                media.setLooping(true);
            }
        });
    }
    /*暂停播放*/
    public void pauseVoic(){
        if(media!=null && media.isPlaying()) {
            media.pause();
            position = media.getCurrentPosition();
        }
    }
    /*恢复播放*/
    public void resumeVoic(){
        if(media!=null && !media.isPlaying()) {
            media.start();
            media.seekTo(position);
        }

    }

    public void stopVoice() {
        if (media != null) {
            media.stop();
            media.release(); //切记一定要release
            media = null;
        }
    }
}
