package com.example.cpaas_android_sample_app

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.cpaasapi.sdk.api.CPaaSAPICb
import com.cpaasapi.sdk.api.ICall

class InfoFragment : Fragment() {
    private lateinit var mainModel: MainViewModel
    private lateinit var goToCallListener: GoToCallViewListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainModel = ViewModelProvider(this).get(MainViewModel::class.java)
        setView(view)
    }

    private fun setView(view: View) {
        val userId = view.findViewById<EditText>(R.id.et_userid).text.toString()
        val destId = view.findViewById<EditText>(R.id.et_destId).text.toString()

        view.findViewById<ImageView>(R.id.btn_register).setOnClickListener {
            onRegisterToCpaasPressed(userId)
        }
        view.findViewById<ImageView>(R.id.btn_call).setOnClickListener {
            onStartCallPressed(destId)
        }
    }

    private fun onRegisterToCpaasPressed(userId: String) {
        mainModel.onRegisterToCpaasPressed(userId, object : CPaaSAPICb {
            override fun onIncomingCall(call: ICall) {
                goToCallListener.goToCallView()
            }

            override fun onRegistrationComplete(success: Boolean) {
                //Register complete
            }
        })
    }

    private fun onStartCallPressed(destId: String) {
        mainModel.onStartCallPressed(destId)
        goToCallListener.goToCallView()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        goToCallListener = activity as GoToCallViewListener
    }

    companion object {
        @JvmStatic
        fun newInstance() = InfoFragment()
    }

    interface GoToCallViewListener {
        fun goToCallView()
    }
}