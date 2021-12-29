package slimeknights.tconstruct.tools.modifiers.slotless;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.mutable.MutableObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;
import slimeknights.tconstruct.library.tools.stat.IToolStat;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStatId;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.Util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** Modifier to directly modify a tool's stats */
public class StatOverrideModifier extends SingleUseModifier {
  /** Key of all stats added to the tool */
  private static final ResourceLocation KEY_BONUS = TConstruct.getResource("override_bonus");
  /** Key of all stats multiplied by the tool */
  private static final ResourceLocation KEY_MULTIPLY = TConstruct.getResource("override_multiplier");
  /** Prefix for adding bonuses to the tooltip */
  private static final Component LANG_BONUS = TConstruct.makeTranslation("modifier", "stat_override.bonuses").withStyle(ChatFormatting.UNDERLINE);
  /** Prefix for adding multipliers to the tooltip */
  private static final Component LANG_MULTIPLY = TConstruct.makeTranslation("modifier", "stat_override.multipliers").withStyle(ChatFormatting.UNDERLINE);

  public StatOverrideModifier() {
    super(-1);
  }

  @Override
  public boolean shouldDisplay(boolean advanced) {
    return advanced;
  }

  @Override
  public void onRemoved(IModifierToolStack tool) {
    tool.getPersistentData().remove(KEY_BONUS);
    tool.getPersistentData().remove(KEY_MULTIPLY);
  }

  /** Processes the stats from Tag into the consumer */
  private static void processStats(IModDataReadOnly persistentData, ResourceLocation key, StatConsumer consumer) {
    if (persistentData.contains(key, Tag.TAG_COMPOUND)) {
      CompoundTag nbt = persistentData.getCompound(key);
      for (String name : nbt.getAllKeys()) {
        ToolStatId id = ToolStatId.tryCreate(name);
        if (id != null) {
          IToolStat<?> stat = ToolStats.getToolStat(id);
          if (stat != null) {
            consumer.handle(stat, nbt.getFloat(name));
          }
        }
      }
    }
  }

  @Override
  public void addToolStats(ToolRebuildContext context, int level, ModifierStatsBuilder builder) {
    IModDataReadOnly persistentData = context.getPersistentData();
    processStats(persistentData, KEY_BONUS, (stat, value) -> stat.applyBonus(builder, value));
    processStats(persistentData, KEY_MULTIPLY, (stat, value) -> {
      if (stat instanceof FloatToolStat) {
        ((FloatToolStat) stat).multiply(builder, value);
      }
    });
  }

  /** Helper to get descriptions for one of the groups */
  private static void addToTooltip(IModDataReadOnly persistentData, ResourceLocation groupKey, Component listStart, DecimalFormat format, Consumer<Component> consumer) {
    if (persistentData.contains(groupKey, Tag.TAG_COMPOUND)) {
      CompoundTag stats = persistentData.getCompound(groupKey);

      // first one found has special behavior
      boolean first = true;
      for (String key : stats.getAllKeys()) {
        // ignore invalid stat names
        ToolStatId id = ToolStatId.tryCreate(key);
        if (id != null) {
          IToolStat<?> stat = ToolStats.getToolStat(id);
          if (stat != null) {
            // add prefix for the first of the type
            if (first) {
              consumer.accept(listStart);
              first = false;
            }
            // add stat
            consumer.accept(new TextComponent("* ").append(stat.getPrefix()).append(format.format(stats.getFloat(key))));
          }
        }
      }
    }
  }

