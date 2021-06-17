package com.kusdianto.disasterreport1

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_informasi.*


class InformasiActivity : AppCompatActivity() {

    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_informasi)

        preferences = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        val name = preferences.getString("NAME", "")
        tv_nama_bencana.text = name
        val keterangan = preferences.getString("KETERANGAN", "")
        tv_keterangan.text = keterangan

        val shre = PreferenceManager.getDefaultSharedPreferences(this)
        val edit: SharedPreferences.Editor = shre.edit()
        edit.putString("imagepath", "/sdcard/imh.jpeg")
        edit.commit()
    }
}