package slimeknights.tconstruct.tools.modifiers.defense;

import lombok.RequiredArgsConstructor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.impl.IncrementalModifier;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.tools.logic.ModifierMaxLevel;

import java.util.List;

/** Base class for protection modifiers that want to keep track of the largest level for a bonus */
@RequiredArgsConstructor
public abstract class AbstractProtectionModifier<T extends ModifierMaxLevel> extends IncrementalModifier {
  private final TinkerDataKey<T> key;

  /** Creates a new data instance */
  protected abstract T createData();

  /** Called when the last piece of equipment is removed to reset the data */
  protected void reset(T data) {}

  @Override
  public void onUnequip(IToolStackView tool, int level, EquipmentChangeContext context) {
    LivingEntity entity = context.getEntity();
    EquipmentSlot slot = context.getChangedSlot();
    if (slot.getType() == Type.ARMOR && !entity.level.isClientSide) {
      context.getTinkerData().ifPresent(data -> {
        T modData = data.get(key);
        if (modData != null) {
          modData.set(slot, 0);
          if (modData.getMax() == 0) {
            reset(modData);
          }
        }
      });
    }
  }

  @Override
  public void onEquip(IToolStackView tool, int level, EquipmentChangeContext context) {
    LivingEntity entity = context.getEntity();
    EquipmentSlot slot = context.getChangedSlot();
    if (!entity.level.isClientSide && slot.getType() == Type.ARMOR && !tool.isBroken()) {
      float scaledLevel = getScaledLevel(tool, level);
      context.getTinkerData().ifPresent(data -> {
        T modData = data.get(key);
        if (modData == null) {
          // not calculated yet? add all vanilla values to the tracker
          modData = createData();
          data.put(key, modData);
        }
        // add ourself to the data
        modData.set(slot, scaledLevel);
      });
    }
  }

  /**
   * Adds the resistance type tooltip to the armor
   * @param modifier    Modifier instance
   * @param tool        Tool getting the tooltip
   * @param level       Modifier level
   * @param multiplier  Amount per level
   * @param tooltip     Tooltip to show
   */
  public static void addResistanceTooltip(IncrementalModifier modifier, IToolStackView tool, int level, float multiplier, List<Component> tooltip) {
    if (tool.hasTag(TinkerTags.Items.ARMOR)) {
      tooltip.add(modifier.applyStyle(new TextComponent(Util.PERCENT_BOOST_FORMAT.format(Math.min(modifier.getScaledLevel(tool, level) * multiplier / 25f, 0.8f)))
                                        .append(" ")
                                        .append(new TranslatableComponent(modifier.getTranslationKey() + ".resistance"))));
    }
  }
}
