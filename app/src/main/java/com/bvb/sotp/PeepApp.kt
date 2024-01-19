package com.bvb.sotp

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.multidex.MultiDex
import com.bvb.sotp.realm.MobilePushRealmModel
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults
import java.util.concurrent.atomic.AtomicLong

class PeepApp : Application() {

    var mLastPause: Long = 0

    companion object {
        public var mobilePushPrimaryKey: AtomicLong? = null

    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)

    }

    override fun onCreate() {
        super.onCreate()
        mLastPause = 0;
        Log.w("Application", "Launch");
        initRealm()

    }

    private fun initRealm() {
        Realm.init(this)
        val config = RealmConfiguration.Builder()
            .name("bvb.realm")
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(config)
        val realm = Realm.getInstance(config)
        try {
            //Attempt to get the last id of the last entry in the Quote class and use that as the
            //Starting point of your primary key. If your Quote table is not created yet, then this
            //attempt will fail, and then in the catch clause you want to create a table
            mobilePushPrimaryKey =
                AtomicLong(
                    (realm.where(
                        MobilePushRealmModel::class.java
                    ).max("id")?.toLong() ?: 0) + 1
                )
        } catch (e: Exception) {
            //All write transaction should happen within a transaction, this code block
            //Should only be called the first time your app runs
            realm.beginTransaction()
            //Create temp Quote so as to create the table
            val quote: MobilePushRealmModel =
                realm.createObject(MobilePushRealmModel::class.java, 0)
            //Now set the primary key again
            mobilePushPrimaryKey =
                AtomicLong(
                    (realm.where(
                        MobilePushRealmModel::class.java
                    ).max("id")?.toLong() ?: 0) + 1
                )
            //remove temp quote
            val results: RealmResults<MobilePushRealmModel> =
                realm.where(MobilePushRealmModel::class.java).equalTo("id", 0L).findAll()
            results.deleteAllFromRealm()
            realm.commitTransaction()
        }

    }
}