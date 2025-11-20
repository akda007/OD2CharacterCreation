package com.akda.od2.domain.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.akda.od2.R
import com.akda.od2.domain.application.BattleSimulator
import com.akda.od2.domain.application.Monster
import com.akda.od2.domain.model.Player
import com.akda.od2.ui.MainActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Singleton to share state between Service and UI easily
object BattleStateManager {
    private val _battleLogs = MutableStateFlow<List<String>>(emptyList())
    val battleLogs: StateFlow<List<String>> = _battleLogs.asStateFlow()

    private val _currentPlayer = MutableStateFlow<Player?>(null)
    val currentPlayer: StateFlow<Player?> = _currentPlayer.asStateFlow()

    private val _currentMonster = MutableStateFlow<Monster?>(null)
    val currentMonster: StateFlow<Monster?> = _currentMonster.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    fun setPlayer(player: Player) { _currentPlayer.value = player }
    fun updatePlayerHp(hp: Int) { _currentPlayer.value = _currentPlayer.value?.copy(currentHitPoints = hp) }
    fun setMonster(monster: Monster?) { _currentMonster.value = monster }
    fun addLogs(logs: List<String>) {
        val current = _battleLogs.value.toMutableList()
        current.addAll(0, logs) // Add to top
        if (current.size > 50) current.removeAt(current.lastIndex) // Keep list small
        _battleLogs.value = current
    }
    fun setRunning(running: Boolean) { _isRunning.value = running }
    fun clearLogs() { _battleLogs.value = emptyList() }
}

class BattleService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())
    private val battleSimulator = BattleSimulator() // In a real app, Inject this
    private val NOTIFICATION_ID = 1
    private val CHANNEL_ID = "od2_battle_channel"

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()

        // Start as Foreground Service
        startForeground(NOTIFICATION_ID, createNotification("Explorando a masmorra...", "A batalha está ativa."))

        BattleStateManager.setRunning(true)
        startBattleLoop()

        return START_STICKY
    }

    private fun startBattleLoop() {
        serviceScope.launch {
            var turn = 1

            while (isActive) {
                val player = BattleStateManager.currentPlayer.value
                var monster = BattleStateManager.currentMonster.value

                if (player == null || player.currentHitPoints <= 0) {
                    stopSelf() // Stop service if player is dead or null
                    break
                }

                // Spawn monster if needed
                if (monster == null || monster.hp <= 0) {
                    if (monster != null && monster.hp <= 0) {
                        BattleStateManager.addLogs(listOf("🏆 **${monster.name} foi derrotado!** (+XP)"))
                        delay(2000)
                    }
                    monster = battleSimulator.generateRandomMonster()
                    BattleStateManager.setMonster(monster)
                    BattleStateManager.addLogs(listOf("⚔️ Um ${monster.name} selvagem apareceu!"))
                    delay(1500)
                }

                // Process Turn
                val logs = battleSimulator.processTurn(player, monster, turn)
                BattleStateManager.addLogs(logs)

                // Update State
                BattleStateManager.updatePlayerHp(player.currentHitPoints)
                BattleStateManager.setMonster(monster) // Update monster HP in state

                // Check Player Death
                if (player.currentHitPoints <= 0) {
                    sendDeathNotification(player.name)
                    BattleStateManager.addLogs(listOf("💀 **${player.name} MORREU!**"))
                    BattleStateManager.setRunning(false)
                    stopSelf()
                } else {
                    // Update Notification with status
                    val notification = createNotification(
                        "Batalha em andamento",
                        "HP: ${player.currentHitPoints} | VS ${monster.name} (HP: ${monster.hp})"
                    )
                    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.notify(NOTIFICATION_ID, notification)
                }

                turn++
                delay(3000) // Wait 3 seconds per turn (Idle pace)
            }
        }
    }

    private fun sendDeathNotification(playerName: String) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("PERSONAGEM MORREU!")
            .setContentText("Tristes notícias... $playerName caiu em combate.")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Ensure you have an icon
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(2, notification)
    }

    private fun createNotification(title: String, content: String): android.app.Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Old Dragon Battle",
                NotificationManager.IMPORTANCE_LOW // Low for ongoing, High for death
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        BattleStateManager.setRunning(false)
        serviceScope.cancel()
        super.onDestroy()
    }
}