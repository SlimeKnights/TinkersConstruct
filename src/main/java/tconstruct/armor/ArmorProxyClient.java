package tconstruct.armor;

import com.google.common.collect.Lists;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import java.util.*;
import mantle.lib.client.MantleClientRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.settings.*;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.potion.*;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import tconstruct.armor.gui.*;
import tconstruct.armor.items.TravelGear;
import tconstruct.armor.model.*;
import tconstruct.armor.player.*;
import tconstruct.client.*;
import tconstruct.client.tabs.*;
import tconstruct.common.TProxyCommon;
import tconstruct.library.accessory.IAccessoryModel;
import tconstruct.library.client.TConstructClientRegistry;
import tconstruct.library.crafting.ModifyBuilder;
import tconstruct.tools.TinkerTools;
import tconstruct.world.TinkerWorld;

public class ArmorProxyClient extends ArmorProxyCommon
{
    public static WingModel wings = new WingModel();
    public static BootBump bootbump = new BootBump();
    public static HiddenPlayerModel glove = new HiddenPlayerModel(0.25F, 4);
    public static HiddenPlayerModel vest = new HiddenPlayerModel(0.25f, 1);
    public static BeltModel belt = new BeltModel();

    public static KnapsackInventory knapsack = new KnapsackInventory();
    public static ArmorExtended armorExtended = new ArmorExtended();

    @Override
    public void initialize ()
    {
        registerGuiHandler();
        registerKeys();
        registerManualIcons();
        registerManualRecipes();
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(new ArmorAbilitiesClient(mc, controlInstance));
    }

    private void registerManualIcons ()
    {
        MantleClientRegistry.registerManualIcon("travelgoggles", TinkerArmor.travelGoggles.getDefaultItem());
        MantleClientRegistry.registerManualIcon("travelvest", TinkerArmor.travelVest.getDefaultItem());
        MantleClientRegistry.registerManualIcon("travelwings", TinkerArmor.travelWings.getDefaultItem());
        MantleClientRegistry.registerManualIcon("travelboots", TinkerArmor.travelBoots.getDefaultItem());
        MantleClientRegistry.registerManualIcon("travelbelt", TinkerArmor.travelBelt.getDefaultItem());
        MantleClientRegistry.registerManualIcon("travelglove", TinkerArmor.travelGlove.getDefaultItem());
    }

    private void registerManualRecipes ()
    {
        ItemStack feather = new ItemStack(Items.feather);
        ItemStack redstone = new ItemStack(Items.redstone);
        ItemStack goggles = TinkerArmor.travelGoggles.getDefaultItem();

        TConstructClientRegistry.registerManualModifier("nightvision", goggles.copy(), new ItemStack(Items.flint_and_steel), new ItemStack(Items.potionitem, 1, 8198), new ItemStack(Items.golden_carrot), null);

        ItemStack vest = TinkerArmor.travelVest.getDefaultItem();
        TConstructClientRegistry.registerManualModifier("dodge", vest.copy(), new ItemStack(Items.ender_eye), new ItemStack(Items.ender_pearl), new ItemStack(Items.sugar), null);
        TConstructClientRegistry.registerManualModifier("stealth", vest.copy(), new ItemStack(Items.fermented_spider_eye), new ItemStack(Items.ender_eye), new ItemStack(Items.potionitem, 1, 8206), new ItemStack(Items.golden_carrot));

        ItemStack wings = TinkerArmor.travelWings.getDefaultItem();
        TConstructClientRegistry.registerManualModifier("doublejumpwings", wings.copy(), new ItemStack(Items.ghast_tear), new ItemStack(TinkerWorld.slimeGel, 1, 0), new ItemStack(Blocks.piston), null);

        ItemStack[] recipe = new ItemStack[] { new ItemStack(TinkerWorld.slimeGel, 1, 0), new ItemStack(Items.ender_pearl), feather, feather, feather, feather, feather, feather };
        ItemStack modWings = ModifyBuilder.instance.modifyItem(wings, recipe);
        MantleClientRegistry.registerManualLargeRecipe("featherfall", modWings.copy(), feather, new ItemStack(TinkerWorld.slimeGel, 1, 0), feather, feather, wings.copy(), feather, feather, new ItemStack(Items.ender_pearl), feather);

        ItemStack boots = TinkerArmor.travelBoots.getDefaultItem();
        TConstructClientRegistry.registerManualModifier("doublejumpboots", boots.copy(), new ItemStack(Items.ghast_tear), new ItemStack(TinkerWorld.slimeGel, 1, 1), new ItemStack(Blocks.piston), null);
        TConstructClientRegistry.registerManualModifier("waterwalk", boots.copy(), new ItemStack(Blocks.waterlily), new ItemStack(Blocks.waterlily));
        TConstructClientRegistry.registerManualModifier("leadboots", boots.copy(), new ItemStack(Blocks.iron_block));
        TConstructClientRegistry.registerManualModifier("slimysoles", boots.copy(), new ItemStack(TinkerWorld.slimePad, 1, 0), new ItemStack(TinkerWorld.slimePad, 1, 0));

        ItemStack gloves = TinkerArmor.travelGlove.getDefaultItem();
        TConstructClientRegistry.registerManualModifier("glovehaste", gloves.copy(), redstone, new ItemStack(Blocks.redstone_block));
        //MantleClientRegistry.registerManualSmallRecipe("gloveclimb", gloves.copy(), new ItemStack(Items.slime_ball), new ItemStack(Blocks.web), new ItemStack(TinkerTools.materials, 1, 25), null);
        TConstructClientRegistry.registerManualModifier("gloveknuckles", gloves.copy(), new ItemStack(Items.quartz), new ItemStack(Blocks.quartz_block, 1, Short.MAX_VALUE));

        // moss
        ItemStack moss = new ItemStack(TinkerTools.materials, 1, 6);
        TConstructClientRegistry.registerManualModifier("mossgoggles", goggles.copy(), moss.copy());
        TConstructClientRegistry.registerManualModifier("mossvest", vest.copy(), moss.copy());
        TConstructClientRegistry.registerManualModifier("mosswings", wings.copy(), moss.copy());
        TConstructClientRegistry.registerManualModifier("mossboots", boots.copy(), moss.copy());
    }

