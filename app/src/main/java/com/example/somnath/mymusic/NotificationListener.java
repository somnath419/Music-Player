package com.example.somnath.mymusic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by SOMNATH on 02-12-2017.
 */

public class NotificationListener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context,String.valueOf(intent.getIntExtra("stop",0)),Toast.LENGTH_SHORT).show();

        if(intent.getIntExtra("stop",0)==1)
        {
            Toast.makeText(context,"hwooo",Toast.LENGTH_SHORT).show();

        }




    }

}