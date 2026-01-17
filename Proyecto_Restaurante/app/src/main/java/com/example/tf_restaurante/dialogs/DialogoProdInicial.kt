package com.example.tf_restaurante.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.tf_restaurante.R
import com.example.tf_restaurante.model.Producto
import com.example.tf_restaurante.model.ProducSeleccionado
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.math.roundToInt


class DialogoProdInicial : DialogFragment(), View.OnClickListener {

    private lateinit var db: FirebaseDatabase
    lateinit var listenerT: OnProductoTotal
    private lateinit var auth: FirebaseAuth;
    lateinit var btnokSel: String
    private lateinit var vista: View
    private lateinit var img: ImageView
    private lateinit var producto: TextView
    private lateinit var precio: TextView
    private lateinit var arriba: Button
    private lateinit var abajo: Button
    private lateinit var ok: Button
    private lateinit var back: Button
    private lateinit var elim: Button
    private lateinit var editCant: TextView
    var cont = 0
    private var valorGral: Double = 0.0
    private lateinit var arrayPrecio: ArrayList<Double>
    var acumTot = 0.0
    lateinit var productoGr: Producto
    var botSel:Boolean=true

//Envío
    interface OnProductoTotal {
        fun onProdVeAdmin(producSeleccionado: ProducSeleccionado)
        fun onEnvBtnConfir(envBtn: String)
        fun onRecreoDI(envBtn: String)

    }
//Recibo
    companion object {
        val args = Bundle()
        fun newInstance(producto: Producto): DialogoProdInicial {
            args.putSerializable("producto", producto)
            val fragment = DialogoProdInicial()
            fragment.arguments = args
            return fragment
        }
        fun btnOkSelec(btnSel: String) {
            args.putSerializable("selBtn", btnSel)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        vista = LayoutInflater.from(context).inflate(R.layout.dialogo_prod_inic, null)
        listenerT = requireContext() as OnProductoTotal
        db = FirebaseDatabase.getInstance("https://restaurante-tfg-default-rtdb.firebaseio.com/")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        var builder = AlertDialog.Builder(requireContext())
        builder.setView(vista)
        instancias()
        auth = FirebaseAuth.getInstance();
        return builder.create()
    }


    fun instancias() {

        img = vista.findViewById(R.id.img_dialogo)
        producto = vista.findViewById(R.id.text_producto)
        precio = vista.findViewById(R.id.text_precio)
        arriba = vista.findViewById(R.id.btn_arriba)
        abajo = vista.findViewById(R.id.btn_abajo)
        ok = vista.findViewById(R.id.btn_ok)
        back = vista.findViewById(R.id.btn_cancel)
        elim = vista.findViewById(R.id.btn_eliminar)
        editCant = vista.findViewById(R.id.edit_can_pro)

    }

    override fun onStart() {
        productoGr = this.arguments?.getSerializable("producto") as Producto
        btnokSel = this.arguments?.getString("selBtn").toString()
        botSel = this.arguments?.getBoolean("selBool") as Boolean

        valorGral = productoGr.precio!!.toDouble()

        //Si es Admin, seteo y cargo valores de una forma distinto a lo normal
        if (auth.currentUser!!.email.equals("usuarioadmin@gmail.com") && (auth.currentUser!!.uid.equals("V64nPiwdlUhIIk7K8xt7upDQTsc2" ))) {
            Glide.with(requireContext()).load(productoGr.imagen).apply(RequestOptions.circleCropTransform()).into(img)
            producto.setText(productoGr.titulo!!.toString())
            elim.visibility=View.VISIBLE
            ok.setText("Cargar")
        } else {
            precio.setText("Precio:Є " + valorGral)
            Glide.with(requireContext()).load(productoGr.imagen).apply(RequestOptions.circleCropTransform()).into(img)
            producto.setText(productoGr.titulo!!.toString())
        }
        super.onStart()
    }

    override fun onResume() {

        arriba.setOnClickListener(this)
        abajo.setOnClickListener(this)
        ok.setOnClickListener(this)
        back.setOnClickListener(this)
        elim.setOnClickListener(this)
        super.onResume()
    }

    override fun onClick(v: View?) {

        when (v!!.id) {
            R.id.btn_eliminar -> {
        //Si confirmo la eliminación siendo Admin salgo del cuadro de diálogo y envío la func para recrear el Activiti en el SecondAc
             var notificacion = Snackbar.make(vista,"¿Seguro que desea eliminar? " ,Snackbar.LENGTH_INDEFINITE)
                notificacion.setAction("Confirmar") {
                    var prodReferen = db.getReference("productos")
                        .child(btnokSel)
                        .child(productoGr.titulo.toString())
                    prodReferen.setValue(null)

                    dismiss()
                    listenerT.onRecreoDI(btnokSel)
                }
                notificacion.show()

            }

            R.id.btn_arriba -> {
                cont += 1
                editCant.setText((cont).toString())

            }
            R.id.btn_abajo -> {
                cont -= 1
                if (editCant.text.toString().toInt() <= 0) {
                    Snackbar.make(v, "No es posible", Snackbar.LENGTH_SHORT).show()
                    cont = 0
                } else {

                    editCant.setText((cont).toString())
                }

            }
            R.id.btn_ok -> {
                Log.v("contador ", cont.toString())

                if (cont > 0) {

                    fun cargoDial(){
                    //Multiplica el precio x la cantidad
                        arrayPrecio = ArrayList()
                        arrayPrecio.add((valorGral) * cont.toDouble())
                        for (i in arrayPrecio) {
                            acumTot += i
                        }
                        var prodTot: ProducSeleccionado
                        //Redondeo con el roundoff
                        var roundoff =
                            (acumTot * 100).roundToInt().toDouble() / 100
                        //Cargo los valores en en el item
                        prodTot = (ProducSeleccionado(
                            productoGr.imagen,
                            cont,
                            productoGr.titulo,
                            productoGr.precio,
                            roundoff,
                            productoGr.stock
                        ))
                        //Si es 0 la cantidad no me carga nada
                        if (prodTot.cantProducto.toString().toInt()>0) {
                            listenerT.onProdVeAdmin(prodTot)
                        }
                        cont=0
                    }

                    if (auth.currentUser!!.email.equals("usuarioadmin@gmail.com") && (auth.currentUser!!.uid.equals("V64nPiwdlUhIIk7K8xt7upDQTsc2" ))) {
                      //Despues de haber recibido el string del botón selec lo reenvio desde aquí que es cuando fue confirmada la acción
                        listenerT.onEnvBtnConfir(btnokSel)
                        //Llamo a la fun que multiplica el precio x la cantidad y me carga los item de c/produc
                        cargoDial()
                        //Tiene que haber un dismiss para el Admin y otro para el Cliente,no se puede poner al final que valga para los dos porque falla
                        dismiss()
                    } else {

                        //Traigo el valor del stock en tiempo real
                            db.getReference("productos")
                                .child(btnokSel)
                                .child(productoGr.titulo.toString())
                                .child("stock")
                                .addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        var stockI = snapshot.getValue(Int::class.java)

                        //FIN Traigo el valor del stock en tiempo real
                            //Resto el valor de stock que recibo de la BD por la cantidad que consumió el cliente
                                        fun inter( btnNomRec: String,tit: String,  valorSumado: Int   ) {
                                            var prodReferen = db.getReference("productos")
                                                .child(btnNomRec)
                                                .child(tit)
                                                .child("stock")
                                            prodReferen.setValue(valorSumado)
                                        }
                            //FIN Resto el valor de stock que recibo de la BD por la cantidad que consumió el cliente

                                //Valido si hay productos en stock de la BD
                                        if ((cont <= stockI!!) && (stockI > 0)) {
                                            inter(  btnokSel, productoGr.titulo.toString(),(stockI - cont) )
                                            //Llamo a la fun que multiplica el precio x la cantidad y me carga los item de c/produc
                                            cargoDial()
                                            //Tiene que haber un dismiss para el Admin y otro para el Cliente,no se puede poner al final que valga para los dos porque falla
                                            dismiss()
                                        } else if (stockI == 0 && cont > 0) {
                                            Snackbar.make(vista,"No hay de este producto disponible", Snackbar.LENGTH_SHORT ).show()
                                        } else if (cont > stockI) {
                                            Snackbar.make(vista,"Supera el stock, usted puede seleccionar ${stockI} de este producto",Snackbar.LENGTH_SHORT).show()
                                        }
                                //FIN Valido si hay productos en stock de la BD

                                    }
                                    override fun onCancelled(error: DatabaseError) {
                                        Snackbar.make(vista,"Error en la conexión", Snackbar.LENGTH_SHORT).show()
                                    }
                                })
                    }
                } else {
                    Snackbar.make(vista, "Ingrese un valor mayor que 1! ", Snackbar.LENGTH_SHORT).show()
                }
            }
            R.id.btn_cancel -> {
                dismiss()
            }
        }
    }}

