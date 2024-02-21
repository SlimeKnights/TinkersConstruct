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
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.RepairFactorModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ToolActionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ToolDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
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
 * Class representing both modifiers and traits. Acts as a storage container for {@link ModifierHook} modules, which are used to implement various modifier behaviors.
 * @see TinkerHooks
 * @see #registerHooks(Builder)
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
   * Note that this is run in the constructor, so you are unable to use any instance fields in this method unless initialized in this method.
   * TODO 1.19: consider making abstract as everyone is going to need it in the future.
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
   * Overridable method to create the display name for this modifier, ideal to modify colors.
   * TODO: this method does not really seem to do much, is it really needed? I feel like it was supposed to be called in {@link #getDisplayName()}, but it needs to be mutable for that.
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

  /** @deprecated use {@link TooltipModifierHook} */
  @Deprecated
  public void addInformation(IToolStackView tool, int level, @Nullable Player player, List<Component> tooltip, slimeknights.tconstruct.library.utils.TooltipKey tooltipKey, TooltipFlag tooltipFlag) {}

  /** @deprecated use {@link TooltipModifierHook} */
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

  /** @deprecated use {@link AttributesModifierHook} */
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

  /** @deprecated use {@link ToolDamageModifierHook} */
  @Deprecated
  public int onDamageTool(IToolStackView tool, int level, int amount, @Nullable LivingEntity holder) {
    return amount;
  }

  /** @deprecated use {@link RepairFactorModifierHook} */
  @Deprecated
  public float getRepairFactor(IToolStackView toolStack, int level, float factor) {
    return factor;
  }

  /** @deprecated use {@link slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook} */
  @Deprecated
  public void onInventoryTick(IToolStackView tool, int level, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {}

  /**
   * Called on entity or block loot to allow modifying loot
   * @param tool           Current tool instance
   * @param level          Modifier level
   * @param generatedLoot  Current loot list before this modifier
   * @param context        Full loot context
   * @return  Loot replacement
   * TODO: can we ditch this hook in favor of just using GLMs? Just need a loot condition to detect a modifier, and it gives us a lot more flexability
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

  /** @deprecated use {@link ToolActionModifierHook} */
  @Deprecated
  public boolean canPerformAction(IToolStackView tool, int level, ToolAction toolAction) {
    return false;
  }

  /* Harvest hooks */

  /** @deprecated use {@link slimeknights.tconstruct.library.modifiers.hook.mining.BreakSpeedModifierHook} */
  @Deprecated
  public void onBreakSpeed(IToolStackView tool, int level, BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) {}

  /** @deprecated use {@link slimeknights.tconstruct.library.modifiers.hook.HarvestEnchantmentsModifierHook} */
  @Deprecated
  public void applyHarvestEnchantments(IToolStackView tool, int level, ToolHarvestContext context, BiConsumer<Enchantment,Integer> consumer) {}

  /** @deprecated use {@link slimeknights.tconstruct.library.modifiers.hook.LootingModifierHook} */
  @Deprecated
  public int getLootingValue(IToolStackView tool, int level, LivingEntity holder, Entity target, @Nullable DamageSource damageSource, int looting) {
    return looting;
  }

  /** @deprecated use {@link slimeknights.tconstruct.library.modifiers.hook.mining.RemoveBlockModifierHook} */
  @Nullable
  @Deprecated
  public Boolean removeBlock(IToolStackView tool, int level, ToolHarvestContext context) {
    return null;
  }

  /** @deprecated use {@link slimeknights.tconstruct.library.modifiers.hook.mining.BlockBreakModifierHook} */
  @Deprecated
  public void afterBlockBreak(IToolStackView tool, int level, ToolHarvestContext context) {}

  /** @deprecated use {@link slimeknights.tconstruct.library.modifiers.hook.mining.FinishHarvestModifierHook} */
  @Deprecated
  public void finishBreakingBlocks(IToolStackView tool, int level, ToolHarvestContext context) {}


  /* Attack hooks */

  /** @deprecated use {@link MeleeDamageModifierHook} */
  @Deprecated
  public float getEntityDamage(IToolStackView tool, int level, ToolAttackContext context, float baseDamage, float damage) {
    return damage;
  }

  /** @deprecated use {@link MeleeHitModifierHook#beforeMeleeHit(IToolStackView, ModifierEntry, ToolAttackContext, float, float, float)} */
  @Deprecated
  public float beforeEntityHit(IToolStackView tool, int level, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
    return knockback;
  }

  /** @deprecated use {@link MeleeHitModifierHook#afterMeleeHit(IToolStackView, ModifierEntry, ToolAttackContext, float)} */
  @Deprecated
  public int afterEntityHit(IToolStackView tool, int level, ToolAttackContext context, float damageDealt) {
    return 0;
  }

  /** @deprecated use {@link MeleeHitModifierHook#failedMeleeHit(IToolStackView, ModifierEntry, ToolAttackContext, float)} */
  @Deprecated
  public void failedEntityHit(IToolStackView tool, int level, ToolAttackContext context) {}


  /* Armor */

  /** @deprecated use {@link slimeknights.tconstruct.library.modifiers.hook.combat.ProtectionModifierHook} */
  @Deprecated
  public float getProtectionModifier(IToolStackView tool, int level, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float modifierValue) {
    return modifierValue;
  }

  /** @deprecated use {@link slimeknights.tconstruct.library.modifiers.hook.combat.DamageBlockModifierHook} */
  @Deprecated
  public boolean isSourceBlocked(IToolStackView tool, int level, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount) {
    return false;
  }

  /** @deprecated use {@link slimeknights.tconstruct.library.modifiers.hook.combat.DamageTakenModifierHook} */
  @Deprecated
  public void onAttacked(IToolStackView tool, int level, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {}

  /** @deprecated use {@link slimeknights.tconstruct.library.modifiers.hook.combat.DamageDealtModifierHook} */
  @Deprecated
  public void attackWithArmor(IToolStackView tool, int level, EquipmentContext context, EquipmentSlot slotType, LivingEntity target, DamageSource source, float amount, boolean isDirectDamage) {}


  /* Equipment events */

  /** @deprecated use {@link EquipmentChangeModifierHook#onUnequip(IToolStackView, ModifierEntry, EquipmentChangeContext)} */
  @Deprecated
  public void onUnequip(IToolStackView tool, int level, EquipmentChangeContext context) {}

  /** @deprecated use {@link EquipmentChangeModifierHook#onEquip(IToolStackView, ModifierEntry, EquipmentChangeContext)} */
  @Deprecated
  public void onEquip(IToolStackView tool, int level, EquipmentChangeContext context) {}

  /** @deprecated use {@link EquipmentChangeModifierHook#onEquipmentChange(IToolStackView, ModifierEntry, EquipmentChangeContext, EquipmentSlot)} */
  @Deprecated
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

  /** @deprecated use {@link slimeknights.tconstruct.library.modifiers.hook.display.DurabilityDisplayModifierHook#getDurabilityWidth(IToolStackView, ModifierEntry)} */
  @Deprecated
  public double getDamagePercentage(IToolStackView tool, int level) {
    return Double.NaN;
  }

  /** @deprecated use {@link slimeknights.tconstruct.library.modifiers.hook.display.DurabilityDisplayModifierHook#showDurabilityBar(IToolStackView, ModifierEntry)} */
  @Nullable
  @Deprecated
  public Boolean showDurabilityBar(IToolStackView tool, int level) {
    return null;
  }

  /** @deprecated use {@link slimeknights.tconstruct.library.modifiers.hook.display.DurabilityDisplayModifierHook#getDurabilityRGB(IToolStackView, ModifierEntry)} */
  @Deprecated
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

  /** @deprecated use {@link TooltipModifierHook#addFlatBoost(Modifier, Component, double, List)} */
  @Deprecated
  protected void addFlatBoost(Component name, double bonus, List<Component> tooltip) {
    TooltipModifierHook.addFlatBoost(this, name, bonus, tooltip);
  }

  /** @deprecated use {@link TooltipModifierHook#addPercentBoost(Modifier, Component, double, List)} (Modifier, Component, double, List)} */
  @Deprecated
  protected void addPercentTooltip(Component name, double bonus, List<Component> tooltip) {
    TooltipModifierHook.addPercentBoost(this, name, bonus, tooltip);
  }

  /** @deprecated use {@link TooltipModifierHook#addStatBoost(IToolStackView, Modifier, FloatToolStat, TagKey, float, List)} */
  @Deprecated
  protected void addStatTooltip(IToolStackView tool, FloatToolStat stat, TagKey<Item> condition, float amount, List<Component> tooltip) {
    TooltipModifierHook.addStatBoost(tool, this, stat, condition, amount, tooltip);
  }

  /** @deprecated use {@link TooltipModifierHook#addDamageBoost(IToolStackView, Modifier, float, List)} */
  @Deprecated
  protected void addDamageTooltip(IToolStackView tool, float amount, List<Component> tooltip) {
    TooltipModifierHook.addDamageBoost(tool, this, amount, tooltip);
  }

  /**
   * Tries an expected module against the given module type, returning null if failing. Do not use if you extend another modifier with modules
   * @deprecated use {@link #registerHooks(Builder)} with the new hook system.
   */
  @SuppressWarnings("unchecked")
  @Nullable
  @Deprecated
  protected static <M, E> E tryModuleMatch(Class<E> expected, Class<M> moduleType, M module) {
    if (moduleType == expected) {
      return (E) module;
    }
    return null;
  }
}
