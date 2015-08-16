package tconstruct.smeltery;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;

import tconstruct.ClientProxy;
import tconstruct.library.client.CustomTextureCreator;
import tconstruct.library.utils.TagUtil;
import tconstruct.library.tools.Pattern;

public class SmelteryClientProxy extends ClientProxy {

  @Override
  public void preInit() {
    super.preInit();

    MinecraftForge.EVENT_BUS.register(new SmelteryClientEvents());
  }

  @Override
  protected void registerModels() {
    final ResourceLocation castLoc = getItemLocation(TinkerSmeltery.cast);
    CustomTextureCreator.castModelLocation = new ResourceLocation(castLoc.getResourceDomain(), "item/" + castLoc.getResourcePath());
    ModelLoader.setCustomMeshDefinition(TinkerSmeltery.cast, new ItemMeshDefinition() {
      @Override
      public ModelResourceLocation getModelLocation(ItemStack stack) {
        NBTTagCompound tag = TagUtil.getTagSafe(stack);
        String suffix = tag.getString(Pattern.TAG_PARTTYPE);

        if(!suffix.isEmpty())
          suffix = "_" + suffix;

        return new ModelResourceLocation(new ResourceLocation(castLoc.getResourceDomain(),
                                                              castLoc.getResourcePath() + suffix),
                                         "inventory");
      }
    });
  }
}
