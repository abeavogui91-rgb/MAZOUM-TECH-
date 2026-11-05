package com.example

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun navigationItems_areNotNull() {
    val items = com.example.ui.navigation.NavigationItem.bottomNavItems
    assertEquals(5, items.size)
    items.forEach { item ->
      assertNotNull("Navigation item should not be null", item)
      assertNotNull("Route should not be null", item.route)
      assertNotNull("Title should not be null", item.title)
    }
  }

  @Test
  fun geminiRepository_initializesWithGemini15FlashModel() {
    val repository = com.example.repository.GeminiRepository()
    assertEquals("gemini-1.5-flash", repository.modelName)
  }

  @Test
  fun assistantViewModel_initializesWithGreetingAndState() {
    val viewModel = com.example.ui.viewmodel.AssistantViewModel()
    val initialMessages = viewModel.messages.value
    assertTrue("Should have at least 1 greeting message", initialMessages.isNotEmpty())
    assertEquals(com.example.ui.model.MessageSender.AI, initialMessages.first().sender)
    assertFalse(viewModel.isLoading.value)
    assertEquals("", viewModel.inputText.value)

    viewModel.onInputTextChanged("Bonjour MAZOUM")
    assertEquals("Bonjour MAZOUM", viewModel.inputText.value)
  }
}
