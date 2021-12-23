package slimeknights.tconstruct.gadgets.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvent;
import slimeknights.mantle.item.ArmorTooltipItem;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.shared.block.SlimeType;

public class SlimeBootsItem extends ArmorTooltipItem {

  public SlimeBootsItem(SlimeType slimeType, Properties props) {
    super(new SlimeArmorMaterial(slimeType.getString() + "_slime"), EquipmentSlotType.FEET, props);
  }

  @Override
  public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot) {
    return HashMultimap.create();
  }

  public static class SlimeArmorMaterial implements IArmorMaterial {
    private final Ingredient empty_repair_material = Ingredient.fromItems(Items.AIR);
    private final String name;

    public SlimeArmorMaterial(String slimeName) {
      name = slimeName;
    }

    @Override
    public int getDurability(EquipmentSlotType slotIn) {
      return 0;
    }

    @Override
    public int getDamageReductionAmount(EquipmentSlotType slotIn) {
      return 0;
    }

    @Override
    public int getEnchantability() {
      return 0;
    }

    @Override
    public SoundEvent getSoundEvent() {
      return Sounds.EQUIP_SLIME.getSound();
    }

    @Override
    public Ingredient getRepairMaterial() {
      return this.empty_repair_material;
    }

    @Override
    public String getName() {
      return TConstruct.resourceString(name);
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
