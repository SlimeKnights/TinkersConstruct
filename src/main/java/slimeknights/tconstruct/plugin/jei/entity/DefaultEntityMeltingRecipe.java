package slimeknights.tconstruct.plugin.jei.entity;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.recipe.EntityIngredient;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipe;
import slimeknights.tconstruct.smeltery.tileentity.module.EntityMeltingModule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Extension of entity melting recipe for the sake of displaying entities in the default "recipe"
 */
public class DefaultEntityMeltingRecipe extends EntityMeltingRecipe {
  /**
   * Gets a list of entity types, filtered by the recipe list
   * @param recipes  Recipe list
   * @return List of entity types
   */
  private static List<EntityType<?>> getEntityList(List<EntityMeltingRecipe> recipes) {
    List<EntityType<?>> unusedTypes = new ArrayList<>();
    typeLoop:
    for (EntityType<?> type : ForgeRegistries.ENTITIES) {
      // use tag overrides for default recipe
      if (TinkerTags.EntityTypes.MELTING_HIDE.contains(type)) continue;
      if (type.getClassification() == EntityClassification.MISC && !TinkerTags.EntityTypes.MELTING_SHOW.contains(type)) continue;
      for (EntityMeltingRecipe recipe : recipes) {
        if (recipe.matches(type)) {
          continue typeLoop;
        }
      }
      unusedTypes.add(type);
    }
    return ImmutableList.copyOf(unusedTypes);
  }

  private final Lazy<List<EntityType<?>>> entityList;
  public DefaultEntityMeltingRecipe(List<EntityMeltingRecipe> recipes) {
    super(Util.getResource("__default"), EntityIngredient.EMPTY, EntityMeltingModule.getDefaultFluid(), 2);
    entityList = Lazy.of(() -> getEntityList(recipes));
  }

  @Override
  public Collection<EntityType<?>> getInputs() {
    return entityList.get();
  }
}
