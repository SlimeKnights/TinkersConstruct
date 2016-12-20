package slimeknights.tconstruct.library.book.sectiontransformer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.mantle.client.book.data.SectionData;
import slimeknights.tconstruct.library.book.content.ContentListing;

@SideOnly(Side.CLIENT)
public class ContentListingSectionTransformer extends SectionTransformer {

  public ContentListingSectionTransformer(String sectionName) {
    super(sectionName);
  }

  @Override
  public void transform(BookData book, SectionData data) {
    ContentListing listing = new ContentListing();
    listing.title = book.translate(sectionName);

    PageData listingPage = new PageData(true);
    listingPage.name = sectionName;
    listingPage.source = data.source;
    listingPage.parent = data;
    listingPage.content = listing;

    data.pages.forEach(sectionPage -> processPage(book, listing, sectionPage));

    if(listing.hasEntries()) {
      listingPage.load();

      data.pages.add(0, listingPage);
    }
  }

  protected void processPage(BookData book, ContentListing listing, PageData page) {
    if(!page.getTitle().equals("hidden")) {
      listing.addEntry(book.translate(page.getTitle()), page);
    }
  }
}
