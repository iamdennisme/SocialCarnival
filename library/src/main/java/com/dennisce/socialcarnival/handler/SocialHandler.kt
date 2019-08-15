package com.dennisce.socialcarnival.handler

import android.app.Activity
import com.dennisce.socialcarnival.enums.SocialShareType
import com.dennisce.socialcarnival.pay.PayInfo
import com.dennisce.socialcarnival.shareMedia.ShareMedia
import io.reactivex.Observable

/**
 * @program: SocialCarnival
 * @description:
 * @author:taicheng
 * @create: 19-4-22
 **/
interface SocialHandler {
     fun authorize(activity: Activity): Observable<Map<String, String>>
     fun share(activity: Activity, shareMedia: ShareMedia, socialShareType: SocialShareType): Observable<SocialShareType>
     fun pay(activity: Activity,payInfo: PayInfo): Observable<String>
}