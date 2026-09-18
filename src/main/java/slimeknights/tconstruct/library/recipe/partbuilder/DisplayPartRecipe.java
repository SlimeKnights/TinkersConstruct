package slimeknights.tconstruct.library.recipe.partbuilder;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

import javax.annotation.Nullable;
import java.util.List;

/** Part builder recipe for JEI display with full control over display. */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class DisplayPartRecipe implements IDisplayPartBuilderRecipe {
  /** ID of recipe; should generally match a real recipe JSON */
  private final ResourceLocation id;
  /** Material variant for name display */
  private final List<MaterialVariant> materials;
  /** Display title override. */
  @Nullable
  private final Component displayTitle;
  /** Title tooltip */
  private final List<Component> tooltip;
  /** Pattern button input */
  private final List<Pattern> patterns;
  /** Pattern item input */
  private final List<ItemStack> patternItems;
  /** Recipe material cost */
  private final int cost;
  /** List of items to display for the material */
  private final List<ItemStack> materialItems;
  /** List of items to display for the result */
  private final List<ItemStack> resultItems;

  /** @deprecated use {@link #id(ResourceLocation)} */
  @Deprecated(forRemoval = true)
  public DisplayPartRecipe(ResourceLocation id, MaterialVariant material, Pattern pattern, List<ItemStack> patternItems, int cost, List<ItemStack> materialItems, List<ItemStack> resultItems) {
    this(id, List.of(material), null, List.of(), List.of(pattern), patternItems, cost, materialItems, resultItems);
  }

  @Override
  public MaterialVariant getMaterial() {
    if (materials.isEmpty()) {
      return MaterialVariant.UNKNOWN;
    }
    return materials.get(0);
  }

  @Override
  public Pattern getPattern() {
    return patterns.get(0);
  }

  /* Required part builder methods */

  @Override
  public boolean partialMatch(IPartBuilderContainer inv) {
    throw new UnsupportedOperationException();
  }

  /** @deprecated needed to implement the interface, not meant to be used */
  @Override
  @Deprecated
  @Internal
  public boolean matches(IPartBuilderContainer pContainer, Level pLevel) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
    throw new UnsupportedOperationException();
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    throw new UnsupportedOperationException();
  }


  /* Builder */

  /** Creates a new builder for the given recipe ID */
  public static Builder id(ResourceLocation id) {
    return new Builder(id);
  }

  @Setter
  @Accessors(fluent = true)
  @RequiredArgsConstructor
  public static class Builder {
    /** ID of recipe; should generally match a real recipe JSON */
    private final ResourceLocation id;
    /** Material variant for name display */
    private List<MaterialVariant> materials = List.of();
    /** Title to display in JEI */
    private Component title = null;
    /** Tooltip to display in JEI */
    private List<Component> tooltip = List.of();
    /** Pattern button input */
    private List<Pattern> patterns = List.of();
    /** Pattern item input */
    private List<ItemStack> patternItems = List.of();
    /** Recipe material cost */
    private int cost = 0;
    /** List of items to display for the material */
    private List<ItemStack> materialItems = List.of();
    /** List of items to display for the result */
    private List<ItemStack> results = List.of();

    /** Sets the material. */
    public Builder material(MaterialVariant material) {
      return materials(List.of(material));
    }

    /** Sets the material. */
    public Builder material(MaterialVariantId material) {
      return material(MaterialVariant.of(material));
    }

    /** Sets the pattern to a single pattern */
    public Builder pattern(Pattern pattern) {
      return patterns(List.of(pattern));
    }

    /** Sets the pattern item to a single item */
    public Builder patternItem(ItemStack stack) {
      return patternItems(List.of(stack));
    }

    /** Sets the pattern items to the given ingredient */
    public Builder patternItem(Ingredient ingredient) {
      return patternItems(List.of(ingredient.getItems()));
    }

    /** Sets the material item to a single item */
    public Builder materialItem(ItemStack stack) {
      return materialItems(List.of(stack));
    }

    /** Sets the result to a single item */
    public Builder result(ItemStack stack) {
      return results(List.of(stack));
    }

    /** Builds the display recipe */
    public IDisplayPartBuilderRecipe build() {
      if (results.isEmpty()) throw new IllegalStateException("Results cannot be empty");
      return new DisplayPartRecipe(id, materials, title, tooltip, patterns, patternItems, cost, materialItems, results);
    }
  }
}
