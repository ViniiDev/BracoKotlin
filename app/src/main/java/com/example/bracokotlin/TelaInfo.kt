package com.example.bracokotlin

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TelaInfo : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela_info) // Carrega o layout tela_info.xml

        val infoText: TextView = findViewById(R.id.btnInfo)
        infoText.text = "Informações sobre o aplicativo"

        // Caso precise configurar elementos no layout, como TextView ou Botões, você pode fazer isso aqui.
        // Exemplo: val infoText: TextView = findViewById(R.id.info_text)
    }
}

