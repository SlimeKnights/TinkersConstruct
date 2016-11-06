package slimeknights.tconstruct.library.book.sectiontransformer;

import com.google.common.collect.Lists;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.mantle.client.book.data.SectionData;
import slimeknights.mantle.client.book.data.content.PageContent;
import slimeknights.mantle.client.book.data.element.ImageData;
import slimeknights.mantle.client.book.repository.BookRepository;
import slimeknights.mantle.client.gui.book.element.ElementImage;
import slimeknights.mantle.client.gui.book.element.ElementItem;
import slimeknights.mantle.client.gui.book.element.SizedBookElement;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.book.content.ContentMaterial;
import slimeknights.tconstruct.library.book.content.ContentPageIconList;
import slimeknights.tconstruct.library.materials.Material;

@SideOnly(Side.CLIENT)
public abstract class AbstractMaterialSectionTransformer extends SectionTransformer {

  public AbstractMaterialSectionTransformer(String sectionName) {
    super(sectionName);
  }

  protected abstract boolean isValidMaterial(Material material);

  protected abstract PageContent getPageContent(Material material);

  @Override
  public void transform(BookData book, SectionData data) {
    data.source = BookRepository.DUMMY;
    data.parent = book;

    List<Material> materialList = TinkerRegistry.getAllMaterials().stream()
                                                .filter(m -> !m.isHidden())
                                                .filter(Material::hasItems)
                                                .filter(this::isValidMaterial)
                                                .collect(Collectors.toList());

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

      overview.title = book.translate(sectionName);

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

    for(Material material : materialList) {
      PageData page = new PageData(true);
      page.source = data.source;
      page.parent = data;
      page.name = material.getIdentifier();
      page.type = ContentMaterial.ID;
      page.content = getPageContent(material);
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
