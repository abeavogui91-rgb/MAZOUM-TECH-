package com.example.ui.model

enum class Screen(val title: String) {
    WELCOME("Bienvenue"),
    AUTH("Profil"),
    DASHBOARD("Tableau de bord"),
    VOICE_ASSISTANT("Assistant Vocal"),
    CHAT("Assistant IA"),
    VISION("Vision Intelligente"),
    STUDIO("Studio Création"),
    NOTES_AGENDA("Notes & Agenda"),
    MEMORY("Mémoire IA"),
    HISTORY("Historique"),
    DEVICES("Contrôle Numérique"),
    SETTINGS("Sécurité & Paramètres")
}

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class MessageSender {
    USER,
    AI
}

data class ConnectedDevice(
    val id: String,
    val name: String,
    val type: String, // "Smartphone", "PC Windows", "Domotique", "Navigateur"
    val isConnected: Boolean,
    val battery: Int? = null,
    val lastSync: String
)
