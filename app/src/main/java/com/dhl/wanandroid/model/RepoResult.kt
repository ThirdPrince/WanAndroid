package com.dhl.wanandroid.model

import android.text.TextUtils

/**
 * @Title: RepoResult
 * @Package $
 * @Description: 包装网络请求成功与失败的情况
 * @author $
 * @date 2022 12 23
 * @version V2.0
 */
class RepoResult<T> {

    var result: T? = null
    var errorMessage: String? = null

    constructor(result: T, errorMessage: String) {
        this.result = result
        this.errorMessage = errorMessage
    }

    constructor(errorMessage: String) {
        this.errorMessage = errorMessage
    }

    val isSuccess: Boolean
        get() = TextUtils.isEmpty(errorMessage)

}