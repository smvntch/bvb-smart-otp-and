package com.bvb.sotp.helper

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import com.bvb.sotp.util.Cryptography
import java.util.*

class PreferenceHelper(context: Context) {

    companion object {
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_REFRESH_TOKEN = "refresh_token"
        const val KEY_USER = "user"
        const val KEY_DEVICE_TOKEN = "device_token"
        const val KEY_DEVICE_TOKEN_UPLOADED = "device_token_uploaded"
        const val KEY_USER_PROGRESS = "user_progress"
        const val KEY_NEED_SHOW_TUTORIAL_STORY = "need_show_tutorial_story"
        const val KEY_NEED_SHOW_ACCEPT_PUSH = "need_show_accept_push"
        const val KEY_NEED_SHOW_TUTORIAL_PROFILE = "need_show_tutorial_profile"
        const val KEY_FIRST_TIME = "is_first"
        const val KEY_LANGUAGE = "language"
        const val KEY_ENTER_PIN_CODE_FAIL = "pincode_fail"
        const val KEY_ENTER_PIN_CODE_FAIL_LAST_TIME = "pincode_fail_last_time"
        const val KEY_BIOMETRIC = "biometric"
        const val KEY_GPS = "gps"
        const val KEY_SESSION = "session"
        const val KEY_NAME = "name"
        const val KEY_IS_NOTIFICATION = "is_notification"
        const val KEY_IS_LOCKED = "is_locked"
        const val KEY_IS_MIGRATE = "is_migrate"
        const val KEY_DELTA = "delta"
        const val KEY_LAST_SET_PIN = "last_set_pin"
        const val KEY_LOGIN_MIGRATE = "login_migrate"
        const val KEY_HID = "hid"
        const val KEY_ACCOUNT_ID = "account_id"

    }

    private val preferences by lazy {
        Preferences(context)
    }

    fun getAccessToken(): String? {
        return preferences[KEY_ACCESS_TOKEN]
    }

    fun getRefreshToken(): String? {
        return preferences[KEY_REFRESH_TOKEN]
    }


    fun needShowTutorialStory(): Boolean? {
        return preferences[KEY_NEED_SHOW_TUTORIAL_STORY, true]
    }

    fun setNoNeedShowTutorialStory() {
        preferences[KEY_NEED_SHOW_TUTORIAL_STORY] = false
    }

    fun setDeviceToken(token: String?) {
        preferences[KEY_DEVICE_TOKEN] = token
    }

    fun getDeviceToken(): String? {
        return preferences[KEY_DEVICE_TOKEN]
    }

    fun setBiometricLogin(token: Boolean) {
        preferences[KEY_BIOMETRIC] = token
    }

    fun getBiometricLogin(): Boolean? {
        return preferences[KEY_BIOMETRIC, false]
    }

    fun setGpsLocation(token: Boolean) {
        preferences[KEY_GPS] = token
    }

    fun getGpsLocation(): Boolean? {
        return preferences[KEY_GPS, false]
    }

    fun setDeviceTokenUploaded(uploaded: Boolean) {
        preferences[KEY_DEVICE_TOKEN_UPLOADED] = uploaded
    }

    fun isDeviceTokenUploaded(): Boolean {
        return preferences[KEY_DEVICE_TOKEN_UPLOADED, false]!!
    }

    fun needShowAcceptPush(): Boolean {
        return preferences[KEY_NEED_SHOW_ACCEPT_PUSH, true]!!
    }

    fun setNoNeedShowAcceptPush() {
        preferences[KEY_NEED_SHOW_ACCEPT_PUSH] = false
    }

    fun needShowTutorialProfile(): Boolean {
        return preferences[KEY_NEED_SHOW_TUTORIAL_PROFILE, true]!!
    }

    fun setNoNeedShowTutorialProfile() {
        preferences[KEY_NEED_SHOW_TUTORIAL_PROFILE] = false
    }


    fun setFirstTime() {
        preferences[KEY_FIRST_TIME] = 1
    }

