package slimeknights.tconstruct.gadgets;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import slimeknights.tconstruct.gadgets.entity.EFLNExplosion;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.tools.network.EntityMovementChangePacket;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class Exploder {

  public final double r;
  private final double rr;
  public final int dist;
  private final double explosionStrength;
  private final int blocksPerIteration;
  public final int x, y, z;
  public final World world;
  private final Entity exploder;
  private final EFLNExplosion explosion;

  private int currentRadius;
  private int curX, curY, curZ;

  private List<ItemStack> droppedItems; // map containing all items dropped by the explosion and their amounts

  public Exploder(World world, EFLNExplosion explosion, Entity exploder, BlockPos location, double r, double explosionStrength, int blocksPerIteration) {
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

  public static void startExplosion(World world, EFLNExplosion explosion, Entity entity, BlockPos location, double r, double explosionStrength) {
    Exploder exploder = new Exploder(world, explosion, entity, location, r, explosionStrength, Math.max(50, (int) (r * r * r / 10d)));
    exploder.handleEntities();
    world.playSound(null, location, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);
    MinecraftForge.EVENT_BUS.register(exploder);
  }

  private void handleEntities() {
    final Predicate<Entity> predicate = entity -> entity != null
      && !entity.isImmuneToExplosions()
      && EntityPredicates.NOT_SPECTATING.test(entity)
      && EntityPredicates.IS_ALIVE.test(entity)
      && entity.getPositionVec().squareDistanceTo(this.x, this.y, this.z) <= this.r * this.r;

    // damage and blast back entities
    List<Entity> list = this.world.getEntitiesInAABBexcluding(this.exploder,
      new AxisAlignedBB(this.x - this.r - 1,
        this.y - this.r - 1,
        this.z - this.r - 1,
        this.x + this.r + 1,
        this.y + this.r + 1,
        this.z + this.r + 1),
      predicate
    );
    net.minecraftforge.event.ForgeEventFactory.onExplosionDetonate(this.world, this.explosion, list, this.r * 2);

    for (Entity entity : list) {
      // move it away from the center depending on distance and explosion strength
      Vector3d dir = entity.getPositionVec().subtract(this.exploder.getPositionVec().add(0, -this.r / 2, 0));
      double str = (this.r - dir.length()) / this.r;
      str = Math.max(0.3, str);
      dir = dir.normalize();
      dir = dir.scale(this.explosionStrength * str * 0.3);
      entity.addVelocity(dir.x, dir.y + 0.5, dir.z);
      entity.attackEntityFrom(DamageSource.causeExplosionDamage(this.explosion), (float) (str * this.explosionStrength));

      if (entity instanceof ServerPlayerEntity) {
        TinkerNetwork.getInstance().sendTo(new EntityMovementChangePacket(entity), (ServerPlayerEntity) entity);
      }
    }
  }

  @SubscribeEvent
  public void onTick(TickEvent.WorldTickEvent event) {
    if (event.world == this.world && event.phase == TickEvent.Phase.END) {
      if (!this.iteration()) {
        // goodbye world, we're done exploding
        this.finish();
      }
    }
  }

  private void finish() {
    final int d = (int) this.r / 2;
    final BlockPos pos = new BlockPos(this.x - d, this.y - d, this.z - d);
    final Random random = new Random();

    List<ItemStack> aggregatedDrops = Lists.newArrayList();

    for (ItemStack drop : this.droppedItems) {
      boolean notInList = true;

      // check if it's already in our list
      for (ItemStack stack : aggregatedDrops) {
        if (ItemStack.areItemsEqual(drop, stack) && ItemStack.areItemStackTagsEqual(drop, stack)) {
          stack.grow(drop.getCount());
          notInList = false;
          break;
        }
      }

      if (notInList) {
        aggregatedDrops.add(drop);
      }
    }

    // actually drop the aggregated items
    for (ItemStack drop : aggregatedDrops) {
      int stacksize = drop.getCount();
      do {
        BlockPos spawnPos = pos.add(random.nextInt((int) this.r), random.nextInt((int) this.r), random.nextInt((int) this.r));
        ItemStack dropItemstack = drop.copy();
        dropItemstack.setCount(Math.min(stacksize, 64));
        Block.spawnAsEntity(this.world, spawnPos, dropItemstack);
        stacksize -= dropItemstack.getCount();
      }
      while (stacksize > 0);
    }

    MinecraftForge.EVENT_BUS.unregister(this);
  }

  /**
   * Explodes away all blocks for the current iteration
   */
  private boolean iteration() {
    int count = 0;

    this.explosion.clearAffectedBlockPositions();

    while (count < this.blocksPerIteration && this.currentRadius < (int) this.r + 1) {
      double d = this.curX * this.curX + this.curY * this.curY + this.curZ * this.curZ;
      // inside the explosion?
      if (d <= this.rr) {
        BlockPos blockpos = new BlockPos(this.x + this.curX, this.y + this.curY, this.z + this.curZ);
        BlockState blockState = this.world.getBlockState(blockpos);
        FluidState ifluidstate = this.world.getFluidState(blockpos);

        // no air blocks
        if (!blockState.isAir(this.world, blockpos) || !ifluidstate.isEmpty()) {
          // explosion "strength" at the current position
          double f = this.explosionStrength * (1f - d / this.rr);

          float f2 = Math.max(blockState.getExplosionResistance(this.world, blockpos, this.explosion), ifluidstate.getExplosionResistance(this.world, blockpos, this.explosion));
          if (this.exploder != null) {
            f2 = this.exploder.getExplosionResistance(this.explosion, this.world, blockpos, blockState, ifluidstate, f2);
          }

          f -= (f2 + 0.3F) * 0.3F;

          if (f > 0.0F && (this.exploder == null || this.exploder.canExplosionDestroyBlock(this.explosion, this.world, blockpos, blockState, (float) f))) {
            // block should be exploded
            count++;
            this.explosion.addAffectedBlock(blockpos);
          }
        }
      }
      // get next coordinate;
      this.step();
    }

    net.minecraftforge.event.ForgeEventFactory.onExplosionDetonate(this.world, this.explosion, Collections.emptyList(), this.r * 2);

    this.explosion.getAffectedBlockPositions().forEach(this::explodeBlock);

    return count == this.blocksPerIteration; // can lead to 1 more call where nothing is done, but that's ok
  }

  // get the next coordinate
  private void step() {
    // we go X/Z plane wise from top to bottom
    if (++this.curX > this.currentRadius) {
      this.curX = -this.currentRadius;
      if (++this.curZ > this.currentRadius) {
        this.curZ = -this.currentRadius;
        if (--this.curY < -this.currentRadius) {
          this.currentRadius++;
          this.curX = this.curZ = -this.currentRadius;
          this.curY = this.currentRadius;
        }
      }
    }
    // we skip the internals
    if (this.curY != -this.currentRadius && this.curY != this.currentRadius) {
      // we're not in the top or bottom plane
      if (this.curZ != -this.currentRadius && this.curZ != this.currentRadius) {
        // we're not in the X/Y planes of the cube, we can therefore skip the x to the end if we're inside
        if (this.curX > -this.currentRadius) {
          this.curX = this.currentRadius;
        }
      }
    }
  }

  private void explodeBlock(BlockPos blockpos) {
    BlockState blockstate = this.world.getBlockState(blockpos);

    if (!this.world.isRemote && blockstate.canDropFromExplosion(this.world, blockpos, this.explosion)) {
      TileEntity tileentity = blockstate.hasTileEntity() ? this.world.getTileEntity(blockpos) : null;
      LootContext.Builder builder = (new LootContext.Builder((ServerWorld) this.world)).withRandom(this.world.rand).withParameter(LootParameters.field_237457_g_, Vector3d.copyCentered(blockpos)).withParameter(LootParameters.TOOL, ItemStack.EMPTY).withNullableParameter(LootParameters.BLOCK_ENTITY, tileentity);

      this.droppedItems.addAll(blockstate.getDrops(builder));
    }

    if (this.world instanceof ServerWorld) {
      for (ServerPlayerEntity serverplayerentity : ((ServerWorld) this.world).getPlayers()) {
        ((ServerWorld) this.world).spawnParticle(serverplayerentity, ParticleTypes.POOF, true, blockpos.getX(), blockpos.getY(), blockpos.getZ(), 2, 0, 0, 0, 0d);
        ((ServerWorld) this.world).spawnParticle(serverplayerentity, ParticleTypes.SMOKE, true, blockpos.getX(), blockpos.getY(), blockpos.getZ(), 1, 0, 0, 0, 0d);
      }
    }

    blockstate.onBlockExploded(this.world, blockpos, this.explosion);
  }

}
