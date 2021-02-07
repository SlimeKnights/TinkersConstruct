package slimeknights.tconstruct.library.modifiers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.logging.log4j.LogManager;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.ToolStatsModifierBuilder;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Random;
import java.util.function.BiConsumer;

/**
 * Interface representing both modifiers and traits.
 * Any behavior special to either one is handled elsewhere.
 */
@RequiredArgsConstructor
public class Modifier implements IForgeRegistryEntry<Modifier> {
  /** Modifier random instance, use for chance based effects */
  protected static Random RANDOM = new Random();

  private static final String KEY_LEVEL = "enchantment.level.";
  public static final int DEFAULT_PRIORITY = 100;

  /** Display color for all text for this modifier */
  @Getter
  private final int color;

  /** Registry name of this modifier, null before fully registered */
  @Getter @Nullable
  private ModifierId registryName;

  /** Cached key used for translations */
  @Nullable
  private String translationKey;
  /** Cached text component for display names */
  @Nullable
  private ITextComponent displayName;
  /** Cached text component for description */
  @Nullable
  private ITextComponent description;

  /**
   * Override this method to make your modifier run earlier or later.
   * Higher numbers run earlier, 100 is default
   * @return Priority
   */
  public int getPriority() {
    return DEFAULT_PRIORITY;
  }


  /* Registry methods */

  @Override
  public final Modifier setRegistryName(ResourceLocation name) {
    if (registryName != null) {
      throw new IllegalStateException("Attempted to set registry name with existing registry name! New: " + name + " Old: " + registryName);
    }
    // check mod container, should be the active mod
    // don't want mods registering stuff in Tinkers namespace, or Minecraft
    String activeMod = ModLoadingContext.get().getActiveNamespace();
    if (!name.getNamespace().equals(activeMod)) {
      LogManager.getLogger().info("Potentially Dangerous alternative prefix for name `{}`, expected `{}`. This could be a intended override, but in most cases indicates a broken mod.", name, activeMod);
    }
    this.registryName = new ModifierId(name);
    return this;
  }

  /**
   * Gets the modifier ID. Unlike {@link #getRegistryName()}, this method must be nonnull
   * @return  Modifier ID
   */
  public ModifierId getId() {
    return Objects.requireNonNull(registryName, "Modifier has null registry name");
  }

  @Override
  public Class<Modifier> getRegistryType() {
    return Modifier.class;
  }


  /* Tooltips */

  /**
   * Overridable method to create a translation key. Will be called once and the result cached
   * @return  Translation key
   */
  protected String makeTranslationKey() {
    return Util.makeTranslationKey("modifier", registryName);
  }

  /**
   * Gets the translation key for this modifier
   * @return  Translation key
   */
  public final String getTranslationKey() {
    if (translationKey == null) {
      translationKey = makeTranslationKey();
    }
    return translationKey;
  }

  /**
   * Overridable method to create the display name for this modifier, ideal to modify colors
   * @return  Display name
   */
  protected ITextComponent makeDisplayName() {
    return new TranslationTextComponent(getTranslationKey());
  }

  /**
   * Gets the display name for this modifier
   * @return  Display name for this modifier
   */
  public final ITextComponent getDisplayName() {
    if (displayName == null) {
      displayName = new TranslationTextComponent(getTranslationKey()).modifyStyle(style -> style.setColor(Color.fromInt(color)));
    }
    return displayName;
  }

  /**
   * Gets the display name for the given level of this modifier
   * @param level  Modifier level
   * @return  Display name
   */
  public ITextComponent getDisplayName(int level) {
    return new TranslationTextComponent(getTranslationKey())
      .appendString(" ")
      .append(new TranslationTextComponent(KEY_LEVEL + level))
      .modifyStyle(style -> style.setColor(Color.fromInt(color)));
  }

