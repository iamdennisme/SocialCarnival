package com.dennisce.socialcarnival

/**
 * @program: SocialCarnival
 * @description:
 * @author:taicheng
 * @create: 19-4-22
 **/
interface SocialAuthorizeListener {
    fun onComplete(socialAuthorizeType: SocialAuthorizeType, map: Map<String, String>)

    fun onError(errMsg: String)

    fun onCancel(socialAuthorizeType: SocialAuthorizeType)
}