package com.dennisce.socialcarnival.wechat

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import com.dennisce.socialcarnival.SocialCarnival
import com.dennisce.socialcarnival.enums.SocialAuthorizeType
import com.dennisce.socialcarnival.handler.WechatHandler
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler

class WXEntryActivity : Activity(), IWXAPIEventHandler {

    private val weChatHandler: WechatHandler by lazy {
        SocialCarnival.get.getSocialHandler(SocialAuthorizeType.WECHAT) as WechatHandler
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        weChatHandler.getApi().handleIntent(intent, this)
    }

    override fun onReq(req: BaseReq) {
        finish()
        Handler().postDelayed({
            weChatHandler.onReq(req)
        }, 500)
    }

    override fun onResp(resp: BaseResp) {
        finish()
        Handler().postDelayed({
            weChatHandler.onResp(resp)
        }, 500)
    }
}