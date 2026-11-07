package com.luahub.injector.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luahub.injector.OwnershipValidator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernLoginMenu(
    onLoginSuccess: () -> Unit
) {
    var inputKey by remember { mutableStateOf("") }
    var statusText by remember { mutableStateOf("MASUKKAN LISENSI LUAHUB") }
    var statusColor by remember { mutableStateOf(NeonCyan) }

    Card(
        modifier = Modifier
            .width(300.dp)
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = CyberCard.copy(alpha = 0.95f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("LUAHUB SECURITY", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
            Spacer(modifier = Modifier.height(4.dp))
            Text(statusText, fontSize = 11.sp, color = statusColor, fontWeight = FontWeight.Medium)
            
            Spacer(modifier = Modifier.height(16.dp))

            // Kolom Input Key Kustom
            TextField(
                value = inputKey,
                onValueChange = { inputKey = it },
                placeholder = { Text("Paste Key di sini...", color = Color.Gray, fontSize = 13.sp) },
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = CyberDark,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedIndicatorColor = NeonCyan,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tombol Login
            Button(
                onClick = {
                    val result = OwnershipValidator.validateUserKey(inputKey)
                    if (result.startsWith("SUCCESS")) {
                        statusText = "AKSES DITERIMA!"
                        statusColor = Color.Green
                        onLoginSuccess()
                    } else {
                        statusText = "KEY SALAH ATAU EXPIRED!"
                        statusColor = NeonRed
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("VERIFIKASI KEY", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}
