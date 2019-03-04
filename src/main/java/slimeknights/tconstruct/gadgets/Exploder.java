package slimeknights.tconstruct.gadgets;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import slimeknights.tconstruct.common.TinkerNetwork;
import slimeknights.tconstruct.gadgets.entity.ExplosionEFLN;
import slimeknights.tconstruct.tools.common.network.EntityMovementChangePacket;

public class Exploder {

  public final double r;
  public final double rr;
  public final int dist;
  public final double explosionStrength;
  public final int blocksPerIteration;
  public final int x, y, z;
  public final World world;
  public final Entity exploder;
  public final ExplosionEFLN explosion;

  protected int currentRadius;
  private int curX, curY, curZ;

  protected List<ItemStack> droppedItems; // map containing all items dropped by the explosion and their amounts

  public Exploder(World world, ExplosionEFLN explosion, Entity exploder, BlockPos location, double r, double explosionStrength, int blocksPerIteration) {
    this.r = r;
    this.world = world;
    this.explosion = explosion;
    this.exploder = exploder;
    this.rr = r * r;
    this.dist = (int) r + 1;
    this.explosionStrength = explosionStrength;
    this.blocksPerIteration = blocksPerIteration;
    this.currentRadius = 0;

    this.x = location.getX();
    this.y = location.getY();
    this.z = location.getZ();

    this.curX = 0;
    this.curY = 0;
    this.curZ = 0;

    this.droppedItems = Lists.newArrayList();
  }

  public static void startExplosion(World world, ExplosionEFLN explosion, Entity entity, BlockPos location, double r, double explosionStrength) {
    Exploder exploder = new Exploder(world, explosion, entity, location, r, explosionStrength, Math.max(50, (int) (r * r * r / 10d)));
    exploder.handleEntities();
    world.playSound(null, location, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);
    MinecraftForge.EVENT_BUS.register(exploder);
  }

  @SubscribeEvent
  public void onTick(TickEvent.WorldTickEvent event) {
    if(event.world == world && event.phase == TickEvent.Phase.END) {
      if(!iteration()) {
        // goodbye world, we're done exploding
        finish();
      }
    }
  }

  void handleEntities() {
    final Predicate<Entity> predicate = entity -> entity != null
                                              && !entity.isImmuneToExplosions()
                                              && EntitySelectors.NOT_SPECTATING.apply(entity)
                                              && EntitySelectors.IS_ALIVE.apply(entity)
                                              && entity.getPositionVector().squareDistanceTo(x, y, z) <= r * r;

    // damage and blast back entities
    List<Entity> list = world.getEntitiesInAABBexcluding(this.exploder,
                                                         new AxisAlignedBB(x - r - 1,
                                                                           y - r - 1,
                                                                           z - r - 1,
                                                                           x + r + 1,
                                                                           y + r + 1,
                                                                           z + r + 1),
                                                         predicate
    );
    net.minecraftforge.event.ForgeEventFactory.onExplosionDetonate(world, explosion, list, r * 2);

    for(Entity entity : list) {
      //double str = 1f - (double)currentRadius/r;
      //str *= str;
      // move it away from the center depending on distance and explosion strength
      Vec3d dir = entity.getPositionVector().subtract(exploder.getPositionVector().addVector(0, -r / 2, 0));
      double str = (r - dir.lengthVector()) / r;
      str = Math.max(0.3, str);
      dir = dir.normalize();
      dir = dir.scale(explosionStrength * str * 0.3);
      entity.addVelocity(dir.x, dir.y + 0.5, dir.z);
      entity.attackEntityFrom(DamageSource.causeExplosionDamage(explosion), (float) (str * explosionStrength));

      if(entity instanceof EntityPlayerMP) {
        TinkerNetwork.sendTo(new EntityMovementChangePacket(entity), (EntityPlayerMP) entity);
      }
    }
  }

