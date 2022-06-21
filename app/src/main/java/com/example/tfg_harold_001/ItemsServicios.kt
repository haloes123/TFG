package com.example.tfg_harold_001

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.example.tfg_harold_001.MainActivity.Usuario.idUsuario
import com.example.tfg_harold_001.databinding.ActivityItemsServiciosBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*


private lateinit var binding: ActivityItemsServiciosBinding


class ItemsServicios : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemsServiciosBinding.inflate(layoutInflater)
        val view = binding.root
        val botonFlotante = binding.fab
        setContentView(view)
        getExtras(botonFlotante)


    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun getExtras(botonFlotante: FloatingActionButton) {
        val bundle = intent.extras
        val name = bundle?.get("nombre")
        val plan = bundle?.get("plan")
        val precio = bundle?.get("precio")
        val tipo = bundle?.get("tipo")
        val estado = bundle?.get("estado")
        val idnoti = bundle?.get("idnoti")
        binding.TituloNombre.setText(name.toString())
        binding.TipoServicio.setText(tipo.toString())
        binding.PlanServicio.setText(plan.toString())
        binding.PrecioServicio.setText(precio.toString())
        binding.EstadoServicio.setText(estado.toString())
        binding.fechaNotificacion.updateDate(2023,2,1)


        botonFlotante.setOnClickListener {
            val fechaDiaNueva = binding.fechaNotificacion.dayOfMonth
            val fechaMesNuevo = binding.fechaNotificacion.month
            val fechaAñoNuevo = binding.fechaNotificacion.year
            val horaNueva = binding.tiempoNotificacion.hour
            val minutoNueva = binding.tiempoNotificacion.minute
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager


            notificationManager.cancel(idnoti.toString().toInt())

            val intent = Intent(applicationContext, Notification::class.java)
            val title = name.toString()
            val message = "Tu suscripcion esta apunto de caducar!"
            intent.putExtra(titleExtra, title)
            intent.putExtra(messageExtra, message)

            val pendingIntent = PendingIntent.getBroadcast(
                applicationContext,
                Notification.Identificador,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val time = getTime(fechaDiaNueva,fechaMesNuevo,fechaAñoNuevo, horaNueva, minutoNueva)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    time,
                    pendingIntent
                )
            }

            db.collection("Usuarios/${idUsuario}/Servicios").document(name.toString())
                .update(
                    "Estado", binding.EstadoServicio.text.toString(),
                    "IDnoti", Notification.Identificador,
                    "Plan", binding.PlanServicio.text.toString(),
                    "Precio", binding.PrecioServicio.text.toString().toDouble(),
                    "Tipo", binding.TipoServicio.text.toString()
                )

            startActivity(Intent(this,HomeActivity::class.java))
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this, HomeActivity::class.java))
    }

    private fun getTime(
        fechaDiaNueva: Int,
        fechaMesNuevo: Int,
        fechaAñoNuevo: Int,
        horaNueva: Int,
        minutoNueva: Int
    ): Long {

        val calendar = Calendar.getInstance()
        calendar.set(fechaAñoNuevo,fechaMesNuevo,
            fechaDiaNueva,horaNueva,minutoNueva)

        return calendar.timeInMillis
    }
}