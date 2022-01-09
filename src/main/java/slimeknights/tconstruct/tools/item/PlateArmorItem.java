package slimeknights.tconstruct.tools.item;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.IItemRenderProperties;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.item.ModifiableArmorItem;
import slimeknights.tconstruct.tools.client.PlateArmorModel;

import java.util.function.Consumer;

public class PlateArmorItem extends ModifiableArmorItem {
  public PlateArmorItem(ModifiableArmorMaterial material, ArmorSlotType slotType, Properties properties) {
    super(material, slotType, properties);
  }

  @Override
  public void initializeClient(Consumer<IItemRenderProperties> consumer) {
    consumer.accept(new IItemRenderProperties() {
      @Override
      public <A extends HumanoidModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, A _default) {
        return PlateArmorModel.getModel(itemStack, armorSlot, _default);
      }
    });
  }
}
