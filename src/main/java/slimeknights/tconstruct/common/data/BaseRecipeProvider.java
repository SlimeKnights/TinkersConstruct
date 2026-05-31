package slimeknights.tconstruct.common.data;

import net.minecraft.data.PackOutput;
import net.minecraft.data.CachedOutput;
import slimeknights.mantle.compat.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import slimeknights.mantle.data.GenericDataProvider;
import slimeknights.mantle.recipe.data.IRecipeHelper;
import slimeknights.tconstruct.TConstruct;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Shared logic for each module's recipe provider
 */
public abstract class BaseRecipeProvider extends GenericDataProvider implements IConditionBuilder, IRecipeHelper {
  public BaseRecipeProvider(PackOutput generator) {
    super(generator, Target.DATA_PACK, "recipes");
    TConstruct.sealTinkersClass(this, "BaseRecipeProvider", "BaseRecipeProvider is trivial to recreate and directly extending can lead to addon recipes polluting our namespace.");
  }

  protected abstract void buildRecipes(Consumer<FinishedRecipe> consumer);

  @Override
  public abstract String getName();

  @Override
  public CompletableFuture<?> run(CachedOutput cache) {
    List<CompletableFuture<?>> tasks = new ArrayList<>();
    buildRecipes(recipe -> tasks.add(saveJson(cache, recipe.getId(), recipe.serializeRecipe())));
    return allOf(tasks);
  }

  protected static Criterion<?> has(ItemLike item) {
    return InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(item).build());
  }

  protected static Criterion<?> has(TagKey<Item> tag) {
    return InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(tag).build());
  }

  @Override
  public String getModId() {
    return TConstruct.MOD_ID;
  }
}
