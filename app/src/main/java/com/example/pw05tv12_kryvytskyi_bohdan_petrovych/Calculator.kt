package com.example.pw05tv12_kryvytskyi_bohdan_petrovych

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlin.math.pow

@Composable
fun ReliabilityCalculator() {
    var connection by remember { mutableStateOf("6") }
    var accidentPrice by remember { mutableStateOf("23.6") }
    var planedPrice by remember { mutableStateOf("17.6") }

    var calculationResult by remember { mutableStateOf<CalculationResult?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = connection,
            onValueChange = { connection = it },
            label = { Text("Підключення (n)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = accidentPrice,
            onValueChange = { accidentPrice = it },
            label = { Text("Ціна аварії (ac_price)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = planedPrice,
            onValueChange = { planedPrice = it },
            label = { Text("Планова ціна (pl_price)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                calculationResult = calculateReliability(
                    connection.toFloatOrNull() ?: 6f,
                    accidentPrice.toFloatOrNull() ?: 23.6f,
                    planedPrice.toFloatOrNull() ?: 17.6f
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Розрахувати")
        }

        Spacer(modifier = Modifier.height(16.dp))

        calculationResult?.let { result ->
            Card(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    ResultRow("Частота відмов (W_oc)", result.wOc)
                    ResultRow("Середній час відновлення (t_v_oc)", result.tvOc, "рік^-1")
                    ResultRow("Коефіцієнт аварійного простою (k_a_oc)", result.kaOc, "год")
                    ResultRow("Коефіцієнт планового простою (k_p_oc)", result.kpOc)
                    ResultRow("Частота відмов (W_dk)", result.wDk, "рік^-1")
                    ResultRow("Частота відмов з урахуванням вимикача (W_dc)", result.wDc, "рік^-1")
                    Text("Математичні сподівання:")
                    ResultRow("аварійних поломок (math_W_ned_a)", result.mathWNedA, "кВт*год")
                    ResultRow("планових поломок (math_W_ned_p)", result.mathWNedP, "кВт*год")
                    ResultRow("збитків (math_loses)", result.mathLoses, "грн")
                }
            }
        }
    }
}

@Composable
private fun ResultRow(label: String, value: Float, unit: String = "") {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            softWrap = true,
            maxLines = Int.MAX_VALUE
        )
        Text(
            text = "${String.format("%.4f", value)} $unit",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}

private data class CalculationResult(
    val wOc: Float,
    val tvOc: Float,
    val kaOc: Float,
    val kpOc: Float,
    val wDk: Float,
    val wDc: Float,
    val mathWNedA: Float,
    val mathWNedP: Float,
    val mathLoses: Float
)

private fun calculateReliability(
    n: Float,
    accidentPrice: Float,
    planedPrice: Float
): CalculationResult {
    val wOc = 0.01f + 0.07f + 0.015f + 0.02f + 0.03f * n
    val tvOc = (0.01f * 30 + 0.07f * 10 + 0.015f * 100 + 0.02f * 15 + (0.03f * n) * 2) / wOc
    val kaOc = (wOc * tvOc) / 8760
    val kpOc = 1.2f * (43f / 8760f)
    val wDk = 2 * wOc * (kaOc + kpOc)
    val wDc = wDk + 0.02f

    val mathWNedA = 0.01f * 45f * 10f.pow(-3) * 5.12f * 10f.pow(3) * 6451f
    val mathWNedP = 4f * 10f.pow(3) * 5.12f * 10f.pow(3) * 6451f
    val mathLoses = accidentPrice * mathWNedA + planedPrice * mathWNedP

    return CalculationResult(
        wOc = wOc,
        tvOc = tvOc,
        kaOc = kaOc,
        kpOc = kpOc,
        wDk = wDk,
        wDc = wDc,
        mathWNedA = mathWNedA,
        mathWNedP = mathWNedP,
        mathLoses = mathLoses
    )
}