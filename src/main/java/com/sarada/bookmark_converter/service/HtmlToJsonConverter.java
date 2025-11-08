package com.sarada.bookmark_converter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sarada.bookmark_converter.model.Bookmark;
import com.sarada.bookmark_converter.model.BookmarkFolder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class HtmlToJsonConverter {

  private final ObjectMapper objectMapper = new ObjectMapper();

  private static final Pattern BOOKMARK_PATTERN =
      Pattern.compile("<A\\s+([^>]+)>([^<]*)</A>", Pattern.CASE_INSENSITIVE);

  private static final Pattern FOLDER_PATTERN =
      Pattern.compile("<H3([^>]*)>([^<]*)</H3>", Pattern.CASE_INSENSITIVE);

  private static final Pattern ATTR_PATTERN =
      Pattern.compile("(\\w+)=\"([^\"]*)\"", Pattern.CASE_INSENSITIVE);

  public String convertHtmlToJson(File htmlFile) throws IOException {
    log.info("Converting HTML file to JSON: {}", htmlFile.getAbsolutePath());
    BookmarkFolder rootFolder = parseHtmlFile(htmlFile);

    ObjectNode rootNode = buildJsonFromFolder(rootFolder);

    return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
  }

  private BookmarkFolder parseHtmlFile(File htmlFile) throws IOException {
    BookmarkFolder root = BookmarkFolder.builder().title("Bookmarks Menu").build();

    Stack<BookmarkFolder> folderStack = new Stack<>();
    folderStack.push(root);

    try (BufferedReader reader = new BufferedReader(new FileReader(htmlFile))) {
      String line;
      while ((line = reader.readLine()) != null) {
        line = line.trim();

        // Check for folder start
        Matcher folderMatcher = FOLDER_PATTERN.matcher(line);
        if (folderMatcher.find()) {
          String attributes = folderMatcher.group(1);
          String title = unescapeHtml(folderMatcher.group(2));

          BookmarkFolder folder = BookmarkFolder.builder().title(title).build();

          // Parse attributes
          Matcher attrMatcher = ATTR_PATTERN.matcher(attributes);
          while (attrMatcher.find()) {
            String attrName = attrMatcher.group(1).toUpperCase();
            String attrValue = attrMatcher.group(2);

            if ("ADD_DATE".equals(attrName)) {
              folder.setDateAdded(Long.parseLong(attrValue) * 1000000L);
            } else if ("LAST_MODIFIED".equals(attrName)) {
              folder.setLastModified(Long.parseLong(attrValue) * 1000000L);
            }
          }

          folderStack.peek().getFolders().add(folder);
          folderStack.push(folder);
          continue;
        }

        // Check for bookmark
        Matcher bookmarkMatcher = BOOKMARK_PATTERN.matcher(line);
        if (bookmarkMatcher.find()) {
          String attributes = bookmarkMatcher.group(1);
          String title = unescapeHtml(bookmarkMatcher.group(2));

          Bookmark bookmark = Bookmark.builder().title(title).build();

          // Parse attributes
          Matcher attrMatcher = ATTR_PATTERN.matcher(attributes);
          while (attrMatcher.find()) {
            String attrName = attrMatcher.group(1).toUpperCase();
            String attrValue = attrMatcher.group(2);

            if ("HREF".equals(attrName)) {
              bookmark.setUrl(unescapeHtml(attrValue));
            } else if ("ADD_DATE".equals(attrName)) {
              bookmark.setDateAdded(Long.parseLong(attrValue) * 1000000L);
            } else if ("LAST_MODIFIED".equals(attrName)) {
              bookmark.setLastModified(Long.parseLong(attrValue) * 1000000L);
            } else if ("ICON".equals(attrName)) {
              bookmark.setIcon(attrValue);
            } else if ("ICON_URI".equals(attrName)) {
              bookmark.setIconUri(attrValue);
            }
          }

          folderStack.peek().getBookmarks().add(bookmark);
          continue;
        }

        // Check for folder end
        if (line.contains("</DL>") && folderStack.size() > 1) {
          folderStack.pop();
        }
      }
    }

    return root;
  }

  private ObjectNode buildJsonFromFolder(BookmarkFolder folder) {
    ObjectNode node = objectMapper.createObjectNode();

    node.put("title", folder.getTitle());
    node.put("type", "text/x-moz-place-container");

    if (folder.getDateAdded() != null) {
      node.put("dateAdded", folder.getDateAdded());
    }
    if (folder.getLastModified() != null) {
      node.put("lastModified", folder.getLastModified());
    }

    ArrayNode childrenArray = objectMapper.createArrayNode();

    // Add bookmarks
    for (Bookmark bookmark : folder.getBookmarks()) {
      ObjectNode bookmarkNode = objectMapper.createObjectNode();
      bookmarkNode.put("title", bookmark.getTitle());
      bookmarkNode.put("uri", bookmark.getUrl());
      bookmarkNode.put("type", "text/x-moz-place");

      if (bookmark.getDateAdded() != null) {
        bookmarkNode.put("dateAdded", bookmark.getDateAdded());
      }
      if (bookmark.getLastModified() != null) {
        bookmarkNode.put("lastModified", bookmark.getLastModified());
      }
      if (bookmark.getIcon() != null) {
        bookmarkNode.put("icon", bookmark.getIcon());
      }
      if (bookmark.getIconUri() != null) {
        bookmarkNode.put("iconUri", bookmark.getIconUri());
      }

      childrenArray.add(bookmarkNode);
    }

    // Add folders
    for (BookmarkFolder subFolder : folder.getFolders()) {
      childrenArray.add(buildJsonFromFolder(subFolder));
    }

    if (childrenArray.size() > 0) {
      node.set("children", childrenArray);
    }

    return node;
  }

  private String unescapeHtml(String text) {
    if (text == null) return "";
    return text.replace("&amp;", "&")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&quot;", "\"")
        .replace("&#39;", "'");
  }
}
