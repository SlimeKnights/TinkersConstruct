package slimeknights.tconstruct.tools.recipe.severing;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.MooshroomEntity.Type;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.recipe.EntityIngredient;
import slimeknights.mantle.recipe.ItemOutput;
import slimeknights.tconstruct.library.recipe.modifiers.SeveringRecipe;

/**
 * Recipe to deshroom a mooshroom, taking brown into account
 */
public class MooshroomDemushroomingRecipe extends SeveringRecipe {
  public MooshroomDemushroomingRecipe(ResourceLocation id) {
    super(id, EntityIngredient.of(EntityType.MOOSHROOM), ItemOutput.fromStack(new ItemStack(Items.RED_MUSHROOM, 5)));
  }

  @Override
  public ItemStack getOutput(Entity entity) {
    if (entity instanceof MooshroomEntity) {
      MooshroomEntity mooshroom = ((MooshroomEntity) entity);
      if (!mooshroom.isChild()) {
        return new ItemStack(mooshroom.getMooshroomType() == Type.BROWN ? Items.BROWN_MUSHROOM : Items.RED_MUSHROOM, 5);
      }
    }
    return ItemStack.EMPTY;
  }
}
