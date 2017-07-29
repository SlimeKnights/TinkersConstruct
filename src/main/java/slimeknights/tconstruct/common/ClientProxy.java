package slimeknights.tconstruct.common;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;

import java.util.stream.Stream;

import javax.annotation.Nonnull;

import slimeknights.mantle.network.AbstractPacket;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.book.TinkerBook;
import slimeknights.tconstruct.library.client.CustomFontRenderer;
import slimeknights.tconstruct.library.client.CustomTextureCreator;
import slimeknights.tconstruct.library.client.crosshair.CrosshairRenderEvents;
import slimeknights.tconstruct.library.client.material.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.client.material.deserializers.BlockRenderInfoDeserializer;
import slimeknights.tconstruct.library.client.material.deserializers.ColoredRenderInfoDeserializer;
import slimeknights.tconstruct.library.client.material.deserializers.InverseMultiColorRenderInfoDeserializer;
import slimeknights.tconstruct.library.client.material.deserializers.MetalRenderInfoDeserializer;
import slimeknights.tconstruct.library.client.material.deserializers.MultiColorRenderInfoDeserializer;
import slimeknights.tconstruct.library.client.material.deserializers.TexturedMetalRenderInfoDeserializer;
import slimeknights.tconstruct.library.client.model.MaterialModelLoader;
import slimeknights.tconstruct.library.client.model.ModifierModelLoader;
import slimeknights.tconstruct.library.client.model.ToolModelLoader;
import slimeknights.tconstruct.library.client.particle.EntitySlimeFx;
import slimeknights.tconstruct.library.client.particle.Particles;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialGUI;
import slimeknights.tconstruct.library.tools.Pattern;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.client.ParticleEffect;
import slimeknights.tconstruct.shared.client.ParticleEndspeed;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.common.client.particle.ParticleAttackCleaver;
import slimeknights.tconstruct.tools.common.client.particle.ParticleAttackFrypan;
import slimeknights.tconstruct.tools.common.client.particle.ParticleAttackHammer;
import slimeknights.tconstruct.tools.common.client.particle.ParticleAttackHatchet;
import slimeknights.tconstruct.tools.common.client.particle.ParticleAttackLongsword;
import slimeknights.tconstruct.tools.common.client.particle.ParticleAttackLumberAxe;
import slimeknights.tconstruct.tools.common.client.particle.ParticleAttackRapier;
import slimeknights.tconstruct.tools.harvest.TinkerHarvestTools;

public abstract class ClientProxy extends CommonProxy {

  public static Material RenderMaterials[];
  public static Material RenderMaterialString;

  public static final ResourceLocation BOOK_MODIFY = Util.getResource("textures/gui/book/modify.png");

  private static final Minecraft mc = Minecraft.getMinecraft();
  public static CustomFontRenderer fontRenderer;

  protected static final ToolModelLoader loader = new ToolModelLoader();
  protected static final MaterialModelLoader materialLoader = new MaterialModelLoader();
  protected static final ModifierModelLoader modifierLoader = new ModifierModelLoader();

  public static void initClient() {
    // i wonder if this is OK :D
    ModelLoaderRegistry.registerLoader(loader);
    ModelLoaderRegistry.registerLoader(materialLoader);
    ModelLoaderRegistry.registerLoader(modifierLoader);

    MaterialRenderInfoLoader.addRenderInfo("colored", ColoredRenderInfoDeserializer.class);
    MaterialRenderInfoLoader.addRenderInfo("multicolor", MultiColorRenderInfoDeserializer.class);
    MaterialRenderInfoLoader.addRenderInfo("inverse_multicolor", InverseMultiColorRenderInfoDeserializer.class);
    MaterialRenderInfoLoader.addRenderInfo("metal", MetalRenderInfoDeserializer.class);
    MaterialRenderInfoLoader.addRenderInfo("metal_textured", TexturedMetalRenderInfoDeserializer.class);
    MaterialRenderInfoLoader.addRenderInfo("block", BlockRenderInfoDeserializer.class);

    MinecraftForge.EVENT_BUS.register(CustomTextureCreator.INSTANCE);
  }

