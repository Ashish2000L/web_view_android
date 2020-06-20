package com.example.web_view;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessagingService;

public class deviceToken extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String DeviceToken= FirebaseInstanceId.getInstance().getToken();
        assert DeviceToken != null;
        Log.d("DEVICE TOKEN",DeviceToken);

    }
}
