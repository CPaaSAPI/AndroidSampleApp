package com.example.cpaas_android_sample_app

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.cpaasapi.sdk.api.*
import com.cpaasapi.sdk.api.voice.CPaaSCall
import com.cpaasapi.sdk.api.voice.CPaaSCallEvents
import com.cpaasapi.sdk.api.voice.CPaaSReason
import com.cpaasapi.sdk.data.CPaaSAPISettings
import com.cpaasapi.sdk.data.CallOptions
import com.cpaasapi.sdk.data.ServiceType
import com.cpaasapi.sdk.utils.Const

/**
 * A View Model that demonstrate the usage of CPaaS API calls
 * (such as CPaaSAPI.register(), CPaaSAPI.voice.create(), CPaaSAPI.voice.connect, etc...)
 */
class CPaaSViewModel(private val app: Application) : AndroidViewModel(app) {
    val message = MutableLiveData<String>()
    var currentCall: CPaaSCall? = null

    // To get ACCOUNT_SID & AUTH_TOKEN please visit this link:
    // https://usstaging.restcomm.com/docs/api/overview.html#_authentication
    private val ACCOUNT_SID = "<Account SID from restcomm>"
    private val AUTH_TOKEN = "<Auth Token from restcomm>"



    fun onRegisterToCpaasPressed(callback: CPaaSAPICb) {
        val settings = CPaaSAPISettings(
            customDomain = "webrtc-dev.restcomm.com",
            accountSid = ACCOUNT_SID,
            authToken = AUTH_TOKEN,
            appSid = "ClickToCallDevApp",
            clientId = "YOUR_USER_ID",
            PNSToken = "PNSTOKEN",
            baseURL = Const.HTTP_URL_AWS)
        Log.d("CPAASAPI", "$settings")

        // API initialization, must be called first and once in order to use this API.
        // MavSettings - setting object contains preparations regarding this SDK.
        CPaaSAPI.register(settings, app.applicationContext, object: CPaaSAPICb {
            override fun onIncomingCall(callId: String, callerId: String, serviceType: ServiceType) {
                // Got call from callerId
                handleIncomingCall(callId, callerId, serviceType, callback)
            }

            override fun onRegistrationState(state: REGISTRATION_STATE) {
                val success = state == REGISTRATION_STATE.REGISTERED
                // API initialization was completed successfully
                message.postValue("RegistrationComplete: $success")
            }
        })
    }

    fun handleIncomingCall(
        callId: String,
        callerId: String,
        serviceType: ServiceType,
        callback: CPaaSAPICb
    ) {
        val api = if (serviceType == ServiceType.VOICE) {
            CPaaSAPI.voice
        } else {
            // feature use CPaaSAPI.video
            CPaaSAPI.voice
        }
        api.connect(callId, CallOptions(audio = true)) { result ->
            // Call accepted immediately
            // (You can add here UI dialog with accept / reject).
            result.onSuccess { ICall ->
                currentCall = ICall

                message.postValue("GOT CALL")
                callback.onIncomingCall(callId = callId, callerId = callerId, serviceType = serviceType)
            }

            result.onFailure { error ->
                // handle connection error
                message.postValue("Failed connect call ${error.message}")
            }
        }
    }

    fun onStartCallPressed() {
        // Initialization of call by steps:
        // - voice.create Generate 'callId' and prepare CPaaS SDK to call;
        // - voice.connect Connect caller to created call by 'callId'.
        CPaaSAPI.voice.create { createResult ->
            createResult.onSuccess { callId ->
                // got callId
                CPaaSAPI.voice.connect(callId = callId,
                                callOptions = CallOptions(audio = true))
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

    fun startCallEventListener(listener: CPaaSCallEvents) {
        // Listen to Call events
        currentCall?.eventListener = object: CPaaSCallEvents {
            /**
             * The call was successfully connected,
             * or the process of reconnecting was completed successfully.
             */
            override fun onConnected() {
                listener.onConnected()
            }

            /**
             * Outgoing call is ringing on required destination.
             */
            override fun onRinging() {
                listener.onRinging()
            }

            /**
             * The call failed to connect.
             * Will provide {@link Reason} for more information about what failure occurred.
             */
            override fun onConnectedFailure(reason: CPaaSReason) {
                message.postValue(reason.toString())
                listener.onConnectedFailure(reason)
            }

            /**
             * The call was ended.
             * Can happen for the following reasons:
             ** caller or callee calls 'endCall()' on the 'Call' object
             ** error occurs on the client or the server that terminates the call
             *
             * If the call ends normally `Reason` is null. If the call ends due to an error the `Reason` is non-null.
             */
            override fun onCallEnd(reason: CPaaSReason) {
                message.postValue(reason.toString())
                listener.onCallEnd(reason)
            }

            /**
             * The call is reconnecting and currently unavailable
             * Will provide {@link Reason} for more information about what failure occurred.
             */
            override fun onReconnecting(reason: CPaaSReason) {
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