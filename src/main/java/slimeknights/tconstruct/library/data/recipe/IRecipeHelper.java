package slimeknights.tconstruct.library.data.recipe;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Item;
import net.minecraft.tags.Tag.Named;
import net.minecraft.tags.ItemTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition;
import net.minecraftforge.registries.IForgeRegistryEntry;
import slimeknights.mantle.recipe.data.ConsumerWrapperBuilder;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Interface for common resource location and condition methods
 */
public interface IRecipeHelper {
  /* Location helpers */

  /** Gets the ID of the mod adding recipes */
  String getModId();

  /**
   * Gets a resource location for the mod
   * @param name  Location path
   * @return  Location for the mod
   */
  default ResourceLocation modResource(String name) {
    return new ResourceLocation(getModId(), name);
  }

  /**
   * Gets a resource location string for Tinkers
   * @param id  Location path
   * @return  Location for Tinkers
   */
  default String modPrefix(String id) {
    return getModId() + ":" + id;
  }

  /**
   * Prefixes the resource location path with the given value
   * @param entry    Item registry name to use
   * @param prefix  Prefix value
   * @return  Resource location path
   */
  default ResourceLocation wrap(IForgeRegistryEntry<?> entry, String prefix, String suffix) {
    ResourceLocation loc = Objects.requireNonNull(entry.getRegistryName());
    return modResource(prefix + loc.getPath() + suffix);
  }

  /**
   * Prefixes the resource location path with the given value
   * @param entry    Item registry name to use
   * @param prefix  Prefix value
   * @return  Resource location path
   */
  default ResourceLocation wrap(Supplier<? extends IForgeRegistryEntry<?>> entry, String prefix, String suffix) {
    return wrap(entry.get(), prefix, suffix);
  }

  /**
   * Prefixes the resource location path with the given value
   * @param entry   Entry registry name to use
   * @param prefix  Prefix value
   * @return  Resource location path
   */
  default ResourceLocation prefix(IForgeRegistryEntry<?> entry, String prefix) {
    ResourceLocation loc = Objects.requireNonNull(entry.getRegistryName());
    return modResource(prefix + loc.getPath());
  }

  /**
   * Prefixes the resource location path with the given value
   * @param entry   Entry registry name to use
   * @param prefix  Prefix value
   * @return  Resource location path
   */
  default ResourceLocation prefix(Supplier<? extends IForgeRegistryEntry<?>> entry, String prefix) {
    return prefix(entry.get(), prefix);
  }


  /* Tags and conditions */

  /**
   * Gets a tag by name
   * @param modId  Mod ID for tag
   * @param name   Tag name
   * @return  Tag instance
   */
  default Named<Item> getTag(String modId, String name) {
    return ItemTags.bind(modId + ":" + name);
  }

  /**
   * Creates a condition for a tag existing
   * @param name  Forge tag name
   * @return  Condition for tag existing
   */
  default ICondition tagCondition(String name) {
    return new NotCondition(new TagEmptyCondition("forge", name));
  }

  /**
   * Creates a consumer instance with the added conditions
   * @param consumer    Base consumer
   * @param conditions  Extra conditions
   * @return  Wrapped consumer
   */
  default Consumer<FinishedRecipe> withCondition(Consumer<FinishedRecipe> consumer, ICondition... conditions) {
    ConsumerWrapperBuilder builder = ConsumerWrapperBuilder.wrap();
    for (ICondition condition : conditions) {
      builder.addCondition(condition);
    }
    return builder.build(consumer);
  }
}
