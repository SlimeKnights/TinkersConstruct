package slimeknights.tconstruct.library.book.sectiontransformer;

import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.mantle.client.book.data.SectionData;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.book.ContentListing;
import slimeknights.tconstruct.library.book.ContentModifier;
import slimeknights.tconstruct.library.modifiers.IModifier;

public class ModifierSectionTransformer extends SectionTransformer {

  public ModifierSectionTransformer() {
    super("modifiers");
  }

  @Override
  public void transform(BookData book, SectionData data) {
    boolean gotData = false;

    ContentListing listing = new ContentListing();
    listing.title = book.translate("modifiers");

    PageData page = new PageData(true);
    page.name = "modifiers";
    page.source = data.source;
    page.parent = data;
    page.content = listing;

    for(PageData modifierPage : data.pages) {
      if(modifierPage.content instanceof ContentModifier) {
        IModifier modifier = TinkerRegistry.getModifier(((ContentModifier) modifierPage.content).modifierName);
        if(modifier != null) {
          gotData = true;
          listing.addEntry(modifier.getLocalizedName(), modifierPage);
        }
      }
    }

    if(gotData) {
      page.load();

      data.pages.add(0, page);
    }
  }
}
