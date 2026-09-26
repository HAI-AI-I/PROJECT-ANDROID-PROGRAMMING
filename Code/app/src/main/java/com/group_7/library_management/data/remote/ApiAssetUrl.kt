package com.group_7.library_management.data.remote

import com.group_7.library_management.BuildConfig

fun String?.toApiAssetUrl(): String? {
    val path = this?.trim()?.takeIf(String::isNotEmpty) ?: return null
    if (
        path.startsWith("http://", ignoreCase = true) ||
        path.startsWith("https://", ignoreCase = true) ||
        path.startsWith("content://", ignoreCase = true) ||
        path.startsWith("file://", ignoreCase = true)
    ) {
        return path
    }

    return "${BuildConfig.API_BASE_URL.trimEnd('/')}/${path.trimStart('/')}"
}
