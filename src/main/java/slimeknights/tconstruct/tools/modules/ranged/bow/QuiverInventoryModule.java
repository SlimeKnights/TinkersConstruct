package slimeknights.tconstruct.tools.modules.ranged.bow;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.item.ItemPredicate;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.json.IntRange;
import slimeknights.tconstruct.library.json.LevelingInt;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.library.tools.capability.inventory.InventoryModule;
import slimeknights.tconstruct.library.tools.item.ranged.ModifiableBowItem;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

/** Extension of {@link InventoryModule} with a filter setup for bows. */
public class QuiverInventoryModule extends InventoryModule {
  /** Loader instance */
  public static final RecordLoadable<QuiverInventoryModule> LOADER = RecordLoadable.create(KEY_FIELD, SLOTS_FIELD, LIMIT_FIELD, PATTERN_FIELD, ModifierCondition.CONTEXT_FIELD, VALIDATION_FIELD, QuiverInventoryModule::new);

  private QuiverInventoryModule(@Nullable ResourceLocation key, LevelingInt slots, LevelingInt slotLimit, @Nullable Pattern pattern, ModifierCondition<IToolContext> condition, IntRange validationLevel) {
    super(key, slots, slotLimit, ItemPredicate.ANY, pattern, condition, validationLevel);
  }

  @Override
  public RecordLoadable<QuiverInventoryModule> getLoader() {
    return LOADER;
  }

  @Override
  public boolean isItemValid(IToolStackView tool, ModifierEntry modifier, int slot, ItemStack stack) {
    if (condition().matches(tool, modifier)) {
      return stack.is(ItemTags.ARROWS)
        || tool.hasTag(TinkerTags.Items.CROSSBOWS) && stack.is(Items.FIREWORK_ROCKET)
        || stack.is(TinkerTags.Items.BALLISTA_AMMO) && ModifiableBowItem.isBallista(tool);
    }
    return false;
  }


  /* Builder */

  /** Creates a new builder instance */
  public static Builder builder() {
    return new Builder();
  }

  public static class Builder extends InventoryModule.Builder {
    private Builder() {}

    @Deprecated(forRemoval = true)
    @Override
    public InventoryModule.Builder filter(IJsonPredicate<Item> filter) {
      throw new IllegalStateException("Cannot set filter on QuiverInventoryModule");
    }

    @Override
    public InventoryModule slots(int base, int perLevel) {
      return new QuiverInventoryModule(key, new LevelingInt(base, perLevel), slotLimit, pattern, condition, validationLevel);
    }
  }
}