    @Override
    protected void registerGuiHandler ()
    {
        super.registerGuiHandler();
        TProxyCommon.registerClientGuiHandler(inventoryGui, this);
        TProxyCommon.registerClientGuiHandler(armorGuiID, this);
        TProxyCommon.registerClientGuiHandler(knapsackGuiID, this);
    }

    @Override
    public Object getClientGuiElement (int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if (ID == ArmorProxyCommon.inventoryGui)
        {
            GuiInventory inventory = new GuiInventory(player);
            return inventory;
        }
        if (ID == ArmorProxyCommon.armorGuiID)
        {
            ArmorProxyClient.armorExtended.init(Minecraft.getMinecraft().thePlayer);
            return new ArmorExtendedGui(player.inventory, ArmorProxyClient.armorExtended);
        }
        if (ID == ArmorProxyCommon.knapsackGuiID)
        {
            ArmorProxyClient.knapsack.init(Minecraft.getMinecraft().thePlayer);
            return new KnapsackGui(player.inventory, ArmorProxyClient.knapsack);
        }
        return null;
    }

    @Override
    public void registerTickHandler ()
    {
        FMLCommonHandler.instance().bus().register(new ArmorTickHandler());
    }

    /* Keybindings */
    public static ArmorControls controlInstance;

    @Override
    public void registerKeys ()
    {
        controlInstance = new ArmorControls();
        uploadKeyBindingsToGame(Minecraft.getMinecraft().gameSettings, controlInstance);

        TabRegistry.registerTab(new InventoryTabVanilla());
        TabRegistry.registerTab(new InventoryTabArmorExtended());
        TabRegistry.registerTab(new InventoryTabKnapsack());
    }

    public void uploadKeyBindingsToGame (GameSettings settings, TKeyHandler keyhandler)
    {
        ArrayList<KeyBinding> harvestedBindings = Lists.newArrayList();
        for (KeyBinding kb : keyhandler.keyBindings)
        {
            harvestedBindings.add(kb);
        }

        KeyBinding[] modKeyBindings = harvestedBindings.toArray(new KeyBinding[harvestedBindings.size()]);
        KeyBinding[] allKeys = new KeyBinding[settings.keyBindings.length + modKeyBindings.length];
        System.arraycopy(settings.keyBindings, 0, allKeys, 0, settings.keyBindings.length);
        System.arraycopy(modKeyBindings, 0, allKeys, settings.keyBindings.length, modKeyBindings.length);
        settings.keyBindings = allKeys;
        settings.loadOptions();
    }

    Minecraft mc = Minecraft.getMinecraft();

