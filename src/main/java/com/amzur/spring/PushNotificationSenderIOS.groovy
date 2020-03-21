/*
  Author : Srilekha
  Sending Push Notifications to IOS device
 */
package com.amzur.spring

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.notnoop.apns.ApnsNotification
import com.notnoop.apns.ApnsService
import com.notnoop.apns.APNS
import com.notnoop.apns.PayloadBuilder
import grails.converters.JSON
import org.grails.web.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value

/*
 * Copyright (c) 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

/**
 * PushNotificationSender encapsulates sending push notifications.
 */
public class PushNotificationSenderIOS {

    private static final Logger logger = LoggerFactory.getLogger(PushNotificationSenderIOS.class);

    ApnsService service

    @Value('${pushNotifications.IOS_p12_password}')
    public final static String IOS_p12_password = "12345678";

    @Value('${pushNotifications.IOS_p12_file}')
    public final static String IOS_p12_file = "/home/ubuntu/app/p12/Evok_p12Certificates.p12";

    @Value('${pushNotifications.is_production}')
    public final static boolean is_production = false;


    public int sendNotification(PushNotificationCmd pushNotificationCmd) throws Exception{
        InputStream inputStream = new FileInputStream(IOS_p12_file);

        if(is_production){
            service=  APNS.newService()
                    .withCert(inputStream,IOS_p12_password)
                    .withProductionDestination().build();
        }else{
            service=  APNS.newService()
                    .withCert(inputStream,IOS_p12_password)
                    .withSandboxDestination().build();
        }

        PushNotifyCmd notificationResponse = new PushNotifyCmd()

        notificationResponse.click_action = pushNotificationCmd.click_action
        notificationResponse.status = pushNotificationCmd.status
        notificationResponse.category = pushNotificationCmd.category
        notificationResponse.date = pushNotificationCmd.date
        notificationResponse.ID = pushNotificationCmd.id
        notificationResponse.title = pushNotificationCmd.title
        notificationResponse.body = pushNotificationCmd.message

        Gson gsonBuilder = new GsonBuilder().create();
        String jsonFromPojo = gsonBuilder.toJson(notificationResponse);
        JSONObject json = JSON.parse(jsonFromPojo)

        PayloadBuilder pb = APNS.newPayload().alertTitle(notificationResponse.title)
                .alertBody( notificationResponse.body)
        pb.customField("data",json)

        String payload = pb.build();

        logger.info('payload:' +payload)

        ApnsNotification notification = service.push(pushNotificationCmd.fcmToken, payload);

        logger.info('The message has been sent to IOS device..'+notification.expiry)

        if(inputStream!=null)
            inputStream.close()

        return 200
    }
    /* @SuppressWarnings("static-access")
      public static void main(String[] args) throws Exception {
         PushNotificationSenderIOS notify = new PushNotificationSenderIOS()
         PushNotificationCmd pushNotificationCmd1 = new PushNotificationCmd()
         pushNotificationCmd1.title = 'Your Connection Code Request is approved'
         pushNotificationCmd1.message = 'On boarded successfully'
         pushNotificationCmd1.fcmToken = 'FA4DDF6838E8D284C7CE781A2F4B7C3D05FD7D025122A2145155B07718620E8E'
         pushNotificationCmd1.status = 'Approved'
         pushNotificationCmd1.click_action = 'FLUTTER_NOTIFICATION_CLICK'
         pushNotificationCmd1.id = '12345'
         pushNotificationCmd1.date = '24-02-2020'
         pushNotificationCmd1.category = 'Connection Code'
         notify.sendNotification(pushNotificationCmd1)
      }*/
}
