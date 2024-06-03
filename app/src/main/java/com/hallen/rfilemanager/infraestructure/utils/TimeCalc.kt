package com.hallen.rfilemanager.infraestructure.utils

import com.orhanobut.logger.Logger
import kotlin.system.measureTimeMillis

class TimeCalc {
    companion object {
        fun calc(function: () -> Unit): Any? {
            var returnVal: Any?
            val time = measureTimeMillis {
                returnVal = function()
            }
            Logger.i("TIME: $time")
            return returnVal
        }
    }

}