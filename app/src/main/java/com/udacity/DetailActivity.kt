package com.udacity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

/**
 *  Detail Activity to show downloadstatus using Motionlayout
 */
class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val fileName = intent.extras?.getString("fileText")

        val status = intent.extras?.getBoolean("status")

        if (status == true)
            status_detail.text = "success"
        else {
            status_detail.text = "failed"
            status_detail.setTextColor(resources.getColor(R.color.fail))
        }

        description_detail.text = fileName

        ok_button.setOnClickListener {
            this.finish()
        }
    }
}
