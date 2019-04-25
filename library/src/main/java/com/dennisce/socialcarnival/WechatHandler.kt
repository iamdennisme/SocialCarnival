package com.dennisce.socialcarnival

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import com.blankj.utilcode.util.LogUtils
import com.dennisce.socialcarnival.shareMedia.*
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.*
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import io.reactivex.Emitter
import io.reactivex.Observable
import java.util.*


/**
 * @program: SocialCarnival
 * @description:
 * @author:taicheng
 * @create: 19-4-22
 **/

@SuppressLint("CheckResult")
class WechatHandler(context: Context, socialConfig: SocialConfig) : SocialHandler, IWXAPIEventHandler {
    companion object {
        private const val RETURN_MSG_TYPE_LOGIN = 1
        private const val K32 = 1024 * 32

        private const val SCOPE = "snsapi_userinfo,snsapi_friend,snsapi_message"
        private const val STATE = "none"
    }

    private var mLastTransaction = ""

    private var mAuthorizeEmitter: Emitter<Map<String, String>>? = null

    private var mShareEmitter: Emitter<SocialShareType>? = null

    private var currentShareType = SocialShareType.WECHAT


    private val mWXApi by lazy {
        WXAPIFactory.createWXAPI(context.applicationContext, socialConfig.wechatAppId, true).apply {
            registerApp(socialConfig.wechatAppId)
        }
    }

    fun getApi(): IWXAPI {
        return mWXApi
    }

    override fun onResp(resp: BaseResp) {
        LogUtils.d("错误码 : " + resp.errCode + "")

        if (resp.type == RETURN_MSG_TYPE_LOGIN) {//登录
            when (resp.errCode) {
                BaseResp.ErrCode.ERR_AUTH_DENIED -> {
                    mAuthorizeEmitter?.onError(Throwable(resp.errStr))
                    LogUtils.d("login_auth_denied")
                }
                BaseResp.ErrCode.ERR_OK -> {
                    val code = (resp as SendAuth.Resp).code
                    val data = HashMap<String, String>()
                    data["code"] = resp.code
                    mAuthorizeEmitter?.onNext(data)
                    LogUtils.d("code = $code")
                }
                BaseResp.ErrCode.ERR_USER_CANCEL -> { // 授权取消
                    LogUtils.d("login_user_cancel")
                    mAuthorizeEmitter?.onError(Throwable("login_user_cancel"))
                }
            }
            return
        }
        //分享
        when (resp.errCode) {
            BaseResp.ErrCode.ERR_AUTH_DENIED -> {
                mShareEmitter?.onError(Throwable(resp.errStr))
                LogUtils.d("share_auth_denied")
            }
            BaseResp.ErrCode.ERR_OK -> {
                mShareEmitter?.onNext(currentShareType)
                LogUtils.d("wechat share success")
            }
            BaseResp.ErrCode.ERR_USER_CANCEL -> { // 分享取消
                LogUtils.d("share_user_cancel")
                mShareEmitter?.onError(Throwable("share_user_cancel"))
            }
        }
    }

    override fun onReq(p0: BaseReq?) {

    }


    override fun authorize(activity: Activity): Observable<Map<String, String>> {
        val result = Observable.create<Map<String, String>> { emitter ->
            mAuthorizeEmitter = emitter
        }

        if (!mWXApi.isWXAppInstalled) {
            mAuthorizeEmitter?.onError(Throwable("wechat not install"))
            LogUtils.d("wechat not install")
            return result
        }
        val req = SendAuth.Req()
        req.scope = SCOPE
        req.state = STATE
        req.transaction = buildTransaction("authorize")
        mLastTransaction = req.transaction
        if (!this.mWXApi.sendReq(req)) {
            mAuthorizeEmitter?.onError(Throwable("wechat api sendReq fail"))
            LogUtils.d("wechat api sendReq fail")
        }
        return result
    }

