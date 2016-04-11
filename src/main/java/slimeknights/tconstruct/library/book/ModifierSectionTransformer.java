package slimeknights.tconstruct.library.book;

import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.mantle.client.book.data.SectionData;
import slimeknights.mantle.client.book.data.content.ContentText;
import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.book.repository.BookRepository;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.modifiers.Modifier;

public class ModifierSectionTransformer extends SectionTransformer {

  public ModifierSectionTransformer() {
    super("modifiers");
  }

  @Override
  public void transform(BookData book, SectionData data) {
    data.source = BookRepository.DUMMY;
    data.parent = book;

    for(IModifier modifier : TinkerRegistry.getAllModifiers()) {
      ContentText foo = new ContentText();
      foo.text = new TextData[]{new TextData(modifier.getLocalizedName())};

      PageData page = new PageData(true);
      page.source = data.source;
      page.parent = data;
      page.type = "f";
      page.content = foo;
      page.load();

      data.pages.add(page);
    }
  }
}
