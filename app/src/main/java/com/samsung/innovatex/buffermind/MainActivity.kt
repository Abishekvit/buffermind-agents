// File: app/src/main/java/com/samsung/innovatex/buffermind/MainActivity.kt

package com.samsung.innovatex.buffermind

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.samsung.innovatex.buffermind.feature.player.PlayerActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, PlayerActivity::class.java)
        startActivity(intent)
        finish()
    }
}