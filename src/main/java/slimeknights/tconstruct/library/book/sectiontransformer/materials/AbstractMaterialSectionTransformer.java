package slimeknights.tconstruct.library.book.sectiontransformer.materials;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.mantle.client.book.data.SectionData;
import slimeknights.mantle.client.book.data.content.PageContent;
import slimeknights.mantle.client.book.data.element.ImageData;
import slimeknights.mantle.client.book.repository.BookRepository;
import slimeknights.mantle.client.screen.book.element.ImageElement;
import slimeknights.mantle.client.screen.book.element.ItemElement;
import slimeknights.mantle.client.screen.book.element.SizedBookElement;
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.book.content.ContentMaterial;
import slimeknights.tconstruct.library.book.content.ContentPageIconList;
import slimeknights.tconstruct.library.book.sectiontransformer.SectionTransformer;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;

import java.util.ArrayList;
import java.util.Arrays;
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
    List<ContentPageIconList> listPages = ContentPageIconList.getPagesNeededForItemCount(materialList.size(), sectionData, book.translate(this.sectionName));

    ListIterator<ContentPageIconList> iter = listPages.listIterator();
    ContentPageIconList overview = iter.next();

    for (IMaterial material : materialList) {
      assert Minecraft.getInstance().world != null;
      List<MaterialRecipe> recipes = RecipeHelper.getJEIRecipes(Minecraft.getInstance().world.getRecipeManager(), RecipeTypes.MATERIAL, MaterialRecipe.class).stream().filter(recipe -> recipe.getMaterial() == material).collect(Collectors.toList());
      List<ItemStack> displayStacks = new ArrayList<>();

      for (MaterialRecipe recipe : recipes) {
        for (Ingredient ingredient : recipe.getIngredients()) {
          if (!ingredient.hasNoMatchingItems()) {
            displayStacks.addAll(Arrays.asList(ingredient.getMatchingStacks()));
          }
        }
      }

      PageData page = this.addPage(sectionData, material.getIdentifier().toString(), ContentMaterial.ID, this.getPageContent(material, displayStacks));

      SizedBookElement icon;
      if (!displayStacks.isEmpty())
        icon = new ItemElement(0, 0, 1f, displayStacks);
      else {
        icon = new ImageElement(0, 0, 32, 32, ImageData.MISSING);
        System.out.println("Material with id " + material.getIdentifier() + " has no representation items associated with it");
      }

      while (!overview.addLink(icon, new TranslationTextComponent(material.getTranslationKey()).modifyStyle(style -> style.setColor(material.getColor())), page)) {
        overview = iter.next();
      }
    }
  }
}
