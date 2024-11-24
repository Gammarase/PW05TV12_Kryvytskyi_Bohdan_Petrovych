package com.example.pw05tv12_kryvytskyi_bohdan_petrovych

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReliabilityCalculator()
        }
    }
}
