package slimeknights.tconstruct.tools.item;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.IItemRenderProperties;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.tools.client.SlimelytraArmorModel;

import java.util.function.Consumer;

public class SlimelytraItem extends SlimesuitItem {
  public SlimelytraItem(ModifiableArmorMaterial material, Properties properties) {
    super(material, ArmorSlotType.CHESTPLATE, properties);
  }

  @Override
  public void initializeClient(Consumer<IItemRenderProperties> consumer) {
    consumer.accept(new IItemRenderProperties() {
      @Override
      public <A extends HumanoidModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, A _default) {
        return SlimelytraArmorModel.getModel(entityLiving, itemStack, _default);
      }
    });
  }
}
