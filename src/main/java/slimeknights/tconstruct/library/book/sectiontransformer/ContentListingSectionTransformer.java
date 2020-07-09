package slimeknights.tconstruct.library.book.sectiontransformer;

import java.util.ArrayList;
import java.util.Arrays;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.mantle.client.book.data.SectionData;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.book.content.ContentListing;



@SideOnly(Side.CLIENT)
public class ContentListingSectionTransformer extends SectionTransformer {

  private ArrayList<PageData> filteredPage = new ArrayList<>();
  
  public ContentListingSectionTransformer(String sectionName) {
    super(sectionName);
  }

  @Override
  public void transform(BookData book, SectionData data) {
    ContentListing listing = new ContentListing();
    listing.title = book.translate(sectionName);

    //filter pages by blacklist
    data.pages.forEach(sectionPage -> filterPage(sectionPage));
    data.pages = filteredPage;
    
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

  protected void filterPage(PageData page) {
    if(!Arrays.asList(Config.toolTypeBlacklist).contains(page.name)) {
      filteredPage.add(page);
    }
  }
  
  protected void processPage(BookData book, ContentListing listing, PageData page) {
    if(!page.getTitle().equals("hidden")) {
      listing.addEntry(book.translate(page.getTitle()), page);
    }
  }
}
