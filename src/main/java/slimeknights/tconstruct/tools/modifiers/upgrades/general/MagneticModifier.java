package slimeknights.tconstruct.tools.modifiers.upgrades.general;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType.Group;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.hooks.IHarvestModifier;
import slimeknights.tconstruct.library.modifiers.hooks.IShearModifier;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.List;

public class MagneticModifier extends Modifier implements IHarvestModifier, IShearModifier {
  /** Player modifier data key for haste */
  private static final ResourceLocation MAGNET = TConstruct.getResource("magnet");

  public MagneticModifier() {
    super(0x720000);
    MinecraftForge.EVENT_BUS.addListener(MagneticModifier::onLivingTick);
  }

  @Override
  public void afterBlockBreak(IModifierToolStack tool, int level, ToolHarvestContext context) {
    if (!context.isAOE()) {
      TinkerModifiers.magneticEffect.get().apply(context.getLiving(), 30, level - 1);
    }
  }

  @Override
  public int afterEntityHit(IModifierToolStack tool, int level, ToolAttackContext context, float damageDealt) {
    if (!context.isExtraAttack()) {
      TinkerModifiers.magneticEffect.get().apply(context.getAttacker(), 30, level - 1);
    }
    return 0;
  }

  @Override
  public void afterHarvest(IModifierToolStack tool, int level, ItemUseContext context, ServerWorld world, BlockState state, BlockPos pos) {
    PlayerEntity player = context.getPlayer();
    if (player != null) {
      TinkerModifiers.magneticEffect.get().apply(player, 30, level - 1);
    }
  }

  @Override
  public void afterShearEntity(IModifierToolStack tool, int level, PlayerEntity player, Entity entity, boolean isTarget) {
    if (isTarget) {
      TinkerModifiers.magneticEffect.get().apply(player, 30, level - 1);
    }
  }

  @SuppressWarnings("unchecked")
  @Nullable
  @Override
  public <T> T getModule(Class<T> type) {
    if (type == IHarvestModifier.class || type == IShearModifier.class) {
      return (T) this;
    }
    return null;
  }


  // armor

  @Override
  public void onEquip(IModifierToolStack tool, int level, EquipmentChangeContext context) {
    if (context.getChangedSlot().getSlotType() == Group.ARMOR) {
      ModifierUtil.addTotalArmorModifierLevel(tool, context, MAGNET, level);
    }
  }

  @Override
  public void onUnequip(IModifierToolStack tool, int level, EquipmentChangeContext context) {
    if (context.getChangedSlot().getSlotType() == Group.ARMOR) {
      ModifierUtil.addTotalArmorModifierLevel(tool, context, MAGNET, -level);
    }
  }

  private static void onLivingTick(LivingUpdateEvent event) {
    LivingEntity entity = event.getEntityLiving();
    if ((entity.ticksExisted & 1) == 0) {
      int level = ModifierUtil.getTotalModifierLevel(entity, MAGNET);
      if (level > 0) {
        applyMagnet(entity, level);
      }
    }
  }

  /** Performs the magnetic effect */
  public static void applyMagnet(LivingEntity entity, int amplifier) {
    // super magnetic - inspired by botanias code
    double x = entity.getPosX();
    double y = entity.getPosY();
    double z = entity.getPosZ();
    float range = 3f + 1f * amplifier;
    List<ItemEntity> items = entity.getEntityWorld().getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(x - range, y - range, z - range, x + range, y + range, z + range));

    // only pull up to 200 items
    int pulled = 0;
    for (ItemEntity item : items) {
      if (item.getItem().isEmpty() || !item.isAlive()) {
        continue;
      }
      // calculate direction: item -> player
      Vector3d vec = entity.getPositionVec()
                           .subtract(item.getPosX(), item.getPosY(), item.getPosZ())
                           .normalize()
                           .scale(0.05f + amplifier * 0.05f);
      if (!item.hasNoGravity()) {
        vec = vec.add(0, 0.04f, 0);
      }

      // we calculated the movement vector and set it to the correct strength.. now we apply it \o/
      item.setMotion(item.getMotion().add(vec));

      // use stack size as limiting factor
      pulled += item.getItem().getCount();
      if (pulled > 200) {
        break;
      }
    }
  }
}
