package slimeknights.tconstruct.gadgets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.registry.IRenderFactory;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.gadgets.entity.EntityThrowball;

public class RenderThrowball extends RenderSnowball<EntityThrowball> {

  public static final IRenderFactory<EntityThrowball> FACTORY = new Factory();

  public RenderThrowball(RenderManager renderManagerIn, Item p_i46137_2_, RenderItem p_i46137_3_) {
    super(renderManagerIn, p_i46137_2_, p_i46137_3_);
  }

  @Nonnull
  @Override
  public ItemStack getStackToRender(EntityThrowball entityIn) {
    if(entityIn.type != null) {
      return new ItemStack(item, 1, entityIn.type.ordinal());
    }
    return ItemStack.EMPTY;
  }

  private static class Factory implements IRenderFactory<EntityThrowball> {

    @Override
    public Render<? super EntityThrowball> createRenderFor(RenderManager manager) {
      return new RenderThrowball(manager, TinkerGadgets.throwball, Minecraft.getMinecraft().getRenderItem());
    }
  }
}
