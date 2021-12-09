package slimeknights.tconstruct.library.book;

import slimeknights.mantle.client.book.data.content.PageContent;

/** @deprecated use {@link PageContent#setLargeTitle(Boolean)} and {@link PageContent#setCenterTitle(Boolean)} */
public abstract class TinkerPage extends PageContent {
  public TinkerPage() {
    this.setCenterTitle(true);
    this.setLargeTitle(true);
  }
}
