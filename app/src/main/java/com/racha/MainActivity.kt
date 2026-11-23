package com.racha

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.racha.ui.Navegacion
import com.racha.ui.TemaRacha

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TemaRacha {
                Navegacion()
            }
        }
    }
}
