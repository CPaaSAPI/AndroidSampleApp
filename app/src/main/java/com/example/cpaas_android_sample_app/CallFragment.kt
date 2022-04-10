package com.example.cpaas_android_sample_app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.cpaasapi.sdk.api.ICallEvents
import com.cpaasapi.sdk.api.Reason

class CallFragment : Fragment() {
    private lateinit var mainModel: MainViewModel
    var isMicEnable = true // because webrtc track  mute = !enable. keep it by track
    var callStatus: TextView? = null
    var callProgressBar: ProgressBar? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_call, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setView(view)
        registerViewModel()
    }

    private fun setView(view: View) {
        callStatus = view.findViewById(R.id.call_status)
        callProgressBar = view.findViewById(R.id.call_progress)

        view.findViewById<ImageView>(R.id.mute_btn).setOnClickListener {
            isMicEnable = !isMicEnable
            var image = R.drawable.ic_mic
            if (!isMicEnable) {
                image = R.drawable.ic_mic_unmute
            }
            (it as ImageView).setImageDrawable(view.context.getDrawable(image))
            mainModel.onMutePressed(isMicEnable)
        }
        view.findViewById<ImageView>(R.id.end_btn).setOnClickListener {
            mainModel.onEndPressed()
            requireActivity().onBackPressed()
        }
    }

    private fun registerViewModel() {
        // model should be registered
        // main model can send call events to activity.
        mainModel = ViewModelProvider(this).get(MainViewModel::class.java)
        mainModel.startCallEventListener(object: ICallEvents {
            override fun onConnected() {
                callProgressBar?.visibility = View.GONE
                callStatus?.text = "Connected"
            }
            override fun onRinging() {
                callStatus?.text = "Ringing"
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() = CallFragment()
    }
}
