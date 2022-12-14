/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.server.wifi;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.net.wifi.WifiContext;

import com.android.internal.messages.nano.SystemMessageProto.SystemMessage;
import com.android.wifi.resources.R;


/** Factory for Wifi Wake notifications. */
public class WakeupNotificationFactory {

    public static final String ACTION_DISMISS_NOTIFICATION =
            "com.android.server.wifi.wakeup.DISMISS_NOTIFICATION";
    public static final String ACTION_OPEN_WIFI_PREFERENCES =
            "com.android.server.wifi.wakeup.OPEN_WIFI_PREFERENCES";
    public static final String ACTION_OPEN_WIFI_SETTINGS =
            "com.android.server.wifi.wakeup.OPEN_WIFI_SETTINGS";
    public static final String ACTION_TURN_OFF_WIFI_WAKE =
            "com.android.server.wifi.wakeup.TURN_OFF_WIFI_WAKE";

    /** Notification channel ID for onboarding messages. */
    public static final int ONBOARD_ID = SystemMessage.NOTE_WIFI_WAKE_ONBOARD;

    private final WifiContext mContext;
    private final FrameworkFacade mFrameworkFacade;

    WakeupNotificationFactory(WifiContext context, FrameworkFacade frameworkFacade) {
        mContext = context;
        mFrameworkFacade = frameworkFacade;
    }

    /**
     * Creates a Wifi Wake onboarding notification.
     */
    public Notification createOnboardingNotification() {
        CharSequence title = mContext.getText(R.string.wifi_wakeup_onboarding_title);
        CharSequence content = mContext.getText(R.string.wifi_wakeup_onboarding_subtext);
        CharSequence disableText = mContext.getText(R.string.wifi_wakeup_onboarding_action_disable);
        int color = mContext.getResources()
                .getColor(android.R.color.system_notification_accent_color, mContext.getTheme());

        final Notification.Action disableAction = new Notification.Action.Builder(
                null /* icon */, disableText, getPrivateBroadcast(ACTION_TURN_OFF_WIFI_WAKE))
                .build();

        return mFrameworkFacade.makeNotificationBuilder(mContext,
                WifiService.NOTIFICATION_NETWORK_STATUS)
                .setSmallIcon(Icon.createWithResource(mContext.getWifiOverlayApkPkgName(),
                        R.drawable.ic_wifi_settings))
                .setTicker(title)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(getPrivateBroadcast(ACTION_OPEN_WIFI_PREFERENCES))
                .setDeleteIntent(getPrivateBroadcast(ACTION_DISMISS_NOTIFICATION))
                .addAction(disableAction)
                .setShowWhen(false)
                .setLocalOnly(true)
                .setColor(color)
                .build();
    }


    private PendingIntent getPrivateBroadcast(String action) {
        Intent intent = new Intent(action).setPackage(mContext.getServiceWifiPackageName());
        return mFrameworkFacade.getBroadcast(mContext, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }
}
