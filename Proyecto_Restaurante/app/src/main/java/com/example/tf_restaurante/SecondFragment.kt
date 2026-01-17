package com.example.tf_restaurante

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.tf_restaurante.databinding.FragmentSecondBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase

    override fun onResume() {
        //Le pongo nombre de Titulo al SecFrag
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.regist)
        super.onResume()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        auth = FirebaseAuth.getInstance();
        db = Firebase.database("https://restaurante-tfg-default-rtdb.firebaseio.com/")
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnAtras.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
        binding.btnRegistr.setOnClickListener {
            // CREO USUARIO
            if (!binding.editNomReg.text.isEmpty() && !binding.editPassReg.text.isEmpty()){
                auth.createUserWithEmailAndPassword(binding.editNomReg.text.toString(),binding.editPassReg.text.toString())
                    .addOnCompleteListener {
                        if (it.isSuccessful){
                            val bundle =Bundle()
                            bundle.putString("nombre",binding.editNomReg.text.toString())
                            bundle.putString("uid",auth.currentUser!!.uid)
                            findNavController().navigate(R.id.action_SecondFragment_to_secondActivity,bundle)
                        }else{
                            Snackbar.make(binding.root,"El usuario ya existe", Snackbar.LENGTH_SHORT).show()
                        }
                    }
            }else{
                Snackbar.make(binding.root,"No ha ingresado datos", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}