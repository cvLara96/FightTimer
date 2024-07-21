package com.example.fighttimer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.core.content.ContextCompat
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

    //Iniciamos el final time
    private var finalTimeRound : Int = 0
    private var finalTimeRest : Int = 0

    //Creamos el countDownTimer
    private lateinit var countDownTimer : CountDownTimer

    // Variables para manejar el estado del cronómetro
    private var timeRemaining: Long = 0L
    private var isPaused: Boolean = false
    private var isRunning: Boolean = false

    // Variables para los periodos y rondas
    private var isRoundPeriod: Boolean = true
    private var roundsRemaining: Int = 0
    private var totalRounds: Int = 0 // Variable para el total de rounds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCronoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val rounds = intent.extras?.getInt(ROUND_KEY) ?: 1
        val minRound = intent.extras?.getInt(MINUTE_ROUND_KEY) ?: 0
        val secRound = intent.extras?.getInt(SECONDS_ROUND_KEY) ?: 0
        val minRest = intent.extras?.getInt(MINUTE_REST_KEY) ?: 0
        val secRest = intent.extras?.getInt(SECONDS_REST_KEY) ?: 0

        finalTimeRound = (minRound*60)+secRound
        finalTimeRest = (minRest*60)+secRest
        roundsRemaining = rounds
        totalRounds = rounds

        initListener()


    }

    private fun initListener() {
        binding.btnStartFight.setOnClickListener {
            if(finalTimeRound == 0 &&finalTimeRest == 0){
                Toast.makeText(this, "Primero debes configurar el tiempo de entrenamiento", Toast.LENGTH_SHORT).show()
            }else{
                if(!isRunning){
                    startTimer()
                }else if(isPaused){
                    resumeTimer()
                }
            }
        }

        binding.btnPausetFight.setOnClickListener {
            if(finalTimeRound == 0 &&finalTimeRest == 0){
                Toast.makeText(this, "Primero debes configurar el tiempo de entrenamiento", Toast.LENGTH_SHORT).show()
            }else{
                pauseTimer()
            }

        }

        binding.btnStopFight.setOnClickListener {
            if(finalTimeRound == 0 && finalTimeRest == 0){
                Toast.makeText(this, "Primero debes configurar el tiempo de entrenamiento", Toast.LENGTH_SHORT).show()
            }else{
                stopTimer()
            }
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

    }

    private fun startTimer() {
        if (roundsRemaining > 0) {
            isRunning = true
            isPaused = false
            val initialTime = if (isRoundPeriod) finalTimeRound else finalTimeRest
            timeRemaining = initialTime * 1000L

            updateUI()

            countDownTimer = object : CountDownTimer(timeRemaining, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    timeRemaining = millisUntilFinished
                    updateUI()
                }

                override fun onFinish() {
                    if (isRoundPeriod) {
                        isRoundPeriod = false
                    } else {
                        isRoundPeriod = true
                        roundsRemaining -= 1
                    }
                    if (roundsRemaining > 0 || !isRoundPeriod) {
                        startTimer()
                    } else {
                        stopTimer() // All rounds finished
                    }
                }
            }.start()
        }
    }

    private fun pauseTimer() {
        if (isRunning && !isPaused) {
            countDownTimer.cancel()
            isPaused = true
        }
    }

    private fun resumeTimer() {
        isRunning = true
        isPaused = false

        updateUI()

        countDownTimer = object : CountDownTimer(timeRemaining, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemaining = millisUntilFinished
                updateUI()
            }

            override fun onFinish() {
                if (isRoundPeriod) {
                    isRoundPeriod = false
                } else {
                    isRoundPeriod = true
                    roundsRemaining -= 1
                }
                if (roundsRemaining > 0 || !isRoundPeriod) {
                    startTimer()
                } else {
                    stopTimer() // All rounds finished
                }
            }
        }.start()
    }

    private fun stopTimer() {
        try {
            countDownTimer?.cancel()
            isRunning = false
            isPaused = false
            timeRemaining = 0L
            roundsRemaining = 0
            updateUI()

        } catch (e: UninitializedPropertyAccessException) {
            Toast.makeText(this, "El cronometro aun no se ha iniciado", Toast.LENGTH_SHORT).show()
        }

    }

    private fun updateUI() {

        val totalSeconds = timeRemaining / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60

        //Para mostrar el round por el que vamos
        val currenRound = totalRounds - roundsRemaining + 1

        binding.tvMinutes.text = String.format("%02d", minutes)
        binding.tvSeconds.text = String.format("%02d", seconds)

        if(isRoundPeriod){
            binding.tvRound.text = "COMBATE \nROUND $currenRound"
            binding.tvRound.setTextColor(
                ContextCompat.getColor(this, R.color.crono_color)
            )
            binding.tvMinutes.setTextColor(
                ContextCompat.getColor(this, R.color.crono_color)
            )
            binding.tvSeconds.setTextColor(
                ContextCompat.getColor(this, R.color.crono_color)
            )
            binding.tvPoints.setTextColor(
                ContextCompat.getColor(this, R.color.crono_color)
            )
        }else{
            binding.tvRound.text = "DESCANSO \nQUEDAN ${roundsRemaining-1} ROUNDS"
            binding.tvRound.setTextColor(
                ContextCompat.getColor(this, R.color.start_color)
            )
            binding.tvMinutes.setTextColor(
                ContextCompat.getColor(this, R.color.start_color)
            )
            binding.tvSeconds.setTextColor(
                ContextCompat.getColor(this, R.color.start_color)
            )
            binding.tvPoints.setTextColor(
                ContextCompat.getColor(this, R.color.start_color)
            )
        }

        if(roundsRemaining == 0){
            binding.tvRound.text = "SPARRING FINALIZADO"
            binding.tvRound.setTextColor(
                ContextCompat.getColor(this, R.color.background_component)
            )
            binding.tvMinutes.setTextColor(
                ContextCompat.getColor(this, R.color.background_component)
            )
            binding.tvSeconds.setTextColor(
                ContextCompat.getColor(this, R.color.background_component)
            )
            binding.tvPoints.setTextColor(
                ContextCompat.getColor(this, R.color.background_component)
            )

        }


    }


}