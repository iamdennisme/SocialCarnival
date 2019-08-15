package com.dennisce.socialcarnival.pay

data class WechatPayOrder(
    val packageValue: String,
    val appId: String,
    val nonceStr: String,
    val partnerId: String,
    val prepayId: String,
    val sign: String,
    val timeStamp: String
):PayInfo