  public static void initRenderMaterials() {
    RenderMaterials = new Material[4];
    RenderMaterials[0] = new MaterialGUI("_internal_render1");
    RenderMaterials[0].setRenderInfo(0x684e1e);
    RenderMaterials[1] = new MaterialGUI("_internal_render2");
    RenderMaterials[1].setRenderInfo(0xc1c1c1);
    RenderMaterials[2] = new MaterialGUI("_internal_render3");
    RenderMaterials[2].setRenderInfo(0x2376dd);
    RenderMaterials[3] = new MaterialGUI("_internal_render4");
    RenderMaterials[3].setRenderInfo(0x7146b0);

    RenderMaterialString = new MaterialGUI("_internal_renderString");
    RenderMaterialString.setRenderInfo(0xffffff);

    // yes, these will only be registered clientside
    // but it shouldn't matter because they're never used serverside and we don't use indices
    Stream.concat(
      Stream.of(RenderMaterials),
      Stream.of(RenderMaterialString)
    ).forEach(TinkerRegistry::addMaterial);
  }

  public static void initRenderer() {
    if(TinkerHarvestTools.pickaxe != null) {
      TinkerRegistry.tabTools.setDisplayIcon(TinkerHarvestTools.pickaxe.buildItemForRendering(ImmutableList.of(RenderMaterials[0], RenderMaterials[1], RenderMaterials[2])));
    }
    if(TinkerTools.pickHead != null) {
      TinkerRegistry.tabParts.setDisplayIcon(TinkerTools.pickHead.getItemstackWithMaterial(RenderMaterials[2]));
    }

    IReloadableResourceManager resourceManager = (IReloadableResourceManager) mc.getResourceManager();
    resourceManager.registerReloadListener(MaterialRenderInfoLoader.INSTANCE);
    resourceManager.registerReloadListener(CustomTextureCreator.INSTANCE);

    // Font renderer for tooltips and GUIs
    fontRenderer = new CustomFontRenderer(mc.gameSettings,
                                          new ResourceLocation("textures/font/ascii.png"),
                                          mc.renderEngine);
    if(mc.gameSettings.language != null) {
      fontRenderer.setUnicodeFlag(mc.getLanguageManager().isCurrentLocaleUnicode() || mc.gameSettings.forceUnicodeFont);
      fontRenderer.setBidiFlag(mc.getLanguageManager().isCurrentLanguageBidirectional());
    }
    resourceManager.registerReloadListener(fontRenderer);


    // Font Renderer for the tinker books
    FontRenderer bookRenderer = new CustomFontRenderer(mc.gameSettings,
                                                       new ResourceLocation("textures/font/ascii.png"),
                                                       mc.renderEngine);
    bookRenderer.setUnicodeFlag(true);
    if(mc.gameSettings.language != null) {
      fontRenderer.setBidiFlag(mc.getLanguageManager().isCurrentLanguageBidirectional());
    }
    TinkerBook.INSTANCE.fontRenderer = bookRenderer;

    MinecraftForge.EVENT_BUS.register(CrosshairRenderEvents.INSTANCE);
  }

  /** Register with name only, defaults to TiC domain */
  protected void registerItemModelTiC(ItemStack item, String name) {
    if(item != null && !StringUtils.isNullOrEmpty(name)) {
      ModelRegisterUtil.registerItemModel(item, Util.getResource(name));
    }
  }

  @Override
  public void sendPacketToServerOnly(AbstractPacket packet) {
    TinkerNetwork.sendToServer(packet);
  }

  @Override
  public void spawnParticle(Particles particleType, World world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int... data) {
    if(world == null) {
      world = mc.world;
    }
    Particle effect = createParticle(particleType, world, x, y, z, xSpeed, ySpeed, zSpeed, data);
    mc.effectRenderer.addEffect(effect);

    if(particleType == Particles.EFFECT && data[0] > 1) {
      for(int i = 0; i < data[0] - 1; i++) {
        effect = createParticle(particleType, world, x, y, z, xSpeed, ySpeed, zSpeed, data);
        mc.effectRenderer.addEffect(effect);
      }
    }
  }

