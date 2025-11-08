package com.sarada.bookmark_converter.model;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkFolder {
  private String title;
  private Long dateAdded;
  private Long lastModified;

  @Builder.Default private List<BookmarkFolder> folders = new ArrayList<>();

  @Builder.Default private List<Bookmark> bookmarks = new ArrayList<>();
}
