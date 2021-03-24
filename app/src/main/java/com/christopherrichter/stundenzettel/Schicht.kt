package com.christopherrichter.stundenzettel

import android.os.Build
import androidx.annotation.RequiresApi
import java.io.Serializable
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

data class Schicht(var schichtBeginn: LocalDateTime) : Serializable {

    var schichtEnde = schichtBeginn
    var zuege = mutableListOf<Zug>()

    class Zug(var abfahrt: Etappe) : Serializable {
        var ankunft: Etappe = abfahrt;
    }

    class Etappe(var ort: String, var zeitpunkt: LocalDateTime) : Serializable


}
