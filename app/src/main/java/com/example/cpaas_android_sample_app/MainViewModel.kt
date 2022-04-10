package com.example.cpaas_android_sample_app

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cpaasapi.sdk.api.*
import com.cpaasapi.sdk.data.CPaaSAPISettings
import com.cpaasapi.sdk.data.CallOptions
import com.cpaasapi.sdk.utils.Const

class MainViewModel(private val app: Application) : AndroidViewModel(app) {
    val message = MutableLiveData<String>()
    var currentCall: ICall? = null

    fun onRegisterToCpaasPressed(userId: String, cPaaSAPICb: CPaaSAPICb) {
        val settings = CPaaSAPISettings("usstaging.restcomm.com","sid","token","555343456", userId,"PNSTOKEN", Const.WS_URL_AWS)
        CPaaSAPI.register(settings, app.applicationContext, object:
            CPaaSAPICb {
            override fun onIncomingCall(call: ICall) {
                //accept call immediately
                //(maybe you would like to add here UI dialog with accept / reject).
                currentCall = call
                currentCall!!.joinCall()
                message.postValue("GOT CALL")

                cPaaSAPICb.onIncomingCall(call)
            }

            override fun onRegistrationComplete(success: Boolean) {
                message.postValue("RegistrationComplete: $success")
            }
        })
    }

    fun onStartCallPressed(destId: String) {
        currentCall = CPaaSAPI.startCall(
            destinationId = destId,
            callOptions = CallOptions(audio = true)
        )
        //todo goToCall
    }

    //todo portrait
    fun startCallEventListener(listener: ICallEvents) {
        currentCall?.eventListener = object: ICallEvents {
            override fun onConnected() {
                listener.onConnected()
            }

            override fun onRinging() {
                listener.onRinging()
            }

            override fun onConnectedFailure(reason: Reason) {
                message.postValue(reason.toString())
                listener.onConnectedFailure(reason)
            }

            override fun onCallEnd(reason: Reason?) {
                message.postValue(reason?.toString() ?: "Call ended")
                listener.onCallEnd(reason)
            }

            override fun onReconnecting(reason: Reason) {
                //not working yet
            }
        }
    }

    fun onMutePressed(isMute: Boolean) {
        if (isMute) {
            currentCall?.mute {}
        } else {
            currentCall?.unMute {}
        }
    }

    fun onEndPressed() {
        currentCall?.endCall()
    }

}