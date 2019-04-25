package com.dennisce.socialcarnival

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.dennisce.socialcarnival.config.SocialConfig
import com.dennisce.socialcarnival.handler.QQHandler
import com.dennisce.socialcarnival.handler.SocialHandler
import com.dennisce.socialcarnival.handler.WechatHandler
import com.dennisce.socialcarnival.enums.SocialAuthorizeType
import com.dennisce.socialcarnival.enums.SocialShareType
import com.dennisce.socialcarnival.shareMedia.ShareMedia
import io.reactivex.Observable
import java.lang.ref.WeakReference

/**
 * @program: SocialCarnival
 * @description:
 * @author:taicheng
 * @create: 19-4-22
 **/

class SocialCarnival private constructor() {

    companion object {
        private var weekReference: WeakReference<Context>? = null
        fun init(context: Context) {
            weekReference = WeakReference(context)
        }

        val get: SocialCarnival by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            if (weekReference == null) {
                throw Exception("SocialCarnival:you must init in Application first")
            }
            return@lazy SocialCarnival(weekReference!!.get()!!)
        }
    }

    val socialConfig by lazy {
        SocialConfig()
    }

    private val mMapSocialAuthorizeHandler: HashMap<SocialAuthorizeType, SocialHandler> by lazy {
        HashMap<SocialAuthorizeType, SocialHandler>()
    }

    private lateinit var mContext: Context

    private constructor(context: Context) : this() {
        mContext = context
    }

    fun getSocialHandler(socialAuthorizeType: SocialAuthorizeType): SocialHandler {
        if (mMapSocialAuthorizeHandler.containsKey(socialAuthorizeType)) {
            return mMapSocialAuthorizeHandler[socialAuthorizeType]!!
        }
        if (socialAuthorizeType == SocialAuthorizeType.WECHAT) {
            val handler = WechatHandler(mContext, socialConfig)
            mMapSocialAuthorizeHandler[SocialAuthorizeType.WECHAT] = handler
            return handler
        }

        val handler = QQHandler(mContext, socialConfig)
        mMapSocialAuthorizeHandler[SocialAuthorizeType.QQ] = handler
        return handler
    }

    fun authorize(activity: Activity, socialAuthorizeType: SocialAuthorizeType): Observable<Map<String, String>> {
        return getSocialHandler(socialAuthorizeType).authorize(activity)
    }

    fun share(activity: Activity, socialShareType: SocialShareType, shareMedia: ShareMedia):Observable<SocialShareType>{
       return when(socialShareType){
            SocialShareType.WECHAT, SocialShareType.WECHAT_CIRCLE ->{
                getSocialHandler(SocialAuthorizeType.WECHAT)
            }

            else->{//QQ
                 getSocialHandler(SocialAuthorizeType.QQ)
            }

        }.let {
           return@let it.share(activity,shareMedia,socialShareType)
        }
    }

    fun setActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        (getSocialHandler(SocialAuthorizeType.QQ) as QQHandler).setActivityResult(requestCode, resultCode, data)
    }
}