package slimeknights.tconstruct.tables.inventory.table.partbuilder;

import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.recipe.partbuilder.IPartBuilderInventory;
import slimeknights.tconstruct.tables.tileentity.table.PartBuilderTileEntity;

import javax.annotation.Nullable;

public class PartBuilderInventoryWrapper implements IPartBuilderInventory {
  private final PartBuilderTileEntity builder;
  private final World world;
  @Nullable
  @Getter
  private MaterialRecipe material;

  public PartBuilderInventoryWrapper(PartBuilderTileEntity builder) {
    this.builder = builder;
    this.world = builder.getWorld();
  }

  @Override
  public ItemStack getStack() {
    return builder.getStack(PartBuilderTileEntity.MATERIAL_SLOT);
  }

  @Override
  public ItemStack getPatternStack() {
    return builder.getStack(PartBuilderTileEntity.PATTERN_SLOT);
  }

  /**
   * Updates the material based on the current material slot contents
   */
  public void updateMaterial() {
    this.material = world.getRecipeManager().getFirstMatch(RecipeTypes.MATERIAL, this, world).orElse(null);
  }
}
