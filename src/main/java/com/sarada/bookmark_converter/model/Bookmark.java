package com.sarada.bookmark_converter.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bookmark {
  private String title;
  private String url;
  private Long dateAdded;
  private Long lastModified;
  private String icon;
  private String iconUri;
}
