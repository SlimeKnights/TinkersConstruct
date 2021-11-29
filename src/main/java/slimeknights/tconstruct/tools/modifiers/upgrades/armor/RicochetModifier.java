package slimeknights.tconstruct.tools.modifiers.upgrades.armor;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class RicochetModifier extends Modifier {
  private static final TinkerDataKey<Integer> LEVELS = TConstruct.createKey("ricochet");
  public RicochetModifier() {
    super(0x01cbcd);
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
    event.getEntityLiving().getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> {
      int levels = data.get(LEVELS, 0);
      if (levels > 0) {
        // adds +20% knockback per level
        event.setStrength(event.getStrength() * (1 + levels * 0.2f));
      }
    });
  }
}
