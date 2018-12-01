package com.vdurmont.emoji;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads the emojis from a JSON database.
 *
 * @author Vincent DURMONT [vdurmont@gmail.com]
 */
public class EmojiLoader {
  /**
   * No need for a constructor, all the methods are static.
   */
  private EmojiLoader() {}

  /**
   * Loads a JSONArray of emojis from an InputStream, parses it and returns the
   * associated list of {@link com.vdurmont.emoji.Emoji}s
   *
   * @param stream the stream of the JSONArray
   *
   * @return the list of {@link com.vdurmont.emoji.Emoji}s
   * @throws IOException if an error occurs while reading the stream or parsing
   * the JSONArray
   */
  public static List<Emoji> loadEmojis(InputStream stream) throws IOException {
    JsonNode emojisJSON = new ObjectMapper().readTree(stream);
    List<Emoji> emojis = new ArrayList<Emoji>(emojisJSON.size());
    for (int i = 0; i < emojisJSON.size(); i++) {
      Emoji emoji = buildEmojiFromJSON(emojisJSON.get(i));
      if (emoji != null) {
        emojis.add(emoji);
      }
    }
    return emojis;
  }

  protected static Emoji buildEmojiFromJSON(
    JsonNode json
  ) throws UnsupportedEncodingException {
    if (!json.has("emoji")) {
      return null;
    }

    byte[] bytes = json.get("emoji").asText().getBytes("UTF-8");
    String description = null;
    if (json.has("description")) {
      description = json.get("description").asText();
    }
    boolean supportsFitzpatrick = false;
    if (json.has("supports_fitzpatrick")) {
      supportsFitzpatrick = json.get("supports_fitzpatrick").asBoolean();
    }
    List<String> aliases = jsonArrayToStringList(json.get("aliases"));
    List<String> tags = jsonArrayToStringList(json.get("tags"));
    return new Emoji(description, supportsFitzpatrick, aliases, tags, bytes);
  }

  private static List<String> jsonArrayToStringList(JsonNode array) {
    List<String> strings = new ArrayList<String>(array.size());
    for (int i = 0; i < array.size(); i++) {
      strings.add(array.get(i).asText());
    }
    return strings;
  }
}
