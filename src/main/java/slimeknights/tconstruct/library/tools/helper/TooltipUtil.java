package slimeknights.tconstruct.library.tools.helper;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.DistExecutor;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.definition.PartRequirement;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.library.tools.item.ITinkerStationDisplay;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.TooltipKey;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiPredicate;

/** Helper functions for adding tooltips to tools */
public class TooltipUtil {
  /** Tool tag to set that makes a tool a display tool */
  public static final String KEY_DISPLAY = "tic_display";
  /** Function to show all attributes in the tooltip */
  public static final BiPredicate<Attribute, Operation> SHOW_ALL_ATTRIBUTES = (att, op) -> true;
  /** Function to show all attributes in the tooltip */
  public static final BiPredicate<Attribute, Operation> SHOW_MELEE_ATTRIBUTES = (att, op) -> op != Operation.ADDITION || (att != Attributes.ATTACK_DAMAGE && att != Attributes.ATTACK_SPEED);
  /** Function to show all attributes in the tooltip */
  public static final BiPredicate<Attribute, Operation> SHOW_ARMOR_ATTRIBUTES = (att, op) -> op != Operation.ADDITION || (att != Attributes.ARMOR && att != Attributes.ARMOR_TOUGHNESS && att != Attributes.KNOCKBACK_RESISTANCE);

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
  public static Component getDisplayName(ItemStack stack, @Nullable IModifierToolStack tool, ToolDefinition toolDefinition) {
    List<PartRequirement> components = toolDefinition.getData().getParts();
    Component baseName = new TranslatableComponent(stack.getDescriptionId());
    if (components.isEmpty()) {
      return baseName;
    }
    // if the tool is not named we use the repair tools for a prefix like thing
    // we save all the ones for the name in a set so we don't have the same material in it twice
    Set<IMaterial> nameMaterials = Sets.newLinkedHashSet();
    if (tool == null) tool = ToolStack.from(stack);
    List<IMaterial> materials = tool.getMaterialsList();
    if (materials.size() == components.size()) {
      for (int i = 0; i < components.size(); i++) {
        if (i < materials.size() && MaterialRegistry.getInstance().canRepair(components.get(i).getStatType())) {
          nameMaterials.add(materials.get(i));
        }
      }
    }
    return ITinkerStationDisplay.getCombinedItemName(stack, baseName, nameMaterials);
  }

