package com.dennisce.socialcarnival

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.blankj.utilcode.util.Utils
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun JSONObject.jsonToMap(): Map<String, String> {
    val map = HashMap<String, String>()
    val iterator = this.keys()

    while (iterator.hasNext()) {
        val var4 = iterator.next() as String
        map[var4] = this.opt(var4).toString()
    }
    return map
}

fun Bitmap.bitmap2Bytes(): ByteArray {
    ByteArrayOutputStream().let{
        this.compress(Bitmap.CompressFormat.PNG, 100, it)
        return it.toByteArray()
    }
}

fun ByteArray.compressBitmap(byteCount: Int): ByteArray {
    var isFinish = false
    if (this.size > byteCount) {
        val outputStream = ByteArrayOutputStream()
        val tmpBitmap = BitmapFactory.decodeByteArray(this, 0, this.size)
        var times = 1
        var percentage: Double

        while (!isFinish && times <= 10) {
            percentage = Math.pow(0.8, times.toDouble())
            val compressData = (100.0 * percentage).toInt()
            tmpBitmap.compress(Bitmap.CompressFormat.JPEG, compressData, outputStream)
            if (outputStream.size() < byteCount) {
                isFinish = true
            } else {
                outputStream.reset()
                ++times
            }
        }
        val outputStreamByte = outputStream.toByteArray()
        if (!tmpBitmap.isRecycled) {
            tmpBitmap.recycle()
        }
        return outputStreamByte
    }

    return this
}

fun Bitmap.saveBitmap(path:String): File? {
    var out: FileOutputStream? = null
    try {
        out = FileOutputStream(path)
        this.compress(Bitmap.CompressFormat.PNG, 100, out) // bmp is your Bitmap INSTANCE
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    } finally {
        try {
            out?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return File(path)
}
