package slimeknights.tconstruct.common;

import net.minecraft.block.Block;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.GameData;

import javax.annotation.Nonnull;

import slimeknights.mantle.item.ItemBlockMeta;
import slimeknights.mantle.network.AbstractPacket;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.book.TinkerBook;
import slimeknights.tconstruct.library.client.CustomFontRenderer;
import slimeknights.tconstruct.library.client.CustomTextureCreator;
import slimeknights.tconstruct.library.client.model.MaterialModelLoader;
import slimeknights.tconstruct.library.client.model.ModifierModelLoader;
import slimeknights.tconstruct.library.client.model.ToolModelLoader;
import slimeknights.tconstruct.library.client.particle.EntitySlimeFx;
import slimeknights.tconstruct.library.client.particle.Particles;
import slimeknights.tconstruct.library.client.texture.AbstractColoredTexture;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.tools.Pattern;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.client.ParticleEffect;
import slimeknights.tconstruct.tools.client.particle.ParticleAttackCleaver;
import slimeknights.tconstruct.tools.client.particle.ParticleAttackFrypan;
import slimeknights.tconstruct.tools.client.particle.ParticleAttackHammer;
import slimeknights.tconstruct.tools.client.particle.ParticleAttackHatchet;
import slimeknights.tconstruct.tools.client.particle.ParticleAttackLongsword;
import slimeknights.tconstruct.tools.client.particle.ParticleAttackLumberAxe;
import slimeknights.tconstruct.tools.client.particle.ParticleAttackRapier;

public abstract class ClientProxy extends CommonProxy {

  public static CustomFontRenderer fontRenderer;

  protected static final ToolModelLoader loader = new ToolModelLoader();
  protected static final MaterialModelLoader materialLoader = new MaterialModelLoader();
  protected static final ModifierModelLoader modifierLoader = new ModifierModelLoader();

  public static void initClient() {
    // i wonder if this is OK :D
    ModelLoaderRegistry.registerLoader(loader);
    ModelLoaderRegistry.registerLoader(materialLoader);
    ModelLoaderRegistry.registerLoader(modifierLoader);
  }

  public static void initRenderer() {

    CustomTextureCreator creator = new CustomTextureCreator();

    MinecraftForge.EVENT_BUS.register(creator);
    ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(creator);
    ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(AbstractColoredTexture.CacheClearer.INSTANCE);

    // Font renderer for tooltips and GUIs
    fontRenderer = new CustomFontRenderer(Minecraft.getMinecraft().gameSettings,
                                          new ResourceLocation("textures/font/ascii.png"),
                                          Minecraft.getMinecraft().renderEngine);
    if(Minecraft.getMinecraft().gameSettings.language != null) {
      fontRenderer.setUnicodeFlag(Minecraft.getMinecraft().getLanguageManager().isCurrentLocaleUnicode() || Minecraft.getMinecraft().gameSettings.forceUnicodeFont);
      fontRenderer.setBidiFlag(Minecraft.getMinecraft().getLanguageManager().isCurrentLanguageBidirectional());
    }
    ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(fontRenderer);


    // Font Renderer for the tinker books
    FontRenderer bookRenderer = new CustomFontRenderer(Minecraft.getMinecraft().gameSettings,
                                                       new ResourceLocation("textures/font/ascii.png"),
                                                       Minecraft.getMinecraft().renderEngine);
    bookRenderer.setUnicodeFlag(true);
    if(Minecraft.getMinecraft().gameSettings.language != null) {
      fontRenderer.setBidiFlag(Minecraft.getMinecraft().getLanguageManager().isCurrentLanguageBidirectional());
    }
    TinkerBook.INSTANCE.fontRenderer = bookRenderer;
  }

  protected ResourceLocation registerModel(Item item, String... customVariants) {
    return registerModel(item, 0, customVariants);
  }

