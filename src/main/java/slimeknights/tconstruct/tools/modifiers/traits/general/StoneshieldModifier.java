package slimeknights.tconstruct.tools.modifiers.traits.general;

import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.json.LevelingInt;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.modules.capacity.CapacityBarModule;
import slimeknights.tconstruct.library.modifiers.modules.capacity.DurabilityShieldModule;
import slimeknights.tconstruct.library.modifiers.modules.capacity.LootToCapacityModule;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

/** @deprecated use {@link CapacityBarModule}, {@link DurabilityShieldModule}, and {@link LootToCapacityModule} */
@Deprecated(forRemoval = true)
public class StoneshieldModifier extends Modifier {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(new CapacityBarModule(LevelingInt.eachLevel(100), ToolStats.DURABILITY));
    hookBuilder.addModule(new DurabilityShieldModule(0x7F7F7F));
    hookBuilder.addModule(LootToCapacityModule.consume(Ingredient.of(TinkerTags.Items.STONESHIELDS)).amount(3).eachLevel(0.2f));
  }

  @Override
  public int getPriority() {
    // higher than overslime, to ensure this is removed first
    return 175;
  }
}
