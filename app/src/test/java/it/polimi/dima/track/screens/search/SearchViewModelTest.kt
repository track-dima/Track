package it.polimi.dima.track.screens.search

import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SearchViewModelTest {
  private lateinit var searchViewModel: SearchViewModel

  @Before
  fun setUp() {
    searchViewModel = SearchViewModel(
      mockk(),
      mockk(),
    )
  }

  @Test
  fun onSearchTextChanged() {
    val searchText = "New search text"
    searchViewModel.onSearchTextChanged(searchText)

    assertEquals(searchText, searchViewModel.searchText.value)
  }

  @Test
  fun onSearchTextChanged_EmptyString() {
    val searchText = ""
    searchViewModel.onSearchTextChanged(searchText)

    assertEquals(searchText, searchViewModel.searchText.value)
  }

  @Test
  fun onClearClick() {
    searchViewModel.onClearClick()
    assertEquals("", searchViewModel.searchText.value)
  }
}