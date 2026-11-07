package com.ushub.injector.ui

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

val NeonCyan = Color(0xFF00E5FF)
val NeonRed = Color(0xFFFF1744)
val CyberDark = Color(0xFF0B0E14)
val CyberCard = Color(0xFF161C26)

@Composable
fun ModernFloatingMenu(
    onHeadlockChanged: (Boolean) -> Unit,
    onEspChanged: (Boolean) -> Unit,
    onMinimizeClick: () -> Unit
) {
    var headlockActive by remember { mutableStateOf(false) }
    var espActive by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .width(290.dp)
            .padding(4.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = CyberCard.copy(alpha = 0.90f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("LUAHUB V1.0.0", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                Text("[ACTIVE RUNNING]", fontSize = 10.sp, color = NeonCyan.copy(alpha = 0.6f))
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = NeonCyan.copy(alpha = 0.2f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // FITUR HEADLOCK
            FeatureRow(title = "Headlock (Lock Kepala)", isActive = headlockActive) {
                headlockActive = it
                onHeadlockChanged(it)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // FITUR ESP WALLHACK
            FeatureRow(title = "ESP X-Ray Wallhack", isActive = espActive) {
                espActive = it
                onEspChanged(it)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onMinimizeClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = NeonRed.copy(alpha = 0.8f)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("MINIMIZE PANEL", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun FeatureRow(title: String, isActive: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(CyberDark.copy(alpha = 0.5f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 13.sp, color = if (isActive) Color.White else Color.Gray)
        Switch(
            checked = isActive,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan, checkedTrackColor = NeonCyan.copy(alpha = 0.3f))
        )
    }
}
