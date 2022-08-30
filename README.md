# AndroidSampleApp
Sample code that demonstrate how to integrate the CPaaSAPI SDK to a kotlin application


# Integration Guide - Getting Started


This guide provides you with an overview of the key objects you will use to build your Voice application using the CPaaSAPI SDK.
The CPaaSAPI SDK exposes voice services that allow you to have a call with a CPaaS application or receive a call from a CPaaS application.


## Prerequisites

### Restcomm account and authentication parameters

1. To add a Click-To-Call capability to your application, you need an active Restcomm account. You can find more info [here](https://www.restcomm.com/docs/getting-started.html)
2. After you sign up with Restcomm, you can find your _Account SID_ and _Auth Token_ by navigating to _Your profile --> Account_ in the Restcomm Console.


### Platform Prerequisites
1. Support for Android API level 21 or higher
2. Support for Java 11

## Getting the CPaaS API SDK

The CPaaS API SDK is available for download in Maven Central.

[Link to the repo]()



### Maven


```kotlin 
implementation ("com.cpaasapi.sdk:1.0.0")
```

### Permission:
Please add the following permissions to ``AndroidManifest.xml``
```
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
    <uses-permission android:name="android.permission.INTERNET" />
```

## API document for the Voice service SDK
[API Document for the Voice Service](voice-api.md)

To start 

## Initializing the SDK

To start using the SDK you should register the SDK by using the `CPaaSAPI.register()` method using the parameters below.

💡 Note: 
* This should be done once in a session
* We recommend to do it as soon as you open the application, it will save you time on the call connection.

```kotlin 
        CPaaSAPI.register(
            CPaaSAPISettings(customDomain = "api.your-organization.com",
                accountSid = "xxxxxxx",
                authToken = "xxxxxxx",
                appSid = "xxxxxxx",
                clientId = "xxxxxxx",
                PNSToken = "xxxxxxx",
                baseURL = "https://xxxxxxx.com"),
            applicationContext, object: CPaaSAPICb {
                override fun onIncomingCall(
                    callId: String,
                    callerId: String,
                    serviceType: ServiceType) {
                    // todo Listening to incoming calls. connect/reject a call with the given callId
                }

                override fun onRegistrationComplete(success: Boolean) {
                    // todo you can start your call here 
                }
            }
        )
```

### Authorization and Web Socket Connection

If the application registration was successful, the following events will take place:

1. Authorization – Our servers will authenticate and authorize the use of the SDK
2. Web socket connection initialization – the SDK will initialize the web socket connection.
3. Once the registration process has completed, you will get a call-back that indicates  if it was successful or not

💡 NOTE: The steps above must be successfully completed. Failure in any of the above procedures will prevent the SDK from working properly.


##  Starting a call

To initiate a call simply call **CPaaSAPI.voice.create(...)** and **CPaaSAPI.voice.connect(...)** .
``create()``: creates and returns a call id that you can use or share with others
``connect()``: starts the actual connection to the call and returns an api call obj.

```kotlin
    CPaaSAPI.voice.create { createResult ->
        createResult.onSuccess { callId ->
            CPaaSAPI.voice.connect(callId, callOptions) { result ->
                result.onSuccess {  call ->
                    currentCall = call
                    // Here you can: 
                    // show call UI
                    // Start listen to call events: currentCall.eventListener = {...}
                }
            }
        }
    }
```

### The CPaaSCall Object

`CPaaSCall` is an API object that return from the ``connect()`` API that lets you perform actions on an *active call* such as muting or ending the call.


```kotlin
call.mute() //mute the current call
call.unmute() //unmute the current call
call.end() //end current call
```

##  Answering an Incoming Call
To answer an incoming call, you need to listen to event: ``CPaaSAPICb.onIncomingCall``.
When there is an incoming call event, you can use ``CPaaSAPI.voice.connect(callId, callOptions)`` to answer it with, using the given callId.


```kotlin
override fun onIncomingCall(
    callId: String,
    callerId: String,
    serviceType: ServiceType) {
    CPaaSAPI.voice.connect(callId, callOptions)
}
```
### Listening to Call Events

You can listen to various call events such as: call connected, call ringing, connection failed, call ended, call reconnecting.

```kotlin
call.eventListener = object: CPaaSCallEvents {
    override fun onConnected() {
        // call connected you have audio here
    }

    override fun onRinging() {
        // callee got your call, wait for his action (accept/reject)
    }

    override fun onConnectedFailure(reason: CPaaSReason) {
        // connection failed, check reason
    }

    override fun onCallEnd(reason: CPaaSReason?) {
        // call end, you can remove call UI
    }

    override fun onReconnecting(reason: CPaaSReason) {
        // call on reconnecting process, show notification for user
    }
}
````

