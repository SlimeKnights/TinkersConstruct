package slimeknights.tconstruct.shared;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoader;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.shared.block.BlockClearStainedGlass;
import slimeknights.tconstruct.shared.block.BlockClearStainedGlass.EnumGlassColor;

import static slimeknights.tconstruct.common.ModelRegisterUtil.registerItemBlockMeta;
import static slimeknights.tconstruct.common.ModelRegisterUtil.registerItemModel;
import static slimeknights.tconstruct.shared.TinkerCommons.blockClearGlass;
import static slimeknights.tconstruct.shared.TinkerCommons.blockClearStainedGlass;
import static slimeknights.tconstruct.shared.TinkerCommons.blockDecoGround;
import static slimeknights.tconstruct.shared.TinkerCommons.blockFirewood;
import static slimeknights.tconstruct.shared.TinkerCommons.blockMetal;
import static slimeknights.tconstruct.shared.TinkerCommons.blockOre;
import static slimeknights.tconstruct.shared.TinkerCommons.blockSlime;
import static slimeknights.tconstruct.shared.TinkerCommons.blockSlimeCongealed;
import static slimeknights.tconstruct.shared.TinkerCommons.blockSoil;
import static slimeknights.tconstruct.shared.TinkerCommons.book;
import static slimeknights.tconstruct.shared.TinkerCommons.edibles;
import static slimeknights.tconstruct.shared.TinkerCommons.ingots;
import static slimeknights.tconstruct.shared.TinkerCommons.materials;
import static slimeknights.tconstruct.shared.TinkerCommons.nuggets;
import static slimeknights.tconstruct.shared.TinkerCommons.slabDecoGround;
import static slimeknights.tconstruct.shared.TinkerCommons.slabFirewood;
import static slimeknights.tconstruct.shared.TinkerCommons.stairsFirewood;
import static slimeknights.tconstruct.shared.TinkerCommons.stairsLavawood;
import static slimeknights.tconstruct.shared.TinkerCommons.stairsMudBrick;

public class CommonsClientProxy extends ClientProxy {

  public static Minecraft minecraft = Minecraft.getMinecraft();

  @Override
  public void init() {
    final BlockColors blockColors = minecraft.getBlockColors();

    // stained glass
    blockColors.registerBlockColorHandler(
        new IBlockColor() {
          @Override
          public int colorMultiplier(@Nonnull IBlockState state, IBlockAccess access, BlockPos pos, int tintIndex) {
            EnumGlassColor type = state.getValue(BlockClearStainedGlass.COLOR);
            return type.getColor();
          }
        },
        blockClearStainedGlass);

    minecraft.getItemColors().registerItemColorHandler(
        new IItemColor() {
          @SuppressWarnings("deprecation")
          @Override
          public int getColorFromItemstack(@Nonnull ItemStack stack, int tintIndex) {
            IBlockState iblockstate = ((ItemBlock) stack.getItem()).getBlock().getStateFromMeta(stack.getMetadata());
            return blockColors.colorMultiplier(iblockstate, null, null, tintIndex);
          }
        },
        blockClearStainedGlass);

    super.init();
  }

  @Override
  public void registerModels() {
    // ignore color state for the clear stained glass, it is handled by tinting
    ModelLoader.setCustomStateMapper(blockClearStainedGlass, (new StateMap.Builder()).ignore(BlockClearStainedGlass.COLOR).build());

    nuggets.registerItemModels();
    ingots.registerItemModels();
    materials.registerItemModels();
    edibles.registerItemModels();

    registerItemModel(book, 0, "inventory");

    registerItemBlockMeta(blockMetal);
    registerItemBlockMeta(blockSoil);
    registerItemBlockMeta(blockOre);
    registerItemBlockMeta(blockFirewood);
    registerItemBlockMeta(blockSlime);
    registerItemBlockMeta(blockSlimeCongealed);
    registerItemBlockMeta(slabFirewood);
    registerItemModel(stairsFirewood);
    registerItemModel(stairsLavawood);
    registerItemModel(blockClearGlass);
    registerItemModel(blockClearStainedGlass); // this is enumBlock, but the model is tinted instead of using a state

    registerItemBlockMeta(blockDecoGround);
    registerItemBlockMeta(slabDecoGround);
    registerItemModel(stairsMudBrick);
  }
}
