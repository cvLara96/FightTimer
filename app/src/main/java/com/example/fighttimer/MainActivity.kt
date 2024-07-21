package com.example.fighttimer

import android.app.Dialog
import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.example.fighttimer.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    //Creamos el binding
    private lateinit var binding: ActivityMainBinding

    //Creamos las variables que necesitamos
    private var currentRound : Int = 3 //Por defecto mostrara 3 rounds
    private var currentRoundMinutes : Int = 0 //Por defecto mostrara 0 minuto
    private var currentRoundSeconds : Int = 0 //Por defecto mostrara 0 segundos
    private var currentRestMinutes : Int = 0 //Por defecto mostrara 0 minuto
    private var currentRestSeconds : Int = 0 //Por defecto mostrara 0 segundos
    private var textTime : String = "0"

    companion object{
        const val ROUND_KEY = "ROUNDS"
        const val MINUTE_ROUND_KEY = "MIN_ROUND"
        const val SECONDS_ROUND_KEY = "SEC_ROUND"
        const val MINUTE_REST_KEY = "MIN_REST"
        const val SECONDS_REST_KEY = "SEC_REST"
    }

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

        binding.cvSettingRound.setOnClickListener {
            showDialogRound()
        }

        binding.cvSettingRest.setOnClickListener {
            showDialogRest()
        }

        binding.btnReset.setOnClickListener {
            restart()
        }

        binding.btnStart.setOnClickListener {
            navigateToCrono()
        }


    }

    //Metodo que muestra el dialogo de configuracion de tiempo de descanso
    private fun showDialogRest() {

        val dialog = Dialog(this)

        dialog.setContentView(R.layout.dialog_setting_time)

        //Accedemos a los componentes de la vista
        val btnSave : Button = dialog.findViewById(R.id.btnSave)
        val etMinutes : EditText = dialog.findViewById(R.id.etMinutes)
        val etSeconds : EditText = dialog.findViewById(R.id.etSeconds)

        btnSave.setOnClickListener {

            currentRestMinutes = etMinutes.text.toString().toInt()
            currentRestSeconds = etSeconds.text.toString().toInt()

            if(currentRestMinutes>90 || currentRestSeconds>59){
                Toast.makeText(this, "No puedes superar los 90 minutos ni los 60 segundos", Toast.LENGTH_SHORT).show()
            }else{

                setMinutesRest()
                setSecondsRest()
                binding.cardView3.isVisible = true
                binding.cvSettingRest.isVisible = false
                dialog.hide()
            }

        }

        dialog.show()

    }

    //Metodo que muestra el dialogo de configuracion de tiempo de round
    private fun showDialogRound() {

        val dialog = Dialog(this)

        dialog.setContentView(R.layout.dialog_setting_time)

        //Accedemos a los componentes de la vista
        val btnSave : Button = dialog.findViewById(R.id.btnSave)
        val etMinutes : EditText = dialog.findViewById(R.id.etMinutes)
        val etSeconds : EditText = dialog.findViewById(R.id.etSeconds)

        btnSave.setOnClickListener {

            currentRoundMinutes = etMinutes.text.toString().toInt()
            currentRoundSeconds = etSeconds.text.toString().toInt()

            if(currentRoundMinutes>90 || currentRoundSeconds>59){
                Toast.makeText(this, "No puedes superar los 90 minutos ni los 60 segundos", Toast.LENGTH_SHORT).show()
            }else{
                setMinutesRound()
                setSecondsRound()
                binding.cardView2.isVisible = true
                binding.cvSettingRound.isVisible = false
                dialog.hide()
            }

        }

        dialog.show()

    }



    //Metodo que establece el numero de rounds
    private fun setRound() {
        binding.tvNumRounds.text = currentRound.toString()
    }

    //Metodo que establece los minutos de round
    private fun setMinutesRound(){

        val minutesRound :String =
        if(currentRoundMinutes<10){"$textTime$currentRoundMinutes"}
        else{currentRoundMinutes.toString()}

        binding.tvMinutesRound.text = minutesRound

    }

    //Metodo que establece los segundos de round
    private fun setSecondsRound(){

        val secondsRound : String =
        if(currentRoundSeconds<10) {"$textTime$currentRoundSeconds"}
        else{currentRoundSeconds.toString()}

        binding.tvSecondsRound.text = secondsRound

    }

    //Metodo que establece los minutos de descanso
    private fun setMinutesRest(){

        val minutesRest :String =
            if(currentRestMinutes<10){"$textTime$currentRestMinutes"}
            else{currentRestMinutes.toString()}

        binding.tvMinutesRest.text = minutesRest
    }

    //Metodo que establece los segundos de descanso
    private fun setSecondsRest(){

        val secondsRest :String =
            if(currentRestSeconds<10){"$textTime$currentRestSeconds"}
            else{currentRestSeconds.toString()}

        binding.tvSecondsRest.text = secondsRest
    }

    //Metodo que reestablece la IU
    private fun restart(){
        currentRound = 3 //Por defecto mostrara 3 rounds
        currentRoundMinutes = 0 //Por defecto mostrara 0 minuto
        currentRoundSeconds = 0 //Por defecto mostrara 0 segundos
        currentRestMinutes = 0 //Por defecto mostrara 0 minuto
        currentRestSeconds = 0 //Por defecto mostrara 0 segundos

        setRound()

        binding.cardView2.isInvisible = true
        binding.cvSettingRound.isVisible = true

        binding.cardView3.isInvisible = true
        binding.cvSettingRest.isVisible = true

    }

    //Metodo para ir a la actividad del cronometro
    private fun navigateToCrono(){

        intent = Intent(this, CronoActivity::class.java)

        intent.putExtra(ROUND_KEY, currentRound)
        intent.putExtra(MINUTE_ROUND_KEY, currentRoundMinutes)
        intent.putExtra(SECONDS_ROUND_KEY, currentRoundSeconds)
        intent.putExtra(MINUTE_REST_KEY, currentRestMinutes)
        intent.putExtra(SECONDS_REST_KEY, currentRestSeconds)

        startActivity(intent)


    }

}