package slimeknights.tconstruct.library.recipe.modifiers.severing;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.helper.LoggingRecipeSerializer;
import slimeknights.mantle.recipe.ingredient.EntityIngredient;
import slimeknights.mantle.util.JsonHelper;

import javax.annotation.Nullable;

public class AgeableSeveringRecipe extends SeveringRecipe {
  private final ItemOutput childOutput;
  public AgeableSeveringRecipe(ResourceLocation id, EntityIngredient ingredient, ItemOutput adultOutput, ItemOutput childOutput) {
    super(id, ingredient, adultOutput);
    this.childOutput = childOutput;
  }

  @Override
  public ItemStack getOutput(Entity entity) {
    if (entity instanceof LivingEntity && ((LivingEntity) entity).isBaby()) {
      return childOutput.get().copy();
    }
    return getOutput().copy();
  }

  /** Serializer for this recipe */
  public static class Serializer implements LoggingRecipeSerializer<AgeableSeveringRecipe> {
    @Override
    public AgeableSeveringRecipe fromJson(ResourceLocation id, JsonObject json) {
      EntityIngredient ingredient = EntityIngredient.deserialize(JsonHelper.getElement(json, "entity"));
      ItemOutput adult = ItemOutput.Loadable.REQUIRED_STACK.getIfPresent(json, "adult_result");
      ItemOutput child = ItemOutput.Loadable.OPTIONAL_STACK.getOrEmpty(json, "child_result");
      return new AgeableSeveringRecipe(id, ingredient, adult, child);
    }

    @Nullable
    @Override
    public AgeableSeveringRecipe fromNetworkSafe(ResourceLocation id, FriendlyByteBuf buffer) {
      EntityIngredient ingredient = EntityIngredient.read(buffer);
      ItemOutput adult = ItemOutput.read(buffer);
      ItemOutput child = ItemOutput.read(buffer);
      return new AgeableSeveringRecipe(id, ingredient, adult, child);
    }

    @Override
    public void toNetworkSafe(FriendlyByteBuf buffer, AgeableSeveringRecipe recipe) {
      recipe.ingredient.write(buffer);
      recipe.output.write(buffer);
      recipe.childOutput.write(buffer);
    }
  }
}