  private void finish() {
    final int d = (int) r / 2;
    final BlockPos pos = new BlockPos(x - d, y - d, z - d);
    final Random random = new Random();

    List<ItemStack> aggregatedDrops = Lists.newArrayList();

    for(ItemStack drop : droppedItems) {
      boolean notInList = true;

      // check if it's already in our list
      for(ItemStack stack : aggregatedDrops) {
        if(ItemStack.areItemsEqual(drop, stack) && ItemStack.areItemStackTagsEqual(drop, stack)) {
          stack.grow(drop.getCount());
          notInList = false;
          break;
        }
      }

      if(notInList) {
        aggregatedDrops.add(drop);
      }
    }

    // actually drop the aggregated items
    for(ItemStack drop : aggregatedDrops) {
      int stacksize = drop.getCount();
      do {
        BlockPos spawnPos = pos.add(random.nextInt((int) r), random.nextInt((int) r), random.nextInt((int) r));
        ItemStack dropItemstack = drop.copy();
        dropItemstack.setCount(Math.min(stacksize, 64));
        Block.spawnAsEntity(world, spawnPos, dropItemstack);
        stacksize -= dropItemstack.getCount();
      } while(stacksize > 0);
    }

    MinecraftForge.EVENT_BUS.unregister(this);
  }

  /**
   * Explodes away all blocks for the current iteration
   */
  private boolean iteration() {
    int count = 0;

    explosion.clearAffectedBlockPositions();

    while(count < blocksPerIteration && currentRadius < (int) r + 1) {
      double d = curX * curX + curY * curY + curZ * curZ;
      // inside the explosion?
      if(d <= rr) {
        BlockPos pos = new BlockPos(x + curX, y + curY, z + curZ);
        IBlockState state = world.getBlockState(pos);

        // no air blocks
        if(!state.getBlock().isAir(state, world, pos)) {
          // explosion "strength" at the current position
          double f = explosionStrength * (1f - d / rr);

          float f2 = exploder != null ? exploder.getExplosionResistance(explosion, world, pos, state) : state.getBlock().getExplosionResistance(world, pos, null, explosion);
          f -= (f2 + 0.3F) * 0.3F;

          if(f > 0.0F && (exploder == null || exploder.canExplosionDestroyBlock(explosion, world, pos, state, (float) f))) {
            // block should be exploded
            count++;
            explosion.addAffectedBlock(pos);
          }
        }
      }
      // get next coordinate;
      step();
    }

    net.minecraftforge.event.ForgeEventFactory.onExplosionDetonate(world, explosion, Collections.emptyList(), r * 2);

    explosion.getAffectedBlockPositions().forEach(this::explodeBlock);

    return count == blocksPerIteration; // can lead to 1 more call where nothing is done, but that's ok
  }

  // get the next coordinate
  private void step() {
    // we go X/Z plane wise from top to bottom
    if(++curX > currentRadius) {
      curX = -currentRadius;
      if(++curZ > currentRadius) {
        curZ = -currentRadius;
        if(--curY < -currentRadius) {
          currentRadius++;
          curX = curZ = -currentRadius;
          curY = currentRadius;
        }
      }
    }
    // we skip the internals
    if(curY != -currentRadius && curY != currentRadius) {
      // we're not in the top or bottom plane
      if(curZ != -currentRadius && curZ != currentRadius) {
        // we're not in the X/Y planes of the cube, we can therefore skip the x to the end if we're inside
        if(curX > -currentRadius) {
          curX = currentRadius;
        }
      }
    }
  }

  private void explodeBlock(BlockPos pos) {
    IBlockState state = world.getBlockState(pos);
    Block block = state.getBlock();
    if(!world.isRemote && block.canDropFromExplosion(explosion)) {
      List<ItemStack> drops = block.getDrops(world, pos, state, 0);
      ForgeEventFactory.fireBlockHarvesting(drops, world, pos, state, 0, 1f, false, null);
      droppedItems.addAll(drops);
    }

    if(world instanceof WorldServer) {
      ((WorldServer) world).spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, true, pos.getX(), pos.getY(), pos.getZ(), 2, 0, 0, 0, 0d);
      ((WorldServer) world).spawnParticle(EnumParticleTypes.SMOKE_LARGE, true, pos.getX(), pos.getY(), pos.getZ(), 1, 0, 0, 0, 0d);
    }

    block.onBlockExploded(world, pos, explosion);
  }
}
