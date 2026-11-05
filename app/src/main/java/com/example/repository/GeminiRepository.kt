package com.example.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Content
import com.google.firebase.ai.type.TextPart
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

private const val TAG = "GeminiRepository"

/**
 * Repository dédié à l'encapsulation des interactions avec l'API Gemini via la bibliothèque `firebase-ai`.
 * Permet à MAZOUM TECH de configurer et d'envoyer des requêtes textuelles vers l'assistant intelligent.
 *
 * @param modelName Nom du modèle Gemini cible (configuré par défaut sur 'gemini-1.5-flash' selon la spécification).
 * @param fallbackModelName Modèle de secours ('gemini-3.8-flash') en cas d'indisponibilité ou dépréciation backend.
 */
class GeminiRepository(
    val modelName: String = "gemini-1.5-flash",
    private val fallbackModelName: String = "gemini-3.8-flash"
) {

    // Initialisation paresseuse du modèle principal gemini-1.5-flash
    private val generativeModel by lazy {
        Firebase.ai.generativeModel(
            modelName = modelName,
            systemInstruction = Content(
                role = "system",
                parts = listOf(
                    TextPart(
                        """
                        Tu es MAZOUM TECH, un assistant personnel intelligent de nouvelle génération, futuriste, élégant et bienveillant.
                        Slogan : « Votre intelligence numérique, partout avec vous. »
                        Langue principale : Français impeccable, clair, professionnel et structuré.
                        Mission : Répondre avec pertinence, esprit de synthèse et bienveillance aux besoins de l'utilisateur.
                        """.trimIndent()
                    )
                )
            )
        )
    }

    // Modèle de secours moderne (Gemini 3.x Flash)
    private val fallbackModel by lazy {
        Firebase.ai.generativeModel(
            modelName = fallbackModelName,
            systemInstruction = Content(
                role = "system",
                parts = listOf(
                    TextPart(
                        """
                        Tu es MAZOUM TECH, un assistant personnel intelligent de nouvelle génération.
                        Slogan : « Votre intelligence numérique, partout avec vous. »
                        Langue principale : Français.
                        """.trimIndent()
                    )
                )
            )
        )
    }

    /**
     * Envoie un message texte simple au modèle Gemini et retourne la réponse générée.
     *
     * @param prompt Requête textuelle formulée par l'utilisateur.
     * @return Texte de la réponse produite par le modèle Gemini.
     */
    suspend fun sendMessage(prompt: String): String = withContext(Dispatchers.IO) {
        require(prompt.isNotBlank()) { "Le prompt ne peut pas être vide." }

        try {
            withTimeout(60_000L) {
                val response = generativeModel.generateContent(prompt)
                response.text ?: "Aucune réponse n'a été produite."
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "Échec avec le modèle $modelName (${e.message}), tentative avec le modèle de secours $fallbackModelName", e)
            try {
                withTimeout(60_000L) {
                    val response = fallbackModel.generateContent(prompt)
                    response.text ?: "Aucune réponse n'a été produite par le modèle de secours."
                }
            } catch (fallbackError: CancellationException) {
                throw fallbackError
            } catch (fallbackError: Exception) {
                Log.e(TAG, "Échec également avec le modèle de secours", fallbackError)
                throw fallbackError
            }
        }
    }

    /**
     * Envoie une requête textuelle et encapsule le résultat dans un [Result].
     *
     * @param prompt Texte de la requête.
     * @return [Result] contenant le texte généré ou l'exception rencontrée.
     */
    suspend fun sendTextMessage(prompt: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val response = sendMessage(prompt)
            Result.success(response)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Envoie une requête textuelle avec diffusion en continu (Streaming) sous la forme d'un [Flow].
     * Émet les morceaux textuels accumulés pour réduire la latence ressentie.
     *
     * @param prompt Texte de la requête utilisateur.
     * @return [Flow] émettant la chaîne de caractères cumulative au fil de la génération.
     */
    fun sendMessageStream(prompt: String): Flow<String> = flow {
        require(prompt.isNotBlank()) { "Le prompt ne peut pas être vide." }

        val accumulatedText = StringBuilder()
        var streamSucceeded = false

        try {
            withTimeout(60_000L) {
                generativeModel.generateContentStream(prompt).collect { chunk ->
                    val chunkText = chunk.text.orEmpty()
                    if (chunkText.isNotEmpty()) {
                        accumulatedText.append(chunkText)
                        emit(accumulatedText.toString())
                        streamSucceeded = true
                    }
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            if (!streamSucceeded) {
                Log.w(TAG, "Échec du stream avec $modelName, bascule sur $fallbackModelName", e)
                withTimeout(60_000L) {
                    fallbackModel.generateContentStream(prompt).collect { chunk ->
                        val chunkText = chunk.text.orEmpty()
                        if (chunkText.isNotEmpty()) {
                            accumulatedText.append(chunkText)
                            emit(accumulatedText.toString())
                        }
                    }
                }
            } else {
                throw e
            }
        }
    }.flowOn(Dispatchers.IO)
}
