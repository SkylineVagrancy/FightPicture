package com.zjp.fightpicture;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;


import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXEmojiObject;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXVideoObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * Created by pzh on 16/9/8.
 */
public class ShareUtils {


    public static boolean isWeiXinInstallAndSupport(Context context) {
        boolean flag = false;
        IWXAPI api = WXAPIFactory.createWXAPI(context, "wxe6bc4012b43c1255", false);
        if (api.isWXAppInstalled() && api.isWXAppSupportAPI()) {
            flag = true;
        }
        return flag;
    }


    /**
     * @param context
     * @param type    1:分享到朋友圈            2：分享到微信好友
     * @param title   标题
     * @param url     跳转url
     */
    public static void shareToWeChat(Context context, int type, String title, String url) {
        IWXAPI api = WXAPIFactory.createWXAPI(context, "wxe6bc4012b43c1255", false);
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, 150, 150, true);
        bitmap.recycle();

        WXWebpageObject webpageObject = new WXWebpageObject();
        webpageObject.webpageUrl = url;

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = webpageObject;
        msg.title = title;
        msg.thumbData = bmpToByteArray(thumbBmp, true);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.message = msg;
        req.transaction = System.currentTimeMillis() + "";
        req.scene = type == 1 ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
        api.sendReq(req);
    }

    public static void shareBitmapToWechat(Context context, int type, byte[] bitmap) {
        IWXAPI api = WXAPIFactory.createWXAPI(context, "wxe6bc4012b43c1255", false);
        WXEmojiObject imageObject = new WXEmojiObject(bitmap);
//        WXImageObject imageObject = new WXImageObject(bitmap);
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imageObject;
        Bitmap thumbBmp = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        msg.thumbData = bmpToByteArray(thumbBmp, true);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = System.currentTimeMillis() + "";
        req.message = msg;
        req.scene = type == 1 ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
        api.sendReq(req);

    }


//    /**
//     * @param context
//     * @param title       标题
//     * @param description 描述
//     * @param url         跳转URL
//     * @param imageUrl    图片链接
//     * @param callBack
//     */
//    public static void shareToQQ(Tencent mTencent, Context context, String title, String description, String url, String imageUrl, IUiListener callBack) {
//        Bundle params = new Bundle();
//        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
//        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
//        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, description);
//        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);
//        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl);
//        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "我去彩票站");
//        mTencent.shareToQQ((Activity) context, params, callBack);
//    }
//
//
//    /**
//     * @param context
//     * @param picLists    图片arraylist,可以分享多张图片
//     * @param title       标题
//     * @param description 描述
//     * @param url         跳转url
//     * @param callBack
//     */
//    public static void shareToZone(Context context, ArrayList<String> picLists, String title, String description, String url, IUiListener callBack) {
//        Tencent mTencent = Tencent.createInstance(AppConfig.QQ_APPID, context.getApplicationContext());
//        final Bundle params = new Bundle();
//        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
//        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);//必填
//        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, description);//选填
//        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, url);//必填
//        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, picLists);
//        mTencent.shareToQzone((Activity) context, params, callBack);
//    }
//
//    /**
//     * @param context
//     * @param phoneNum 短信接受者电话号码
//     * @param content  短信内容
//     */
//    public static void shareToSms(Context context, String phoneNum, String content) {
//        Uri smsToUri = Uri.parse("smsto:" + phoneNum);
//
//        Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
//
//        intent.putExtra("sms_body", content);
//
//        context.startActivity(intent);
//    }


//    /**
//     * @param context
//     * @param resId       显示图片
//     * @param title       标题
//     * @param description 描述
//     * @param url         跳转url
//     */
//    public static void shareToWeibo(IWeiboShareAPI api, final Context context, int resId, String title, String description, String url) {
//        WebpageObject mediaObject = new WebpageObject();
//        mediaObject.identify = Utility.generateGUID();
//        mediaObject.title = title;
//        mediaObject.description = title;
//
//        TextObject textObject = new TextObject();
//        textObject.text = title;
//
//        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
//        // 设置 Bitmap 类型的图片到视频对象里         设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
//        mediaObject.setThumbImage(bitmap);
//        mediaObject.actionUrl = url;
//        mediaObject.defaultText = title;
//
//        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
//        weiboMessage.mediaObject = mediaObject;
//        weiboMessage.textObject = textObject;
//        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
//        // 用transaction唯一标识一个请求
//        request.transaction = String.valueOf(System.currentTimeMillis());
//        request.multiMessage = weiboMessage;
//        // 3. 发送请求消息到微博，唤起微博分享界面
//
//
//        AuthInfo authInfo = new AuthInfo(context, AppConfig.WEIBO_APPID, "", AppConfig.SCOPE);
//        Oauth2AccessToken accessToken = readAccessToken(context.getApplicationContext());
//        String token = "";
//        if (accessToken != null) {
//            token = accessToken.getToken();
//        }
//        api.sendRequest((Activity) context, request, authInfo, token, new WeiboAuthListener() {
//
//            @Override
//            public void onWeiboException(WeiboException arg0) {
//            }
//
//            @Override
//            public void onComplete(Bundle bundle) {
//                // TODO Auto-generated method stub
//                Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
//                writeAccessToken(context.getApplicationContext(), newToken);
//            }
//
//            @Override
//            public void onCancel() {
//            }
//        });
//
//    }
//
//    public static void writeAccessToken(Context context, Oauth2AccessToken token) {
//        if (null == context || null == token) {
//            return;
//        }
//
//        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
//        SharedPreferences.Editor editor = pref.edit();
//        editor.putString(KEY_UID, token.getUid());
//        editor.putString(KEY_ACCESS_TOKEN, token.getToken());
//        editor.putString(KEY_REFRESH_TOKEN, token.getRefreshToken());
//        editor.putLong(KEY_EXPIRES_IN, token.getExpiresTime());
//        editor.commit();
//    }
//
//    public static Oauth2AccessToken readAccessToken(Context context) {
//        if (null == context) {
//            return null;
//        }
//
//        Oauth2AccessToken token = new Oauth2AccessToken();
//        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
//        token.setUid(pref.getString(KEY_UID, ""));
//        token.setToken(pref.getString(KEY_ACCESS_TOKEN, ""));
//        token.setRefreshToken(pref.getString(KEY_REFRESH_TOKEN, ""));
//        token.setExpiresTime(pref.getLong(KEY_EXPIRES_IN, 0));
//
//        return token;
//    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }
        byte[] result = output.toByteArray();

        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


}