  /**
   * Stack sensitive version of {@link #getDisplayName(int)}. Useful for displaying persistent data such as overslime or redstone amount
   * @param tool   Tool instance
   * @param level  Tool level
   * @return  Stack sensitive display name
   */
  public ITextComponent getDisplayName(IModifierToolStack tool, int level) {
    return getDisplayName(level);
  }

  /**
   * Gets the description for this modifier
   * @return  Description for this modifier
   */
  public final ITextComponent getDescription() {
    if (description == null) {
      description = new TranslationTextComponent(getTranslationKey() + ".description");
    }
    return description;
  }


  /* Tool building hooks */

  /**
   * Adds any relevant volatile data to the tool data. This data is rebuilt every time modifiers rebuild.
   * <br>
   * Alternatives:
   * <ul>
   *   <li>Persistent mod data (accessed via {@link IModifierToolStack}): Can be written to freely, but will not automatically remove if the modifier is removed.</li>
   * </ul>
   * @param persistentData  Extra modifier NBT. Note that if you rely on a value in persistent data, it is up to you to ensure tool stats refresh if it changes
   * @param level           Modifier level
   * @param volatileData    Mutable mod NBT data, result of this method
   */
  public void addVolatileData(IModDataReadOnly persistentData, int level, ModDataNBT volatileData) {}

  /**
   * Adds raw stats to the tool. Called whenever tool stats are rebuilt.
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link #addAttributes(IModifierToolStack, int, BiConsumer)}: Allows dynamic stats based on any tool stat, but does not support mining speed, mining level, or durability.</li>
   *   <li>{@link #onBreakSpeed(IModifierToolStack, int, BreakSpeed)}: Allows dynamic mining speed based on the block mined and the entity mining. Will not show in tooltips.</li>
   * </ul>
   * @param persistentData  Extra modifier NBT. Note that if you rely on a value in persistent data, it is up to you to ensure tool stats refresh if it changes
   * @param volatileData    Modifier NBT calculated from modifiers in {@link #addVolatileData(IModDataReadOnly, int, ModDataNBT)}
   * @param level           Modifier level
   * @param builder         Tool stat builder
   */
  public void addToolStats(IModDataReadOnly persistentData, IModDataReadOnly volatileData, int level, ToolStatsModifierBuilder builder) {}

  /**
   * Adds enchantments from this modifier's effect. Used when an enchantment must be used for compatibility in vanilla, in general try to use a different hook.
   * @param persistentData  Extra modifier NBT. Note that if you rely on a value in persistent data, it is up to you to ensure tool stats refresh if it changes
   * @param volatileData    Modifier NBT calculated from modifiers in {@link #addVolatileData(IModDataReadOnly, int, ModDataNBT)}
   * @param level           Modifier level
   * @param consumer  Consumer accepting any enchantments
   */
  public void addEnchantments(IModDataReadOnly persistentData, IModDataReadOnly volatileData, int level, BiConsumer<Enchantment, Integer> consumer) {}

  /**
   * Adds attributes from this modifier's effect. Called whenever the item stack refreshes capabilities.
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link #addToolStats(IModDataReadOnly, IModDataReadOnly, int, ToolStatsModifierBuilder)}: Limited context, but can affect durability, mining level, and mining speed.</li>
   * </ul>
   * @param tool      Current tool instance
   * @param level     Modifier level
   * @param consumer  Attribute consumer
   */
  public void addAttributes(IModifierToolStack tool, int level, BiConsumer<Attribute,AttributeModifier> consumer) {}

  /**
   * Called when modifiers or tool materials change to validate the tool. You are free to modify persistent data in this hook if needed.
   * Do not validate max level here, simply ignore levels over max if needed.
   * @param tool   Current tool instance
   * @param level  Modifier level
   * @return  PASS result if success, failure if there was an error.
   */
  public ValidatedResult validate(ToolStack tool, int level) {
    return ValidatedResult.PASS;
  }

  /* Hooks */

