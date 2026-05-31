package slimeknights.tconstruct.plugin.jei.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.common.util.Lazy;
import slimeknights.mantle.compat.neoforged.neoforge.registries.ForgeRegistries;
import slimeknights.mantle.recipe.helper.FluidOutput;
import slimeknights.mantle.recipe.ingredient.EntityIngredient;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipe;
import slimeknights.tconstruct.smeltery.block.entity.module.EntityMeltingModule;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Extension of entity melting recipe for the sake of displaying entities in the default "recipe"
 */
public class DefaultEntityMeltingRecipe extends EntityMeltingRecipe {
  /**
   * Gets a list of entity types, filtered by the recipe list
   * @param recipes  Recipe list
   * @return List of entity types
   */
  private static EntityIngredient getEntityList(List<EntityMeltingRecipe> recipes) {
    Set<EntityType<?>> unusedTypes = new LinkedHashSet<>();
    typeLoop:
    for (EntityType<?> type : ForgeRegistries.ENTITY_TYPES) {
      // use tag overrides for default recipe
      if (type.is(TinkerTags.EntityTypes.MELTING_HIDE)) continue;
      if (type.getCategory() == MobCategory.MISC && !type.is(TinkerTags.EntityTypes.MELTING_SHOW)) continue;
      for (EntityMeltingRecipe recipe : recipes) {
        if (recipe.matches(type)) {
          continue typeLoop;
        }
      }
      unusedTypes.add(type);
    }
    return EntityIngredient.of(unusedTypes);
  }

  private final Lazy<EntityIngredient> entities;
  public DefaultEntityMeltingRecipe(List<EntityMeltingRecipe> recipes) {
    super(TConstruct.getResource("__default"), EntityIngredient.EMPTY, FluidOutput.fromStack(EntityMeltingModule.getDefaultFluid()), 2);
    entities = Lazy.of(() -> getEntityList(recipes));
  }

  @Override
  public EntityIngredient getIngredient() {
    return entities.get();
  }
}
