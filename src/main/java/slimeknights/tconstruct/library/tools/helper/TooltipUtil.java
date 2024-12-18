package slimeknights.tconstruct.library.tools.helper;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStack.TooltipPart;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import slimeknights.mantle.client.SafeClientAccess;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.client.materials.MaterialTooltipCache;
import slimeknights.tconstruct.library.materials.IMaterialRegistry;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolMaterialHook;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolPartsHook;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.library.tools.item.ITinkerStationDisplay;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.part.IToolPart;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiPredicate;

/** Helper functions for adding tooltips to tools */
public class TooltipUtil {
  /** Translation key for the tool name format string */
  public static final String KEY_FORMAT = TConstruct.makeTranslationKey("item", "tool.format");
  /** Format for a name ID pair */
  public static final String KEY_ID_FORMAT = TConstruct.makeTranslationKey("item", "tool.id_format");
  /** Translation key for the tool name format string */
  private static final Component MATERIAL_SEPARATOR = TConstruct.makeTranslation("item", "tool.material_separator");

  /** Tool tag to set that makes a tool a display tool */
  public static final String KEY_DISPLAY = "tic_display";
  /** Tag to set name without name being italic */
  private static final String KEY_NAME = "tic_name";

  /** Function to show all attributes in the tooltip */
  public static final BiPredicate<Attribute, Operation> SHOW_ALL_ATTRIBUTES = (att, op) -> true;
  /** Function to show all attributes in the tooltip */
  public static final BiPredicate<Attribute, Operation> SHOW_MELEE_ATTRIBUTES = (att, op) -> op != Operation.ADDITION || (att != Attributes.ATTACK_DAMAGE && att != Attributes.ATTACK_SPEED && att != Attributes.ARMOR && att != Attributes.ARMOR_TOUGHNESS && att != Attributes.KNOCKBACK_RESISTANCE);
  /** Function to show all attributes in the tooltip */
  public static final BiPredicate<Attribute, Operation> SHOW_ARMOR_ATTRIBUTES = (att, op) -> op != Operation.ADDITION || (att != Attributes.ARMOR && att != Attributes.ARMOR_TOUGHNESS && att != Attributes.KNOCKBACK_RESISTANCE);

  /** Flags used when not holding control or shift */
  private static final int DEFAULT_HIDE_FLAGS = TooltipPart.ENCHANTMENTS.getMask();
  /** Flags used when holding control or shift */
  private static final int MODIFIER_HIDE_FLAGS = TooltipPart.ENCHANTMENTS.getMask() | TooltipPart.MODIFIERS.getMask();

  private TooltipUtil() {}

  /** Tooltip telling the player to hold shift for more info */
  public static final Component TOOLTIP_HOLD_SHIFT = TConstruct.makeTranslation("tooltip", "hold_shift", TConstruct.makeTranslation("key", "shift").withStyle(ChatFormatting.YELLOW, ChatFormatting.ITALIC));
  /** Tooltip telling the player to hold control for part info */
  public static final Component TOOLTIP_HOLD_CTRL = TConstruct.makeTranslation("tooltip", "hold_ctrl", TConstruct.makeTranslation("key", "ctrl").withStyle(ChatFormatting.AQUA, ChatFormatting.ITALIC));
  /** Tooltip for when tool data is missing */
  private static final Component NO_DATA = TConstruct.makeTranslation("tooltip", "missing_data").withStyle(ChatFormatting.GRAY);
  /** Tooltip for when a tool is uninitialized */
  private static final Component UNINITIALIZED = TConstruct.makeTranslation("tooltip", "uninitialized").withStyle(ChatFormatting.GRAY);
  /** Extra tooltip for multipart tools with no materials */
  private static final Component RANDOM_MATERIALS = TConstruct.makeTranslation("tooltip", "random_materials").withStyle(ChatFormatting.GRAY);

