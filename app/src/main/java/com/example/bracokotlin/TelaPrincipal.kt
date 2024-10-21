package com.example.bracokotlin

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
// import android.widget.Button
import android.widget.LinearLayout

class TelaPrincipal : BaseActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela_principal) // define o layout da tela principal

        val btnConectaBT = findViewById<LinearLayout>(R.id.btnConectaBT)
        val btnEnviaMSG = findViewById<LinearLayout>(R.id.btnEnviaMSG)
        val btnInfo = findViewById<LinearLayout>(R.id.informacoes_ajuda)

        // mandar para a tela de conexão com o HC06
        btnConectaBT.setOnClickListener {
            val intent = Intent(this, TelaConexaoBT::class.java)
            startActivity(intent)
        }

        // mandar para a tela de envio de mensagens
        btnEnviaMSG.setOnClickListener {
            val intent = Intent(this, TelaEnviaMSG::class.java)
            startActivity(intent)
        }
        // mandar para a tela de informações
        btnInfo.setOnClickListener {
            val intent = Intent(this, TelaInfo::class.java)
            startActivity(intent)
        }
    }
}