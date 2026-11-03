package com.example.adipatiresto

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ReceiptActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt)

        val tvDetails = findViewById<TextView>(R.id.tvReceiptDetails)
        val tvOrderId = findViewById<TextView>(R.id.tvOrderId)
        val btnBack = findViewById<Button>(R.id.btnBackToMenu)

        // Menangkap data dari MainActivity
        val orderSummary = intent.getStringExtra("ORDER_SUMMARY")
        val orderId = intent.getStringExtra("ORDER_ID")

        // Menampilkan data ke layar
        tvDetails.text = orderSummary
        tvOrderId.text = "ID Pesanan: $orderId"

        btnBack.setOnClickListener {
            finish()
        }
    }
}