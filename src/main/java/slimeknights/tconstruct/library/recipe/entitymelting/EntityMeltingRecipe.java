package slimeknights.tconstruct.library.recipe.entitymelting;

import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.recipe.EntityIngredient;
import slimeknights.mantle.recipe.ICustomOutputRecipe;
import slimeknights.mantle.recipe.inventory.IEmptyInventory;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Recipe to melt an entity into a fluid
 */
public class EntityMeltingRecipe implements ICustomOutputRecipe<IEmptyInventory> {
  private final Identifier id;
  private final EntityIngredient ingredient;
  private final FluidVolume output;
  private final int damage;

  @SuppressWarnings("rawtypes")
  private List<List<EntityType>> displayInputs;

  public EntityMeltingRecipe(Identifier id, EntityIngredient ingredient, FluidVolume output, int damage) {
    this.id = id;
    this.ingredient = ingredient;
    this.output = output;
    this.damage = damage;
  }

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
  public FluidVolume getOutput(LivingEntity entity) {
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
  public net.minecraft.recipe.RecipeSerializer<?> getSerializer() {
    return TinkerSmeltery.entityMeltingSerializer;
  }

  @Override
  public RecipeType<?> getType() {
    return RecipeTypes.ENTITY_MELTING;
  }

  /** @deprecated use {@link #matches(EntityType)}*/
  @Deprecated
  @Override
  public boolean matches(IEmptyInventory inv, World worldIn) {
    return false;
  }

  public Identifier getId() {
    return this.id;
  }

  public int getDamage() {
    return this.damage;
  }

  /** Serializer for this recipe */
  public static class Serializer implements RecipeSerializer<EntityMeltingRecipe> {
    @Override
    public EntityMeltingRecipe read(Identifier id, JsonObject json) {
      EntityIngredient ingredient = EntityIngredient.deserialize(JsonHelper.getElement(json, "entity"));
      FluidVolume output = FluidVolume.fromJson(net.minecraft.util.JsonHelper.getObject(json, "result"));
      int damage = net.minecraft.util.JsonHelper.getInt(json, "damage", 2);
      return new EntityMeltingRecipe(id, ingredient, output, damage);
    }

    @Nullable
    @Override
    public EntityMeltingRecipe read(Identifier id, PacketByteBuf buffer) {
      try {
        EntityIngredient ingredient = EntityIngredient.read(buffer);
        FluidVolume output = FluidVolume.fromMcBuffer(buffer);
        int damage = buffer.readVarInt();
        return new EntityMeltingRecipe(id, ingredient, output, damage);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public void write(PacketByteBuf buffer, EntityMeltingRecipe recipe) {
      recipe.ingredient.write(buffer);
      recipe.output.toMcBuffer(buffer);
      buffer.writeVarInt(recipe.damage);
    }
  }
}
