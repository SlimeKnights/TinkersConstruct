package slimeknights.tconstruct.tools.modifiers.upgrades.armor;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.capability.EntityModifierDataCapability;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class RicochetModifier extends Modifier {
  private static final ResourceLocation LEVELS = TConstruct.getResource("ricochet_levels");
  public RicochetModifier() {
    super(0x76BE6D);
    MinecraftForge.EVENT_BUS.addListener(this::livingKnockback);
  }

  @Override
  public void onUnequip(IModifierToolStack tool, int level, EquipmentChangeContext context) {
    if (!context.getEntity().getEntityWorld().isRemote) {
      ModifierUtil.addTotalArmorModifierLevel(tool, context, LEVELS, -level);
    }
  }

  @Override
  public void onEquip(IModifierToolStack tool, int level, EquipmentChangeContext context) {
    if (!context.getEntity().getEntityWorld().isRemote) {
      ModifierUtil.addTotalArmorModifierLevel(tool, context, LEVELS, level);
    }
  }

  /** Called on knockback to adjust player knockback */
  private void livingKnockback(LivingKnockBackEvent event) {
    event.getEntityLiving().getCapability(EntityModifierDataCapability.CAPABILITY).ifPresent(data -> {
      int levels = data.getInt(LEVELS);
      if (levels > 0) {
        // adds +10% knockback per level
        event.setStrength(event.getStrength() * (1 + levels * 0.2f));
      }
    });
  }
}
