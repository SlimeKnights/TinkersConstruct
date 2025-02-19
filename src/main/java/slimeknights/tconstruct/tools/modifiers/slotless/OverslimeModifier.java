package slimeknights.tconstruct.tools.modifiers.slotless;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.TinkerTags.Items;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.DurabilityShieldModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStatId;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;

public class OverslimeModifier extends DurabilityShieldModifier implements ToolStatsModifierHook {
  /** Stat for the overslime cap, copies the durability global multiplier on build */
  public static final FloatToolStat OVERSLIME_STAT = ToolStats.register(new FloatToolStat(new ToolStatId(TConstruct.MOD_ID, "overslime"), 0xFF71DC85, 0, 0, Short.MAX_VALUE, TinkerTags.Items.DURABILITY) {
    @Override
    public Float build(ModifierStatsBuilder parent, Object builderObj) {
      return super.build(parent, builderObj) * parent.getMultiplier(ToolStats.DURABILITY);
    }
  });

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
    return getShield(tool) < getShieldCapacity(tool, modifier) ? true : null;
  }

  @Override
  public int getDurabilityRGB(IToolStackView tool, ModifierEntry modifier) {
    if (getShield(tool) > 0) {
      // just always display light blue, not much point in color changing really
      return 0x00D0FF;
    }
    return -1;
  }


  /* Shield implementation */

  @Override
  protected ResourceLocation getShieldKey() {
    return getId();
  }

  @Override
  public int getShieldCapacity(IToolStackView tool, ModifierEntry modifier) {
    return tool.getStats().getInt(OVERSLIME_STAT);
  }

  /**
   * Adds to the overslime on a tool
   * @param tool    Tool instance
   * @param entry   Overslime entry on the tool
   * @param amount  Amount to add
   */
  public void addOverslime(IToolStackView tool, ModifierEntry entry, int amount) {
    // yeah, I am hardcoding overworked. If you need something similar, put in an issue request on github
    // grants +100% restoring per level
    addShield(tool, entry, amount * (1 + tool.getModifierLevel(TinkerModifiers.overworked.getId())));
  }
}
