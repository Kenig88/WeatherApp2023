package com.example.weatherapp2023.adapters

import android.app.AlertDialog
import android.content.Context
import android.widget.EditText


object DialogManager { //14
    fun locationSettingsDialog(context: Context, listener: Listener){
        val builder = AlertDialog.Builder(context)
        val dialog = builder.create()
        dialog.setTitle("Enable location?")
        dialog.setMessage("Location is disabled, do you want to enable it?")

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok"){_,_ ->
            listener.onClick(null)
            dialog.dismiss()
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel"){_,_ ->
            dialog.dismiss()
        }
        dialog.show()
    }

    fun searchByCityName(context: Context, listener: Listener) { //15
        val builder = AlertDialog.Builder(context)
        val edText = EditText(context)
        builder.setView(edText)
        val dialog = builder.create()
        dialog.setTitle("City name:")

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok") { _, _ ->
            listener.onClick(edText.text.toString())
            dialog.dismiss()
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel") { _, _ ->
            dialog.dismiss()
        }
        dialog.show()
    }

    interface Listener{
        fun onClick(cityName: String?) //15.1
    }
}