    private static final ResourceLocation hearts = new ResourceLocation("tinker", "textures/gui/newhearts.png");
    private static final ResourceLocation icons = new ResourceLocation("textures/gui/icons.png");
    // public static int left_height = 39;
    // public static int right_height = 39;
    Random rand = new Random();
    int updateCounter = 0;

    GameSettings gs = Minecraft.getMinecraft().gameSettings;

    @SubscribeEvent
    public void goggleZoom (FOVUpdateEvent event)
    {
        if (ArmorControls.zoom)
        {
            ItemStack helmet = event.entity.getCurrentArmor(3);
            if (helmet != null && helmet.getItem() instanceof TravelGear)
            {
                event.newfov = 0.3f;
            }
        }
        //ItemStack feet = player.getCurrentArmor(0);
        //event.newfov = 1.0f;
    }

    /* HUD */
    @SubscribeEvent
    public void renderHealthbar (RenderGameOverlayEvent.Pre event)
    {
        if (!Loader.isModLoaded("tukmc_Vz") || Loader.isModLoaded("borderlands"))// Loader check to avoid conflicting
        // with a GUI mod (thanks Vazkii!)
        {
            if (event.type == ElementType.HEALTH)
            {
                updateCounter++;

                ScaledResolution scaledresolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
                int scaledWidth = scaledresolution.getScaledWidth();
                int scaledHeight = scaledresolution.getScaledHeight();
                int xBasePos = scaledWidth / 2 - 91;
                int yBasePos = scaledHeight - 39;

                boolean highlight = mc.thePlayer.hurtResistantTime / 3 % 2 == 1;

                if (mc.thePlayer.hurtResistantTime < 10)
                {
                    highlight = false;
                }

                IAttributeInstance attrMaxHealth = this.mc.thePlayer.getEntityAttribute(SharedMonsterAttributes.maxHealth);
                int health = MathHelper.ceiling_float_int(mc.thePlayer.getHealth());
                int healthLast = MathHelper.ceiling_float_int(mc.thePlayer.prevHealth);
                float healthMax = (float) attrMaxHealth.getAttributeValue();
                if (healthMax > 20)
                    healthMax = 20;
                float absorb = this.mc.thePlayer.getAbsorptionAmount();

                int healthRows = MathHelper.ceiling_float_int((healthMax + absorb) / 2.0F / 10.0F);
                int rowHeight = Math.max(10 - (healthRows - 2), 3);

                this.rand.setSeed((long) (updateCounter * 312871));

                int left = scaledWidth / 2 - 91;
                int top = scaledHeight - GuiIngameForge.left_height;

                if (!GuiIngameForge.renderExperiance)
                {
                    top += 7;
                    yBasePos += 7;
                }

                int regen = -1;
                if (mc.thePlayer.isPotionActive(Potion.regeneration))
                {
                    regen = updateCounter % 25;
                }

                final int TOP = 9 * (mc.theWorld.getWorldInfo().isHardcoreModeEnabled() ? 5 : 0);
                final int BACKGROUND = (highlight ? 25 : 16);
                int MARGIN = 16;
                if (mc.thePlayer.isPotionActive(Potion.poison))
                    MARGIN += 36;
                else if (mc.thePlayer.isPotionActive(Potion.wither))
                    MARGIN += 72;
                float absorbRemaining = absorb;

                for (int i = MathHelper.ceiling_float_int((healthMax + absorb) / 2.0F) - 1; i >= 0; --i)
                {
                    int b0 = (highlight ? 1 : 0);
                    int row = MathHelper.ceiling_float_int((float) (i + 1) / 10.0F) - 1;
                    int x = left + i % 10 * 8;
                    int y = top - row * rowHeight;

                    if (health <= 4)
                        y += rand.nextInt(2);
                    if (i == regen)
                        y -= 2;

                    drawTexturedModalRect(x, y, BACKGROUND, TOP, 9, 9);

                    if (highlight)
                    {
                        if (i * 2 + 1 < healthLast)
                            drawTexturedModalRect(x, y, MARGIN + 54, TOP, 9, 9); // 6
                        else if (i * 2 + 1 == healthLast)
                            drawTexturedModalRect(x, y, MARGIN + 63, TOP, 9, 9); // 7
                    }

                    if (absorbRemaining > 0.0F)
                    {
                        if (absorbRemaining == absorb && absorb % 2.0F == 1.0F)
                            drawTexturedModalRect(x, y, MARGIN + 153, TOP, 9, 9); // 17
                        else
                            drawTexturedModalRect(x, y, MARGIN + 144, TOP, 9, 9); // 16
                        absorbRemaining -= 2.0F;
                    }
                    else
                    {
                        if (i * 2 + 1 < health)
                            drawTexturedModalRect(x, y, MARGIN + 36, TOP, 9, 9); // 4
                        else if (i * 2 + 1 == health)
                            drawTexturedModalRect(x, y, MARGIN + 45, TOP, 9, 9); // 5
                    }
                }

                int potionOffset = 0;
                PotionEffect potion = mc.thePlayer.getActivePotionEffect(Potion.wither);
                if (potion != null)
                    potionOffset = 18;
                potion = mc.thePlayer.getActivePotionEffect(Potion.poison);
                if (potion != null)
                    potionOffset = 9;

                // Extra hearts
                this.mc.getTextureManager().bindTexture(hearts);

                int hp = MathHelper.ceiling_float_int(this.mc.thePlayer.getHealth());
                for (int iter = 0; iter < hp / 20; iter++)
                {
                    int renderHearts = (hp - 20 * (iter + 1)) / 2;
                    if (renderHearts > 10)
                        renderHearts = 10;
                    for (int i = 0; i < renderHearts; i++)
                    {
                        int y = 0;
                        if (i == regen)
                            y -= 2;
                        this.drawTexturedModalRect(xBasePos + 8 * i, yBasePos + y, 0 + 18 * iter, potionOffset, 9, 9);
                    }
                    if (hp % 2 == 1 && renderHearts < 10)
                    {
                        this.drawTexturedModalRect(xBasePos + 8 * renderHearts, yBasePos, 9 + 18 * iter, potionOffset, 9, 9);
                    }
                }

                this.mc.getTextureManager().bindTexture(icons);
                GuiIngameForge.left_height += 10;
                if (absorb > 0)
                    GuiIngameForge.left_height += 10;

                event.setCanceled(true);
                if (event.type == ElementType.CROSSHAIRS && gs.thirdPersonView != 0)
                {
                    event.setCanceled(true);
                }
            }
        }
    }

