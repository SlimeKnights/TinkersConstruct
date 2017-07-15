package slimeknights.tconstruct.library.book.sectiontransformer;

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

    if(materialList.isEmpty()) {
      return;
    }

    // calculate pages needed
    List<ContentPageIconList> listPages = ContentPageIconList.getPagesNeededForItemCount(materialList.size(), data, book.translate(sectionName));

    ListIterator<ContentPageIconList> iter = listPages.listIterator();
    ContentPageIconList overview = iter.next();

    for(Material material : materialList) {
      PageData page = addPage(data, material.getIdentifier(), ContentMaterial.ID, getPageContent(material));

      SizedBookElement icon;
      if(material.getRepresentativeItem() != null) {
        icon = new ElementItem(0, 0, 1f, material.getRepresentativeItem());
      }
      else {
        icon = new ElementImage(ImageData.MISSING);
      }

      while(!overview.addLink(icon, material.getLocalizedNameColored(), page)) {
        overview = iter.next();
      }
    }
  }
}
