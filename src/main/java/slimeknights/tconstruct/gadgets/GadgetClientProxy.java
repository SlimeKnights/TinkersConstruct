package slimeknights.tconstruct.gadgets;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.ItemColors;
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
import slimeknights.tconstruct.library.client.model.ToolModelLoader;
import slimeknights.tconstruct.shared.block.BlockSlime;

import static slimeknights.tconstruct.common.ModelRegisterUtil.registerItemBlockMeta;
import static slimeknights.tconstruct.common.ModelRegisterUtil.registerItemModel;
import static slimeknights.tconstruct.common.ModelRegisterUtil.registerToolModel;

public class GadgetClientProxy extends ClientProxy {

  @Override
  public void preInit() {
    super.preInit();

    MinecraftForge.EVENT_BUS.register(new GadgetClientEvents());
  }

  @Override
  public void init() {
    Minecraft minecraft = Minecraft.getMinecraft();

    // slime channels
    minecraft.getBlockColors().registerBlockColorHandler(
        (@Nonnull IBlockState state, IBlockAccess access, BlockPos pos, int tintIndex) -> state.getValue(BlockSlimeChannel.TYPE).getColor(),
        TinkerGadgets.slimeChannel);

    ItemColors colors = minecraft.getItemColors();
    colors.registerItemColorHandler(
        (@Nonnull ItemStack stack, int tintIndex) -> BlockSlime.SlimeType.fromMeta(stack.getItemDamage()).getColor(),
        TinkerGadgets.slimeChannel);
    colors.registerItemColorHandler(
        (@Nonnull ItemStack stack, int tintIndex) -> TinkerGadgets.slimeBoots.getColor(stack),
        TinkerGadgets.slimeBoots, TinkerGadgets.slimeSling);


    super.init();
  }

  @Override
  public void registerModels() {
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

    registerItemModel(TinkerGadgets.woodenHopper);

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
      for(boolean withMap : new boolean[]{true, false}) {
        String variant = RenderFancyItemFrame.getVariant(type, withMap);
        ModelResourceLocation loc = Util.getModelResource("fancy_frame", variant);
        ModelLoader.registerItemVariants(TinkerGadgets.fancyFrame, loc);
        if(!withMap) {
          ModelLoader.setCustomModelResourceLocation(TinkerGadgets.fancyFrame, type.ordinal(), loc);
        }
      }
    }
    RenderingRegistry.registerEntityRenderingHandler(EntityThrowball.class, RenderThrowball.FACTORY);


    // Mom's Spaghetti
    TinkerGadgets.spaghetti.registerItemModels();
    registerToolModel(TinkerGadgets.momsSpaghetti, Util.getResource("moms_spaghetti" + ToolModelLoader.EXTENSION));
  }

  @Override
  public void postInit() {
    super.postInit();
  }
}