  /**
   * Called when the tool is damaged. Can be used to cancel, decrease, or increase the damage.
   * @param toolStack  Tool stack
   * @param level      Tool level
   * @param amount     Amount of damage to deal
   * @return  Replacement damage. Returning 0 cancels the damage and stops other modifiers from processing.
   */
  public int onDamageTool(IModifierToolStack toolStack, int level, int amount) {
    return amount;
  }

  /**
   * Called when the tool is repair. Can be used to decrease, increase, or cancel the repair.
   * Note canceling it may give unexpected behavior at the tool station.
   * @param toolStack  Tool stack
   * @param level      Tool level
   * @param amount     Amount of damage to deal
   * @return  Replacement damage. Returning 0 cancels the repair and stops other modifiers from processing.
   */
  public int onRepairTool(IModifierToolStack toolStack, int level, int amount) {
    return amount;
  }

  /**
   * Called when the stack updates in the player inventory
   * @param tool           Current tool instance
   * @param level          Modifier level
   * @param world          World containing tool
   * @param holder         Entity holding tool
   * @param itemSlot       Slot containing this tool
   * @param isSelected     If true, this item is currently in the player's main hand
   * @param isCorrectSlot  If true, this item is in the proper slot. For tools, that is main hand or off hand. For armor, this means its in the correct armor slot
   * @param stack          Item stack instance to check other slots for the tool. Do not modify
   */
  public void onInventoryTick(IModifierToolStack tool, int level, World world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {}


  /* Harvest hooks */

  /**
   * Called when break speed is being calculated to affect mining speed conditionally.
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link #addToolStats(IModDataReadOnly, IModDataReadOnly, int, ToolStatsModifierBuilder)}: Limited context, but effect shows in the tooltip.</li>
   *   <li>{@link #onBlockBreak(IModifierToolStack, int, BreakEvent)}: Can directly prevent block breaking instead of just change breaking speed. Called just once per block break</li>
   * </ul>
   * @param tool   Current tool instance
   * @param level  Modifier level
   * @param event  Event instance
   */
  public void onBreakSpeed(IModifierToolStack tool, int level, BreakSpeed event) {}

  /**
   * Called before a block is broken. Can be used to modify block XP or as a last chance to prevent block breaking.
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link #onBreakSpeed(IModifierToolStack, int, BreakSpeed)}: Make the block break slower instead of outright canceling it.</li>
   *   <li>{@link #afterBlockBreak(IModifierToolStack, int, World, BlockState, BlockPos, LivingEntity, boolean)}: Fired after we know the block broke successfully, to perform extra effects.</li>
   * </ul>
   * @param tool   Tool used
   * @param level  Modifier level
   * @param event  Break event
   */
  public void onBlockBreak(IModifierToolStack tool, int level, BreakEvent event) {}

  /**
   * Called after a block is broken
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link #onBlockBreak(IModifierToolStack, int, BreakEvent)}: Can be used to prevent block breaking.</li>
   * </ul>
   * @param tool          Tool used
   * @param level         Modifier level
   * @param world         World instance
   * @param state         Block broken
   * @param pos           Position broken
   * @param living        Entity breaking the block
   * @param wasEffective  If true, tool was effective at breaking this block
   */
  public void afterBlockBreak(IModifierToolStack tool, int level, World world, BlockState state, BlockPos pos, LivingEntity living, boolean wasEffective) {}


  /* Attack hooks */

  /**
   * Called when a living entity is attacked, before critical hit damage is calculated. Allows modifying the damage dealt.
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link #addToolStats(IModDataReadOnly, IModDataReadOnly, int, ToolStatsModifierBuilder)}: Adjusts the base tool stats that show in the tooltip, but has less context for modification</li>
   *   <li>{@link #afterLivingHit(IModifierToolStack, int, LivingEntity, LivingEntity, float, boolean, boolean)}: Perform special attacks on entity hit beyond damage boosts</li>
   * </ul>
   * @param tool          Tool used to attack
   * @param level         Modifier level
   * @param attacker      Entity doing the attacking
   * @param target        Entity being attacked
   * @param baseDamage    Base damage dealt before modifiers
   * @param damage        Computed damage from all prior modifiers
   * @param isCritical    If true, this attack is a critical hit
   * @param fullyCharged  If true, this attack was fully charged (could perform a sword sweep)
   * @return  New damage to deal
   */
  public float applyLivingDamage(IModifierToolStack tool, int level, LivingEntity attacker, LivingEntity target, float baseDamage, float damage, boolean isCritical, boolean fullyCharged) {
    return damage;
  }

  /**
   * Called when a living entity is attacked. Used to calculate the knockback this attack will do. Damage is final damage including critical damage.
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link #afterLivingHit(IModifierToolStack, int, LivingEntity, LivingEntity, float, boolean, boolean)}: Perform special attacks on entity hit beyond knockback boosts</li>
   * </ul>
   * @param tool           Tool used to attack
   * @param level          Modifier level
   * @param attacker       Entity doing the attacking
   * @param target         Entity being attacked
   * @param damage         Damage to deal to the attacker
   * @param baseKnockback  Base knockback before modifiers
   * @param knockback      Computed knockback from all prior modifiers
   * @param isCritical     If true, this attack is a critical hit
   * @param fullyCharged   If true, this attack was fully charged (could perform a sword sweep)
   * @return  New knockback to apply
   */
  public float applyLivingKnockback(IModifierToolStack tool, int level, LivingEntity attacker, LivingEntity target, float damage, float baseKnockback, float knockback, boolean isCritical, boolean fullyCharged) {
    return knockback;
  }

  /**
   * Called after a living entity is successfully attacked. Used to apply special effects on hit.
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link #addToolStats(IModDataReadOnly, IModDataReadOnly, int, ToolStatsModifierBuilder)}: Adjusts the base tool stats that affect damage</li>
   *   <li>{@link #applyLivingDamage(IModifierToolStack, int, LivingEntity, LivingEntity, float, float, boolean, boolean)}: Change the amount of damage dealt with attacker context</li>
   *   <li>{@link #applyLivingKnockback(IModifierToolStack, int, LivingEntity, LivingEntity, float, float, float, boolean, boolean)}: Change the amount of knockback dealt</li>
   * </ul>
   * @param tool          Tool used to attack
   * @param level         Modifier level
   * @param attacker      Entity doing the attacking
   * @param target        Entity being attacked
   * @param damageDealt   Amount of damage successfully dealt
   * @param isCritical    If true, this attack is a critical hit
   * @param fullyCharged  If true, this attack was fully charged (could perform a sword sweep)
   * @return  Extra damage to deal to the tool
   */
  public int afterLivingHit(IModifierToolStack tool, int level, LivingEntity attacker, LivingEntity target, float damageDealt, boolean isCritical, boolean fullyCharged) {
    return 0;
  }


  /* Display */

  /**
   * Determines if the modifier should display
   * @param advanced  If true, in an advanced view such as the tinker station. False for tooltips
   * @return  True if the modifier should show
   */
  public boolean shouldDisplay(boolean advanced) {
    return true;
  }

  /**
   * Gets the damage percentage for display.  First tool returning something other than NaN will determine display durability
   * @param tool   Tool instance
   * @param level  Modifier level
   * @return  Damage percentage. 0 is undamaged, 1 is fully damaged.
   */
  public double getDamagePercentage(IModifierToolStack tool, int level) {
    return Double.NaN;
  }

  /**
   * Override the default tool logic for showing the durability bar
   * @param tool   Tool instance
   * @param level  Modifier level
   * @return  True forces the bar to show, false forces it to hide. Return null to allow default behavior
   */
  @Nullable
  public Boolean showDurabilityBar(IModifierToolStack tool, int level) {
    return null;
  }

  /**
   * Gets the RGB for the durability bar
   * @param tool   Tool instance
   * @param level  Modifier level
   * @return  RGB, or -1 to not handle it
   */
  public int getDurabilityRGB(IModifierToolStack tool, int level) {
    return -1;
  }
}
