package slimeknights.tconstruct.tools.recipe.severing;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.recipe.EntityIngredient;
import slimeknights.mantle.recipe.ItemOutput;
import slimeknights.tconstruct.library.recipe.modifiers.SeveringRecipe;
import slimeknights.tconstruct.tools.TinkerModifiers;

/** Beheading recipe to drop pumpkins only if equipped */
public class SnowGolemBeheadingRecipe extends SeveringRecipe {
  public SnowGolemBeheadingRecipe(ResourceLocation id) {
    super(id, EntityIngredient.of(EntityType.SNOW_GOLEM), ItemOutput.fromItem(Items.CARVED_PUMPKIN));
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return TinkerModifiers.snowGolemBeheadingSerializer.get();
  }

  @Override
  public ItemStack getOutput(Entity entity) {
    if (entity instanceof SnowGolemEntity && !((SnowGolemEntity)entity).isPumpkinEquipped()) {
      return new ItemStack(Blocks.SNOW_BLOCK);
    }
    return getOutput().copy();
  }
}
