package slimeknights.tconstruct.tools.modifiers.upgrades.harvest;

import net.minecraft.inventory.EquipmentSlotType.Group;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.TooltipFlag;
import slimeknights.tconstruct.library.utils.Util;

import java.util.List;

public class HasteModifier extends IncrementalModifier {
  private static final ITextComponent MINING_SPEED = TConstruct.makeTranslation("modifier", "fake_attribute.mining_speed");
  /** Player modifier data key for haste */
  public static final ResourceLocation HASTE = TConstruct.getResource("haste");

  public HasteModifier() {
    super(0x7F0901);
  }

  @Override
  public ITextComponent getDisplayName(int level) {
    // displays special names for levels of haste
    if (level <= 5) {
      return applyStyle(new TranslationTextComponent(getTranslationKey() + "." + level));
    }
    return super.getDisplayName(level);
  }

  @Override
  public void addToolStats(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, IModDataReadOnly volatileData, int level, ModifierStatsBuilder builder) {
    float scaledLevel = getScaledLevel(persistentData, level);
    // currently gives +5 speed per level
    // for comparison, vanilla gives +2, 5, 10, 17, 26 for efficiency I to V
    // 5 per level gives us          +5, 10, 15, 20, 25 for 5 levels
    ToolStats.MINING_SPEED.add(builder, scaledLevel * 5f);
    // maxes at 125%, number chosen to be comparable DPS to quartz
    ToolStats.ATTACK_SPEED.multiply(builder, 1 + scaledLevel * 0.05f);
  }


  // armor

  @Override
  public void onEquip(IModifierToolStack tool, int level, EquipmentChangeContext context) {
    if (context.getChangedSlot().getSlotType() == Group.ARMOR) {
      ModifierUtil.addTotalArmorModifierFloat(tool, context, HASTE, getScaledLevel(tool, level));
    }
  }

  @Override
  public void onUnequip(IModifierToolStack tool, int level, EquipmentChangeContext context) {
    if (context.getChangedSlot().getSlotType() == Group.ARMOR) {
      ModifierUtil.addTotalArmorModifierFloat(tool, context, HASTE, -getScaledLevel(tool, level));
    }
  }

  @Override
  public void addInformation(IModifierToolStack tool, int level, List<ITextComponent> tooltip, TooltipFlag flag) {
    if (tool.hasTag(TinkerTags.Items.ARMOR)) {
      double boost = 0.1 * getScaledLevel(tool, level);
      if (boost != 0) {
        tooltip.add(applyStyle(new StringTextComponent(Util.PERCENT_BOOST_FORMAT.format(boost)).appendString(" ").appendSibling(MINING_SPEED)));
      }
    }
  }
}
