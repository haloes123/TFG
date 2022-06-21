package com.example.tfg_harold_001

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.tfg_harold_001.databinding.ActivityAuthBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


private var mAuth: FirebaseAuth? = null
private lateinit var binding: ActivityAuthBinding


class MainActivity : AppCompatActivity() {
    companion object Usuario{
        var idUsuario : String = ""
    }
    private val db = FirebaseFirestore.getInstance()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        mAuth = FirebaseAuth.getInstance()

        binding.singUp.setOnClickListener {
            binding.singUp.background = resources.getDrawable(R.drawable.switch_trcks,null)
            binding.singUp.setTextColor(resources.getColor(R.color.black,null))
            binding.logIn.background = null
            binding.LoginButton.visibility = View.GONE
            binding.RegisterButton.visibility = View.VISIBLE
            binding.singUpLayout.visibility = View.VISIBLE
            binding.logInLayout.visibility = View.GONE
            binding.singUp.setTextColor(resources.getColor(R.color.black,null))
        }
        binding.logIn.setOnClickListener {
            binding.singUp.background = null
            binding.singUp.setTextColor(resources.getColor(R.color.black,null))
            binding.logIn.background = resources.getDrawable(R.drawable.switch_trcks,null)
            binding.RegisterButton.visibility = View.GONE
            binding.LoginButton.visibility = View.VISIBLE
            binding.singUpLayout.visibility = View.GONE
            binding.logInLayout.visibility = View.VISIBLE
            binding.logIn.setTextColor(resources.getColor(R.color.black,null))
        }

        setup()
    }

    private fun setup() {
        val i = Intent(this, HomeActivity::class.java)
        title = "Autenticacion"
        binding.LoginButton.setOnClickListener {
                mAuth!!
                    .signInWithEmailAndPassword(
                    binding.emailEditText.text.toString(), binding.PasswordEditText.text.toString()).addOnCompleteListener {

                        if (it.isSuccessful){
                            idUsuario = mAuth!!.uid.toString()
                            startActivity(i)
                        }else{
                            showAlert()
                        }
                }
        }

        binding.RegisterButton.setOnClickListener {

            if(binding.ContraseARegistro.text.toString() == binding.ContraseARegistro2.text.toString()){
                val i = Intent(this, HomeActivity::class.java)
                mAuth!!
                    .createUserWithEmailAndPassword(
                        binding.emailRegistro.text.toString(), binding.ContraseARegistro.text.toString()).addOnCompleteListener {
                        if (it.isSuccessful){
                            idUsuario = mAuth!!.uid.toString()
                            val user = hashMapOf(
                                "Nombre" to binding.Nombre.text.toString(),
                            )
                            db.collection("Usuarios")
                                .document(mAuth!!.uid.toString()).set(user)
                                .addOnSuccessListener { documentReference ->
                                    Toast.makeText(this, "Cuenta creada con exito", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Ha habido un fallo al crear la cuenta.", Toast.LENGTH_SHORT).show()
                                }
                            mAuth!!
                                .signInWithEmailAndPassword(
                                    binding.emailRegistro.text.toString(), binding.ContraseARegistro.text.toString()).addOnCompleteListener {

                                    if (it.isSuccessful){
                                        idUsuario = mAuth!!.uid.toString()
                                        Toast.makeText(this, idUsuario, Toast.LENGTH_SHORT).show()
                                        startActivity(i)
                                    }else{
                                        showAlert()
                                    }
                                }
                        }else{
                            showAlert()
                        }
                    }

            }else{
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error de autenticando al usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }



}