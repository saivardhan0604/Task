package com.app.myapplication

import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.app.myapplication.databinding.ActivityMainBinding
import com.app.myapplication.databinding.ActivitySignInBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val colors = arrayOf(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.MAGENTA)
    private var colorIndex = 0
    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // Get the root view
        val rootView = window.decorView

        // Start coroutine to change color every 3 seconds
        GlobalScope.launch(Dispatchers.Main) {
            while (true) {
                delay(3000) // Wait for 3 seconds
                rootView.setBackgroundColor(colors[colorIndex]) // Change color
                colorIndex = (colorIndex + 1) % colors.size // Cycle through colors
            }
        }
    }
}
