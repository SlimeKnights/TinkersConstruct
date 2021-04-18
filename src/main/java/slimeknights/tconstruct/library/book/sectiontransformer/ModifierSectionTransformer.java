package slimeknights.tconstruct.library.book.sectiontransformer;

import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.tconstruct.library.TinkerRegistries;
import slimeknights.tconstruct.library.book.content.ContentListing;
import slimeknights.tconstruct.library.book.content.ContentModifier;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierId;

public class ModifierSectionTransformer extends ContentListingSectionTransformer {

  public ModifierSectionTransformer() {
    super("modifiers");
  }

  @Override
  protected void processPage(BookData book, ContentListing listing, PageData page) {
    if (page.content instanceof ContentModifier) {
      Modifier modifier = TinkerRegistries.MODIFIERS.getValue(new ModifierId(((ContentModifier) page.content).modifierID));
      if (modifier != null) {
        listing.addEntry(modifier.getDisplayName().getString(), page);
      }
    }
  }
}
