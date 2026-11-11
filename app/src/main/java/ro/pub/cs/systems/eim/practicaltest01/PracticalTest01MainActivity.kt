package ro.pub.cs.systems.eim.practicaltest01

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.EditText

class PracticalTest01MainActivity : AppCompatActivity() {
    private lateinit var leftText: EditText
    private lateinit var rightText: EditText
    private lateinit var leftButton: Button
    private lateinit var rightButton: Button

    private lateinit var navigateButton: Button

    private var leftCount = 0
    private var rightCount = 0

    private val PRAG_SERVICIU = 5
    private var isServiceStarted = false

    private val messageReceiver = object : android.content.BroadcastReceiver() {
        override fun onReceive(context: android.content.Context, intent: android.content.Intent) {
            val actiune = intent.action
            val datePrimite = intent.getStringExtra("mesaj_broadcast")
            // Jurnalizarea în consolă
            android.util.Log.d("EIM_BROADCAST", "Acțiune: $actiune | Mesaj: $datePrimite")
        }
    }
    private lateinit var intentFilter: android.content.IntentFilter

    // Declararea lansatorului pentru activitatea secundară
    private val activityResultLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Acest bloc se execută automat când se închide SecondaryActivity (când s-a apelat finish())
        when (result.resultCode) {
            RESULT_OK -> {
                android.widget.Toast.makeText(this, "Rezultat primit: OK", android.widget.Toast.LENGTH_LONG).show()
            }
            RESULT_CANCELED -> {
                android.widget.Toast.makeText(this, "Rezultat primit: CANCELED", android.widget.Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practical_test01_main)

        leftText = findViewById(R.id.left_text)
        rightText  = findViewById(R.id.right_text)
        leftButton = findViewById(R.id.left_button)
        rightButton = findViewById(R.id.right_button)
        navigateButton = findViewById(R.id.navigate_button)

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("leftCount")) {
                leftCount = savedInstanceState.getInt("leftCount")
                leftText.setText(leftCount.toString())
            }
            if (savedInstanceState.containsKey("rightCount")) {
                rightCount = savedInstanceState.getInt("rightCount")
                rightText.setText(rightCount.toString())
            }
        }


        leftButton.setOnClickListener {
            leftCount++
            leftText.setText(leftCount.toString())
            verificaPragSiPornesteServiciu()  //PT SERVICII
        }

        rightButton.setOnClickListener {
            rightCount++
            rightText.setText(rightCount.toString())
            verificaPragSiPornesteServiciu() //PT SERVICII
        }
        navigateButton.setOnClickListener {
            val intent = Intent(this, PracticalTest01SecondaryActivity::class.java)

            val totalClicks = leftCount + rightCount

            intent.putExtra("totalClicks", totalClicks)

            activityResultLauncher.launch(intent)

        }
        intentFilter = android.content.IntentFilter()
        intentFilter.addAction("ro.pub.cs.systems.eim.practicaltest01.aritmetica")
        intentFilter.addAction("ro.pub.cs.systems.eim.practicaltest01.geometrica")
        intentFilter.addAction("ro.pub.cs.systems.eim.practicaltest01.routing")
        }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("leftCount", leftCount)
        outState.putInt("rightCount", rightCount)
    }

    private fun verificaPragSiPornesteServiciu() {
        if (leftCount + rightCount > PRAG_SERVICIU && !isServiceStarted) {
            val intent = Intent(this, PracticalTest01Service::class.java)
            intent.putExtra("leftCount", leftCount)
            intent.putExtra("rightCount", rightCount)

            // Pornim serviciul
            startService(intent)
            isServiceStarted = true
        }
    }

    override fun onResume() {
        super.onResume()
        // Pornim radioul când aplicația e în prim plan (Android 14+ necesită RECEIVER_EXPORTED, folosim varianta compatibilă)
        androidx.core.content.ContextCompat.registerReceiver(
            this,
            messageReceiver,
            intentFilter,
            androidx.core.content.ContextCompat.RECEIVER_EXPORTED
        )
    }

    override fun onPause() {
        // Oprim ascultarea radioului când aplicația e în fundal
        unregisterReceiver(messageReceiver)
        super.onPause()
    }

    override fun onDestroy() {
        // Cerință D.1 b) - Oprim serviciul când aplicația este distrusă
        val intent = Intent(this, PracticalTest01Service::class.java)
        stopService(intent)
        super.onDestroy()
    }
    }



