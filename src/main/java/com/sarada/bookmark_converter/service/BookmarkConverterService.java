package com.sarada.bookmark_converter.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookmarkConverterService {

  private final JsonToHtmlConverter jsonToHtmlConverter;
  private final HtmlToJsonConverter htmlToJsonConverter;

  public void convertJsonToHtml(String inputJsonPath, String outputHtmlPath) {
    try {
      log.info("Starting conversion from JSON to HTML");
      log.info("Input: {}", inputJsonPath);
      log.info("Output: {}", outputHtmlPath);

      File inputFile = new File(inputJsonPath);
      if (!inputFile.exists()) {
        log.error("Input file does not exist: {}", inputJsonPath);
        throw new IllegalArgumentException("Input file does not exist: " + inputJsonPath);
      }

      String html = jsonToHtmlConverter.convertJsonToHtml(inputFile);

      try (FileWriter writer = new FileWriter(outputHtmlPath)) {
        writer.write(html);
      }

      log.info("Successfully converted JSON to HTML: {}", outputHtmlPath);
    } catch (IOException e) {
      log.error("Error converting JSON to HTML", e);
      throw new RuntimeException("Error converting JSON to HTML", e);
    }
  }

  public void convertHtmlToJson(String inputHtmlPath, String outputJsonPath) {
    try {
      log.info("Starting conversion from HTML to JSON");
      log.info("Input: {}", inputHtmlPath);
      log.info("Output: {}", outputJsonPath);

      File inputFile = new File(inputHtmlPath);
      if (!inputFile.exists()) {
        log.error("Input file does not exist: {}", inputHtmlPath);
        throw new IllegalArgumentException("Input file does not exist: " + inputHtmlPath);
      }

      String json = htmlToJsonConverter.convertHtmlToJson(inputFile);

      try (FileWriter writer = new FileWriter(outputJsonPath)) {
        writer.write(json);
      }

      log.info("Successfully converted HTML to JSON: {}", outputJsonPath);
    } catch (IOException e) {
      log.error("Error converting HTML to JSON", e);
      throw new RuntimeException("Error converting HTML to JSON", e);
    }
  }
}
