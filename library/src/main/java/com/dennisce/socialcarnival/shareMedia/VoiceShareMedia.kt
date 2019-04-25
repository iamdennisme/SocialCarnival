package com.dennisce.socialcarnival.shareMedia

import android.graphics.Bitmap
import com.dennisce.socialcarnival.shareMedia.ShareMedia

/**
 * @program: SocialCarnival
 * @description:
 * @author:taicheng
 * @create: 19-4-24
 **/

data class VoiceShareMedia(var  voiceUrl: String,
                           var title: String,
                           var description: String,
                           var thumb: Bitmap): ShareMedia