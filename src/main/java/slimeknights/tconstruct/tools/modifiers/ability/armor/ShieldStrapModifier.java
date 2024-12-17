package slimeknights.tconstruct.tools.modifiers.ability.armor;

import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.hook.interaction.KeybindInteractModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.build.VolatileFlagModule;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.library.tools.capability.inventory.InventoryMenuModule;
import slimeknights.tconstruct.library.tools.capability.inventory.InventoryModule;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability;
import slimeknights.tconstruct.tools.modules.armor.ShieldStrapModule;

import java.util.Set;

/** @deprecated use {@link VolatileFlagModule}, {@link InventoryMenuModule}, {@link ShieldStrapModule}, and {@link InventoryMenuModule} */
@Deprecated(forRemoval = true)
public class ShieldStrapModifier extends Modifier implements KeybindInteractModifierHook {
  private static final ResourceLocation KEY = TConstruct.getResource("shield_strap");
  private static final Pattern PATTERN = new Pattern(TConstruct.MOD_ID, "shield_plus");

  @Override
  public int getPriority() {
    return 95; // before pockets and tool belt
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(new VolatileFlagModule(ToolInventoryCapability.INCLUDE_OFFHAND));
    hookBuilder.addModule(InventoryModule.builder().key(KEY).pattern(PATTERN).slotsPerLevel(1));
    hookBuilder.addModule(new ShieldStrapModule(Set.of(TooltipKey.NORMAL)));
    hookBuilder.addModule(InventoryMenuModule.SHIFT);
  }
}
