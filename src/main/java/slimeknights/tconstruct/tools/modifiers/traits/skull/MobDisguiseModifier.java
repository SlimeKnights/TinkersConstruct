package slimeknights.tconstruct.tools.modifiers.traits.skull;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot.Type;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.impl.SingleUseModifier;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

@RequiredArgsConstructor
public class MobDisguiseModifier extends SingleUseModifier {
  public static final TinkerDataKey<Multiset<EntityType<?>>> DISGUISES = TConstruct.createKey("mob_disguise");

  private final EntityType<?> type;

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
