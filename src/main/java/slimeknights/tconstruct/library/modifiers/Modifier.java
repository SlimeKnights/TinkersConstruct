package slimeknights.tconstruct.library.modifiers;

import com.google.gson.JsonObject;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import slimeknights.mantle.client.ResourceColorManager;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IHaveLoader;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierManager.ModifierRegistrationEvent;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap.Builder;
import slimeknights.tconstruct.library.modifiers.util.ModifierLevelDisplay;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;

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
    ModifierHookMap.Builder hookBuilder = ModifierHookMap.builder();
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
    return Component.translatable(getTranslationKey());
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
      displayName = Component.translatable(getTranslationKey()).withStyle(style -> style.withColor(getTextColor()));
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

  /**
   * Gets the description for this modifier
   * @return  Description for this modifier
   */
  public List<Component> getDescriptionList() {
    if (descriptionList == null) {
      descriptionList = Arrays.asList(
        Component.translatable(getTranslationKey() + ".flavor").withStyle(ChatFormatting.ITALIC),
        Component.translatable(getTranslationKey() + ".description"));
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
      return Component.empty();
    }
    MutableComponent textComponent = Component.literal("");
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

  /**
   * Determines if the modifier should display
   * @param advanced  If true, in an advanced view such as the tinker station. False for tooltips
   * @return  True if the modifier should show
   */
  public boolean shouldDisplay(boolean advanced) {
    return true;
  }


  /* Hooks */

  /**
   * Called on entity or block loot to allow modifying loot
   * @param tool           Current tool instance
   * @param level          Modifier level
   * @param generatedLoot  Current loot list before this modifier
   * @param context        Full loot context
   * @return  Loot replacement
   * TODO: can we ditch this hook in favor of just using GLMs? Just need a loot condition to detect a modifier, and it gives us a lot more flexability
   */
  public void processLoot(IToolStackView tool, int level, List<ItemStack> generatedLoot, LootContext context) {}


  /* Modules */

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
    if (entity.isEyeInFluid(FluidTags.WATER) && !EnchantmentHelper.hasAquaAffinity(entity)) {
      modifier /= 5.0F;
    }
    if (!entity.isOnGround()) {
      modifier /= 5.0F;
    }
    return modifier;
  }
}