/*


 =====================================================================================
 A. BUTTON (Buton)
 =====================================================================================
    // 1. Ascultător de click (ce se întâmplă când utilizatorul apasă pe buton):
    myButton.setOnClickListener {
        // Codul executat la apăsare
    }

    // 2. Schimbarea textului de pe buton din cod:
    myButton.text = "APĂSAT!"
    // sau: myButton.setText("APĂSAT!")

    // 3. Dezactivarea/Activarea butonului:
    myButton.isEnabled = false // devine gri și nu mai poate fi apăsat
    myButton.isEnabled = true  // redevine activ

 =====================================================================================
 B. CHECKBOX (Căsuță de bifat)
 =====================================================================================
    // 1. Verificarea stării (Dacă este bifat sau nu în acel moment - returnează Boolean: true/false):
    if (myCheckBox.isChecked) {
        // Este bifat
    } else {
        // Nu este bifat
    }

    // 2. Bifarea sau debifarea forțată din cod:
    myCheckBox.isChecked = true  // Bifează caseta programatic
    myCheckBox.isChecked = false // Debifează caseta

    // 3. Ascultător dedicat pentru momentul când starea bifează/debifează instantaneu:
    myCheckBox.setOnCheckedChangeListener { _, isChecked ->
        if (isChecked) {
            // Utilizatorul tocmai a bifat căsuța
        } else {
            // Utilizatorul tocmai a debifat căsuța
        }
    }

    // 4. Important pentru B.2 (Salvarea stării la rotire):
    // - În XML pui obligatoriu: android:saveEnabled="false"
    // - În onSaveInstanceState: outState.putBoolean("KEY_CHECK", myCheckBox.isChecked)
    // - În onCreate (la restaurare): myCheckBox.isChecked = savedInstanceState.getBoolean("KEY_CHECK", false)

 =====================================================================================
 C. EDITTEXT (Casetă de text în care utilizatorul poate tasta)
 =====================================================================================
    // 1. CITIREA textului scris de utilizator:
    val textExtras = myEditText.text.toString()

    // 2. CITIREA ca număr întreg (folosește întotdeauna toIntOrNull() ca să nu crape dacă e gol!):
    val numarInt = myEditText.text.toString().toIntOrNull() ?: 0

    // 3. CITIREA ca număr cu virgulă (Double):
    val numarDouble = myEditText.text.toString().toDoubleOrNull() ?: 0.0

    // 4. SCRIEREA / SETAREA unui text nou în EditText:
    myEditText.setText("Valoare nouă")
    // ATENȚIE: Nu folosi niciodată myEditText.text = "..." pe un EditText, dă eroare! Folosește doar .setText().

    // 5. GOLIREA câmpului de text:
    myEditText.setText("")

    // 6. Blocarea / Debilitarea câmpului (devine needitabil):
    myEditText.isEnabled = false // devine gri, utilizatorul nu poate tasta în el
    myEditText.isEnabled = true  // devine editabil

    // 7. Mutarea cursorului / Focus pe EditText:
    myEditText.requestFocus()

 =====================================================================================
 D. TEXTVIEW (Etichetă simplă de text - doar pentru afișare, fără chenar)
 =====================================================================================
    // 1. SCRIEREA / SETAREA unui text în TextView:
    myTextView.text = "Acesta este un rezultat: 10"
    // sau: myTextView.setText("Text nou")

    // 2. CITIREA textului existent în TextView:
    val textAfisatAcum = myTextView.text.toString()

    // 3. Controlul VIZIBILITĂȚII (Apare / Dispare de pe ecran):
    myTextView.visibility = View.VISIBLE   // Vizibil normal (apare pe ecran)
    myTextView.visibility = View.INVISIBLE // Ascuns, dar păstrează spațiul liber ocupat pe ecran
    myTextView.visibility = View.GONE      // Ascuns complet, spațiul lui dispare (rândul se strânge)

 =====================================================================================
 E. TOAST (Mesaje pop-up scurte afișate pe ecran)
 =====================================================================================
    Toast.makeText(this, "Acesta este un mesaj Toast!", Toast.LENGTH_SHORT).show()

 =====================================================================================
 F. ROTIREA ECRANULUI (Șablonul universal de salvare/restaurare a stării pentru B.2)
 =====================================================================================
    // 1. SALVAREA (În afara lui onCreate, în interiorul clasei):
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("CHEIE_TEXT", myEditText.text.toString())
        outState.putBoolean("CHEIE_CHECK", myCheckBox.isChecked)
        outState.putInt("CHEIE_NUMAR", unContorInt)
    }

    // 2. RESTAURAREA (În interiorul lui onCreate, după findViewById):
    if (savedInstanceState != null) {
        if (savedInstanceState.containsKey("CHEIE_TEXT")) {
            myEditText.setText(savedInstanceState.getString("CHEIE_TEXT"))
        }
        if (savedInstanceState.containsKey("CHEIE_CHECK")) {
            myCheckBox.isChecked = savedInstanceState.getBoolean("CHEIE_CHECK", false)
        }
        if (savedInstanceState.containsKey("CHEIE_NUMAR")) {
            unContorInt = savedInstanceState.getInt("CHEIE_NUMAR", 0)
        }
    }
 =====================================================================================
*/

