package slimeknights.tconstruct.library.recipe.modifiers.severing;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeSpawnEggItem;
import slimeknights.mantle.recipe.ICustomOutputRecipe;
import slimeknights.mantle.recipe.container.IEmptyContainer;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.helper.LoggingRecipeSerializer;
import slimeknights.mantle.recipe.ingredient.EntityIngredient;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

/**
 * Recipe to convert an entity into a head or other item for the severing modifier
 */
@RequiredArgsConstructor
public class SeveringRecipe implements ICustomOutputRecipe<IEmptyContainer> {
  @Getter
  private final ResourceLocation id;
  protected final EntityIngredient ingredient;
  protected final ItemOutput output;

  @SuppressWarnings("rawtypes")
  private List<EntityType> entityInputs;
  private List<ItemStack> itemInputs;

  /**
   * Checks if the recipe matches the given type
   * @param type  Type
   * @return  True if it matches
   */
  public boolean matches(EntityType<?> type) {
    return ingredient.test(type);
  }

  /**
   * Gets the output for this recipe for display in JEI, needs to be consistent
   * @return  Display output
   */
  public ItemStack getOutput() {
    return output.get();
  }

  /**
   * Gets the output for this recipe, does not need to be consistent (can use randomness) and may be empty
   * @param entity  Entity being melted
   * @return  Item output
   */
  public ItemStack getOutput(Entity entity) {
    return getOutput().copy();
  }

  /**
   * Gets a list of inputs for display in JEI
   * @return  Entity type inputs
   */
  @SuppressWarnings("rawtypes")
  public List<EntityType> getEntityInputs() {
    if (entityInputs == null) {
      entityInputs = ImmutableList.copyOf(ingredient.getTypes());
    }
    return entityInputs;
  }

  /**
   * Gets a list of item inputs for recipe lookup in JEI
   * @return  Item inputs
   */
  public List<ItemStack> getItemInputs() {
    if (itemInputs == null) {
      itemInputs = getEntityInputs().stream()
                                    .map(ForgeSpawnEggItem::fromEntityType)
                                    .filter(Objects::nonNull)
                                    .map(ItemStack::new)
                                    .toList();
    }
    return itemInputs;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerModifiers.severingSerializer.get();
  }

  @Override
  public RecipeType<?> getType() {
    return RecipeTypes.SEVERING;
  }

  /** @deprecated use {@link #matches(EntityType)}*/
  @Deprecated
  @Override
  public boolean matches(IEmptyContainer inv, Level worldIn) {
    return false;
  }

  /** Serializer for this recipe */
  public static class Serializer extends LoggingRecipeSerializer<SeveringRecipe> {
    @Override
    public SeveringRecipe fromJson(ResourceLocation id, JsonObject json) {
      EntityIngredient ingredient = EntityIngredient.deserialize(JsonHelper.getElement(json, "entity"));
      ItemOutput output = ItemOutput.fromJson(JsonHelper.getElement(json, "result"));
      return new SeveringRecipe(id, ingredient, output);
    }

    @Nullable
    @Override
    protected SeveringRecipe fromNetworkSafe(ResourceLocation id, FriendlyByteBuf buffer) {
      EntityIngredient ingredient = EntityIngredient.read(buffer);
      ItemOutput output = ItemOutput.read(buffer);
      return new SeveringRecipe(id, ingredient, output);
    }

    @Override
    protected void toNetworkSafe(FriendlyByteBuf buffer, SeveringRecipe recipe) {
      recipe.ingredient.write(buffer);
      recipe.output.write(buffer);
    }
  }
}
