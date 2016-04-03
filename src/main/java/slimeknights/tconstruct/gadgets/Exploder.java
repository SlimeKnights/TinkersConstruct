package slimeknights.tconstruct.gadgets;

import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.procedure.TObjectIntProcedure;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketParticles;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Random;

public class Exploder {

  public final double r;
  public final double rr;
  public final int dist;
  public final double explosionStrength;
  public final int blocksPerIteration;
  public final int x,y,z;
  public final World world;
  public final Entity exploder;
  public final Explosion explosion;

  protected int currentRadius;
  private int curX, curY, curZ;

  protected TObjectIntHashMap<Pair<Item, Integer>> droppedItems; // map containing all items dropped by the explosion and their amounts

  public Exploder(World world, Explosion explosion, Entity exploder, BlockPos location, double r, double explosionStrength, int blocksPerIteration) {
    this.r = r;
    this.world = world;
    this.explosion = explosion;
    this.exploder = exploder;
    this.rr = r*r;
    this.dist = (int)r + 1;
    this.explosionStrength = explosionStrength;
    this.blocksPerIteration = blocksPerIteration;
    this.currentRadius = 0;

    this.x = location.getX();
    this.y = location.getY();
    this.z = location.getZ();

    this.curX = 0;
    this.curY = 0;
    this.curZ = 0;

    this.droppedItems = new TObjectIntHashMap<Pair<Item, Integer>>();
  }

  public static void startExplosion(World world, Explosion explosion, Entity entity, BlockPos location, double r, double explosionStrength) {
    Exploder exploder = new Exploder(world, explosion, entity, location, r, explosionStrength, Math.max(50, (int)(r*r*r/10d)));
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

  private void finish() {
    final int d = (int)r/2;
    final BlockPos pos = new BlockPos(x-d,y-d,z-d);
    final Random random = new Random();
    // drop items
    droppedItems.forEachEntry(new TObjectIntProcedure<Pair<Item, Integer>>() {
      @Override
      public boolean execute(Pair<Item, Integer> a, int b) {
        BlockPos spawnPos = pos.add(random.nextInt((int)r), random.nextInt((int)r), random.nextInt((int)r));
        do {
          int c = Math.min(b, 64);
          Block.spawnAsEntity(world, spawnPos, new ItemStack(a.getKey(), c, a.getValue()));
          b -= c;
        } while(b > 0);
        return true;
      }
    });
    MinecraftForge.EVENT_BUS.unregister(this);
  }

  /**
   * Explodes away all blocks for the current iteration
   */
  private boolean iteration() {
    int count = 0;

    while(count < blocksPerIteration && currentRadius < (int)r + 1) {
      double d = curX*curX + curY*curY + curZ * curZ;
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

          if(f > 0.0F && (exploder == null || exploder.verifyExplosion(explosion, world, pos, state, (float) f))) {
            // block should be exploded
            count++;
            explodeBlock(state, pos);
          }
        }
      }
      // get next coordinate;
      step();
    }

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

  private void explodeBlock(IBlockState state, BlockPos pos) {
    Block block = state.getBlock();
    if(!world.isRemote && block.canDropFromExplosion(explosion)) {
      List<ItemStack> drops = block.getDrops(world, pos, state, 0);
      ForgeEventFactory.fireBlockHarvesting(drops, world, pos, state, 0, 1f, false, null);
      for(ItemStack stack : drops) {
        Pair<Item, Integer> pair = Pair.of(stack.getItem(), stack.getMetadata());
        // add the items to the drops
        droppedItems.put(pair, stack.stackSize + droppedItems.get(pair));
      }
      //block.dropBlockAsItemWithChance(world, pos, state, 1.0F, 0);
    }

    if(world instanceof WorldServer) {
      ((WorldServer) world).spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, true, pos.getX(), pos.getY(), pos.getZ(), 2, 0,0,0, 0d);
      ((WorldServer) world).spawnParticle(EnumParticleTypes.SMOKE_LARGE, true, pos.getX(), pos.getY(), pos.getZ(), 1, 0,0,0, 0d);
    }

    block.onBlockExploded(world, pos, explosion);
  }
}
