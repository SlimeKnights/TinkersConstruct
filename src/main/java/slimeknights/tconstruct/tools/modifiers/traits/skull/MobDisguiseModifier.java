package slimeknights.tconstruct.tools.modifiers.traits.skull;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot.Type;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class MobDisguiseModifier extends SingleUseModifier {
  public static final TinkerDataKey<Multiset<EntityType<?>>> DISGUISES = TConstruct.createKey("mob_disguise");
  private static boolean registeredListener = false;
  private final EntityType<?> type;
  public MobDisguiseModifier(int color, EntityType<?> type) {
    super(color);
    this.type = type;
    if (!registeredListener) {
      registeredListener = true;
    }
  }

  @Override
  public void onEquip(IToolStackView tool, int level, EquipmentChangeContext context) {
    if (context.getChangedSlot().getType() == Type.ARMOR) {
      context.getTinkerData().ifPresent(data -> {
        Multiset<EntityType<?>> disguises = data.get(DISGUISES);
        if (disguises == null) {
          disguises = HashMultiset.create();
          data.put(DISGUISES, disguises);
        }
        disguises.add(type);
      });
    }
  }

  @Override
  public void onUnequip(IToolStackView tool, int level, EquipmentChangeContext context) {
    if (context.getChangedSlot().getType() == Type.ARMOR) {
      context.getTinkerData().ifPresent(data -> {
        Multiset<EntityType<?>> disguises = data.get(DISGUISES);
        if (disguises != null) {
          disguises.remove(type);
        }
      });
    }
  }
}
