package com.pdf_generator.openhtmltopdf_library;

import com.openhtmltopdf.extend.FSSupplier;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PDFUtil {

  public static void configurePdfRendererBuilder(PdfRendererBuilder builder, String appWorkspace) {
    String fontDirectoryPath = appWorkspace + "config/font/allFont";
    Map<FSSupplier<InputStream>, String> fonts = loadFonts(fontDirectoryPath);
    for (Map.Entry<FSSupplier<InputStream>, String> font : fonts.entrySet()) {
      builder.useFont(font.getKey(), font.getValue());
    }
  }

  private static Map<FSSupplier<InputStream>, String> loadFonts(String fontDirectoryPath) {
    Map<FSSupplier<InputStream>, String> fonts = new HashMap<>();
    Map<String, String> fontMap = fontConfiguration();
    try {
      File fontDirectory = new File(fontDirectoryPath);
      if (fontDirectory.isDirectory()) {
        for (File fontFile : fontDirectory.listFiles()) {
          String fontFamily = fontMap.get(fontFile.getName());
          if (fontFamily != null) {
            fonts.put(() -> {
              try {
                return new FileInputStream(fontFile);
              } catch (Exception e) {
                log.error("Failed to load font: " + fontFile.getName(), e);
                return InputStream.nullInputStream();
              }
            }, fontFamily);
          } else {
            log.warn("No font family mapping found for font file: " + fontFile.getName());
          }
        }
      }
    } catch (Exception e) {
      log.error("Failed to load fonts from directory: " + fontDirectoryPath, e);
    }
    return fonts;
  }

  public static Map<String, String> fontConfiguration() {
    Map<String, String> fontMap = new HashMap<>();
    fontMap.put("Poppins-Regular.ttf", "poppins");
    fontMap.put("Nirmala-UI.ttf", "Nirmala UI");
    fontMap.put("DevLys 010 Normal.ttf","DevLys 010");
    return fontMap;
  }
}
