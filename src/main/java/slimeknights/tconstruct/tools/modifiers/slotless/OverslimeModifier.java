package slimeknights.tconstruct.tools.modifiers.slotless;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.TinkerTags.Items;
import slimeknights.tconstruct.library.modifiers.impl.DurabilityShieldModifier;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.nbt.IModDataView;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;

public class OverslimeModifier extends DurabilityShieldModifier {
  /** Key for max overslime on a tool */
  private static final ResourceLocation KEY_OVERSLIME_CAP = TConstruct.getResource("overslime_cap");
  /**
   * Key marking another modifier as an overslime "friend". If no friends exist, overslime causes some debuffs.
   * Use {@link #getFriendKey()} when possible
   */
  public static final ResourceLocation KEY_OVERSLIME_FRIEND = TConstruct.getResource("overslime_friend");

  @Override
  public Component getDisplayName(int level) {
    // display name without the level
    return super.getDisplayName();
  }


  /* Tool building */

  @Override
  public void addVolatileData(ToolRebuildContext context, int level, ModDataNBT volatileData) {
    // base cap
    addCapacity(volatileData, (int)(50 * context.getDefinition().getData().getMultiplier(ToolStats.DURABILITY)));
  }

  @Override
  public void addToolStats(ToolRebuildContext context, int level, ModifierStatsBuilder builder) {
    if (!context.getVolatileData().getBoolean(KEY_OVERSLIME_FRIEND)) {
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


  /* Hooks */

  @Override
  public int getPriority() {
    // higher than reinforced, reinforced does not protect overslime
    return 150;
  }


  /* Display */

  @Nullable
  @Override
  public Boolean showDurabilityBar(IToolStackView tool, int level) {
    // only show as fully repaired if overslime is full
    return getOverslime(tool) < getCapacity(tool) ? true : null;
  }

  @Override
  public int getDurabilityRGB(IToolStackView tool, int level) {
    if (getOverslime(tool) > 0) {
      // just always display light blue, not much point in color changing really
      return 0x00D0FF;
    }
    return -1;
  }


  /* Data keys */

  @Override
  protected ResourceLocation getShieldKey() {
    return getId();
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
  public int getCapacity(IModDataView volatileData) {
    return volatileData.getInt(getCapacityKey());
  }

  /**
   * Helper to reduce code errors
   * @param tool  Tool instance
   * @return  Overslime cap
   */
  public int getCapacity(IToolStackView tool) {
    return getCapacity(tool.getVolatileData());
  }

  @Override
  protected int getShieldCapacity(IToolStackView tool, int level) {
    return getCapacity(tool);
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
  public int getOverslime(IToolStackView tool) {
    return getShield(tool);
  }

  @Override
  public void setShield(ModDataNBT persistentData, int amount) {
    super.setShield(persistentData, amount);
  }

  /**
   * Sets the overslime on a tool
   */
  public void setOverslime(IToolStackView tool, int amount) {
    setShield(tool, 0, amount); // level is unused for overslime capacity
  }

  /**
   * Adds to the overslime on a tool
   */
  public void addOverslime(IToolStackView tool, int amount) {
    // yeah, I am hardcoding overworked. If you need something similar, put in an issue request on github
    // grants +100% restoring per level
    int overworked = tool.getModifierLevel(TinkerModifiers.overworked.getId());
    addShield(tool, 0, amount * (1 + overworked));
  }
}
