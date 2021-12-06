package slimeknights.tconstruct.tools.modifiers.traits.skull;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType.Group;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingVisibilityEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class MobDisguiseModifier extends SingleUseModifier {
  private static final TinkerDataKey<Multiset<EntityType<?>>> DISGUISES = TConstruct.createKey("mob_disguise");
  private static boolean registeredListener = false;
  private final EntityType<?> type;
  public MobDisguiseModifier(int color, EntityType<?> type) {
    super(color);
    this.type = type;
    if (!registeredListener) {
      registeredListener = true;
      MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, LivingVisibilityEvent.class, MobDisguiseModifier::livingVisibility);
    }
  }

  @Override
  public void onEquip(IModifierToolStack tool, int level, EquipmentChangeContext context) {
    if (context.getChangedSlot().getSlotType() == Group.ARMOR) {
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
  public void onUnequip(IModifierToolStack tool, int level, EquipmentChangeContext context) {
    if (context.getChangedSlot().getSlotType() == Group.ARMOR) {
      context.getTinkerData().ifPresent(data -> {
        Multiset<EntityType<?>> disguises = data.get(DISGUISES);
        if (disguises != null) {
          disguises.remove(type);
        }
      });
    }
  }

  /** Reduces visibility to mobs */
  private static void livingVisibility(LivingVisibilityEvent event) {
    Entity lookingEntity = event.getLookingEntity();
    if (lookingEntity == null) {
      return;
    }
    LivingEntity living = event.getEntityLiving();
    living.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> {
      Multiset<EntityType<?>> disguises = data.get(DISGUISES);
      if (disguises != null && disguises.contains(lookingEntity.getType())) {
        // not as good as a real head
        event.modifyVisibility(0.65f);
      }
    });
  }
}