  /**
   * If true, this stack was created for display, so some of the tooltip is suppressed
   * @param stack  Stack to check
   * @return  True if marked display
   */
  public static boolean isDisplay(ItemStack stack) {
    CompoundTag nbt = stack.getTag();
    return nbt != null && nbt.getBoolean(KEY_DISPLAY);
  }

  /** Gets the name for a given material variant */
  @Nullable
  private static Component nameFor(String itemKey, Component itemName, MaterialVariantId variantId) {
    String materialKey = MaterialTooltipCache.getKey(variantId);
    String key = itemKey + "." + materialKey;
    if (Util.canTranslate(key)) {
      return Component.translatable(key);
    }
    // name format override
    String formatKey = materialKey + ".format";
    if (Util.canTranslate(formatKey)) {
      return Component.translatable(formatKey, itemName);
    }
    // base name with generic format
    if (Util.canTranslate(materialKey)) {
      return Component.translatable(KEY_FORMAT, Component.translatable(materialKey), itemName);
    }
    return null;
  }

  /**
   * Gets the display name for a single material
   * @param stack     Stack instance
   * @param itemName  Name of the stack on its own
   * @param material  Material to use
   * @return  Name for a material tool
   */
  private static Component getMaterialItemName(ItemStack stack, Component itemName, MaterialVariantId material) {
    String itemKey = stack.getDescriptionId();
    if (material.hasVariant()) {
      Component component = nameFor(itemKey, itemName, material);
      if (component != null) {
        return component;
      }
    }
    Component component = nameFor(itemKey, itemName, material.getId());
    if (component != null) {
      return component;
    }
    return itemName;
  }

  /**
   * Combines the given display name with the material names to form the new given name
   *
   * @param itemName the standard display name
   * @param materials the list of material names
   * @return the combined item name
   */
  private static Component getCombinedItemName(Component itemName, Collection<Component> materials) {
    if (materials.isEmpty()) {
      return itemName;
    }
    // separate materials by dash
    MutableComponent name = Component.literal("");
    Iterator<Component> iter = materials.iterator();
    name.append(iter.next());
    while (iter.hasNext()) {
      name.append(MATERIAL_SEPARATOR).append(iter.next());
    }
    return Component.translatable(KEY_FORMAT, name, itemName);
  }

  /** Sets the tool name in a way that will not be italic */
  public static void setDisplayName(ItemStack tool, String name) {
    if (name.isEmpty()) {
      CompoundTag tag = tool.getTag();
      if (tag != null) {
        tag.remove(KEY_NAME);
      }
    } else {
      tool.getOrCreateTag().putString(KEY_NAME, name);
    }
    tool.resetHoverName();
  }

  /** Gets the display name from the given tool */
  public static String getDisplayName(ItemStack tool) {
    CompoundTag tag = tool.getTag();
    if (tag != null) {
      return tag.getString(KEY_NAME);
    }
    return "";
  }

  /**
   * Gets the display name for a tool including the head material in the name
   * @param stack           Stack instance
   * @param toolDefinition  Tool definition
   * @return  Display name including the head material
   */
  public static Component getDisplayName(ItemStack stack, ToolDefinition toolDefinition) {
    return getDisplayName(stack, null, toolDefinition);
  }

