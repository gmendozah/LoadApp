package com.udacity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        val button = findViewById<Button>(R.id.ok_button)
        val fileNameTV = findViewById<TextView>(R.id.file_name)
        val statusTV = findViewById<TextView>(R.id.status)
        val extras = intent.extras
        if (extras != null) {
            fileNameTV.text = extras.getString("file_name")
            statusTV.text = extras.getString("status")
            if (statusTV.text.toString() == getString(R.string.status_successful)) {
                statusTV.setTextColor(Color.GREEN)
            } else {
                statusTV.setTextColor(Color.RED)
            }
        }

        button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

}
