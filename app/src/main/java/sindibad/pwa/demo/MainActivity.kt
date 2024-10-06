package sindibad.pwa.demo

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import sindibad.pwa.demo.pwa.view.WebViewActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val openSindibadButton = findViewById<MaterialButton>(R.id.openBtn)
        openSindibadButton.setOnClickListener { openSindibadPWA() }
    }

    private fun openSindibadPWA() {
        startActivity(
            Intent(
                this,
                WebViewActivity::class.java
            )
        )
    }
}