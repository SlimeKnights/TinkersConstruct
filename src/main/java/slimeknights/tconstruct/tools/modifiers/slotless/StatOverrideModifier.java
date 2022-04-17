package slimeknights.tconstruct.tools.modifiers.slotless;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.mutable.MutableObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.nbt.IModDataView;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.stat.INumericToolStat;
import slimeknights.tconstruct.library.tools.stat.IToolStat;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStatId;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/** Modifier to directly modify a tool's stats */
public class StatOverrideModifier extends NoLevelsModifier {
  /** Key of all stats added to the tool */
  private static final ResourceLocation KEY_BONUS = TConstruct.getResource("override_bonus");
  /** Key of all stats multiplied by the tool */
  private static final ResourceLocation KEY_MULTIPLY = TConstruct.getResource("override_multiplier");
  /** Prefix for adding bonuses to the tooltip */
  private static final Component LANG_BONUS = TConstruct.makeTranslation("modifier", "stat_override.bonuses").withStyle(ChatFormatting.UNDERLINE);
  /** Prefix for adding multipliers to the tooltip */
  private static final Component LANG_MULTIPLY = TConstruct.makeTranslation("modifier", "stat_override.multipliers").withStyle(ChatFormatting.UNDERLINE);

  @Override
  public boolean shouldDisplay(boolean advanced) {
    return advanced;
  }

  @Override
  public void onRemoved(IToolStackView tool) {
    tool.getPersistentData().remove(KEY_BONUS);
    tool.getPersistentData().remove(KEY_MULTIPLY);
  }

  /** Processes the stats from Tag into the consumer */
  private static void processStats(IModDataView persistentData, ResourceLocation key, StatConsumer consumer) {
    if (persistentData.contains(key, Tag.TAG_COMPOUND)) {
      CompoundTag nbt = persistentData.getCompound(key);
      for (String name : nbt.getAllKeys()) {
        ToolStatId id = ToolStatId.tryCreate(name);
        if (id != null) {
          IToolStat<?> stat = ToolStats.getToolStat(id);
          if (stat != null) {
            consumer.handle(stat, Objects.requireNonNull(nbt.get(name)));
          }
        }
      }
    }
  }

  /** Generic friendly update method for stats */
  private static <T> void update(ModifierStatsBuilder builder, IToolStat<T> stat, Tag tag) {
    T value = stat.read(tag);
    if (value != null) {
      stat.update(builder, value);
    }
  }

  @Override
  public void addToolStats(ToolRebuildContext context, int level, ModifierStatsBuilder builder) {
    IModDataView persistentData = context.getPersistentData();
    processStats(persistentData, KEY_BONUS, (stat, tag) -> update(builder, stat, tag));
    processStats(persistentData, KEY_MULTIPLY, (stat, tag) -> {
      if (stat instanceof INumericToolStat<?> numeric && TagUtil.isNumeric(tag)) {
        numeric.multiply(builder, ((NumericTag)tag).getAsFloat());
      }
    });
  }

  @Nullable
  private static <T> Component format(IToolStat<T> stat, Tag tag) {
    T value = stat.read(tag);
    return value == null ? null : stat.formatValue(value);
  }

