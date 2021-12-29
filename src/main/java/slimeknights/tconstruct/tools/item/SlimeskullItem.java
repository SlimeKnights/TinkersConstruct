package slimeknights.tconstruct.tools.item;

import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.item.ModifiableArmorItem;

/** This item is mainly to return the proper model for a slimeskull */
public class SlimeskullItem extends ModifiableArmorItem {
  public SlimeskullItem(ModifiableArmorMaterial material, Properties properties) {
    super(material, ArmorSlotType.HELMET, properties);
  }
//
//  @Override
//  public void initializeClient(Consumer<IItemRenderProperties> consumer) {
//    consumer.accept(new IItemRenderProperties() {
//      @SuppressWarnings("unchecked")
//      @Override
//      public <A extends HumanoidModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, A _default) {
//        SlimeskullArmorModel.INSTANCE.setToolAndBase(itemStack, _default);
//        return (A)SlimeskullArmorModel.INSTANCE;
//      }
//    });
//  }
}
