package com.bvb.sotp.bus

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class RxBus {

    companion object {
        var instance: RxBus? = null
        fun get(): RxBus {
            instance?.let {
                return it
            }
            instance = RxBus()
            return instance!!
        }
    }

    private val bus: PublishSubject<Any> = PublishSubject.create()

    fun send(data: Any) {
        bus.onNext(data)
    }

    fun toObservable(): Observable<Any> {
        return bus
    }
}