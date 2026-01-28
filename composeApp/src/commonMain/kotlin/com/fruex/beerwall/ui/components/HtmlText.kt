package com.fruex.beerwall.ui.components

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit

@Composable
fun HtmlText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodySmall,
    color: Color = Color.Unspecified,
    linkColor: Color = MaterialTheme.colorScheme.primary,
    fontSize: TextUnit = TextUnit.Unspecified
) {
    val annotatedString = buildHtmlAnnotatedString(text, linkColor)

    val textColor = if (color != Color.Unspecified) {
        color
    } else if (style.color != Color.Unspecified) {
        style.color
    } else {
        LocalContentColor.current
    }

    Text(
        text = annotatedString,
        modifier = modifier,
        style = style.copy(color = textColor, fontSize = fontSize)
    )
}

fun buildHtmlAnnotatedString(text: String, linkColor: Color): AnnotatedString {
    return buildAnnotatedString {
        var currentIndex = 0
        val linkRegex = "<a\\s+(?:[^>]*?\\s+)?href=\"([^\"]*)\"[^>]*>(.*?)</a>".toRegex(RegexOption.IGNORE_CASE)

        val matches = linkRegex.findAll(text)

        for (match in matches) {
            val preMatch = text.substring(currentIndex, match.range.first)
            append(cleanHtml(preMatch))

            val url = match.groupValues[1]
            val linkText = cleanHtml(match.groupValues[2])

            val linkAnnotation = LinkAnnotation.Url(
                url = url,
                styles = TextLinkStyles(
                    style = SpanStyle(
                        color = linkColor,
                        textDecoration = TextDecoration.Underline
                    )
                )
            )
            withLink(linkAnnotation) {
                append(linkText)
            }

            currentIndex = match.range.last + 1
        }

        if (currentIndex < text.length) {
            append(cleanHtml(text.substring(currentIndex)))
        }
    }
}

private fun cleanHtml(input: String): String {
    return input
        .replace("<br>", "\n")
        .replace("<br/>", "\n")
        .replace("<br />", "\n")
        .replace("<ul[^>]*>".toRegex(), "\n")
        .replace("</ul>", "")
        .replace("<li[^>]*>".toRegex(), "• ")
        .replace("</li>", "\n")
        .replace("<[^>]+>".toRegex(), "") // Remove all other tags
        .replace("&nbsp;", " ")
        .replace("&amp;", "&")
        .replace("&quot;", "\"")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
}
