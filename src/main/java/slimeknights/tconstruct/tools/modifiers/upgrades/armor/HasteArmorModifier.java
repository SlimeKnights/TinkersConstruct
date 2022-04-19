package slimeknights.tconstruct.tools.modifiers.upgrades.armor;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.impl.IncrementalArmorLevelModifier;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.TooltipKey;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.List;

public class HasteArmorModifier extends IncrementalArmorLevelModifier {
  private static final Component MINING_SPEED = TConstruct.makeTranslation("modifier", "fake_attribute.mining_speed");
  /** Player modifier data key for haste */
  public static final TinkerDataKey<Float> HASTE = TConstruct.createKey("haste");

  public HasteArmorModifier() {
    super(HASTE);
  }

  @Override
  public Component getDisplayName() {
    return super.getDisplayName();
  }

  @Override
  public Component getDisplayName(int level) {
    if (TinkerModifiers.haste.isBound()) {
      return TinkerModifiers.haste.get().getDisplayName(level);
    }
    return super.getDisplayName(level);
  }

  @Override
  public void addInformation(IToolStackView tool, int level, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    double boost = 0.1 * getScaledLevel(tool, level);
    if (boost != 0) {
      tooltip.add(applyStyle(new TextComponent(Util.PERCENT_BOOST_FORMAT.format(boost)).append(" ").append(MINING_SPEED)));
    }
  }
}
