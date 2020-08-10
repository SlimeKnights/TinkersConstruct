package slimeknights.tconstruct.test;
/*
import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.profiler.IProfiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.tags.NetworkTagManager;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraft.world.ITickList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.WorldInfo;

import javax.annotation.Nullable;
import java.util.List;

import static org.mockito.Mockito.mock;

public class TestWorld extends World {

  public TestWorld(boolean remote) {
    super(
      new WorldInfo(new WorldSettings(123, GameType.SURVIVAL, false, false, WorldType.DEFAULT), "Test"),
      DimensionType.OVERWORLD,
      (world, dimension1) -> mock(AbstractChunkProvider.class),
      mock(IProfiler.class),
      remote);
  }

  @Override
  public void notifyBlockUpdate(BlockPos pos, BlockState oldState, BlockState newState, int flags) {

  }

  @Override
  public void playSound(@Nullable PlayerEntity player, double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch) {

  }

  @Override
  public void playMovingSound(@Nullable PlayerEntity playerIn, Entity entityIn, SoundEvent eventIn, SoundCategory categoryIn, float volume, float pitch) {

  }

  @Nullable
  @Override
  public Entity getEntityByID(int id) {
    return null;
  }

  @Nullable
  @Override
  public MapData getMapData(String mapName) {
    return null;
  }

  @Override
  public void registerMapData(MapData mapDataIn) {

  }

  @Override
  public int getNextMapId() {
    return 0;
  }

  @Override
  public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {

  }

  @Override
  public Scoreboard getScoreboard() {
    return mock(Scoreboard.class);
  }

  @Override
  public RecipeManager getRecipeManager() {
    return mock(RecipeManager.class);
  }

  @Override
  public NetworkTagManager getTags() {
    return mock(NetworkTagManager.class);
  }

  @Override
  @SuppressWarnings("unchecked")
  public ITickList<Block> getPendingBlockTicks() {
    return mock(ITickList.class);
  }

  @Override
  @SuppressWarnings("unchecked")
  public ITickList<Fluid> getPendingFluidTicks() {
    return mock(ITickList.class);
  }

  @Override
  public void playEvent(@Nullable PlayerEntity player, int type, BlockPos pos, int data) {

  }

  @Override
  public List<? extends PlayerEntity> getPlayers() {
    return ImmutableList.of();
  }

  @Override
  public Biome getNoiseBiomeRaw(int x, int y, int z) {
    return mock(Biome.class);
  }
}
*/
