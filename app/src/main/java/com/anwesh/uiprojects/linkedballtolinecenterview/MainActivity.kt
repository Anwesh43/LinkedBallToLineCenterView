package com.anwesh.uiprojects.linkedballtolinecenterview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.balltolinecenterview.BallToLineCenterView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BallToLineCenterView.create(this)
    }
}
