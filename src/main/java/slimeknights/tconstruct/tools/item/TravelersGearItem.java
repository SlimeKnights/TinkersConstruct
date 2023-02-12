package slimeknights.tconstruct.tools.item;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.IItemRenderProperties;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.item.ModifiableArmorItem;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.client.TravelersGearModel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class TravelersGearItem extends ModifiableArmorItem {
  /** Golden texture for armor */
  private static final String GOLDEN_ARMOR = TConstruct.resourceString("textures/models/armor/travelers_golden_1.png");
  /** Golden texture for leggings */
  private static final String GOLDEN_LEGS = TConstruct.resourceString("textures/models/armor/travelers_golden_2.png");

  public TravelersGearItem(ModifiableArmorMaterial material, ArmorSlotType slotType, Properties properties) {
    super(material, slotType, properties);
  }

  @Nullable
  @Override
  public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
    if (ModifierUtil.getModifierLevel(stack, TinkerModifiers.golden.getId()) > 0) {
      return slot == EquipmentSlot.LEGS ? GOLDEN_LEGS : GOLDEN_ARMOR;
    }
    return null;
  }

  @Override
  public void initializeClient(Consumer<IItemRenderProperties> consumer) {
    consumer.accept(new IItemRenderProperties() {
      @Nonnull
      @Override
      public Model getBaseArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel<?> _default) {
        return TravelersGearModel.getModel(itemStack, armorSlot, _default);
      }
    });
  }
}
