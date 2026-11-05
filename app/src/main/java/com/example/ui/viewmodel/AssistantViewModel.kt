package com.example.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.repository.GeminiRepository
import com.example.ui.model.ChatMessage
import com.example.ui.model.MessageSender
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "AssistantViewModel"

/**
 * ViewModel dédié à l'écran de l'Assistant Conversationnel de MAZOUM TECH.
 * Gère l'historique des échanges, l'état de chargement et l'acheminement des requêtes
 * vers le [GeminiRepository].
 */
class AssistantViewModel(
    val geminiRepository: GeminiRepository = GeminiRepository()
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = MessageSender.AI,
                text = "Bonjour ! Je suis MAZOUM TECH, votre assistant conversationnel intelligent.\n« Votre intelligence numérique, partout avec vous. »\n\nComment puis-je vous accompagner aujourd'hui ?"
            )
        )
    )
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    fun onInputTextChanged(newText: String) {
        _inputText.value = newText
    }

    /**
     * Envoie le message saisi par l'utilisateur au modèle Gemini et diffuse la réponse
     * de l'IA en temps réel dans la liste des messages.
     *
     * @param userText Texte personnalisé optionnel (utilise inputText si non spécifié).
     */
    fun sendMessage(userText: String = _inputText.value) {
        val query = userText.trim()
        if (query.isBlank() || _isLoading.value) return

        // Réinitialisation du champ de saisie
        _inputText.value = ""

        // Ajout immédiat du message utilisateur
        val currentList = _messages.value.toMutableList()
        currentList.add(ChatMessage(sender = MessageSender.USER, text = query))
        _messages.value = currentList

        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Streaming de la réponse pour une réactivité instantanée
                geminiRepository.sendMessageStream(query).collect { chunk ->
                    val updatedList = _messages.value.toMutableList()
                    if (updatedList.lastOrNull()?.sender == MessageSender.AI) {
                        updatedList[updatedList.lastIndex] = updatedList.last().copy(text = chunk)
                    } else {
                        updatedList.add(ChatMessage(sender = MessageSender.AI, text = chunk))
                    }
                    _messages.value = updatedList
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Échec de l'échange avec l'assistant conversationnel", e)
                val updatedList = _messages.value.toMutableList()
                val errorMsg = "Désolé, une difficulté est survenue : ${e.localizedMessage ?: "Vérifiez votre connexion."}"
                updatedList.add(ChatMessage(sender = MessageSender.AI, text = errorMsg))
                _messages.value = updatedList
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Réinitialise la conversation avec un message d'accueil.
     */
    fun clearMessages() {
        _messages.value = listOf(
            ChatMessage(
                sender = MessageSender.AI,
                text = "Nouvelle session initialisée. Posez-moi vos questions !"
            )
        )
    }
}
