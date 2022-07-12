package com.example.cpaas_android_sample_app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.cpaasapi.sdk.api.ICallEvents
import com.cpaasapi.sdk.api.Reason

/**
 * This Fragment is responsible for the view during an active call
 */
class CallFragment : Fragment() {
    private lateinit var cPaaSModel: CPaaSViewModel
    var mute = false
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
        registerViewModel()
        setView(view)
    }

    private fun registerViewModel() {
        cPaaSModel = ViewModelProvider(requireActivity()).get(CPaaSViewModel::class.java)
        // Listen to call event so we can update UI accordingly
        cPaaSModel.startCallEventListener(object: ICallEvents {
            override fun onConnected() {
                activity?.runOnUiThread {
                    callProgressBar?.visibility = View.GONE
                    callStatus?.text = getString(R.string.connected)
                }
            }
            override fun onRinging() {
                activity?.runOnUiThread {
                    callStatus?.text = getString(R.string.ringing)
                }
            }

            override fun onCallEnd(reason: Reason?) {
                activity?.runOnUiThread {
                    activity!!.onBackPressed()
                }
            }
        })
    }

    private fun setView(view: View) {
        // Call status TextView will show statuses such as 'connecting', 'ringing', 'connected'
        callStatus = view.findViewById(R.id.call_status)
        // Call progress bar showed until initializing the call
        callProgressBar = view.findViewById(R.id.call_progress)

        // Mute/UnMute button enable/disable microphone on local side
        view.findViewById<ImageView>(R.id.mute_btn).setOnClickListener {
            mute = !mute
            var image = R.drawable.ic_mic
            if (mute) {
                image = R.drawable.ic_mic_unmute
            }
            (it as ImageView).setImageDrawable(view.context.getDrawable(image))
            cPaaSModel.onMutePressed(mute)
        }

        // End call button will end call & close call fragment
        view.findViewById<ImageView>(R.id.end_btn).setOnClickListener {
            cPaaSModel.onEndPressed()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = CallFragment()
    }

}
