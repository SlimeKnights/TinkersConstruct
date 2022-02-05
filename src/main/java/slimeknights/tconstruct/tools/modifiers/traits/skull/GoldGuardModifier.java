package slimeknights.tconstruct.tools.modifiers.traits.skull;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.impl.SingleUseModifier;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.ComputableDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.item.ModifiableArmorItem;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.TooltipKey;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class GoldGuardModifier extends SingleUseModifier {
  private static final UUID GOLD_GUARD_UUID = UUID.fromString("fbae11f1-b547-47e8-ae0c-f2cf24a46d93");
  private static final ComputableDataKey<GoldGuardGold> TOTAL_GOLD = TConstruct.createKey("gold_guard", GoldGuardGold::new);

  @Override
  public void onEquip(IToolStackView tool, int level, EquipmentChangeContext context) {
    // adding a helmet? activate bonus
    if (context.getChangedSlot() == EquipmentSlot.HEAD) {
      context.getTinkerData().ifPresent(data -> {
        GoldGuardGold gold = data.get(TOTAL_GOLD);
        if (gold == null) {
          data.computeIfAbsent(TOTAL_GOLD).initialize(context);
        } else {
          gold.setGold(EquipmentSlot.HEAD, tool.getVolatileData().getBoolean(ModifiableArmorItem.PIGLIN_NEUTRAL), context.getEntity());
        }
      });
    }
  }

  @Override
  public void onUnequip(IToolStackView tool, int level, EquipmentChangeContext context) {
    if (context.getChangedSlot() == EquipmentSlot.HEAD) {
      IToolStackView newTool = context.getReplacementTool();
      // when replacing with a helmet that lacks this modifier, remove bonus
      if (newTool == null || newTool.getModifierLevel(this) == 0) {
        context.getTinkerData().ifPresent(data -> data.remove(TOTAL_GOLD));
        AttributeInstance instance = context.getEntity().getAttribute(Attributes.MAX_HEALTH);
        if (instance != null) {
          instance.removeModifier(GOLD_GUARD_UUID);
        }
      }
    }
  }

  @Override
  public void onEquipmentChange(IToolStackView tool, int level, EquipmentChangeContext context, EquipmentSlot slotType) {
    // adding a helmet? activate bonus
    EquipmentSlot changed = context.getChangedSlot();
    if (slotType == EquipmentSlot.HEAD && changed.getType() == Type.ARMOR) {
      LivingEntity living = context.getEntity();
      boolean hasGold = ChrysophiliteModifier.hasGold(context, changed);
      context.getTinkerData().ifPresent(data -> data.computeIfAbsent(TOTAL_GOLD).setGold(changed, hasGold, living));
    }
  }

  @Override
  public void addInformation(IToolStackView tool, int level, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    if (player != null && tooltipKey == TooltipKey.SHIFT) {
      AttributeInstance instance = player.getAttribute(Attributes.MAX_HEALTH);
      if (instance != null) {
        AttributeModifier modifier = instance.getModifier(GOLD_GUARD_UUID);
        if (modifier != null) {
          tooltip.add(applyStyle(new TextComponent(Util.BONUS_FORMAT.format(modifier.getAmount()) + " ")
                                   .append(new TranslatableComponent(getTranslationKey() + "." + "health"))));
        }
      }
    }
  }

  /** Internal logic to update gold on the player */
  private static class GoldGuardGold extends ChrysophiliteModifier.TotalGold {
    /** Adds the health boost to the player */
    private void updateAttribute(LivingEntity living) {
      // update attribute
      AttributeInstance instance = living.getAttribute(Attributes.MAX_HEALTH);
      if (instance != null) {
        if (instance.getModifier(GOLD_GUARD_UUID) != null) {
          instance.removeModifier(GOLD_GUARD_UUID);
        }
        // +2 hearts per level, and a bonus of 2 for having the modifier
        instance.addTransientModifier(new AttributeModifier(GOLD_GUARD_UUID, "tconstruct.gold_guard", getTotalGold() * 4, Operation.ADDITION));
      }
    }

    /** Sets the slot to having gold or not and updates the attribute */
    public void setGold(EquipmentSlot slotType, boolean value, LivingEntity living) {
      if (setGold(slotType, value)) {
        updateAttribute(living);
      }
    }

    @Override
    public void initialize(EquipmentChangeContext context) {
      super.initialize(context);
      updateAttribute(context.getEntity());
    }
  }
}
