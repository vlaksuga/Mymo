package com.vlaksuga.mymo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter


class IntroAdapter(private val intros: List<Intro>, private val context: Context) : PagerAdapter() {

    private lateinit var layoutInflater: LayoutInflater


    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return intros.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = LayoutInflater.from(context)
        val view : View = layoutInflater.inflate(R.layout.intro_item, container, false)
        val imageView : ImageView = view.findViewById(R.id.intro_imageView)
        val title : TextView = view.findViewById(R.id.intro_title_textView)
        val desc : TextView = view.findViewById(R.id.intro_desc_textView)

        imageView.setImageResource(intros[position].image)
        title.text = intros[position].title
        desc.text = intros[position].desc

        container.addView(view,0)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }
}