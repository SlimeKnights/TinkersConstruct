package slimeknights.tconstruct.gadgets.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import slimeknights.mantle.item.ArmorTooltipItem;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.shared.block.SlimeType;

public class SlimeBootsItem extends ArmorTooltipItem {

  public SlimeBootsItem(SlimeType slimeType, Settings props) {
    super(new SlimeArmorMaterial(slimeType.asString() + "_slime"), EquipmentSlot.FEET, props);
  }

  @Override
  public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot equipmentSlot) {
    return HashMultimap.create();
  }

  public static class SlimeArmorMaterial implements ArmorMaterial {
    private final Ingredient empty_repair_material = Ingredient.ofItems(Items.AIR);
    private final String name;

    public SlimeArmorMaterial(String slimeName) {
      name = slimeName;
    }

    @Override
    public int getDurability(EquipmentSlot slotIn) {
      return 0;
    }

    @Override
    public int getProtectionAmount(EquipmentSlot slotIn) {
      return 0;
    }

    @Override
    public int getEnchantability() {
      return 0;
    }

    @Override
    public SoundEvent getEquipSound() {
      return SoundEvents.BLOCK_SLIME_BLOCK_PLACE;
    }

    @Override
    public Ingredient getRepairIngredient() {
      return this.empty_repair_material;
    }

    @Override
    public String getName() {
      return Util.resource(name);
    }

    @Override
    public float getToughness() {
      return 0;
    }

    @Override
    public float getKnockbackResistance() {
      return 0;
    }
  }
}
