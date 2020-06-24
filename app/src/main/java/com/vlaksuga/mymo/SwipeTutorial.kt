package com.vlaksuga.mymo

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import androidx.vectordrawable.graphics.drawable.ArgbEvaluator
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_swipe_tutorial.*

class SwipeTutorial : AppCompatActivity() {

    private lateinit var viewPager: ViewPager
    private lateinit var introAdapter: IntroAdapter
    private lateinit var intros : List<Intro>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_swipe_tutorial)

        App.prefs.introPassed = true

        intros = ArrayList<Intro>()
        (intros as ArrayList<Intro>).add(Intro(R.drawable.colorful, getString(R.string.intro_title1), getString(R.string.intro_desc1)))
        (intros as ArrayList<Intro>).add(Intro(R.drawable.buttons, getString(R.string.intro_title2), getString(R.string.intro_desc2)))
        (intros as ArrayList<Intro>).add(Intro(R.drawable.delete, getString(R.string.intro_title3),getString(R.string.intro_desc3)))

        introAdapter = IntroAdapter(intros, this)
        viewPager = findViewById(R.id.intro_viewPager)
        viewPager.adapter = introAdapter
        viewPager.setPadding(50, 160, 50, 0)



        val introPassButton = findViewById<Button>(R.id.start_button)
        introPassButton.setOnClickListener {
            val toMainIntent = Intent(this, MainActivity::class.java)
            startActivity(toMainIntent)
        }

    }
}
