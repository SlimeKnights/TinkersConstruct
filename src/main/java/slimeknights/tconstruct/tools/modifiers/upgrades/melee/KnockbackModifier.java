package slimeknights.tconstruct.tools.modifiers.upgrades.melee;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.util.ModifierAttribute;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.UUID;
import java.util.function.BiConsumer;

/** @deprecated use {@link slimeknights.tconstruct.library.modifiers.modules.combat.KnockbackModule} */
@Deprecated
public class KnockbackModifier extends Modifier {
  private static final UUID[] UUIDS = new UUID[4];
  static {
    for (EquipmentSlot slot : ModifiableArmorMaterial.ARMOR_SLOTS) {
      UUIDS[slot.getIndex()] = ModifierAttribute.getUUID("tconstruct.knockback", slot);
    }
  }

  @Override
  public float beforeEntityHit(IToolStackView tool, int level, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
    // do not boost unarmed attacks twice, thats a bit too much knockback for the cost
    if (!context.getAttacker().getItemInHand(context.getHand()).isEmpty()) {
      return knockback + level * 0.5f;
    }
    return knockback;
  }

  @Override
  public void addAttributes(IToolStackView tool, int level, EquipmentSlot slot, BiConsumer<Attribute,AttributeModifier> consumer) {
    if (slot.getType() == Type.ARMOR) {
      consumer.accept(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(UUIDS[slot.getIndex()], "tconstruct.knockback." + slot.getName(), 1 * getEffectiveLevel(tool, level), Operation.ADDITION));
    }
  }
}
