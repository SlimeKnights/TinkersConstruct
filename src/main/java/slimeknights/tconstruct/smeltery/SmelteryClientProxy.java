package slimeknights.tconstruct.smeltery;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelDynBucket;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.Locale;

import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.library.client.CustomTextureCreator;
import slimeknights.tconstruct.library.tools.Pattern;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.smeltery.client.SmelteryRenderer;
import slimeknights.tconstruct.smeltery.client.TankRenderer;
import slimeknights.tconstruct.smeltery.tileentity.TileSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.TileTank;

public class SmelteryClientProxy extends ClientProxy {

  @Override
  public void preInit() {
    super.preInit();

    MinecraftForge.EVENT_BUS.register(new SmelteryClientEvents());
  }

  @Override
  protected void registerModels() {
    // Blocks
    registerItemModel(Item.getItemFromBlock(TinkerSmeltery.smelteryController));
    registerItemModel(Item.getItemFromBlock(TinkerSmeltery.faucet));

    // TEs
    ClientRegistry.bindTileEntitySpecialRenderer(TileTank.class, new TankRenderer());
    ClientRegistry.bindTileEntitySpecialRenderer(TileSmeltery.class, new SmelteryRenderer());


    // Items
    final ResourceLocation castLoc = SmelteryClientEvents.locBlankCast;
    CustomTextureCreator.castModelLocation = new ResourceLocation(castLoc.getResourceDomain(), "item/" + castLoc.getResourcePath());
    ModelLoader.setCustomMeshDefinition(TinkerSmeltery.cast, new ItemMeshDefinition() {
      @Override
      public ModelResourceLocation getModelLocation(ItemStack stack) {
        NBTTagCompound tag = TagUtil.getTagSafe(stack);
        String suffix = tag.getString(Pattern.TAG_PARTTYPE).toLowerCase(Locale.US);

        if(!suffix.isEmpty())
          suffix = "_" + suffix;

        return new ModelResourceLocation(new ResourceLocation(castLoc.getResourceDomain(),
                                                              castLoc.getResourcePath() + suffix),
                                         "inventory");
      }
    });

    // universal bucket
    ModelLoader.setBucketModelDefinition(TinkerSmeltery.bucket);
  }
}
