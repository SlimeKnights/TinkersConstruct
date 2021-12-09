package slimeknights.tconstruct.library.book.content;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.ForgeI18n;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.screen.book.BookScreen;
import slimeknights.mantle.client.screen.book.element.BookElement;
import slimeknights.mantle.client.screen.book.element.ItemElement;
import slimeknights.mantle.client.screen.book.element.TextElement;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.book.elements.TinkerItemElement;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.casting.IDisplayableCastingRecipe;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.item.ArmorSlotType;
import slimeknights.tconstruct.tools.stats.SkullStats;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/** Extension of the material page to display skull stats for the slimeskull */
public class ContentMaterialSkull extends ContentMaterial {
  /** Translation key for skull recipe */
  private static final String SKULL_FROM = TConstruct.makeTranslationKey("book", "material.skull_from");

  /** casting recipe used to create this item */
  protected transient IDisplayableCastingRecipe skullRecipe = null;
  /** If true, casting recipe was looked up */
  private transient boolean searchedSkullRecipe = false;
  /** List of skull items as inputs */
  protected transient List<ItemStack> skullStacks = null;

  public ContentMaterialSkull(IMaterial material, boolean detailed) {
    super(material, detailed);
  }

  /** Gets the recipe to cast this skull */
  @Nullable
  private IDisplayableCastingRecipe getSkullRecipe() {
    World world = Minecraft.getInstance().world;
    if (!searchedSkullRecipe && world != null) {
      skullRecipe = world.getRecipeManager().getRecipesForType(RecipeTypes.CASTING_BASIN).stream()
                         .filter(recipe -> recipe instanceof IDisplayableCastingRecipe)
                         .map(recipe -> (IDisplayableCastingRecipe)recipe)
                         .filter(recipe -> {
                           ItemStack output = recipe.getOutput();
                           return output.getItem() == TinkerTools.slimesuit.get(ArmorSlotType.HELMET) && MaterialIdNBT.from(output).getMaterial(0).toString().equals(materialName);
                         })
                         .findFirst()
                         .orElse(null);
      searchedSkullRecipe = true;
    }
    return skullRecipe;
  }

  @Override
  public ITextComponent getTitle() {
    // display slimeskull instead of material name
    IDisplayableCastingRecipe skullRecipe = getSkullRecipe();
    if (skullRecipe != null) {
      return skullRecipe.getOutput().getDisplayName();
    }
    return super.getTitle();
  }

  @Override
  public List<ItemStack> getDisplayStacks() {
    // display skull items instead of repair items
    IDisplayableCastingRecipe skullRecipe = getSkullRecipe();
    if (skullRecipe != null) {
      List<ItemStack> skulls = skullRecipe.getCastItems();
      if (!skulls.isEmpty()) {
        return skulls;
      }
    }
    return super.getDisplayStacks();
  }

  @Override
  protected boolean supportsStatType(MaterialStatsId statsId) {
    return statsId.equals(SkullStats.ID); // support only skulls
  }

  @Override
  protected void addPrimaryDisplayItems(List<ItemElement> displayTools, MaterialId materialId) {
    displayTools.add(new TinkerItemElement(TinkerToolParts.repairKit.get().withMaterialForDisplay(materialId)));

    super.addPrimaryDisplayItems(displayTools, materialId);

    // add skull recipe to display items
    IDisplayableCastingRecipe skullRecipe = getSkullRecipe();
    if (skullRecipe != null) {
      // add repair kit
      List<ItemStack> casts = skullRecipe.getCastItems();
      if (!casts.isEmpty()) {
        ItemElement elementItem = new TinkerItemElement(0, 0, 1, casts);
        elementItem.tooltip = ImmutableList.of(new TranslationTextComponent(SKULL_FROM, casts.get(0).getDisplayName()));
        displayTools.add(elementItem);
      }
    }
  }

  @Override
  public void build(BookData book, ArrayList<BookElement> list, boolean rightSide) {
    IMaterial material = getMaterial();
    this.addTitle(list, getTitle().getString(), true, material.getColor().getColor());

    // the cool tools to the left/right
    this.addDisplayItems(list, rightSide ? BookScreen.PAGE_WIDTH - 18 : 0, material.getIdentifier());

    // align page
    int top = getTitleHeight();
    int left = rightSide ? 0 : 22;
    int y = top + 5;
    int x = left + 5;
    int w = BookScreen.PAGE_WIDTH - 20;

    // skull stats, full width
    int skullTraits = this.addStatsDisplay(x, y, w, list, material, SkullStats.ID);
    y+= 65;

    // inspirational quote, or boring description text
    MaterialId id = material.getIdentifier();
    String textKey = String.format(detailed ? "material.%s.%s.skull_encyclopedia" : "material.%s.%s.skull_flavor", id.getNamespace(), id.getPath());
    if (I18n.hasKey(textKey)) {
      // using forge instead of I18n.format as that prevents % from being interpreted as a format key
      String translated = ForgeI18n.getPattern(textKey);
      if (!detailed) {
        translated = '"' + translated + '"';
      }
      TextData flavourData = new TextData(translated);
      flavourData.italic = !detailed;
      list.add(new TextElement(x, y + 10 * skullTraits, w, 60, flavourData));
    }
  }
}
