package slimeknights.tconstruct.tools.modifiers.upgrades.general;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.tconstruct.library.modifiers.impl.SingleLevelModifier;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.TooltipKey;

import javax.annotation.Nullable;
import java.util.List;

import static slimeknights.tconstruct.common.TinkerTags.Items.ARMOR;
import static slimeknights.tconstruct.common.TinkerTags.Items.DURABILITY;
import static slimeknights.tconstruct.common.TinkerTags.Items.HARVEST;

public class EmeraldModifier extends SingleLevelModifier {
  @Override
  public void addVolatileData(ToolRebuildContext context, int level, ModDataNBT volatileData) {
    IModifiable.setRarity(volatileData, Rarity.UNCOMMON);
  }

  @Override
  public void addToolStats(ToolRebuildContext context, int level, ModifierStatsBuilder builder) {
    Item item = context.getItem();
    if (DURABILITY.contains(item)) {
      ToolStats.DURABILITY.multiply(builder, 1 + (level * 0.5f));
    }
    if (HARVEST.contains(item)) {
      ToolStats.HARVEST_TIER.update(builder, Tiers.IRON);
    }
    if (ARMOR.contains(item)) {
      ToolStats.KNOCKBACK_RESISTANCE.add(builder, level * 0.05f);
    }
  }

  @Override
  public float getEntityDamage(IToolStackView tool, int level, ToolAttackContext context, float baseDamage, float damage) {
    LivingEntity living = context.getLivingTarget();
    if (living != null && living.getMobType() == MobType.ILLAGER) {
      damage += level * 2.5f * tool.getMultiplier(ToolStats.ATTACK_DAMAGE);
    }
    return damage;
  }

  @Override
  public void addInformation(IToolStackView tool, int level, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    addDamageTooltip(tool, level * 2.5f, tooltip);
  }
}
