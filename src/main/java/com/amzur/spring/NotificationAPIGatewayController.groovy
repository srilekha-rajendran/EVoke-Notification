package com.amzur.spring


import groovy.json.JsonBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/notification" )
class NotificationAPIGatewayController {

    private static final Logger log = LoggerFactory.getLogger(NotificationAPIGatewayController.class);
    public static java.text.SimpleDateFormat sdfUTC = new java.text.SimpleDateFormat("yyyyMMdd'T'HHmmss.SSS")
    private HttpServletRequest request;

    @Autowired
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    /**
     *
     * @param evseIdentifier This will be subtopic (an entry in kafka message message header).
     * @param chargeSessionId as well as idTag for txn
     * @param connectorId Connector index
     * @return
     */
    @RequestMapping(value = '/send', method = RequestMethod.POST)
    @ResponseBody
    String pushNotification(PushNotificationCmd pushNotificationCmd) {
        def retVal = [response: -1]
        int status = 0;
        if(pushNotificationCmd){
            if( pushNotificationCmd.deviceType == MobileDeviceType.ANDROID ){
                status = PushNotificationAndroid.pushFCMNotification(notificationCmd)
                log.info('Notification status.ANDROID..'+status)
            }else if ( pushNotificationCmd.deviceType == MobileDeviceType.IOS ){
                status = PushNotificationSenderIOS.sendNotification(notificationCmd)
                log.info('Notification status..IOS.'+status)
            }
        }
        [response: 0,'status':'success']
        return new JsonBuilder(retVal).toPrettyString()
    }
}
