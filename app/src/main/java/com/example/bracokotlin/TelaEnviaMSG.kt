package com.example.bracokotlin

import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.io.OutputStream
import com.example.bracokotlin.TelaConexaoBT.ConexaoBt
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class TelaEnviaMSG : AppCompatActivity() {

    private lateinit var campo_mensagem: TextView
    private lateinit var speechRecognizer: SpeechRecognizer
    private var ultimaAcao: String? = null // Variável para armazenar a última ação
        set(value) {
            field = value
            value?.let { enviaMsg(it) } // Enviar a ação via Bluetooth sempre que for modificada
        }
    private var isListening = true
    private var socket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela_envia_msg)

        campo_mensagem = findViewById(R.id.tvText)

        // Checar permissões de gravação de áudio
        checkAudioPermission()

        // Inicializa o SpeechRecognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {
                if (isListening) startListening()
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val spokenText = matches?.get(0) ?: ""

                campo_mensagem.text = spokenText

                // Atualizar 'ultimaAcao' e enviar via Bluetooth
                when {
                    spokenText.equals("Abrir mão", ignoreCase = true) -> {
                        ultimaAcao = "Abrir mão"
                        Toast.makeText(this@TelaEnviaMSG, "Comando $ultimaAcao enviado!", Toast.LENGTH_SHORT).show()
                    }
                    spokenText.equals("Fechar mão", ignoreCase = true) -> {
                        ultimaAcao = "Fechar mão"
                        Toast.makeText(this@TelaEnviaMSG, "Comando $ultimaAcao enviado!", Toast.LENGTH_SHORT).show()
                    }
                    spokenText.equals("Olá", ignoreCase = true) -> {
                        startFullRecognition()
                    }
                    spokenText.equals("Sair", ignoreCase = true) -> {
                        isListening = false
                        Toast.makeText(this@TelaEnviaMSG, "Reconhecimento de voz encerrado", Toast.LENGTH_SHORT).show()
                        speechRecognizer.stopListening()
                    }
                    else -> {
                        if (isListening) startListening()
                    }
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        // Recuperar o socket e o OutputStream do singleton de conexão Bluetooth
        socket = ConexaoBt.socket
        outputStream = ConexaoBt.outputStream

        // Iniciar a escuta de reconhecimento de voz ao iniciar o app
        startListening()
    }

    private fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt-BR")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }

        speechRecognizer.startListening(intent)
    }

    private fun startFullRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt-BR")
        }
        try {
            speechRecognizer.startListening(intent)
        } catch (e: Exception) {
            Toast.makeText(applicationContext, "Erro ao iniciar reconhecimento completo", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun checkAudioPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.RECORD_AUDIO),
                1
            )
        }
    }

    private fun enviaMsg(msg: String) {
        // Verificar se o socket e o OutputStream estão disponíveis
        if (socket != null && outputStream != null) {
            try {
                outputStream?.write(msg.toByteArray()) // Enviar a mensagem via Bluetooth
                Toast.makeText(this, "Mensagem enviada: $msg", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, "Falha ao enviar mensagem", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Não conectado ao dispositivo Bluetooth", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Libera o SpeechRecognizer quando a atividade for destruída
        speechRecognizer.destroy()
    }
}
