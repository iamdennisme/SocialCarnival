package com.dennisce.socialcarnival.shareMedia

import android.graphics.Bitmap
import com.dennisce.socialcarnival.shareMedia.ShareMedia

/**
 * @program: SocialCarnival
 * @description:
 * @author:taicheng
 * @create: 19-4-24
 */

class WechatMiniAppShareMedia(
    var originId: String,//小程序url
    var title: String, //标题
    var desc: String, //描述
    var thumb: Bitmap, //缩略图
    var url: String,//兼容低版本链接
    var path: String//参数path
): ShareMedia