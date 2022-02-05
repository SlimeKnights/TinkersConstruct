package slimeknights.tconstruct.tools.modifiers.upgrades.harvest;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.impl.IncrementalArmorLevelModifier;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.TooltipKey;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.util.List;

public class HasteModifier extends IncrementalArmorLevelModifier {
  private static final Component MINING_SPEED = TConstruct.makeTranslation("modifier", "fake_attribute.mining_speed");
  /** Player modifier data key for haste */
  public static final TinkerDataKey<Float> HASTE = TConstruct.createKey("haste");

  public HasteModifier() {
    super(HASTE);
  }

  @Override
  public Component getDisplayName(int level) {
    // displays special names for levels of haste
    if (level <= 5) {
      return applyStyle(new TranslatableComponent(getTranslationKey() + "." + level));
    }
    return super.getDisplayName(level);
  }

  @Override
  public void addToolStats(ToolRebuildContext context, int level, ModifierStatsBuilder builder) {
    float scaledLevel = getScaledLevel(context, level);
    // currently gives +5 speed per level
    // for comparison, vanilla gives +2, 5, 10, 17, 26 for efficiency I to V
    // 5 per level gives us          +5, 10, 15, 20, 25 for 5 levels
    ToolStats.MINING_SPEED.add(builder, scaledLevel * 5f);
  }


  // armor

  @Override
  public void addInformation(IToolStackView tool, int level, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    if (tool.hasTag(TinkerTags.Items.ARMOR)) {
      double boost = 0.1 * getScaledLevel(tool, level);
      if (boost != 0) {
        tooltip.add(applyStyle(new TextComponent(Util.PERCENT_BOOST_FORMAT.format(boost)).append(" ").append(MINING_SPEED)));
      }
    }
  }
}
