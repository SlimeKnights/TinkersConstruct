package slimeknights.tconstruct.world;

import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.init.Blocks;
import net.minecraftforge.client.model.ModelLoader;

import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.world.block.BlockSlimeGrass;
import slimeknights.tconstruct.world.block.BlockSlimeLeaves;
import slimeknights.tconstruct.world.client.SlimeColorizer;

public class WorldClientProxy extends ClientProxy {
  public static SlimeColorizer slimeColorizer = new SlimeColorizer();

  @Override
  public void preInit() {
    ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(slimeColorizer);

    super.preInit();
  }

  @Override
  protected void registerModels() {
    ModelLoader.setCustomStateMapper(TinkerWorld.slimeGrass, (new StateMap.Builder()).ignore(BlockSlimeGrass.FOLIAGE).build());
    ModelLoader.setCustomStateMapper(TinkerWorld.slimeLeaves, (new StateMap.Builder())
        .ignore(BlockSlimeGrass.FOLIAGE, BlockLeaves.CHECK_DECAY, BlockLeaves.DECAYABLE).build());
    ModelLoader.setCustomStateMapper(TinkerWorld.slimeGrassTall, (new StateMap.Builder()).ignore(BlockSlimeGrass.FOLIAGE).build());
  }
}
