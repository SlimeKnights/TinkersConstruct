package slimeknights.tconstruct.world.client;

import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.LinkedHashMap;

@SideOnly(Side.CLIENT)
public class CustomStateMap extends StateMapperBase {

  private final String customName;

  public CustomStateMap(String customName) {
    this.customName = customName;
  }

  protected ModelResourceLocation getModelResourceLocation(IBlockState state)
  {
    LinkedHashMap<IProperty,Comparable> linkedhashmap = Maps.newLinkedHashMap(state.getProperties());
    ResourceLocation res = new ResourceLocation(((ResourceLocation)Block.blockRegistry.getNameForObject(state.getBlock())).getResourceDomain(), customName);

    return new ModelResourceLocation(res, this.getPropertyString(linkedhashmap));
  }
}
