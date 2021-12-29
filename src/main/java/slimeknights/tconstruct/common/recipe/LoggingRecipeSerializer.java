package slimeknights.tconstruct.common.recipe;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistryEntry;
import slimeknights.tconstruct.TConstruct;

import javax.annotation.Nullable;

/**
 * Recipe serializer that logs exceptions before throwing them as otherwise the exceptions may be invisible
 * TODO: move to Mantle
 * @param <T>  Recipe class
 */
public abstract class LoggingRecipeSerializer<T extends Recipe<?>> extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<T> {
  /**
   * Read the recipe from the packet
   * @param id      Recipe ID
   * @param buffer  Buffer instance
   * @return  Parsed recipe
   * @throws RuntimeException  If any errors happen, the exception will be logged automatically
   */
  @Nullable
  protected abstract T readSafe(ResourceLocation id, FriendlyByteBuf buffer);

  /**
   * Write the method to the buffer
   * @param buffer  Buffer instance
   * @param recipe  Recipe instance
   * @throws RuntimeException  If any errors happen, the exception will be logged automatically
   */
  protected abstract void writeSafe(FriendlyByteBuf buffer, T recipe);

  @Nullable
  @Override
  public T fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
    try {
      return readSafe(id, buffer);
    } catch (RuntimeException e) {
      TConstruct.LOG.error("{}: Error writing recipe to packet", this.getClass().getSimpleName(), e);
      throw e;
    }
  }

  @Override
  public void toNetwork(FriendlyByteBuf buffer, T recipe) {
    try {
      writeSafe(buffer, recipe);
    } catch (RuntimeException e) {
      TConstruct.LOG.error("{}: Error reading recipe from packet", this.getClass().getSimpleName(), e);
      throw e;
    }
  }
}
