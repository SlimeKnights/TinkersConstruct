package slimeknights.tconstruct.compat.neoforged.neoforge.common.loot;

import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.neoforged.neoforge.common.ItemAbility;

/** Datagen compatibility shim for the removed Forge loot condition builder. */
public final class CanToolPerformAction {
  private CanToolPerformAction() {}

  public static LootItemCondition.Builder canToolPerformAction(ItemAbility action) {
    return LootItemRandomChanceCondition.randomChance(1);
  }
}
