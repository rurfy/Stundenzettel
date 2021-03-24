package com.christopherrichter.stundenzettel

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.christopherrichter.stundenzettel.databinding.ActivityZugfahrtenBinding
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

val TARGETMAIL: Array<String> = arrayOf("test@test.com")
const val MAILTEXT = "Das ist ein Testtext. \nEr kann jederzeit geändert werden.\n"

fun formatTime(date: LocalDateTime): String {
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val text = date.format(formatter)
    return LocalTime.parse(text, formatter).toString()
}

fun formatDate(date: LocalDateTime): String {
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val text = date.format(formatter)
    return LocalDate.parse(text, formatter).toString()
}

class ZugfahrtenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityZugfahrtenBinding
    private lateinit var schicht: Schicht

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityZugfahrtenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        schicht = intent.getSerializableExtra("Schicht") as Schicht;

        lateinit var zug1: Schicht.Zug

        binding.bAbfahrt.setOnClickListener {
            if(binding.eTAbfahrtOrt.text.toString() != "Ort") {
                if(binding.bAbfahrt.text.toString() == resources.getString(R.string.fahrt_beginnen)) {
                    zug1 = Schicht.Zug(etappeEintragen(binding.bAbfahrt, binding.eTAbfahrtOrt))
                    binding.bAnkunft.visibility = View.VISIBLE
                    binding.eTAnkunftOrt.visibility = View.VISIBLE
                    binding.bAbfahrtSenden.visibility = View.VISIBLE
                }
                else {
                    buildPicker(zug1.abfahrt)
                    binding.bAbfahrt.text = formatTime(zug1.abfahrt.zeitpunkt)
                }
            }
            else showErrorText()
        }

        binding.bAbfahrtSenden.setOnClickListener {
            buildDialog(zug1.abfahrt, "Fahrt begonnen")
        }

        binding.bAnkunft.setOnClickListener {
            if(binding.eTAnkunftOrt.text.toString() != "Ort") {
                if(binding.bAnkunft.text.toString() == resources.getString(R.string.fahrt_beenden)) {
                    zug1.ankunft = etappeEintragen(binding.bAnkunft, binding.eTAnkunftOrt)
                    schicht.zuege.add(zug1)
                    binding.bNeueFahrt.visibility = View.VISIBLE
                    binding.bDienstende.visibility = View.VISIBLE
                    binding.bAnkunftSenden.visibility = View.VISIBLE
                }
                else {
                    buildPicker(zug1.ankunft)
                    binding.bAnkunft.text = formatTime(zug1.ankunft.zeitpunkt)
                }
            }
            else showErrorText()
        }

        binding.bAnkunftSenden.setOnClickListener {
            buildDialog(zug1.ankunft, "Fahrt beendet")
        }

        binding.bNeueFahrt.setOnClickListener {
            finish()
            startActivity(intent)
        }

        binding.bDienstende.setOnClickListener {
            val intent = Intent(this, OverviewActivity::class.java).apply {
                putExtra("Schicht", schicht)
            }
            startActivity(intent)
        }
    }

    private fun etappeEintragen(button: Button, editText: EditText) : Schicht.Etappe {
        val now = LocalDateTime.now()
        button.text = formatTime(now)

        return Schicht.Etappe(editText.text.toString(), now)
    }

    private fun showErrorText() {
        Toast.makeText(this, "Bitte Ort eingeben", Toast.LENGTH_SHORT).show()
    }

    private fun buildDialog(etappe: Schicht.Etappe, subject: String) {
        val newFragment = ConfirmationDialogFragment(etappe, subject)
        newFragment.show(supportFragmentManager, "Dialog")
    }

    private fun buildPicker(etappe: Schicht.Etappe) {
        val time = etappe.zeitpunkt
        val timePicker = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            etappe.zeitpunkt = time.withHour(hourOfDay).withMinute(minute)
        }, time.hour, time.minute, true)
        timePicker.updateTime(time.hour, time.minute)
        timePicker.show()
    }
}

class ConfirmationDialogFragment(etappe: Schicht.Etappe, private val subject: String) : DialogFragment() {

    private val text = "Ort: " + etappe.ort + "\nZeitpunkt: " + formatTime(etappe.zeitpunkt)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage(text)
                .setTitle(R.string.confirmationMessage)
                .setPositiveButton(R.string.confirm,
                    DialogInterface.OnClickListener { dialog, id ->
                        // FIRE ZE MISSILES!
                        val mailIntent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:") // only email apps should handle this
                            putExtra(Intent.EXTRA_EMAIL, TARGETMAIL)
                            putExtra(Intent.EXTRA_SUBJECT, subject)
                            putExtra(Intent.EXTRA_TEXT, MAILTEXT + text)
                        }
                        startActivity(mailIntent)
                    })
                .setNegativeButton(R.string.cancel,
                    DialogInterface.OnClickListener { dialog, id ->
                        // User cancelled the dialog
                    })
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}

class TimePickerFragment(var time: LocalDateTime) : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker
        val hour = time.hour
        val minute = time.minute

        // Create a new instance of TimePickerDialog and return it
        return TimePickerDialog(activity, this, hour, minute, true)
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        // Do something with the time chosen by the user
        Log.d("Times", (hourOfDay + minute).toString())
        time = time.withHour(hourOfDay).withMinute(minute)
        Log.d("Times", time.toString())
    }
}