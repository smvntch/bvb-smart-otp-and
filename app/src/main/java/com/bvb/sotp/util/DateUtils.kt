package com.bvb.sotp.util

import com.bvb.sotp.Constant
import java.util.*
import java.util.concurrent.TimeUnit
import android.R.attr.y
import android.R.attr.x


class DateUtils {

    fun calculateDelta(time: String): Int {

        var mili = (TimeUnit.SECONDS.toMillis(time.toLong()))
        var now = (System.currentTimeMillis())
        var calendar = Calendar.getInstance()
        calendar.timeInMillis = now
        var stClient = calendar.get(Calendar.SECOND)

        var deltaA = (mili - now) / 1000
        var deltaS = 0
        if (deltaA >= 0) {
            var MST = Constant.Intv - Math.abs(stClient).rem(Constant.Intv)
            var MDA = Math.abs(deltaA).rem(Constant.Intv).toInt()
            if (MST > MDA) {
                deltaS = MDA
            } else {
                deltaS = -1 * (Constant.Intv - Math.abs(deltaA).toInt().rem(Constant.Intv))
            }
        } else {
            var MST = Math.abs(stClient).rem(Constant.Intv)
            var MDA = Math.abs(deltaA).rem(Constant.Intv).toInt()
            if (MST > MDA) {
                deltaS = -1 * (Math.abs(deltaA).toInt().rem(Constant.Intv))

            } else {
                deltaS = (Constant.Intv - Math.abs(deltaA).toInt().rem(Constant.Intv))

            }
        }

        return deltaS
    }

    fun caculateInteval(now: Long, deltaS: Int): Int {
        var calendar = Calendar.getInstance()
        calendar.timeInMillis = now
        var stClient = calendar.get(Calendar.SECOND)

        var mgt = Math.abs(stClient).rem(Constant.Intv)
        var result = 2 * Constant.Intv - deltaS - mgt
        return result
    }

    fun mod(num1: Int, num2: Int): Int {

        val result = num1 % num2
        return if (result < 0) result + num2 else result
    }

    fun calculateDiffDay(time: Long): Int {

        var daysDiff = numDaysBetween(Calendar.getInstance(), time, System.currentTimeMillis())

        return daysDiff
    }

    fun numDaysBetween(c: Calendar, fromTime: Long, toTime: Long): Int {
        var result = 0
        if (toTime <= fromTime) return result

        c.timeInMillis = toTime
        val toYear = c.get(Calendar.YEAR)
        result += c.get(Calendar.DAY_OF_YEAR)

        c.timeInMillis = fromTime
        result -= c.get(Calendar.DAY_OF_YEAR)

        while (c.get(Calendar.YEAR) < toYear) {
            result += c.getActualMaximum(Calendar.DAY_OF_YEAR)
            c.add(Calendar.YEAR, 1)
        }

        return result
    }
}