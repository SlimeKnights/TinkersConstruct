package slimeknights.tconstruct.library.book.elements;

import slimeknights.mantle.client.book.data.element.TextData;

/** @deprecated use {@link slimeknights.mantle.client.screen.book.element.ListingLeftElement} */
@Deprecated
public class ListingLeftElement extends slimeknights.mantle.client.screen.book.element.ListingLeftElement {
  public ListingLeftElement(int x, int y, int width, int height, boolean subSection, TextData... text) {
    super(x, y, width, height, subSection, text);
  }
}
