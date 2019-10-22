package com.kamer.builder

import androidx.appcompat.app.AppCompatActivity
import java.lang.ref.WeakReference


object ActivityHolder {

    private var weakActivity: WeakReference<AppCompatActivity>? = null

    fun getActivity(): AppCompatActivity = weakActivity!!.get()!!

    fun setActivity(activity: AppCompatActivity) {
        weakActivity = WeakReference(activity)
    }

}