  /**
   * Gets the display name for a tool including the head material in the name
   * @param stack  Stack instance
   * @param tool   Tool instance
   * @return  Display name including the head material
   */
  public static Component getDisplayName(ItemStack stack, @Nullable IToolStackView tool, ToolDefinition toolDefinition) {
    String name = getDisplayName(stack);
    if (!name.isEmpty()) {
      return Component.literal(name);
    }
    List<MaterialStatsId> components = ToolMaterialHook.stats(toolDefinition);
    Component baseName = Component.translatable(stack.getDescriptionId());
    if (components.isEmpty()) {
      return baseName;
    }

    // if there is a mismatch in material size, just stop here
    if (tool == null) tool = ToolStack.from(stack);
    MaterialNBT materials = tool.getMaterials();
    if (materials.size() != components.size()) {
      return baseName;
    }

    // if the tool is not named we use the repair materials for a prefix like thing
    // set ensures we don't use the same name twice, specifically a set of components ensures if two variants have the same name we don't use both
    Set<Component> nameMaterials = Sets.newLinkedHashSet();
    MaterialVariantId firstMaterial = null;
    IMaterialRegistry registry = MaterialRegistry.getInstance();
    for (int i = 0; i < components.size(); i++) {
      if (i < materials.size() && registry.canRepair(components.get(i))) {
        MaterialVariantId material = materials.get(i).getVariant();
        if (!IMaterial.UNKNOWN_ID.equals(material)) {
          if (firstMaterial == null) {
            firstMaterial = material;
          }
          nameMaterials.add(MaterialTooltipCache.getDisplayName(material));
        }
      }
    }
    // if a single material, use the single material logic
    if (nameMaterials.size() == 1) {
      return getMaterialItemName(stack, baseName, firstMaterial);
    }
    // multiple means we mix them together
    return getCombinedItemName(baseName, nameMaterials);
  }

  /** Replaces the world argument with the local player */
  public static void addInformation(IModifiableDisplay item, ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    Player player = world == null ? null : SafeClientAccess.getPlayer();
    TooltipUtil.addInformation(item, stack, player, tooltip, tooltipKey, tooltipFlag);
  }

  /**
   * Full logic for adding tooltip information, other than attributes
   */
  public static void addInformation(IModifiableDisplay item, ItemStack stack, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    // if the display tag is set, just show modifiers
    ToolDefinition definition = item.getToolDefinition();
    if (isDisplay(stack)) {
      ToolStack tool = ToolStack.from(stack);
      addModifierNames(stack, tool, tooltip, tooltipFlag);
      // No definition?
    } else if (!definition.isDataLoaded()) {
      tooltip.add(NO_DATA);

      // if not initialized, show no data tooltip on non-standard items
    } else if (!ToolStack.isInitialized(stack)) {
      tooltip.add(UNINITIALIZED);
      if (definition.hasMaterials()) {
        CompoundTag nbt = stack.getTag();
        if (nbt == null || !nbt.contains(ToolStack.TAG_MATERIALS, Tag.TAG_LIST)) {
          tooltip.add(RANDOM_MATERIALS);
        }
      }
    } else {
      switch (tooltipKey) {
        case SHIFT:
          item.getStatInformation(ToolStack.from(stack), player, tooltip, tooltipKey, tooltipFlag);
          break;
        case CONTROL:
          if (definition.hasMaterials()) {
            getComponents(item, stack, tooltip, tooltipFlag);
            break;
          }
          // intentional fallthrough
        default:
          ToolStack tool = ToolStack.from(stack);
          getDefaultInfo(stack, tool, tooltip, tooltipFlag);
          break;
      }
    }
  }

  /**
   * Adds modifier names to the tooltip
   * @param stack      Stack instance. If empty, skips adding enchantment names
   * @param tool       Tool instance
   * @param tooltips   Tooltip list
   * @param flag      Tooltip flag
   */
  @SuppressWarnings("deprecation")
  public static void addModifierNames(ItemStack stack, IToolStackView tool, List<Component> tooltips, TooltipFlag flag) {
    for (ModifierEntry entry : tool.getModifierList()) {
      if (entry.getModifier().shouldDisplay(false)) {
        Component name = entry.getModifier().getDisplayName(tool, entry);
        if (flag.isAdvanced() && Config.CLIENT.modifiersIDsInAdvancedTooltips.get()) {
          tooltips.add(Component.translatable(KEY_ID_FORMAT, name, Component.literal(entry.getModifier().getId().toString())).withStyle(ChatFormatting.DARK_GRAY));
        } else {
          tooltips.add(name);
        }
      }
    }
    if (!stack.isEmpty()) {
      CompoundTag tag = stack.getTag();
      if (tag != null && tag.contains("Enchantments", Tag.TAG_LIST)) {
        ListTag enchantments = tag.getList("Enchantments", Tag.TAG_COMPOUND);
        for (int i = 0; i < enchantments.size(); ++i) {
          CompoundTag enchantmentTag = enchantments.getCompound(i);
          // TODO: is this the best place for this, or should we let vanilla run?
          Registry.ENCHANTMENT.getOptional(ResourceLocation.tryParse(enchantmentTag.getString("id")))
                              .ifPresent(enchantment -> tooltips.add(enchantment.getFullname(enchantmentTag.getInt("lvl"))));
        }
      }
    }
  }

