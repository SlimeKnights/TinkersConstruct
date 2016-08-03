package slimeknights.tconstruct.library.client.model;

import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.util.ResourceLocation;

import java.util.LinkedHashMap;

import javax.annotation.Nonnull;

/**
 * Maps a single property to multiple blockstate files in order to make the mapping easier to handle
 */
public class PropertyStateMapper extends StateMapperBase {

  private final PropertyEnum<?> prop;
  private final IProperty<?>[] ignore;

  private String name;

  public PropertyStateMapper(String name, PropertyEnum<?> prop, IProperty<?>... ignore) {
    this.name = name + "_";
    this.prop = prop;
    this.ignore = ignore;
  }

  @Nonnull
  @Override
  protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
    LinkedHashMap<IProperty<?>, Comparable<?>> map = Maps.newLinkedHashMap(state.getProperties());
    map.remove(prop);
    for(IProperty<?> ignored : ignore) {
      map.remove(ignored);
    }
    ResourceLocation res = new ResourceLocation(Block.REGISTRY.getNameForObject(state.getBlock()).getResourceDomain(), name + state.getValue(prop).getName());

    return new ModelResourceLocation(res, this.getPropertyString(map));
  }

}
