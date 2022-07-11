package com.example.cpaas_android_sample_app

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.cpaasapi.sdk.api.*
import com.cpaasapi.sdk.data.CPaaSAPISettings
import com.cpaasapi.sdk.data.CallOptionService
import com.cpaasapi.sdk.data.CallOptions
import com.cpaasapi.sdk.data.ServiceType
import com.cpaasapi.sdk.utils.Const

/**
 * A View Model that demonstrate the usage of CPaaS API calls
 * (such as CPaaSAPI.register(), CPaaSAPI.startCall(), call.eventListener, etc...)
 */
class CPaaSViewModel(private val app: Application) : AndroidViewModel(app) {
    val message = MutableLiveData<String>()
    var currentCall: ICall? = null

    // TODO!!! Remove it before publishing
    private val COGNITO_USERNAME = "AC3c5b4177e5fdd813720bc0d6dd7f057e"
    private val COGNITO_PASSWORD = "c8b0fca2c59d9198b641ce60fe9b501b"

    fun onRegisterToCpaasPressed(cPaaSAPICb: CPaaSAPICb) {
        val settings = CPaaSAPISettings("webrtc-dev.restcomm.com",COGNITO_USERNAME,COGNITO_PASSWORD,"ClickToCallDevApp", "YOUR_USER_ID","PNSTOKEN", Const.HTTP_URL_AWS)
        Log.d("CPAASAPI", "$settings")

        // API initialization, must be called first and once in order to use this API.
        // MavSettings - setting object contains preparations regarding this SDK.
        CPaaSAPI.register(settings, app.applicationContext, object: CPaaSAPICb {
            override fun onIncomingCall(callId: String, callerId: String, serviceType: ServiceType) {
                // Got call from callerId
                val api = if (serviceType == ServiceType.VOICE) {
                    CPaaSAPI.voice
                } else {
                    // feature use CPaaSAPI.video
                    CPaaSAPI.voice
                }
                api.connect(callId, CallOptions(audio = true, CallOptionService.P2A)) { result ->
                    result.onSuccess { ICall ->
                        currentCall = ICall

                        message.postValue("GOT CALL")
                        cPaaSAPICb.onIncomingCall(callId = callId, callerId = callerId, serviceType = serviceType)
                    }

                    result.onFailure { error ->
                        // handle connection error
                        message.postValue("Failed connect call ${error.message}")
                    }
                }
            }

            override fun onRegistrationComplete(success: Boolean) {
                // API initialization was completed successfully
                message.postValue("RegistrationComplete: $success")
            }
        })
    }

    fun onStartCallPressed(destId: String) {
        // Start a call to destId. creates and returns a new call object that represents this call.
        CPaaSAPI.voice.create { createResult ->
            createResult.onSuccess { callId ->
                CPaaSAPI.voice.connect(callId = callId,
                                callOptions = CallOptions(audio = true, CallOptionService.P2A))
                { connectCallResult ->
                    connectCallResult.onSuccess { call ->
                        currentCall = call
                    }

                    connectCallResult.onFailure { error ->
                        message.postValue("Failed connect call ${error.message}")
                    }
                }
            }

            createResult.onFailure { createError ->
                // handle create call error
                message.postValue("Failed create call ${createError.message}")
            }
        }
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