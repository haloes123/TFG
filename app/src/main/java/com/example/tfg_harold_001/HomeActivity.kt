package com.example.tfg_harold_001


import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tfg_harold_001.MainActivity.Usuario.idUsuario
import com.example.tfg_harold_001.databinding.ActivityHomeBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import java.util.*


class HomeActivity : AppCompatActivity() {
    //private var mAdView: AdView? = null
    private lateinit var binding: ActivityHomeBinding
    private lateinit var suscripcionesRecyclerView : RecyclerView
    private lateinit var suscripcionesArray : MutableList<Suscripciones>
   // private lateinit var objetoPrueba : Suscripciones
    private val db = FirebaseFirestore.getInstance()
    private lateinit var myAdapter : AdaptadorSuscripciones


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val subsRecycler = binding.recyclerSuscripciones
        val botonFlotante = binding.fab
        val publicidad = binding.adView

        MobileAds.initialize(this) {}

        val adRequest = AdRequest.Builder().build()
        publicidad.loadAd(adRequest)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
        suscripcionesArray = mutableListOf()
        subsRecycler.layoutManager = LinearLayoutManager(this)
        subsRecycler.setHasFixedSize(true)
        myAdapter = AdaptadorSuscripciones(suscripcionesArray, this)
        subsRecycler.adapter = myAdapter
        EventChangeListener()

        botonFlotante.setOnClickListener {
            addInfo()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val name = "Suscripciones Notificaciones"
        val desc = "Notificaciones"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun addInfo() {

        val inflter = LayoutInflater.from(this)
        val v = inflter.inflate(R.layout.activity_add_item,null)
        /**set view*/

        val nombreServicio = v.findViewById<EditText>(R.id.nombreServicio)
        val planServicio = v.findViewById<EditText>(R.id.planServicio)
        val precioServicio = v.findViewById<EditText>(R.id.precioServicio)
        val tipoServicio = v.findViewById<EditText>(R.id.tipoServicio)
        val estadoServicio = v.findViewById<EditText>(R.id.estadoServicio)
        val fechaNotificacion = v.findViewById<DatePicker>(R.id.fechaNotificacion)
        val horaNotificacion = v.findViewById<TimePicker>(R.id.tiempoNotificación)

        val addDialog = AlertDialog.Builder(this, R.style.MyDialog)
        addDialog.setView(v)
        addDialog.setPositiveButton("Ok"){
                dialog,_->
            val nombreNuevo = nombreServicio!!.text.toString()
            val planNuevo = planServicio!!.text.toString()
            val precioNuevo = precioServicio!!.text.toString()
            val tipoNuevo = tipoServicio!!.text.toString()
            val estadoNuevo = estadoServicio!!.text.toString()
            val fechaDiaNueva = fechaNotificacion!!.dayOfMonth.toString()
            val fechaMesNuevo = fechaNotificacion.month.toString()
            val fechaAñoNuevo = fechaNotificacion.year.toString()
            val horaNueva = horaNotificacion.hour.toString()
            val minutoNueva = horaNotificacion.minute.toString()

            scheduleNotification(nombreNuevo,fechaDiaNueva,fechaMesNuevo,fechaAñoNuevo,horaNueva, minutoNueva)

            if (nombreNuevo.isNotEmpty() && planNuevo.isNotEmpty() && precioNuevo.isNotEmpty() && tipoNuevo.isNotEmpty()){
                val NuevaSuscripcion = hashMapOf(
                    "Nombre" to nombreNuevo,
                    "Plan" to planNuevo,
                    "Precio" to precioNuevo.toDouble(),
                    "Tipo" to tipoNuevo,
                    "Estado" to estadoNuevo,
                    "IDnoti" to Notification.Identificador
                )
                db.collection("Usuarios/$idUsuario/Servicios").document(nombreNuevo).set(NuevaSuscripcion)
                myAdapter.notifyDataSetChanged()
                dialog.dismiss()
            }else{
                dialog.dismiss()
            }

        }
        addDialog.setNegativeButton("Cancelar"){
                dialog,_->
            dialog.dismiss()
        }
        addDialog.create()

        addDialog.show()

    }

    private fun scheduleNotification(
        nombreNuevo: String,
        fechaDiaNueva: String,
        fechaMesNuevo: String,
        fechaAñoNuevo: String,
        horaNueva: String,
        minutoNueva: String
    ) {
        val intent = Intent(applicationContext, Notification::class.java)
        val title = nombreNuevo
        val message = "Tu suscripción esta apunto de caducar!"
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

    }

    private fun getTime(
        fechaDiaNueva: String,
        fechaMesNuevo: String,
        fechaAñoNuevo: String,
        horaNueva: String,
        minutoNueva: String
    ): Long {

        val calendar = Calendar.getInstance()
        calendar.set(fechaAñoNuevo.toInt(),fechaMesNuevo.toInt(),
            fechaDiaNueva.toInt(),horaNueva.toInt(),minutoNueva.toInt())

        return calendar.timeInMillis
    }

    private fun EventChangeListener(){
        db.collection("Usuarios/$idUsuario/Servicios").
                addSnapshotListener(object : EventListener<QuerySnapshot>{
                    override fun onEvent(
                        value: QuerySnapshot?,
                        error: FirebaseFirestoreException?
                    ) {
                        if (error != null){
                            Log.e("Firestore Error",error.message.toString())
                            return
                        }
                        for (dc : DocumentChange in value?.documentChanges!!){
                            if (dc.type == DocumentChange.Type.ADDED){
                                suscripcionesArray.add(dc.document.toObject(Suscripciones::class.java))
                            }
                        }
                        myAdapter.notifyDataSetChanged()
                    }
                })
    }

}