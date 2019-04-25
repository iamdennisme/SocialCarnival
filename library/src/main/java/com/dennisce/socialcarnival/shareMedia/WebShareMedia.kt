package com.dennisce.socialcarnival.shareMedia

import android.graphics.Bitmap
import com.dennisce.socialcarnival.shareMedia.ShareMedia

/**
 * @program: SocialCarnival
 * @description:
 * @author:taicheng
 * @create: 19-4-24
 **/

data class WebShareMedia(val webUrl: String, val title: String, val description: String, val thumb: Bitmap): ShareMedia