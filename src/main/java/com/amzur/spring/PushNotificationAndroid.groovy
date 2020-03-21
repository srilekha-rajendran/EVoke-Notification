/*
  Author : Srilekha
  Sending Push Notifications to Android device
 */
package com.amzur.spring

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import grails.converters.JSON
import org.grails.web.json.JSONObject
import org.springframework.beans.factory.annotation.Value

public class PushNotificationAndroid {

    // Method to send Notifications from server to client end.

    @Value('${pushNotifications.android_fcmUrl}')
    public final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";

    @Value('${pushNotifications.android_fcmServerKey}')
    public final static String FCM_SERVER_KEY ="AAAAdXoPZzI:APA91bGqElhNUlvemIGwD4A-1bshvwU3qSgI1WPwPU4XP-DDvsSaHeBtJgkN9RQgiDdqr-xsRu6l4sXOCmoLUIj6X6i9d6XXbilMnYWf6k4NKDv-he0dXvz5KytxzvM0ey9hOKHMxoas";

    public static int pushFCMNotification(PushNotificationCmd pushNotificationCmd) throws Exception {

        String FMCurl = API_URL_FCM;

        URL url = new URL(FMCurl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "key=" + FCM_SERVER_KEY);
        conn.setRequestProperty("Content-Type", "application/json");

        PushNotifyCmd notificationResponse = new PushNotifyCmd()

        notificationResponse.click_action = pushNotificationCmd.click_action
        notificationResponse.status = pushNotificationCmd.status
        notificationResponse.category = pushNotificationCmd.category
        notificationResponse.date = pushNotificationCmd.date
        notificationResponse.ID = pushNotificationCmd.id
        notificationResponse.title = pushNotificationCmd.title
        notificationResponse.body = pushNotificationCmd.message

        NotificationPushCmd titleBody = new NotificationPushCmd()

        titleBody.title = pushNotificationCmd.title
        titleBody.body = pushNotificationCmd.message

        //TODO better naming convention to be followed -srilekha
        Gson gsonBuilder = new GsonBuilder().create();
        String jsonFromPojo = gsonBuilder.toJson(notificationResponse);
        String jsonFromPojo1 = gsonBuilder.toJson(titleBody);

        JSONObject json = JSON.parse(jsonFromPojo)
        JSONObject json1 = JSON.parse(jsonFromPojo1)

        JSONObject data = new JSONObject();
        data.put("to", pushNotificationCmd.fcmToken.trim());


        JSONObject info = new JSONObject();

        data.put('notification',json1)
        data.put("data", json);

        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        println('JSON Object '+data.toString())
        wr.write(data);
        wr.flush();
        wr.close();

        int responseCode = conn.getResponseCode();
        println("Response Code : " + responseCode);
        println("Response message : " + conn.responseMessage);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = bufferedReader.readLine()) != null) {
            response.append(inputLine);
        }
        bufferedReader.close();

        return responseCode;
    }

    /* @SuppressWarnings("static-access")
     public static void main(String[] args) throws Exception {
         String userDeviceIdKey = 'fNwvVjEmIfk:APA91bH9sKdm1S6TAYkuf1b10xsWD5pKDl9h_U7YNheuWWhC3izATGatR5sK3rXLwaAXjiJHpWWvwNS_5FKpfVUxAlNSd0NKIWV75mM9bVoX_8-e49DXPirQLIgNV4FjGPkYs1Mr2Th4'

         String title="Your Connection Code Request is approved";
         String message="On boarded successfully";

         PushNotificationCmd pushNotificationCmd1 = new PushNotificationCmd()
         pushNotificationCmd1.title = title
         pushNotificationCmd1.message = message
         pushNotificationCmd1.fcmToken = userDeviceIdKey
         pushNotificationCmd1.status = 'Approved'
         pushNotificationCmd1.click_action = 'FLUTTER_NOTIFICATION_CLICK'
         pushNotificationCmd1.id = '12345'
         pushNotificationCmd1.date = '24-02-2020'
         pushNotificationCmd1.category = 'Connection Code'
         PushNotificationAndroid.pushFCMNotification(pushNotificationCmd1);
     }*/
}

class NotificationPushCmd{
    String title
    String body
}

class PushNotifyCmd{
    String status
    String category
    String click_action
    String ID
    String date
    String title
    String body
}

/*
{
  notification:
 {
 title: Flutter Title,
 body: Hello narendra
 },
 data: {
 status: Approved,
 category: ConnectionCode,  //charging Activity
 click_action: FLUTTER_NOTIFICATION_CLICK,
 ID:uniqueiadenyfier,
 date:'MM-dd-yyyy'
  }
 }
 */
