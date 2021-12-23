package slimeknights.tconstruct.library.book.sectiontransformer;

import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.mantle.client.book.data.SectionData;
import slimeknights.mantle.client.book.transformer.SectionTransformer;
import slimeknights.tconstruct.library.book.content.ContentListing;

/** @deprecated use {@link slimeknights.mantle.client.book.transformer.ContentListingSectionTransformer} */
@Deprecated
public class ContentListingSectionTransformer extends SectionTransformer {
  public ContentListingSectionTransformer(String sectionName) {
    super(sectionName);
  }

  @Override
  public void transform(BookData book, SectionData data) {
    ContentListing listing = new ContentListing();
    listing.title = book.translate(sectionName);
    String subtextKey = sectionName + ".subtext";
    if (book.strings.containsKey(subtextKey)) {
      listing.subText = book.translate(subtextKey);
    }

    PageData listingPage = new PageData(true);
    listingPage.name = sectionName;
    listingPage.source = data.source;
    listingPage.parent = data;
    listingPage.content = listing;

    data.pages.forEach(sectionPage -> processPage(book, listing, sectionPage));

    if (listing.hasEntries()) {
      listingPage.load();

      data.pages.add(0, listingPage);
    }
  }

  protected void processPage(BookData book, ContentListing listing, PageData page) {
    if (!page.getTitle().equals("hidden")) {
      listing.addEntry(book.translate(page.getTitle()), page);
    }
  }
}
