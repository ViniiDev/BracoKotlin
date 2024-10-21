package com.example.bracokotlin
import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException
import java.io.OutputStream
import java.util.*

class TelaConexaoBT : BaseActivity() {

    private lateinit var bt_Adapter: BluetoothAdapter // Adaptador Bluetooth para gerenciar as funcionalidades do Bluetooth
    private lateinit var dispPareados_Adapter: ArrayAdapter<String> // Adaptador para exibir dispositivos pareados
    private lateinit var conecta_btn: Button // Botão para conectar ao HC-06
    private lateinit var lista_dispositivos: ListView // Lista para exibir os dispositivos pareados
    private var socket: BluetoothSocket? = null // Conexão via socket Bluetooth
    private var outputStream: OutputStream? = null // Serve para enviar os dados

    // Objeto chamado companion para definir constantes relacionadas a permissões
    companion object {
        private const val REQUEST_ENABLE_BT = 1 // Constante que solicita a ativação do Bluetooth
        private const val REQUEST_BLUETOOTH_PERMISSION = 2 // Constante que solicita permissões do Bluetooth
    }
    // Objeto chamado Singleton, para guardar o socket e o stream de mensagem para o HC06
    object ConexaoBt {
        var socket: BluetoothSocket? = null
        var outputStream: OutputStream? = null
    }

    // Método padrão, de quando o app é criado
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela_conexao_bt) // Define o layout (que são os XML's)

        // Inicializando os componentes do layout aberto
        conecta_btn = findViewById(R.id.btnConnect) // Botão para conectar o Bluetooth
        lista_dispositivos = findViewById(R.id.lvDevices) // Lista de dispositivos pareados


        // capturando o adaptador Bluetooth do dispositivo
        bt_Adapter = BluetoothAdapter.getDefaultAdapter()

        // Verificando se o dispositivo tem Bluetooth (se for nulo exibo o erro)
        if (bt_Adapter == null) {
            Toast.makeText(this, "Bluetooth não suportado", Toast.LENGTH_SHORT).show()
            return
        }

        // quando o botão de conectar for clicado
        conecta_btn.setOnClickListener {

            // Verifico se as permissões do bluetooth não foram concedidas
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {

                // se não foram, eu solicito
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN),
                    REQUEST_BLUETOOTH_PERMISSION
                )
            } else {
                // se tenho permissão para usar o bluetooth, verifico se ele está ativado
                if (!bt_Adapter.isEnabled) {
                    //se ele não estiver eu ativo
                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
                } else {
                    // se tiver ativado eu chamo a função para exibir os dispositivos pareados
                    mostrarDispPareados()
                }
            }
        }

    }

    // Método chamado quando o usuário responde uma solicitação de permissão
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Verificando se a permissão foi para o Bluetooth
        if (requestCode == REQUEST_BLUETOOTH_PERMISSION) {

            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    // Se o usuário aceitar a permissão
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {

                        // verifico se o bluetooth está ligado
                        if (!bt_Adapter.isEnabled) {

                            // se não estiver eu ligo
                            val ligaBT = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                            startActivityForResult(ligaBT, REQUEST_ENABLE_BT)
                        } else {

                            // se estiver eu mostro os dispositivos pareados
                            mostrarDispPareados()
                        }
                    }
                } catch (e: SecurityException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Erro: Há permissões bloqueadas", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Permissão do Bluetooth negada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Método chamado após a resposta da solicitação de ativação do Bluetooth
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Verificando se a solicitação foi para ativar o Bluetooth
        if (requestCode == REQUEST_ENABLE_BT) {
            // verifico se o bluetooth foi ativado
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Bluetooth ativado", Toast.LENGTH_SHORT).show()
                // se foi, mostro os dispositivos pareados
                mostrarDispPareados()
            } else {
                Toast.makeText(this, "Bluetooth não ativado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Função que exibe dispositivos pareados
    private fun mostrarDispPareados() {
        // Verifico novamente as permissões do Bluetooth
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {

            // se não estiverem concedidas, eu solicito novamente
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN),
                REQUEST_BLUETOOTH_PERMISSION
            )
            return
        }

        // capturo a lista de dispositivos pareados
        val dispPareados: Set<BluetoothDevice>? = bt_Adapter.bondedDevices
        // Lista para armazenar nomes e endereços dos dispositivos
        val listaDisp = ArrayList<String>()

        // Se houver dispositivos pareados, adiciona seus nomes e endereços na lista
        if (dispPareados != null && dispPareados.isNotEmpty()) {
            for (dispositivo in dispPareados) {
                listaDisp.add("${dispositivo.name}\n${dispositivo.address}")
            }
        } else {
            Toast.makeText(this, "Nenhum dispositivo pareado encontrado", Toast.LENGTH_SHORT).show()
        }

        // mostro a lista de dispositivos na tela
        dispPareados_Adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaDisp)
        lista_dispositivos.adapter = dispPareados_Adapter

        // quando clicar em um dos dispositivos pareados
        lista_dispositivos.setOnItemClickListener { _, _, position, _ ->
            val dispInfo = listaDisp[position]
            val enderecoMAC = dispInfo.substring(dispInfo.length - 17) // puxando o endereço MAC do dispositivo
            conecta_HC06(enderecoMAC) // Conectando com o dispositivo selecionado
        }
    }

    // Função para conectar no HC06 utilizando o endereço MAC
    private fun conecta_HC06(enderecoMAC: String) {
        // Verificando novamente as permissões
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {

            // se não foram concedidas, solicita novamente
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN),
                REQUEST_BLUETOOTH_PERMISSION
            )
            return
        }

        // capturo o dispositivo Bluetooth pelo endereço MAC
        val dispositivo: BluetoothDevice = bt_Adapter.getRemoteDevice(enderecoMAC)
        val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // UUID padrão para o HC-06

        try {
            // Cria um socket para conseguir enviar mensagens para o HC06
            socket = dispositivo.createRfcommSocketToServiceRecord(uuid)
            bt_Adapter.cancelDiscovery() // para de procurar dispositivos (ajuda na economia de recurso)
            socket?.connect() // Conectando no HC06
            outputStream = socket?.outputStream // socket para enviar as mensagens para o HC06
            Toast.makeText(this, "Conectado ao dispositivo", Toast.LENGTH_SHORT).show()

            // Salvando o socket no singleton
            ConexaoBt.socket = socket
            ConexaoBt.outputStream = socket?.outputStream


        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Falha ao conectar", Toast.LENGTH_SHORT).show()
        }
    }
}