    public void drawTexturedModalRect (int par1, int par2, int par3, int par4, int par5, int par6)
    {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double) (par1 + 0), (double) (par2 + par6), (double) this.zLevel, (double) ((float) (par3 + 0) * f), (double) ((float) (par4 + par6) * f1));
        tessellator.addVertexWithUV((double) (par1 + par5), (double) (par2 + par6), (double) this.zLevel, (double) ((float) (par3 + par5) * f), (double) ((float) (par4 + par6) * f1));
        tessellator.addVertexWithUV((double) (par1 + par5), (double) (par2 + 0), (double) this.zLevel, (double) ((float) (par3 + par5) * f), (double) ((float) (par4 + 0) * f1));
        tessellator.addVertexWithUV((double) (par1 + 0), (double) (par2 + 0), (double) this.zLevel, (double) ((float) (par3 + 0) * f), (double) ((float) (par4 + 0) * f1));
        tessellator.draw();
    }

    double zLevel = 0;

    /* Armor rendering */
    @SubscribeEvent
    public void adjustArmor (RenderPlayerEvent.SetArmorModel event)
    {
        switch (event.slot)
        {
        case 1:
            ArmorProxyClient.vest.onGround = event.renderer.modelBipedMain.onGround;
            ArmorProxyClient.vest.isRiding = event.renderer.modelBipedMain.isRiding;
            ArmorProxyClient.vest.isChild = event.renderer.modelBipedMain.isChild;
            ArmorProxyClient.vest.isSneak = event.renderer.modelBipedMain.isSneak;
        case 2:
            ArmorProxyClient.wings.onGround = event.renderer.modelBipedMain.onGround;
            ArmorProxyClient.wings.isRiding = event.renderer.modelBipedMain.isRiding;
            ArmorProxyClient.wings.isChild = event.renderer.modelBipedMain.isChild;
            ArmorProxyClient.wings.isSneak = event.renderer.modelBipedMain.isSneak;

            ArmorProxyClient.glove.onGround = event.renderer.modelBipedMain.onGround;
            ArmorProxyClient.glove.isRiding = event.renderer.modelBipedMain.isRiding;
            ArmorProxyClient.glove.isChild = event.renderer.modelBipedMain.isChild;
            ArmorProxyClient.glove.isSneak = event.renderer.modelBipedMain.isSneak;
            ArmorProxyClient.glove.heldItemLeft = event.renderer.modelBipedMain.heldItemLeft;
            ArmorProxyClient.glove.heldItemRight = event.renderer.modelBipedMain.heldItemRight;

            ArmorProxyClient.belt.onGround = event.renderer.modelBipedMain.onGround;
            ArmorProxyClient.belt.isRiding = event.renderer.modelBipedMain.isRiding;
            ArmorProxyClient.belt.isChild = event.renderer.modelBipedMain.isChild;
            ArmorProxyClient.belt.isSneak = event.renderer.modelBipedMain.isSneak;

            renderArmorExtras(event);

            break;
        case 3:
            ArmorProxyClient.bootbump.onGround = event.renderer.modelBipedMain.onGround;
            ArmorProxyClient.bootbump.isRiding = event.renderer.modelBipedMain.isRiding;
            ArmorProxyClient.bootbump.isChild = event.renderer.modelBipedMain.isChild;
            ArmorProxyClient.bootbump.isSneak = event.renderer.modelBipedMain.isSneak;
            break;
        }
    }

    void renderArmorExtras (RenderPlayerEvent.SetArmorModel event)
    {
        float partialTick = event.partialRenderTick;

        EntityPlayer player = event.entityPlayer;
        float posX = (float) (player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTick);
        float posY = (float) (player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTick);
        float posZ = (float) (player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTick);

        float yawOffset = this.interpolateRotation(player.prevRenderYawOffset, player.renderYawOffset, partialTick);
        float yawRotation = this.interpolateRotation(player.prevRotationYawHead, player.rotationYawHead, partialTick);
        float pitch;
        final float zeropointsixtwofive = 0.0625F;

        if (player.isRiding() && player.ridingEntity instanceof EntityLivingBase)
        {
            EntityLivingBase entitylivingbase1 = (EntityLivingBase) player.ridingEntity;
            yawOffset = this.interpolateRotation(entitylivingbase1.prevRenderYawOffset, entitylivingbase1.renderYawOffset, partialTick);
            pitch = MathHelper.wrapAngleTo180_float(yawRotation - yawOffset);

            if (pitch < -85.0F)
            {
                pitch = -85.0F;
            }

            if (pitch >= 85.0F)
            {
                pitch = 85.0F;
            }

            yawOffset = yawRotation - pitch;

            if (pitch * pitch > 2500.0F)
            {
                yawOffset += pitch * 0.2F;
            }
        }

        pitch = this.handleRotationFloat(player, partialTick);
        float bodyRotation = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTick;
        float limbSwing = player.prevLimbSwingAmount + (player.limbSwingAmount - player.prevLimbSwingAmount) * partialTick;
        float limbSwingMod = player.limbSwing - player.limbSwingAmount * (1.0F - partialTick);
        //TPlayerStats stats = TPlayerStats.get(player);
        ArmorExtended armor = ArmorProxyClient.armorExtended; //TODO: Do this for every player, not just the client
        if (armor.inventory[1] != null)
        {
            Item item = armor.inventory[1].getItem();
            ModelBiped model = item.getArmorModel(player, armor.inventory[1], 4);

            if (item instanceof IAccessoryModel)
            {
                this.mc.getTextureManager().bindTexture(((IAccessoryModel) item).getWearbleTexture(player, armor.inventory[1], 1));
                model.setLivingAnimations(player, limbSwingMod, limbSwing, partialTick);
                model.render(player, limbSwingMod, limbSwing, pitch, yawRotation - yawOffset, bodyRotation, zeropointsixtwofive);
            }
        }

        if (armor.inventory[3] != null)
        {
            Item item = armor.inventory[3].getItem();
            ModelBiped model = item.getArmorModel(player, armor.inventory[3], 5);

            if (item instanceof IAccessoryModel)
            {
                this.mc.getTextureManager().bindTexture(((IAccessoryModel) item).getWearbleTexture(player, armor.inventory[1], 1));
                model.setLivingAnimations(player, limbSwingMod, limbSwing, partialTick);
                model.render(player, limbSwingMod, limbSwing, pitch, yawRotation - yawOffset, bodyRotation, zeropointsixtwofive);
            }
        }
    }

    private float interpolateRotation (float par1, float par2, float par3)
    {
        float f3;

        for (f3 = par2 - par1; f3 < -180.0F; f3 += 360.0F)
        {
            ;
        }

        while (f3 >= 180.0F)
        {
            f3 -= 360.0F;
        }

        return par1 + par3 * f3;
    }

    protected float handleRotationFloat (EntityLivingBase par1EntityLivingBase, float par2)
    {
        return (float) par1EntityLivingBase.ticksExisted + par2;
    }

}