  /**
   * Adds information when holding neither control nor shift
   * @param tool      Tool stack instance
   * @param tooltips  Tooltip list
   * @param flag      Tooltip flag
   */
  public static void getDefaultInfo(ItemStack stack, IToolStackView tool, List<Component> tooltips, TooltipFlag flag) {
    // shows as broken when broken, hold shift for proper durability
    if (tool.getItem().canBeDepleted() && !tool.isUnbreakable() && tool.hasTag(TinkerTags.Items.DURABILITY)) {
      tooltips.add(TooltipBuilder.formatDurability(tool.getCurrentDurability(), tool.getStats().getInt(ToolStats.DURABILITY), true));
    }
    // modifier tooltip
    addModifierNames(stack, tool, tooltips, flag);
    tooltips.add(Component.empty());
    tooltips.add(TOOLTIP_HOLD_SHIFT);
    if (tool.getDefinition().hasMaterials()) {
      tooltips.add(TOOLTIP_HOLD_CTRL);
    }
  }

  /**
   * Gets the  default information for the given tool stack
   *
   * @param tool      the tool stack
   * @param tooltip   Tooltip list
   * @param flag      Tooltip flag
   * @return List from the parameter after filling
   */
  public static List<Component> getDefaultStats(IToolStackView tool, @Nullable Player player, List<Component> tooltip, TooltipKey key, TooltipFlag flag) {
    TooltipBuilder builder = new TooltipBuilder(tool, tooltip);
    if (tool.hasTag(TinkerTags.Items.DURABILITY)) {
      builder.addDurability();
    }
    if (tool.hasTag(TinkerTags.Items.RANGED)) {
      builder.add(ToolStats.DRAW_SPEED);
      builder.add(ToolStats.VELOCITY);
      builder.add(ToolStats.PROJECTILE_DAMAGE);
      builder.add(ToolStats.ACCURACY);
    }
    if (tool.hasTag(TinkerTags.Items.MELEE_WEAPON)) {
      builder.addWithAttribute(ToolStats.ATTACK_DAMAGE, Attributes.ATTACK_DAMAGE);
      builder.add(ToolStats.ATTACK_SPEED);
    }
    if (tool.hasTag(TinkerTags.Items.HARVEST)) {
      if (tool.hasTag(TinkerTags.Items.HARVEST_PRIMARY)) {
        builder.addTier();
      }
      builder.add(ToolStats.MINING_SPEED);
    }
    // slimestaffs and shields are holdable armor, so show armor stats
    if (tool.hasTag(TinkerTags.Items.ARMOR)) {
      builder.addOptional(ToolStats.ARMOR);
      builder.addOptional(ToolStats.ARMOR_TOUGHNESS);
      builder.addOptional(ToolStats.KNOCKBACK_RESISTANCE, 10f);
    }
    // TODO: should this be a tag? or a volatile flag?
    if (tool.getModifierLevel(TinkerModifiers.blocking.getId()) > 0 || tool.getModifierLevel(TinkerModifiers.parrying.getId()) > 0) {
      builder.add(ToolStats.BLOCK_AMOUNT);
      builder.add(ToolStats.BLOCK_ANGLE);
    }

    builder.addAllFreeSlots();
    for (ModifierEntry entry : tool.getModifierList()) {
      entry.getHook(ModifierHooks.TOOLTIP).addTooltip(tool, entry, player, tooltip, key, flag);
    }
    return builder.getTooltips();
  }

