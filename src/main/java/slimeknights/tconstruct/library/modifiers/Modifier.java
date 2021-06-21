package slimeknights.tconstruct.library.modifiers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.UseAction;
import net.minecraft.loot.LootContext;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectUtils;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.logging.log4j.LogManager;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiConsumer;

/**
 * Interface representing both modifiers and traits.
 * Any behavior special to either one is handled elsewhere.
 */
@RequiredArgsConstructor
public class Modifier implements IForgeRegistryEntry<Modifier> {
  private static final AttributeModifier ANTI_KNOCKBACK_MODIFIER = new AttributeModifier(TConstruct.modID + ".anti_knockback", 1f, Operation.ADDITION);

  /** Modifier random instance, use for chance based effects */
  protected static Random RANDOM = new Random();

  protected static final String KEY_LEVEL = "enchantment.level.";
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
  private List<ITextComponent> descriptionList;
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
   * Applies relevant text styles (typically color) to the modifier text
   * @param component  Component to modifiy
   * @return  Resulting component
   */
  public IFormattableTextComponent applyStyle(IFormattableTextComponent component) {
      return component.modifyStyle(style -> style.setColor(Color.fromInt(color)));
  }

  /**
   * Gets the display name for this modifier
   * @return  Display name for this modifier
   */
  public final ITextComponent getDisplayName() {
    if (displayName == null) {
      displayName = new TranslationTextComponent(getTranslationKey()).modifyStyle(style -> style.setColor(Color.fromInt(getColor())));
    }
    return displayName;
  }

