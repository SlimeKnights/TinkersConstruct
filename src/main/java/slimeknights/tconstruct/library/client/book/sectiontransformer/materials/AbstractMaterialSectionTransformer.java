package slimeknights.tconstruct.library.client.book.sectiontransformer.materials;

import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.mantle.client.book.data.SectionData;
import slimeknights.mantle.client.book.data.content.PageContent;
import slimeknights.mantle.client.book.repository.BookRepository;
import slimeknights.mantle.client.book.transformer.SectionTransformer;
import slimeknights.mantle.client.screen.book.element.ItemElement;
import slimeknights.mantle.client.screen.book.element.SizedBookElement;
import slimeknights.tconstruct.library.client.book.content.ContentMaterial;
import slimeknights.tconstruct.library.client.book.content.ContentPageIconList;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;
import java.util.function.Predicate;

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
  protected ContentMaterial getPageContent(MaterialVariantId material) {
    return new ContentMaterial(material, detailed);
  }

  @Override
  public void transform(BookData book, SectionData sectionData) {
    createPages(book, sectionData, this::isValidMaterial, this::getPageContent);
  }

  /** Helper to add a page to the section */
  private static PageData addPageStatic(SectionData data, String name, ResourceLocation type, PageContent content) {
    PageData page = new PageData(true);
    page.source = data.source;
    page.parent = data;
    page.name = name;
    page.type = type;
    page.content = content;
    page.load();

    data.pages.add(page);

    return page;
  }

  /**
   * Creates all the pages for the materials
   * @param book            Book data
   * @param sectionData     Section data
   * @param validMaterial   Predicate to validate materials
   * @param pageCreator     Logic to create a page
   */
  public static void createPages(BookData book, SectionData sectionData, Predicate<IMaterial> validMaterial, Function<MaterialVariantId, ContentMaterial> pageCreator) {
    sectionData.source = BookRepository.DUMMY;
    sectionData.parent = book;

    List<IMaterial> materialList = MaterialRegistry.getMaterials().stream().filter(validMaterial).toList();
    if (materialList.isEmpty()) {
      return;
    }

    // calculate pages needed
    List<ContentPageIconList> listPages = ContentPageIconList.getPagesNeededForItemCount(materialList.size(), sectionData, book.translate(sectionData.name), book.strings.get(String.format("%s.subtext", sectionData.name)));

    ListIterator<ContentPageIconList> iter = listPages.listIterator();
    ContentPageIconList overview = iter.next();

    for (IMaterial material : materialList) {
      MaterialId materialId = material.getIdentifier();
      ContentMaterial contentMaterial = pageCreator.apply(materialId);
      PageData page = addPageStatic(sectionData, materialId.toString(), ContentMaterial.ID, contentMaterial);

      SizedBookElement icon = new ItemElement(0, 0, 1f, contentMaterial.getDisplayStacks());
      while (!overview.addLink(icon, contentMaterial.getTitleComponent(), page)) {
        overview = iter.next();
      }
    }
  }
}
