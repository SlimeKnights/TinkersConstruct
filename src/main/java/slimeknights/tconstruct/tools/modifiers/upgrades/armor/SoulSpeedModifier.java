package slimeknights.tconstruct.tools.modifiers.upgrades.armor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.hooks.IArmorWalkModifier;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.TooltipKey;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class SoulSpeedModifier extends Modifier implements IArmorWalkModifier {
  /** UUID for speed boost */
  private static final UUID ATTRIBUTE_BONUS = UUID.fromString("f61dde72-5b8d-11ec-bf63-0242ac130002");

  /** Gets the position this entity is standing on, cloned from protected living entity method */
  private static BlockPos getOnPosition(LivingEntity living) {
    Vec3 position = living.position();
    int x = Mth.floor(position.x);
    int y = Mth.floor(position.y - (double)0.2F);
    int z = Mth.floor(position.z);
    BlockPos pos = new BlockPos(x, y, z);
    if (living.level.isEmptyBlock(pos)) {
      BlockPos below = pos.below();
      BlockState blockstate = living.level.getBlockState(below);
      if (blockstate.collisionExtendsVertically(living.level, below, living)) {
        return below;
      }
    }

    return pos;
  }

  @Override
  public void onWalk(IToolStackView tool, int level, LivingEntity living, BlockPos prevPos, BlockPos newPos) {
    // no point trying if not on the ground
    if (tool.isBroken() || !living.isOnGround() || living.level.isClientSide) {
      return;
    }
    // must have speed
    AttributeInstance attribute = living.getAttribute(Attributes.MOVEMENT_SPEED);
    if (attribute == null) {
      return;
    }
    // not above air
    BlockPos belowPos = getOnPosition(living);
    BlockState below = living.level.getBlockState(belowPos);
    if (below.isAir()) {
      return;
    }

    // start by removing the attribute, we are likely going to give it a new number
    if (attribute.getModifier(ATTRIBUTE_BONUS) != null) {
      attribute.removeModifier(ATTRIBUTE_BONUS);
    }

    // add back speed boost if above a soul speed block and not flying
    if (!living.isFallFlying() && below.is(BlockTags.SOUL_SPEED_BLOCKS)) {
      Random rand = living.getRandom();

      // boost speed
      float boost = (0.03f + level * 0.0105f);
      float speedFactor = below.getBlock().getSpeedFactor();
      if (speedFactor != 1.0f) {
        boost *= (1 / speedFactor);
      }
      attribute.addTransientModifier(new AttributeModifier(ATTRIBUTE_BONUS, "tconstruct.modifier.soul_speed", boost, Operation.ADDITION));

      // damage boots
      if (rand.nextFloat() < 0.04F) {
        ToolDamageUtil.damageAnimated(tool, 1, living, EquipmentSlot.FEET);
      }

      // particles and sounds
      Vec3 motion = living.getDeltaMovement();
      if (living.level instanceof ServerLevel) {
        ((ServerLevel)living.level).sendParticles(ParticleTypes.SOUL,
                                 living.getX() + (rand.nextDouble() - 0.5) * living.getBbWidth(),
                                 living.getY() + 0.1,
                                 living.getZ() + (rand.nextDouble() - 0.5) * living.getBbWidth(),
                                 0, motion.x * -0.2, 0.1, motion.z * -0.2, 1);
      }
      living.level.playSound(null, living.getX(), living.getY(), living.getZ(), SoundEvents.SOUL_ESCAPE, living.getSoundSource(), rand.nextFloat() * 0.4f + rand.nextFloat() > 0.9f ? 0.6f : 0.0f, 0.6f + rand.nextFloat() * 0.4f);
    }
  }

  @Override
  public void onUnequip(IToolStackView tool, int level, EquipmentChangeContext context) {
    // remove boost when boots are removed
    LivingEntity livingEntity = context.getEntity();
    if (!livingEntity.level.isClientSide && context.getChangedSlot() == EquipmentSlot.FEET) {
      IToolStackView newTool = context.getReplacementTool();
      // damaging the tool will trigger this hook, so ensure the new tool has the same level
      if (newTool == null || newTool.isBroken() || newTool.getModifierLevel(this) != level) {
        AttributeInstance attribute = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
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
  public void addInformation(IToolStackView tool, int level, @Nullable Player player, List<Component> tooltip, TooltipKey key, TooltipFlag tooltipFlag) {
    // must either have no player or a player on soulsand
    if (player == null || key != TooltipKey.SHIFT || (!player.isFallFlying() && player.level.getBlockState(getOnPosition(player)).is(BlockTags.SOUL_SPEED_BLOCKS))) {
      // multiplies boost by 10 and displays as a percent as the players base movement speed is 0.1 and is in unknown units
      // percentages make sense
      addPercentTooltip(getDisplayName(), 0.3f + level * 0.105f, tooltip);
    }
  }
}
