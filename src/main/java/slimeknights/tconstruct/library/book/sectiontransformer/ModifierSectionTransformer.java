package slimeknights.tconstruct.library.book.sectiontransformer;

import slimeknights.mantle.client.book.data.PageData;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.book.content.ContentListing;
import slimeknights.tconstruct.library.book.content.ContentModifier;
import slimeknights.tconstruct.library.modifiers.IModifier;

public class ModifierSectionTransformer extends ContentListingSectionTransformer {

  public ModifierSectionTransformer() {
    super("modifiers");
  }


  @Override
  protected void processPage(ContentListing listing, PageData page) {
    if(page.content instanceof ContentModifier) {
      IModifier modifier = TinkerRegistry.getModifier(((ContentModifier) page.content).modifierName);
      if(modifier != null) {
        listing.addEntry(modifier.getLocalizedName(), page);
      }
    }
  }
}
