package slimeknights.tconstruct.tools.recipe.severing;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.MushroomCow.MushroomType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.ingredient.EntityIngredient;
import slimeknights.tconstruct.library.recipe.modifiers.severing.SeveringRecipe;

/**
 * Recipe to deshroom a mooshroom, taking brown into account
 */
public class MooshroomDemushroomingRecipe extends SeveringRecipe {
  public MooshroomDemushroomingRecipe(ResourceLocation id) {
    super(id, EntityIngredient.of(EntityType.MOOSHROOM), ItemOutput.fromStack(new ItemStack(Items.RED_MUSHROOM, 5)));
  }

  @Override
  public ItemStack getOutput(Entity entity) {
    if (entity instanceof MushroomCow mooshroom) {
      if (!mooshroom.isBaby()) {
        return new ItemStack(mooshroom.getMushroomType() == MushroomType.BROWN ? Items.BROWN_MUSHROOM : Items.RED_MUSHROOM, 5);
      }
    }
    return ItemStack.EMPTY;
  }
}
