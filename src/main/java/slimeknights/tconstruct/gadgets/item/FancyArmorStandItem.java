package slimeknights.tconstruct.gadgets.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.gadgets.entity.FancyArmorStandEntity;
import slimeknights.tconstruct.gadgets.entity.FancyArmorStandEntity.StandType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

/** Item to place {@link FancyArmorStandEntity} */
public class FancyArmorStandItem extends Item {
  private final StandType type;
  public FancyArmorStandItem(Properties pProperties, StandType type) {
    super(pProperties);
    this.type = type;
  }

  @Override
  public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> tooltip, TooltipFlag pIsAdvanced) {
    tooltip.add(Component.translatable(getDescriptionId() + ".tooltip").withStyle(ChatFormatting.GRAY));
  }

  /** Based on {@link net.minecraft.world.item.ArmorStandItem#useOn(UseOnContext)} */
  @Override
  public InteractionResult useOn(UseOnContext context) {
    Direction direction = context.getClickedFace();
    if (direction == Direction.DOWN) {
      return InteractionResult.FAIL;
    }

    Level level = context.getLevel();
    BlockPos pos = new BlockPlaceContext(context).getClickedPos();
    Vec3 center = Vec3.atBottomCenterOf(pos);
    AABB bounds = EntityType.ARMOR_STAND.getDimensions().makeBoundingBox(center.x(), center.y(), center.z());
    if (!level.noCollision(null, bounds) || !level.getEntities(null, bounds).isEmpty()) {
      return InteractionResult.FAIL;
    }

    ItemStack stack = context.getItemInHand();
    if (level instanceof ServerLevel server) {
      Player player = context.getPlayer();
      Consumer<FancyArmorStandEntity> consumer = EntityType.createDefaultStackConfig(server, stack, player);
      FancyArmorStandEntity stand = TinkerGadgets.armorStandEntity.get().create(server, stack.getTag(), consumer, pos, MobSpawnType.SPAWN_EGG, true, true);
      if (stand == null) {
        return InteractionResult.FAIL;
      }
      type.onPlace(stand);
      if (player != null) {
        // copy players hand, but invert if offhand placed it
        stand.setLeft((context.getHand() == InteractionHand.MAIN_HAND) == (player.getMainArm() == HumanoidArm.LEFT));
      }

      float rot = Mth.floor((Mth.wrapDegrees(context.getRotation() - 180f) + 22.5f) / 45f) * 45f;
      stand.moveTo(stand.getX(), stand.getY(), stand.getZ(), rot, 0.0F);
      server.addFreshEntityWithPassengers(stand);
      level.playSound(null, stand.getX(), stand.getY(), stand.getZ(), SoundEvents.ARMOR_STAND_PLACE, SoundSource.BLOCKS, 0.75F, 0.8F);
      stand.gameEvent(GameEvent.ENTITY_PLACE, context.getPlayer());
    }

    stack.shrink(1);
    return InteractionResult.sidedSuccess(level.isClientSide);
  }
}
