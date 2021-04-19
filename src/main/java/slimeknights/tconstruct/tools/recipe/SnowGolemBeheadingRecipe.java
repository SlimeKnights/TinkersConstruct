package slimeknights.tconstruct.tools.recipe;

import slimeknights.mantle.recipe.EntityIngredient;
import slimeknights.mantle.recipe.ItemOutput;
import slimeknights.tconstruct.library.recipe.modifiers.BeheadingRecipe;
import slimeknights.tconstruct.tools.TinkerModifiers;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;

/** Beheading recipe to drop pumpkins only if equipped */
public class SnowGolemBeheadingRecipe extends BeheadingRecipe {
  public SnowGolemBeheadingRecipe(Identifier id) {
    super(id, EntityIngredient.of(EntityType.SNOW_GOLEM), ItemOutput.fromItem(Items.CARVED_PUMPKIN));
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerModifiers.snowGolemBeheadingSerializer;
  }

  @Override
  public ItemStack getOutput(Entity entity) {
    if (entity instanceof SnowGolemEntity && !((SnowGolemEntity)entity).hasPumpkin()) {
      return new ItemStack(Blocks.SNOW_BLOCK);
    }
    return getOutput().copy();
  }
}
