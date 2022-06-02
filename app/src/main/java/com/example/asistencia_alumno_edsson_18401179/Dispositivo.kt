package com.example.asistencia_alumno_edsson_18401179

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteException

class Dispositivo(este: Context) {
    private var este = este
    private var err = ""
    var nombre = ""
    var direccion = ""
    var contador=0

    fun vacia():Boolean{
        var bd = BaseDatos(este,"dispositivo", null,1)
        var dis = Dispositivo(este)
        err = ""

        try {
            var tabla = bd.readableDatabase
            var SQL_SELECT = "SELECT COUNT(*) FROM DISPOSITIVO "

            var cursor = tabla.rawQuery(SQL_SELECT, null)
            if(cursor.moveToFirst()){
                dis.contador = cursor.getInt(0)

                if (dis.contador==0){
                    return false
                }
            }
        }catch (err: SQLiteException){
            this.err = err.message!!
        }finally {
            bd.close()
        }
        return true
    }

    fun insertar():Boolean {
        var bd = BaseDatos(este,"dispositivo", null,1)
        err = ""
        try {
            val tabla = bd.writableDatabase
            var datos = ContentValues()

            datos.put("NOMBRE",nombre)
            datos.put("MAC",direccion)

            var resultado = tabla.insert("DISPOSITIVO",null,datos)
            if(resultado == -1L){
                return false
            }
        }catch (err: SQLiteException){
            this.err = err.message!!
            return false
        }finally {
            bd.close()
        }
        return true
    }

    fun mostrar() :Dispositivo{
        var bd = BaseDatos(este,"dispositivo", null,1)
        var dis = Dispositivo(este)
        err = ""

        try {
            var tabla = bd.readableDatabase
            var SQL_SELECT = "SELECT MAC FROM DISPOSITIVO "

            var cursor = tabla.rawQuery(SQL_SELECT, null)
            if(cursor.moveToFirst()){
                dis.direccion = cursor.getString(0)
                println("------------------------"+dis.direccion+"------------------------------------")
            }
        }catch (err: SQLiteException){
            this.err = err.message!!
        }finally {
            bd.close()
        }
        return dis
    }

    fun actualizar(): Boolean{
        var bd = BaseDatos(este,"dispositivo", null,1)
        try {
            var tabla = bd.writableDatabase
            val datos = ContentValues()
            datos.put("NOMBRE",nombre)
            datos.put("MAC",direccion)


            val respuesta = tabla.update("DISPOSITIVO",datos,"MAC=?"
                ,arrayOf(direccion.toString()))
            if(respuesta==0){
                return false
            }

        }catch (err: SQLiteException){
            AlertDialog.Builder(este)
                .setTitle("Error")
                .setMessage(err.message!!)
                .show()
            return false
        }finally {
            bd.close()
        }
        return true
    }
}