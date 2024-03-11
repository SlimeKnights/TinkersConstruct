package slimeknights.tconstruct.tools.modifiers.traits.skull;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class BoonOfSssssModifier extends NoLevelsModifier {
  public BoonOfSssssModifier() {
    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, PotionEvent.PotionAddedEvent.class, this::onPotionStart);
  }

  @Override
  public void onUnequip(IToolStackView tool, int level, EquipmentChangeContext context) {
    super.onUnequip(tool, level, context);
    if (context.getChangedSlot() == EquipmentSlot.HEAD) {
      IToolStackView replacement = context.getReplacementTool();
      if (replacement == null || replacement.getModifierLevel(this) == 0 || replacement.getItem() != tool.getItem()) {
        // cure effects using the helmet
        context.getEntity().curePotionEffects(new ItemStack(tool.getItem()));
      }
    }
  }

  /** Called when the potion effects start to apply this effect */
  private void onPotionStart(PotionEvent.PotionAddedEvent event) {
    MobEffectInstance newEffect = event.getPotionEffect();
    if (newEffect.getEffect().isBeneficial() && !newEffect.getCurativeItems().isEmpty()) {
      LivingEntity living = event.getEntityLiving();
      // strong bones has to be the helmet as we use it for curing
      // TODO 1.20: can use the new cure effects to make this work in any slot
      ItemStack helmet = living.getItemBySlot(EquipmentSlot.HEAD);
      if (ModifierUtil.getModifierLevel(helmet, this.getId()) > 0) {
        newEffect.duration *= 1.25f;
        newEffect.getCurativeItems().add(new ItemStack(helmet.getItem()));
      }
    }
  }
}
