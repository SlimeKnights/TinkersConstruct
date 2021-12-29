package slimeknights.tconstruct.tables.tileentity.table.crafting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.recipe.partbuilder.IPartBuilderInventory;
import slimeknights.tconstruct.tables.tileentity.table.PartBuilderTileEntity;

import javax.annotation.Nullable;
import java.util.Objects;

public class PartBuilderInventoryWrapper implements IPartBuilderInventory {
  private final PartBuilderTileEntity builder;
  /** If true, the material recipe is out of date*/
  private boolean materialNeedsUpdate = true;
  /** Cached material recipe, may be null if not a material item */
  @Nullable
  private MaterialRecipe material = null;

  public PartBuilderInventoryWrapper(PartBuilderTileEntity builder) {
    this.builder = builder;
  }

  @Override
  public ItemStack getStack() {
    return builder.getItem(PartBuilderTileEntity.MATERIAL_SLOT);
  }

  @Override
  public ItemStack getPatternStack() {
    return builder.getItem(PartBuilderTileEntity.PATTERN_SLOT);
  }

  /** Gets the tiles world */
  protected Level getWorld() {
    return Objects.requireNonNull(builder.getLevel(), "Tile entity world must be nonnull");
  }

  /** Refreshes the stored material */
  public void refreshMaterial() {
    this.materialNeedsUpdate = true;
    this.material = null;
  }

  @Override
  @Nullable
  public MaterialRecipe getMaterial() {
    if (this.materialNeedsUpdate) {
      this.materialNeedsUpdate = false;
      if (getStack().isEmpty()) {
        this.material = null;
      } else {
        Level world = getWorld();
        this.material = world.getRecipeManager().getRecipeFor(RecipeTypes.MATERIAL, this, world).orElse(null);
      }
    }
    return this.material;
  }
}
