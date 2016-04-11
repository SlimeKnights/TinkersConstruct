package slimeknights.tconstruct.library.book;

import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.mantle.client.book.data.SectionData;
import slimeknights.mantle.client.book.repository.BookRepository;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;

public class MaterialSectionTransformer extends SectionTransformer {

  public MaterialSectionTransformer() {
    super("materials");
  }

  @Override
  public void transform(BookData book, SectionData data) {
    data.source = BookRepository.DUMMY;
    data.parent = book;

    for(Material material : TinkerRegistry.getAllMaterials()) {
      if(material.getAllStats().isEmpty() || !material.hasItems()) {
        continue;
      }
      PageData page = new PageData(true);
      page.source = data.source;
      page.parent = data;
      page.type = ContentMaterial.Tool.ID;
      page.content = new ContentMaterial.Tool(material);
      page.load();

      data.pages.add(page);
    }
  }
}
