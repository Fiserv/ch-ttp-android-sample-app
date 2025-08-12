package com.fiserv.commercehub.androidttp.ui.main.screen.viewmodel


import android.content.Context
import androidx.lifecycle.ViewModel
import com.fiserv.commercehub.ttp.provider.FiservTTPCardReader

class ChoiceScreenViewModel() : ViewModel() {

    fun getMagicCubeSDKVersion():String
    {
        return FiservTTPCardReader.getMagicCubeSdkVersion()
    }

    fun getTTPSDKVersion():String
    {
        return  FiservTTPCardReader.getAndroidTTPSdkVersion()
    }


}