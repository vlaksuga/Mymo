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
        (intros as ArrayList<Intro>).add(Intro(R.drawable.page3, "그룹 관리","메모를 색이 있는 그룹으로 관리할 수 있습니다."))
        (intros as ArrayList<Intro>).add(Intro(R.drawable.page2, "검색","검색으로 원하는 메모를 쉽게 찾을 수 있습니다."))
        (intros as ArrayList<Intro>).add(Intro(R.drawable.page1, "빠른 삭제","오른쪽으로 밀어주세요. 메모가 삭제됩니다."))

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
