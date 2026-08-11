package com.rfcoding.deliverytracker

import android.app.Application
import com.rfcoding.deliverytracker.di.mainModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class DeliveryTrackerApp: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@DeliveryTrackerApp)
            modules(mainModule)
        }
    }
}