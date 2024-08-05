package com.pdf_generator.openhtmltopdf_library;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import com.openhtmltopdf.pdfboxout.PageSupplier;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

@Slf4j
public class BackgroundImageHandler implements PageSupplier {

  private String imagePath;

  public BackgroundImageHandler(String imagePath) {
    this.imagePath = defaultIfNull(imagePath, EMPTY);
  }

  @Override
  public PDPage requestPage(PDDocument pdDocument, float v, float v1, int i, int i1) {
    PDPage page = new PDPage(PDRectangle.A4);
    try {
      pdDocument.addPage(page);
      if (imagePath.isEmpty()) {
        return page;
      }
      byte[] imageBytes = Files.readAllBytes(Paths.get(imagePath));
      PDImageXObject letterheadImage = PDImageXObject.createFromByteArray(pdDocument, imageBytes, "letterhead");
      PDPageContentStream contentStream = new PDPageContentStream(pdDocument, page, PDPageContentStream.AppendMode.APPEND, true, true);
      contentStream.drawImage(letterheadImage, 0, 0, PDRectangle.A4.getWidth(), PDRectangle.A4.getHeight());
      contentStream.close();
    } catch (Exception e) {
      log.error("error applying background image to pdf file", e);
    }
    return page;
  }
}
