package com.bvb.sotp.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.View
import androidx.biometric.BiometricManager
import com.bvb.sotp.PeepApp
import com.bvb.sotp.R
import com.bvb.sotp.realm.MobilePushRealmModel
import io.realm.Realm
import org.json.JSONObject
import kotlin.math.abs


class Utils {
    companion object {
        fun setColorBackground(view: View, id: Int) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                view.setBackgroundColor(view.context.getColor(id))
            } else {
                view.setBackgroundColor(view.context.resources.getColor(id))

            }
        }

        fun capitalize(str: String): String? {
            if (TextUtils.isEmpty(str)) {
                return str
            }
            val arr = str.toCharArray()
            var capitalizeNext = true

            val phrase = StringBuilder()
            for (c in arr) {
                if (capitalizeNext && Character.isLetter(c)) {
                    phrase.append(Character.toUpperCase(c))
                    capitalizeNext = false
                    continue
                } else if (Character.isWhitespace(c)) {
                    capitalizeNext = true
                }
                phrase.append(c)
            }

            return phrase.toString()
        }

        fun isValidPincode(str: String): Boolean {

            var array = str.toCharArray()

            var arrayList: ArrayList<Int> = ArrayList()

            for (i in array.indices) {

                var num1 = array[i].toString().toInt()
                arrayList.add(num1)

            }
            var repeat = !isRepeat(arrayList)
            var linear = !isLinear(arrayList)

            return repeat && linear
        }

        fun isRepeat(arr: ArrayList<Int>): Boolean {
            if (arr.isNullOrEmpty()) {
                return false
            }

            var firstChar = arr[0]
            var count = 0
            for (i in 1..arr.size - 1) {

                if (firstChar == arr[i]) {
                    count++;
                }

            }

            if (count == arr.size - 1) {
                return true
            }

            return false
        }

        fun isLinear(arr: ArrayList<Int>): Boolean {

            var arrayList: ArrayList<Int> = ArrayList()

            var firstChar = arr[0]
            var count = 0
            for (i in 1..arr.size - 1) {
                var value = firstChar - arr[i]

                if (abs(value) == 1) {

                    count++
                    arrayList.add(value)

                }

                firstChar = arr[i]
            }
            var checkcount = (count == arr.size - 1)
            var checkrepeat = isRepeat(arrayList)


            if (checkcount && (arrayList.size == arr.size - 1) && checkrepeat) {
                return true
            }

            return false
        }

        fun isAvailable(context: Context): Boolean {
            val biometricManager = BiometricManager.from(context)
            if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {
                if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT))
                    return true
            }
            return false

        }

        fun isAvailableFinger(context: Context): Boolean {
            val biometricManager = BiometricManager.from(context)
            if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS ||
                biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED
            ) {
                if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT))
                    return true
            }
            return false

        }

        fun convertDpToPixel(dp: Float, context: Context): Float {
            return dp * (context.resources
                .displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        }

        fun saveNoti(content: String?, message: String?, type: String, status: String) {
            val realm = Realm.getDefaultInstance()
            val id = PeepApp.mobilePushPrimaryKey!!.getAndIncrement()
            realm.executeTransactionAsync { realm1: Realm ->
                val model = realm1.createObject(
                    MobilePushRealmModel::class.java, id
                )
                model.date = System.currentTimeMillis()
                model.content = content
                model.detail = message
                model.type = type
                model.status = status
            }
        }

        fun saveNotiOther(type: String) {
            val realm = Realm.getDefaultInstance()
            val id = PeepApp.mobilePushPrimaryKey!!.getAndIncrement()
            realm.executeTransactionAsync { realm1: Realm ->
                val model = realm1.createObject(
                    MobilePushRealmModel::class.java, id
                )
                model.date = System.currentTimeMillis()
                model.type = type
            }
        }

        fun getSessionCode(s: String): String {
            var result = ""
            try {
                val ssContent: String = s.substring(s.indexOf("sessioncode=") + 12, s.length)
                result = ssContent.substring(0, s.indexOf("type"))
//                val jObject = JSONObject(content)
//                result = jObject.getString("sessioncode")
            } catch (e: Exception) {

            }
            return result
        }

        fun getTransactionDetail(context: Context, content: String?): String {
            var result = ""
            try {
                val jObject = JSONObject(content)

                if (jObject.has("f1")) {
                    result += "Tài khoản nguồn: " + jObject.getString("f1") + "\n"
                }

                if (jObject.has("f2")) {
                    result += "Tài khoản nhận: " + jObject.getString("f2") + "\n"
                }

                if (jObject.has("f3")) {
                    result += "Người thụ hưởng:: " + jObject.getString("f3") + "\n"
                }

                if (jObject.has("f4")) {
                    result += "Ngân hàng nhận: " + jObject.getString("f4") + "\n"
                }

                if (jObject.has("f5")) {
                    result += "Số tiền: " + jObject.getString("f5") + "\n"
                }

                if (jObject.has("f6")) {
                    result += "Số tiền: " + jObject.getString("f6") + "\n"
                }

                if (jObject.has("f7")) {
                    result += "Ngày hiệu lực: " + jObject.getString("f7") + "\n"
                }

                if (jObject.has("f8")) {
                    result += "Phí dịch vụ: " + jObject.getString("f8") + "\n"
                }

                if (jObject.has("f9")) {
                    result += "Tổng tiền: " + jObject.getString("f9") + "\n"
                }

                if (jObject.has("f10")) {
                    result += "Số CMT/HC: " + jObject.getString("f10") + "\n"
                }

                if (jObject.has("f11")) {
                    result += "Ngày cấp: " + jObject.getString("f11") + "\n"
                }

                if (jObject.has("f12")) {
                    result += "Nơi cấp: " + jObject.getString("f12") + "\n"
                }

                if (jObject.has("f13")) {
                    result += "Số thẻ nhận: " + jObject.getString("f13") + "\n"
                }

                if (jObject.has("f14")) {
                    result += "Loại dịch vụ: " + jObject.getString("f14") + "\n"
                }

                if (jObject.has("f15")) {
                    result += "Nhà cung cấp: " + jObject.getString("f15") + "\n"
                }

                if (jObject.has("f16")) {
                    result += "Số hóa đơn: " + jObject.getString("f16") + "\n"
                }

                if (jObject.has("f17")) {
                    result += "Số tiền cước: " + jObject.getString("f17") + "\n"
                }

                if (jObject.has("f18")) {
                    result += "Số tiền chiết khấu: " + jObject.getString("f18") + "\n"
                }

                if (jObject.has("f19")) {
                    result += "Số tiền thanh toán: " + jObject.getString("f19") + "\n"
                }

                if (jObject.has("f20")) {
                    result += "Số điện thoại: " + jObject.getString("f20") + "\n"
                }

                if (jObject.has("f21")) {
                    result += "Mệnh giá nạp tiền: " + jObject.getString("f21") + "\n"
                }

                if (jObject.has("f22")) {
                    result += "Số tiền chiết khấu: " + jObject.getString("f22") + "\n"
                }

                if (jObject.has("f23")) {
                    result += "Loại thẻ: " + jObject.getString("f23") + "\n"
                }

                if (jObject.has("f24")) {
                    result += "Số thẻ: " + jObject.getString("f24") + "\n"
                }

                if (jObject.has("f25")) {
                    result += "Tên chủ thẻ: " + jObject.getString("f25") + "\n"
                }


                if (jObject.has("f26")) {
                    result += "Dịch vụ: " + jObject.getString("f26") + "\n"
                }

                if (jObject.has("f27")) {
                    result += "Nhà cung cấp: " + jObject.getString("f27") + "\n"
                }

                if (jObject.has("f28")) {
                    result += "Số hợp đồng bảo hiểm: " + jObject.getString("f28") + "\n"
                }

                if (jObject.has("f29")) {
                    result += "Kỳ phí: " + jObject.getString("f29") + "\n"
                }

                if (jObject.has("f30")) {
                    result += "Tên bên mua BH: " + jObject.getString("f30") + "\n"
                }

                if (jObject.has("f31")) {
                    result += "Người được BH: " + jObject.getString("f31") + "\n"
                }

                if (jObject.has("f32")) {
                    result += "Khuyến mãi: " + jObject.getString("f32") + "\n"
                }

                if (jObject.has("f33")) {
                    result += context.getString(R.string.total_amount_transaction) + jObject.getString(
                        "f33"
                    ) + "\n"
                }
                if (jObject.has("f34")) {
                    result += context.getString(R.string.total_amount_money) + jObject.getString("f34") + "\n"
                }
                if (jObject.has("f35")) {
                    result += context.getString(R.string.transaction_date) + jObject.getString("f35") + "\n"
                }

            } catch (e: Exception) {

            }

            return result

        }
    }


}