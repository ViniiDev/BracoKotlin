package com.example.bracokotlin
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView //para usar animações em json


//essa tela serve apenas para iniciar o app, mostrando o logo
class TelaInicial : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.tela_inicial) // Definindo o layout da tela

        // carregando um json animado usando o lottie
        val animationView = findViewById<LottieAnimationView>(R.id.lottie_logo)
        animationView.setAnimation(R.raw.robo_run)
        animationView.playAnimation()

        // Usando a classe Handler para esperar 3 segundos antes de iniciar a MainActivity
        Handler(Looper.getMainLooper()).postDelayed({

            // iniciando a mainactivity
            startActivity(Intent(this, TelaPrincipal::class.java))
            finish() // fechando a tela inicial
        }, 3000)
    }
}
