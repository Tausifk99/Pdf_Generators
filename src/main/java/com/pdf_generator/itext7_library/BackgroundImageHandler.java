package com.pdf_generator.itext7_library;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Image;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class BackgroundImageHandler implements IEventHandler {

  private String base64Image;

  public BackgroundImageHandler(String base64Image) {
    this.base64Image = defaultIfNull(base64Image, StringUtils.EMPTY);
  }

  @Override
  public void handleEvent(Event event) {
    try {
      PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
      PdfDocument pdfDoc = docEvent.getDocument();
      PdfPage page = docEvent.getPage();
      PdfCanvas canvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdfDoc);
      Rectangle pageSize = page.getPageSize();

      byte[] decodedBytes = Base64.getDecoder().decode(base64Image);
      Image backgroundImage = new Image(ImageDataFactory.create(decodedBytes));

      backgroundImage.setFixedPosition(0, 0);
      backgroundImage.scaleToFit(PageSize.A4.getWidth(), PageSize.A4.getHeight());
      Canvas cnv = new Canvas(canvas, pageSize);
      cnv.add(backgroundImage);
      cnv.close();
    } catch (Exception e) {
      log.error("error applying background image to pdf file", e);
    }
  }
}
