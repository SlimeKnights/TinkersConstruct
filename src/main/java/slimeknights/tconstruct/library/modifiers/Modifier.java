package slimeknights.tconstruct.library.modifiers;

import com.google.gson.JsonObject;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.mantle.client.ResourceColorManager;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.GenericLoaderRegistry.IHaveLoader;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierManager.ModifierRegistrationEvent;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap.Builder;
import slimeknights.tconstruct.library.modifiers.util.ModifierLevelDisplay;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.RestrictedCompoundTag;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.BiConsumer;

/**
 * Interface representing both modifiers and traits.
 * Any behavior special to either one is handled elsewhere.
 */
@SuppressWarnings("unused")
public class Modifier implements IHaveLoader<Modifier> {
  /** Default loader instance for a modifier with no properties */
  public static final IGenericLoader<Modifier> DEFAULT_LOADER = new IGenericLoader<>() {
    @Override
    public Modifier deserialize(JsonObject json) {
      return new Modifier();
    }

    @Override
    public Modifier fromNetwork(FriendlyByteBuf buffer) {
      return new Modifier();
    }

    @Override
    public void serialize(Modifier object, JsonObject json) {
      if (object.getClass() != Modifier.class) {
        throw new IllegalStateException("Attempting to serialize a subclass of Modifier using the default modifier loader, this likely means the modifier did not override getLoader()");
      }
    }

    @Override
    public void toNetwork(Modifier object, FriendlyByteBuf buffer) {}
  };

  /** Modifier random instance, use for chance based effects */
  protected static Random RANDOM = new Random();

  /** Priority of modfiers by default */
  public static final int DEFAULT_PRIORITY = 100;

  /** Registry name of this modifier, null before fully registered */
  private ModifierId id;

  /** Cached key used for translations */
  @Nullable
  private String translationKey;
  /** Cached text component for display names */
  @Nullable
  private Component displayName;
  /** Cached text component for description */
  @Nullable
  protected List<Component> descriptionList;
  /** Cached text component for description */
  @Nullable
  private Component description;
  /** Map of all modifier hooks registered to this modifier */
  @Getter
  private final ModifierHookMap hooks;

  /** Creates a new modifier using the given hook map */
  protected Modifier(ModifierHookMap hooks) {
    this.hooks = hooks;
  }

  /** Creates a new instance using the hook builder */
  public Modifier() {
    ModifierHookMap.Builder hookBuilder = new ModifierHookMap.Builder();
    registerHooks(hookBuilder);
    this.hooks = hookBuilder.build();
  }

  /**
   * Registers a hook to the modifier.
   * Note that this is run in the constructor, so you are unable to use any instance fields in this method unless initialized in this method
   */
  protected void registerHooks(ModifierHookMap.Builder hookBuilder) {}

  @Override
  public IGenericLoader<? extends Modifier> getLoader() {
    return DEFAULT_LOADER;
  }

  /**
   * Override this method to make your modifier run earlier or later.
   * Higher numbers run earlier, 100 is default
   * @return Priority
   */
  public int getPriority() {
    return DEFAULT_PRIORITY;
  }


  /* Registry methods */

  /** Sets the modifiers ID. Internal as ID is set through {@link ModifierRegistrationEvent} or the dynamic loader */
  final void setId(ModifierId name) {
    if (id != null) {
      throw new IllegalStateException("Attempted to set registry name with existing registry name! New: " + name + " Old: " + id);
    }
    this.id = name;
  }

  /**
   * Gets the modifier ID
   * @return  Modifier ID
   */
  public ModifierId getId() {
    return Objects.requireNonNull(id, "Modifier has null registry name");
  }

  /** Checks if the modifier is in the given tag */
  public final boolean is(TagKey<Modifier> tag) {
    return ModifierManager.isInTag(this.getId(), tag);
  }


  /* Tooltips */

  /**
   * Called on pack reload to clear caches
   * @param packType type of pack being reloaded
   */
  public void clearCache(PackType packType) {
    if (packType == PackType.CLIENT_RESOURCES) {
      displayName = null;
    }
  }

  /** Gets the color for this modifier */
  public final TextColor getTextColor() {
    return ResourceColorManager.getTextColor(getTranslationKey());
  }

