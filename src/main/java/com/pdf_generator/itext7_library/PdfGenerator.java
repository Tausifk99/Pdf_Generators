package com.pdf_generator.itext7_library;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.BlockElement;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.font.FontProvider;
import java.io.ByteArrayOutputStream;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PdfGenerator {

  public byte[] startPdfCreation(String template) {
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDocument = new PdfDocument(writer)) {

      pdfDocument.setDefaultPageSize(PageSize.A4);
      Document document = new Document(pdfDocument);
      document.setLeftMargin(26);
      document.setRightMargin(26);

      pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, new BackgroundImageHandler("letterhead"));
      FontProvider fontProvider = getFontProvider("fontName");
      ConverterProperties properties = getConverterProperties(fontProvider);

      List<IElement> elements = HtmlConverter.convertToElements(template, properties);
      for (IElement element : elements) {
        document.add((BlockElement) element);
      }
      document.close();
      return outputStream.toByteArray();
    } catch (Exception e) {
      log.error("Error while creating a PDF", e);
    }
    return new byte[0];
  }

  private ConverterProperties getConverterProperties(FontProvider fontProvider) {
    ConverterProperties properties = new ConverterProperties();
    properties.setFontProvider(fontProvider);
    return properties;
  }

  private FontProvider getFontProvider(String fontName) {

    String fontPath = "fontFolderPath/" + "font/";
    try {
      FontProvider fontProvider = new DefaultFontProvider(false, false, false);
      List<String> specialFonts = List.of("krutidev");
      if (specialFonts.contains(fontName)) {
        fontProvider.addDirectory(fontPath + "poppins");
      }
      fontProvider.addDirectory(fontPath + fontName);
      fontProvider.addDirectory(fontPath + "other_language");
      return fontProvider;
    } catch (Exception e) {
      log.error("unable to apply fonts", e);
    }
    return null;
  }
}
