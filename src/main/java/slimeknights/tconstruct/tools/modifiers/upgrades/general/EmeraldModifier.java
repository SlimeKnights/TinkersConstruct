package slimeknights.tconstruct.tools.modifiers.upgrades.general;

import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.library.modifiers.SingleLevelModifier;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.HarvestLevels;
import slimeknights.tconstruct.library.utils.TooltipFlag;
import slimeknights.tconstruct.library.utils.TooltipKey;

import javax.annotation.Nullable;
import java.util.List;

import static slimeknights.tconstruct.common.TinkerTags.Items.ARMOR;
import static slimeknights.tconstruct.common.TinkerTags.Items.DURABILITY;
import static slimeknights.tconstruct.common.TinkerTags.Items.HARVEST;

public class EmeraldModifier extends SingleLevelModifier {
  public EmeraldModifier() {
    super(0x41f384);
  }

  @Override
  public void addVolatileData(ToolRebuildContext context, int level, ModDataNBT volatileData) {
    IModifiable.setRarity(volatileData, Rarity.UNCOMMON);
  }

  @Override
  public void addToolStats(ToolRebuildContext context, int level, ModifierStatsBuilder builder) {
    Item item = context.getItem();
    if (item.is(DURABILITY)) {
      ToolStats.DURABILITY.multiply(builder, 1 + (level * 0.5f));
    }
    if (item.is(HARVEST)) {
      ToolStats.HARVEST_LEVEL.set(builder, HarvestLevels.IRON);
    }
    if (item.is(ARMOR)) {
      ToolStats.KNOCKBACK_RESISTANCE.add(builder, level * 0.05f);
    }
  }

  @Override
  public float getEntityDamage(IModifierToolStack tool, int level, ToolAttackContext context, float baseDamage, float damage) {
    LivingEntity living = context.getLivingTarget();
    if (living != null && living.getMobType() == CreatureAttribute.ILLAGER) {
      damage += level * 2.5f * tool.getModifier(ToolStats.ATTACK_DAMAGE);
    }
    return damage;
  }

  @Override
  public void addInformation(IModifierToolStack tool, int level, @Nullable PlayerEntity player, List<ITextComponent> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    addDamageTooltip(tool, level * 2.5f, tooltip);
  }
}
