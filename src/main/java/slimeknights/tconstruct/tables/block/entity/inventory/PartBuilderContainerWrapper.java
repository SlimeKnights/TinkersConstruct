package slimeknights.tconstruct.tables.block.entity.inventory;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialCastingLookup;
import slimeknights.tconstruct.library.recipe.material.IMaterialValue;
import slimeknights.tconstruct.library.recipe.material.MaterialValue;
import slimeknights.tconstruct.library.recipe.partbuilder.IPartBuilderContainer;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;
import slimeknights.tconstruct.tables.block.entity.table.PartBuilderBlockEntity;

import javax.annotation.Nullable;
import java.util.Objects;

public class PartBuilderContainerWrapper implements IPartBuilderContainer {
  private final PartBuilderBlockEntity builder;
  /** If true, the material recipe is out of date*/
  private boolean materialNeedsUpdate = true;
  /** Cached material recipe, may be null if not a material item */
  @Nullable
  private IMaterialValue material = null;

  public PartBuilderContainerWrapper(PartBuilderBlockEntity builder) {
    this.builder = builder;
  }

  @Override
  public ItemStack getStack() {
    return builder.getItem(PartBuilderBlockEntity.MATERIAL_SLOT);
  }

  @Override
  public ItemStack getPatternStack() {
    return builder.getItem(PartBuilderBlockEntity.PATTERN_SLOT);
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
  public IMaterialValue getMaterial() {
    if (this.materialNeedsUpdate) {
      this.materialNeedsUpdate = false;
      ItemStack stack = getStack();
      if (stack.isEmpty()) {
        this.material = null;
      } else if (stack.is(TinkerTags.Items.TOOL_PARTS)) {
        MaterialVariantId material = IMaterialItem.getMaterialFromStack(stack);
        int cost = MaterialCastingLookup.getItemCost(stack.getItem());
        if (cost == 0 || IMaterial.UNKNOWN_ID.matchesVariant(material)) {
          this.material = null;
        } else {
          this.material = new MaterialValue(material, cost);
        }
      } else {
        Level world = getWorld();
        this.material = world.getRecipeManager().getRecipeFor(TinkerRecipeTypes.MATERIAL.get(), this, world).orElse(null);
      }
    }
    return this.material;
  }
}