  /** Helper to get descriptions for one of the groups */
  private static void addToTooltip(IModDataView persistentData, ResourceLocation groupKey, Component listStart, DecimalFormat format, Consumer<Component> consumer) {
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
            if (stat instanceof INumericToolStat<?>) {
              consumer.accept(new TextComponent("* ").append(stat.getPrefix()).append(format.format(stats.getFloat(key))));
            } else {
              Component formatted = format(stat, Objects.requireNonNull(stats.get(key)));
              if (formatted != null) {
                consumer.accept(new TextComponent("* ").append(formatted));
              }
            }
          }
        }
      }
    }
  }

  @Override
  public List<Component> getDescriptionList(IToolStackView tool, int level) {
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
    IModDataView persistentData = tool.getPersistentData();
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
   * Gets the tag for the given group key, creating if it needed
   * @param tool       Tool instance
   * @param groupKey   Group key
   * @param createTag  If true, creates the tag if missing
   * @return  Tag, or null if missing and {@code createTag} is false
   */
  @Nullable
  private static CompoundTag getTag(IToolStackView tool, ResourceLocation groupKey, boolean createTag) {
    // first, find the proper tag, create if missing
    ModDataNBT data = tool.getPersistentData();
    CompoundTag nbt;
    if (data.contains(groupKey, Tag.TAG_COMPOUND)) {
      return data.getCompound(groupKey);
    } else if (createTag) {
      nbt = new CompoundTag();
      data.put(groupKey, nbt);
      return nbt;
    } else {
      // if setting a value to 0 and no tag, nothing to do
      return null;
    }
  }

  /** Gets the given stat from Tag */
  private static float getStat(IToolStackView tool, ResourceLocation groupKey, INumericToolStat<?> stat, float defaultValue) {
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
   * Shared logic to set the given stat in Tag
   * @param tool      Tool to set
   * @param groupKey  Stat group key
   * @param stat      Stat to set
   * @param value     New stat value
   * @return  True if the modifier is required to represent this change
   */
  private static boolean setStat(IToolStackView tool, ResourceLocation groupKey, INumericToolStat<?> stat, float value, float neutralValue) {
    CompoundTag nbt = getTag(tool, groupKey, value != neutralValue);
    if (nbt == null) {
      return false;
    }
    // if setting to 0, remove the tag, otherwise set it
    String name = stat.getName().toString();
    if (value != neutralValue) {
      nbt.putFloat(name, value);
      return true;
    }
    // remove the value
    nbt.remove(name);
    if (nbt.getAllKeys().isEmpty()) {
      tool.getPersistentData().remove(groupKey);
      return false;
    }
    return true;
  }

  /**
   * Sets the bonus for the stat to the given value
   * @param tool   Tool
   * @param stat   Stat to set
   * @param value  New value
   */
  public <T> boolean set(IToolStackView tool, IToolStat<T> stat, T value) {
    // first, find the proper tag, create if missing
    ModDataNBT data = tool.getPersistentData();
    boolean storeValue;
    if (stat instanceof INumericToolStat) {
      storeValue = ((Number)value).intValue() != 0;
    } else {
      storeValue = value != stat.getDefaultValue();
    }
    // create tag if needed
    CompoundTag nbt = getTag(tool, KEY_BONUS, storeValue);
    if (nbt == null) {
      return false;
    }
    // if we have something to store, do so
    String name = stat.getName().toString();
    if (storeValue) {
      Tag tag = stat.write(value);
      if (tag != null) {
        nbt.put(name, tag);
        return true;
      }
    }
    // remove the value if nothing to store
    nbt.remove(name);
    if (nbt.getAllKeys().isEmpty()) {
      data.remove(StatOverrideModifier.KEY_BONUS);
      return false;
    }
    return true;
  }

  /**
   * Adds the given value to the current stat bonus
   * @param tool   Tool
   * @param stat   Stat to update
   * @param bonus  Value to add to current bonus
   */
  public boolean addBonus(IToolStackView tool, INumericToolStat<?> stat, float bonus) {
    if (bonus != 0) {
      return setStat(tool, KEY_BONUS, stat, getStat(tool, KEY_BONUS, stat, 0) + bonus, 0);
    }
    return false;
  }

  /**
   * Sets the bonus for the modifier to the given value
   * @param tool        Tool
   * @param stat        Stat to set
   * @param multiplier  New multiplier
   */
  public boolean setMultiplier(IToolStackView tool, INumericToolStat<?> stat, float multiplier) {
    return setStat(tool, KEY_MULTIPLY, stat, multiplier, 1);
  }

  /**
   * Multiplies the tool's multiplier by the given value
   * @param tool   Tool
   * @param stat   Stat to set
   * @param value  New multiplier
   */
  public boolean multiply(IToolStackView tool, INumericToolStat<?> stat, float value) {
    if (value != 1) {
      return setMultiplier(tool, stat, getStat(tool, KEY_MULTIPLY, stat, 1) * value);
    }
    return false;
  }

  /** Removes the given stat from the bonuses */
  public <T> boolean remove(IToolStackView tool, IToolStat<T> stat) {
    // create tag if needed
    CompoundTag nbt = getTag(tool, KEY_BONUS, false);
    if (nbt == null) {
      return false;
    }
    // remove the value
    nbt.remove(stat.getName().toString());
    if (nbt.getAllKeys().isEmpty()) {
      tool.getPersistentData().remove(StatOverrideModifier.KEY_BONUS);
      return false;
    }
    return true;
  }

  @FunctionalInterface
  private interface StatConsumer {
    void handle(IToolStat<?> stat, Tag value);
  }
}
