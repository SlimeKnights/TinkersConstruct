package slimeknights.tconstruct.tools.modifiers.defense;

import lombok.RequiredArgsConstructor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.data.ModifierMaxLevel;
import slimeknights.tconstruct.library.modifiers.impl.IncrementalModifier;
import slimeknights.tconstruct.library.modifiers.modules.armor.ProtectionModule;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

/** Base class for protection modifiers that want to keep track of the largest level for a bonus */
@RequiredArgsConstructor
public abstract class AbstractProtectionModifier<T extends ModifierMaxLevel> extends IncrementalModifier {
  private final TinkerDataKey<T> key;

  /** @deprecated use {@link #createData(EquipmentChangeContext)}*/
  @SuppressWarnings("DeprecatedIsStillUsed")
  @Deprecated
  protected abstract T createData();

  /** Creates a new data instance */
  protected T createData(EquipmentChangeContext context) {
    return createData();
  }

  /** @deprecated use {@link #reset(ModifierMaxLevel, EquipmentChangeContext)} */
  @Deprecated
  protected void reset(T data) {}

  /** Called when the last piece of equipment is removed to reset the data */
  protected void reset(T data, EquipmentChangeContext context) {
    reset(data);
  }

  /** Called to apply updates to the piece */
  protected void set(T data, EquipmentSlot slot, float scaledLevel, EquipmentChangeContext context) {
    data.set(slot, scaledLevel);
  }

  @Override
  public void onUnequip(IToolStackView tool, int level, EquipmentChangeContext context) {
    LivingEntity entity = context.getEntity();
    EquipmentSlot slot = context.getChangedSlot();
    if (ModifierUtil.validArmorSlot(tool, slot) && !entity.level.isClientSide) {
      context.getTinkerData().ifPresent(data -> {
        T modData = data.get(key);
        if (modData != null) {
          set(modData, slot, 0, context);
          if (modData.getMax() == 0) {
            reset(modData, context);
          }
        }
      });
    }
  }

  @Override
  public void onEquip(IToolStackView tool, int level, EquipmentChangeContext context) {
    LivingEntity entity = context.getEntity();
    EquipmentSlot slot = context.getChangedSlot();
    if (!entity.level.isClientSide && ModifierUtil.validArmorSlot(tool, slot) && !tool.isBroken()) {
      float scaledLevel = getScaledLevel(tool, level);
      context.getTinkerData().ifPresent(data -> {
        T modData = data.get(key);
        if (modData == null) {
          // not calculated yet? add all vanilla values to the tracker
          modData = createData(context);
          data.put(key, modData);
        }
        // add ourself to the data
        set(modData, slot, scaledLevel, context);
      });
    }
  }

  /** @deprecated use {@link ProtectionModule#addResistanceTooltip(IToolStackView, Modifier, float, Player, List)} */
  @Deprecated
  public static void addResistanceTooltip(Modifier modifier, IToolStackView tool, int level, float multiplier, List<Component> tooltip) {
    ProtectionModule.addResistanceTooltip(tool, modifier, modifier.getEffectiveLevel(tool, level) * multiplier, null, tooltip);
  }
}
