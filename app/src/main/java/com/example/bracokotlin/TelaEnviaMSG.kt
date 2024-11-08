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
import android.view.View
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class TelaEnviaMSG : AppCompatActivity() {

    private lateinit var campo_mensagem: TextView
    private lateinit var speechRecognizer: SpeechRecognizer
    private var isKeywordRecognized = false // Variável que indica se a palavra "Olá" foi reconhecida
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
        Toast.makeText(this@TelaEnviaMSG, "Diga 'Olá' para iniciar", Toast.LENGTH_SHORT).show()
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

                // Atualizar 'ultimaAcao' e enviar via Bluetooth
                when {
                    !isKeywordRecognized -> {
                        // Se ainda não reconheceu "Olá", verifica se foi dito
                        if (spokenText.equals("Olá", ignoreCase = true)) {
                            isKeywordRecognized = true
                            campo_mensagem.visibility = View.VISIBLE // Exibe o campo de mensagem
                            Toast.makeText(this@TelaEnviaMSG, "Reconhecimento de voz iniciado!", Toast.LENGTH_SHORT).show()
                                startFullRecognition() // Inicia o reconhecimento total
                            } else {
                                // Continua escutando até que "Olá" seja dito
                                startFullRecognition()
                            }
                        }
                        else -> {
                            campo_mensagem.text = spokenText
                            startFullRecognition() // Continua ouvindo os próximos comandos
                        }

                }
                // Atualizar 'ultimaAcao' e enviar via Bluetooth
                when {
                    spokenText.equals("Fechar mão", ignoreCase = true) -> {
                        ultimaAcao = "A"
                        Toast.makeText(this@TelaEnviaMSG, "Comando Fechar mão enviado!", Toast.LENGTH_SHORT).show()
                        startFullRecognition()
                    }
                    spokenText.equals("Abrir mão", ignoreCase = true) -> {
                        ultimaAcao = "B"
                        Toast.makeText(this@TelaEnviaMSG, "Comando Abrir mão enviado!", Toast.LENGTH_SHORT).show()
                        startFullRecognition()
                    }
                    // COMANDOS PARA ABRIR OS DEDOS
                    spokenText.equals("Abrir polegar", ignoreCase = true) -> {
                        ultimaAcao = "C"
                        Toast.makeText(this@TelaEnviaMSG, "Comando Abrir polegar enviado!", Toast.LENGTH_SHORT).show()
                        startFullRecognition()
                    }
                    spokenText.equals("Abrir indicador", ignoreCase = true) -> {
                        ultimaAcao = "D"
                        Toast.makeText(this@TelaEnviaMSG, "Comando Abrir indicador enviado!", Toast.LENGTH_SHORT).show()
                        startFullRecognition()
                    }
                    spokenText.equals("Abrir medio", ignoreCase = true) -> {
                        ultimaAcao = "E"
                        Toast.makeText(this@TelaEnviaMSG, "Comando Abrir medio enviado!", Toast.LENGTH_SHORT).show()
                        startFullRecognition()
                    }
                    spokenText.equals("Abrir anelar", ignoreCase = true) -> {
                        ultimaAcao = "F"
                        Toast.makeText(this@TelaEnviaMSG, "Comando Abrir anelar enviado!", Toast.LENGTH_SHORT).show()
                        startFullRecognition()
                    }
                    spokenText.equals("Abrir mindinho", ignoreCase = true) -> {
                        ultimaAcao = "G"
                        Toast.makeText(this@TelaEnviaMSG, "Comando Abrir mindinho enviado!", Toast.LENGTH_SHORT).show()
                        startFullRecognition()
                    }
                    // COMANDOS PARA FECHAR OS DEDOS
                    spokenText.equals("Fechar polegar", ignoreCase = true) -> {
                        ultimaAcao = "H"
                        Toast.makeText(this@TelaEnviaMSG, "Comando Fechar polegar enviado!", Toast.LENGTH_SHORT).show()
                        startFullRecognition()
                    }
                    spokenText.equals("Fechar indicador", ignoreCase = true) -> {
                        ultimaAcao = "I"
                        Toast.makeText(this@TelaEnviaMSG, "Comando Fechar indicador enviado!", Toast.LENGTH_SHORT).show()
                        startFullRecognition()
                    }
                    spokenText.equals("Fechar medio", ignoreCase = true) -> {
                        ultimaAcao = "J"
                        Toast.makeText(this@TelaEnviaMSG, "Comando Fechar medio enviado!", Toast.LENGTH_SHORT).show()
                        startFullRecognition()
                    }
                    spokenText.equals("Fechar anelar", ignoreCase = true) -> {
                        ultimaAcao = "K"
                        Toast.makeText(this@TelaEnviaMSG, "Comando Fechar anelar enviado!", Toast.LENGTH_SHORT).show()
                        startFullRecognition()
                    }
                    spokenText.equals("Fechar mindinho", ignoreCase = true) -> {
                        ultimaAcao = "L"
                        Toast.makeText(this@TelaEnviaMSG, "Comando Fechar mindinho enviado!", Toast.LENGTH_SHORT).show()
                        startFullRecognition()
                    }
                    //COMANDOS PARA ABRIR E FECHAR 'PINÇA' COM O INDICADOR E POLEGAR
                    spokenText.equals("Abrir pinça", ignoreCase = true) -> {
                        ultimaAcao = "M"
                        Toast.makeText(this@TelaEnviaMSG, "Comando Abrir pinça enviado!", Toast.LENGTH_SHORT).show()
                        startFullRecognition()
                    }
                    spokenText.equals("Fechar pinça", ignoreCase = true) -> {
                        ultimaAcao = "N"
                        Toast.makeText(this@TelaEnviaMSG, "Comando Fechar pinça enviado!", Toast.LENGTH_SHORT).show()
                        startFullRecognition()
                    }
                    // COMANDOS PARA NAVEGAR ENTRE AS TELAS DENTRO DA TELA DE ENVIAR MENSAGEM
                    spokenText.equals("Conectar braço", ignoreCase = true) -> {
                        val intent = Intent(this@TelaEnviaMSG, TelaConexaoBT::class.java)
                        startActivity(intent)
                    }
                    spokenText.equals("Ajuda", ignoreCase = true) -> {
                        val intent = Intent(this@TelaEnviaMSG, TelaInfo::class.java)
                        startActivity(intent)
                    }
                    spokenText.equals("Home", ignoreCase = true) -> {
                        val intent = Intent(this@TelaEnviaMSG, TelaPrincipal::class.java)
                        startActivity(intent)
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
