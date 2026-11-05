package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.MazoumAiManager
import com.example.data.local.entity.HistoryItemEntity
import com.example.data.local.entity.MemoryEntity
import com.example.data.local.entity.NoteEntity
import com.example.data.local.entity.TaskEntity
import com.example.data.repository.MazoumRepository
import com.example.ui.model.ChatMessage
import com.example.ui.model.ConnectedDevice
import com.example.ui.model.MessageSender
import com.example.ui.model.Screen
import com.example.util.VoiceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val TAG = "MazoumViewModel"

class MazoumViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MazoumRepository(application)
    private val aiManager = MazoumAiManager()
    val voiceManager = VoiceManager(application)

    // Navigation stack
    private val _screenStack = MutableStateFlow(
        listOf(if (repository.hasCompletedOnboarding()) Screen.DASHBOARD else Screen.WELCOME)
    )
    val currentScreen: StateFlow<Screen> = MutableStateFlow(
        if (repository.hasCompletedOnboarding()) Screen.DASHBOARD else Screen.WELCOME
    ).apply {
        viewModelScope.launch {
            _screenStack.collect { stack ->
                this@apply.value = stack.lastOrNull() ?: Screen.DASHBOARD
            }
        }
    }

    fun navigateTo(screen: Screen) {
        val currentList = _screenStack.value.toMutableList()
        if (screen == Screen.DASHBOARD) {
            _screenStack.value = listOf(Screen.DASHBOARD)
        } else {
            if (currentList.lastOrNull() != screen) {
                currentList.add(screen)
                _screenStack.value = currentList
            }
        }
    }

    fun navigateBack(): Boolean {
        val currentList = _screenStack.value.toMutableList()
        if (currentList.size > 1) {
            currentList.removeAt(currentList.lastIndex)
            _screenStack.value = currentList
            voiceManager.stop()
            return true
        }
        return false
    }

    // User profile state
    val userName = MutableStateFlow(repository.getUserName())
    val userRole = MutableStateFlow(repository.getUserRole())
    val isMemoryEnabled = MutableStateFlow(repository.isMemoryEnabled())
    val isVoiceFeedbackEnabled = MutableStateFlow(repository.isVoiceFeedbackEnabled())
    val voiceSpeed = MutableStateFlow(repository.getVoiceSpeed())

    fun updateProfile(name: String, role: String) {
        userName.value = name
        userRole.value = role
        repository.setUserName(name)
        repository.setUserRole(role)
        repository.setOnboardingCompleted(true)
    }

    fun toggleMemory(enabled: Boolean) {
        isMemoryEnabled.value = enabled
        repository.setMemoryEnabled(enabled)
    }

    fun toggleVoiceFeedback(enabled: Boolean) {
        isVoiceFeedbackEnabled.value = enabled
        repository.setVoiceFeedbackEnabled(enabled)
    }

    fun updateVoiceSpeed(speed: Float) {
        voiceSpeed.value = speed
        repository.setVoiceSpeed(speed)
    }

    // Data lists from Room
    val notes: StateFlow<List<NoteEntity>> = repository.allNotes.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val tasks: StateFlow<List<TaskEntity>> = repository.allTasks.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val memories: StateFlow<List<MemoryEntity>> = repository.allMemories.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val history: StateFlow<List<HistoryItemEntity>> = repository.allHistory.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    // Note operations
    fun addNote(title: String, content: String, category: String) {
        viewModelScope.launch {
            repository.insertNote(title, content, category)
            repository.logHistory("NOTE", "Note créée : $title", content.take(60))
        }
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    // Task operations
    fun addTask(title: String, description: String, dueDate: String, priority: String, category: String) {
        viewModelScope.launch {
            repository.insertTask(title, description, dueDate, priority, category)
            repository.logHistory("TASK", "Tâche ajoutée : $title", "Échéance: $dueDate | Priorité: $priority")
        }
    }

    fun toggleTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.toggleTaskCompleted(task.id, !task.isCompleted)
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    // Memory operations
    fun addMemoryItem(category: String, content: String) {
        viewModelScope.launch {
            repository.addMemory(category, content)
            repository.logHistory("MEMORY", "Mémoire apprise", "$category : $content")
        }
    }

    fun deleteMemoryItem(memory: MemoryEntity) {
        viewModelScope.launch {
            repository.deleteMemory(memory)
        }
    }

    fun clearAllMemory() {
        viewModelScope.launch {
            repository.clearMemory()
        }
    }

    fun deleteHistory(id: Long) {
        viewModelScope.launch {
            repository.deleteHistory(id)
        }
    }

    // Reset all app data
    fun resetAllData() {
        viewModelScope.launch {
            repository.clearAllData()
            _chatMessages.value = listOf(
                ChatMessage(
                    sender = MessageSender.AI,
                    text = "Bonjour ${userName.value}. Toutes les données locales ont été réinitialisées. En quoi puis-je vous assister aujourd'hui ?"
                )
            )
        }
    }

    // Chat State
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = MessageSender.AI,
                text = "Bonjour ! Je suis MAZOUM TECH, votre intelligence numérique personnelle. Comment puis-je vous accompagner aujourd'hui ?"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    fun sendChatMessage(userText: String) {
        if (userText.isBlank()) return
        val current = _chatMessages.value.toMutableList()
        current.add(ChatMessage(sender = MessageSender.USER, text = userText.trim()))
        _chatMessages.value = current

        viewModelScope.launch {
            _isChatLoading.value = true
            try {
                val memoryFacts = if (isMemoryEnabled.value) {
                    memories.value.map { "${it.category}: ${it.content}" }
                } else emptyList()

                var aiResponse = ""
                aiManager.streamChat(
                    message = userText,
                    userName = userName.value,
                    userRole = userRole.value,
                    memoryFacts = memoryFacts
                ).collect { chunk ->
                    aiResponse = chunk
                    val updated = _chatMessages.value.toMutableList()
                    if (updated.lastOrNull()?.sender == MessageSender.AI) {
                        updated[updated.lastIndex] = updated.last().copy(text = chunk)
                    } else {
                        updated.add(ChatMessage(sender = MessageSender.AI, text = chunk))
                    }
                    _chatMessages.value = updated
                }

                repository.logHistory("CHAT", userText.take(40), aiResponse.take(60))

                if (isVoiceFeedbackEnabled.value && aiResponse.isNotBlank()) {
                    voiceManager.speak(aiResponse, voiceSpeed.value)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Chat failed", e)
                val updated = _chatMessages.value.toMutableList()
                val errorMsg = "Désolé, une anomalie est survenue : ${e.localizedMessage ?: "erreur réseau"}"
                updated.add(ChatMessage(sender = MessageSender.AI, text = errorMsg))
                _chatMessages.value = updated
            } finally {
                _isChatLoading.value = false
            }
        }
    }

    // Voice Assistant Mode State
    private val _voiceStatus = MutableStateFlow("Touchez le micro pour parler à MAZOUM TECH")
    val voiceStatus: StateFlow<String> = _voiceStatus.asStateFlow()

    private val _isVoiceListening = MutableStateFlow(false)
    val isVoiceListening: StateFlow<Boolean> = _isVoiceListening.asStateFlow()

    private val _voiceResponse = MutableStateFlow("")
    val voiceResponse: StateFlow<String> = _voiceResponse.asStateFlow()

    fun processVoiceQuery(query: String) {
        if (query.isBlank()) return
        _isVoiceListening.value = false
        _voiceStatus.value = "Analyse de : « $query »"

        viewModelScope.launch {
            try {
                val memoryFacts = if (isMemoryEnabled.value) {
                    memories.value.map { "${it.category}: ${it.content}" }
                } else emptyList()

                val response = aiManager.generateChatResponse(
                    message = query,
                    userName = userName.value,
                    userRole = userRole.value,
                    memoryFacts = memoryFacts
                )

                _voiceResponse.value = response
                _voiceStatus.value = "Réponse vocale en cours..."
                repository.logHistory("VOICE", query.take(40), response.take(60))
                voiceManager.speak(response, voiceSpeed.value)
            } catch (e: Exception) {
                _voiceStatus.value = "Erreur vocale : ${e.localizedMessage}"
            }
        }
    }

    // Vision State
    private val _selectedBitmap = MutableStateFlow<Bitmap?>(null)
    val selectedBitmap: StateFlow<Bitmap?> = _selectedBitmap.asStateFlow()

    private val _visionResult = MutableStateFlow<String?>(null)
    val visionResult: StateFlow<String?> = _visionResult.asStateFlow()

    private val _isVisionLoading = MutableStateFlow(false)
    val isVisionLoading: StateFlow<Boolean> = _isVisionLoading.asStateFlow()

    private val _visionAnalysisType = MutableStateFlow("ALL") // "ALL", "OCR", "ERROR", "DOCUMENT", "TRANSLATE"
    val visionAnalysisType: StateFlow<String> = _visionAnalysisType.asStateFlow()

    fun setSelectedImage(bitmap: Bitmap?) {
        _selectedBitmap.value = bitmap
        _visionResult.value = null
    }

    fun setVisionAnalysisType(type: String) {
        _visionAnalysisType.value = type
    }

    fun analyzeSelectedImage(customQuery: String) {
        val bitmap = _selectedBitmap.value ?: return
        viewModelScope.launch {
            _isVisionLoading.value = true
            try {
                val result = aiManager.analyzeImage(
                    bitmap = bitmap,
                    analysisType = _visionAnalysisType.value,
                    userQuery = customQuery
                )
                _visionResult.value = result
                repository.logHistory("VISION", "Analyse Vision (${_visionAnalysisType.value})", result.take(60))
            } catch (e: Exception) {
                _visionResult.value = "Échec de l'analyse : ${e.localizedMessage}"
            } finally {
                _isVisionLoading.value = false
            }
        }
    }

    // Studio State
    private val _studioTool = MutableStateFlow("SCRIPT") // "SCRIPT", "TEXT", "PROMPT", "SOCIAL", "VOICEOVER"
    val studioTool: StateFlow<String> = _studioTool.asStateFlow()

    private val _studioResult = MutableStateFlow<String?>(null)
    val studioResult: StateFlow<String?> = _studioResult.asStateFlow()

    private val _isStudioLoading = MutableStateFlow(false)
    val isStudioLoading: StateFlow<Boolean> = _isStudioLoading.asStateFlow()

    fun setStudioTool(tool: String) {
        _studioTool.value = tool
        _studioResult.value = null
    }

    fun generateStudioContent(topic: String, platform: String, tone: String, details: String) {
        if (topic.isBlank()) return
        viewModelScope.launch {
            _isStudioLoading.value = true
            try {
                val result = aiManager.generateCreativeStudio(
                    toolType = _studioTool.value,
                    topic = topic,
                    targetPlatform = platform,
                    tone = tone,
                    customDetails = details
                )
                _studioResult.value = result
                repository.logHistory("STUDIO", "${_studioTool.value} : $topic", result.take(60))
            } catch (e: Exception) {
                _studioResult.value = "Erreur de génération : ${e.localizedMessage}"
            } finally {
                _isStudioLoading.value = false
            }
        }
    }

    // Digital Control / Devices State
    val devices = MutableStateFlow(
        listOf(
            ConnectedDevice("1", "Smartphone Android (Cet appareil)", "Smartphone", isConnected = true, battery = 88, lastSync = "En direct"),
            ConnectedDevice("2", "Station de travail Windows", "PC Windows", isConnected = false, battery = null, lastSync = "Passerelle prête"),
            ConnectedDevice("3", "Hub Domotique & Bureau", "Domotique", isConnected = false, battery = null, lastSync = "Module en veille"),
            ConnectedDevice("4", "Extension Navigateur Web", "Navigateur", isConnected = true, battery = null, lastSync = "Prêt à synchroniser")
        )
    )

    fun toggleDevice(id: String) {
        devices.value = devices.value.map {
            if (it.id == id && it.id != "1") it.copy(isConnected = !it.isConnected) else it
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager.shutdown()
    }
}
