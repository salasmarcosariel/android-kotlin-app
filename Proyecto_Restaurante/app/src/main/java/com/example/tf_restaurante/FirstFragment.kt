package com.example.tf_restaurante

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.tf_restaurante.databinding.FragmentFirstBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth


class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        auth = FirebaseAuth.getInstance();
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//LOGIN
        binding.btnLogin.setOnClickListener {
            if (!binding.editNomLog.text.isEmpty() && !binding.editPassLog.text.isEmpty()) {
                    auth.signInWithEmailAndPassword(binding.editNomLog.text.toString(),binding.editPassLog.text.toString())
                        .addOnCompleteListener {
                            if (!it.isSuccessful) {
                                Snackbar.make(binding.root,"El usuario no existe",Snackbar.LENGTH_SHORT).show()
                            } else {
                                val bundle = Bundle()
                                var email :String?=null
                                if (auth.currentUser!!.email.equals("usuarioadmin@gmail.com") && (auth.currentUser!!.uid.equals("V64nPiwdlUhIIk7K8xt7upDQTsc2"))) {
                                }else{
                                    email = it.result.user!!.email
                                    email = binding.editNomLog.text.toString()
                                    bundle.putString("nombre", email)
                                    bundle.putString("uid", auth.currentUser!!.uid)
                                }
                                findNavController().navigate(R.id.action_FirstFragment_to_secondActivity,bundle)
                            }
                        }
                } else {
                    Snackbar.make(binding.root, "No ha ingresado datos", Snackbar.LENGTH_SHORT).show()
                }


            }
        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

    }

    override fun onResume() {
        super.onResume()
        //Le pongo nombre de Titulo al FirstFrag
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.base)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}