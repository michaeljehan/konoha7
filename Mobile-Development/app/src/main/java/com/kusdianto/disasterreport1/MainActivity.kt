package com.kusdianto.disasterreport1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_user_report.setOnClickListener(this)
        btn_admin_report.setOnClickListener(this)
        btn_informasi.setOnClickListener(this)
    }
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_user_report -> {
                val moveUserReportActivity = Intent(this, UserReportActivity::class.java)
                startActivity(moveUserReportActivity)
            }
            R.id.btn_admin_report -> {
                val moveAdminReportActivity = Intent(this, AdminReportActivity::class.java)
                startActivity(moveAdminReportActivity)
            }
            R.id.btn_informasi -> {
                val moveInformasiActivity = Intent(this, InformasiActivity::class.java)
                startActivity(moveInformasiActivity)
            }
        }
    }
}
