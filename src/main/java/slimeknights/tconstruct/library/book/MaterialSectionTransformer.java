package slimeknights.tconstruct.library.book;

import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.mantle.client.book.data.SectionData;
import slimeknights.mantle.client.book.data.element.ImageData;
import slimeknights.mantle.client.book.repository.BookRepository;
import slimeknights.mantle.client.gui.book.element.ElementImage;
import slimeknights.mantle.client.gui.book.element.ElementItem;
import slimeknights.mantle.client.gui.book.element.SizedBookElement;
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

    // list page
    ContentPageIconList overview = new ContentPageIconList(20);
    PageData page = new PageData(true);
    page.source = data.source;
    page.parent = data;
    page.content = overview;
    page.load();

    data.pages.add(page);

    overview.title = book.translate("materials");

    for(Material material : TinkerRegistry.getAllMaterials()) {
      if(material.isHidden() || material.getAllStats().isEmpty() || !material.hasItems()) {
        continue;
      }
      page = new PageData(true);
      page.source = data.source;
      page.parent = data;
      page.name = material.getIdentifier();
      page.type = ContentMaterial.Tool.ID;
      page.content = new ContentMaterial.Tool(material);
      page.load();

      SizedBookElement icon;
      if(material.getRepresentativeItem() != null) {
        icon = new ElementItem(0, 0, 1f, material.getRepresentativeItem());
      }
      else {
        icon = new ElementImage(ImageData.MISSING);
      }
      overview.addLink(icon, material.getLocalizedNameColored(), page);

      data.pages.add(page);
    }
  }
}
