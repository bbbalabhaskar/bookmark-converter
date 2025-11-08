package com.sarada.bookmark_converter;

import com.sarada.bookmark_converter.service.BookmarkConverterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class BookmarkConverterApplication implements CommandLineRunner {

  private final BookmarkConverterService bookmarkConverterService;

  public static void main(String[] args) {
    SpringApplication.run(BookmarkConverterApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    log.info("Bookmark Converter Application Started");
    log.info("========================================");

    if (args.length < 3) {
      printUsage();
      return;
    }

    String command = args[0].toLowerCase();
    String inputFile = args[1];
    String outputFile = args[2];

    try {
      switch (command) {
        case "json-to-html":
          log.info("Converting JSON to HTML...");
          bookmarkConverterService.convertJsonToHtml(inputFile, outputFile);
          log.info("Conversion completed successfully!");
          break;

        case "html-to-json":
          log.info("Converting HTML to JSON...");
          bookmarkConverterService.convertHtmlToJson(inputFile, outputFile);
          log.info("Conversion completed successfully!");
          break;

        default:
          log.error("Unknown command: {}", command);
          printUsage();
      }
    } catch (Exception e) {
      log.error("Error during conversion", e);
      System.exit(1);
    }
  }

  private void printUsage() {
    log.info("Usage:");
    log.info("  json-to-html <input.json> <output.html>");
    log.info("    Convert Firefox bookmarks from JSON format to HTML format");
    log.info("");
    log.info("  html-to-json <input.html> <output.json>");
    log.info("    Convert Firefox bookmarks from HTML format to JSON format");
    log.info("");
    log.info("Examples:");
    log.info(
        "  mvn spring-boot:run -Dspring-boot.run.arguments=\"json-to-html bookmarks.json"
            + " bookmarks.html\"");
    log.info(
        "  mvn spring-boot:run -Dspring-boot.run.arguments=\"html-to-json bookmarks.html"
            + " bookmarks.json\"");
  }
}
