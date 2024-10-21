package com.example.bracokotlin

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    private lateinit var speechRecognizer: SpeechRecognizer
    private var isListening = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar reconhecimento de voz
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

                // Navegação por voz
                navigateByVoice(spokenText)
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        startListening()
    }

    private fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt-BR")
        }
        speechRecognizer.startListening(intent)
    }

    // Lógica de navegação centralizada
    private fun navigateByVoice(spokenText: String) {
        when {
            spokenText.equals("Conectar braço", ignoreCase = true) -> {
                val intent = Intent(this, TelaConexaoBT::class.java)
                startActivity(intent)
            }
            spokenText.equals("Enviar comandos", ignoreCase = true) -> {
                val intent = Intent(this, TelaEnviaMSG::class.java)
                startActivity(intent)
            }
            spokenText.equals("Home", ignoreCase = true) -> {
                val intent = Intent(this, TelaPrincipal::class.java)
                startActivity(intent)
            }
            spokenText.equals("Ajuda", ignoreCase = true) -> {
                val intent = Intent(this, TelaInfo::class.java)
                startActivity(intent)
            }
            else -> {
                if (isListening) startListening()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
    }
}
