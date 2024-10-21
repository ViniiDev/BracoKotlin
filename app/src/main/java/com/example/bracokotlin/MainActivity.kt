package com.example.bracokotlin

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import android.content.Intent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException
import java.io.OutputStream
import java.util.UUID

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Build
import androidx.annotation.RequiresApi
// import com.example.bracokotlin.TelaEnviaMSG

class MainActivity : BaseActivity() {

    private lateinit var bt_Adapter: BluetoothAdapter // Adaptador Bluetooth
    private lateinit var dispPareados_Adapter: ArrayAdapter<String> // Adaptador para dispositivos pareados
    private lateinit var conecta_btn: Button // Botão para conectar
    private lateinit var lista_dispositivos: ListView // Lista de dispositivos pareados
    private var socket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null

    companion object {
        private const val REQUEST_ENABLE_BT = 1
        private const val REQUEST_BLUETOOTH_PERMISSION = 2

        // Singleton para manter a conexão Bluetooth acessível em diferentes telas
        var bluetoothSocket: BluetoothSocket? = null
        var bluetoothOutputStream: OutputStream? = null
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializando os componentes do layout
        conecta_btn = findViewById(R.id.btnConnect)
        lista_dispositivos = findViewById(R.id.lvDevices)

        bt_Adapter = BluetoothAdapter.getDefaultAdapter()

        // Verificação se o dispositivo suporta Bluetooth
        if (bt_Adapter == null) {
            Toast.makeText(this, "Bluetooth não suportado", Toast.LENGTH_SHORT).show()
            return
        }

        // Listener para o botão de conectar
        conecta_btn.setOnClickListener {
            checkBluetoothPermissionsAndConnect()
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun checkBluetoothPermissionsAndConnect() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN),
                REQUEST_BLUETOOTH_PERMISSION
            )
        } else {
            if (!bt_Adapter.isEnabled) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            } else {
                mostrarDispPareados()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_BLUETOOTH_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkBluetoothPermissionsAndConnect()
            } else {
                Toast.makeText(this, "Permissão do Bluetooth negada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
            Toast.makeText(this, "Bluetooth ativado", Toast.LENGTH_SHORT).show()
            mostrarDispPareados()
        } else {
            Toast.makeText(this, "Bluetooth não ativado", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun mostrarDispPareados() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN),
                REQUEST_BLUETOOTH_PERMISSION
            )
            return
        }

        val dispPareados: Set<BluetoothDevice>? = bt_Adapter.bondedDevices
        val listaDisp = ArrayList<String>()

        if (dispPareados != null && dispPareados.isNotEmpty()) {
            for (dispositivo in dispPareados) {
                listaDisp.add("${dispositivo.name}\n${dispositivo.address}")
            }
        } else {
            Toast.makeText(this, "Nenhum dispositivo pareado encontrado", Toast.LENGTH_SHORT).show()
        }

        dispPareados_Adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaDisp)
        lista_dispositivos.adapter = dispPareados_Adapter

        lista_dispositivos.setOnItemClickListener { _, _, position, _ ->
            val dispInfo = listaDisp[position]
            val enderecoMAC = dispInfo.substring(dispInfo.length - 17)
            conecta_HC06(enderecoMAC)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun conecta_HC06(enderecoMAC: String) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN),
                REQUEST_BLUETOOTH_PERMISSION
            )
            return
        }

        val dispositivo: BluetoothDevice = bt_Adapter.getRemoteDevice(enderecoMAC)
        val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

        try {
            socket = dispositivo.createRfcommSocketToServiceRecord(uuid)
            bt_Adapter.cancelDiscovery()
            socket?.connect()
            outputStream = socket?.outputStream

            bluetoothSocket = socket
            bluetoothOutputStream = outputStream

            Toast.makeText(this, "Conectado ao dispositivo", Toast.LENGTH_SHORT).show()

            // Iniciar a tela de envio de mensagem após a conexão
            val intent = Intent(this, TelaEnviaMSG::class.java)
            startActivity(intent)

        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Falha ao conectar", Toast.LENGTH_SHORT).show()
        }
    }
}


