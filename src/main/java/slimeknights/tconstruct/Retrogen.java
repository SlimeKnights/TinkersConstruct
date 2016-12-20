package slimeknights.tconstruct;

public class Retrogen {
/*
  @SubscribeEvent
  public void onChunkLoad(ChunkDataEvent.Load event) {
    if(!Config.retrogen) {
      return;
    }
    // check if we have retrogen data
    int version = event.getData().getInteger("Tinker_Gen");
    if(version < Config.retrogenVersion) {
      // do the retrogen!
      Chunk chunk = event.getChunk();
      // prepare random
      long worldSeed = event.world.getSeed();
      Random fmlRandom = new Random(worldSeed);
      long xSeed = fmlRandom.nextLong() >> 2 + 1L;
      long zSeed = fmlRandom.nextLong() >> 2 + 1L;
      long chunkSeed = (xSeed * chunk.xPosition + zSeed * chunk.zPosition) ^ worldSeed;

      // slime islands
      fmlRandom.setSeed(chunkSeed);
      SlimeIslandGenerator.INSTANCE.generate(fmlRandom, chunk.xPosition, chunk.zPosition, event.world, event.world.getChunkProvider(), event.world.getChunkProvider());

      // nether ores
      fmlRandom.setSeed(chunkSeed);
      NetherOreGenerator.INSTANCE.generate(fmlRandom, chunk.xPosition, chunk.zPosition, event.world, event.world.getChunkProvider(), event.world.getChunkProvider());

      // save that we retrogenned
      event.getData().setInteger("Tinker_Gen", Config.retrogenVersion);
    }
  }*/
}
