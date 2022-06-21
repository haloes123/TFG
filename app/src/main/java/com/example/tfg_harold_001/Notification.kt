package com.example.tfg_harold_001

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import java.text.SimpleDateFormat
import java.util.*

const val channelID = "channel1"
const val titleExtra = "titleExtra"
const val messageExtra = "messageExtra"

class Notification : BroadcastReceiver()
{
    companion object IdNotificacion{
        var Identificador : Int = createID()
        fun createID(): Int {
            val now = Date()
            return SimpleDateFormat("ddHHmmss", Locale.US).format(now).toInt()
        }
    }
    override fun onReceive(context: Context, intent: Intent)
    {
        val notification = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(intent.getStringExtra(titleExtra))
            .setContentText(intent.getStringExtra(messageExtra))
            .build()

        Identificador = createID();
        val  manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(Identificador, notification)

    }


}