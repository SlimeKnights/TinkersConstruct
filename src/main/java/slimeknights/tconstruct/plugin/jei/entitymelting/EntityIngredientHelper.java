package slimeknights.tconstruct.plugin.jei.entitymelting;

import mezz.jei.api.ingredients.IIngredientHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Objects;

/** Handler for working with entity types as ingredients */
@SuppressWarnings("rawtypes")
public class EntityIngredientHelper implements IIngredientHelper<EntityType> {

  @Nullable
  @Override
  public EntityType getMatch(Iterable<EntityType> iterable, EntityType type) {
    for (EntityType<?> match : iterable) {
      if (match == type) {
        return match;
      }
    }
    return null;
  }

  @Override
  public String getDisplayName(EntityType type) {
    return type.getName().getString();
  }

  @Override
  public String getUniqueId(EntityType type) {
    return Objects.requireNonNull(type.getRegistryName()).toString();
  }

  @Override
  public String getWildcardId(EntityType type) {
    return getUniqueId(type);
  }

  @Override
  public String getModId(EntityType type) {
    return Objects.requireNonNull(type.getRegistryName()).getNamespace();
  }

  @Override
  public String getResourceId(EntityType type) {
    return Objects.requireNonNull(type.getRegistryName()).getPath();
  }

  @Override
  public EntityType copyIngredient(EntityType type) {
    return type;
  }

  @Override
  public String getErrorInfo(@Nullable EntityType type) {
    if (type == null) {
      return "null";
    }
    ResourceLocation name = type.getRegistryName();
    if (name == null) {
      return "unnamed sadface :(";
    }
    return name.toString();
  }
}