  /**
   * Registers a model variant for you. :3 The model-string is obtained through the game registry.
   */
  protected ResourceLocation registerModel(Item item, int meta, String... customVariants) {
    // get the registered name for the object
    Object o = GameData.getItemRegistry().getNameForObject(item);

    // are you trying to add an unregistered item...?
    if(o == null) {
      TConstruct.log.error("Trying to register a model for an unregistered item: %s" + item.getUnlocalizedName());
      // bad boi
      return null;
    }

    ResourceLocation location = (ResourceLocation) o;

    location = new ResourceLocation(location.getResourceDomain(), location.getResourcePath());

    // and plop it in.
    // This here is needed for the model to be found ingame when the game looks for a model to render an Itemstack (Item:Meta)
    ModelLoader.setCustomModelResourceLocation(item, meta,
                                               new ModelResourceLocation(location,
                                                                         "inventory"));

    // We have to readd the default variant if we have custom variants, since it wont be added otherwise
    if(customVariants.length > 0) {
      ModelLoader.registerItemVariants(item, location);
    }

    for(String customVariant : customVariants) {
      String custom = location.getResourceDomain() + ":" + customVariant;
      ModelLoader.registerItemVariants(item, new ResourceLocation(custom));
    }

    return location;
  }

  protected void registerItemModel(ItemStack item, String name) {

    // tell Minecraft which textures it has to load. This is resource-domain sensitive
    if(!name.contains(":")) {
      name = Util.resource(name);
    }

    ModelLoader.registerItemVariants(item.getItem(), new ResourceLocation(name));
    // tell the game which model to use for this item-meta combination
    ModelLoader.setCustomModelResourceLocation(item.getItem(), item
        .getMetadata(), new ModelResourceLocation(name, "inventory"));
  }

  /**
   * Registers a multimodel that should be loaded via our multimodel loader The model-string is obtained through the
   * game registry.
   */
  protected ResourceLocation registerToolModel(Item item) {
    ResourceLocation itemLocation = getItemLocation(item);
    if(itemLocation == null) {
      return null;
    }

    String path = "tools/" + itemLocation.getResourcePath() + ToolModelLoader.EXTENSION;

    return registerToolModel(item, new ResourceLocation(itemLocation.getResourceDomain(), path));
  }

  protected ResourceLocation registerToolModel(Item item, final ResourceLocation location) {
    if(!location.getResourcePath().endsWith(ToolModelLoader.EXTENSION)) {
      TConstruct.log.error("The material-model " + location.toString() + " does not end with '"
                           + ToolModelLoader.EXTENSION
                           + "' and will therefore not be loaded by the custom model loader!");
    }

    return registerIt(item, location);
  }

  public ResourceLocation registerMaterialItemModel(Item item) {
    ResourceLocation itemLocation = getItemLocation(item);
    if(itemLocation == null) {
      return null;
    }
    return registerMaterialModel(item, new ResourceLocation(itemLocation.getResourceDomain(),
                                                            itemLocation.getResourcePath()
                                                            + MaterialModelLoader.EXTENSION));
  }

  public ResourceLocation registerMaterialModel(Item item, final ResourceLocation location) {
    if(!location.getResourcePath().endsWith(MaterialModelLoader.EXTENSION)) {
      TConstruct.log.error("The material-model " + location.toString() + " does not end with '"
                           + MaterialModelLoader.EXTENSION
                           + "' and will therefore not be loaded by the custom model loader!");
    }

    return registerIt(item, location);
  }

  public void registerModifierModel(IModifier modifier, ResourceLocation location) {
    modifierLoader.registerModifierFile(modifier.getIdentifier(), location);
  }

  public ResourceLocation registerItemModel(Item item) {
    ResourceLocation itemLocation = getItemLocation(item);
    if(itemLocation == null) {
      return null;
    }

    return registerIt(item, itemLocation);
  }
  
  public ResourceLocation registerItemModel(Block block) {
    return registerItemModel(Item.getItemFromBlock(block));
  }

