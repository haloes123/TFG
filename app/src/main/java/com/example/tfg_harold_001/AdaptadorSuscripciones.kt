package com.example.tfg_harold_001

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.tfg_harold_001.MainActivity.Usuario.idUsuario
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class AdaptadorSuscripciones (private val Suscripciones : MutableList<Suscripciones>, val c:Context) : RecyclerView.Adapter<AdaptadorSuscripciones.ViewHolder>() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_elementos,viewGroup, false)
        return  ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {

        val currentitem = Suscripciones[i]

        viewHolder.nombre.text = currentitem.Nombre
        viewHolder.plan.text = currentitem.Plan
        viewHolder.precio.text = currentitem.Precio.toString()
        viewHolder.tipo.text = currentitem.Tipo
        viewHolder.estado.text = currentitem.Estado
        viewHolder.idNotificacion.text = currentitem.IDnoti.toString()
    }

    override fun getItemCount(): Int {
        return Suscripciones.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var nombre: MaterialTextView
        var plan: MaterialTextView
        var precio : MaterialTextView
        var tipo : MaterialTextView
        var estado : MaterialTextView
        var idNotificacion : MaterialTextView

        var mMenus: ImageView

        init {

            nombre = itemView.findViewById(R.id.nombreServicio)
            plan = itemView.findViewById(R.id.plan)
            precio = itemView.findViewById(R.id.precio)
            tipo = itemView.findViewById(R.id.tipoSuscripcion)
            estado = itemView.findViewById(R.id.estado)
            mMenus = itemView.findViewById(R.id.mMenu)
            idNotificacion = itemView.findViewById(R.id.notificacion)
            mMenus.setOnClickListener { popupMenus(it) }



        }

        private fun popupMenus(v :View) {
            val position = Suscripciones[adapterPosition]
            val popupMenus = PopupMenu(c,v)
            popupMenus.inflate(R.menu.show_menu)
            popupMenus.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.editText->{
                        val i = Intent(itemView.context, ItemsServicios::class.java)
                        i.putExtra("nombre", nombre.text)
                        i.putExtra("plan", plan.text)
                        i.putExtra("precio", precio.text)
                        i.putExtra("tipo", tipo.text)
                        i.putExtra("estado", estado.text)
                        i.putExtra("idnoti", idNotificacion.text)
                        itemView.context.startActivity(i)
                        true
                    }
                    R.id.deleteText->{
                        AlertDialog.Builder(c)
                            .setTitle("Eliminar")
                            .setIcon(R.drawable.ic_warning)
                            .setMessage("¿Esta seguro que quieres borrar la suscripcion?")
                            .setPositiveButton("Si"){
                                dialog,_->
                                db.collection("Usuarios/$idUsuario/Servicios").document(
                                    nombre.text.toString()
                                ).delete()
                                Suscripciones.removeAt(adapterPosition)
                                notifyDataSetChanged()
                                dialog.dismiss()
                            }
                            .setNegativeButton("No"){
                                dialog,_->
                                dialog.dismiss()
                            }
                            .create()
                            .show()

                        true
                    }
                    else-> true
                }
            }

            popupMenus.show()
            val popup = PopupMenu::class.java.getDeclaredField("mPopup")
            popup.isAccessible = true
            val menu = popup.get(popupMenus)
            menu.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                .invoke(menu, true)
        }
    }
}