  /**
   * Gets the  default information for the given tool stack
   *
   * @param tool      the tool stack
   * @param tooltip   Tooltip list
   * @param flag      Tooltip flag
   * @return List from the parameter after filling
   */
  public static List<Component> getArmorStats(IToolStackView tool, @Nullable Player player, List<Component> tooltip, TooltipKey key, TooltipFlag flag) {
    TooltipBuilder builder = new TooltipBuilder(tool, tooltip);
    if (tool.hasTag(TinkerTags.Items.DURABILITY)) {
      builder.addDurability();
    }
    if (tool.hasTag(TinkerTags.Items.ARMOR)) {
      builder.add(ToolStats.ARMOR);
      builder.addOptional(ToolStats.ARMOR_TOUGHNESS);
      builder.addOptional(ToolStats.KNOCKBACK_RESISTANCE, 10f);
    }
    if (tool.hasTag(TinkerTags.Items.UNARMED)) {
      builder.addWithAttribute(ToolStats.ATTACK_DAMAGE, Attributes.ATTACK_DAMAGE);
    }

    builder.addAllFreeSlots();

    for (ModifierEntry entry : tool.getModifierList()) {
      entry.getHook(ModifierHooks.TOOLTIP).addTooltip(tool, entry, player, tooltip, key, flag);
    }
    return builder.getTooltips();
  }

  /**
   * Gets the tooltip of the components list of a tool
   * @param item      Modifiable item instance
   * @param stack     Item stack being displayed
   * @param tooltips  List of tooltips
   * @param flag      Tooltip flag, if advanced will show material IDs
   */
  public static void getComponents(IModifiable item, ItemStack stack, List<Component> tooltips, TooltipFlag flag) {
    // no components, nothing to do
    List<MaterialStatsId> components = ToolMaterialHook.stats(item.getToolDefinition());
    if (components.isEmpty()) {
      return;
    }
    // no materials is bad
    MaterialNBT materials = ToolStack.from(stack).getMaterials();
    if (materials.size() == 0) {
      tooltips.add(NO_DATA);
      return;
    }
    // wrong number is bad
    if (materials.size() < components.size()) {
      return;
    }
    // start by displaying all tool parts
    int max = components.size() - 1;
    List<IToolPart> parts = ToolPartsHook.parts(item.getToolDefinition());
    int partCount = parts.size();
    for (int i = 0; i <= max; i++) {
      MaterialVariantId material = materials.get(i).getVariant();
      // display tool parts as the tool part name, nicer to work with
      Component componentName;
      if (i < partCount) {
        componentName = parts.get(i).withMaterial(material).getHoverName();
      } else {
        componentName = Component.translatable(KEY_FORMAT, MaterialTooltipCache.getDisplayName(material), Component.translatable(Util.makeTranslationKey("stat", components.get(i))));
      }
      // underline it and color it with the material name
      tooltips.add(componentName.copy().withStyle(ChatFormatting.UNDERLINE).withStyle(style -> style.withColor(MaterialTooltipCache.getColor(material))));
      // material IDs on advanced
      if (flag.isAdvanced()) {
        tooltips.add((Component.literal(material.toString())).withStyle(ChatFormatting.DARK_GRAY));
      }
      // material stats
      MaterialRegistry.getInstance().getMaterialStats(material.getId(), components.get(i)).ifPresent(stat -> tooltips.addAll(stat.getLocalizedInfo()));
      if (i != max) {
        tooltips.add(Component.empty());
      }
    }
  }

