package slimeknights.tconstruct.library.book.sectiontransformer.materials;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.mantle.client.book.data.SectionData;
import slimeknights.mantle.client.book.repository.BookRepository;
import slimeknights.mantle.client.book.transformer.SectionTransformer;
import slimeknights.mantle.client.screen.book.element.ItemElement;
import slimeknights.mantle.client.screen.book.element.SizedBookElement;
import slimeknights.tconstruct.library.book.content.ContentMaterial;
import slimeknights.tconstruct.library.book.content.ContentPageIconList;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;

import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractMaterialSectionTransformer extends SectionTransformer {

  protected final boolean detailed;
  public AbstractMaterialSectionTransformer(String sectionName, boolean detailed) {
    super(sectionName);
    this.detailed = detailed;
  }

  /**
   * Determines if a material should show in this book
   * @param material  Material to check
   * @return  True if it should show
   */
  protected abstract boolean isValidMaterial(IMaterial material);

  /**
   * Gets the page for the given material, can override if you use a different page type
   * @param material       Material to display
   * @return  Material page
   */
  protected ContentMaterial getPageContent(IMaterial material) {
    return new ContentMaterial(material, detailed);
  }

  @Override
  public void transform(BookData book, SectionData sectionData) {
    sectionData.source = BookRepository.DUMMY;
    sectionData.parent = book;

    List<IMaterial> materialList = MaterialRegistry.getMaterials().stream()
      .filter(this::isValidMaterial)
      .collect(Collectors.toList());

    if (materialList.isEmpty()) {
      return;
    }

    // calculate pages needed
    List<ContentPageIconList> listPages = ContentPageIconList.getPagesNeededForItemCount(materialList.size(), sectionData, book.translate(this.sectionName), book.strings.get(String.format("%s.subtext", this.sectionName)));

    ListIterator<ContentPageIconList> iter = listPages.listIterator();
    ContentPageIconList overview = iter.next();

    for (IMaterial material : materialList) {
      ContentMaterial contentMaterial = this.getPageContent(material);
      PageData page = this.addPage(sectionData, material.getIdentifier().toString(), ContentMaterial.ID, contentMaterial);

      SizedBookElement icon = new ItemElement(0, 0, 1f, contentMaterial.getDisplayStacks());
      while (!overview.addLink(icon, contentMaterial.getTitle(), page)) {
        overview = iter.next();
      }
    }
  }
}
