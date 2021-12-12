package slimeknights.tconstruct.library.tools.helper;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants.NBT;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.PartRequirement;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.library.tools.item.ITinkerStationDisplay;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.TooltipFlag;
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
  public static final ITextComponent TOOLTIP_HOLD_SHIFT = TConstruct.makeTranslation("tooltip", "hold_shift", TConstruct.makeTranslation("key", "shift").mergeStyle(TextFormatting.YELLOW, TextFormatting.ITALIC));
  /** Tooltip telling the player to hold control for part info */
  public static final ITextComponent TOOLTIP_HOLD_CTRL = TConstruct.makeTranslation("tooltip", "hold_ctrl", TConstruct.makeTranslation("key", "ctrl").mergeStyle(TextFormatting.AQUA, TextFormatting.ITALIC));
  /** Tooltip for when tool data is missing */
  private static final ITextComponent NO_DATA = TConstruct.makeTranslation("tooltip", "missing_data").mergeStyle(TextFormatting.GRAY);

  /**
   * If true, this stack was created for display, so some of the tooltip is suppressed
   * @param stack  Stack to check
   * @return  True if marked display
   */
  public static boolean isDisplay(ItemStack stack) {
    CompoundNBT nbt = stack.getTag();
    return nbt != null && nbt.getBoolean(KEY_DISPLAY);
  }

  /**
   * Gets the display name for a tool including the head material in the name
   * @param stack           Stack instance
   * @param toolDefinition  Tool definition
   * @return  Display name including the head material
   */
  public static ITextComponent getDisplayName(ItemStack stack, ToolDefinition toolDefinition) {
    return getDisplayName(stack, null, toolDefinition);
  }

  /**
   * Gets the display name for a tool including the head material in the name
   * @param stack  Stack instance
   * @param tool   Tool instance
   * @return  Display name including the head material
   */
  public static ITextComponent getDisplayName(ItemStack stack, @Nullable IModifierToolStack tool, ToolDefinition toolDefinition) {
    List<PartRequirement> components = toolDefinition.getData().getParts();
    ITextComponent baseName = new TranslationTextComponent(stack.getTranslationKey());
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

  /** @deprecated use {@link #addInformation(IModifiableDisplay, ItemStack, PlayerEntity, List, TooltipKey, TooltipFlag)} */
  @Deprecated
  public static void addInformation(IModifiableDisplay item, ItemStack stack, List<ITextComponent> tooltip, TooltipKey tooltipKey, boolean isAdvanced) {
    addInformation(item, stack, null, tooltip, tooltipKey, isAdvanced ? TooltipFlag.ADVANCED : TooltipFlag.NORMAL);
  }

  /** Translates client side only logic to a method that exists on serverside, used primarily since vanilla is annoying and takes away player access in the tooltip */
  @OnlyIn(Dist.CLIENT)
  public static void addInformation(IModifiableDisplay item, ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, TooltipKey tooltipKey, ITooltipFlag tooltipFlag) {
    PlayerEntity player = world == null ? null : Minecraft.getInstance().player;
    TooltipUtil.addInformation(item, stack, player, tooltip, tooltipKey, TooltipFlag.fromVanilla(tooltipFlag));
  }

  /**
   * Full logic for adding tooltip information, other than attributes
   */
  public static void addInformation(IModifiableDisplay item, ItemStack stack, @Nullable PlayerEntity player, List<ITextComponent> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    // if the display tag is set, just show modifiers
    if (isDisplay(stack)) {
      ToolStack tool = ToolStack.from(stack);
      for (ModifierEntry entry : tool.getModifierList()) {
        if (entry.getModifier().shouldDisplay(false)) {
          tooltip.add(entry.getModifier().getDisplayName(tool, entry.getLevel()));
        }
      }
      // if not initialized, show no data tooltip on non-standard items
    } else if (!ToolStack.isInitialized(stack)) {
      if (item.getToolDefinition().isMultipart()) {
        CompoundNBT nbt = stack.getTag();
        if (nbt == null || !nbt.contains(ToolStack.TAG_MATERIALS, NBT.TAG_LIST)) {
          tooltip.add(NO_DATA);
        }
      }
    } else {
      switch (tooltipKey) {
        case SHIFT:
          item.getStatInformation(ToolStack.from(stack), player, tooltip, tooltipKey, tooltipFlag);
          break;
        case CONTROL:
          if (item.getToolDefinition().isMultipart()) {
            TooltipUtil.getComponents(item, stack, tooltip);
            break;
          }
          // intentional fallthrough
        default:
          ToolStack tool = ToolStack.from(stack);
          getDefaultInfo(tool, tooltip);
          addAttributes(item, tool, player, tooltip, SHOW_ALL_ATTRIBUTES, EquipmentSlotType.values());
          break;
      }
    }
  }

  /**
   * Adds information when holding neither control nor shift
   * @param stack     Stack instance
   * @param tooltips  Tooltip list
   */
  public static void getDefaultInfo(ItemStack stack, List<ITextComponent> tooltips) {
    getDefaultInfo(ToolStack.from(stack), tooltips);
  }

  /**
   * Adds information when holding neither control nor shift
   * @param tool      Tool stack instance
   * @param tooltips  Tooltip list
   */
  public static void getDefaultInfo(IModifierToolStack tool, List<ITextComponent> tooltips) {
    // shows as broken when broken, hold shift for proper durability
    if (tool.getItem().isDamageable() && !tool.isUnbreakable()) {
      tooltips.add(TooltipBuilder.formatDurability(tool.getCurrentDurability(), tool.getStats().getInt(ToolStats.DURABILITY), true));
    }
    // modifier tooltip
    for (ModifierEntry entry : tool.getModifierList()) {
      if (entry.getModifier().shouldDisplay(false)) {
        tooltips.add(entry.getModifier().getDisplayName(tool, entry.getLevel()));
      }
    }
    tooltips.add(StringTextComponent.EMPTY);
    tooltips.add(TOOLTIP_HOLD_SHIFT);
    if (tool.getDefinition().isMultipart()) {
      tooltips.add(TOOLTIP_HOLD_CTRL);
    }
  }


  /** @deprecated use {@link #getDefaultStats(IModifierToolStack, PlayerEntity, List, TooltipKey, TooltipFlag)} */
  @Deprecated
  public static List<ITextComponent> getDefaultStats(IModifierToolStack tool, List<ITextComponent> tooltip, TooltipFlag flag) {
    return getDefaultStats(tool, null, tooltip, TooltipKey.NORMAL, flag);
  }

  /**
   * Gets the  default information for the given tool stack
   *
   * @param tool      the tool stack
   * @param tooltip   Tooltip list
   * @param flag      Tooltip flag
   * @return List from the parameter after filling
   */
  public static List<ITextComponent> getDefaultStats(IModifierToolStack tool, @Nullable PlayerEntity player, List<ITextComponent> tooltip, TooltipKey key, TooltipFlag flag) {
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

  /** @deprecated {@link #getArmorStats(IModifierToolStack, PlayerEntity, List, TooltipKey, TooltipFlag)} */
  @Deprecated
  public static List<ITextComponent> getArmorStats(IModifierToolStack tool, List<ITextComponent> tooltip, TooltipFlag flag) { 
    return getArmorStats(tool, null, tooltip, TooltipKey.NORMAL, flag);
  }

  /**
   * Gets the  default information for the given tool stack
   *
   * @param tool      the tool stack
   * @param tooltip   Tooltip list
   * @param flag      Tooltip flag
   * @return List from the parameter after filling
   */
  public static List<ITextComponent> getArmorStats(IModifierToolStack tool, @Nullable PlayerEntity player, List<ITextComponent> tooltip, TooltipKey key, TooltipFlag flag) {
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
  public static void getComponents(IModifiable item, ItemStack stack, List<ITextComponent> tooltips) {
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
      tooltips.add(requirement.nameForMaterial(material).deepCopy().mergeStyle(TextFormatting.UNDERLINE).modifyStyle(style -> style.setColor(material.getColor())));
      MaterialRegistry.getInstance().getMaterialStats(material.getIdentifier(), requirement.getStatType()).ifPresent(stat -> tooltips.addAll(stat.getLocalizedInfo()));
      if (i != max) {
        tooltips.add(StringTextComponent.EMPTY);
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
  public static void addAttributes(ITinkerStationDisplay item, IModifierToolStack tool, @Nullable PlayerEntity player, List<ITextComponent> tooltip, BiPredicate<Attribute, Operation> showAttribute, EquipmentSlotType... slots) {
    for (EquipmentSlotType slot : slots) {
      Multimap<Attribute,AttributeModifier> modifiers = item.getAttributeModifiers(tool, slot);
      if (!modifiers.isEmpty()) {
        if (slots.length > 1) {
          tooltip.add(StringTextComponent.EMPTY);
          tooltip.add((new TranslationTextComponent("item.modifiers." + slot.getName())).mergeStyle(TextFormatting.GRAY));
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
            if (modifier.getID() == Item.ATTACK_DAMAGE_MODIFIER) {
              amount += player.getBaseAttributeValue(Attributes.ATTACK_DAMAGE);
              showEquals = true;
            } else if (modifier.getID() == Item.ATTACK_SPEED_MODIFIER) {
              amount += player.getBaseAttributeValue(Attributes.ATTACK_SPEED);
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
          ITextComponent name = new TranslationTextComponent(attribute.getAttributeName());
          if (showEquals) {
            tooltip.add(new StringTextComponent(" ")
                          .appendSibling(new TranslationTextComponent("attribute.modifier.equals." + operation.getId(), ItemStack.DECIMALFORMAT.format(displayValue), name))
                          .mergeStyle(TextFormatting.DARK_GREEN));
          } else if (amount > 0.0D) {
            tooltip.add((new TranslationTextComponent("attribute.modifier.plus." + operation.getId(), ItemStack.DECIMALFORMAT.format(displayValue), name))
                          .mergeStyle(TextFormatting.BLUE));
          } else if (amount < 0.0D) {
            displayValue *= -1;
            tooltip.add((new TranslationTextComponent("attribute.modifier.take." + operation.getId(), ItemStack.DECIMALFORMAT.format(displayValue), name))
                          .mergeStyle(TextFormatting.RED));
          }
        }
      }
    }
  }
}
