package slimeknights.tconstruct.tools.modifiers.ability.armor;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.capability.EntityModifierDataCapability;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import java.util.Random;

public class DoubleJumpModifier extends Modifier {
  public static final ResourceLocation EXTRA_JUMPS = TConstruct.getResource("extra_jumps");
  public static final ResourceLocation JUMPS = TConstruct.getResource("jumps");

  private ITextComponent levelOneName = null;
  private ITextComponent levelTwoName = null;

  public DoubleJumpModifier() {
    super(0xFF950D);
    MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, DoubleJumpModifier::onLand);
  }

  @Override
  public void onUnequip(IModifierToolStack tool, int level, EquipmentChangeContext context) {
    ModifierUtil.addTotalArmorModifierLevel(tool, context, EXTRA_JUMPS, -level);
  }

  @Override
  public void onEquip(IModifierToolStack tool, int level, EquipmentChangeContext context) {
    ModifierUtil.addTotalArmorModifierLevel(tool, context, EXTRA_JUMPS, level);
  }

  @Override
  public ITextComponent getDisplayName(int level) {
    if (level == 1) {
      if (levelOneName == null) {
        levelOneName = applyStyle(new TranslationTextComponent(getTranslationKey() + ".double"));
      }
      return levelOneName;
    }
    if (level == 2) {
      if (levelTwoName == null) {
        levelTwoName = applyStyle(new TranslationTextComponent(getTranslationKey() + ".triple"));
      }
      return levelTwoName;
    }
    return super.getDisplayName(level);
  }

  /**
   * Causes the player to jump an extra time, if possible
   * @param entity  Entity instance who wishes to jump again
   * @return  True if the entity jumpped, false if not
   */
  public static boolean extraJump(PlayerEntity entity) {
    // validate preconditions, no using when swimming, elytra, or on the ground
    if (!entity.isOnGround() && !entity.isOnLadder() && !entity.isInWaterOrBubbleColumn()) {
      // determine modifier level
      int maxJumps = ModifierUtil.getTotalModifierLevel(entity, EXTRA_JUMPS);
      if (maxJumps > 0) {
        return entity.getCapability(EntityModifierDataCapability.CAPABILITY).filter(data -> {
          int jumps = data.getInt(JUMPS);
          if (jumps < maxJumps) {
            entity.jump();
            Random random = entity.getEntityWorld().getRandom();
            for (int i = 0; i < 4; i++) {
              entity.getEntityWorld().addParticle(ParticleTypes.HAPPY_VILLAGER, entity.getPosX() - 0.25f + random.nextFloat() * 0.5f, entity.getPosY(), entity.getPosZ() - 0.25f + random.nextFloat() * 0.5f, 0, 0, 0);
            }
            entity.playSound(Sounds.EXTRA_JUMP.getSound(), 0.5f, 0.5f);
            data.putInt(JUMPS, jumps + 1);
            return true;
          }
          return false;
        }).isPresent();
      }
    }
    return false;
  }

  /** Event handler to reset the number of times we have jumpped in mid air */
  private static void onLand(LivingFallEvent event) {
    event.getEntity().getCapability(EntityModifierDataCapability.CAPABILITY).ifPresent(data -> data.remove(JUMPS));
  }
}
