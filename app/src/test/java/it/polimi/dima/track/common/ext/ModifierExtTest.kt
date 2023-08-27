package it.polimi.dima.track.common.ext

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Test

class ModifierExtTest {

    @Test
    fun textButton() {
        val expected = Modifier.fillMaxWidth()
            .padding(16.dp, 8.dp, 16.dp, 0.dp)
        assertEquals(expected, Modifier.textButton())
    }

    @Test
    fun basicButton() {
        val expected = Modifier.fillMaxWidth()
            .padding(16.dp, 8.dp)
        assertEquals(expected, Modifier.basicButton())
    }

    @Test
    fun card() {
        val expected = Modifier.padding(16.dp, 0.dp, 16.dp, 8.dp)
        assertEquals(expected, Modifier.card())
    }

    @Test
    fun contextMenu() {
        val expected = Modifier.wrapContentWidth()
        assertEquals(expected, Modifier.contextMenu())
    }

    @Test
    fun dropdownSelector() {
        val expected = Modifier.fillMaxWidth()
        assertEquals(expected, Modifier.dropdownSelector())
    }

    @Test
    fun fieldModifier() {
        val expected = Modifier.fillMaxWidth()
            .padding(16.dp, 4.dp)
        assertEquals(expected, Modifier.fieldModifier())
    }

    @Test
    fun toolbarActions() {
        val expected = Modifier.wrapContentSize(Alignment.TopEnd)
        assertEquals(expected, Modifier.toolbarActions())
    }

    @Test
    fun bigSpacer() {
        val expected = Modifier.fillMaxWidth()
            .height(24.dp)
        assertEquals(expected, Modifier.bigSpacer())
    }

    @Test
    fun spacer() {
        val expected = Modifier.fillMaxWidth()
            .height(16.dp)
        assertEquals(expected, Modifier.spacer())
    }

    @Test
    fun smallSpacer() {
        val expected = Modifier.fillMaxWidth()
            .height(8.dp)
        assertEquals(expected, Modifier.smallSpacer())
    }
}
