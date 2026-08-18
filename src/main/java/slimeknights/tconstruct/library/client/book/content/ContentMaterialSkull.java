package slimeknights.tconstruct.library.client.book.content;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.mantle.client.screen.book.element.ItemElement;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.book.content.material.SingleMaterialStatContent;
import slimeknights.tconstruct.library.client.book.elements.TinkerItemElement;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.library.recipe.casting.IDisplayableCastingRecipe;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.stats.SkullStats;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Extension of the material page to display skull stats for the slimeskull
 * TODO 1.21: move to {@link slimeknights.tconstruct.library.client.book.content.material}.
 */
public class ContentMaterialSkull extends SingleMaterialStatContent {
  /** Translation key for skull recipe */
  private static final Component SKULL = TConstruct.makeTranslation("book", "material.skull");
  /** Translation key for skull recipe */
  private static final String SKULL_FROM = TConstruct.makeTranslationKey("book", "material.skull_from");
  /** Page ID for using this index directly */
  public static final ResourceLocation ID = TConstruct.getResource("skull_material");

  /** casting recipe used to create this item */
  protected transient IDisplayableCastingRecipe skullRecipe = null;
  /** If true, casting recipe was looked up */
  private transient boolean searchedSkullRecipe = false;
  /** List of skull items as inputs */
  protected transient List<ItemStack> skullStacks = null;

  public ContentMaterialSkull(MaterialVariantId material, boolean detailed) {
    super(material, detailed);
  }

  @Override
  public ResourceLocation getId() {
    return ID;
  }

  @Override
  protected MaterialStatsId getStatType() {
    return SkullStats.ID;
  }

  @Override
  protected boolean hasPart() {
    return false;
  }

  @Override
  protected String translationSuffix() {
    return "skull";
  }

  @Override
  protected String getTextKey(MaterialId material) {
    // TODO 1.21: switch key format to encyclopedia.skull and encyclopedia.flavor
    return String.format(detailed ? "material.%s.%s.skull_encyclopedia" : "material.%s.%s.skull_flavor", material.getNamespace(), material.getPath());
  }

  /** Gets the recipe to cast this skull */
  @Nullable
  private IDisplayableCastingRecipe getSkullRecipe() {
    Level world = Minecraft.getInstance().level;
    if (!searchedSkullRecipe && world != null) {
      skullRecipe = world.getRecipeManager().getAllRecipesFor(TinkerRecipeTypes.CASTING_BASIN.get()).stream()
												 .filter(recipe -> recipe instanceof IDisplayableCastingRecipe)
												 .map(recipe -> (IDisplayableCastingRecipe)recipe)
												 .filter(recipe -> {
                           ItemStack output = recipe.getOutput();
                           return output.getItem() == TinkerTools.slimesuit.get(ArmorItem.Type.HELMET) && MaterialIdNBT.from(output).getMaterial(0).getId().toString().equals(materialName);
                         })
												 .findFirst()
												 .orElse(null);
      searchedSkullRecipe = true;
    }
    return skullRecipe;
  }

  @Override
  public Component getTitleComponent() {
    // display slimeskull instead of material name
    IDisplayableCastingRecipe skullRecipe = getSkullRecipe();
    if (skullRecipe != null) {
      return skullRecipe.getOutput().getHoverName();
    }
    return super.getTitleComponent();
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
  protected void addPrimaryDisplayItems(List<ItemElement> displayTools, MaterialVariantId materialId) {
    displayTools.add(new TinkerItemElement(TinkerToolParts.repairKit.get().withMaterialForDisplay(materialId)));

    super.addPrimaryDisplayItems(displayTools, materialId);

    // add skull recipe to display items
    IDisplayableCastingRecipe skullRecipe = getSkullRecipe();
    if (skullRecipe != null) {
      // add repair kit
      List<ItemStack> casts = skullRecipe.getCastItems();
      if (!casts.isEmpty()) {
        ItemElement elementItem = new TinkerItemElement(0, 0, 1, casts);
        elementItem.tooltip = List.of(
          SKULL,
          Component.translatable(SKULL_FROM, casts.get(0).getHoverName()).withStyle(ChatFormatting.GRAY)
        );
        displayTools.add(elementItem);
      }
    }
  }
}