  /** Replaces the world argument with the local player */
  public static void addInformation(IModifiableDisplay item, ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    // TODO: consider a helper for the following
    Player player = world == null ? null : DistExecutor.unsafeRunForDist(() -> () -> Minecraft.getInstance().player, () -> () -> null);
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
      addModifierNames(stack, tool, tooltip);
      // No definition?
    } else if (!definition.isDataLoaded()) {
      tooltip.add(NO_DATA);

      // if not initialized, show no data tooltip on non-standard items
    } else if (!ToolStack.isInitialized(stack)) {
      tooltip.add(UNINITIALIZED);
      if (definition.isMultipart()) {
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
          if (definition.isMultipart()) {
            TooltipUtil.getComponents(item, stack, tooltip);
            break;
          }
          // intentional fallthrough
        default:
          ToolStack tool = ToolStack.from(stack);
          getDefaultInfo(stack, tool, tooltip);
          addAttributes(item, tool, player, tooltip, SHOW_ALL_ATTRIBUTES, EquipmentSlot.values());
          break;
      }
    }
  }

  /**
   * Adds information when holding neither control nor shift
   * @param stack     Stack instance
   * @param tooltips  Tooltip list
   */
  public static void getDefaultInfo(ItemStack stack, List<Component> tooltips) {
    getDefaultInfo(stack, ToolStack.from(stack), tooltips);
  }

  /**
   * Adds modifier names to the tooltip
   * @param stack      Stack instance. If empty, skips adding enchantment names
   * @param tool       Tool instance
   * @param tooltips   Tooltip list
   */
  public static void addModifierNames(ItemStack stack, IModifierToolStack tool, List<Component> tooltips) {
    for (ModifierEntry entry : tool.getModifierList()) {
      if (entry.getModifier().shouldDisplay(false)) {
        tooltips.add(entry.getModifier().getDisplayName(tool, entry.getLevel()));
      }
    }
    if (!stack.isEmpty()) {
      CompoundTag tag = stack.getTag();
      if (tag != null && tag.contains("Enchantments", Tag.TAG_LIST)) {
        ListTag enchantments = tag.getList("Enchantments", Tag.TAG_COMPOUND);
        for (int i = 0; i < enchantments.size(); ++i) {
          CompoundTag enchantmentTag = enchantments.getCompound(i);
          // TODO: tag to whitelist/blacklist enchantments in the tooltip, depends on which ones we reimplement and which work on their own
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
   */
  public static void getDefaultInfo(ItemStack stack, IModifierToolStack tool, List<Component> tooltips) {
    // shows as broken when broken, hold shift for proper durability
    if (tool.getItem().canBeDepleted() && !tool.isUnbreakable()) {
      tooltips.add(TooltipBuilder.formatDurability(tool.getCurrentDurability(), tool.getStats().getInt(ToolStats.DURABILITY), true));
    }
    // modifier tooltip
    addModifierNames(stack, tool, tooltips);
    tooltips.add(TextComponent.EMPTY);
    tooltips.add(TOOLTIP_HOLD_SHIFT);
    if (tool.getDefinition().isMultipart()) {
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
  public static List<Component> getDefaultStats(IModifierToolStack tool, @Nullable Player player, List<Component> tooltip, TooltipKey key, TooltipFlag flag) {
    TooltipBuilder builder = new TooltipBuilder(tool, tooltip);
    Item item = tool.getItem();
    if (TinkerTags.Items.DURABILITY.contains(item)) {
      builder.addDurability();
    }
    if (TinkerTags.Items.MELEE.contains(item)) {
      builder.addWithAttribute(ToolStats.ATTACK_DAMAGE, Attributes.ATTACK_DAMAGE);
      builder.add(ToolStats.ATTACK_SPEED);
    }
    if (TinkerTags.Items.HARVEST.contains(item)) {
      if (TinkerTags.Items.HARVEST_PRIMARY.contains(tool.getItem())) {
        builder.add(ToolStats.HARVEST_LEVEL);
      }
      builder.add(ToolStats.MINING_SPEED);
    }

    builder.addAllFreeSlots();
    for (ModifierEntry entry : tool.getModifierList()) {
      entry.getModifier().addInformation(tool, entry.getLevel(), player, tooltip, key, flag);
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
  public static List<Component> getArmorStats(IModifierToolStack tool, @Nullable Player player, List<Component> tooltip, TooltipKey key, TooltipFlag flag) {
    TooltipBuilder builder = new TooltipBuilder(tool, tooltip);
    Item item = tool.getItem();
    if (TinkerTags.Items.DURABILITY.contains(item)) {
      builder.addDurability();
    }
    if (TinkerTags.Items.ARMOR.contains(item)) {
      builder.add(ToolStats.ARMOR);
      builder.addOptional(ToolStats.ARMOR_TOUGHNESS);
      builder.addOptional(ToolStats.KNOCKBACK_RESISTANCE, 10f);
    }
    if (TinkerTags.Items.CHESTPLATES.contains(item) && tool.getModifierLevel(TinkerModifiers.unarmed.get()) > 0) {
      builder.addWithAttribute(ToolStats.ATTACK_DAMAGE, Attributes.ATTACK_DAMAGE);
    }

    builder.addAllFreeSlots();

    for (ModifierEntry entry : tool.getModifierList()) {
      entry.getModifier().addInformation(tool, entry.getLevel(), player, tooltip, key, flag);
    }
    return builder.getTooltips();
  }

  /**
   * Gets the tooltip of the components list of a tool
   * @param item      Modifiable item instance
   * @param stack     Item stack being displayed
   * @param tooltips  List of tooltips
   */
  public static void getComponents(IModifiable item, ItemStack stack, List<Component> tooltips) {
    // no components, nothing to do
    List<PartRequirement> components = item.getToolDefinition().getData().getParts();
    if (components.isEmpty()) {
      return;
    }
    // no materials is bad
    List<IMaterial> materials = ToolStack.from(stack).getMaterialsList();
    if (materials.isEmpty()) {
      tooltips.add(NO_DATA);
      return;
    }
    // wrong number is bad
    if (materials.size() < components.size()) {
      return;
    }
    // finally, display them all
    int max = components.size() - 1;
    for (int i = 0; i <= max; i++) {
      PartRequirement requirement = components.get(i);
      IMaterial material = materials.get(i);
      tooltips.add(requirement.nameForMaterial(material).copy().withStyle(ChatFormatting.UNDERLINE).withStyle(style -> style.withColor(material.getColor())));
      MaterialRegistry.getInstance().getMaterialStats(material.getIdentifier(), requirement.getStatType()).ifPresent(stat -> tooltips.addAll(stat.getLocalizedInfo()));
      if (i != max) {
        tooltips.add(TextComponent.EMPTY);
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
  public static void addAttributes(ITinkerStationDisplay item, IModifierToolStack tool, @Nullable Player player, List<Component> tooltip, BiPredicate<Attribute, Operation> showAttribute, EquipmentSlot... slots) {
    for (EquipmentSlot slot : slots) {
      Multimap<Attribute,AttributeModifier> modifiers = item.getAttributeModifiers(tool, slot);
      if (!modifiers.isEmpty()) {
        if (slots.length > 1) {
          tooltip.add(TextComponent.EMPTY);
          tooltip.add((new TranslatableComponent("item.modifiers." + slot.getName())).withStyle(ChatFormatting.GRAY));
        }

        for (Entry<Attribute, AttributeModifier> entry : modifiers.entries()) {
          Attribute attribute = entry.getKey();
          AttributeModifier modifier = entry.getValue();
          Operation operation = modifier.getOperation();
          // allow suppressing specific attributes
          if (!showAttribute.test(attribute, operation)) {
            continue;
          }
          // find value
          double amount = modifier.getAmount();
          boolean showEquals = false;
          if (player != null) {
            if (modifier.getId() == Item.BASE_ATTACK_DAMAGE_UUID) {
              amount += player.getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
              showEquals = true;
            } else if (modifier.getId() == Item.BASE_ATTACK_SPEED_UUID) {
              amount += player.getAttributeBaseValue(Attributes.ATTACK_SPEED);
              showEquals = true;
            }
          }
          // some numbers display a bit different
          double displayValue = amount;
          if (modifier.getOperation() == Operation.ADDITION) {
            // vanilla multiplies knockback resist by 10 for some odd reason
            if (attribute.equals(Attributes.KNOCKBACK_RESISTANCE)) {
              displayValue *= 10;
            }
          } else {
            // display multiply as percentage
            displayValue *= 100;
          }
          // final tooltip addition
          Component name = new TranslatableComponent(attribute.getDescriptionId());
          if (showEquals) {
            tooltip.add(new TextComponent(" ")
                          .append(new TranslatableComponent("attribute.modifier.equals." + operation.toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(displayValue), name))
                          .withStyle(ChatFormatting.DARK_GREEN));
          } else if (amount > 0.0D) {
            tooltip.add((new TranslatableComponent("attribute.modifier.plus." + operation.toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(displayValue), name))
                          .withStyle(ChatFormatting.BLUE));
          } else if (amount < 0.0D) {
            displayValue *= -1;
            tooltip.add((new TranslatableComponent("attribute.modifier.take." + operation.toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(displayValue), name))
                          .withStyle(ChatFormatting.RED));
          }
        }
      }
    }
  }
}
