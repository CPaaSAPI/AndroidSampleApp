package com.example.cpaas_android_sample_app

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.cpaasapi.sdk.api.*
import com.cpaasapi.sdk.data.CPaaSAPISettings
import com.cpaasapi.sdk.data.CallOptions
import com.cpaasapi.sdk.utils.Const

/**
 * A View Model that demonstrate the usage of CPaaS API calls
 * (such as CPaaSAPI.register(), CPaaSAPI.startCall(), call.eventListener, etc...)
 */
class CPaaSViewModel(private val app: Application) : AndroidViewModel(app) {
    val message = MutableLiveData<String>()
    var currentCall: ICall? = null

    fun onRegisterToCpaasPressed(cPaaSAPICb: CPaaSAPICb) {
        val settings = CPaaSAPISettings("webrtc-dev.restcomm.com","sid","token","ClickToCallDevApp", "YOUR_USER_ID","PNSTOKEN", Const.WS_URL_AWS)
        Log.d("CPAASAPI dudu", "$settings")

        // API initialization, must be called first and once in order to use this API.
        // MavSettings - setting object contains preparations regarding this SDK.
        CPaaSAPI.register(settings, app.applicationContext, object:
            CPaaSAPICb {
            override fun onIncomingCall(call: ICall) {
                // Here we accept the call immediately
                // (maybe you would like to add here UI dialog with accept / reject).
                currentCall = call
                currentCall!!.joinCall()

                message.postValue("GOT CALL")
                cPaaSAPICb.onIncomingCall(call)
            }

            override fun onRegistrationComplete(success: Boolean) {
                // API initialization was completed successfully
                message.postValue("RegistrationComplete: $success")
            }
        })
    }

    fun onStartCallPressed(destId: String) {
        // Start a call to destId. creates and returns a new call object that represents this call.
        currentCall = CPaaSAPI.startCall(
            destinationId = destId,
            callOptions = CallOptions(audio = true)
        )
    }

    fun startCallEventListener(listener: ICallEvents) {
        // Listen to Call events
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
                message.postValue(reason?.toString() ?: "CALL ENDED")
                listener.onCallEnd(reason)
            }

            override fun onReconnecting(reason: Reason) {
                listener.onReconnecting(reason)
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