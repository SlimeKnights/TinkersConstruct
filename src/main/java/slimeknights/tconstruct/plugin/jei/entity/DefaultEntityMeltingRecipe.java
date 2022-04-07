package slimeknights.tconstruct.plugin.jei.entity;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.recipe.ingredient.EntityIngredient;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipe;
import slimeknights.tconstruct.smeltery.block.entity.module.EntityMeltingModule;

import java.util.ArrayList;
import java.util.List;

/**
 * Extension of entity melting recipe for the sake of displaying entities in the default "recipe"
 */
@SuppressWarnings("rawtypes")
public class DefaultEntityMeltingRecipe extends EntityMeltingRecipe {
  /**
   * Gets a list of entity types, filtered by the recipe list
   * @param recipes  Recipe list
   * @return List of entity types
   */
  private static List<EntityType> getEntityList(List<EntityMeltingRecipe> recipes) {
    List<EntityType> unusedTypes = new ArrayList<>();
    typeLoop:
    for (EntityType<?> type : ForgeRegistries.ENTITIES) {
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
    return ImmutableList.copyOf(unusedTypes);
  }

  private final Lazy<List<EntityType>> entityList;
  public DefaultEntityMeltingRecipe(List<EntityMeltingRecipe> recipes) {
    super(TConstruct.getResource("__default"), EntityIngredient.EMPTY, EntityMeltingModule.getDefaultFluid(), 2);
    entityList = Lazy.of(() -> getEntityList(recipes));
  }

  @Override
  public List<EntityType> getEntityInputs() {
    return entityList.get();
  }
}
