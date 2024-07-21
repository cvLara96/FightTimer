package com.example.fighttimer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fighttimer.MainActivity.Companion.MINUTE_REST_KEY
import com.example.fighttimer.MainActivity.Companion.MINUTE_ROUND_KEY
import com.example.fighttimer.MainActivity.Companion.ROUND_KEY
import com.example.fighttimer.MainActivity.Companion.SECONDS_REST_KEY
import com.example.fighttimer.MainActivity.Companion.SECONDS_ROUND_KEY
import com.example.fighttimer.databinding.ActivityCronoBinding
import com.example.fighttimer.databinding.ActivityMainBinding

class CronoActivity : AppCompatActivity() {

    //Creamos el binding
    private lateinit var binding: ActivityCronoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCronoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val rounds = intent.extras?.getInt(ROUND_KEY) ?: 0
        val minRound = intent.extras?.getInt(MINUTE_ROUND_KEY) ?: 0
        val secRound = intent.extras?.getInt(SECONDS_ROUND_KEY) ?: 0
        val minRest = intent.extras?.getInt(MINUTE_REST_KEY) ?: 0
        val secRest = intent.extras?.getInt(SECONDS_REST_KEY) ?: 0

        binding.tvrounds.text = rounds.toString()
        binding.tvminutesRound.text = minRound.toString()
        binding.tvSecondsRoun.text = secRound.toString()
        binding.tvdescansoMint.text = minRest.toString()
        binding.tvdescansoSegs.text = secRest.toString()

    }
}