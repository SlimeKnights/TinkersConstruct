package slimeknights.tconstruct.tools.modifiers.free;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants.NBT;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;
import slimeknights.tconstruct.library.tools.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;

public class OverslimeModifier extends SingleUseModifier {
  private static final String KEY_OVERSLIME_AMOUNT = Util.makeTranslationKey("gui", "amount");
  /** Key for remaining overslime on a tool */
  private static final ResourceLocation KEY_OVERSLIME = Util.getResource("overslime");
  /** Key for max overslime on a tool */
  private static final ResourceLocation KEY_OVERSLIME_CAP = Util.getResource("overslime_cap");
  /** Key marking another modifier as an overslime "friend". If no friends exist, overslime causes some debuffs */
  public static final ResourceLocation KEY_OVERSLIME_FRIEND = Util.getResource("overslime_friend");

  public OverslimeModifier() {
    super(0x71DC85);
  }

  @Override
  public ITextComponent getDisplayName(IModifierToolStack tool, int level) {
    return getDisplayName().deepCopy().appendString(": " + getOverslime(tool) + " / " + getCap(tool));
  }


  /* Tool building */

  @Override
  public void addVolatileData(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, int level, ModDataNBT volatileData) {
    // add overslime cap if missing, just a consistency thing really
    if (!volatileData.contains(KEY_OVERSLIME_CAP, NBT.TAG_ANY_NUMERIC)) {
      volatileData.putInt(KEY_OVERSLIME_CAP, getDefaultCap(toolDefinition));
    }
  }

  @Override
  public void addToolStats(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, IModDataReadOnly volatileData, int level, ModifierStatsBuilder builder) {
    if (!volatileData.getBoolean(KEY_OVERSLIME_FRIEND)) {
      builder.multiplyAttackDamage(0.9f);
      builder.multiplyMiningSpeed(0.9f);
    }
  }

  @Override
  public ValidatedResult validate(ToolStack tool, int level) {
    // clear excess overslime
    int cap = getCap(tool);
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
      int cap = getCap(tool);
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
    return getOverslime(tool) < getCap(tool);
  }

  @Override
  public int getDurabilityRGB(IModifierToolStack tool, int level) {
    if (getOverslime(tool) > 0) {
      // just always display light blue, not much point in color changing really
      return 0x00D0FF;
    }
    return -1;
  }


  /* NBT helpers */

  /**
   * Gets the default overslime cap
   * @param toolDefinition  Tool definiton
   * @return  Default cap
   */
  private static int getDefaultCap(ToolDefinition toolDefinition) {
    // cap is 10% of base durability
    return (int)(50 * toolDefinition.getBaseStatDefinition().getDurabilityModifier());
  }

  /**
   * Gets the current overslime cap
   * @param volatileData  Volatile data instance
   * @return  Current cap
   */
  public static int getCap(ToolDefinition toolDefinition, IModDataReadOnly volatileData) {
    if (volatileData.contains(KEY_OVERSLIME_CAP, NBT.TAG_ANY_NUMERIC)) {
      return volatileData.getInt(KEY_OVERSLIME_CAP);
    }
    return getDefaultCap(toolDefinition);
  }

  /**
   * Helper to reduce code errors
   * @param tool  Tool instance
   * @return  Overslime cap
   */
  public static int getCap(IModifierToolStack tool) {
    return getCap(tool.getDefinition(), tool.getVolatileData());
  }

  /**
   * Sets the given amount to the cap, if you are going to use this method, your modifier should be high priority to prevent blocking others
   * In general, {@link #addCap(ToolDefinition, ModDataNBT, int)} or {@link #multiplyCap(ToolDefinition, ModDataNBT, float)} will serve you better
   * @param volatileData  Volatile data instance
   * @param amount        Amount to set
   */
  public static void setCap(ModDataNBT volatileData, int amount) {
    volatileData.putInt(KEY_OVERSLIME_CAP, amount);
  }

  /**
   * Adds the given amount to the cap
   * @param volatileData  Volatile data instance
   * @param amount        Amount to add
   */
  public static void addCap(ToolDefinition toolDefinition, ModDataNBT volatileData, int amount) {
    setCap(volatileData, getCap(toolDefinition, volatileData) + amount);
  }

  /**
   * Adds the given amount to the cap
   * @param volatileData  Volatile data instance
   * @param factor        Multiplication factor
   */
  public static void multiplyCap(ToolDefinition toolDefinition, ModDataNBT volatileData, float factor) {
    volatileData.putInt(KEY_OVERSLIME_CAP, (int)(getCap(toolDefinition, volatileData) * factor));
  }

  /**
   * Gets the current overslime on the tool
   * @param tool  Tool stack instance
   * @return  Default cap
   */
  public static int getOverslime(IModifierToolStack tool) {
    return tool.getPersistentData().getInt(KEY_OVERSLIME);
  }

  /**
   * Sets the overslime on a tool
   */
  public static void setOverslime(ToolDefinition toolDefinition, ModDataNBT persistentData, IModDataReadOnly volatileData, int amount) {
    persistentData.putInt(KEY_OVERSLIME, MathHelper.clamp(amount, 0, getCap(toolDefinition, volatileData)));
  }

  /**
   * Sets the overslime on a tool
   */
  public static void setOverslime(IModifierToolStack tool, int amount) {
    setOverslime(tool.getDefinition(), tool.getPersistentData(), tool.getVolatileData(), amount);
  }

  /**
   * Adds to the overslime on a tool
   */
  public static void addOverslime(IModifierToolStack tool, int amount) {
    setOverslime(tool, getOverslime(tool) + amount);
  }
}
