package slimeknights.tconstruct.library.recipe.modifiers;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import slimeknights.mantle.recipe.EntityIngredient;
import slimeknights.mantle.recipe.ICustomOutputRecipe;
import slimeknights.mantle.recipe.ItemOutput;
import slimeknights.mantle.recipe.RecipeSerializer;
import slimeknights.mantle.recipe.inventory.IEmptyInventory;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

/**
 * Recipe to convert an entity into a head for the beheading modifier
 */
@RequiredArgsConstructor
public class BeheadingRecipe implements ICustomOutputRecipe<IEmptyInventory> {
  @Getter
  private final ResourceLocation id;
  private final EntityIngredient ingredient;
  private final ItemOutput output;

  @SuppressWarnings("rawtypes")
  private List<List<EntityType>> displayInputs;

  /**
   * Checks if the recipe matches the given type
   * @param type  Type
   * @return  True if it matches
   */
  public boolean matches(EntityType<?> type) {
    return ingredient.test(type);
  }

  /**
   * Gets the output for this recipe for display in JEI
   * @return  Display output
   */
  public ItemStack getOutput() {
    return output.get();
  }

  /**
   * Gets the output for this recipe
   * @param entity  Entity being melted
   * @return  Fluid output
   */
  public ItemStack getOutput(Entity entity) {
    return output.get().copy();
  }

  /** Gets a list of inputs for display */
  public Collection<EntityType<?>> getInputs() {
    return ingredient.getTypes();
  }

  /**
   * Gets a list of inputs for display in JEI
   * @return  Entity type inputs
   */
  @SuppressWarnings("rawtypes")
  public List<List<EntityType>> getDisplayInputs() {
    if (displayInputs == null) {
      displayInputs = ImmutableList.of(ImmutableList.copyOf(getInputs()));
    }
    return displayInputs;
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return TinkerModifiers.beheadingSerializer.get();
  }

  @Override
  public IRecipeType<?> getType() {
    return RecipeTypes.BEHEADING;
  }

  /** @deprecated use {@link #matches(EntityType)}*/
  @Deprecated
  @Override
  public boolean matches(IEmptyInventory inv, World worldIn) {
    return false;
  }

  /** Serializer for this recipe */
  public static class Serializer extends RecipeSerializer<BeheadingRecipe> {
    @Override
    public BeheadingRecipe read(ResourceLocation id, JsonObject json) {
      EntityIngredient ingredient = EntityIngredient.deserialize(JsonHelper.getElement(json, "entity"));
      ItemOutput output = ItemOutput.fromJson(JsonHelper.getElement(json, "result"));
      return new BeheadingRecipe(id, ingredient, output);
    }

    @Nullable
    @Override
    public BeheadingRecipe read(ResourceLocation id, PacketBuffer buffer) {
      EntityIngredient ingredient = EntityIngredient.read(buffer);
      ItemOutput output = ItemOutput.read(buffer);
      return new BeheadingRecipe(id, ingredient, output);
    }

    @Override
    public void write(PacketBuffer buffer, BeheadingRecipe recipe) {
      recipe.ingredient.write(buffer);
      recipe.output.write(buffer);
    }
  }
}
