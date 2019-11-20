package com.example.sounddetect

import android.app.Application
import androidx.lifecycle.LiveData


/**
 * Each game controller should extend such base class
 */
abstract class GameController(application: Application) {

    abstract fun subscribeUpdates(): LiveData<GameEvents>

    abstract fun unSubscribeUpdates()

    abstract fun stopDetection()

    abstract fun startDetection()

    abstract fun setMinScreamLimit(minScreamLimit: Int)

    abstract fun getMaxVolumeLiveData(): LiveData<String>

}