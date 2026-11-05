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
}
