package slimeknights.tconstruct.tools.modifiers.traits.melee;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.TooltipKey;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.List;

public class ScorchingModifier extends Modifier {
  @Override
  public void addVolatileData(ToolRebuildContext context, int level, ModDataNBT volatileData) {
    TinkerModifiers.tank.get().addCapacity(volatileData, FluidValues.BRICK);
  }

  @Override
  public float getEntityDamage(IToolStackView tool, int level, ToolAttackContext context, float baseDamage, float damage) {
    if (context.getTarget().isOnFire()) {
      damage += 2f * level * tool.getMultiplier(ToolStats.ATTACK_DAMAGE);
    }
    return damage;
  }

  @Override
  public void addInformation(IToolStackView tool, int level, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    addDamageTooltip(tool, level * 2, tooltip);
  }
}
