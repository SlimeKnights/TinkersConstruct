package slimeknights.tconstruct.gadgets.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import slimeknights.mantle.item.ArmorTooltipItem;
import slimeknights.tconstruct.items.GadgetItems;
import slimeknights.tconstruct.library.SlimeBounceHandler;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.shared.block.SlimeBlock;
import slimeknights.tconstruct.tools.common.network.BouncedPacket;

public class SlimeBootsItem extends ArmorTooltipItem implements IDyeableArmorItem {

  private static final IArmorMaterial SLIME = new IArmorMaterial() {
    private final Ingredient empty_repair_material = Ingredient.fromItems(Items.AIR);

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
      return SoundEvents.BLOCK_SLIME_BLOCK_PLACE;
    }

    @Override
    public Ingredient getRepairMaterial() {
      return this.empty_repair_material;
    }

    @Override
    public String getName() {
      return Util.resource("slime");
    }

    @Override
    public float getToughness() {
      return 0;
    }
  };

  private final SlimeBlock.SlimeType slimeType;

  public SlimeBootsItem(SlimeBlock.SlimeType slimeType) {
    super(SLIME, EquipmentSlotType.FEET, (new Properties()).group(TinkerRegistry.tabGadgets).maxStackSize(1));
    this.slimeType = slimeType;
  }

  @Override
  public boolean hasColor(ItemStack stack) {
    return true;
  }

  @Override
  public int getColor(ItemStack stack) {
    return this.slimeType.getBallColor();
  }

  @Override
  public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot) {
    return HashMultimap.<String, AttributeModifier>create();
  }
}
