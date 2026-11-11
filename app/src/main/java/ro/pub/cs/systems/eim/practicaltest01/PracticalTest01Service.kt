package ro.pub.cs.systems.eim.practicaltest01

import android.app.Service
import android.content.Intent
import android.os.IBinder
import java.util.Date
import kotlin.math.sqrt
import kotlin.random.Random

class PracticalTest01Service : Service() {

    private var processingThread: ProcessingThread? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Luăm numerele trimise din MainActivity
        val leftCount = intent?.getIntExtra("leftCount", 0) ?: 0
        val rightCount = intent?.getIntExtra("rightCount", 0) ?: 0

        // Pornim thread-ul dacă nu este deja pornit
        if (processingThread == null) {
            processingThread = ProcessingThread(this, leftCount, rightCount)
            processingThread?.start()
        }

        return START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onDestroy() {
        // Oprim thread-ul când serviciul este distrus
        processingThread?.stopThread()
        super.onDestroy()
    }
}

// Thread-ul care rulează în fundal
class ProcessingThread(
    private val context: android.content.Context,
    private val left: Int,
    private val right: Int
) : Thread() {

    private var isRunning = true

    // Cele 3 acțiuni (frecvențe) diferite cerute în enunț
    private val actiuni = arrayOf(
        "ro.pub.cs.systems.eim.practicaltest01.aritmetica",
        "ro.pub.cs.systems.eim.practicaltest01.geometrica",
        "ro.pub.cs.systems.eim.practicaltest01.routing"
    )

    override fun run() {
        while (isRunning) {
            // Calculele cerute
            val mediaAritmetica = (left + right) / 2.0
            val mediaGeometrica = sqrt((left * right).toDouble())

            // Crearea mesajului cu data și ora
            val mesaj = "Data: ${Date()}, Aritmetica: $mediaAritmetica, Geometrica: $mediaGeometrica"

            // Trimiterea Broadcast-ului (strigătul la radio)
            val intent = Intent()
            // Alegem aleator una dintre cele 3 acțiuni
            intent.action = actiuni[Random.nextInt(actiuni.size)]
            intent.putExtra("mesaj_broadcast", mesaj)
            context.sendBroadcast(intent)

            // Așteptăm 10 secunde (10.000 milisecunde)
            try {
                sleep(10000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    fun stopThread() {
        isRunning = false
    }
}




/* sablon
package ro.pub.cs.systems.eim.practicaltest01 // Schimbă cu pachetul tău

import android.app.Service
import android.content.Intent
import android.os.IBinder

class MyService : Service() {

    private var workerThread: WorkerThread? = null

    // 1. Aici primim datele de la MainActivity când pornește serviciul
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val data1 = intent?.getIntExtra("CHEIE_DATE_1", 0) ?: 0
        // val data2 = intent?.getStringExtra("CHEIE_DATE_2") ?: ""

        // Pornim crainicul (Thread-ul) doar dacă nu e deja pornit
        if (workerThread == null) {
            workerThread = WorkerThread(this, data1)
            workerThread?.start()
        }

        // Returnăm asta ca sistemul să repornească serviciul dacă e ucis de lipsa de memorie
        return START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent): IBinder? = null

    // 2. Aici se execută când aplicația se închide și se apelează stopService
    override fun onDestroy() {
        workerThread?.stopWork()
        super.onDestroy()
    }

    // =====================================================================
    // CRAINICUL (Thread-ul) - El face munca grea în fundal
    // =====================================================================
    private inner class WorkerThread(
        private val context: android.content.Context,
        private val datePrimite: Int
    ) : Thread() {

        private var isRunning = true

        override fun run() {
            while (isRunning) {
                // --- AICI MODIFICI LA EXAMEN ÎN FUNCȚIE DE CERINȚĂ ---
                val rezultatCalculat = datePrimite * 10 // Exemplu de calcul
                val mesajDeTrimis = "Rezultatul este $rezultatCalculat"
                // -------------------------------------------------------

                // Pregătim mesajul de difuzare
                val broadcastIntent = Intent()

                // Setăm FRECVENȚA (Trebuie să fie unică). Aceeași o vom asculta în MainActivity.
                broadcastIntent.action = "ro.pub.cs.systems.eim.BROADCAST_ACTION_UNIV"

                // Punem pachetul cu datele
                broadcastIntent.putExtra("DATE_BROADCAST", mesajDeTrimis)

                // Strigăm la radio!
                context.sendBroadcast(broadcastIntent)

                // Punem crainicul să doarmă (ex: 5000 ms = 5 secunde)
                try {
                    sleep(5000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }

        fun stopWork() {
            isRunning = false
        }
    }
} */