  @Override
  public void spawnSlimeParticle(World world, double x, double y, double z) {
    mc.effectRenderer.addEffect(new EntitySlimeFx(world, x, y, z, TinkerCommons.matSlimeBallBlue.getItem(), TinkerCommons.matSlimeBallBlue.getItemDamage()));
  }

  public static Particle createParticle(Particles type, World world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int... data) {
    switch(type) {
      // entities
      case BLUE_SLIME:
        return new EntitySlimeFx(world, x, y, z, TinkerCommons.matSlimeBallBlue.getItem(), TinkerCommons.matSlimeBallBlue.getItemDamage());
      // attack
      case CLEAVER_ATTACK:
        return new ParticleAttackCleaver(world, x, y, z, xSpeed, ySpeed, zSpeed, mc.getTextureManager());
      case LONGSWORD_ATTACK:
        return new ParticleAttackLongsword(world, x, y, z, xSpeed, ySpeed, zSpeed, mc.getTextureManager());
      case RAPIER_ATTACK:
        return new ParticleAttackRapier(world, x, y, z, xSpeed, ySpeed, zSpeed, mc.getTextureManager());
      case HATCHET_ATTACK:
        return new ParticleAttackHatchet(world, x, y, z, xSpeed, ySpeed, zSpeed, mc.getTextureManager());
      case LUMBERAXE_ATTACK:
        return new ParticleAttackLumberAxe(world, x, y, z, xSpeed, ySpeed, zSpeed, mc.getTextureManager());
      case FRYPAN_ATTACK:
        return new ParticleAttackFrypan(world, x, y, z, xSpeed, ySpeed, zSpeed, mc.getTextureManager());
      case HAMMER_ATTACK:
        return new ParticleAttackHammer(world, x, y, z, xSpeed, ySpeed, zSpeed, mc.getTextureManager());
      // effects
      case EFFECT:
        return new ParticleEffect(data[1], world, x, y, z, xSpeed, ySpeed, zSpeed);
      case ENDSPEED:
        return new ParticleEndspeed(world, x, y, z, xSpeed, ySpeed, zSpeed);
    }

    return null;
  }

  @Override
  public void preventPlayerSlowdown(Entity player, float originalSpeed, Item item) {
    // has to be done in onUpdate because onTickUsing is too early and gets overwritten. bleh.
    if(player instanceof EntityPlayerSP) {
      EntityPlayerSP playerSP = (EntityPlayerSP) player;
      ItemStack usingItem = playerSP.getActiveItemStack();
      if(!usingItem.isEmpty() && usingItem.getItem() == item) {
        // no slowdown from charging it up
        playerSP.movementInput.moveForward *= originalSpeed * 5.0F;
        playerSP.movementInput.moveStrafe *= originalSpeed * 5.0F;
      }
    }
  }

  @Override
  public void customExplosion(World world, Explosion explosion) {
    net.minecraftforge.event.ForgeEventFactory.onExplosionStart(world, explosion);
    explosion.doExplosionA();
    explosion.doExplosionB(true);
  }

  public static class PatternMeshDefinition implements ItemMeshDefinition {

    private final ResourceLocation baseLocation;

    public PatternMeshDefinition(ResourceLocation baseLocation) {
      this.baseLocation = baseLocation;
    }

    @Nonnull
    @Override
    public ModelResourceLocation getModelLocation(@Nonnull ItemStack stack) {
      Item item = Pattern.getPartFromTag(stack);
      String suffix = "";
      if(item != null) {
        suffix = Pattern.getTextureIdentifier(item);
      }

      return new ModelResourceLocation(new ResourceLocation(baseLocation.getResourceDomain(),
                                                            baseLocation.getResourcePath() + suffix),
                                       "inventory");
    }
  }

  @Override
  public void updateEquippedItemForRendering(EnumHand hand) {
    mc.getItemRenderer().resetEquippedProgress(hand);
    mc.getItemRenderer().updateEquippedItem();
  }
}
