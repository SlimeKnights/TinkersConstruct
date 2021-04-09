package slimeknights.tconstruct.library.book.sectiontransformer.materials;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.mantle.client.book.data.SectionData;
import slimeknights.mantle.client.book.data.content.PageContent;
import slimeknights.mantle.client.book.data.element.ImageData;
import slimeknights.mantle.client.book.repository.BookRepository;
import slimeknights.mantle.client.screen.book.element.ImageElement;
import slimeknights.mantle.client.screen.book.element.SizedBookElement;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.book.content.ContentMaterial;
import slimeknights.tconstruct.library.book.content.ContentPageIconList;
import slimeknights.tconstruct.library.book.sectiontransformer.SectionTransformer;
import slimeknights.tconstruct.library.materials.IMaterial;

import java.awt.*;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractMaterialSectionTransformer extends SectionTransformer {

  public AbstractMaterialSectionTransformer(String sectionName) {
    super(sectionName);
  }

  protected abstract boolean isValidMaterial(IMaterial material);

  protected abstract PageContent getPageContent(IMaterial material);

  @Override
  public void transform(BookData book, SectionData sectionData) {
    sectionData.source = BookRepository.DUMMY;
    sectionData.parent = book;

    List<IMaterial> materialList = MaterialRegistry.getMaterials().stream()
      .filter(this::isValidMaterial)
      .collect(Collectors.toList());

    if(materialList.isEmpty()) {
      return;
    }

    // calculate pages needed
    List<ContentPageIconList> listPages = ContentPageIconList.getPagesNeededForItemCount(materialList.size(), sectionData, book.translate(this.sectionName));

    ListIterator<ContentPageIconList> iter = listPages.listIterator();
    ContentPageIconList overview = iter.next();

    for(IMaterial material : materialList) {
      PageData page = this.addPage(sectionData, material.getIdentifier().toString(), ContentMaterial.ID, this.getPageContent(material));

      SizedBookElement icon = new ImageElement(ImageData.MISSING);

      while(!overview.addLink(icon, material.getTranslationKey(), page)) {
        overview = iter.next();
      }
    }
  }
}
