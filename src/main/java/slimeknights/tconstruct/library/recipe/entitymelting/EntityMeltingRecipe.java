package slimeknights.tconstruct.library.recipe.entitymelting;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.data.loadable.common.FluidStackLoadable;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.ICustomOutputRecipe;
import slimeknights.mantle.recipe.container.IEmptyContainer;
import slimeknights.mantle.recipe.ingredient.EntityIngredient;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Recipe to melt an entity into a fluid
 */
@RequiredArgsConstructor
public class EntityMeltingRecipe implements ICustomOutputRecipe<IEmptyContainer> {
  public static final RecordLoadable<EntityMeltingRecipe> LOADER = RecordLoadable.create(
    ContextKey.ID.requiredField(),
    EntityIngredient.LOADABLE.requiredField("entity", r -> r.ingredient),
    FluidStackLoadable.REQUIRED_STACK.requiredField("result", r -> r.output),
    IntLoadable.FROM_ONE.defaultField("damage", 2, true, r -> r.damage),
    EntityMeltingRecipe::new);

  @Getter
  private final ResourceLocation id;
  private final EntityIngredient ingredient;
  @Getter
  private final FluidStack output;
  @Getter
  private final int damage;

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

  /**
   * Gets a collection of inputs for filtering in JEI
   * @return  Collection of types
   */
  public Collection<EntityType<?>> getInputs() {
    return ingredient.getTypes();
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerSmeltery.entityMeltingSerializer.get();
  }

  @Override
  public RecipeType<?> getType() {
    return TinkerRecipeTypes.ENTITY_MELTING.get();
  }

  /** @deprecated use {@link #matches(EntityType)}*/
  @Deprecated
  @Override
  public boolean matches(IEmptyContainer inv, Level worldIn) {
    return false;
  }
}
