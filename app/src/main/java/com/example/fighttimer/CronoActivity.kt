package com.example.fighttimer

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.TextView
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

    // MediaPlayer para reproducir audio
    private var mediaPlayerCampana: MediaPlayer? = null
    private var mediaPlayerBox: MediaPlayer? = null
    private var mediaPlayerBaquetas: MediaPlayer? = null
    private var mediaPlayerStop: MediaPlayer? = null
    private var mediaPlayerSegundosFuera: MediaPlayer? = null

    //Variables para controlar si ha sonado el aviso de 10segs
    private var isReproductedBaquetas: Boolean = false
    private var isReproductedSegundosFuera: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCronoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar MediaPlayer con el archivo de audio
        mediaPlayerCampana = MediaPlayer.create(this, R.raw.campana)
        mediaPlayerBox = MediaPlayer.create(this, R.raw.box)
        mediaPlayerBaquetas = MediaPlayer.create(this, R.raw.baquetas)
        mediaPlayerStop = MediaPlayer.create(this, R.raw.stop)
        mediaPlayerSegundosFuera = MediaPlayer.create(this, R.raw.segundos_fuera)

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
            mediaPlayerCampana?.start()
            val initialTime = if (isRoundPeriod) finalTimeRound else finalTimeRest
            timeRemaining = initialTime * 1000L

            updateUI()

            countDownTimer = object : CountDownTimer(timeRemaining, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    timeRemaining = millisUntilFinished
                    updateUI()
                    tenSeconds(millisUntilFinished)
                }

                override fun onFinish() {
                    if (isRoundPeriod) {
                        isRoundPeriod = false
                    } else {
                        isRoundPeriod = true
                        roundsRemaining -= 1
                    }

                    isReproductedBaquetas = false
                    isReproductedSegundosFuera = false

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
            mediaPlayerStop?.start()
            isPaused = true
        }
    }

    private fun resumeTimer() {
        isRunning = true
        isPaused = false
        mediaPlayerBox?.start()

        updateUI()

        countDownTimer = object : CountDownTimer(timeRemaining, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemaining = millisUntilFinished
                updateUI()
                tenSeconds(millisUntilFinished)
            }

            override fun onFinish() {
                if (isRoundPeriod) {
                    isRoundPeriod = false
                } else {
                    isRoundPeriod = true
                    roundsRemaining -= 1
                }

                isReproductedBaquetas = false
                isReproductedSegundosFuera = false

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
            updateColors(
                binding.tvRound,
                binding.tvMinutes,
                binding.tvSeconds,
                binding.tvPoints,
                R.color.crono_color
            )

        }else{
            binding.tvRound.text = "DESCANSO \nQUEDAN ${roundsRemaining-1} ROUNDS"
            updateColors(
                binding.tvRound,
                binding.tvMinutes,
                binding.tvSeconds,
                binding.tvPoints,
                R.color.start_color
            )
        }

        if(roundsRemaining == 0){
            binding.tvRound.text = "SPARRING FINALIZADO"
            updateColors(
                binding.tvRound,
                binding.tvMinutes,
                binding.tvSeconds,
                binding.tvPoints,
                R.color.background_component
            )

        }

    }

    //Metodo para no tener que repetir este bloque de codigo:
    /*
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
     */
    private fun updateColors(textRound : TextView,textMinutes : TextView,textSeconds : TextView,textPoints : TextView, color : Int) {

        textRound.setTextColor(
            ContextCompat.getColor(this, color)
        )

        textMinutes.setTextColor(
            ContextCompat.getColor(this, color)
        )

        textSeconds.setTextColor(
            ContextCompat.getColor(this, color)
        )

        textPoints.setTextColor(
            ContextCompat.getColor(this, color)
        )

    }

    private fun tenSeconds(millisUntilFinished:Long){
        // Reproducir el sonido de advertencia a los 10 segundos restantes
        if (millisUntilFinished <= 10000) {
            if (isRoundPeriod && !isReproductedBaquetas) {
                isReproductedBaquetas = !isReproductedBaquetas
                mediaPlayerBaquetas?.start()
            } else if (!isRoundPeriod && !isReproductedSegundosFuera){
                isReproductedSegundosFuera = !isReproductedSegundosFuera
                mediaPlayerSegundosFuera?.start()
            }
        }
    }


}