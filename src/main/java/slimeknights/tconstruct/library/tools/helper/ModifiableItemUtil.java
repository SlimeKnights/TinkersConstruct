package slimeknights.tconstruct.library.tools.helper;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

/** General item helper functions */
public class ModifiableItemUtil {
  private static final UUID[] HELD_ARMOR_UUID = new UUID[]{UUID.fromString("00a1a5fe-43b5-4849-8660-de9aa497736a"), UUID.fromString("6776fd7e-4b22-4cdf-a0bc-bb8d2ad1f0bf")};

  private ModifiableItemUtil() {}

  /**
   * Gets attribute modifiers for a weapon with melee capability
   * @param tool  Tool instance
   * @param slot  Held slot
   * @return  Map of attribute modifiers
   */
  public static Multimap<Attribute,AttributeModifier> getMeleeAttributeModifiers(IToolStackView tool, EquipmentSlot slot) {
    ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
    if (!tool.isBroken()) {
      // base stats
      StatsNBT statsNBT = tool.getStats();
      if (slot == EquipmentSlot.MAINHAND && tool.hasTag(TinkerTags.Items.MELEE)) {
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_UUID, "tconstruct.tool.attack_damage", statsNBT.get(ToolStats.ATTACK_DAMAGE), AttributeModifier.Operation.ADDITION));
        // base attack speed is 4, but our numbers start from 4
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_UUID, "tconstruct.tool.attack_speed", statsNBT.get(ToolStats.ATTACK_SPEED) - 4d, AttributeModifier.Operation.ADDITION));
      }

      if (slot.getType() == Type.HAND) {
        // shields and slimestaffs can get armor
        if (tool.hasTag(TinkerTags.Items.ARMOR)) {
          UUID uuid = HELD_ARMOR_UUID[slot.getIndex()];
          double value = statsNBT.get(ToolStats.ARMOR);
          if (value != 0) {
            builder.put(Attributes.ARMOR, new AttributeModifier(uuid, "tconstruct.held.armor", value, AttributeModifier.Operation.ADDITION));
          }
          value = statsNBT.get(ToolStats.ARMOR_TOUGHNESS);
          if (value != 0) {
            builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "tconstruct.held.toughness", value, AttributeModifier.Operation.ADDITION));
          }
          value = statsNBT.get(ToolStats.KNOCKBACK_RESISTANCE);
          if (value != 0) {
            builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(uuid, "tconstruct.held.knockback_resistance", value, AttributeModifier.Operation.ADDITION));
          }
        }

        // grab attributes from modifiers, only do for hands (other slots would just be weird)
        BiConsumer<net.minecraft.world.entity.ai.attributes.Attribute,AttributeModifier> attributeConsumer = builder::put;
        for (ModifierEntry entry : tool.getModifierList()) {
          entry.getHook(TinkerHooks.ATTRIBUTES).addAttributes(tool, entry, slot, attributeConsumer);
        }
      }
    }
    return builder.build();
  }

  /**
   * Logic to prevent reanimation on tools when properties such as autorepair change
   * @param oldStack      Old stack instance
   * @param newStack      New stack instance
   * @param slotChanged   If true, a slot changed
   * @return  True if a reequip animation should be triggered
   */
  public static boolean shouldCauseReequip(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
    if (oldStack == newStack) {
      return false;
    }
    // basic changes
    if (slotChanged || oldStack.getItem() != newStack.getItem()) {
      return true;
    }

    // if the tool props changed,
    ToolStack oldTool = ToolStack.from(oldStack);
    ToolStack newTool = ToolStack.from(newStack);

    // check if modifiers or materials changed
    if (!oldTool.getMaterials().equals(newTool.getMaterials())) {
      return true;
    }
    if (!oldTool.getModifierList().equals(newTool.getModifierList())) {
      return true;
    }

    // if the attributes changed, reequip
    Multimap<Attribute,AttributeModifier> attributesNew = newStack.getAttributeModifiers(EquipmentSlot.MAINHAND);
    Multimap<Attribute, AttributeModifier> attributesOld = oldStack.getAttributeModifiers(EquipmentSlot.MAINHAND);
    if (attributesNew.size() != attributesOld.size()) {
      return true;
    }
    for (Attribute attribute : attributesOld.keySet()) {
      if (!attributesNew.containsKey(attribute)) {
        return true;
      }
      Iterator<AttributeModifier> iter1 = attributesNew.get(attribute).iterator();
      Iterator<AttributeModifier> iter2 = attributesOld.get(attribute).iterator();
      while (iter1.hasNext() && iter2.hasNext()) {
        if (!iter1.next().equals(iter2.next())) {
          return true;
        }
      }
    }
    // no changes, no reequip
    return false;
  }

  /**
   * Handles ticking a modifiable item that works when held. Armor uses different logic
   * @param stack       Modifiable stack
   * @param worldIn     World instance
   * @param entityIn    Entity holding the tool
   * @param itemSlot    Slot with the tool
   * @param isSelected  If true, the tool is selected
   */
  public static void heldInventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
    // don't care about non-living, they skip most tool context
    if (entityIn instanceof LivingEntity) {
      ToolStack tool = ToolStack.from(stack);
      if (!worldIn.isClientSide) {
        tool.ensureHasData();
      }
      List<ModifierEntry> modifiers = tool.getModifierList();
      if (!modifiers.isEmpty()) {
        LivingEntity living = (LivingEntity) entityIn;
        // we pass in the stack for most custom context, but for the sake of armor its easier to tell them that this is the correct slot for effects
        boolean isHeld = isSelected || living.getOffhandItem() == stack;
        for (ModifierEntry entry : modifiers) {
          entry.getModifier().onInventoryTick(tool, entry.getLevel(), worldIn, living, itemSlot, isSelected, isHeld, stack);
        }
      }
    }
  }
}
