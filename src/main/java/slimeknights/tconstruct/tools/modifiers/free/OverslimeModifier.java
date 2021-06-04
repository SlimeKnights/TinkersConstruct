package slimeknights.tconstruct.tools.modifiers.free;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;

public class OverslimeModifier extends SingleUseModifier {
  private static final String KEY_OVERSLIME_AMOUNT = Util.makeTranslationKey("gui", "amount");
  /** Key for remaining overslime on a tool */
  private static final ResourceLocation KEY_OVERSLIME = Util.getResource("overslime");
  /** Key for max overslime on a tool */
  private static final ResourceLocation KEY_OVERSLIME_CAP = Util.getResource("overslime_cap");
  /** Key marking another modifier as an overslime "friend". If no friends exist, overslime causes some debuffs */
  private static final ResourceLocation KEY_OVERSLIME_FRIEND = Util.getResource("overslime_friend");

  public OverslimeModifier() {
    super(0x71DC85);
  }

  @Override
  public ITextComponent getDisplayName(IModifierToolStack tool, int level) {
    return getDisplayName().deepCopy().appendString(": " + getOverslime(tool) + " / " + getCapacity(tool));
  }


  /* Tool building */

  @Override
  public void addVolatileData(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, int level, ModDataNBT volatileData) {
    // base cap
    addCapacity(volatileData, (int)(50 * toolDefinition.getBaseStatDefinition().getModifier(ToolStats.DURABILITY)));
  }

  @Override
  public void addToolStats(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, IModDataReadOnly volatileData, int level, ModifierStatsBuilder builder) {
    if (!volatileData.getBoolean(KEY_OVERSLIME_FRIEND)) {
      ToolStats.ATTACK_DAMAGE.multiply(builder, 0.9f);
      ToolStats.MINING_SPEED.multiply(builder, 0.9f);
    }
  }

  @Override
  public ValidatedResult validate(IModifierToolStack tool, int level) {
    // clear excess overslime
    int cap = getCapacity(tool);
    if (getOverslime(tool) > cap) {
      setOverslime(tool, cap);
    }
    return ValidatedResult.PASS;
  }


  /* Hooks */

  @Override
  public int getPriority() {
    // higher than reinforced, reinforced does not protect overslime
    return 150;
  }

  @Override
  public int onDamageTool(IModifierToolStack toolStack, int level, int amount) {
    int overslime = getOverslime(toolStack);
    if (overslime > 0) {
      // if we have more overslime than amount, remove some overslime
      if (overslime >= amount) {
        setOverslime(toolStack, overslime - amount);
        return 0;
      }
      // amount is more than overslime, reduce and clear overslime
      amount -= overslime;
      setOverslime(toolStack, 0);
    }
    return amount;
  }


  /* Display */

  @Override
  public double getDamagePercentage(IModifierToolStack tool, int level) {
    int overslime = getOverslime(tool);
    if (overslime > 0) {
      int cap = getCapacity(tool);
      if (overslime > cap) {
        return 0;
      }
      return ((double) (cap - overslime) / cap);
    }
    return Double.NaN;
  }

  @Nullable
  @Override
  public Boolean showDurabilityBar(IModifierToolStack tool, int level) {
    // only show as fully repaired if overslime is full
    return getOverslime(tool) < getCapacity(tool);
  }

  @Override
  public int getDurabilityRGB(IModifierToolStack tool, int level) {
    if (getOverslime(tool) > 0) {
      // just always display light blue, not much point in color changing really
      return 0x00D0FF;
    }
    return -1;
  }


  /* Data keys */

  /** Gets the key for overslime */
  public ResourceLocation getOverslimeKey() {
    return KEY_OVERSLIME;
  }

  /** Gets the key for overslime capacity */
  public ResourceLocation getCapacityKey() {
    return KEY_OVERSLIME_CAP;
  }

  /** Gets the key for overslime friends */
  public ResourceLocation getFriendKey() {
    return KEY_OVERSLIME_FRIEND;
  }

  /** Sets the friend key in this tool */
  public void setFriend(ModDataNBT volatileData) {
    volatileData.putBoolean(getFriendKey(), true);
  }

  /* Capacity helpers */

  /**
   * Gets the current overslime cap
   * @param volatileData  Volatile data instance
   * @return  Current cap
   */
  public int getCapacity(IModDataReadOnly volatileData) {
    return volatileData.getInt(getCapacityKey());
  }

  /**
   * Helper to reduce code errors
   * @param tool  Tool instance
   * @return  Overslime cap
   */
  public int getCapacity(IModifierToolStack tool) {
    return getCapacity(tool.getVolatileData());
  }

  /**
   * Sets the given amount to the cap, if you are going to use this method, your modifier should be high priority to prevent blocking others
   * In general, {@link #addCapacity(ModDataNBT, int)} or {@link #multiplyCapacity(ModDataNBT, float)} will serve you better
   * @param volatileData  Volatile data instance
   * @param amount        Amount to set
   */
  public void setCapacity(ModDataNBT volatileData, int amount) {
    volatileData.putInt(KEY_OVERSLIME_CAP, amount);
  }

  /**
   * Adds the given amount to the cap
   * @param volatileData  Volatile data instance
   * @param amount        Amount to add
   */
  public void addCapacity(ModDataNBT volatileData, int amount) {
    setCapacity(volatileData, getCapacity(volatileData) + amount);
  }

  /**
   * Adds the given amount to the cap
   * @param volatileData  Volatile data instance
   * @param factor        Multiplication factor
   */
  public void multiplyCapacity(ModDataNBT volatileData, float factor) {
    volatileData.putInt(KEY_OVERSLIME_CAP, (int)(getCapacity(volatileData) * factor));
  }


  /* Overslime helpers */

  /**
   * Gets the current overslime on the tool
   * @param tool  Tool stack instance
   * @return  Default cap
   */
  public int getOverslime(IModifierToolStack tool) {
    return tool.getPersistentData().getInt(getOverslimeKey());
  }

  /**
   * Sets the overslime, bypassing the capacity
   * @param persistentData  Persistent data
   * @param amount          Amount to set
   * @deprecated For display use only, in general use {@link #setOverslime(IModifierToolStack, int)}
   */
  @Deprecated
  public void setOverslime(ModDataNBT persistentData, int amount) {
    persistentData.putInt(getOverslimeKey(), Math.max(amount, 0));
  }

  /**
   * Sets the overslime on a tool
   */
  public void setOverslime(IModifierToolStack tool, int amount) {
    setOverslime(tool.getPersistentData(), Math.min(amount, getCapacity(tool)));
  }

  /**
   * Adds to the overslime on a tool
   */
  public void addOverslime(IModifierToolStack tool, int amount) {
    setOverslime(tool, getOverslime(tool) + amount);
  }
}
