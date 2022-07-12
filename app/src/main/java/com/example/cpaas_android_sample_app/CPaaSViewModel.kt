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
 * (such as CPaaSAPI.register(), CPaaSAPI.voice.create(), CPaaSAPI.voice.connect, etc...)
 */
class CPaaSViewModel(private val app: Application) : AndroidViewModel(app) {
    val message = MutableLiveData<String>()
    var currentCall: ICall? = null

    // To get ACCOUNT_SID & AUTH_TOKEN please visit this link:
    // https://usstaging.restcomm.com/docs/api/overview.html#_authentication
    private val ACCOUNT_SID = "<Account SID from restcomm>"
    private val AUTH_TOKEN = "<Auth Token from restcomm>"

    fun onRegisterToCpaasPressed(cPaaSAPICb: CPaaSAPICb) {
        val settings = CPaaSAPISettings(
            customDomain = "webrtc-dev.restcomm.com",
            accountSid = ACCOUNT_SID,
            accountToken = AUTH_TOKEN,
            appSid = "ClickToCallDevApp",
            clientId = "YOUR_USER_ID",
            PNSToken = "PNSTOKEN",
            BaseURL = Const.HTTP_URL_AWS)
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

    fun onStartCallPressed() {
        // Initialization of call by steps:
        // - voice.create Generate 'callId' and prepare CPaaS SDK to call;
        // - voice.connect Connect caller to created call by 'callId'.
        CPaaSAPI.voice.create { createResult ->
            createResult.onSuccess { callId ->
                // got callId
                CPaaSAPI.voice.connect(callId = callId,
                                callOptions = CallOptions(audio = true, CallOptionService.P2A))
                { connectCallResult ->
                    connectCallResult.onSuccess { call ->
                        // call created
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