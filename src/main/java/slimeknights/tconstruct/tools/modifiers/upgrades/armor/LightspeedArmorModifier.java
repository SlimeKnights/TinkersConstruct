package slimeknights.tconstruct.tools.modifiers.upgrades.armor;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.LightType;
import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.modifiers.hooks.IArmorWalkModifier;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.utils.TooltipFlag;
import slimeknights.tconstruct.library.utils.TooltipKey;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class LightspeedArmorModifier extends IncrementalModifier implements IArmorWalkModifier {
  /** UUID for speed boost */
  private static final UUID ATTRIBUTE_BONUS = UUID.fromString("8790747b-6654-4bd8-83c7-dbe9ae04c0ca");
  public LightspeedArmorModifier() {
    super(0xFFBC5E);
  }

  @Override
  public void onWalk(IModifierToolStack tool, int level, LivingEntity living, BlockPos prevPos, BlockPos newPos) {
    // no point trying if not on the ground
    if (tool.isBroken() || !living.isOnGround() || living.level.isClientSide) {
      return;
    }
    // must have speed
    ModifiableAttributeInstance attribute = living.getAttribute(Attributes.MOVEMENT_SPEED);
    if (attribute == null) {
      return;
    }
    // start by removing the attribute, we are likely going to give it a new number
    if (attribute.getModifier(ATTRIBUTE_BONUS) != null) {
      attribute.removeModifier(ATTRIBUTE_BONUS);
    }

    // not above air
    Vector3d vecPos = living.position();
    BlockPos pos = new BlockPos(vecPos.x, vecPos.y + 0.5f, vecPos.z);
    int light = living.level.getBrightness(LightType.BLOCK, pos);
    if (light > 5) {
      int scaledLight = light - 5;
      attribute.addTransientModifier(new AttributeModifier(ATTRIBUTE_BONUS, "tconstruct.modifier.lightspeed", scaledLight * 0.0015f * getScaledLevel(tool, level), Operation.ADDITION));

      // damage boots
      if (RANDOM.nextFloat() < (0.005f * scaledLight)) {
        ToolDamageUtil.damageAnimated(tool, 1, living, EquipmentSlotType.FEET);
      }
    }
  }

  @Override
  public void onUnequip(IModifierToolStack tool, int level, EquipmentChangeContext context) {
    // remove boost when boots are removed
    LivingEntity livingEntity = context.getEntity();
    if (context.getChangedSlot() == EquipmentSlotType.FEET) {
      IModifierToolStack newTool = context.getReplacementTool();
      // damaging the tool will trigger this hook, so ensure the new tool has the same level
      if (newTool == null || newTool.isBroken() || getScaledLevel(newTool, newTool.getModifierLevel(this)) != getScaledLevel(tool, level)) {
        ModifiableAttributeInstance attribute = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attribute != null && attribute.getModifier(ATTRIBUTE_BONUS) != null) {
          attribute.removeModifier(ATTRIBUTE_BONUS);
        }
      }
    }
  }

  @Nullable
  @Override
  public <T> T getModule(Class<T> type) {
    return tryModuleMatch(type, IArmorWalkModifier.class, this);
  }

  @Override
  public void addInformation(IModifierToolStack tool, int level, @Nullable PlayerEntity player, List<ITextComponent> tooltip, TooltipKey key, TooltipFlag tooltipFlag) {
    // multiplies boost by 10 and displays as a percent as the players base movement speed is 0.1 and is in unknown units
    // percentages make sense
    float boost;
    if (player != null && key == TooltipKey.SHIFT) {
      int light = player.level.getBrightness(LightType.BLOCK, player.blockPosition());
      boost = 0.015f * (light - 5) * getScaledLevel(tool, level);
    } else {
      boost = 0.15f * getScaledLevel(tool, level);
    }
    if (boost > 0) {
      addPercentTooltip(getDisplayName(), boost, tooltip);
    }
  }
}
