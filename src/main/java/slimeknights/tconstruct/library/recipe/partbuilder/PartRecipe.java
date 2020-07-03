package slimeknights.tconstruct.library.recipe.partbuilder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.tables.TinkerTables;

import javax.annotation.Nullable;
import java.util.Optional;

@AllArgsConstructor
public class PartRecipe implements IRecipe<IPartBuilderInventory> {
  @Getter
  protected final ResourceLocation id;
  @Getter
  protected final String group;
  @Getter
  protected final ResourceLocation pattern;
  /** Recipe material cost */
  @Getter
  protected final int cost;
  /** Normal recipe result, with no material */
  protected final ItemStack output;
  // TODO: should proably not be nullable
  @Nullable
  protected final IMaterialItem materialItem;

  public PartRecipe(ResourceLocation id, String group, ResourceLocation pattern, int cost, ItemStack output) {
    this.id = id;
    this.group = group;
    this.pattern = pattern;
    this.cost = cost;
    this.output = output;
    this.materialItem = (IMaterialItem)Optional.of(output.getItem())
                                               .filter(item -> item instanceof IMaterialItem)
                                               .orElse(null);
  }

  @Override
  public IRecipeType<?> getType() {
    return RecipeTypes.PART_BUILDER;
  }

  @Override
  public ItemStack getIcon() {
    return new ItemStack(TinkerTables.partBuilder);
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return TinkerTables.partRecipeSerializer.get();
  }

  /**
   * Checks if the recipe supports the given pattern, used to filter out the button list
   * @param inv  Inventory instance
   * @return  True if the recipe matches the given pattern
   */
  public boolean matchesPattern(IPartBuilderInventory inv) {
    return inv.getPatternStack().getItem() == TinkerTables.pattern.get();
  }

  /**
   * Checks if the recipe is valid for the given input. Assumes {@link #matchesPattern(IPartBuilderInventory)} is true
   * @param inv    Inventory instance
   * @param world  World instance
   * @return  True if this recipe matches
   */
  @Override
  public boolean matches(IPartBuilderInventory inv, World world) {
    // TODO: fix this in the JSON parser
    if (materialItem == null) {
      return true;
    }

    // must have a material
    MaterialRecipe materialRecipe = inv.getMaterial();
    if (materialRecipe != null) {
      // material must be craftable, usable in the item, and have a cost we can afford
      IMaterial material = materialRecipe.getMaterial();
      return material.isCraftable() && materialItem.canUseMaterial(material)
             && inv.getStack().getCount() >= materialRecipe.getItemsUsed(cost);
    }
    return false;
  }

  /**
   * Gets the number of material items consumed by this recipe
   * @param inv  Crafting inventory
   * @return  Number of items consumed
   */
  public int getItemsUsed(IPartBuilderInventory inv) {
    return Optional.ofNullable(inv.getMaterial())
                   .map(mat -> mat.getItemsUsed(cost))
                   .orElse(1);
  }

  @Override
  public ItemStack getRecipeOutput() {
    return this.output;
  }

  @Override
  public ItemStack getCraftingResult(IPartBuilderInventory inv) {
    if (materialItem != null) {
      MaterialRecipe materialRecipe = inv.getMaterial();
      if (materialRecipe != null) {
        return materialItem.getItemstackWithMaterial(materialRecipe.getMaterial());
      }
    }
    return this.output.copy();
  }

  /* Required methods */
  @Override
  public boolean canFit(int width, int height) {
    return true;
  }
}