  /** Gets the color for this modifier */
  public final int getColor() {
    return getTextColor().getValue();
  }

  /**
   * Overridable method to create a translation key. Will be called once and the result cached
   * @return  Translation key
   */
  protected String makeTranslationKey() {
    return Util.makeTranslationKey("modifier", Objects.requireNonNull(id));
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
  protected Component makeDisplayName() {
    return new TranslatableComponent(getTranslationKey());
  }

  /**
   * Applies relevant text styles (typically color) to the modifier text
   * @param component  Component to modifiy
   * @return  Resulting component
   */
  public MutableComponent applyStyle(MutableComponent component) {
      return component.withStyle(style -> style.withColor(getTextColor()));
  }

  /**
   * Gets the display name for this modifier
   * @return  Display name for this modifier
   */
  public Component getDisplayName() {
    if (displayName == null) {
      displayName = new TranslatableComponent(getTranslationKey()).withStyle(style -> style.withColor(getTextColor()));
    }
    return displayName;
  }

  /**
   * Gets the display name for the given level of this modifier
   * @param level  Modifier level
   * @return  Display name
   */
  public Component getDisplayName(int level) {
    return ModifierLevelDisplay.DEFAULT.nameForLevel(this, level);
  }

  /**
   * Stack sensitive version of {@link #getDisplayName(int)}. Useful for displaying persistent data such as overslime or redstone amount
   * @param tool   Tool instance
   * @param level  Tool level
   * @return  Stack sensitive display name
   */
  public Component getDisplayName(IToolStackView tool, int level) {
    return getDisplayName(level);
  }

  /** @deprecated use {@link slimeknights.tconstruct.library.modifiers.hook.TooltipModifierHook} */
  @Deprecated
  public void addInformation(IToolStackView tool, int level, @Nullable Player player, List<Component> tooltip, slimeknights.tconstruct.library.utils.TooltipKey tooltipKey, TooltipFlag tooltipFlag) {}

  /** @deprecated use {@link slimeknights.tconstruct.library.modifiers.hook.TooltipModifierHook} */
  @Deprecated
  public void addInformation(IToolStackView tool, int level, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    addInformation(tool, level, player, tooltip, slimeknights.tconstruct.library.utils.TooltipKey.fromMantle(tooltipKey), tooltipFlag);
  }

  /**
   * Gets the description for this modifier
   * @return  Description for this modifier
   */
  public List<Component> getDescriptionList() {
    if (descriptionList == null) {
      descriptionList = Arrays.asList(
        new TranslatableComponent(getTranslationKey() + ".flavor").withStyle(ChatFormatting.ITALIC),
        new TranslatableComponent(getTranslationKey() + ".description"));
    }
    return descriptionList;
  }

  /**
   * Gets the description for this modifier, sensitive to the tool
   * @param level Modifier level
   * @return  Description for this modifier
   */
  public List<Component> getDescriptionList(int level) {
    return getDescriptionList();
  }

  /**
   * Gets the description for this modifier, sensitive to the tool
   * @param tool  Tool containing this modifier
   * @param level Modifier level
   * @return  Description for this modifier
   */
  public List<Component> getDescriptionList(IToolStackView tool, int level) {
    return getDescriptionList(level);
  }

  /** Converts a list of text components to a single text component, newline separated */
  private static Component listToComponent(List<Component> list) {
    if (list.isEmpty()) {
      return TextComponent.EMPTY;
    }
    MutableComponent textComponent = new TextComponent("");
    Iterator<Component> iterator = list.iterator();
    textComponent.append(iterator.next());
    while (iterator.hasNext()) {
      textComponent.append("\n");
      textComponent.append(iterator.next());
    }
    return textComponent;
  }

  /**
   * Gets the description for this modifier
   * @return  Description for this modifier
   */
  public final Component getDescription() {
    if (description == null) {
      description = listToComponent(getDescriptionList());
    }
    return description;
  }

  /**
   * Gets the description for this modifier
   * @return  Description for this modifier
   */
  public final Component getDescription(int level) {
    // if the method is not overridden, use the cached description component
    List<Component> extendedDescription = getDescriptionList(level);
    if (extendedDescription == getDescriptionList()) {
      return getDescription();
    }
    return listToComponent(extendedDescription);
  }

  /**
   * Gets the description for this modifier
   * @return  Description for this modifier
   */
  public final Component getDescription(IToolStackView tool, int level) {
    // if the method is not overridden, use the cached description component
    List<Component> extendedDescription = getDescriptionList(tool, level);
    if (extendedDescription == getDescriptionList()) {
      return getDescription();
    }
    return listToComponent(extendedDescription);
  }


  /* General hooks */

  /**
   * Gets the level scaled based on attributes of modifier data. Used mainly for incremental modifiers.
   * @param tool  Tool context
   * @param level  Modifier level
   * @return  Modifier level, possibly adjusted by tool properties
   */
  public float getEffectiveLevel(IToolContext tool, int level) {
    return level;
  }


  /* Tool building hooks */

  /** @deprecated use {@link slimeknights.tconstruct.library.modifiers.hook.build.VolatileDataModifierHook} */
  @Deprecated
  public void addVolatileData(ToolRebuildContext context, int level, ModDataNBT volatileData) {}

  /** @deprecated use {@link slimeknights.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook} */
  @Deprecated
  public void addToolStats(ToolRebuildContext context, int level, ModifierStatsBuilder builder) {}

  /** @deprecated use {@link slimeknights.tconstruct.library.modifiers.hook.build.AttributesModifierHook} */
  @Deprecated
  public void addAttributes(IToolStackView tool, int level, EquipmentSlot slot, BiConsumer<Attribute,AttributeModifier> consumer) {}

  /** @deprecated use {@link slimeknights.tconstruct.library.modifiers.hook.build.RawDataModifierHook#addRawData(IToolStackView, ModifierEntry, RestrictedCompoundTag)} */
  @Deprecated
  public void addRawData(IToolStackView tool, int level, RestrictedCompoundTag tag) {}

  /** @deprecated use {@link slimeknights.tconstruct.library.modifiers.hook.build.ValidateModifierHook} */
  @Deprecated
  public ValidatedResult validate(IToolStackView tool, int level) {
    return ValidatedResult.PASS;
  }

  /** @deprecated use {@link slimeknights.tconstruct.library.modifiers.hook.build.RawDataModifierHook#removeRawData(IToolStackView, Modifier, RestrictedCompoundTag)} */
  @Deprecated
  public void beforeRemoved(IToolStackView tool, RestrictedCompoundTag tag) {}

  /** @deprecated use {@link slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook} */
  @Deprecated
  public void onRemoved(IToolStackView tool) {}


  /* Hooks */

  /**
   * Called when the tool is damaged. Can be used to cancel, decrease, or increase the damage.
   * @param tool       Tool stack
   * @param level      Tool level
   * @param amount     Amount of damage to deal
   * @param holder     Entity holding the tool
   * @return  Replacement damage. Returning 0 cancels the damage and stops other modifiers from processing.
   */
  public int onDamageTool(IToolStackView tool, int level, int amount, @Nullable LivingEntity holder) {
    return amount;
  }

  /**
   * Called when the tool is repair. Can be used to decrease, increase, or cancel the repair.
   * @param toolStack  Tool stack
   * @param level      Tool level
   * @param factor     Original factor
   * @return  Replacement factor. Returning 0 prevents repair
   */
  public float getRepairFactor(IToolStackView toolStack, int level, float factor) {
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
  public void onInventoryTick(IToolStackView tool, int level, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {}

  /**
   * Called on entity or block loot to allow modifying loot
   * @param tool           Current tool instance
   * @param level          Modifier level
   * @param generatedLoot  Current loot list before this modifier
   * @param context        Full loot context
   * @return  Loot replacement
   */
  public List<ItemStack> processLoot(IToolStackView tool, int level, List<ItemStack> generatedLoot, LootContext context) {
    return generatedLoot;
  }


  /* Interaction hooks */

  /** @deprecated use {@link slimeknights.tconstruct.library.modifiers.hook.interaction.BlockInteractionModifierHook#beforeBlockUse(IToolStackView, ModifierEntry, UseOnContext, InteractionSource)}} */
  @Deprecated
  public InteractionResult beforeBlockUse(IToolStackView tool, int level, UseOnContext context, EquipmentSlot slot) {
    return InteractionResult.PASS;
  }

  /** @deprecated use {@link slimeknights.tconstruct.library.modifiers.hook.interaction.BlockInteractionModifierHook#afterBlockUse(IToolStackView, ModifierEntry, UseOnContext, InteractionSource)}} */
  @Deprecated
  public InteractionResult afterBlockUse(IToolStackView tool, int level, UseOnContext context, EquipmentSlot slot) {
    return InteractionResult.PASS;
  }

  /** @deprecated use {@link slimeknights.tconstruct.library.modifiers.hook.interaction.EntityInteractionModifierHook#beforeEntityUse(IToolStackView, ModifierEntry, Player, Entity, InteractionHand, InteractionSource)}} */
  @Deprecated
  public InteractionResult beforeEntityUse(IToolStackView tool, int level, Player player, Entity target, InteractionHand hand, EquipmentSlot slot) {
    return InteractionResult.PASS;
  }

  /** @deprecated use {@link slimeknights.tconstruct.library.modifiers.hook.interaction.EntityInteractionModifierHook#afterEntityUse(IToolStackView, ModifierEntry, Player, LivingEntity, InteractionHand, InteractionSource)}} */
  @Deprecated
  public InteractionResult afterEntityUse(IToolStackView tool, int level, Player player, LivingEntity target, InteractionHand hand, EquipmentSlot slot) {
    return InteractionResult.PASS;
  }

  /** @deprecated use {@link slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook#onToolUse(IToolStackView, ModifierEntry, Player, InteractionHand, InteractionSource)}} */
  @Deprecated
  public InteractionResult onToolUse(IToolStackView tool, int level, Level world, Player player, InteractionHand hand, EquipmentSlot slot) {
    return InteractionResult.PASS;
  }

  /** @deprecated use {@link slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook#onStoppedUsing(IToolStackView, ModifierEntry, LivingEntity, int)}} */
  @Deprecated
  public boolean onStoppedUsing(IToolStackView tool, int level, Level world, LivingEntity entity, int timeLeft) {
    return false;
  }

  /** @deprecated use {@link slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook#onFinishUsing(IToolStackView, ModifierEntry, LivingEntity)}} */
  @Deprecated
  public boolean onFinishUsing(IToolStackView tool, int level, Level world, LivingEntity entity) {
    return false;
  }

  /** @deprecated use {@link slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook#getUseDuration(IToolStackView, ModifierEntry)}} */
  @Deprecated
  public int getUseDuration(IToolStackView tool, int level) {
     return 0;
  }

  /** @deprecated use {@link slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook#getUseAction(IToolStackView, ModifierEntry)}} */
  @Deprecated
  public UseAnim getUseAction(IToolStackView tool, int level) {
     return UseAnim.NONE;
  }

  /**
   * Checks if the tool can perform the given tool action. If any modifier returns true, the action is assumed to be present
   * @param tool        Tool to check, will never be broken
   * @param level       Modifier level
   * @param toolAction  Action to check
   * @return  True if the tool can perform the action.
   */
  public boolean canPerformAction(IToolStackView tool, int level, ToolAction toolAction) {
    return false;
  }

  /* Harvest hooks */

  /**
   * Called when break speed is being calculated to affect mining speed conditionally.
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link #addToolStats(ToolRebuildContext, int, ModifierStatsBuilder)}: Limited context, but effect shows in the tooltip.</li>
   * </ul>
   * @param tool                 Current tool instance
   * @param level                Modifier level
   * @param event                Event instance
   * @param sideHit              Side of the block that was hit
   * @param isEffective          If true, the tool is effective against this block type
   * @param miningSpeedModifier  Calculated modifier from potion effects such as haste and environment such as water, use for additive bonuses to ensure consistency with the mining speed stat
   */
  public void onBreakSpeed(IToolStackView tool, int level, BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) {}

  /**
   * Adds harvest loot table related enchantments from this modifier's effect, called before breaking a block.
   * Needed to add enchantments for silk touch and fortune. Can add conditionally if needed.
   * For looting, see {@link #getLootingValue(IToolStackView, int, LivingEntity, Entity, DamageSource, int)}
   * @param tool      Tool used
   * @param level     Modifier level
   * @param context   Harvest context
   * @param consumer  Consumer accepting any enchantments
   * @deprecated use {@link slimeknights.tconstruct.library.modifiers.hook.HarvestEnchantmentsModifierHook}
   */
  @SuppressWarnings("DeprecatedIsStillUsed")
  @Deprecated
  public void applyHarvestEnchantments(IToolStackView tool, int level, ToolHarvestContext context, BiConsumer<Enchantment,Integer> consumer) {}

  /**
   * Gets the amount of luck contained in this tool
   * @param tool          Tool instance
   * @param level         Modifier level
   * @param holder        Entity holding the tool
   * @param target        Entity being looted
   * @param damageSource  Damage source that killed the entity. May be null if this hook is called without attacking anything (e.g. shearing)
   * @param looting          Luck value set from previous modifiers
   * @return New luck value
   * @deprecated use {@link slimeknights.tconstruct.library.modifiers.hook.LootingModifierHook}
   */
  @SuppressWarnings("DeprecatedIsStillUsed")
  @Deprecated
  public int getLootingValue(IToolStackView tool, int level, LivingEntity holder, Entity target, @Nullable DamageSource damageSource, int looting) {
    return looting;
  }

  /**
   * Removes the block from the world
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link #afterBlockBreak(IToolStackView, int, ToolHarvestContext)}: Called after the block is successfully removed.</li>
   * </ul>
   * @param tool      Tool used
   * @param level     Modifier level
   * @param context   Harvest context
   * @return  True to override the default block removing logic and stop all later modifiers from running. False to override default without breaking the block. Null to let default logic run
   */
  @Nullable
  public Boolean removeBlock(IToolStackView tool, int level, ToolHarvestContext context) {
    return null;
  }

  /**
   * Called after a block is broken to apply special effects
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link #removeBlock(IToolStackView, int, ToolHarvestContext)}: Called before the block is set to air.</li>
   *   <li>{@link #finishBreakingBlocks(IToolStackView, int, ToolHarvestContext)}: Called after all blocks are broken instead of per block.</li>
   * </ul>
   * @param tool      Tool used
   * @param level     Modifier level
   * @param context   Harvest context
   */
  public void afterBlockBreak(IToolStackView tool, int level, ToolHarvestContext context) {}

  /**
   * Called after all blocks are broken on the target block
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link #afterBlockBreak(IToolStackView, int, ToolHarvestContext)}: Called after each individual block is broken.</li>
   * </ul>
   * @param tool      Tool used
   * @param level     Modifier level
   * @param context   Harvest context
   */
  public void finishBreakingBlocks(IToolStackView tool, int level, ToolHarvestContext context) {}


  /* Attack hooks */

  /**
   * Called when an entity is attacked, before critical hit damage is calculated. Allows modifying the damage dealt.
   * Do not modify the entity here, its possible the attack will still be canceled without calling further hooks due to 0 damage being dealt.
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link #addToolStats(ToolRebuildContext, int, ModifierStatsBuilder)}: Adjusts the base tool stats that show in the tooltip, but has less context for modification</li>
   *   <li>{@link #beforeEntityHit(IToolStackView, int, ToolAttackContext, float, float, float)}: If you need to modify the entity before attacking, use this hook</li>
   *   <li>{@link #afterEntityHit(IToolStackView, int, ToolAttackContext, float)}: Perform special attacks on entity hit beyond damage boosts</li>
   * </ul>
   * @param tool          Tool used to attack
   * @param level         Modifier level
   * @param context       Attack context
   * @param baseDamage    Base damage dealt before modifiers
   * @param damage        Computed damage from all prior modifiers
   * @return  New damage to deal
   */
  public float getEntityDamage(IToolStackView tool, int level, ToolAttackContext context, float baseDamage, float damage) {
    return damage;
  }

  /**
   * Called right before an entity is hit, used to modify knockback applied or to apply special effects that need to run before damage. Damage is final damage including critical damage.
   * Note there is still a chance this attack won't deal damage, if that happens {@link #failedEntityHit(IToolStackView, int, ToolAttackContext)} will run.
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link #afterEntityHit(IToolStackView, int, ToolAttackContext, float)}: Perform special attacks on entity hit beyond knockback boosts</li>
   * </ul>
   * @param tool           Tool used to attack
   * @param level          Modifier level
   * @param context        Attack context
   * @param damage         Damage to deal to the attacker
   * @param baseKnockback  Base knockback before modifiers
   * @param knockback      Computed knockback from all prior modifiers
   * @return  New knockback to apply. 0.5 is equivelent to 1 level of the vanilla enchant
   */
  public float beforeEntityHit(IToolStackView tool, int level, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
    return knockback;
  }

  /**
   * Called after a living entity is successfully attacked. Used to apply special effects on hit.
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link #addToolStats(ToolRebuildContext, int, ModifierStatsBuilder)}: Adjusts the base tool stats that affect damage</li>
   *   <li>{@link #getEntityDamage(IToolStackView, int, ToolAttackContext, float, float)}: Change the amount of damage dealt with attacker context</li>
   *   <li>{@link #beforeEntityHit(IToolStackView, int, ToolAttackContext, float, float, float)}: Change the amount of knockback dealt</li>
   *   <li>{@link #failedEntityHit(IToolStackView, int, ToolAttackContext)}: Called after living hit when damage was not dealt</li>
   * </ul>
   * @param tool          Tool used to attack
   * @param level         Modifier level
   * @param context       Attack context
   * @param damageDealt   Amount of damage successfully dealt
   * @return  Extra damage to deal to the tool
   */
  public int afterEntityHit(IToolStackView tool, int level, ToolAttackContext context, float damageDealt) {
    return 0;
  }

  /**
   * Called after attacking an entity when no damage was dealt
   * @param tool          Tool used to attack
   * @param level         Modifier level
   * @param context       Attack context
   */
  public void failedEntityHit(IToolStackView tool, int level, ToolAttackContext context) {}


  /* Armor */

  /**
   * Gets the protection value of the armor from this modifier. A value of 1 blocks about 4% of damage, equivalent to 1 level of the protection enchantment.
   * Maximum effect is 80% reduction from a modifier value of 20. Can also go negative, up to 180% increase from a modifier value of -20
   * <br/>
   * Alternatives:
   * <ul>
   *   <li>{@link #isSourceBlocked(IToolStackView, int, EquipmentContext, EquipmentSlot, DamageSource, float)}: Allows canceling the attack entirely, including the hurt animation.</li>
   *   <li>{@link #onAttacked(IToolStackView, int, EquipmentContext, EquipmentSlot, DamageSource, float, boolean)}: Allows running logic that should take place on attack, such as counterattacks.</li>
   * </ul>
   * @param tool            Worn armor
   * @param level           Modifier level
   * @param context         Equipment context of the entity wearing the armor
   * @param slotType        Slot containing the armor
   * @param source          Damage source
   * @param modifierValue   Modifier value from previous modifiers to add
   * @return  New modifier value
   */
  public float getProtectionModifier(IToolStackView tool, int level, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float modifierValue) {
    return modifierValue;
  }

  /**
   * Checks if this modifier blocks damage from the given source.
   * <br/>
   * Alternatives:
   * <ul>
   *   <li>{@link #getProtectionModifier(IToolStackView, int, EquipmentContext, EquipmentSlot, DamageSource, float)}: Allows reducing damage from a source rather than completely blocking it. Reduced damage will still play the attack animation.</li>
   *   <li>{@link #onAttacked(IToolStackView, int, EquipmentContext, EquipmentSlot, DamageSource, float, boolean)}: Allows running logic that should take place on attack, such as counterattacks.</li>
   * </ul>
   * @param tool       Tool being used
   * @param level      Level of the modifier
   * @param context    Context of entity and other equipment
   * @param slotType   Slot containing the tool
   * @param source     Damage source causing the attack
   * @param amount     Amount of damage caused
   * @return True if this attack should be blocked entirely
   */
  public boolean isSourceBlocked(IToolStackView tool, int level, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount) {
    return false;
  }

  /**
   * Runs after an entity is attacked (and we know the attack will land). Note you can attack the entity here, but you are responsible for preventing infinite recursion if you do so (by detecting your own attack source for instance)
   * <br/>
   * Alternatives:
   * <ul>
   *   <li>{@link #isSourceBlocked(IToolStackView, int, EquipmentContext, EquipmentSlot, DamageSource, float)}: Allows canceling the attack entirely, including the hurt animation.</li>
   *   <li>{@link #getProtectionModifier(IToolStackView, int, EquipmentContext, EquipmentSlot, DamageSource, float)}: Allows reducing the attack damage.</li>
   * </ul>
   * @param tool             Tool being used
   * @param level            Level of the modifier
   * @param context          Context of entity and other equipment
   * @param slotType         Slot containing the tool
   * @param source           Damage source causing the attack
   * @param amount           Amount of damage caused
   * @param isDirectDamage   If true, this attack is direct damage from an entity
   */
  public void onAttacked(IToolStackView tool, int level, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {}

  /**
   * Called when an entity is attacked and this entity is the attacker
   * @param tool             Tool being used
   * @param level            Level of the modifier
   * @param context          Context of entity and other equipment
   * @param slotType         Slot containing the tool
   * @param target           Entity that was attacked
   * @param source           Damage source used in the attack
   * @param amount           Amount of damage caused
   * @param isDirectDamage   If true, this attack is direct damage from an entity
   */
  public void attackWithArmor(IToolStackView tool, int level, EquipmentContext context, EquipmentSlot slotType, LivingEntity target, DamageSource source, float amount, boolean isDirectDamage) {}

  /* Equipment events */

  /**
   * Called when a tinker tool is unequipped from an entity
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link #onEquip(IToolStackView, int, EquipmentChangeContext)}}: Called when a tool is added to an entity</li>
   *   <li>{@link #onEquipmentChange(IToolStackView, int, EquipmentChangeContext, EquipmentSlot)}: Called on all other slots that did not change</li>
   * </ul>
   * @param tool         Tool unequipped
   * @param level        Level of the modifier
   * @param context      Context about the event
   */
  public void onUnequip(IToolStackView tool, int level, EquipmentChangeContext context) {}

  /**
   * Called when a tinker tool is equipped to an entity
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link #onUnequip(IToolStackView, int, EquipmentChangeContext)}: Called when a tool is removed from an entity</li>
   *   <li>{@link #onEquipmentChange(IToolStackView, int, EquipmentChangeContext, EquipmentSlot)}: Called on all other slots did not change</li>
   * </ul>
   * @param tool         Tool equipped
   * @param level        Level of the modifier
   * @param context      Context about the event
   */
  public void onEquip(IToolStackView tool, int level, EquipmentChangeContext context) {}

  /**
   * Called when a stack in a different slot changed. Not called on the slot that changed
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link #onUnequip(IToolStackView, int, EquipmentChangeContext)}: Called when a tool is removed from an entity</li>
   *   <li>{@link #onEquip(IToolStackView, int, EquipmentChangeContext)}: Called when a tool is added to an entity. Called instead of this hook for the new item</li>
   * </ul>
   * @param tool      Tool instance
   * @param level     Modifier level
   * @param context   Context describing the change
   * @param slotType  Slot containing this tool, did not change
   */
  public void onEquipmentChange(IToolStackView tool, int level, EquipmentChangeContext context, EquipmentSlot slotType) {}


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
  public double getDamagePercentage(IToolStackView tool, int level) {
    return Double.NaN;
  }

  /**
   * Override the default tool logic for showing the durability bar
   * @param tool   Tool instance
   * @param level  Modifier level
   * @return  True forces the bar to show, false forces it to hide. Return null to allow default behavior
   */
  @Nullable
  public Boolean showDurabilityBar(IToolStackView tool, int level) {
    return null;
  }

  /**
   * Gets the RGB for the durability bar
   * @param tool   Tool instance
   * @param level  Modifier level
   * @return  RGB, or -1 to not handle it
   */
  public int getDurabilityRGB(IToolStackView tool, int level) {
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
   * @deprecated use {@link #getHook(ModifierHook)}
   */
  @Nullable @Deprecated
  public <T> T getModule(Class<T> type) {
    return null;
  }

  /**
   * Gets a hook of this modifier. To modify the return values, use {@link #registerHooks(Builder)}
   *
   * @param hook  Hook to fetch
   * @param <T>   Hook return type
   * @return  Submodule implementing the hook, or default instance if its not implemented
   */
  public final <T> T getHook(ModifierHook<T> hook) {
    return hooks.getOrDefault(hook);
  }


  @Override
  public String toString() {
    return "Modifier{" + id + '}';
  }


  /* Utils */

  /**
   * Gets the tool stack from the given entities mainhand. Useful for specialized event handling in modifiers
   * @param living  Entity instance
   * @return  Tool stack
   */
  @Nullable
  public static ToolStack getHeldTool(@Nullable LivingEntity living, InteractionHand hand) {
    return getHeldTool(living, Util.getSlotType(hand));
  }

  /**
   * Gets the tool stack from the given entities mainhand. Useful for specialized event handling in modifiers
   * @param living  Entity instance
   * @return  Tool stack
   */
  @Nullable
  public static ToolStack getHeldTool(@Nullable LivingEntity living, EquipmentSlot slot) {
    if (living == null) {
      return null;
    }
    ItemStack stack = living.getItemBySlot(slot);
    if (stack.isEmpty() || !stack.is(TinkerTags.Items.MODIFIABLE)) {
      return null;
    }
    ToolStack tool = ToolStack.from(stack);
    return tool.isBroken() ? null : tool;
  }

  /**
   * Gets the mining speed modifier for the current conditions, notably potions and armor enchants
   * @param entity  Entity to check
   * @return  Mining speed modifier
   */
  public static float getMiningModifier(LivingEntity entity) {
    float modifier = 1.0f;
    // haste effect
    if (MobEffectUtil.hasDigSpeed(entity)) {
      modifier *= 1.0F + (MobEffectUtil.getDigSpeedAmplification(entity) + 1) * 0.2f;
    }
    // mining fatigue
    MobEffectInstance miningFatigue = entity.getEffect(MobEffects.DIG_SLOWDOWN);
    if (miningFatigue != null) {
      switch (miningFatigue.getAmplifier()) {
        case 0 -> modifier *= 0.3F;
        case 1 -> modifier *= 0.09F;
        case 2 -> modifier *= 0.0027F;
        default -> modifier *= 8.1E-4F;
      }
    }
    // water
    if (entity.isEyeInFluid(FluidTags.WATER) && !ModifierUtil.hasAquaAffinity(entity)) {
      modifier /= 5.0F;
    }
    if (!entity.isOnGround()) {
      modifier /= 5.0F;
    }
    return modifier;
  }

  /**
   * Adds a flat bonus tooltip
   * @param name     Bonus name
   * @param bonus    Bonus amount
   * @param tooltip  Tooltip list
   */
  protected void addFlatBoost(Component name, double bonus, List<Component> tooltip) {
    tooltip.add(applyStyle(new TextComponent(Util.BONUS_FORMAT.format(bonus) + " ").append(name)));
  }

  /**
   * Adds a percent bonus tooltip
   * @param name     Bonus name
   * @param bonus    Bonus amount
   * @param tooltip  Tooltip list
   */
  protected void addPercentTooltip(Component name, double bonus, List<Component> tooltip) {
    tooltip.add(applyStyle(new TextComponent(Util.PERCENT_BOOST_FORMAT.format(bonus) + " ").append(name)));
  }

  /**
   * Adds a tooltip showing a bonus stat
   * @param tool       Tool instance
   * @param stat       Stat added
   * @param condition  Condition to show the tooltip
   * @param amount     Amount to show, before scaling by the tool's modifier
   * @param tooltip    Tooltip list
   */
  protected void addStatTooltip(IToolStackView tool, FloatToolStat stat, TagKey<Item> condition, float amount, List<Component> tooltip) {
    if (tool.hasTag(condition)) {
      addFlatBoost(new TranslatableComponent(getTranslationKey() + "." + stat.getName().getPath()), amount * tool.getMultiplier(stat), tooltip);
    }
  }

  /**
   * Adds a tooltip showing the bonus damage and the type of damage
   * @param tool     Tool instance
   * @param amount   Damage amount
   * @param tooltip  Tooltip
   */
  protected void addDamageTooltip(IToolStackView tool, float amount, List<Component> tooltip) {
    addStatTooltip(tool, ToolStats.ATTACK_DAMAGE, TinkerTags.Items.MELEE_OR_UNARMED, amount, tooltip);
  }

  /** Tries an expected module against the given module type, returning null if failing. Do not use if you extend another modifier with modules */
  @SuppressWarnings("unchecked")
  @Nullable
  protected static <M, E> E tryModuleMatch(Class<E> expected, Class<M> moduleType, M module) {
    if (moduleType == expected) {
      return (E) module;
    }
    return null;
  }
}
