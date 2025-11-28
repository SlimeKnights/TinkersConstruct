package slimeknights.tconstruct.library.client.book.sectiontransformer.materials;

import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.SectionData;
import slimeknights.mantle.client.book.transformer.SectionTransformer;
import slimeknights.tconstruct.library.client.book.content.AbstractMaterialContent;
import slimeknights.tconstruct.library.client.book.content.MeleeHarvestMaterialContent;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

import java.util.function.Function;
import java.util.function.Predicate;

/** @deprecated use {@link TierRangeMaterialSectionTransformer} */
@Deprecated(forRemoval = true)
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
  protected AbstractMaterialContent getPageContent(MaterialVariantId material) {
    return new MeleeHarvestMaterialContent(material, detailed);
  }

  @Override
  public void transform(BookData book, SectionData sectionData) {
    createPages(book, sectionData, this::isValidMaterial, this::getPageContent);
  }

  /**
   * Creates all the pages for the materials
   * @param book            Book data
   * @param sectionData     Section data
   * @param validMaterial   Predicate to validate materials
   * @param pageCreator     Logic to create a page
   * @deprecated use {@link TierRangeMaterialSectionTransformer#createPages(BookData, SectionData, Predicate, Function)}
   */
  @Deprecated(forRemoval = true)
  public static void createPages(BookData book, SectionData sectionData, Predicate<IMaterial> validMaterial, Function<MaterialVariantId,AbstractMaterialContent> pageCreator) {
    TierRangeMaterialSectionTransformer.createPages(book, sectionData, validMaterial, pageCreator, null);
  }
}