    override fun share(
        activity: Activity,
        shareMedia: ShareMedia,
        socialShareType: SocialShareType
    ): Observable<SocialShareType> {
        val result = Observable.create<SocialShareType> { emitter ->
            mShareEmitter = emitter
            currentShareType = socialShareType
        }

        if (!mWXApi.isWXAppInstalled) {
            mAuthorizeEmitter?.onError(Throwable("wechat not install"))
            LogUtils.d("wechat not install")
            return result
        }

        val msg = WXMediaMessage()
        var type = ""

        when (shareMedia) {
            is WebShareMedia -> {
                type = "webpage"
                //web object
                val webPageObject = WXWebpageObject()
                webPageObject.webpageUrl = shareMedia.webUrl
                msg.mediaObject = webPageObject
                msg.title = shareMedia.title
                msg.description = shareMedia.description
                msg.thumbData = shareMedia.thumb.bitmap2Bytes()
            }
            is TextShareMedia -> {
                type = "text"
                //text object
                val textObject = WXTextObject()
                textObject.text = shareMedia.text
                msg.mediaObject = textObject
                msg.description = shareMedia.text
            }
            is ImageShareMedia -> {
                type = "image"
                //image object
                val imageObject = WXImageObject()
                //image限制10M
                imageObject.imageData = shareMedia.image.bitmap2Bytes().compressBitmap(10 * 1024 * 1024)
                msg.mediaObject = imageObject
                //直接缩放图片
                val thumb = Bitmap.createScaledBitmap(shareMedia.image, 200, 200, true)
                msg.thumbData = thumb.bitmap2Bytes()
                thumb.recycle()
            }
            is VoiceShareMedia -> {
                type = "music"
                val musicObject = WXMusicObject()
                musicObject.musicUrl = shareMedia.voiceUrl
                msg.mediaObject = musicObject
                msg.title = shareMedia.title
                msg.description = shareMedia.description
                msg.thumbData = shareMedia.thumb.bitmap2Bytes()
            }
            is VideoShareMedia -> {
                type = "video"
                val videoObject = WXVideoObject()
                videoObject.videoUrl = shareMedia.videoUrl
                msg.mediaObject = videoObject
                msg.title = shareMedia.title
                msg.description = shareMedia.description
                msg.thumbData = shareMedia.thumb.bitmap2Bytes()
            }
            is WechatMiniAppShareMedia -> {
                val miniProgram = WXMiniProgramObject()
                miniProgram.userName = shareMedia.originId//小程序端提供参数
                miniProgram.webpageUrl = shareMedia.url
                miniProgram.path = shareMedia.path
                msg.mediaObject = miniProgram
                msg.title = shareMedia.title
                msg.description = shareMedia.desc
                msg.thumbData = shareMedia.thumb.bitmap2Bytes()
            }
            else -> {
                mShareEmitter?.onError(Throwable("wechat is not support this shareMedia"))
                return result
            }
        }

        //压缩缩略图到32kb
        if (msg.thumbData != null && msg.thumbData.size > K32) {//微信sdk里面判断的大小
            msg.thumbData.compressBitmap(K32)
        }

        //发起request
        val req = SendMessageToWX.Req()
        req.message = msg
        req.transaction = buildTransaction(type)
        mLastTransaction = req.transaction

        if (socialShareType === SocialShareType.WECHAT) {     //分享好友
            req.scene = SendMessageToWX.Req.WXSceneSession
        } else if (socialShareType === SocialShareType.WECHAT_CIRCLE) {      //分享朋友圈
            req.scene = SendMessageToWX.Req.WXSceneTimeline
        }

        if (!this.mWXApi.sendReq(req)) {
            this.mShareEmitter?.onError(Throwable("$socialShareType share fail"))
            LogUtils.d("$socialShareType share fail")
        }
        return result
    }

    private fun buildTransaction(type: String): String {
        return type + System.currentTimeMillis()
    }
}
