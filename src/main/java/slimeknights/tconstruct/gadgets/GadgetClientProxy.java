package slimeknights.tconstruct.gadgets;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import java.util.Locale;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.gadgets.block.BlockSlimeChannel;
import slimeknights.tconstruct.gadgets.client.RenderFancyItemFrame;
import slimeknights.tconstruct.gadgets.client.RenderThrowball;
import slimeknights.tconstruct.gadgets.entity.EntityFancyItemFrame;
import slimeknights.tconstruct.gadgets.entity.EntityThrowball;
import slimeknights.tconstruct.gadgets.item.ItemThrowball;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.model.PropertyStateMapper;
import slimeknights.tconstruct.shared.block.BlockSlime;

public class GadgetClientProxy extends ClientProxy {

  
  @Override
  public void init() {
    Minecraft minecraft = Minecraft.getMinecraft();

    // slime channels
    minecraft.getBlockColors().registerBlockColorHandler(
        new IBlockColor() {
          @Override
          public int colorMultiplier(@Nonnull IBlockState state, IBlockAccess access, BlockPos pos, int tintIndex) {
            return state.getValue(BlockSlimeChannel.TYPE).getColor();
          }
        },
        TinkerGadgets.slimeChannel);

    minecraft.getItemColors().registerItemColorHandler(
        new IItemColor() {
          @Override
          public int getColorFromItemstack(@Nonnull ItemStack stack, int tintIndex) {
            return BlockSlime.SlimeType.fromMeta(stack.getItemDamage()).getColor();
          }
        },
        TinkerGadgets.slimeChannel);

    super.init();
  }
  
  @Override
  protected void registerModels() {
    super.registerModels();

    // separate the sides into separate model files to make the blockstate rotations easier
    ModelLoader.setCustomStateMapper(TinkerGadgets.slimeChannel, new PropertyStateMapper("slime_channel", BlockSlimeChannel.SIDE, BlockSlimeChannel.TYPE));

    // Blocks
    registerItemModel(TinkerGadgets.stoneTorch);
    registerItemModel(TinkerGadgets.stoneLadder);
    registerItemModel(TinkerGadgets.punji);
    registerItemModel(TinkerGadgets.rack);

    registerItemModel(TinkerGadgets.woodRail);
    registerItemModel(TinkerGadgets.woodRailTrapdoor);

    registerItemModel(TinkerGadgets.slimeChannel); //tinted for variants
    
    registerItemBlockMeta(TinkerGadgets.driedClay);
    registerItemBlockMeta(TinkerGadgets.brownstone);

    // slabs
    registerItemBlockMeta(TinkerGadgets.driedClaySlab);
    registerItemBlockMeta(TinkerGadgets.brownstoneSlab);
    registerItemBlockMeta(TinkerGadgets.brownstoneSlab2);

    // stairs
    registerItemModel(TinkerGadgets.driedClayStairs);
    registerItemModel(TinkerGadgets.driedBrickStairs);
    registerItemModel(TinkerGadgets.brownstoneStairsSmooth);
    registerItemModel(TinkerGadgets.brownstoneStairsRough);
    registerItemModel(TinkerGadgets.brownstoneStairsPaver);
    registerItemModel(TinkerGadgets.brownstoneStairsBrick);
    registerItemModel(TinkerGadgets.brownstoneStairsBrickCracked);
    registerItemModel(TinkerGadgets.brownstoneStairsBrickFancy);
    registerItemModel(TinkerGadgets.brownstoneStairsBrickSquare);
    registerItemModel(TinkerGadgets.brownstoneStairsBrickTriangle);
    registerItemModel(TinkerGadgets.brownstoneStairsBrickSmall);
    registerItemModel(TinkerGadgets.brownstoneStairsRoad);
    registerItemModel(TinkerGadgets.brownstoneStairsTile);
    registerItemModel(TinkerGadgets.brownstoneStairsCreeper);

    // Items
    registerItemModel(TinkerGadgets.slimeSling);
    registerItemModel(TinkerGadgets.slimeBoots);
    registerItemModel(TinkerGadgets.piggybackPack);
    registerItemModel(TinkerGadgets.stoneStick);

    for(ItemThrowball.ThrowballType type : ItemThrowball.ThrowballType.values()) {
      registerItemModel(TinkerGadgets.throwball, type.ordinal(), type.name().toLowerCase(Locale.US));
    }

    // Entity
    RenderingRegistry.registerEntityRenderingHandler(EntityFancyItemFrame.class, RenderFancyItemFrame.FACTORY);

    for(EntityFancyItemFrame.FrameType type : EntityFancyItemFrame.FrameType.values()) {
      ModelResourceLocation loc = Util.getModelResource("fancy_frame", type.toString());
      ModelLoader.registerItemVariants(TinkerGadgets.fancyFrame, loc);
      ModelLoader.setCustomModelResourceLocation(TinkerGadgets.fancyFrame, type.ordinal(), loc);
    }
    RenderingRegistry.registerEntityRenderingHandler(EntityThrowball.class, RenderThrowball.FACTORY);
  }

  @Override
  public void postInit() {
    super.postInit();

    MinecraftForge.EVENT_BUS.register(new GadgetClientEvents());
  }
}