  /**
   * Adds attributes to the tooltip
   * @param item           Modifiable item instance
   * @param tool           Tool instance, primary source of info for the tool
   * @param player         Player instance
   * @param tooltip        Tooltip instance
   * @param showAttribute  Predicate to determine whether an attribute should show
   * @param slots          List of slots to display
   */
  public static void addAttributes(ITinkerStationDisplay item, IToolStackView tool, @Nullable Player player, List<Component> tooltip, BiPredicate<Attribute, Operation> showAttribute, EquipmentSlot... slots) {
    for (EquipmentSlot slot : slots) {
      Multimap<Attribute,AttributeModifier> modifiers = item.getAttributeModifiers(tool, slot);
      if (!modifiers.isEmpty()) {
        if (slots.length > 1) {
          tooltip.add(Component.empty());
          tooltip.add((Component.translatable("item.modifiers." + slot.getName())).withStyle(ChatFormatting.GRAY));
        }

        for (Entry<Attribute, AttributeModifier> entry : modifiers.entries()) {
          Attribute attribute = entry.getKey();
          AttributeModifier modifier = entry.getValue();
          Operation operation = modifier.getOperation();
          // allow suppressing specific attributes
          if (!showAttribute.test(attribute, operation)) {
            continue;
          }
          addAttribute(attribute, operation, modifier.getAmount(), modifier.getId(), player, tooltip);
        }
      }
    }
  }

  /**
   * Adds a single attribute to the tooltip
   * @param attribute  Attribute type
   * @param operation  Attribute operationm
   * @param amount     Attribute amount
   * @param uuid       Attribute UUID
   * @param player     Player instance
   * @param tooltip    Tooltip list
   */
  public static void addAttribute(Attribute attribute, Operation operation, double amount, @Nullable UUID uuid, @Nullable Player player, List<Component> tooltip) {
    // find value
    boolean showEquals = false;
    if (player != null) {
      if (uuid == Item.BASE_ATTACK_DAMAGE_UUID) {
        amount += player.getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
        showEquals = true;
      } else if (uuid == Item.BASE_ATTACK_SPEED_UUID) {
        amount += player.getAttributeBaseValue(Attributes.ATTACK_SPEED);
        showEquals = true;
      }
    }
    // some numbers display a bit different
    double displayValue = amount;
    if (operation == Operation.ADDITION) {
      // vanilla multiplies knockback resist by 10 for some odd reason
      if (attribute.equals(Attributes.KNOCKBACK_RESISTANCE)) {
        displayValue *= 10;
      }
    } else {
      // display multiply as percentage
      displayValue *= 100;
    }
    // final tooltip addition
    Component name = Component.translatable(attribute.getDescriptionId());
    if (showEquals) {
      tooltip.add(Component.literal(" ")
                           .append(Component.translatable("attribute.modifier.equals." + operation.toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(displayValue), name))
                           .withStyle(ChatFormatting.DARK_GREEN));
    } else if (amount > 0.0D) {
      tooltip.add((Component.translatable("attribute.modifier.plus." + operation.toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(displayValue), name))
                    .withStyle(ChatFormatting.BLUE));
    } else if (amount < 0.0D) {
      displayValue *= -1;
      tooltip.add((Component.translatable("attribute.modifier.take." + operation.toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(displayValue), name))
                    .withStyle(ChatFormatting.RED));
    }
  }

  /** Gets the tooltip flags for the current ctrl+shift combination, used to hide enchantments and modifiers from the tooltip as needed */
  public static int getModifierHideFlags(ToolDefinition definition) {
    TooltipKey key = SafeClientAccess.getTooltipKey();
    if (key == TooltipKey.SHIFT || (key == TooltipKey.CONTROL && definition.hasMaterials())) {
      return MODIFIER_HIDE_FLAGS;
    }
    return DEFAULT_HIDE_FLAGS;
  }
}
