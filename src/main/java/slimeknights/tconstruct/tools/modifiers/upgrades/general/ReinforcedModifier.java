package slimeknights.tconstruct.tools.modifiers.upgrades.general;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.tconstruct.library.modifiers.impl.IncrementalModifier;
import slimeknights.tconstruct.library.modifiers.modules.behavior.ReduceToolDamageModule;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.TooltipKey;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.List;

/** @deprecated use {@link ReduceToolDamageModule} */
@Deprecated
public class ReinforcedModifier extends IncrementalModifier {
  /** Default logic that starting at 25% gives a bonus of 5% less per level */
  public static float diminishingPercent(float level) {
    // formula gives 25%, 45%, 60%, 70%, 75% for first 5 levels
    if (level < 5) {
      return 0.025f * level * (11 - level);
    }
    // after level 5.5 the above formula breaks, so just do +5% per level
    // means for levels 6 to 10, you get 80%, 85%, 90%, 95%, 100%
    // in default config we never go past level 5, but nice for datapacks to allow
    return 0.75f + (level - 5) * 0.05f;
  }

  /**
   * Gets the reinforcment percentage for the given level
   * @param level  Level from 0 to 10
   * @return  Percentage
   */
  protected float getPercentage(float level) {
    return diminishingPercent(level);
  }

  /** @deprecated use {@link ReduceToolDamageModule#reduceDamage(int, float)} */
  @Deprecated
  public static int damageReinforced(int amount, float percentage) {
    return ReduceToolDamageModule.reduceDamage(amount, percentage);
  }

  @Override
  public int onDamageTool(IToolStackView tool, int level, int amount, @Nullable LivingEntity holder) {
    return damageReinforced(amount, getPercentage(getScaledLevel(tool, level)));
  }

  @Override
  public void addInformation(IToolStackView tool, int level, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    float reinforced;
    if (tool.getModifierLevel(TinkerModifiers.unbreakable.get()) > 0) {
      reinforced = 1;
    } else {
      reinforced = getPercentage(getScaledLevel(tool, level));
    }
    tooltip.add(applyStyle(new TextComponent(Util.PERCENT_FORMAT.format(reinforced) + " ").append(makeDisplayName())));
  }
}
