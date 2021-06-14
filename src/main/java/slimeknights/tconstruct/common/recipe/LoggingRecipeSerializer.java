package slimeknights.tconstruct.common.recipe;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.recipe.RecipeSerializer;
import slimeknights.tconstruct.TConstruct;

import javax.annotation.Nullable;

/**
 * Recipe serializer that logs exceptions before throwing them as otherwise the exceptions may be invisible
 * TODO: move to Mantle
 * @param <T>  Recipe class
 */
public abstract class LoggingRecipeSerializer<T extends IRecipe<?>> extends RecipeSerializer<T> {
  /**
   * Read the recipe from the packet
   * @param id      Recipe ID
   * @param buffer  Buffer instance
   * @return  Parsed recipe
   * @throws RuntimeException  If any errors happen, the exception will be logged automatically
   */
  @Nullable
  protected abstract T readSafe(ResourceLocation id, PacketBuffer buffer);

  /**
   * Write the method to the buffer
   * @param buffer  Buffer instance
   * @param recipe  Recipe instance
   * @throws RuntimeException  If any errors happen, the exception will be logged automatically
   */
  protected abstract void writeSafe(PacketBuffer buffer, T recipe);

  @Nullable
  @Override
  public T read(ResourceLocation id, PacketBuffer buffer) {
    try {
      return readSafe(id, buffer);
    } catch (RuntimeException e) {
      TConstruct.log.error("{}: Error writing recipe to packet", this.getClass().getSimpleName(), e);
      throw e;
    }
  }

  @Override
  public void write(PacketBuffer buffer, T recipe) {
    try {
      writeSafe(buffer, recipe);
    } catch (RuntimeException e) {
      TConstruct.log.error("{}: Error reading recipe from packet", this.getClass().getSimpleName(), e);
      throw e;
    }
  }
}