  public void registerItemModel(Item item, int meta, String variant) {
    ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), variant));
  }

  private static ResourceLocation registerIt(Item item, final ResourceLocation location) {
    // plop it in.
    // This here is needed for the model to be found ingame when the game looks for a model to render an Itemstack
    // we use an ItemMeshDefinition because it allows us to do it no matter what metadata we use
    ModelLoader.setCustomMeshDefinition(item, new ItemMeshDefinition() {
      @Nonnull
      @Override
      public ModelResourceLocation getModelLocation(@Nonnull ItemStack stack) {
        return new ModelResourceLocation(location, "inventory");
      }
    });

    // We have to readd the default variant if we have custom variants, since it wont be added otherwise and therefore not loaded
    ModelLoader.registerItemVariants(item, location);

    return location;
  }

  protected void registerItemBlockMeta(Block block) {
    if(block != null) {
      ((ItemBlockMeta) Item.getItemFromBlock(block)).registerItemModels();
    }
  }

  public static ResourceLocation getItemLocation(Item item) {
    return Util.getItemLocation(item);
  }

  @Override
  public void sendPacketToServerOnly(AbstractPacket packet) {
    TinkerNetwork.sendToServer(packet);
  }

  @Override
  public void spawnParticle(Particles particleType, World world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int... data) {
    if(world == null) {
      world = Minecraft.getMinecraft().theWorld;
    }
    Particle effect = createParticle(particleType, world, x, y, z, xSpeed, ySpeed, zSpeed, data);
    Minecraft.getMinecraft().effectRenderer.addEffect(effect);

    if(particleType == Particles.EFFECT && data[0] > 1) {
      for(int i = 0; i < data[0]-1; i++) {
        effect = createParticle(particleType, world, x,y,z, xSpeed,ySpeed,zSpeed, data);
        Minecraft.getMinecraft().effectRenderer.addEffect(effect);
      }
    }
  }

  @Override
  public void spawnSlimeParticle(World world, double x, double y, double z) {
    Minecraft.getMinecraft().effectRenderer.addEffect(new EntitySlimeFx(world, x,y,z, TinkerCommons.matSlimeBallBlue.getItem(), TinkerCommons.matSlimeBallBlue.getItemDamage()));
  }

  public static Particle createParticle(Particles type, World world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int... data) {
    switch(type) {
      // entities
      case BLUE_SLIME:
        return new EntitySlimeFx(world, x, y, z, TinkerCommons.matSlimeBallBlue.getItem(), TinkerCommons.matSlimeBallBlue.getItemDamage());
      // attack
      case CLEAVER_ATTACK:
        return new ParticleAttackCleaver(world, x, y, z, xSpeed, ySpeed, zSpeed, Minecraft.getMinecraft().getTextureManager());
      case LONGSWORD_ATTACK:
        return new ParticleAttackLongsword(world, x, y, z, xSpeed, ySpeed, zSpeed, Minecraft.getMinecraft().getTextureManager());
      case RAPIER_ATTACK:
        return new ParticleAttackRapier(world, x, y, z, xSpeed, ySpeed, zSpeed, Minecraft.getMinecraft().getTextureManager());
      case HATCHET_ATTACK:
        return new ParticleAttackHatchet(world, x, y, z, xSpeed, ySpeed, zSpeed, Minecraft.getMinecraft().getTextureManager());
      case LUMBERAXE_ATTACK:
        return new ParticleAttackLumberAxe(world, x, y, z, xSpeed, ySpeed, zSpeed, Minecraft.getMinecraft().getTextureManager());
      case FRYPAN_ATTACK:
        return new ParticleAttackFrypan(world, x, y, z, xSpeed, ySpeed, zSpeed, Minecraft.getMinecraft().getTextureManager());
      case HAMMER_ATTACK:
        return new ParticleAttackHammer(world, x, y, z, xSpeed, ySpeed, zSpeed, Minecraft.getMinecraft().getTextureManager());
      // effects
      case EFFECT:
        return new ParticleEffect(data[1], world, x,y,z, xSpeed, ySpeed, zSpeed);
    }

    return null;
  }

  @Override
  public void preventPlayerSlowdown(Entity player, float originalSpeed, Item item) {
    // has to be done in onUpdate because onTickUsing is too early and gets overwritten. bleh.
    if(player instanceof EntityPlayerSP) {
      EntityPlayerSP playerSP = (EntityPlayerSP) player;
      ItemStack usingItem = playerSP.inventory.getCurrentItem();
      if(usingItem != null && usingItem.getItem() == item) {
        // no slowdown from charging it up
        playerSP.movementInput.moveForward *= originalSpeed * 5.0F;
        playerSP.movementInput.moveStrafe *= originalSpeed * 5.0F;
      }
    }
  }

  @Override
  public void customExplosion(World world, Explosion explosion) {
    if (net.minecraftforge.event.ForgeEventFactory.onExplosionStart(world, explosion));
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
}
