package slimeknights.tconstruct.tools.item;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.IItemRenderProperties;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.tools.client.SlimelytraArmorModel;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class SlimelytraItem extends SlimesuitItem {
  public SlimelytraItem(ModifiableArmorMaterial material, Properties properties) {
    super(material, ArmorSlotType.CHESTPLATE, properties);
  }

  @Override
  public void initializeClient(Consumer<IItemRenderProperties> consumer) {
    consumer.accept(new IItemRenderProperties() {
      @Nonnull
      @Override
      public Model getBaseArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel<?> _default) {
        return SlimelytraArmorModel.getModel(entityLiving, itemStack, _default);
      }
    });
  }
}
