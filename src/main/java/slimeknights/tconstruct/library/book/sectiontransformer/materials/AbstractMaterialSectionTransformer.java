package slimeknights.tconstruct.library.book.sectiontransformer.materials;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.mantle.client.book.data.SectionData;
import slimeknights.mantle.client.book.data.content.PageContent;
import slimeknights.mantle.client.book.repository.BookRepository;
import slimeknights.mantle.client.screen.book.element.ItemElement;
import slimeknights.mantle.client.screen.book.element.SizedBookElement;
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.book.content.ContentMaterial;
import slimeknights.tconstruct.library.book.content.ContentPageIconList;
import slimeknights.tconstruct.library.book.sectiontransformer.SectionTransformer;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.tools.TinkerToolParts;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractMaterialSectionTransformer extends SectionTransformer {

  public AbstractMaterialSectionTransformer(String sectionName) {
    super(sectionName);
  }

  protected abstract boolean isValidMaterial(IMaterial material);

  protected abstract PageContent getPageContent(IMaterial material, List<ItemStack> displayStacks);

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
      assert Minecraft.getInstance().world != null;
      List<MaterialRecipe> recipes = RecipeHelper.getUIRecipes(Minecraft.getInstance().world.getRecipeManager(), RecipeTypes.MATERIAL, MaterialRecipe.class, recipe -> recipe.getMaterial() == material);
      List<ItemStack> displayStacks = new ArrayList<>();

      for (MaterialRecipe recipe : recipes) {
        displayStacks.addAll(recipe.getDisplayItems());
      }
      if (displayStacks.isEmpty()) {
        TConstruct.log.debug("Material with id " + material.getIdentifier() + " has no representation items associated with it, using repair kit");
        // bypass the valid check, because we need to show something
        displayStacks.add(TinkerToolParts.repairKit.get().withMaterialForDisplay(material.getIdentifier()));
      }

      PageData page = this.addPage(sectionData, material.getIdentifier().toString(), ContentMaterial.ID, this.getPageContent(material, displayStacks));

      SizedBookElement icon = new ItemElement(0, 0, 1f, displayStacks);
      while (!overview.addLink(icon, new TranslationTextComponent(material.getTranslationKey()).modifyStyle(style -> style.setColor(material.getColor())), page)) {
        overview = iter.next();
      }
    }
  }
}
