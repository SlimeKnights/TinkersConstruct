package slimeknights.tconstruct.library.book;

import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.mantle.client.book.data.SectionData;
import slimeknights.mantle.client.book.data.content.ContentText;
import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.book.repository.BookRepository;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.modifiers.IModifier;

public class ModifierSectionTransformer extends SectionTransformer {

  public ModifierSectionTransformer() {
    super("modifiers");
  }

  @Override
  public void transform(BookData book, SectionData data) {
    data.source = BookRepository.DUMMY;
    data.parent = book;

    for(IModifier modifier : TinkerRegistry.getAllModifiers()) {
      PageData page = new PageData(true);
      page.source = data.source;
      page.parent = data;
      page.type = "f";
      page.content = new ContentModifier(modifier);
      page.load();

      data.pages.add(page);
    }
  }
}
