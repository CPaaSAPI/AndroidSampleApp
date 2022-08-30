# CPaaS Android SDK API Reference

## API Entries
| Name              | Description                                                                                                      |
|-------------------|------------------------------------------------------------------------------------------------------------------|
| [CPaaSAPI](#cpaasapi)       | Main API Object                                                                                                  |
| [IVoice](#ivoice)           | Voice Service that allow you to have a call with CPaaS application or receiving a call from a CPaaS application. |
| [CPaaSCall](#cpaascall)     | Preform Actions on an active call


\
&nbsp;
\
&nbsp;
## <a name="cpaasapi"></a>CPaaSAPI
CPaaSAPI is your entry point to the CPaaSAPI SDK.
### <a name="register"></a>register
To start using the SDK you should register the SDK by using this function
```kotlin
fun register(settings: CPaaSAPISettings, appContext: Context, cpaasAPICb: CPaaSAPICb)
```
| Name           | Description                                                                    |
|----------------|--------------------------------------------------------------------------------|
| settings       | [CPaaSAPISettings](#cpaasapisettings) object contains parameters needed for registration|
| appContext     | Android application context                                                      |
| cpaasAPICb     | [CPaaSAPICb](#cpaasapicb) is a listener for a general events                     |
### logout
This will clear all objects and unregister from receiving further incoming call
```kotlin
fun logout(appContext: Context)
```
| Name           | Description                                                                    |
|----------------|--------------------------------------------------------------------------------|
| appContext     | Android application context                                                      |
### <a name="getfileloguri"></a>getFileLogUri
Call this when you want to get the log file
```kotlin
fun getFileLogUri(appContext: Context, result: (CPaaSResult<File, FileNotExistError>) -> Unit)
```
| Name           | Description                                                                    |
|----------------|--------------------------------------------------------------------------------|
| appContext     | Android application context                                              |
| result         | This parameter is of type [CPaaSResult](#cpaasresult) that will return a log file if the request was successful or a [FileNotExistError](#filenotexisterror) if something went wrong.
### voice
This interface of type [IVoice](#ivoice) is your entry point to all voice relevant methods.
```kotlin
var voice: IVoice
```


\
&nbsp;
\
&nbsp;
## <a name="ivoice"></a>IVoice
This interface is your entry point for handling all voice relevant methods.
### create
Creates a callId that can be used to start a call or share it with others
```kotlin
fun create(result: (CPaaSResult<String, CallStartError.NotRegisteredError>) -> Unit)
```
| Name           | Description                                                                    |
|----------------|--------------------------------------------------------------------------------|
| result         | This parameter is of type [CPaaSResult](#cpaasresult) that will return a callId if the request was successful or a [CallStartError](#callstarterror) if something went wrong.
### <a name="connect"></a>connect
Call this when you want to start or join a call.
```kotlin
fun connect(callId: String, callOptions: CallOptions, result: (CPaaSResult<CPaaSCall, CallStartError>) -> Unit)
```
| Name           | Description                                                                    |
|----------------|--------------------------------------------------------------------------------|
| callId         | The callId of the call, this id can be retrieved from create, incomingCall or from sharing
| callOptions    | This parameter is of type [CallOptions](#calloptions) to configure some settings when starting the call
| result         | This parameter is of type [CPaaSResult](#cpaasresult) that will return a [CPaaSCall](#cpaascall) if the request was successful or a [CallStartError](#callstarterror) if something went wrong.
### reject
Use this when you want to reject an incoming call.
```kotlin
fun reject(callId: String)
```
| Name           | Description                                                                    |
|----------------|--------------------------------------------------------------------------------|
| callId         | The callId of the call you want to reject                                      |
### getExistingCall
Returns an existing [CPaaSCall](#cpaascall) by `callId` or `null` if the call doesn't exists.
```kotlin
fun getExistingCall(callId: String): CPaaSCall?
```
| Name           | Description                                                                    |
|----------------|--------------------------------------------------------------------------------|
| callId         | The callId of the call you want to retrieve.                                   |  

\
&nbsp;
\
&nbsp;
## <a name="cpaascall"></a>CPaaSCall
A CPaaSCall object that contains all active call-related actions.
### endCall
Closes the active call.
```kotlin
fun endCall()
```
### getCallId
Returns this call id.
```kotlin
fun getCallId(): String
```
### mute
Mutes yourself, will return a closure with a Boolean to determine if the operation was successful.
```kotlin
fun mute(completion: (success: Boolean) -> Unit)
```
### unMute
UnMutes yourself, will return a closure with a Boolean to determine if the operation was successful.
```kotlin
fun unMute(completion: (success: Boolean) -> Unit)
```
### eventListener
This interface of type [CPaaSCallEvents](#cpaascallevents) should be set to receive events on the call.
```kotlin
var eventListener: CPaaSCallEvents?
```
\
&nbsp;
\
&nbsp;
## Helper Objects
### <a name="cpaascallevents"></a>CPaaSCallEvents
The CPaaSCall supports listening to various call events such as: call connected, call ringing, connection failed, call ended and call reconnecting.
use it via CPaaSCall.eventListener
#### onCallEnd
The call ended and you can remove the call UI.
```kotlin
fun onCallEnd(reason: CPaaSReason)
```
#### onConnected
The call is connected and you have open channel for sending and receiving audio.
```kotlin
fun onConnected()
```
#### onConnectedFailure
The connection failed. You can check the failure [reason](#cpaasreason).
```kotlin
fun onConnectedFailure(reason: CPaaSReason)
```
#### onReconnecting
The call is in a process of reconnecting and you can show a notification to the user.
```kotlin
fun onReconnecting(reason: CPaaSReason)
```
#### onRinging
The Callee received your call. You have to wait for them to accept or reject the call.
```kotlin
fun onRinging()
```
\
&nbsp;
### <a name="cpaasapisettings"></a>CPaaSAPISettings
This class has all parameters needed for registration
```kotlin
fun init(customDomain: String, accountSid: String, authToken: String, appSid: String, clientId: String, PNSToken: String, baseURL: String)
```
| Name           | Description                                                                    |
|----------------|--------------------------------------------------------------------------------|
| customDomain   | The base domain of your CPaaS organization relative to *api.your-company.com* |
| accountSid     | A string that uniquely identifies this account.                                |
| authToken      | The secret authorization token for this account                                |
| appSid         | The string that uniquely identifies this application.                          |
| clientId       | The string that uniquely identifies this client.                               |
| PNSToken       | Push notification server token                                                 |
| baseURL        | Base AWS URL

Parameters can be taken from here: (https://usstaging.restcomm.com/docs/api/overview.html#_authentication)

\
&nbsp;
### <a name="calloptions"></a>CallOptions
This object is used when connecting to a call, you can determine the beginning status of the call with this.

| Name           | Description                                                                    |
|----------------|--------------------------------------------------------------------------------|
| audio          | You can determine if the call will start with the user muted or not |

\
&nbsp;
### <a name="cpaasresult"></a>CPaaSResult
An Object for receiving asynchronous results
#### onSuccess
If the operation was successful this closure will be invoked with the parameter that was set at declaration.
```kotlin
fun onSuccess(action: (value: T) -> Unit): CPaaSResult<T,TException>
```
#### onFailure
If the operation failed this closure will be invoked with the error [type](#cpaaserror) that was set at declaration.
```kotlin
fun onFailure(action: (exception: TException) -> Unit): CPaaSResult<T,TException>
```
#### fold
With this method you will listen to ```onSuccess``` and ```onFailure``` closures in same method.
```kotlin
fun fold(onSuccess: (value: T) -> Unit, onFailure: (exception: TException) -> Unit): Unit?
```
| Name           | Description                                                                    |
|----------------|--------------------------------------------------------------------------------|
| isSuccess      | You can ask CPaaSResult if the operation succeeded                             |
| isFailure      | You can ask CPaaSResult if the operation failed                                |

\
&nbsp;
### <a name="cpaasapicb"></a>CPaaSAPICb
You set this CallBack obj on registration method, it will add a listener for a general events.
#### onIncomingCall
Called when there is an incoming call
```kotlin
fun onIncomingCall(callId: String, callerId: String, serviceType: ServiceType)
```
| Name           | Description                                                                    |
|----------------|--------------------------------------------------------------------------------|
| callId         | The id that identifies the call                                                |
| callerId       | The caller id                                                                 |              
| serviceType    | The [ServiceType](#servicetype) of call you received, this will be needed to respond with correct API
#### onRegistrationComplete
Indicates about the completion of the registration process.
```kotlin
fun onRegistrationComplete(success: Boolean)
```
| Name           | Description                                                                    |
|----------------|--------------------------------------------------------------------------------|
| success        | Determine if the registration was successful                                   |
> **_NOTE:_**  Even if registration was successful you should continue listening to this event as it may still fail for various reasons.

\
&nbsp;
### <a name="cpaasreason"></a>CPaaSReason
The reason why a call ended, failed or is in reconnecting process.

| Name   | Description                                                                                                                                    |
|--------|------------------------------------------------------------------------------------------------------------------------------------------------|
| busy         | Server issue
| reject       | User reject the call
| badRequest   | Missing parameters
| unauthorized | There is an issue with your token, please check that your account was set correctly and parameters were passed correctly           
| notFound     | Server issue
| gone         | Server issue
| timeOut      | Failed to receive response from the server
| rtpLost      | The rtp was lost, we are trying to reconnect
| network      | Call ended as result of a network issue
| byUser       | When the user requested to end the call
| byServer     | When the callee ended the call

\
&nbsp;
### <a name="servicetype"></a>ServiceType
When receiving an incoming call, you can know with this enum the type of the call and which API should be referenced.

| Name   | Description                                                                                                                                    |
|--------|------------------------------------------------------------------------------------------------------------------------------------------------|
| voice         |    We are in a voice call, you should use ```CPaaSAPI.voice``` to connect or reject the call          |
| video         |      TBD         |

\
&nbsp;
### <a name="cpaaserror"></a>CPaaS Error
To assist you in getting back on track if you run into problems, we've given the following Errors Information. This information can also be helpful for submitting logs and providing details to CPaaS Support.
#### <a name="CallStartError"></a>CallStartError
Errors that can be returned as failure to [connect()](#connect).
##### ConnectionError
Can't connect to this callId. Unexpected connection issue prevent the call from being established.
##### MicrophonePermissionError
Microphone access denied. You should encourage the user to grant Microphone permissions.
##### NotRegisteredError
Call cannot be initiated without registration. can be solved by [registering](#register).
##### CallAlreadyExistError
This callId has already been connected. there is already a [CPaaSCall](#cpaascall) object that represents this call.
#### <a name="filenotexisterror"></a>FileNotExistError
Error that can be returned as failure to [getFileLogUri()](#getfileloguri) and indicates that the required file does not exist. 