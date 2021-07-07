package slimeknights.tconstruct.library.book.content;

import lombok.Getter;
import slimeknights.mantle.client.book.BookTransformer;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.mantle.client.book.data.SectionData;
import slimeknights.mantle.client.book.data.content.ContentBlank;

import java.util.Iterator;

/**
 * Variant of blank pages that only adds the page on a specific side, useful to force the next page to the left or right regardless of the number of pages before
 * TODO: move to Mantle
 */
@Getter
public abstract class ContentPadding extends ContentBlank {
  public static final String LEFT_ID = "left_padding";
  public static final String RIGHT_ID = "right_padding";

  /** If true, this page is padding the left side, false pads the right side */
  public abstract boolean isLeft();

  /** Left variant */
  public static class ContentLeftPadding extends ContentPadding {
    @Override
    public boolean isLeft() {
      return true;
    }
  }

  /** Right variant */
  public static class ContentRightPadding extends ContentPadding {
    @Override
    public boolean isLeft() {
      return false;
    }
  }

  /** Transformer to make this page type work */
  public static class PaddingBookTransformer extends BookTransformer {
    public static final PaddingBookTransformer INSTANCE = new PaddingBookTransformer();

    private PaddingBookTransformer() {}

    @Override
    public void transform(BookData bookData) {
      // first page is on the right side
      boolean isLeft = false;
      for (SectionData section : bookData.sections) {
        Iterator<PageData> pageIterator = section.pages.iterator();
        while (pageIterator.hasNext()) {
          PageData data = pageIterator.next();
          // if its left and the current page is odd, or its right and the current page is even, skip
          if (data.content instanceof ContentPadding && ((ContentPadding) data.content).isLeft() == isLeft) {
            pageIterator.remove();
          } else {
            isLeft = !isLeft;
          }
        }
      }
    }
  }
}
