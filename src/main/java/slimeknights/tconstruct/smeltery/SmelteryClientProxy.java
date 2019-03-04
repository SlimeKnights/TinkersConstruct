package slimeknights.tconstruct.smeltery;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.client.CustomTextureCreator;
import slimeknights.tconstruct.smeltery.block.BlockSearedGlass;
import slimeknights.tconstruct.smeltery.block.BlockSmelteryIO;
import slimeknights.tconstruct.smeltery.block.BlockTank;
import slimeknights.tconstruct.smeltery.client.CastingRenderer;
import slimeknights.tconstruct.smeltery.client.ChannelRenderer;
import slimeknights.tconstruct.smeltery.client.FaucetRenderer;
import slimeknights.tconstruct.smeltery.client.SmelteryRenderer;
import slimeknights.tconstruct.smeltery.client.TankRenderer;
import slimeknights.tconstruct.smeltery.client.TinkerTankRenderer;
import slimeknights.tconstruct.smeltery.tileentity.TileCastingBasin;
import slimeknights.tconstruct.smeltery.tileentity.TileCastingTable;
import slimeknights.tconstruct.smeltery.tileentity.TileChannel;
import slimeknights.tconstruct.smeltery.tileentity.TileFaucet;
import slimeknights.tconstruct.smeltery.tileentity.TileSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.TileTank;
import slimeknights.tconstruct.smeltery.tileentity.TileTinkerTank;

import static slimeknights.tconstruct.common.ModelRegisterUtil.registerItemBlockMeta;
import static slimeknights.tconstruct.common.ModelRegisterUtil.registerItemModel;

import javax.annotation.Nonnull;

public class SmelteryClientProxy extends ClientProxy {

  @Override
  public void preInit() {
    super.preInit();

    MinecraftForge.EVENT_BUS.register(new SmelteryClientEvents());
  }

  @Override
  public void registerModels() {
    // ignore color state for the clear stained glass, it is handled by tinting
    ModelLoader.setCustomStateMapper(TinkerSmeltery.searedGlass, (new StateMap.Builder()).ignore(BlockSearedGlass.TYPE).build());

    // Blocks
    registerItemModel(TinkerSmeltery.smelteryController);
    registerItemModel(TinkerSmeltery.faucet);
    registerItemModel(TinkerSmeltery.channel);
    registerItemModel(TinkerSmeltery.searedGlass);
    registerItemModel(TinkerSmeltery.searedFurnaceController);
    registerItemModel(TinkerSmeltery.tinkerTankController);
    registerItemBlockMeta(TinkerSmeltery.searedBlock);
    registerItemBlockMeta(TinkerSmeltery.castingBlock);

    // slabs
    registerItemBlockMeta(TinkerSmeltery.searedSlab);
    registerItemBlockMeta(TinkerSmeltery.searedSlab2);

    // stairs
    registerItemModel(TinkerSmeltery.searedStairsStone);
    registerItemModel(TinkerSmeltery.searedStairsCobble);
    registerItemModel(TinkerSmeltery.searedStairsPaver);
    registerItemModel(TinkerSmeltery.searedStairsBrick);
    registerItemModel(TinkerSmeltery.searedStairsBrickCracked);
    registerItemModel(TinkerSmeltery.searedStairsBrickFancy);
    registerItemModel(TinkerSmeltery.searedStairsBrickSquare);
    registerItemModel(TinkerSmeltery.searedStairsBrickTriangle);
    registerItemModel(TinkerSmeltery.searedStairsBrickSmall);
    registerItemModel(TinkerSmeltery.searedStairsRoad);
    registerItemModel(TinkerSmeltery.searedStairsTile);
    registerItemModel(TinkerSmeltery.searedStairsCreeper);

    // drains
    Item drain = Item.getItemFromBlock(TinkerSmeltery.smelteryIO);
    for(BlockSmelteryIO.IOType type : BlockSmelteryIO.IOType.values()) {
      String variant = String.format("%s=%s,%s=%s",
                                     BlockSmelteryIO.FACING.getName(),
                                     BlockSmelteryIO.FACING.getName(EnumFacing.SOUTH),
                                     BlockSmelteryIO.TYPE.getName(),
                                     BlockSmelteryIO.TYPE.getName(type)
      );
      ModelLoader.setCustomModelResourceLocation(drain, type.meta, new ModelResourceLocation(drain.getRegistryName(), variant));
    }

    // seared tank items
    Item tank = Item.getItemFromBlock(TinkerSmeltery.searedTank);
    for(BlockTank.TankType type : BlockTank.TankType.values()) {
      ModelLoader.setCustomModelResourceLocation(tank, type.meta, new ModelResourceLocation(tank.getRegistryName(), type.getName()));
    }

    // TEs
    ClientRegistry.bindTileEntitySpecialRenderer(TileTank.class, new TankRenderer());
    ClientRegistry.bindTileEntitySpecialRenderer(TileSmeltery.class, new SmelteryRenderer());
    ClientRegistry.bindTileEntitySpecialRenderer(TileTinkerTank.class, new TinkerTankRenderer());
    ClientRegistry.bindTileEntitySpecialRenderer(TileFaucet.class, new FaucetRenderer());
    ClientRegistry.bindTileEntitySpecialRenderer(TileChannel.class, new ChannelRenderer());
    ClientRegistry.bindTileEntitySpecialRenderer(TileCastingTable.class, new CastingRenderer.Table());
    ClientRegistry.bindTileEntitySpecialRenderer(TileCastingBasin.class, new CastingRenderer.Basin());

    // Items
    final ResourceLocation castLoc = SmelteryClientEvents.locBlankCast;
    CustomTextureCreator.castModelLocation = new ResourceLocation(castLoc.getResourceDomain(), "item/" + castLoc.getResourcePath());
    ModelLoader.setCustomMeshDefinition(TinkerSmeltery.cast, new PatternMeshDefinition(castLoc));

    if(Config.claycasts) {
      final ResourceLocation clayCastLoc = SmelteryClientEvents.locClayCast;
      CustomTextureCreator.castModelLocation = new ResourceLocation(clayCastLoc.getResourceDomain(),
                                                                    "item/" + clayCastLoc.getResourcePath());
      ModelLoader.setCustomMeshDefinition(TinkerSmeltery.clayCast, new PatternMeshDefinition(clayCastLoc));
    }

    TinkerSmeltery.castCustom.registerItemModels();
  }

  @Override
  public void init() {
    Minecraft minecraft = Minecraft.getMinecraft();

    // slime channels
    ItemColors colors = minecraft.getItemColors();
    colors.registerItemColorHandler(
        (@Nonnull ItemStack stack, int tintIndex) -> {
          if(!stack.hasTagCompound()) {
            return 0xFFFFFF;
          }
          FluidStack fluid = FluidStack.loadFluidStackFromNBT(stack.getTagCompound());
          if (fluid != null && fluid.amount > 0 && fluid.getFluid() != null) {
            return fluid.getFluid().getColor(fluid);
          }
          return 0xFFFFFF;
        },
        TinkerSmeltery.searedTank);
    super.init();
  }
}
