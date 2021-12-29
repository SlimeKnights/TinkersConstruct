package slimeknights.tconstruct.tools.item;

import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.item.ModifiableArmorItem;

public class SlimelytraItem extends ModifiableArmorItem {
  public SlimelytraItem(ModifiableArmorMaterial material, Properties properties) {
    super(material, ArmorSlotType.CHESTPLATE, properties);
  }
//
//  @Override
//  public void initializeClient(Consumer<IItemRenderProperties> consumer) {
//    consumer.accept(new IItemRenderProperties() {
//      @SuppressWarnings("unchecked")
//      @Override
//      public <A extends HumanoidModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, A _default) {
//        SlimelytraArmorModel.INSTANCE.setEntityAndBase(entityLiving, _default);
//        return (A)SlimelytraArmorModel.INSTANCE;
//      }
//    });
//  }
}
