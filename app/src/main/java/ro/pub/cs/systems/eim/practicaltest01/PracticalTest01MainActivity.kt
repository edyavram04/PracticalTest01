package ro.pub.cs.systems.eim.practicaltest01

import android.os.Bundle
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

    private var leftCount = 0
    private var rightCount = 0




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practical_test01_main)

        leftText = findViewById(R.id.left_text)
        rightText  = findViewById(R.id.right_text)
        leftButton = findViewById(R.id.left_button)
        rightButton = findViewById(R.id.right_button)

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("leftCount")) {
                leftCount = savedInstanceState.getInt("leftCount")
                leftText.setText(leftCount.toString())
            }
            if (savedInstanceState.containsKey("rightCount")) {
                rightCount = savedInstanceState.getInt("rightCount")
            }
        }


        leftButton.setOnClickListener {
            leftCount++
            leftText.setText(leftCount.toString())
        }

        rightButton.setOnClickListener {
            rightCount++
            rightText.setText(rightCount.toString())
        }
        }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("leftCount", leftCount)
        outState.putInt("rightCount", rightCount)
    }
    }
