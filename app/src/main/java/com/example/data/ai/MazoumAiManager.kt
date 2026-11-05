package com.example.data.ai

import android.graphics.Bitmap
import android.util.Log
import com.example.repository.GeminiRepository
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Content
import com.google.firebase.ai.type.ImagePart
import com.google.firebase.ai.type.TextPart
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

private const val TAG = "MazoumAiManager"

class MazoumAiManager(
    val geminiRepository: GeminiRepository = GeminiRepository()
) {

    // Lazy initialization of models for specialized tasks
    private val generalModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            systemInstruction = Content(
                role = "system",
                parts = listOf(
                    TextPart(
                        """
                        Tu es MAZOUM TECH, un assistant personnel intelligent de nouvelle génération, ultra-compétent, futuriste, élégant et bienveillant.
                        Slogan : « Votre intelligence numérique, partout avec vous. »
                        Langue principale : Français impeccable, clair, professionnel et percutant.
                        Missions :
                        - Aider l'utilisateur dans son quotidien (organisation, créativité, productivité, prise de décision).
                        - Répondre avec clarté, pertinence et synthèse.
                        - Utiliser les connaissances mémorisées de l'utilisateur quand elles sont fournies.
                        - Être force de proposition et toujours orienté action.
                        """.trimIndent()
                    )
                )
            )
        )
    }

    private val visionModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            systemInstruction = Content(
                role = "system",
                parts = listOf(
                    TextPart(
                        """
                        Tu es le module Vision Intelligente de MAZOUM TECH.
                        Tu analyses les photographies, documents, captures d'écran, schémas et messages d'erreurs visuels avec une précision remarquable.
                        Fournis des explications claires, identifie le texte présent (OCR intelligent), traduis si nécessaire et donne des recommandations concrètes en français.
                        """.trimIndent()
                    )
                )
            )
        )
    }

    private val studioModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            systemInstruction = Content(
                role = "system",
                parts = listOf(
                    TextPart(
                        """
                        Tu es le Studio de Création IA de MAZOUM TECH.
                        Tu es un expert créatif en rédaction de textes professionnels, conception de scripts vidéo percutants (TikTok, Reels, YouTube), ingénierie de prompts et stratégie de contenu pour les réseaux sociaux.
                        Rédige des contenus de très haute valeur, engageants et prêts à l'emploi en français.
                        """.trimIndent()
                    )
                )
            )
        )
    }

    // Streaming chat with memory context via GeminiRepository
    fun streamChat(
        message: String,
        userName: String,
        userRole: String,
        memoryFacts: List<String>
    ): Flow<String> {
        val memoryBlock = if (memoryFacts.isNotEmpty()) {
            "\n[MÉMOIRE PERSONNELLE CONSERVÉE]:\n" + memoryFacts.joinToString("\n") { "- $it" }
        } else ""

        val prompt = """
            [CONTEXTE UTILISATEUR: Nom=$userName, Rôle=$userRole]$memoryBlock
            
            Message de l'utilisateur: $message
        """.trimIndent()

        return geminiRepository.sendMessageStream(prompt)
    }

    // Direct chat (non-streaming fallback or speech synthesis) via GeminiRepository
    suspend fun generateChatResponse(
        message: String,
        userName: String,
        userRole: String,
        memoryFacts: List<String>
    ): String = withContext(Dispatchers.IO) {
        val memoryBlock = if (memoryFacts.isNotEmpty()) {
            "\n[MÉMOIRE PERSONNELLE CONSERVÉE]:\n" + memoryFacts.joinToString("\n") { "- $it" }
        } else ""

        val prompt = """
            [CONTEXTE UTILISATEUR: Nom=$userName, Rôle=$userRole]$memoryBlock
            
            Message de l'utilisateur: $message
        """.trimIndent()

        try {
            geminiRepository.sendMessage(prompt)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Chat generation error", e)
            "Erreur d'analyse IA : ${e.localizedMessage ?: "Vérifiez votre connexion."}"
        }
    }

    // Vision Analysis
    suspend fun analyzeImage(
        bitmap: Bitmap,
        analysisType: String,
        userQuery: String
    ): String = withContext(Dispatchers.IO) {
        try {
            val taskInstruction = when (analysisType) {
                "OCR" -> "Transcris tout le texte visible sur cette image, puis résume son message principal."
                "ERROR" -> "Analyse ce message d'erreur ou capture d'écran technique. Explique précisément la cause du problème et donne les étapes concrètes pour le corriger."
                "DOCUMENT" -> "Résume ce document, extrais les points clés et indique s'il y a des actions requises."
                "TRANSLATE" -> "Identifie la langue du texte sur l'image et traduis-le intégralement en français soigné."
                else -> "Analyse cette image de manière détaillée : identifie les objets, le contexte, les informations clés et les détails notables."
            }

            val finalPrompt = if (userQuery.isNotBlank()) {
                "$taskInstruction\nDemande spécifique de l'utilisateur : $userQuery"
            } else taskInstruction

            withTimeout(60_000L) {
                val contentPayload = Content(
                    role = "user",
                    parts = listOf(
                        ImagePart(bitmap),
                        TextPart(finalPrompt)
                    )
                )
                val response = visionModel.generateContent(contentPayload)
                response.text ?: "Aucune analyse n'a pu être produite."
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Vision analysis error", e)
            "Erreur d'analyse d'image : ${e.localizedMessage ?: "Impossible d'analyser l'image."}"
        }
    }

    // Creative Studio Content Generation
    suspend fun generateCreativeStudio(
        toolType: String,
        topic: String,
        targetPlatform: String,
        tone: String,
        customDetails: String
    ): String = withContext(Dispatchers.IO) {
        try {
            val prompt = when (toolType) {
                "SCRIPT" -> """
                    Génère un script vidéo dynamique pour $targetPlatform sur le sujet : "$topic".
                    Ton : $tone.
                    Détails : $customDetails.
                    Structure requise :
                    1. Accroche visuelle et sonore (les 3 premières secondes).
                    2. Développement scène par scène avec indications visuelles [Visuel] et voix-off [Voix].
                    3. Appel à l'action percutant (Call to Action).
                    4. Conseils d'éclairage et d'ambiance.
                """.trimIndent()

                "PROMPT" -> """
                    Tu es un ingénieur de prompt de classe mondiale.
                    Optimise le prompt suivant pour obtenir des résultats exceptionnels avec une IA moderne :
                    Sujet ou intention initiale : "$topic"
                    Objectif : $customDetails
                    Génère :
                    1. Le Prompt Maître enrichi (détaillé, structuré, prêt à copier).
                    2. Variables suggérées à personnaliser.
                    3. Explication des améliorations apportées.
                """.trimIndent()

                "SOCIAL" -> """
                    Rédige 3 variations de publications captivantes pour $targetPlatform sur le thème : "$topic".
                    Ton : $tone.
                    Détails : $customDetails.
                    Pour chaque variation :
                    - Titre / Accroche coup de poing
                    - Corps du texte avec sauts de lignes aérés et émojis bien placés
                    - Call To Action (incitation au commentaire/partage)
                    - 8 à 12 hashtags stratégiques
                """.trimIndent()

                "VOICEOVER" -> """
                    Rédige un texte de voix-off professionnel d'environ 60 secondes sur le thème : "$topic".
                    Ton : $tone.
                    Indique les pauses [Pause 1s], les intonations d'insistance et le rythme de parole.
                """.trimIndent()

                else -> """
                    Rédige un texte complet, élégant et persuasif sur le sujet : "$topic".
                    Ton : $tone. Plateforme/Format : $targetPlatform.
                    Détails complémentaires : $customDetails.
                """.trimIndent()
            }

            withTimeout(60_000L) {
                val response = studioModel.generateContent(prompt)
                response.text ?: "Le studio n'a pas pu générer le contenu."
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Creative studio error", e)
            "Erreur du Studio de Création : ${e.localizedMessage ?: "Échec de génération."}"
        }
    }
}
