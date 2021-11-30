package slimeknights.tconstruct.library.book.sectiontransformer;

import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.mantle.client.book.transformer.ContentGroupingSectionTransformer;
import slimeknights.tconstruct.library.TinkerRegistries;
import slimeknights.tconstruct.library.book.content.ContentModifier;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierId;

/** Section transformer to generate an index with modifier names */
public class ModifierSectionTransformer extends ContentGroupingSectionTransformer {
  public static final ModifierSectionTransformer INSTANCE = new ModifierSectionTransformer("modifiers");

  public ModifierSectionTransformer(String name, boolean largeTitle, boolean centerTitle) {
    super(name, largeTitle, centerTitle);
  }

  public ModifierSectionTransformer(String name) {
    super(name, null, null);
  }

  @Override
  protected boolean processPage(BookData book, GroupingBuilder builder, PageData page) {
    // modifiers add including their name
    if (page.content instanceof ContentModifier) {
      ContentModifier modifierContent = (ContentModifier)page.content;
      if (modifierContent.hasRequiredMod()) {
        ModifierId modifierId = new ModifierId(modifierContent.modifierID);
        if (TinkerRegistries.MODIFIERS.containsKey(modifierId)) {
          Modifier modifier = TinkerRegistries.MODIFIERS.getValue(modifierId);
          assert modifier != null; // contains key was true
          String title = page.getTitle();
          // if name is not translatable, use the modifier name
          if (page.name.equals(title)) {
            title = modifier.getDisplayName().getString();
          }
          builder.addPage(title, page);
          return true;
        }
      }
      return false;
      // starting with group means start a new column
    } else if (page.name.startsWith("group_")) {
      // skip adding the page if no data
      if (page.data.isEmpty()) {
        builder.addGroup(page.getTitle(), null);
        return false;
      } else {
        builder.addGroup(page.getTitle(), page);
        return true;
      }
      // anything other than hidden continues same column
    } else if (!page.name.equals("hidden")) {
      builder.addPage(page.getTitle(), page);
    }
    return true;
  }
}
