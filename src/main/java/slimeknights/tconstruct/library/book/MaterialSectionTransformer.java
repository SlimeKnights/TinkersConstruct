package slimeknights.tconstruct.library.book;

import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

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
import slimeknights.tconstruct.library.materials.MaterialTypes;

/** Populates the materials section for tool materials with content */
public class MaterialSectionTransformer extends SectionTransformer {

  public MaterialSectionTransformer() {
    super("materials");
  }

  @Override
  public void transform(BookData book, SectionData data) {
    data.source = BookRepository.DUMMY;
    data.parent = book;

    Collection<Material> materialList = TinkerRegistry.getAllMaterials();

    // calculate pages needed
    int count = materialList.size();
    List<ContentPageIconList> listPages = Lists.newArrayList();

    while(count > 0) {
      ContentPageIconList overview = new ContentPageIconList(20);
      PageData page = new PageData(true);
      page.source = data.source;
      page.parent = data;
      page.content = overview;
      page.load();

      data.pages.add(page);

      overview.title = book.translate("materials");

      listPages.add(overview);

      count -= overview.getMaxIconCount();
    }

    ListIterator<ContentPageIconList> iter = listPages.listIterator(1);
    while(iter.hasNext()) {
      // same scale if multiple pages
      iter.next().maxScale = 1f;
    }


    iter = listPages.listIterator();
    ContentPageIconList overview = iter.next();

    for(Material material : TinkerRegistry.getAllMaterials()) {
      if(material.isHidden() || material.getAllStats().isEmpty() || !material.hasItems()) {
        continue;
      }
      if(!material.hasStats(MaterialTypes.HEAD) && !material.hasStats(MaterialTypes.HEAD) && !material.hasStats(MaterialTypes.HEAD)) {
        continue;
      }
      PageData page = new PageData(true);
      page.source = data.source;
      page.parent = data;
      page.name = material.getIdentifier();
      page.type = ContentMaterial.ID;
      page.content = new ContentMaterial(material);
      page.load();

      SizedBookElement icon;
      if(material.getRepresentativeItem() != null) {
        icon = new ElementItem(0, 0, 1f, material.getRepresentativeItem());
      }
      else {
        icon = new ElementImage(ImageData.MISSING);
      }

      if(!overview.addLink(icon, material.getLocalizedNameColored(), page)) {
        overview = iter.next();
      }

      data.pages.add(page);
    }
  }
}
