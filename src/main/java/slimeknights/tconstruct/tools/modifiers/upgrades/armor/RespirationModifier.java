package slimeknights.tconstruct.tools.modifiers.upgrades.armor;

import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectUtils;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class RespirationModifier extends Modifier {
  private static final TinkerDataKey<Integer> RESPIRATION = TConstruct.createKey("respiration");
  public RespirationModifier() {
    super(0x47BF4A);
    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, LivingUpdateEvent.class, RespirationModifier::livingTick);
  }

  @Override
  public void onEquip(IModifierToolStack tool, int level, EquipmentChangeContext context) {
    ModifierUtil.addTotalArmorModifierLevel(tool, context, RESPIRATION, level);
  }

  @Override
  public void onUnequip(IModifierToolStack tool, int level, EquipmentChangeContext context) {
    ModifierUtil.addTotalArmorModifierLevel(tool, context, RESPIRATION, -level);
  }

  /** Big mess of conditions from living tick for when air goes down */
  private static boolean isLosingAir(LivingEntity living) {
    return living.isAlive()
           && !living.canBreatheUnderwater()
           && living.areEyesInFluid(FluidTags.WATER)
           && !EffectUtils.canBreatheUnderwater(living)
           && !(living instanceof PlayerEntity && ((PlayerEntity) living).abilities.disableDamage)
           && !living.world.getBlockState(new BlockPos(living.getPosX(), living.getPosYEye(), living.getPosZ())).matchesBlock(Blocks.BUBBLE_COLUMN);
  }

  /** Called before air is lost to add an air buffer */
  private static void livingTick(LivingUpdateEvent event) {
    LivingEntity living = event.getEntityLiving();
    living.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> {
      int respiration = data.get(RESPIRATION, 0);
      int air = living.getAir();
      // vanilla has a chance of not losing air with the effect, easiest to implement is just giving some air back
      if (respiration > 0 && air < living.getMaxAir() && isLosingAir(living) && RANDOM.nextInt(respiration + 1) > 0) {
        living.setAir(air + 1);
      }
    });
  }
}