  @Override
  public List<Component> getDescriptionList(IModifierToolStack tool, int level) {
    List<Component> defaultList = getDescriptionList(level);

    // create the list when we first try to add text
    MutableObject<List<Component>> resultList = new MutableObject<>();
    Consumer<Component> consumer = text -> {
      List<Component> list = resultList.getValue();
      if (list == null) {
        list = new ArrayList<>(defaultList);
        resultList.setValue(list);
      }
      list.add(text);
    };

    // run all groups
    IModDataReadOnly persistentData = tool.getPersistentData();
    addToTooltip(persistentData, KEY_BONUS,    LANG_BONUS,    Util.BONUS_FORMAT,      consumer);
    addToTooltip(persistentData, KEY_MULTIPLY, LANG_MULTIPLY, Util.MULTIPLIER_FORMAT, consumer);

    // if anything changed, return the new list
    List<Component> computedList = resultList.getValue();
    if (computedList != null) {
      return computedList;
    }
    return defaultList;
  }

  /* Helpers */

  /**
   * Shared logic to set the given stat in Tag
   * @param tool      Tool to set
   * @param groupKey  Stat group key
   * @param stat      Stat to set
   * @param value     New stat value
   * @return  True if the modifier is required to represent this change
   */
  private static boolean setStat(IModifierToolStack tool, ResourceLocation groupKey, IToolStat<?> stat, float value, float neutralValue) {
    // first, find the proper tag, create if missing
    ModDataNBT data = tool.getPersistentData();
    CompoundTag nbt;
    if (data.contains(groupKey, Tag.TAG_COMPOUND)) {
      nbt = data.getCompound(groupKey);
    } else if (value != neutralValue) {
      nbt = new CompoundTag();
      data.put(groupKey, nbt);
    } else {
      // if setting a value to 0 and no tag, nothing to do
      return false;
    }

    // if setting to 0, remove the tag, otherwise set it
    String name = stat.getName().toString();
    if (value == neutralValue) {
      nbt.remove(name);
      if (nbt.getAllKeys().isEmpty()) {
        data.remove(groupKey);
        return false;
      }
    } else {
      nbt.putFloat(name, value);
    }
    return true;
  }

  /** Gets the given stat from Tag */
  private static float getStat(IModifierToolStack tool, ResourceLocation groupKey, IToolStat<?> stat, float defaultValue) {
    ModDataNBT data = tool.getPersistentData();
    if (data.contains(groupKey, Tag.TAG_COMPOUND)) {
      CompoundTag nbt = data.getCompound(groupKey);
      String name = stat.getName().toString();
      if (nbt.contains(name, Tag.TAG_FLOAT)) {
        return nbt.getFloat(name);
      }
    }
    return defaultValue;
  }

  /**
   * Sets the bonus for the stat to the given value
   * @param tool   Tool
   * @param stat   Stat to set
   * @param bonus  New bonus
   */
  public boolean setBonus(IModifierToolStack tool, IToolStat<?> stat, float bonus) {
    return setStat(tool, KEY_BONUS, stat, bonus, 0);
  }

  /**
   * Adds the given value to the current stat bonus
   * @param tool   Tool
   * @param stat   Stat to update
   * @param bonus  Value to add to current bonus
   */
  public boolean addBonus(IModifierToolStack tool, IToolStat<?> stat, float bonus) {
    if (bonus != 0) {
      return setBonus(tool, stat, getStat(tool, KEY_BONUS, stat, 0) + bonus);
    }
    return false;
  }

  /**
   * Sets the bonus for the modifier to the given value
   * @param tool        Tool
   * @param stat        Stat to set
   * @param multiplier  New multiplier
   */
  public boolean setMultiplier(IModifierToolStack tool, FloatToolStat stat, float multiplier) {
    return setStat(tool, KEY_MULTIPLY, stat, multiplier, 1);
  }

  /**
   * Multiplies the tool's multiplier by the given value
   * @param tool   Tool
   * @param stat   Stat to set
   * @param value  New multiplier
   */
  public boolean multiply(IModifierToolStack tool, FloatToolStat stat, float value) {
    if (value != 1) {
      return setMultiplier(tool, stat, getStat(tool, KEY_MULTIPLY, stat, 1) * value);
    }
    return false;
  }

  @FunctionalInterface
  private interface StatConsumer {
    void handle(IToolStat<?> stat, float value);
  }
}
