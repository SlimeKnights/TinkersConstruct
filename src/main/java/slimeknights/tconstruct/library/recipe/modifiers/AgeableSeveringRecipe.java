package slimeknights.tconstruct.library.recipe.modifiers;

import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.recipe.EntityIngredient;
import slimeknights.mantle.recipe.ItemOutput;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.common.recipe.LoggingRecipeSerializer;

import javax.annotation.Nullable;

public class AgeableSeveringRecipe extends SeveringRecipe {
  @Nullable
  private final ItemOutput childOutput;
  public AgeableSeveringRecipe(ResourceLocation id, EntityIngredient ingredient, ItemOutput adultOutput, @Nullable ItemOutput childOutput) {
    super(id, ingredient, adultOutput);
    this.childOutput = childOutput;
  }

  @Override
  public ItemStack getOutput(Entity entity) {
    if (entity instanceof LivingEntity && ((LivingEntity) entity).isChild()) {
      return childOutput == null ? ItemStack.EMPTY : childOutput.get().copy();
    }
    return getOutput().copy();
  }

  /** Serializer for this recipe */
  public static class Serializer extends LoggingRecipeSerializer<AgeableSeveringRecipe> {
    @Override
    public AgeableSeveringRecipe read(ResourceLocation id, JsonObject json) {
      EntityIngredient ingredient = EntityIngredient.deserialize(JsonHelper.getElement(json, "entity"));
      ItemOutput adult = ItemOutput.fromJson(JsonHelper.getElement(json, "adult_result"));
      ItemOutput child = null;
      if (json.has("child_result")) {
        child = ItemOutput.fromJson(JsonHelper.getElement(json, "child_result"));
      }
      return new AgeableSeveringRecipe(id, ingredient, adult, child);
    }

    @Nullable
    @Override
    protected AgeableSeveringRecipe readSafe(ResourceLocation id, PacketBuffer buffer) {
      EntityIngredient ingredient = EntityIngredient.read(buffer);
      ItemOutput adult = ItemOutput.read(buffer);
      ItemOutput child = null;
      if (buffer.readBoolean()) {
        child = ItemOutput.read(buffer);
      }
      return new AgeableSeveringRecipe(id, ingredient, adult, child);
    }

    @Override
    protected void writeSafe(PacketBuffer buffer, AgeableSeveringRecipe recipe) {
      recipe.ingredient.write(buffer);
      recipe.output.write(buffer);
      if (recipe.childOutput == null) {
        buffer.writeBoolean(false);
      } else {
        buffer.writeBoolean(true);
        recipe.childOutput.write(buffer);
      }
    }
  }
}
