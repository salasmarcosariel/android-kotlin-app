package com.example.tf_restaurante

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tf_restaurante.adapter.AdaptadorProducto
import com.example.tf_restaurante.adapter.AdaptadorProdSelec
import com.example.tf_restaurante.databinding.ActivitySecondBinding
import com.example.tf_restaurante.dialogs.DialogoProdInicial
import com.example.tf_restaurante.dialogs.DialogoProdSelec
import com.example.tf_restaurante.model.Producto
import com.example.tf_restaurante.model.ProducSeleccionado
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SecondActivity : AppCompatActivity(), View.OnClickListener, DialogoProdInicial.OnProductoTotal,DialogoProdSelec.OnRecreoProdSel {
    private var nombre: String? = null
    private lateinit var binding: ActivitySecondBinding
    private lateinit var db: FirebaseDatabase
    private lateinit var adaptador: AdaptadorProducto
    private lateinit var adaptadorTot: AdaptadorProdSelec
    lateinit var aryProductos: ArrayList<Producto>
    lateinit var aryProductosTot: ArrayList<Producto>
    lateinit var ProdGuar: ProducSeleccionado
    lateinit var arrayProdGuar: ArrayList<ProducSeleccionado>
    lateinit var aryPrBtn: ArrayList<String>
    lateinit var arrLAgrBtn: ArrayList<String>
    var acum = 0.0
    private lateinit var auth: FirebaseAuth;
    var btnokSel:String=""
    var stockAda:Int=0

    //Según lo que me pasan lo busco en la BD
    fun solicitoProd(btnPrSel: String) {
        adaptador.listado.clear()
        adaptador.notifyDataSetChanged()
        db.getReference("productos")
            .child(btnPrSel)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (i in snapshot.children) {
                        val producto = i.getValue(Producto::class.java)
                        adaptador.prodIndiv(producto!!)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Snackbar.make(binding.root,"Error en la conexión", Snackbar.LENGTH_SHORT).show()
                }
            })
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        db = FirebaseDatabase.getInstance("https://restaurante-tfg-default-rtdb.firebaseio.com/")
        auth = FirebaseAuth.getInstance();
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)
        instancias()
        configurarRecycler()

        binding.recyThird

        binding.btnComida.setOnClickListener(this)
        binding.btnPostre.setOnClickListener(this)
        binding.btnPagar.setOnClickListener(this)
        binding.btnBebida.setOnClickListener(this)

        //SI ES ADMIN CAMBIO EL NOMBRE DEL BTN
        if (auth.currentUser!!.email.equals("usuarioadmin@gmail.com") && (auth.currentUser!!.uid.equals("V64nPiwdlUhIIk7K8xt7upDQTsc2"))) {
           binding.btnPagar.setText("CARGAR PRODUCTOS")
        }
        //LLAMO A LA FUN DEL ADAP Y LE ENVIO EL STRIG PARA TENER EL NOMBRE DEL BTN SELECCIONADO
        adaptador.agreBtn(btnokSel)

            solicitoProd("bebidas")
            DialogoProdInicial.btnOkSelec("bebidas")



    }

    private fun configurarRecycler() {
        binding.recyThird.adapter = adaptador
        binding.recyThird.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
    }

    private fun instancias() {
        arrLAgrBtn = ArrayList()
        aryProductos = ArrayList()
        aryProductosTot = ArrayList()
        arrayProdGuar = ArrayList()
        aryPrBtn = ArrayList()
        nombre = intent.extras!!.getString("nombre")
        adaptador = AdaptadorProducto(this, aryProductos, supportFragmentManager,aryPrBtn)
        adaptadorTot = AdaptadorProdSelec(this, arrayProdGuar,stockAda)
    }

    override fun onClick(v: View?) {

        when (v!!.id) {
            R.id.btn_pagar -> {
                adaptadorTot.notifyDataSetChanged()
                for (i in arrayProdGuar) {
                    acum = acum + i.valorTotal.toString().toDouble()
                }
                val dialogoTot =DialogoProdSelec.newInstance(arrayProdGuar, acum, nombre.toString())
                dialogoTot.show(supportFragmentManager, "")
                acum -= acum
                DialogoProdSelec.tipoProduc(arrLAgrBtn)
            }
            R.id.btn_bebida -> {
                //Llamo al producto indicado por el btn seleccionado x 3
                solicitoProd("bebidas")
                DialogoProdInicial.btnOkSelec("bebidas")
            }

            R.id.btn_comida -> {
                solicitoProd("comidas")
                DialogoProdInicial.btnOkSelec("comidas")
            }
            R.id.btn_postre -> {
                solicitoProd("postres")
                DialogoProdInicial.btnOkSelec("postres")
            }
        }
    }

//Recibo el producto seleccionado cargado y se lo agrego a un arraylist producto que luego va a ir a DialTodosProd
    override fun onProdVeAdmin(producSeleccionado: ProducSeleccionado) {
        ProdGuar = producSeleccionado
        arrayProdGuar.add(ProdGuar)

    }
//Recibo un String si hay confirmación en el DialProdInd
    override fun onEnvBtnConfir(envBtn: String) {
        arrLAgrBtn.add(envBtn)
    }
//Al confirmar la notificación siendo admin eliminar producto
    override fun onRecreoDI(recibElim:String) {
    solicitoProd(recibElim)
    }
    //Al confirmar la carga de los productos siendo admin recrea el Activity
    override fun onRecreoDS() {
        recreate()
    }

}