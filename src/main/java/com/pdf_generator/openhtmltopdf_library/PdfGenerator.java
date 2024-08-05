package com.pdf_generator.openhtmltopdf_library;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import java.io.FileOutputStream;
import java.io.OutputStream;
import lombok.extern.slf4j.Slf4j;
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
}
