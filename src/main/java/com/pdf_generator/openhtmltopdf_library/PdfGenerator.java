package com.pdf_generator.openhtmltopdf_library;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.text.PDFTextStripper;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;

@Slf4j
public class PdfGenerator {

  public static void generatePdf() {
    PdfRendererBuilder pdfRendererBuilder = new PdfRendererBuilder();
    PDFUtil.configurePdfRendererBuilder(pdfRendererBuilder, "font/folder/path");
    createPdf(pdfRendererBuilder, "html", "filePath")
  }

  public static String createPdf(PdfRendererBuilder builder, String template, String filePath) {
    try (OutputStream os = new FileOutputStream(filePath)) {
      builder.useFastMode();
      Document document = Jsoup.parse(template, "UTF-8");
      builder.withW3cDocument(new W3CDom().fromJsoup(document), "/");
      builder.usePageSupplier(new BackgroundImageHandler("letterheadPath"));
      builder.toStream(os);
      builder.run();
      return filePath;
    } catch (Exception e) {
      log.error("Error while creating a PDF", e);
      return "";
    }
  }

  private void handleBlankPagesInPdf(List<String> filesPath) {
    if (filesPath.isEmpty()) {
      return;
    }
    String directoryPath = new File(filesPath.get(0)).getParent();
    Map<String, String> fileMoves = new HashMap<>();

    for (String filePath : filesPath) {
      try {
        String withoutBlankPageFilePath = removeBlankPages(filePath, directoryPath);
        if (!filePath.equalsIgnoreCase(withoutBlankPageFilePath)) {
          fileMoves.put(withoutBlankPageFilePath, filePath);
        }
      } catch (Exception e) {
        log.error("Failed to process file: " + filePath, e);
      }
    }
    moveFiles(fileMoves);
  }

  private void moveFiles(Map<String, String> fileMoves) {
    for (Map.Entry<String, String> entry : fileMoves.entrySet()) {
      try {
        Files.move(Path.of(entry.getKey()), Path.of(entry.getValue()), StandardCopyOption.REPLACE_EXISTING);
      } catch (Exception e) {
        log.error("Failed to move file from " + entry.getKey() + " to " + entry.getValue(), e);
      }
    }
  }

  private String removeBlankPages(String filePath, String directoryPath) throws Exception {
    try (PDDocument existingDocument = PDDocument.load(new File(filePath));
        PDDocument newDocument = new PDDocument()) {

      PDPageTree pages = existingDocument.getPages();
      PDFTextStripper stripper = new PDFTextStripper();

      int pageNumber = 1;
      int blankPageCount = 0;

      for (PDPage page : pages) {
        stripper.setStartPage(pageNumber);
        stripper.setEndPage(pageNumber);
        String text = stripper.getText(existingDocument).trim();

        if (text.isEmpty()) {
          blankPageCount++;
          log.info("Found {} blank page and removed in file: {}", blankPageCount, filePath);
        } else {
          newDocument.addPage(page);
        }
        pageNumber++;
      }

      if (blankPageCount > 0) {
        String outputPath = directoryPath + "/" + RandomStringUtils.randomAlphanumeric(8) + ".pdf";
        newDocument.save(new File(outputPath));
        return outputPath;
      }
      return filePath;
    }
  }
}
