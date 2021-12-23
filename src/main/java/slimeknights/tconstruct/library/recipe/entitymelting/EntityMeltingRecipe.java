package slimeknights.tconstruct.library.recipe.entitymelting;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.EntityIngredient;
import slimeknights.mantle.recipe.ICustomOutputRecipe;
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.mantle.recipe.inventory.IEmptyInventory;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.common.recipe.LoggingRecipeSerializer;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

/**
 * Recipe to melt an entity into a fluid
 */
@RequiredArgsConstructor
public class EntityMeltingRecipe implements ICustomOutputRecipe<IEmptyInventory> {
  @Getter
  private final ResourceLocation id;
  private final EntityIngredient ingredient;
  @Getter
  private final FluidStack output;
  @Getter
  private final int damage;

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
   * Gets the output for this recipe
   * @param entity  Entity being melted
   * @return  Fluid output
   */
  public FluidStack getOutput(LivingEntity entity) {
    return output.copy();
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

  /**
   * Gets a collection of inputs for filtering in JEI
   * @return  Collection of types
   */
  public Collection<EntityType<?>> getInputs() {
    return ingredient.getTypes();
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return TinkerSmeltery.entityMeltingSerializer.get();
  }

  @Override
  public IRecipeType<?> getType() {
    return RecipeTypes.ENTITY_MELTING;
  }

  /** @deprecated use {@link #matches(EntityType)}*/
  @Deprecated
  @Override
  public boolean matches(IEmptyInventory inv, World worldIn) {
    return false;
  }

  /** Serializer for this recipe */
  public static class Serializer extends LoggingRecipeSerializer<EntityMeltingRecipe> {
    @Override
    public EntityMeltingRecipe read(ResourceLocation id, JsonObject json) {
      EntityIngredient ingredient = EntityIngredient.deserialize(JsonHelper.getElement(json, "entity"));
      FluidStack output = RecipeHelper.deserializeFluidStack(JSONUtils.getJsonObject(json, "result"));
      int damage = JSONUtils.getInt(json, "damage", 2);
      return new EntityMeltingRecipe(id, ingredient, output, damage);
    }

    @Nullable
    @Override
    protected EntityMeltingRecipe readSafe(ResourceLocation id, PacketBuffer buffer) {
      EntityIngredient ingredient = EntityIngredient.read(buffer);
      FluidStack output = buffer.readFluidStack();
      int damage = buffer.readVarInt();
      return new EntityMeltingRecipe(id, ingredient, output, damage);
    }

    @Override
    protected void writeSafe(PacketBuffer buffer, EntityMeltingRecipe recipe) {
      recipe.ingredient.write(buffer);
      buffer.writeFluidStack(recipe.output);
      buffer.writeVarInt(recipe.damage);
    }
  }
}
