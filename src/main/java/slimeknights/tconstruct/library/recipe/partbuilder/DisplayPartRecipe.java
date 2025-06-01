package slimeknights.tconstruct.library.recipe.partbuilder;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;

import java.util.List;

/** Part builder recipe for JEI display with full control over display. */
@RequiredArgsConstructor
@Getter
public class DisplayPartRecipe implements IDisplayPartBuilderRecipe {
  /** ID of recipe; should generally match a real recipe JSON */
  private final ResourceLocation id;
  /** Material variant for name display */
  private final MaterialVariant material;
  /** Pattern button input */
  private final Pattern pattern;
  /** Pattern item input */
  private final List<ItemStack> patternItems;
  /** Recipe material cost */
  private final int cost;
  /** List of items to display for the material */
  private final List<ItemStack> materialItems;
  /** List of items to display for the result */
  private final List<ItemStack> resultItems;


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
}
