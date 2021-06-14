package com.bvb.sotp.util

import java.text.NumberFormat
import java.util.*

/**
 * Created by NaPro on 05/01/2017.
 */

fun convertNumberToString(number: Any): String {
    return NumberFormat.getNumberInstance(Locale.getDefault()).format(number)
}
