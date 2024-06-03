package com.hallen.rfilemanager.model

import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable

data class LaunchActivity(var image: Drawable, var textIdList: String, var launchable: ResolveInfo)