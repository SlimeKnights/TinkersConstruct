package slimeknights.tconstruct.tools.modifiers.traits.general;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.TooltipKey;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.tools.modifiers.upgrades.general.ReinforcedModifier;

import javax.annotation.Nullable;
import java.util.List;

public class DenseModifier extends ReinforcedModifier {
  @Override
  public float getRepairFactor(IToolStackView toolStack, int level, float factor) {
    // the scale used by reinforced was quite nice to use for reduction here, so 25% loss at level 1, etc.
    // by level 5, you will be repairing at 25% efficiency, at level 10 its 0%
    return factor * (1 - super.getPercentage(level));
  }

  @Override
  protected float getPercentage(float level) {
    // formula gives 33%, 55%, 70%, 80% for first 4 levels
    return 1f - (float)(1f / (Math.pow(1.5f, level)));
  }

  @Override
  public void addInformation(IToolStackView tool, int level, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    tooltip.add(applyStyle(new TextComponent(Util.PERCENT_FORMAT.format(getPercentage(getScaledLevel(tool, level))) + " ")
                             .append(makeDisplayName())));
  }
}
