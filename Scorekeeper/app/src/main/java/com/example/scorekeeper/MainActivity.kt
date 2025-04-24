package com.example.scorekeeper

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate


class MainActivity : AppCompatActivity() {

    // 1.5.1. Змінні для зберігання рахунку команд
    private var mScore1: Int = 0
    private var mScore2: Int = 0

    companion object {
        const val STATE_SCORE_1 = "Team 1 Score"
        const val STATE_SCORE_2 = "Team 2 Score"
    }
    // 1.5.2. TextView змінні для відображення рахунку
    private lateinit var mScoreText1: TextView
    private lateinit var mScoreText2: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1.5.3. Знаходимо TextView за ID
        mScoreText1 = findViewById(R.id.score_1)
        mScoreText2 = findViewById(R.id.score_2)

        // Приклади: обробка натискання кнопок
        val decreaseTeam1: ImageButton = findViewById(R.id.decreaseTeam1)
        val increaseTeam1: ImageButton = findViewById(R.id.increaseTeam1)
        val decreaseTeam2: ImageButton = findViewById(R.id.decreaseTeam2)
        val increaseTeam2: ImageButton = findViewById(R.id.increaseTeam2)

        // Зменшення/збільшення оцінки команди 1
        decreaseTeam1.setOnClickListener {
            mScore1--
            mScoreText1.text = mScore1.toString()
        }

        increaseTeam1.setOnClickListener {
            mScore1++
            mScoreText1.text = mScore1.toString()
        }

        // Зменшення/збільшення оцінки команди 2
        decreaseTeam2.setOnClickListener {
            mScore2--
            mScoreText2.text = mScore2.toString()
        }

        increaseTeam2.setOnClickListener {
            mScore2++
            mScoreText2.text = mScore2.toString()
        }

        if (savedInstanceState != null) {
            mScore1 = savedInstanceState.getInt(STATE_SCORE_1)
            mScore2 = savedInstanceState.getInt(STATE_SCORE_2)
            mScoreText1.text = mScore1.toString()
            mScoreText2.text = mScore2.toString()
        }
    }

    fun decreaseScore(view: View) {
        val viewID = view.id
        when (viewID) {
            R.id.decreaseTeam1 -> {
                mScore1--
                mScoreText1.text = mScore1.toString()
            }
            R.id.decreaseTeam2 -> {
                mScore2--
                mScoreText2.text = mScore2.toString()
            }
        }
    }
    fun increaseScore(view: View) {
        val viewID = view.id
        when (viewID) {
            R.id.increaseTeam1 -> {
                mScore1++
                mScoreText1.text = mScore1.toString()
            }
            R.id.increaseTeam2 -> {
                mScore2++
                mScoreText2.text = mScore2.toString()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        // Change the label of the menu based on the state of the app.
        val nightMode = AppCompatDelegate.getDefaultNightMode()
        if (nightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            menu!!.findItem(R.id.night_mode).setTitle(R.string.day_mode)
        } else {
            menu!!.findItem(R.id.night_mode).setTitle(R.string.night_mode)
        }

        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(STATE_SCORE_1, mScore1)
        outState.putInt(STATE_SCORE_2, mScore2)
    }

    override fun onOptionsItemSelected(@NonNull item: MenuItem): Boolean {
        //Check if the correct item was clicked
        if (item.itemId == R.id.night_mode) {
        }
        if (item.itemId === R.id.night_mode) {
            // Get the night mode state of the app.
            val nightMode = AppCompatDelegate.getDefaultNightMode()
            //Set the theme mode for the restarted activity
            if (nightMode == AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            // Recreate the activity for the theme change to take effect.
            recreate()
        }


        return true
    }

}
