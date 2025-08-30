package slimeknights.tconstruct.tools.modifiers.slotless;

import net.minecraft.network.chat.Component;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.TinkerTags.Items;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.DurabilityShieldModifier;
import slimeknights.tconstruct.library.modifiers.modules.capacity.OverslimeModule;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;

/** @deprecated use helpers from {@link OverslimeModule} if possible. */
@Deprecated
public class OverslimeModifier extends DurabilityShieldModifier implements ToolStatsModifierHook {
  /** @deprecated use {@link OverslimeModule#OVERSLIME_STAT} */
  @Deprecated(forRemoval = true)
  public static final FloatToolStat OVERSLIME_STAT = OverslimeModule.OVERSLIME_STAT;

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ModifierHooks.TOOL_STATS);
  }

  @Override
  public Component getDisplayName(int level) {
    // display name without the level
    return super.getDisplayName();
  }

  @Override
  public int getPriority() {
    // higher than reinforced, reinforced does not protect overslime
    return 150;
  }


  /* Tool building */

  @Override
  public void addToolStats(IToolContext context, ModifierEntry modifier, ModifierStatsBuilder builder) {
    // TODO 1.21: encode stat debuffs using JSON?
    OVERSLIME_STAT.add(builder, 50);
    if (!context.getModifiers().has(TinkerTags.Modifiers.OVERSLIME_FRIEND)) {
      if (context.hasTag(Items.MELEE)) {
        ToolStats.ATTACK_DAMAGE.multiply(builder, 0.9f);
      }
      if (context.hasTag(Items.HARVEST)) {
        ToolStats.MINING_SPEED.multiply(builder, 0.9f);
      }
      if (context.hasTag(TinkerTags.Items.ARMOR)) {
        ToolStats.ARMOR.add(builder, -0.5f);
      }
      if (context.hasTag(TinkerTags.Items.RANGED)) {
        ToolStats.VELOCITY.multiply(builder, 0.9f);
      }
    }
  }


  /* Display */

  @Nullable
  @Override
  public Boolean showDurabilityBar(IToolStackView tool, ModifierEntry modifier) {
    // only show as fully repaired if overslime is full
    return getAmount(tool) < getCapacity(tool, modifier) ? true : null;
  }

  @Override
  public int getDurabilityRGB(IToolStackView tool, ModifierEntry modifier) {
    if (getAmount(tool) > 0) {
      // just always display light blue, not much point in color changing really
      return 0x00A0FF;
    }
    return -1;
  }


  /* Shield implementation */

  /** @deprecated use {@link OverslimeModule#getCapacity(IToolStackView)} */
  @Deprecated
  @Override
  public int getShieldCapacity(IToolStackView tool, ModifierEntry modifier) {
    return OverslimeModule.getCapacity(tool);
  }

  /** @deprecated use {@link OverslimeModule#getOverworkedBonus(IToolStackView)} */
  @Deprecated(forRemoval = true)
  public static int getOverworkedBonus(IToolStackView tool) {
    return OverslimeModule.getOverworkedBonus(tool);
  }

  @Override
  public void addAmount(IToolStackView tool, ModifierEntry modifier, int amount) {
    // yeah, I am hardcoding overworked. If you need something similar, put in an issue request on github
    // grants +100% restoring per level
    super.addAmount(tool, modifier, amount * getOverworkedBonus(tool));
  }

  /** @deprecated use {@link OverslimeModule#addAmount(IToolStackView, int)} */
  @Deprecated(forRemoval = true)
  public void addOverslime(IToolStackView tool, ModifierEntry entry, int amount) {
    addAmount(tool, entry, amount);
  }

  /** @deprecated use {@link OverslimeModule#removeAmount(IToolStackView, int)} */
  @Deprecated(forRemoval = true)
  public void removeOverslime(IToolStackView tool, ModifierEntry entry, int amount) {
    removeAmount(tool, entry, amount);
  }
}