/* SERVICIU
// =====================================================================
    // 1. DECLARAȚII SUS ÎN CLASĂ (Sub variabilele butoanelor)
    // =====================================================================
    private var isServiceStarted = false
    private lateinit var intentFilter: android.content.IntentFilter

    // Construim radioul care va asculta mesajele
    private val myBroadcastReceiver = object : android.content.BroadcastReceiver() {
        override fun onReceive(context: android.content.Context, intent: android.content.Intent) {
            // Aici ajunge mesajul trimis din Serviciu!
            val mesajPrimit = intent.getStringExtra("DATE_BROADCAST")
            android.util.Log.d("EXAMEN_BROADCAST", "Am primit: $mesajPrimit")
        }
    }

    // =====================================================================
    // 2. INIȚIALIZARE ÎN onCreate()
    // =====================================================================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ... (setContentView, findViewById etc) ...

        // Setăm radioul pe frecvența corectă (ACEEAȘI din Serviciu)
        intentFilter = android.content.IntentFilter()
        intentFilter.addAction("ro.pub.cs.systems.eim.BROADCAST_ACTION_UNIV")

        // Cum pornești serviciul (de obicei se pune pe un buton sau când se atinge un prag)
        /*
        butonStart.setOnClickListener {
            if (!isServiceStarted) {
                val serviceIntent = Intent(this, MyService::class.java)
                serviceIntent.putExtra("CHEIE_DATE_1", 50)
                startService(serviceIntent)
                isServiceStarted = true
            }
        }
        */
    }

    // =====================================================================
    // 3. PORNIREA ȘI OPRIREA RADIOULUI (Atașate direct în clasă, sub onCreate)
    // =====================================================================
    override fun onResume() {
        super.onResume()
        // Pornim ascultarea când ecranul e activ
        androidx.core.content.ContextCompat.registerReceiver(
            this,
            myBroadcastReceiver,
            intentFilter,
            androidx.core.content.ContextCompat.RECEIVER_EXPORTED
        )
    }

    override fun onPause() {
        // Oprim ascultarea când minimizăm aplicația (să nu consumăm baterie)
        unregisterReceiver(myBroadcastReceiver)
        super.onPause()
    }

    override fun onDestroy() {
        // Oprim complet Serviciul (crainicul din fundal) când se distruge aplicația
        val serviceIntent = Intent(this, MyService::class.java)
        stopService(serviceIntent)
        super.onDestroy()
    }
 */