package slimeknights.tconstruct.library.modifiers.modules.capacity;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.LootContext;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.mantle.data.loadable.common.IngredientLoadable;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ProcessLootModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

/** Module that restores capacity by consuming items from loot, used for stoneshield. */
public record LootToCapacityModule(Ingredient consume, int amount, LevelingValue chance, @Nullable ModifierId owner, ModifierCondition<IToolStackView> condition) implements ModifierModule, ProcessLootModifierHook, CapacitySourceModule, ConditionalModule<IToolStackView> {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<LootToCapacityModule>defaultHooks(ModifierHooks.PROCESS_LOOT);
  public static final RecordLoadable<LootToCapacityModule> LOADER = RecordLoadable.create(
    IngredientLoadable.DISALLOW_EMPTY.requiredField("consume", LootToCapacityModule::consume),
    IntLoadable.FROM_ONE.requiredField("amount", LootToCapacityModule::amount),
    LevelingValue.LOADABLE.requiredField("chance", LootToCapacityModule::chance),
    OWNER_FIELD, ModifierCondition.TOOL_FIELD,
    LootToCapacityModule::new);

  /** @apiNote use {@link #consume(Ingredient)} */
  @Internal
  public LootToCapacityModule {}

  @Override
  public RecordLoadable<LootToCapacityModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public void processLoot(IToolStackView tool, ModifierEntry modifier, List<ItemStack> generatedLoot, LootContext context) {
    if (!condition.matches(tool, modifier)) {
      return;
    }
    Iterator<ItemStack> iterator = generatedLoot.iterator();
    int addedShield = 0;
    // chance per level of consuming each stone
    float chance = this.chance.compute(modifier.getEffectiveLevel());
    while (iterator.hasNext()) {
      ItemStack stack = iterator.next();
      // if the item is a stone, num time
      if (consume.test(stack)) {
        // 100% chance? just add the full count
        if (chance >= 1.0f) {
          addedShield += stack.getCount();
          iterator.remove();
        } else {
          // smaller chance, independant chance per stone
          int reduced = 0;
          for (int i = 0; i < stack.getCount(); i++) {
            if (TConstruct.RANDOM.nextFloat() < chance) {
              reduced++;
            }
          }
          // if we ate them all, remove, otherwise just shrink
          if (reduced == stack.getCount()) {
            iterator.remove();
          } else {
            stack.shrink(reduced);
          }
          addedShield += reduced;
        }
      }
    }

    // if we found any stone, add shield
    if (addedShield > 0) {
      ModifierEntry barModifier = barModifier(tool, modifier);
      barModifier.getHook(ModifierHooks.CAPACITY_BAR).addAmount(tool, barModifier, addedShield * amount);
    }
  }


  /* Builder */

  /** Creates a builder for the given ingredient */
  public static Builder consume(Ingredient consume) {
    return new Builder(consume);
  }

  @Accessors(fluent = true)
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder extends CapacitySourceModule.Builder<Builder> implements LevelingValue.Builder<LootToCapacityModule>  {
    private final Ingredient consume;
    @Setter
    private int amount;

    @Override
    public LootToCapacityModule amount(float flat, float eachLevel) {
      return new LootToCapacityModule(consume, amount, new LevelingValue(flat, eachLevel), owner, condition);
    }
  }
}
