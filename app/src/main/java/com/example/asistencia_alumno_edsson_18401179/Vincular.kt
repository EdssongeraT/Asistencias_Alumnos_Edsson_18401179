package com.example.asistencia_alumno_edsson_18401179

import android.Manifest
import android.R
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.asistencia_alumno_edsson_18401179.databinding.ActivityVincularBinding

class Vincular : AppCompatActivity() {

    lateinit var binding: ActivityVincularBinding
    lateinit var bluetoothA: BluetoothAdapter
    var arregloEncotrados = ArrayList<Dispositivo>()
    var arregloEmparejados = ArrayList<Dispositivo>()

    companion object {
        val NOMBRE=""
        val DIRECCION = ""
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVincularBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bluetoothA = BluetoothAdapter.getDefaultAdapter()

        //Obtiene emparejados
        val emparejados: Set<BluetoothDevice>? = bluetoothA?.bondedDevices
        emparejados?.forEach { dispositivo ->
            val dis= Dispositivo(this)
            val nombre = dispositivo.name
            val direccion = dispositivo.address
            dis.nombre=nombre
            dis.direccion=direccion
            arregloEmparejados.add(dis)
        }

        //llena listview emparejados
        llenarListview(arregloEmparejados,1)

       binding.btnBuscar.setOnClickListener {
           arregloEncotrados.clear()
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {

                    when (intent.action) {
                        BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                            // Device discovery started, show loading indication
                            binding.mensaje.text = "Buscando..."
                        }

                        BluetoothDevice.ACTION_FOUND -> {
                            binding.mensaje.text = "Encotrados:"
                            val dis = Dispositivo(this@Vincular)
                            val device: BluetoothDevice? =
                                intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                            val deviceName = device?.name
                            dis.nombre=deviceName.toString()
                            val deviceHardwareAddress = device?.address // MAC
                            dis.direccion= deviceHardwareAddress.toString()
                            arregloEncotrados.add(dis)
                            llenarListview(arregloEncotrados,2)
                        }
                    }
                }
            }
            val intentFilter = IntentFilter().apply {
                addAction(BluetoothDevice.ACTION_FOUND)
                addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
                addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            }
            if (bluetoothA.isDiscovering()) {

                bluetoothA.cancelDiscovery();
            }
            registerReceiver(receiver, intentFilter)
            bluetoothA.startDiscovery()


        }
        binding.listaDispositivos.setOnItemClickListener { adapterView, view, i, l ->
            listviewClickable(arregloEncotrados,i)
        }
        binding.listaEparejados.setOnItemClickListener { adapterView, view, i, l ->
            listviewClickable(arregloEmparejados,i)
        }
    }



    fun listviewClickable(arreglo:ArrayList<Dispositivo>,i:Int){
        val c = arreglo.get(i)
        AlertDialog.Builder(this)
            .setTitle("Seleccionado")
            .setMessage("Dispositivo${c.nombre}\nMAC:${c.direccion}")
            .setPositiveButton("Seleccionar") { d, i ->
                val dis = Dispositivo(this)
                dis.nombre = c.nombre
                dis.direccion = c.direccion
                if(dis.vacia()==false) {
                    dis.insertar()
                }else{
                    dis.actualizar()
                }
                val datos = Intent().apply {
                    putExtra(NOMBRE,c.nombre)
                    putExtra(DIRECCION,c.direccion)
                }
                setResult(RESULT_OK,datos)
                finish()
            }
            .setNeutralButton("Cerrar") { d, i -> }
            .show()

    }

    fun llenarListview(arreglo:ArrayList<Dispositivo>,id:Int){
        //muestra en listview
        val arres = ArrayList<String>()
        arres.clear()
        (0..arreglo.size - 1).forEach {
            val di= arreglo.get(it)
            arres.add(
                "Nombre:${di.nombre}, " + "MAC:${di.direccion}"
            )
        }
        if(id==1) {
            binding.listaEparejados.adapter = ArrayAdapter<String>(
                this, R.layout.simple_list_item_1, arres
            )
        }else{
            binding.listaDispositivos.adapter = ArrayAdapter<String>(
                this, R.layout.simple_list_item_1, arres
            )
        }

    }

    override fun onDestroy() {
        super.onDestroy()
    }
}

