package com.projects.aldajo92.jetsonbotunal.main

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.projects.aldajo92.jetsonbotunal.R
import com.projects.aldajo92.jetsonbotunal.api.SharedPreferencesManager
import kotlinx.android.synthetic.main.dialog_config.view.button_restart
import kotlinx.android.synthetic.main.dialog_config.view.editText_local_ip
import kotlinx.android.synthetic.main.dialog_config.view.editText_remote
import kotlinx.android.synthetic.main.dialog_config.view.radio_remote
import kotlinx.android.synthetic.main.dialog_config.view.radio_local_ip

class ConfigurationDialog : DialogFragment() {

    private val sharedPreferencesManager by lazy { SharedPreferencesManager(requireContext()) }

    lateinit var layoutView : View

    override fun onCreateDialog(savedInstanceState: Bundle?) = activity?.let {
        val builder = AlertDialog.Builder(it)
        // Get the layout inflater
        val inflater = requireActivity().layoutInflater

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        layoutView = inflater.inflate(R.layout.dialog_config, null)
        builder.setView(layoutView)

        when (sharedPreferencesManager.getStoredIPConfiguration()) {
            SharedPreferencesManager.IPConfigurations.LOCAL_IP -> layoutView.radio_local_ip.isChecked =
                true
            SharedPreferencesManager.IPConfigurations.REMOTE -> layoutView.radio_remote.isChecked = true
        }

        layoutView.editText_local_ip.setText(
            sharedPreferencesManager.getBaseUrl(SharedPreferencesManager.IPConfigurations.LOCAL_IP)
                ?: ""
        )
        layoutView.editText_remote.setText(
            sharedPreferencesManager.getBaseUrl(SharedPreferencesManager.IPConfigurations.REMOTE)
                ?: ""
        )

        layoutView.radio_local_ip.setOnClickListener { v ->
            onRadioButtonClicked(v)
        }
        layoutView.radio_remote.setOnClickListener { v ->
            onRadioButtonClicked(v)
        }
        layoutView.button_restart.setOnClickListener { v ->
            onButtonClicked(v)
        }
        builder.create()
    } ?: throw IllegalStateException("Activity cannot be null")

    private fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked
            // Check which radio button was clicked
            when (view.getId()) {
                R.id.radio_local_ip -> if (checked)
                    sharedPreferencesManager.saveIPConfiguration(SharedPreferencesManager.IPConfigurations.LOCAL_IP)
                R.id.radio_remote -> if (checked)
                    sharedPreferencesManager.saveIPConfiguration(SharedPreferencesManager.IPConfigurations.REMOTE)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedPreferencesManager.saveBaseURL(
            SharedPreferencesManager.IPConfigurations.REMOTE,
            layoutView.editText_remote.text.toString()
        )
        sharedPreferencesManager.saveBaseURL(
            SharedPreferencesManager.IPConfigurations.LOCAL_IP,
            layoutView.editText_local_ip.text.toString()
        )
    }

    private fun onButtonClicked(view: View) {
        if (view.id == R.id.button_restart) {
            restartActivity()
        }
    }

    private fun restartActivity() {
        val intent = requireActivity().intent
        requireActivity().finish()
        startActivity(intent)
    }
}
