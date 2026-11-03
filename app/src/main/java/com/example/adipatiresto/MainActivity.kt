package com.example.adipatiresto

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// Menambahkan imageRes untuk menyimpan gambar
data class MenuItem(val name: String, val price: Int, val imageRes: Int, var quantity: Int = 0)

class MainActivity : AppCompatActivity() {
    private lateinit var rvMenu: RecyclerView
    private lateinit var tvTotal: TextView
    private lateinit var btnCheckout: Button
    private lateinit var rgOrderType: RadioGroup
    private lateinit var etTableNumber: EditText

    // Daftar Menu Lengkap Adipati Resto
    private val menuList = listOf(
        MenuItem("Ayam Bakar Madu", 25000, R.drawable.ayam_bakar),
        MenuItem("Nasi Goreng Spesial", 22000, R.drawable.nasi_goreng),
        MenuItem("Gurame Asam Manis", 45000, R.drawable.gurame),
        MenuItem("Sate Ayam Madura", 20000, R.drawable.sate_ayam),
        MenuItem("Sop Buntut", 55000, R.drawable.sop_buntut),
        MenuItem("Rendang Daging", 24000, R.drawable.rendang),
        MenuItem("Tempe Mendoan", 10000, R.drawable.mendoan),
        MenuItem("Kentang Goreng", 15000, R.drawable.kentang),
        MenuItem("Es Teh Manis", 5000, R.drawable.es_teh),
        MenuItem("Es Jeruk Manis", 7000, R.drawable.es_jeruk),
        MenuItem("Jus Alpukat", 12000, R.drawable.jus_alpukat),
        MenuItem("Kopi Susu Gula Aren", 18000, R.drawable.kopi_susu)
    )

    private var totalPrice = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvMenu = findViewById(R.id.rvMenu)
        tvTotal = findViewById(R.id.tvTotalPrice)
        btnCheckout = findViewById(R.id.btnCheckout)
        rgOrderType = findViewById(R.id.rgOrderType)
        etTableNumber = findViewById(R.id.etTableNumber)

        rvMenu.layoutManager = LinearLayoutManager(this)
        val adapter = MenuAdapter(menuList) { calculateTotal() }
        rvMenu.adapter = adapter

        rgOrderType.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rbDineIn) {
                etTableNumber.visibility = View.VISIBLE
            } else {
                etTableNumber.visibility = View.GONE
                etTableNumber.text.clear()
            }
        }

        btnCheckout.setOnClickListener {
            if (totalPrice == 0) {
                Toast.makeText(this, "Pilih menu terlebih dahulu!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val isDineIn = rgOrderType.checkedRadioButtonId == R.id.rbDineIn
            val orderType = if (isDineIn) "Makan Disini" else "Bawa Pulang"
            val table = etTableNumber.text.toString()

            if (isDineIn && table.isEmpty()) {
                Toast.makeText(this, "Silakan isi nomor meja!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val randomId = "#ORD-${(10000..99999).random()}"
            val orderItems = menuList.filter { it.quantity > 0 }

            var summary = "Tipe Pesanan: $orderType\n"
            if (isDineIn) summary += "Nomor Meja: $table\n"
            summary += "\nDaftar Menu:\n"
            for (item in orderItems) {
                summary += "- ${item.name} (${item.quantity}x)\n"
            }
            summary += "\nTotal Pembayaran: Rp $totalPrice"

            val intent = Intent(this@MainActivity, ReceiptActivity::class.java)
            intent.putExtra("ORDER_SUMMARY", summary)
            intent.putExtra("ORDER_ID", randomId)
            startActivity(intent)

            menuList.forEach { it.quantity = 0 }
            rvMenu.adapter?.notifyDataSetChanged()
            calculateTotal()
            etTableNumber.text.clear()
            rgOrderType.check(R.id.rbDineIn)
        }
    }

    private fun calculateTotal() {
        totalPrice = menuList.sumOf { it.price * it.quantity }
        tvTotal.text = "Rp $totalPrice"
    }
}

class MenuAdapter(private val menuList: List<MenuItem>, private val onQuantityChanged: () -> Unit) :
    RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivPhoto: ImageView = view.findViewById(R.id.ivMenuPhoto) // Memanggil ID Gambar
        val tvName: TextView = view.findViewById(R.id.tvMenuName)
        val tvPrice: TextView = view.findViewById(R.id.tvMenuPrice)
        val tvQty: TextView = view.findViewById(R.id.tvQuantity)
        val btnMinus: Button = view.findViewById(R.id.btnMinus)
        val btnPlus: Button = view.findViewById(R.id.btnPlus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_menu, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val menu = menuList[position]

        // Memasang gambar dan teks ke layar
        holder.ivPhoto.setImageResource(menu.imageRes)
        holder.tvName.text = menu.name
        holder.tvPrice.text = "Rp ${menu.price}"
        holder.tvQty.text = menu.quantity.toString()

        holder.btnPlus.setOnClickListener {
            menu.quantity++
            holder.tvQty.text = menu.quantity.toString()
            onQuantityChanged()
        }

        holder.btnMinus.setOnClickListener {
            if (menu.quantity > 0) {
                menu.quantity--
                holder.tvQty.text = menu.quantity.toString()
                onQuantityChanged()
            }
        }
    }

    override fun getItemCount() = menuList.size
}