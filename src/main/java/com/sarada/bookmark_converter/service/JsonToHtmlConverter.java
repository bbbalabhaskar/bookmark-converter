package com.sarada.bookmark_converter.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sarada.bookmark_converter.model.Bookmark;
import com.sarada.bookmark_converter.model.BookmarkFolder;
import java.io.File;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JsonToHtmlConverter {

  private final ObjectMapper objectMapper = new ObjectMapper();

  public String convertJsonToHtml(File jsonFile) throws IOException {
    log.info("Converting JSON file to HTML: {}", jsonFile.getAbsolutePath());
    JsonNode root = objectMapper.readTree(jsonFile);
    BookmarkFolder rootFolder = parseJsonNode(root);

    StringBuilder html = new StringBuilder();
    html.append("<!DOCTYPE NETSCAPE-Bookmark-file-1>\n");
    html.append("<!-- This is an automatically generated file.\n");
    html.append("     It will be read and overwritten.\n");
    html.append("     DO NOT EDIT! -->\n");
    html.append("<META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=UTF-8\">\n");
    html.append("<TITLE>Bookmarks</TITLE>\n");
    html.append("<H1>Bookmarks Menu</H1>\n\n");
    html.append("<DL><p>\n");

    buildHtmlFromFolder(rootFolder, html, 1);

    html.append("</DL><p>\n");

    return html.toString();
  }

  private BookmarkFolder parseJsonNode(JsonNode node) {
    BookmarkFolder folder = new BookmarkFolder();

    if (node.has("title")) {
      folder.setTitle(node.get("title").asText());
    }
    if (node.has("dateAdded")) {
      folder.setDateAdded(node.get("dateAdded").asLong());
    }
    if (node.has("lastModified")) {
      folder.setLastModified(node.get("lastModified").asLong());
    }

    if (node.has("children")) {
      JsonNode children = node.get("children");
      for (JsonNode child : children) {
        String type = child.has("type") ? child.get("type").asText() : "";

        if ("text/x-moz-place-container".equals(type)) {
          folder.getFolders().add(parseJsonNode(child));
        } else if ("text/x-moz-place".equals(type)) {
          Bookmark bookmark =
              Bookmark.builder()
                  .title(child.has("title") ? child.get("title").asText() : "")
                  .url(child.has("uri") ? child.get("uri").asText() : "")
                  .dateAdded(child.has("dateAdded") ? child.get("dateAdded").asLong() : null)
                  .lastModified(
                      child.has("lastModified") ? child.get("lastModified").asLong() : null)
                  .icon(child.has("icon") ? child.get("icon").asText() : null)
                  .iconUri(child.has("iconUri") ? child.get("iconUri").asText() : null)
                  .build();
          folder.getBookmarks().add(bookmark);
        }
      }
    }

    return folder;
  }

  private void buildHtmlFromFolder(BookmarkFolder folder, StringBuilder html, int level) {
    String indent = "    ".repeat(level);

    // Add bookmarks
    for (Bookmark bookmark : folder.getBookmarks()) {
      html.append(indent)
          .append("<DT><A HREF=\"")
          .append(escapeHtml(bookmark.getUrl()))
          .append("\"");
      if (bookmark.getDateAdded() != null) {
        html.append(" ADD_DATE=\"").append(bookmark.getDateAdded() / 1000000).append("\"");
      }
      if (bookmark.getLastModified() != null) {
        html.append(" LAST_MODIFIED=\"").append(bookmark.getLastModified() / 1000000).append("\"");
      }
      if (bookmark.getIcon() != null) {
        html.append(" ICON=\"").append(escapeHtml(bookmark.getIcon())).append("\"");
      }
      if (bookmark.getIconUri() != null) {
        html.append(" ICON_URI=\"").append(escapeHtml(bookmark.getIconUri())).append("\"");
      }
      html.append(">").append(escapeHtml(bookmark.getTitle())).append("</A>\n");
    }

    // Add folders
    for (BookmarkFolder subFolder : folder.getFolders()) {
      html.append(indent).append("<DT><H3");
      if (subFolder.getDateAdded() != null) {
        html.append(" ADD_DATE=\"").append(subFolder.getDateAdded() / 1000000).append("\"");
      }
      if (subFolder.getLastModified() != null) {
        html.append(" LAST_MODIFIED=\"").append(subFolder.getLastModified() / 1000000).append("\"");
      }
      html.append(">").append(escapeHtml(subFolder.getTitle())).append("</H3>\n");
      html.append(indent).append("<DL><p>\n");
      buildHtmlFromFolder(subFolder, html, level + 1);
      html.append(indent).append("</DL><p>\n");
    }
  }

  private String escapeHtml(String text) {
    if (text == null) return "";
    return text.replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#39;");
  }
}
