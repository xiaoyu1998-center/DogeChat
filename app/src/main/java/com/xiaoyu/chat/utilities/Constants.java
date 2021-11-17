package com.xiaoyu.chat.utilities;

import java.util.HashMap;

public class Constants {
    public static final String KEY_COLLECTION_USERS = "users";
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_STATUS = "status";
    public static final String KEY_PENDING = "0";
    public static final String KEY_SUCCESS = "1";
    public static final String KEY_PASSCODE_TYPE = "passcode_type";
    public static final String KEY_PASSCODE_ENABLED = "passcode_status";
    public static final String GET_USER_ID_BY_VERIFY = "GET_USER_VERIFY_ID";
    public static final String GET_USER_EMAIL_BY_VERIFY = "GET_USER_VERIFY_EMAIL";
    public static final String GET_USER_NAME_BY_VERIFY = "GET_USER_VERIFY_NAME";
    public static final String KEY_CODE = "code";
    public static final String KEY_CODE_EXPIRATION = "code_exp";
    public static final String KEY_USER_REGISTER = "register_date";
    public static final String KEY_PREFERENCE_NAME = "chatAppPreference";
    public static final String KEY_IS_SIGNED_IN = "isSignedIn";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_FCM_TOKEN = "fcmToken";
    public static final String KEY_USER = "user";
    public static final String KEY_COLLECTION_CHAT = "chat";
    public static final String KEY_SENDER_ID = "senderId";
    public static final String KEY_RECEIVER_ID = "receiverId";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_COLLECTION_CONVERSATIONS = "conversation";
    public static final String KEY_SENDER_NAME = "senderName";
    public static final String KEY_RECEIVER_NAME = "receiverName";
    public static final String KEY_SENDER_IMAGE = "senderImage";
    public static final String KEY_RECEIVER_IMAGE = "receiverImage";
    public static final String KEY_LAST_MESSAGE = "lastMessage";
    public static final String KEY_AVAILABILITY = "availability";
    public static final String KEY_COLLECTION_APP = "version";
    public static final String KEY_APP = "app";
    public static final String KEY_APP_NAME = "XIAOYU";
    public static final String KEY_APP_STATUS = "status";
    public static final String KEY_APP_LINK = "link";
    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";

    public static HashMap<String, String> remoteMsgHeaders = null;
    public static HashMap<String, String> getRemoteMsgHeaders() {
        if (remoteMsgHeaders == null) {
            remoteMsgHeaders = new HashMap<>();
            remoteMsgHeaders.put(
                    REMOTE_MSG_AUTHORIZATION,
                    "key=AAAA5P7SHf0:APA91bHT17z62KAna_IdDPCKfE-gSz_TtFI36kPFkk5U2Mp43JZh04zEqNLid-tZFz7rYVUiSHZyjGzl1xz3RKezBdeqK7CBtg-dsxZrHL1C5mT0qh2R_bDdkO3UZMjV5EITdAbpjv1g"
            );
            remoteMsgHeaders.put(
                    REMOTE_MSG_CONTENT_TYPE,
                    "application/json"
            );
        }
        return remoteMsgHeaders;
    }
}
