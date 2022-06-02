package com.example.asistencia_alumno_edsson_18401179

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.asistencia_alumno_edsson_18401179.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    //Adaptador bluetooth
    lateinit var bluetoothA: BluetoothAdapter
    var noControl =""
    val REQUEST_CODE_ENABLE_BT: Int = 1;
    val REQUEST_CODE_DISCOVERABLE_BT: Int = 2;
    val REQUEST_CODE_VINCULAR: Int=3;


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var dis = Dispositivo(this)
            binding.estadoDispositvo.setText("Dispositivo establecido:"+dis.mostrar().direccion)
        //inicializamos adapter
        bluetoothA = BluetoothAdapter.getDefaultAdapter()
        //verificar que bluetooth este encendido o apagado
        if (bluetoothA == null) {
            binding.estadoBluetooth.text = "Bluetooth no disponible"
        } else {
            binding.estadoBluetooth.text = "Bluetooth disponible"
        }
        //Imagen estado bluetooth
        if (bluetoothA.isEnabled) {
            //bluetooth encendido
            binding.iconoBluetooth.setImageResource(R.drawable.ic_bluetooth_on)
        } else {
            //bluetooth apagado
            binding.iconoBluetooth.setImageResource(R.drawable.ic_bluetooth_off)
        }

        //Establecer numero de control
        binding.BtnNoControl.setOnClickListener {
            var campoNoControl=binding.campoNoControl.text.toString()
            if(campoNoControl == "") {
                Toast.makeText(this, "Campo noControl vacio", Toast.LENGTH_LONG).show()
            }else {
                noControl = campoNoControl
                binding.NoControl.text="No de control:${noControl}"
            }
        }

        //abre ventana vincular
        binding.vincular.setOnClickListener {
            encenderBluetooth()
            val Intent = Intent(this,Vincular::class.java)
            startActivityForResult(Intent, REQUEST_CODE_VINCULAR)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_ENABLE_BT ->
                if (resultCode == Activity.RESULT_OK) {
                    binding.iconoBluetooth.setImageResource(R.drawable.ic_bluetooth_on)
                    Toast.makeText(this, "encendido", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "No se pudo encender", Toast.LENGTH_LONG).show()
                }
            REQUEST_CODE_VINCULAR ->
                if(resultCode == Activity.RESULT_OK && data != null){
                    data.apply {
                        val nombre = getStringExtra(Vincular.NOMBRE)
                        val direccion = getStringExtra(Vincular.DIRECCION)
                        binding.estadoDispositvo.text="Dispositivo:${nombre}"
                    }
                }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    @SuppressLint("MissingPermission")
    private fun encenderBluetooth() {
        if (bluetoothA.isEnabled) {
            Toast.makeText(this, "Ya se encuentra encendido", Toast.LENGTH_LONG).show()
        } else {
            if (bluetoothA?.isEnabled == false) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_CODE_ENABLE_BT)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun apagarBluetooth() {
        if (!bluetoothA.isEnabled) {
            Toast.makeText(this, "Ya se encuentra apagado", Toast.LENGTH_LONG).show()
        } else {
            bluetoothA.disable()
            binding.iconoBluetooth.setImageResource(R.drawable.ic_bluetooth_off)
            Toast.makeText(this, "Apagado", Toast.LENGTH_LONG).show()
        }
    }
}