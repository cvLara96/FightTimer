package com.example.fighttimer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.fighttimer.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    //Creamos el binding
    private lateinit var binding: ActivityMainBinding

    //Creamos las variables que necesitamos
    private var currentRound : Int = 3 //Por defecto mostrara 3 rounds
    private var currentRoundMinutes : Int = 3 //Por defecto mostrara 3 minutos
    private var currentRoundSeconds : Int = 0 //Por defecto mostrara 0 segundos
    private var currentRestMinutes : Int = 1 //Por defecto mostrara 1 minuto
    private var currentRestSeconds : Int = 0 //Por defecto mostrara 0 segundos
    private var textTime : String = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Metodo para iniciar los listener de los botones
        initListener()

        //Metodo para iniciar una IU por defecto
        initIU()

    }

    //Metodo que inicia la IU
    private fun initIU() {
        setRound()
        setMinutesRound()
        setSecondsRound()
        setMinutesRest()
        setSecondsRest()
    }

    private fun initListener(){
        binding.addRound.setOnClickListener {
            currentRound +=1
            setRound()
        }

        binding.subtractRound.setOnClickListener {
            if(currentRound>0){
                currentRound-=1
                setRound()
            }else{
                currentRound = 0
                setRound()
                Toast.makeText(this, "El numero de rounds no puede ser inferior a ${currentRound}", Toast.LENGTH_SHORT).show()
            }
        }

        binding.cvMinutesRound.setOnClickListener {
            Toast.makeText(this,"Has pulsado los minutos de round", Toast.LENGTH_SHORT).show()
        }


    }

    //Metodo que establece el numero de rounds
    private fun setRound() {
        binding.tvNumRounds.text = currentRound.toString()
    }

    //Metodo que establece los minutos de round
    private fun setMinutesRound(){
        binding.tvMinutesRound.text = "$textTime$currentRoundMinutes"
    }

    //Metodo que establece los segundos de round
    private fun setSecondsRound(){
        binding.tvSecondsRound.text = "$textTime$currentRoundSeconds"
    }

    //Metodo que establece los minutos de descanso
    private fun setMinutesRest(){
        binding.tvMinutesRest.text = "$textTime$currentRestMinutes"
    }

    //Metodo que establece los segundos de descanso
    private fun setSecondsRest(){
        binding.tvSecondsRest.text = "$textTime$currentRestSeconds"
    }


}