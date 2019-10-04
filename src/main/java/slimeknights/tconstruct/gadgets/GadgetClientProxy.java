package slimeknights.tconstruct.gadgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.gadgets.client.FancyItemFrameRenderer;
import slimeknights.tconstruct.gadgets.entity.EflnBallEntity;
import slimeknights.tconstruct.gadgets.entity.FancyItemFrameEntity;
import slimeknights.tconstruct.gadgets.entity.FrameType;
import slimeknights.tconstruct.gadgets.entity.GlowballEntity;
import slimeknights.tconstruct.gadgets.item.SlimeBootsItem;
import slimeknights.tconstruct.gadgets.item.SlimeSlingItem;

import javax.annotation.Nonnull;

public class GadgetClientProxy extends ClientProxy {

  public static Minecraft minecraft = Minecraft.getInstance();

  @Override
  public void preInit() {
    super.preInit();
  }

  @Override
  public void init() {
    super.init();

    final ItemColors colors = minecraft.getItemColors();

    colors.register((@Nonnull ItemStack stack, int tintIndex) -> SlimeSlingItem.getColorFromStack(stack), TinkerGadgets.slime_sling_blue, TinkerGadgets.slime_sling_purple, TinkerGadgets.slime_sling_magma, TinkerGadgets.slime_sling_green, TinkerGadgets.slime_sling_blood);
    colors.register((@Nonnull ItemStack stack, int tintIndex) -> SlimeBootsItem.getColorFromStack(stack), TinkerGadgets.slime_boots_blue, TinkerGadgets.slime_boots_purple, TinkerGadgets.slime_boots_magma, TinkerGadgets.slime_boots_green, TinkerGadgets.slime_boots_blood);
  }

  @Override
  public void registerModels() {
    super.registerModels();

    // TODO: reinstate when Forge fixes itself
    //StateContainer<Block, BlockState> dummyContainer = new StateContainer.Builder<Block, BlockState>(Blocks.AIR).add(BooleanProperty.create("map")).create(BlockState::new);
    //for (FrameType frameType : FrameType.values()) {
    //  ResourceLocation fancyFrame = new ResourceLocation(TConstruct.modID, frameType.getName() + "_frame");
    //  for (BlockState state : dummyContainer.getValidStates()) {
    //    ModelLoader.addSpecialModel(BlockModelShapes.getModelLocation(fancyFrame, state));
    //  }
    //}

    for (FrameType frameType : FrameType.values()) {
      ModelLoader.addSpecialModel(new ModelResourceLocation(new ResourceLocation(TConstruct.modID, frameType.getName() + "_frame_empty"), "inventory"));
      ModelLoader.addSpecialModel(new ModelResourceLocation(new ResourceLocation(TConstruct.modID, frameType.getName() + "_frame_map"), "inventory"));
    }
  }

  @Override
  public void clientSetup() {
    super.clientSetup();

    Minecraft mc = Minecraft.getInstance();

    RenderingRegistry.registerEntityRenderingHandler(FancyItemFrameEntity.class, (manager) -> new FancyItemFrameRenderer(manager, mc.getItemRenderer()));
    RenderingRegistry.registerEntityRenderingHandler(GlowballEntity.class, (manager) -> new SpriteRenderer(manager, mc.getItemRenderer()));
    RenderingRegistry.registerEntityRenderingHandler(EflnBallEntity.class, (manager) -> new SpriteRenderer(manager, mc.getItemRenderer()));
  }

}
