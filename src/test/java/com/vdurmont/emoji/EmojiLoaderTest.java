package com.vdurmont.emoji;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static com.fasterxml.jackson.databind.node.JsonNodeFactory.instance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class EmojiLoaderTest {
  @Test
  public void load_empty_database_returns_empty_list() throws IOException {
    // GIVEN
    byte[] bytes = instance.objectNode().toString().getBytes("UTF-8");
    InputStream stream = new ByteArrayInputStream(bytes);

    // WHEN
    List<Emoji> emojis = EmojiLoader.loadEmojis(stream);

    // THEN
    assertEquals(0, emojis.size());
  }

  @Test
  public void buildEmojiFromJSON() throws IOException {
    // GIVEN
    JsonNode json = new ObjectMapper().readTree("{"
      + "\"emoji\": \"😄\","
      + "\"description\": \"smiling face with open mouth and smiling eyes\","
      + "\"aliases\": [\"smile\"],"
      + "\"tags\": [\"happy\", \"joy\", \"pleased\"]"
      + "}");

    // WHEN
    Emoji emoji = EmojiLoader.buildEmojiFromJSON(json);

    // THEN
    assertNotNull(emoji);
    assertEquals("😄", emoji.getUnicode());
    assertEquals(
      "smiling face with open mouth and smiling eyes",
      emoji.getDescription()
    );
    assertEquals(1, emoji.getAliases().size());
    assertEquals("smile", emoji.getAliases().get(0));
    assertEquals(3, emoji.getTags().size());
    assertEquals("happy", emoji.getTags().get(0));
    assertEquals("joy", emoji.getTags().get(1));
    assertEquals("pleased", emoji.getTags().get(2));
  }

  @Test
  public void buildEmojiFromJSON_without_description_sets_a_null_description()
          throws IOException {
    // GIVEN
    JsonNode json = new ObjectMapper().readTree("{"
      + "\"emoji\": \"😄\","
      + "\"aliases\": [\"smile\"],"
      + "\"tags\": [\"happy\", \"joy\", \"pleased\"]"
      + "}");

    // WHEN
    Emoji emoji = EmojiLoader.buildEmojiFromJSON(json);

    // THEN
    assertNotNull(emoji);
    assertNull(emoji.getDescription());
  }

  @Test
  public void buildEmojiFromJSON_without_unicode_returns_null()
          throws IOException {
    // GIVEN
    JsonNode json = new ObjectMapper().readTree("{"
      + "\"aliases\": [\"smile\"],"
      + "\"tags\": [\"happy\", \"joy\", \"pleased\"]"
      + "}");

    // WHEN
    Emoji emoji = EmojiLoader.buildEmojiFromJSON(json);

    // THEN
    assertNull(emoji);
  }

  @Test
  public void buildEmojiFromJSON_computes_the_html_codes()
          throws IOException {
    // GIVEN
    JsonNode json = new ObjectMapper().readTree("{"
      + "\"emoji\": \"😄\","
      + "\"description\": \"smiling face with open mouth and smiling eyes\","
      + "\"aliases\": [\"smile\"],"
      + "\"tags\": [\"happy\", \"joy\", \"pleased\"]"
      + "}");

    // WHEN
    Emoji emoji = EmojiLoader.buildEmojiFromJSON(json);

    // THEN
    assertNotNull(emoji);
    assertEquals("😄", emoji.getUnicode());
    assertEquals("&#128516;", emoji.getHtmlDecimal());
    assertEquals("&#x1f604;", emoji.getHtmlHexadecimal());
  }

  @Test
  public void buildEmojiFromJSON_with_support_for_fitzpatrick_true()
          throws IOException {
    // GIVEN
    JsonNode json = new ObjectMapper().readTree("{"
      + "\"emoji\": \"\uD83D\uDC66\","
      + "\"description\": \"boy\","
      + "\"supports_fitzpatrick\": true,"
      + "\"aliases\": [\"boy\"],"
      + "\"tags\": [\"child\"]"
      + "}");

    // WHEN
    Emoji emoji = EmojiLoader.buildEmojiFromJSON(json);

    // THEN
    assertNotNull(emoji);
    assertTrue(emoji.supportsFitzpatrick());
  }

  @Test
  public void buildEmojiFromJSON_with_support_for_fitzpatrick_false()
          throws IOException {
    // GIVEN
    JsonNode json = new ObjectMapper().readTree("{"
      + "\"emoji\": \"\uD83D\uDE15\","
      + "\"description\": \"confused face\","
      + "\"supports_fitzpatrick\": false,"
      + "\"aliases\": [\"confused\"],"
      + "\"tags\": []"
      + "}");

    // WHEN
    Emoji emoji = EmojiLoader.buildEmojiFromJSON(json);

    // THEN
    assertNotNull(emoji);
    assertFalse(emoji.supportsFitzpatrick());
  }

  @Test
  public void buildEmojiFromJSON_without_support_for_fitzpatrick()
          throws IOException {
    // GIVEN
    JsonNode json = new ObjectMapper().readTree("{"
      + "\"emoji\": \"\uD83D\uDE15\","
      + "\"description\": \"confused face\","
      + "\"aliases\": [\"confused\"],"
      + "\"tags\": []"
      + "}");

    // WHEN
    Emoji emoji = EmojiLoader.buildEmojiFromJSON(json);

    // THEN
    assertNotNull(emoji);
    assertFalse(emoji.supportsFitzpatrick());
  }
}