  /**
   * Gets the display name for the given level of this modifier
   * @param level  Modifier level
   * @return  Display name
   */
  public ITextComponent getDisplayName(int level) {
    return applyStyle(new TranslationTextComponent(getTranslationKey())
                        .appendString(" ")
                        .append(new TranslationTextComponent(KEY_LEVEL + level)));
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
   * Adds additional information from the modifier to the tooltip. Shown when holding shift on a tool, or in the stats area of the tinker station
   * @param tool      Tool instance
   * @param level     Tool level
   * @param tooltip   Tooltip
   * @param isAdvanced  Tooltip flag type
   * @param detailed  If true, showing detailed view, such as in the tinker station
   */
  public void addInformation(IModifierToolStack tool, int level, List<ITextComponent> tooltip, boolean isAdvanced, boolean detailed) {}

  /**
   * Gets the description for this modifier
   * @return  Description for this modifier
   */
  public final List<ITextComponent> getDescriptionList() {
    if (descriptionList == null) {
      descriptionList = Arrays.asList(
        new TranslationTextComponent(getTranslationKey() + ".flavor").mergeStyle(TextFormatting.ITALIC),
        new TranslationTextComponent(getTranslationKey() + ".description"));
    }
    return descriptionList;
  }

  /**
   * Gets the description for this modifier
   * @return  Description for this modifier
   */
  public final ITextComponent getDescription() {
    if (description == null) {
      description = getDescriptionList().stream()
                                        .reduce((c1, c2) -> new StringTextComponent("").append(c1).appendString("\n").append(c2))
                                        .orElse(StringTextComponent.EMPTY);
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
   * @param toolDefinition  Tool definition, will be empty for non-multitools
   * @param baseStats       Base material stats. Does not take tool definition or other modifiers into account. Not stored, so if you want any data store it in volatile data
   * @param persistentData  Extra modifier NBT. Note that if you rely on a value in persistent data, it is up to you to ensure tool stats refresh if it changes
   * @param level           Modifier level
   * @param volatileData    Mutable mod NBT data, result of this method
   */
  public void addVolatileData(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, int level, ModDataNBT volatileData) {}

  /**
   * Adds raw stats to the tool. Called whenever tool stats are rebuilt.
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link #addAttributes(IModifierToolStack, int, BiConsumer)}: Allows dynamic stats based on any tool stat, but does not support mining speed, mining level, or durability.</li>
   *   <li>{@link #onBreakSpeed(IModifierToolStack, int, BreakSpeed, Direction, boolean, float)}: Allows dynamic mining speed based on the block mined and the entity mining. Will not show in tooltips.</li>
   * </ul>
   * @param toolDefinition  Tool definition, will be empty for non-multitools
   * @param baseStats       Base material stats. Does not take tool definition or other modifiers into account
   * @param persistentData  Extra modifier NBT. Note that if you rely on a value in persistent data, it is up to you to ensure tool stats refresh if it changes
   * @param volatileData    Modifier NBT calculated from modifiers in {@link #addVolatileData(ToolDefinition, StatsNBT, IModDataReadOnly, int, ModDataNBT)}
   * @param level           Modifier level
   * @param builder         Tool stat builder
   */
  public void addToolStats(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, IModDataReadOnly volatileData, int level, ModifierStatsBuilder builder) {}

  /**
   * Adds attributes from this modifier's effect. Called whenever the item stack refreshes capabilities.
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link #addToolStats(ToolDefinition, StatsNBT, IModDataReadOnly, IModDataReadOnly, int, ModifierStatsBuilder)}: Limited context, but can affect durability, mining level, and mining speed.</li>
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
  public ValidatedResult validate(IModifierToolStack tool, int level) {
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
   * @param toolStack  Tool stack
   * @param level      Tool level
   * @param factor     Original factor
   * @return  Replacement factor. Returning 0 prevents repair
   */
  public float getRepairFactor(IModifierToolStack toolStack, int level, float factor) {
    return factor;
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

  /**
   * Called on entity or block loot to allow modifying loot
   * @param tool           Current tool instance
   * @param level          Modifier level
   * @param generatedLoot  Current loot list before this modifier
   * @param context        Full loot context
   * @return  Loot replacement
   */
  public List<ItemStack> processLoot(IModifierToolStack tool, int level, List<ItemStack> generatedLoot, LootContext context) {
    return generatedLoot;
  }


  /* Interaction hooks */

  /**
   * Called when this item is used when targeting a block, <i>before</i> the block is activated.
   * In general it is better to use {@link #afterBlockUse(IModifierToolStack, int, ItemUseContext)} for consistency with vanilla items.
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link #onEntityUse(IModifierToolStack, int, PlayerEntity, LivingEntity, Hand)}: Processes use actions on entities.</li>
   *   <li>{@link #afterBlockUse(IModifierToolStack, int, ItemUseContext)}: Runs after the block is activated, preferred hook. </li>
   *   <li>{@link #onToolUse(IModifierToolStack, int, World, PlayerEntity, Hand)}: Processes any use actions, but runs later than onBlockUse or onEntityUse.</li>
   * </ul>
   * @param tool           Current tool instance
   * @param level          Modifier level
   * @param context        Full item use context
   * @return  Return PASS or FAIL to allow vanilla handling, any other to stop later modifiers from running.
   */
  public ActionResultType beforeBlockUse(IModifierToolStack tool, int level, ItemUseContext context) {
    return ActionResultType.PASS;
  }


  /**
   * Called when this item is used when targeting a block, <i>after</i> the block is activated. This is the perferred hook for block based tool interactions
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link #onEntityUse(IModifierToolStack, int, PlayerEntity, LivingEntity, Hand)}: Processes use actions on entities.</li>
   *   <li>{@link #beforeBlockUse(IModifierToolStack, int, ItemUseContext)}: Runs before the block is activated, can be used to prevent block interaction entirely but less consistent with vanilla </li>
   *   <li>{@link #onToolUse(IModifierToolStack, int, World, PlayerEntity, Hand)}: Processes any use actions, but runs later than onBlockUse or onEntityUse.</li>
   * </ul>
   * @param tool           Current tool instance
   * @param level          Modifier level
   * @param context        Full item use context
   * @return  Return PASS or FAIL to allow vanilla handling, any other to stop later modifiers from running.
   */
  public ActionResultType afterBlockUse(IModifierToolStack tool, int level, ItemUseContext context) {
    return ActionResultType.PASS;
  }

  /**
    * Called when this item is used when targeting an entity.
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link #afterBlockUse(IModifierToolStack, int, ItemUseContext)}: Processes use actions on blocks.</li>
   *   <li>{@link #onToolUse(IModifierToolStack, int, World, PlayerEntity, Hand)}: Processes any use actions, but runs later than onBlockUse or onEntityUse.</li>
   * </ul>
   * @param tool           Current tool instance
   * @param level          Modifier level
   * @param player         Player holding tool
   * @param target         Target
   * @param hand           Current hand
   * @return  Return PASS or FAIL to allow vanilla handling, any other to stop later modifiers from running.
   */
  public ActionResultType onEntityUse(IModifierToolStack tool, int level, PlayerEntity player, LivingEntity target, Hand hand) {
    return ActionResultType.PASS;
  }

  /**
    * Called when this item is used, after all other hooks PASS.
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link #afterBlockUse(IModifierToolStack, int, ItemUseContext)}: Processes use actions on blocks.</li>
   *   <li>{@link #onEntityUse(IModifierToolStack, int, PlayerEntity, LivingEntity, Hand)}: Processes use actions on entities.</li>
   * </ul>
   * @param tool           Current tool instance
   * @param level          Modifier level
   * @param world          World containing tool
   * @param player         Player holding tool
   * @param hand           Current hand
   * @return  Return PASS or FAIL to allow vanilla handling, any other to stop later modifiers from running.
   */
  public ActionResultType onToolUse(IModifierToolStack tool, int level, World world, PlayerEntity player, Hand hand) {
    return ActionResultType.PASS;
  }

  /**
   * Called when the player stops using the tool.
   * To setup, use {@link LivingEntity#setActiveHand(Hand)} in {@link #onToolUse(IModifierToolStack, int, World, PlayerEntity, Hand)}.
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link #onFinishUsing(IModifierToolStack, int, World, LivingEntity)}: Called when the duration timer reaches the end, even if still held
   *  </ul>
   * @param tool           Current tool instance
   * @param level          Modifier level
   * @param world          World containing tool
   * @param entity         Entity holding tool
   * @param timeLeft       How many ticks of use duration was left
  * @return  Whether the modifier should block any incoming ones from firing
  */
  public boolean onStoppedUsing(IModifierToolStack tool, int level, World world, LivingEntity entity, int timeLeft) {
    return false;
  }

  /**
   * Called when the use duration on this tool reaches the end.
   * To setup, use {@link LivingEntity#setActiveHand(Hand)} in {@link #onToolUse(IModifierToolStack, int, World, PlayerEntity, Hand)} and set the duration in {@link #getUseDuration(IModifierToolStack, int)}
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link #onStoppedUsing(IModifierToolStack, int, World, LivingEntity, int)}: Called when the player lets go before the duration reaches the end
   * </ul>
   * @param tool           Current tool instance
   * @param level          Modifier level
   * @param world          World containing tool
   * @param entity         Entity holding tool
   * @return  Whether the modifier should block any incoming ones from firing
   */
  public boolean onFinishUsing(IModifierToolStack tool, int level, World world, LivingEntity entity) {
    return false;
  }

  /**
   * @param tool           Current tool instance
   * @param level          Modifier level
  * @return  For how many ticks the modifier should run its use action
  */
  public int getUseDuration(IModifierToolStack tool, int level) {
     return 0;
  }

  /**
   * @param tool           Current tool instance
   * @param level          Modifier level
  * @return  Use action to be performed
  */
  public UseAction getUseAction(IModifierToolStack tool, int level) {
     return UseAction.NONE;
  }


  /* Harvest hooks */

  /**
   * Called when break speed is being calculated to affect mining speed conditionally.
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link #addToolStats(ToolDefinition, StatsNBT, IModDataReadOnly, IModDataReadOnly, int, ModifierStatsBuilder)}: Limited context, but effect shows in the tooltip.</li>
   * </ul>
   * @param tool                 Current tool instance
   * @param level                Modifier level
   * @param event                Event instance
   * @param sideHit              Side of the block that was hit
   * @param isEffective          If true, the tool is effective against this block type
   * @param miningSpeedModifier  Calculated modifier from potion effects such as haste and environment such as water, use for additive bonuses to ensure consistency with the mining speed stat
   */
  public void onBreakSpeed(IModifierToolStack tool, int level, BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) {}

  /**
   * Adds harvest loot table related enchantments from this modifier's effect, called before breaking a block.
   * Needed to add enchantments for silk touch and fortune. Can add conditionally if needed.
   * For looting, see {@link #getLootingValue(IModifierToolStack, int, LivingEntity, LivingEntity, DamageSource, int)}
   * @param tool      Tool used
   * @param level     Modifier level
   * @param player    Player holding this tool
   * @param state     Block being harvested
   * @param pos       Position of block being harvested
   * @param sideHit   Side of the block that was hit
   * @param consumer  Consumer accepting any enchantments
   */
  public void applyHarvestEnchantments(IModifierToolStack tool, int level, PlayerEntity player, BlockState state, BlockPos pos, Direction sideHit, BiConsumer<Enchantment,Integer> consumer) {}

  /**
   * Gets the amount of luck contained in this tool
   * @param tool          Tool instance
   * @param level         Modifier level
   * @param holder        Entity holding the tool
   * @param target        Entity being looted
   * @param damageSource  Damage source that killed the entity. May be null if this hook is called without attacking anything (e.g. shearing)
   * @param looting          Luck value set from previous modifiers
   * @return New luck value
   */
  public int getLootingValue(IModifierToolStack tool, int level, LivingEntity holder, LivingEntity target, @Nullable DamageSource damageSource, int looting) {
    return looting;
  }

  /**
   * Removes the block from the world
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link #afterBlockBreak(IModifierToolStack, int, World, BlockState, BlockPos, LivingEntity, boolean, boolean, boolean)}: Called after the block is successfully removed.</li>
   * </ul>
   * @param tool         Tool used
   * @param level        Modifier level
   * @param player       Player breaking the block
   * @param world        World instance
   * @param pos          Position targeted
   * @param state        State being broken
   * @param canHarvest   If true, the block will drop its drops
   * @param isEffective  If true, the player can canHarvest the block. False is typically creative
   * @param isAOEBlock   If true, this block was broken by AOE harvesting. False is the block directly targeted by the tool
   * @return  True to override the default block removing logic and stop all later modifiers from running. False to override default without breaking the block. Null to let default logic run
   */
  @Nullable
  public Boolean removeBlock(IModifierToolStack tool, int level, PlayerEntity player, World world, BlockPos pos, BlockState state, boolean canHarvest, boolean isEffective, boolean isAOEBlock) {
    return null;
  }

  /**
   * Called after a block is broken to apply special effects
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link #removeBlock(IModifierToolStack, int, PlayerEntity, World, BlockPos, BlockState, boolean, boolean, boolean)}: Called before the block is set to air.</li>
   * </ul>
   * @param tool          Tool used
   * @param level         Modifier level
   * @param world         World instance
   * @param state         Block broken
   * @param pos           Position broken
   * @param living        Entity breaking the block
   * @param canHarvest    If true, the block dropped items
   * @param wasEffective  If true, tool was effective at breaking this block
   * @param isAOEBlock    If true, this block was broken by AOE harvesting. False is the block directly targeted by the tool
   */
  public void afterBlockBreak(IModifierToolStack tool, int level, World world, BlockState state, BlockPos pos, LivingEntity living, boolean canHarvest, boolean wasEffective, boolean isAOEBlock) {}


  /* Attack hooks */

  /**
   * Called when a living entity is attacked, before critical hit damage is calculated. Allows modifying the damage dealt. Do not modify the entity here, its possible the attack will still be canceled
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link #addToolStats(ToolDefinition, StatsNBT, IModDataReadOnly, IModDataReadOnly, int, ModifierStatsBuilder)}: Adjusts the base tool stats that show in the tooltip, but has less context for modification</li>
   *   <li>{@link #afterLivingHit(IModifierToolStack, int, LivingEntity, LivingEntity, float, boolean, float, boolean)}: Perform special attacks on entity hit beyond damage boosts</li>
   *   <li>{@link #beforeLivingHit(IModifierToolStack, int, LivingEntity, LivingEntity, float, float, float, boolean, boolean, boolean)}: Apply effects that must run before hit</li>
   * </ul>
   * @param tool          Tool used to attack
   * @param level         Modifier level
   * @param attacker      Entity doing the attacking
   * @param target        Entity being attacked
   * @param baseDamage    Base damage dealt before modifiers
   * @param damage        Computed damage from all prior modifiers
   * @param isCritical    If true, this attack is a critical hit
   * @param fullyCharged  If true, this attack was fully charged (could perform a sword sweep)
   * @param isExtraAttack If true, this attack is targeting a secondary entity, such as through the scythe sweep attack
   * @return  New damage to deal
   */
  public float applyLivingDamage(IModifierToolStack tool, int level, LivingEntity attacker, LivingEntity target, float baseDamage, float damage, boolean isCritical, boolean fullyCharged, boolean isExtraAttack) {
    return damage;
  }

  /**
   * Called right before an entity is hit, used to modify knockback applied or to apply special effects that need to run before damage. Damage is final damage including critical damage.
   * Note there is still a chance this attack won't deal damage, if that happens {@link #failedLivingHit(IModifierToolStack, int, LivingEntity, LivingEntity, boolean, boolean, boolean)} will run.
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link #afterLivingHit(IModifierToolStack, int, LivingEntity, LivingEntity, float, boolean, float, boolean)}: Perform special attacks on entity hit beyond knockback boosts</li>
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
   * @param isExtraAttack If true, this attack is targeting a secondary entity, such as through the scythe sweep attack
   * @return  New knockback to apply. 0.5 is equivelent to 1 level of the vanilla enchant
   */
  public float beforeLivingHit(IModifierToolStack tool, int level, LivingEntity attacker, LivingEntity target, float damage, float baseKnockback, float knockback, boolean isCritical, boolean fullyCharged, boolean isExtraAttack) {
    return knockback;
  }

  /**
   * Called after a living entity is successfully attacked. Used to apply special effects on hit.
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link #addToolStats(ToolDefinition, StatsNBT, IModDataReadOnly, IModDataReadOnly, int, ModifierStatsBuilder)}: Adjusts the base tool stats that affect damage</li>
   *   <li>{@link #applyLivingDamage(IModifierToolStack, int, LivingEntity, LivingEntity, float, float, boolean, boolean, boolean)}: Change the amount of damage dealt with attacker context</li>
   *   <li>{@link #beforeLivingHit(IModifierToolStack, int, LivingEntity, LivingEntity, float, float, float, boolean, boolean, boolean)}: Change the amount of knockback dealt</li>
   *   <li>{@link #failedLivingHit(IModifierToolStack, int, LivingEntity, LivingEntity, boolean, boolean, boolean)}: Called after living hit when damage was not dealt</li>
   * </ul>
   * @param tool          Tool used to attack
   * @param level         Modifier level
   * @param attacker      Entity doing the attacking
   * @param target        Entity being attacked
   * @param damageDealt   Amount of damage successfully dealt
   * @param isCritical    If true, this attack is a critical hit
   * @param cooldown      Current attack cooldown
   * @param isExtraAttack If true, this attack is targeting a secondary entity, such as through the scythe sweep attack
   * @return  Extra damage to deal to the tool
   */
  public int afterLivingHit(IModifierToolStack tool, int level, LivingEntity attacker, LivingEntity target, float damageDealt, boolean isCritical, float cooldown, boolean isExtraAttack) {
    return 0;
  }

  /**
   * Called after attacking an entity when no damage was dealt
   * @param tool          Tool used to attack
   * @param level         Modifier level
   * @param attacker      Entity doing the attacking
   * @param target        Entity being attacked
   * @param isCritical    If true, this attack is a critical hit
   * @param fullyCharged  If true, this attack was fully charged (could perform a sword sweep)
   * @param isExtraAttack If true, this attack is targeting a secondary entity, such as through the scythe sweep attack
   */
  public void failedLivingHit(IModifierToolStack tool, int level, LivingEntity attacker, LivingEntity target, boolean isCritical, boolean fullyCharged, boolean isExtraAttack) {}


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


  /* Modules */

  /**
   * Gets a submodule of this modifier.
   *
   * Submodules will contain tool stack sensitive hooks, and do not contain storage. Generally returning the same instance each time is preferred.
   * @param type  Module type to fetch
   * @param <T>   Module return type
   * @return  Module, or null if the module is not contained
   */
  @Nullable
  public <T> T getModule(Class<T> type) {
    return null;
  }


  /* Utils */

  /**
   * Adds secondary damage to an entity
   * @param source       Damage source
   * @param damage       Damage amount
   * @param target       Target
   * @param noKnockback  If true, prevents extra knockback
   * @return  True if damaged
   */
  public static boolean attackEntitySecondary(DamageSource source, float damage, LivingEntity target, boolean noKnockback) {
    Optional<ModifiableAttributeInstance> knockbackResistance = Optional.ofNullable(target.getAttribute(Attributes.KNOCKBACK_RESISTANCE))
                                                                        .filter(attribute -> !attribute.hasModifier(ANTI_KNOCKBACK_MODIFIER));
    // store last damage before secondary attack
    float oldLastDamage = target.lastDamage;

    // prevent knockback in secondary attacks, if requested
    if (noKnockback) {
      knockbackResistance.ifPresent(attribute -> attribute.applyNonPersistentModifier(ANTI_KNOCKBACK_MODIFIER));
    }

    // set hurt resistance time to 0 because we always want to deal damage in traits
    target.hurtResistantTime = 0;
    boolean hit = target.attackEntityFrom(source, damage);
    // set total received damage, important for AI and stuff
    target.lastDamage += oldLastDamage;

    // remove no knockback marker
    if (noKnockback) {
      knockbackResistance.ifPresent(attribute -> attribute.removeModifier(ANTI_KNOCKBACK_MODIFIER));
    }

    return hit;
  }

  /**
   * Gets the tool stack from the given entities mainhand. Useful for specialized event handling in modifiers
   * @param living  Entity instance
   * @return  Tool stack
   */
  @Nullable
  public static ToolStack getHeldTool(@Nullable LivingEntity living) {
    if (living == null) {
      return null;
    }
    ItemStack stack = living.getHeldItemMainhand();
    if (stack.isEmpty() || !stack.getItem().isIn(TinkerTags.Items.MODIFIABLE)) {
      return null;
    }
    ToolStack tool = ToolStack.from(stack);
    return tool.isBroken() ? null : ToolStack.from(stack);
  }

  /**
   * Gets the mining speed modifier for the current conditions, notably potions and armor enchants
   * @param entity  Entity to check
   * @return  Mining speed modifier
   */
  public static float getMiningModifier(LivingEntity entity) {
    float modifier = 1.0f;
    // haste effect
    if (EffectUtils.hasMiningSpeedup(entity)) {
      modifier *= 1.0F + (EffectUtils.getMiningSpeedup(entity) + 1) * 0.2f;
    }
    // mining fatigue
    EffectInstance miningFatigue = entity.getActivePotionEffect(Effects.MINING_FATIGUE);
    if (miningFatigue != null) {
      switch(miningFatigue.getAmplifier()) {
        case 0:
          modifier *= 0.3F;
          break;
        case 1:
          modifier *= 0.09F;
          break;
        case 2:
          modifier *= 0.0027F;
          break;
        case 3:
        default:
          modifier *= 8.1E-4F;
      }
    }
    // water
    if (entity.areEyesInFluid(FluidTags.WATER) && !EnchantmentHelper.hasAquaAffinity(entity)) {
      modifier /= 5.0F;
    }
    if (!entity.isOnGround()) {
      modifier /= 5.0F;
    }
    return modifier;
  }
}
