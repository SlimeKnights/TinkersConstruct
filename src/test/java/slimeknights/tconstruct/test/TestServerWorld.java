package slimeknights.tconstruct.test;
/*
import lombok.extern.log4j.Log4j2;
import net.minecraft.profiler.IProfiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedPlayerList;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.ServerProperties;
import net.minecraft.util.Util;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.listener.IChunkStatusListener;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;

import java.io.File;
import java.util.Properties;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Log4j2
public class TestServerWorld extends ServerWorld {

  public static TestServerWorld getTestServerWorld() {
    MinecraftServer server = mock(MinecraftServer.class);
    DedicatedServer dedicatedServer = mock(DedicatedServer.class);
    when(dedicatedServer.getServerProperties()).thenReturn(new ServerProperties(new Properties()));
    DedicatedPlayerList dedicatedPlayerList = new DedicatedPlayerList(dedicatedServer);
    when(dedicatedServer.getPlayerList()).thenReturn(dedicatedPlayerList);
    when(server.getPlayerList()).thenReturn(dedicatedPlayerList);

    SaveHandler saveHandler = mock(SaveHandler.class);
    when(saveHandler.getWorldDirectory()).thenReturn(new File("out/testworld"));
    TestServerWorld testServerWorld = new TestServerWorld(dedicatedServer, saveHandler);
    when(server.getWorld(any())).thenReturn(testServerWorld);
    when(dedicatedServer.getWorld(any())).thenReturn(testServerWorld);

    return testServerWorld;
  }

  public TestServerWorld(MinecraftServer server, SaveHandler saveHandlerIn) {
    super(
      server,
      Util.getServerExecutor(),
      saveHandlerIn,
      new WorldInfo(new WorldSettings(123, GameType.SURVIVAL, false, false, WorldType.DEFAULT), "Test"),
      DimensionType.OVERWORLD,
      mock(IProfiler.class),
      mock(IChunkStatusListener.class));
  }
*/
