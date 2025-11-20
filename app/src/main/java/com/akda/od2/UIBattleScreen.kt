package com.akda.od2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.akda.od2.domain.model.Player
import com.akda.od2.domain.service.BattleService
import com.akda.od2.domain.service.BattleStateManager
import com.akda.od2.presentation.viewmodel.CharacterCreationViewModel
import com.akda.od2.ui.ThemedButton

@Composable
fun BattleScreen(navController: NavController, viewModel: CharacterCreationViewModel) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // Permission handling for Notifications (Android 13+) - Native Implementation
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val permission = Manifest.permission.POST_NOTIFICATIONS
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                // Optional: Handle denial if necessary
            }
        )

        LaunchedEffect(Unit) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                launcher.launch(permission)
            }
        }
    }

    // Check if we have a character to play
    val activeCharacter = uiState.savedCharacters.firstOrNull()

    if (activeCharacter == null) {
        NoCharacterScreen(navController)
    } else {
        ActiveBattleScreen(activeCharacter)
    }
}

@Composable
fun NoCharacterScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Você precisa de um herói!", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Crie um personagem antes de se aventurar.", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(32.dp))
        ThemedButton(
            onClick = { navController.navigate("character_creation") },
            text = "Criar Personagem"
        )
    }
}

@Composable
fun ActiveBattleScreen(player: Player) {
    val context = LocalContext.current
    val battleLogs by BattleStateManager.battleLogs.collectAsState()
    val isRunning by BattleStateManager.isRunning.collectAsState()
    val currentPlayerState by BattleStateManager.currentPlayer.collectAsState()
    val currentMonster by BattleStateManager.currentMonster.collectAsState()

    // Initialize state manager with character if needed
    LaunchedEffect(Unit) {
        if (!isRunning && currentPlayerState == null) {
            BattleStateManager.setPlayer(player)
        }
    }

    val displayPlayer = currentPlayerState ?: player

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Header: Player Status
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A))) {
            Column(Modifier.padding(16.dp).fillMaxWidth()) {
                Text(displayPlayer.name, style = MaterialTheme.typography.headlineSmall, color = Color(0xFFD4AF37))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("PV: ${displayPlayer.currentHitPoints}/${displayPlayer.maxHitPoints}", color = if(displayPlayer.currentHitPoints < 5) Color.Red else Color.White)
                    Text("CA: ${displayPlayer.armorClass}")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // VS Section
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text("VS", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Monster Status
        if (currentMonster != null) {
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF3A2A2A))) {
                Column(Modifier.padding(16.dp).fillMaxWidth()) {
                    Text(currentMonster!!.name, style = MaterialTheme.typography.titleMedium, color = Color(0xFFFF6B6B))
                    Text("PV: ${currentMonster!!.hp}", color = Color.White)
                }
            }
        } else {
            Text("Procurando inimigos...", modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Battle Logs
        Text("Registro de Batalha:", style = MaterialTheme.typography.labelLarge)
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFF1E1E1E))
                .padding(8.dp),
            reverseLayout = true
        ) {
            items(battleLogs) { log ->
                Text(log, fontSize = 14.sp, color = Color.LightGray, modifier = Modifier.padding(vertical = 2.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Controls
        if (isRunning) {
            ThemedButton(
                onClick = {
                    val intent = Intent(context, BattleService::class.java)
                    context.stopService(intent)
                },
                text = "Fugir (Parar Batalha)"
            )
        } else {
            val isDead = (displayPlayer.currentHitPoints <= 0)
            ThemedButton(
                onClick = {
                    if (isDead) {
                        // Simple logic to revive for demo purposes or reset
                        BattleStateManager.setPlayer(player) // Reset to full? No, passing original player
                        BattleStateManager.updatePlayerHp(player.maxHitPoints) // Healed
                        BattleStateManager.clearLogs()
                    }
                    BattleStateManager.setPlayer(player)
                    val intent = Intent(context, BattleService::class.java)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(intent)
                    } else {
                        context.startService(intent)
                    }
                },
                text = if (isDead) "Ressuscitar e Lutar" else "Iniciar Aventura"
            )
        }
    }
}