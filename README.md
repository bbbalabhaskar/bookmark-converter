# Bookmark Converter

A Spring Boot command-line application that converts Firefox bookmarks between JSON and HTML formats.

## Features

- **JSON to HTML**: Convert Firefox bookmarks from JSON format to HTML format
- **HTML to JSON**: Convert Firefox bookmarks from HTML format to JSON format
- Preserves bookmark metadata (dates, icons, folder structure)
- Command-line interface for easy automation

## Prerequisites

- Java 25
- Maven 3.x

## Building the Application

```bash
mvn clean install
```

## Usage

The application supports two conversion modes:

### 1. Convert JSON to HTML

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="json-to-html input.json output.html"
```

Or using the JAR:

```bash
java -jar target/bookmark-converter-0.0.1-SNAPSHOT.jar json-to-html input.json output.html
```

### 2. Convert HTML to JSON

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="html-to-json input.html output.json"
```

Or using the JAR:

```bash
java -jar target/bookmark-converter-0.0.1-SNAPSHOT.jar html-to-json input.html output.json
```

## Command Syntax

```
<command> <input-file> <output-file>
```

**Commands:**

- `json-to-html` - Convert from JSON to HTML
- `html-to-json` - Convert from HTML to JSON

**Parameters:**

- `input-file` - Path to the input file
- `output-file` - Path to the output file

## Examples

Convert Firefox JSON bookmarks to HTML:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="json-to-html ~/Downloads/bookmarks.json ~/Desktop/bookmarks.html"
```

Convert HTML bookmarks back to JSON:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="html-to-json ~/Desktop/bookmarks.html ~/Downloads/bookmarks-new.json"
```

## File Formats

### Firefox JSON Format

Firefox uses a JSON structure with the following fields:

- `title` - Bookmark or folder name
- `uri` - URL (for bookmarks)
- `type` - Either "text/x-moz-place" (bookmark) or "text/x-moz-place-container" (folder)
- `dateAdded` - Timestamp in microseconds
- `lastModified` - Timestamp in microseconds
- `children` - Array of child bookmarks/folders

### HTML Netscape Bookmark Format

Standard HTML bookmark format used by Firefox and other browsers:

- Uses `<DL>` lists for structure
- `<A>` tags for bookmarks with HREF attribute
- `<H3>` tags for folder names
- Supports ADD_DATE, LAST_MODIFIED, ICON, and ICON_URI attributes

## Architecture

The application consists of:

- **Model Layer**: `Bookmark` and `BookmarkFolder` domain models
- **Service Layer**:
  - `JsonToHtmlConverter` - Converts JSON to HTML
  - `HtmlToJsonConverter` - Converts HTML to JSON
  - `BookmarkConverterService` - Coordinates conversion operations
- **CLI Layer**: `BookmarkConverterApplication` with `CommandLineRunner`

## Technologies Used

- Spring Boot 3.5.7
- Jackson for JSON processing
- Lombok for reducing boilerplate code
- Java 25

## License

This project is open source and available for use.
