package com.dennisce.socialcarnival

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import com.blankj.utilcode.util.LogUtils
import com.dennisce.socialcarnival.shareMedia.ImageShareMedia
import com.dennisce.socialcarnival.shareMedia.ShareMedia
import com.dennisce.socialcarnival.shareMedia.VoiceShareMedia
import com.dennisce.socialcarnival.shareMedia.WebShareMedia
import com.tencent.connect.share.QQShare
import com.tencent.connect.share.QzoneShare
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError
import io.reactivex.Emitter
import io.reactivex.Observable
import org.json.JSONObject
import java.io.File
import java.util.*

/**
 * @program: SocialCarnival
 * @description:
 * @author:taicheng
 * @create: 19-4-22
 **/

class QQHandler(context: Context, socialConfig: SocialConfig) : SocialHandler {

    private val mTencent by lazy {
        Tencent.createInstance(socialConfig.qqAppId, context.applicationContext)
    }

    override fun authorize(activity: Activity): Observable<Map<String, String>> {
        return Observable.create { emitter ->
            if (!mTencent.isSessionValid) {
                mTencent.login(activity, "all", object : IUiListener {
                    override fun onComplete(p0: Any?) {
                        p0 as JSONObject
                        emitter.onNext(p0.jsonToMap())
                        LogUtils.d(p0.toString())
                    }

                    override fun onCancel() {
                        emitter.onError(Throwable("user_cancel"))
                        LogUtils.d("user_cancel")
                    }

                    override fun onError(p0: UiError?) {
                        emitter.onError(Throwable(p0?.errorMessage))
                        LogUtils.d(p0?.errorMessage)
                    }
                })
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun share(
        activity: Activity, shareMedia: ShareMedia, socialShareType: SocialShareType
    ): Observable<SocialShareType> {
        var emitter: Emitter<SocialShareType>? = null
        val result = Observable.create<SocialShareType> {
            emitter = it
        }
        val path = "${Environment.getExternalStorageDirectory().path}/Pictures/${activity.applicationContext.packageName}.share_qq_img_tmp.png"
        val file = File(path)
        if (file.exists()) {
            file.delete()
        }
        val params = Bundle()
        if (socialShareType == SocialShareType.QZONE) {//qq空间
            if (shareMedia is WebShareMedia) {       //网页分享
                //图片保存本地
                shareMedia.thumb.saveBitmap(path)
                params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT)
                params.putString(QzoneShare.SHARE_TO_QQ_TITLE, shareMedia.title)
                params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, shareMedia.description)
                params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, shareMedia.webUrl)

                val pathArray = ArrayList<String>()
                pathArray.add(path)
                params.putStringArrayList(
                    QzoneShare.SHARE_TO_QQ_IMAGE_URL, pathArray
                )  //!这里是大坑 不能用SHARE_TO_QQ_IMAGE_LOCAL_URL
            } else {
                emitter?.onError(Throwable("QZone is not support this shareMedia"))
                return result
            }

            //qq zone分享
            this.mTencent.shareToQzone(activity, params, object : IUiListener {
                override fun onComplete(o: Any) {
                    emitter?.onNext(socialShareType)
                    if (file.exists()) {
                        file.delete()
                    }
                }

                override fun onError(uiError: UiError) {
                    val errMsg =
                        "errCode=" + uiError.errorCode + " errMsg=" + uiError.errorMessage + " errDetail=" + uiError.errorDetail
                    LogUtils.d(errMsg)
                    emitter?.onError(Throwable(errMsg))
                    if (file.exists()) {
                        file.delete()
                    }
                }

                override fun onCancel() {
                    emitter?.onError(Throwable("share_user_cancel"))
                    if (file.exists()) {
                        file.delete()
                    }
                }
            })
            return result
        }
        //QQ分享

        when (shareMedia) {
            is WebShareMedia -> {
                //图片保存本地
                shareMedia.thumb.saveBitmap(path)
                params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT)
                params.putString(QQShare.SHARE_TO_QQ_TITLE, shareMedia.title)
                params.putString(QQShare.SHARE_TO_QQ_SUMMARY, shareMedia.description)
                params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareMedia.webUrl)
                params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, path)
            }
            is ImageShareMedia -> {
                //图片保存本地
                shareMedia.image.saveBitmap(path)
                params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE)
                params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, path)
            }
            is VoiceShareMedia -> {
                //图片保存本地
                shareMedia.thumb.saveBitmap(path)
                params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_AUDIO)
                params.putString(QQShare.SHARE_TO_QQ_TITLE, shareMedia.title)
                params.putString(QQShare.SHARE_TO_QQ_SUMMARY, shareMedia.description)
                params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareMedia.voiceUrl)
                params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, path)
                params.putString(QQShare.SHARE_TO_QQ_AUDIO_URL, shareMedia.voiceUrl)
            }
            else -> {
                emitter?.onError(Throwable("QQ is not support this shareMedia"))
                return result
            }
        }

        //qq分享
        mTencent.shareToQQ(activity, params, object : IUiListener {
            override fun onComplete(o: Any) {
                emitter?.onNext(socialShareType)
                if (file.exists()) {
                    file.delete()
                }
            }

            override fun onError(uiError: UiError) {
                val errMsg =
                    "errCode=" + uiError.errorCode + " errMsg=" + uiError.errorMessage + " errDetail=" + uiError.errorDetail
                LogUtils.d(errMsg)
                emitter?.onError(Throwable(errMsg))
                if (file.exists()) {
                    file.delete()
                }
            }

            override fun onCancel() {
                emitter?.onError(Throwable("share_user_cancel"))
                if (file.exists()) {
                    file.delete()
                }
            }
        })
        return result
    }


    fun setActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Tencent.onActivityResultData(requestCode, resultCode, data, null)
    }
}