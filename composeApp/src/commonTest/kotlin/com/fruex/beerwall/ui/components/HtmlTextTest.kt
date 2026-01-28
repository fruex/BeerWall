package com.fruex.beerwall.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class HtmlTextTest {

    @Test
    fun testBuildHtmlAnnotatedString_parsesLinkCorrectly() {
        val html = "Click <a href=\"https://example.com\">here</a> for info."
        val annotatedString = buildHtmlAnnotatedString(html, Color.Blue)

        assertEquals("Click here for info.", annotatedString.text)

        val links = annotatedString.getLinkAnnotations(0, annotatedString.length)
        assertEquals(1, links.size)
        val link = links.first().item
        assertIs<LinkAnnotation.Url>(link)
        assertEquals("https://example.com", link.url)
    }

    @Test
    fun testBuildHtmlAnnotatedString_parsesMultipleLinks() {
        val html = "Link1 <a href=\"url1\">one</a>, Link2 <a href=\"url2\">two</a>."
        val annotatedString = buildHtmlAnnotatedString(html, Color.Blue)

        assertEquals("Link1 one, Link2 two.", annotatedString.text)

        val links = annotatedString.getLinkAnnotations(0, annotatedString.length)
        assertEquals(2, links.size)

        val link1 = links[0].item as LinkAnnotation.Url
        assertEquals("url1", link1.url)

        val link2 = links[1].item as LinkAnnotation.Url
        assertEquals("url2", link2.url)
    }

    @Test
    fun testBuildHtmlAnnotatedString_cleansHtmlTags() {
        val html = "<b>Bold</b> <br> NewLine"
        val annotatedString = buildHtmlAnnotatedString(html, Color.Blue)

        assertEquals("Bold \n NewLine", annotatedString.text)
    }
}
