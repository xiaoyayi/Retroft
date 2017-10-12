package com.example.fzy.retrofitdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import java.io.File;

/**
 * Created by Administrator on 2017/10/12.
 */

public class MyDownloadReceiver extends BroadcastReceiver {
    private String downloadPath = "";

    @Override
    public void onReceive(final Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            downloadPath = bundle.getString("path");
        }
        Intent in = new Intent();
        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        in.setAction("android.intent.action.VIEW");
        in.setDataAndType(
                Uri.fromFile(new File(downloadPath)), "video/*");
        context.startActivity(in);

    }
}