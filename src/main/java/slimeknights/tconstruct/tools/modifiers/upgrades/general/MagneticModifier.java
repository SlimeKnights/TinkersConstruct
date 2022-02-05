package slimeknights.tconstruct.tools.modifiers.upgrades.general;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.hooks.IHarvestModifier;
import slimeknights.tconstruct.library.modifiers.hooks.IShearModifier;
import slimeknights.tconstruct.library.modifiers.impl.TotalArmorLevelModifier;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.List;

public class MagneticModifier extends TotalArmorLevelModifier implements IHarvestModifier, IShearModifier {
  /** Player modifier data key for haste */
  private static final TinkerDataKey<Integer> MAGNET = TConstruct.createKey("magnet");

  public MagneticModifier() {
    super(MAGNET);
    MinecraftForge.EVENT_BUS.addListener(MagneticModifier::onLivingTick);
  }

  @Override
  public void afterBlockBreak(IToolStackView tool, int level, ToolHarvestContext context) {
    if (!context.isAOE()) {
      TinkerModifiers.magneticEffect.get().apply(context.getLiving(), 30, level - 1);
    }
  }

  @Override
  public int afterEntityHit(IToolStackView tool, int level, ToolAttackContext context, float damageDealt) {
    if (!context.isExtraAttack()) {
      TinkerModifiers.magneticEffect.get().apply(context.getAttacker(), 30, level - 1);
    }
    return 0;
  }

  @Override
  public void afterHarvest(IToolStackView tool, int level, UseOnContext context, ServerLevel world, BlockState state, BlockPos pos) {
    Player player = context.getPlayer();
    if (player != null) {
      TinkerModifiers.magneticEffect.get().apply(player, 30, level - 1);
    }
  }

  @Override
  public void afterShearEntity(IToolStackView tool, int level, Player player, Entity entity, boolean isTarget) {
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

  /** Called to perform the magnet for armor */
  private static void onLivingTick(LivingUpdateEvent event) {
    LivingEntity entity = event.getEntityLiving();
    if (!entity.isSpectator() && (entity.tickCount & 1) == 0) {
      int level = ModifierUtil.getTotalModifierLevel(entity, MAGNET);
      if (level > 0) {
        applyMagnet(entity, level);
      }
    }
  }

  /** Performs the magnetic effect */
  public static void applyMagnet(LivingEntity entity, int amplifier) {
    // super magnetic - inspired by botanias code
    double x = entity.getX();
    double y = entity.getY();
    double z = entity.getZ();
    float range = 3f + 1f * amplifier;
    List<ItemEntity> items = entity.level.getEntitiesOfClass(ItemEntity.class, new AABB(x - range, y - range, z - range, x + range, y + range, z + range));

    // only pull up to 200 items
    int pulled = 0;
    for (ItemEntity item : items) {
      if (item.getItem().isEmpty() || !item.isAlive()) {
        continue;
      }
      // calculate direction: item -> player
      Vec3 vec = entity.position()
                       .subtract(item.getX(), item.getY(), item.getZ())
                       .normalize()
                       .scale(0.05f + amplifier * 0.05f);
      if (!item.isNoGravity()) {
        vec = vec.add(0, 0.04f, 0);
      }

      // we calculated the movement vector and set it to the correct strength.. now we apply it \o/
      item.setDeltaMovement(item.getDeltaMovement().add(vec));

      // use stack size as limiting factor
      pulled += item.getItem().getCount();
      if (pulled > 200) {
        break;
      }
    }
  }
}
