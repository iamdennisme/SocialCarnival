package com.dennisce.socialcarnival.shareMedia

import android.graphics.Bitmap
import com.dennisce.socialcarnival.shareMedia.ShareMedia

/**
 * @program: SocialCarnival
 * @description:
 * @author:taicheng
 * @create: 19-4-24
 **/

data class TextImageShareMedia(
    var image: Bitmap,
    var text: String
) : ShareMedia