    fun isFirstTime(): Boolean {
        return (preferences[KEY_FIRST_TIME, 0] == 0)
    }

    fun setLang(lang: String) {
        preferences[KEY_LANGUAGE] = lang
    }

    fun getLang(): String {
        return preferences[KEY_LANGUAGE, "vi"].toString()
    }

    fun setPincodeFail(time: Int) {
        preferences[KEY_ENTER_PIN_CODE_FAIL] = time
    }

    fun setPincodeFailLastTime(time: Long) {
        preferences[KEY_ENTER_PIN_CODE_FAIL_LAST_TIME] = time
    }

    fun getPincodeFail(): Int {
        return preferences[KEY_ENTER_PIN_CODE_FAIL, 0]!!

    }

    fun getPincodeFailLastTime(): Long {
        return preferences[KEY_ENTER_PIN_CODE_FAIL_LAST_TIME, 0]!!

    }

    fun setSession(time: String) {
        preferences[KEY_SESSION] = time
    }

    fun getSession(): String {
        return preferences[KEY_SESSION, ""]!!

    }

    fun setName(name: String?) {
        preferences[KEY_NAME] = name
    }

    fun getName(): String {
        return preferences[KEY_NAME, ""]!!

    }

    fun setAccountId(name: String) {
        preferences[KEY_ACCOUNT_ID] = name
    }

    fun getAccountId(): String {
        return preferences[KEY_ACCOUNT_ID, ""]!!

    }

    fun setIsNotification(name: Boolean) {
        preferences[KEY_IS_NOTIFICATION] = name
    }

    fun getIsNotification(): Boolean {
        return preferences[KEY_IS_NOTIFICATION, false]!!

    }

    fun setLocked(name: Boolean) {
        preferences[KEY_IS_LOCKED] = name
    }

    fun getLocked(): Boolean {
        return preferences[KEY_IS_LOCKED, false]!!
    }

    fun setMigrate(name: Boolean) {
        preferences[KEY_IS_MIGRATE] = name
    }

    fun isMigrate(): Boolean {
        return preferences[KEY_IS_MIGRATE, false]!!
    }

    fun getOldPref(activity: Activity, code: String): String {

        var sharedPref = activity.getSharedPreferences("smvn.centagate.PrefCode", Context.MODE_PRIVATE)
        var data = sharedPref.getString(code, "")
        if (TextUtils.isEmpty(data)) {
            return ""
        }
        var cryptography = Cryptography(activity)
        var passcode = cryptography.decryptData(data)

        return passcode
    }

    fun getOldPrefNoneDecrypt(activity: Activity, code: String): String {

        var sharedPref = activity.getSharedPreferences("smvn.centagate.PrefCode", Context.MODE_PRIVATE)
        var passcode = sharedPref.getString(code, "")
        return passcode.toString()
    }

    fun setDelta(time: Int) {
        preferences[KEY_DELTA] = time
    }

    fun getDelta(): Int {
        return preferences[KEY_DELTA, 0]!!

    }

    fun setLastChangePin(time: Long) {
        preferences[KEY_LAST_SET_PIN] = time
    }

    fun getLastChangePin(): Long {
        return preferences[KEY_LAST_SET_PIN, 0]!!

    }

    fun setLoginMigrate(name: Boolean) {
        preferences[KEY_LOGIN_MIGRATE] = name
    }

    fun isLoginMigrate(): Boolean {
        return preferences[KEY_LOGIN_MIGRATE, false]!!
    }

    fun setHid(time: String) {
        preferences[KEY_HID] = time
    }

    fun getHid(): String {
        return preferences[KEY_HID, ""]!!

    }
    fun setPincodeLoginLastTime(time: Long) {
        preferences[KEY_ENTER_PIN_CODE_FAIL_LAST_TIME] = time
    }


    fun getPincodeLoginLastTime(): Long {
        return preferences[KEY_ENTER_PIN_CODE_FAIL_LAST_TIME, 0]!!

    }
}