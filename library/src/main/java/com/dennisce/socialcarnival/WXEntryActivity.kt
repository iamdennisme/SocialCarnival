package com.dennisce.socialcarnival

import android.app.Activity
import android.os.Bundle
import com.dennisce.socialcarnival.SocialAuthorizeType
import com.dennisce.socialcarnival.SocialCarnival
import com.dennisce.socialcarnival.WechatHandler
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler

class WXEntryActivity : Activity(), IWXAPIEventHandler {

    private val weChatHandler: WechatHandler by lazy {
        SocialCarnival.INSTANCE.getSocialHandler(SocialAuthorizeType.WECHAT) as WechatHandler
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        weChatHandler.getApi().handleIntent(intent, this)
    }

    override fun onReq(req: BaseReq) {
        weChatHandler.onReq(req)
        finish()
    }

    override fun onResp(resp: BaseResp) {
        weChatHandler.onResp(resp)
        finish()
    }
}