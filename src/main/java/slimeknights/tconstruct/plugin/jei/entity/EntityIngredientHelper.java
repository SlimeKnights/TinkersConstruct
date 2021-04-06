package slimeknights.tconstruct.plugin.jei.entity;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.recipe.IFocus;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

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

  /**
   * Sets the entity focus based on the item list
   * @param layout       Recipe layout
   * @param group        Entity type group
   * @param entities     Entities to focus
   * @param index        Index of entity slot
   */
  public static void setFocus(IRecipeLayout layout, IGuiIngredientGroup<EntityType> group, Collection<EntityType<?>> entities, int index) {
    IFocus<ItemStack> focus = layout.getFocus(VanillaTypes.ITEM);
    if (focus != null && focus.getValue().getItem() instanceof SpawnEggItem) {
      EntityType<?> type = ((SpawnEggItem) focus.getValue().getItem()).getType(null);
      group.set(index, entities.stream().filter(type::equals).collect(Collectors.toList()));
    }
  }
}
