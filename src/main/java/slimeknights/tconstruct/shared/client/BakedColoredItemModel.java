package slimeknights.tconstruct.shared.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.annotation.Nonnull;

import slimeknights.mantle.client.model.BakedWrapper;
import slimeknights.tconstruct.library.client.model.ModelHelper;

@SideOnly(Side.CLIENT)
public class BakedColoredItemModel extends BakedWrapper {

  public final static int MAX_SUPPORTED_TINT_INDEX = 7;

  private final ImmutableMap<EnumFacing, ImmutableList<BakedQuad>> coloredQuads;
  private final ImmutableList<BakedQuad> coloredDefaultQuads;

  public BakedColoredItemModel(ItemStack itemStack, IBakedModel parent) {
    super(parent);

    Map<EnumFacing, List<BakedQuad>> quads = null;
    boolean didColorQuads = false;

    for(int i = 0; i < MAX_SUPPORTED_TINT_INDEX; i++) {
      int color = Minecraft.getMinecraft().getItemColors().getColorFromItemstack(itemStack, i);
      if(color == -1) {
        continue;
      }

      if(quads == null) {
        // needs to be done this way to keep quad order the same
        quads = Maps.newHashMap();
        for(EnumFacing facing : ModelHelper.MODEL_SIDES) {
          quads.put(facing, Lists.newArrayList(parent.getQuads(null, facing, -1)));
        }
      }


      // itemconsumer is a color-transformer we can reuse
      float b = (float) (color & 0xFF) / 0xFF;
      float g = (float) ((color >>> 8) & 0xFF) / 0xFF;
      float r = (float) ((color >>> 16) & 0xFF) / 0xFF;
      float a = (float) ((color >>> 24) & 0xFF) / 0xFF;
      if(a == 0f) {
        a = 1f;
      }

      // go through all quads and color matching ones
      for(EnumFacing facing : ModelHelper.MODEL_SIDES) {
        ListIterator<BakedQuad> iter = quads.get(facing).listIterator();
        while(iter.hasNext()) {
          BakedQuad quad = iter.next();
          if(quad.getTintIndex() == i) {
            UnpackedBakedQuad.Builder quadBuilder = new UnpackedBakedQuad.Builder(quad.getFormat());
            LightUtil.ItemConsumer itemConsumer = new LightUtil.ItemConsumer(quadBuilder);
            itemConsumer.setAuxColor(r, g, b, a);

            quad.pipe(itemConsumer);
            iter.set(quadBuilder.build());
            didColorQuads = true;
          }
        }
      }
    }

    if(didColorQuads) {
      ImmutableMap.Builder<EnumFacing, ImmutableList<BakedQuad>> mapBuilder = ImmutableMap.builder();
      // immutablemap doesn't allow null as key :(
      for(EnumFacing facing : EnumFacing.values()) {
        mapBuilder.put(facing, ImmutableList.copyOf(quads.get(facing)));
      }
      coloredQuads = mapBuilder.build();
      coloredDefaultQuads = ImmutableList.copyOf(quads.get(null));
    }
    else {
      coloredQuads = ImmutableMap.of();
      coloredDefaultQuads = ImmutableList.of();
    }
  }

  @Nonnull
  @Override
  public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
    if(coloredQuads.isEmpty()) {
      return super.getQuads(state, side, rand);
    }

    if(side == null) {
      return coloredDefaultQuads;
    }

    return coloredQuads.get(side);
  }
}
