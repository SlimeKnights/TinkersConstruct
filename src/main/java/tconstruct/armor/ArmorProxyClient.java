package tconstruct.armor;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import tconstruct.armor.items.TravelGear;
import tconstruct.armor.model.BeltModel;
import tconstruct.armor.model.BootBump;
import tconstruct.armor.model.HiddenPlayerModel;
import tconstruct.armor.model.WingModel;
import tconstruct.client.TControls;
import tconstruct.client.TKeyHandler;
import tconstruct.client.TProxyClient;
import tconstruct.client.gui.ArmorExtendedGui;
import tconstruct.client.gui.KnapsackGui;
import tconstruct.client.tabs.InventoryTabArmorExtended;
import tconstruct.client.tabs.InventoryTabKnapsack;
import tconstruct.client.tabs.InventoryTabVanilla;
import tconstruct.client.tabs.TabRegistry;
import tconstruct.common.TProxyCommon;
import tconstruct.library.accessory.IAccessoryModel;
import tconstruct.library.modifier.IModifyable;
import tconstruct.util.player.TPlayerStats;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class ArmorProxyClient extends ArmorProxyCommon
{
    public static WingModel wings = new WingModel();
    public static BootBump bootbump = new BootBump();
    public static HiddenPlayerModel glove = new HiddenPlayerModel(0.25F, 4);
    public static HiddenPlayerModel vest = new HiddenPlayerModel(0.25f, 1);
    public static BeltModel belt = new BeltModel();

    @Override
    public void initialize ()
    {
        registerGuiHandler();
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
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
            TabRegistry.addTabsToInventory(inventory);
            return inventory;
        }
        if (ID == ArmorProxyCommon.armorGuiID)
        {
            TProxyClient.armorExtended.init(Minecraft.getMinecraft().thePlayer);
            return new ArmorExtendedGui(player.inventory, TProxyClient.armorExtended);
        }
        if (ID == ArmorProxyCommon.knapsackGuiID)
        {
            TProxyClient.knapsack.init(Minecraft.getMinecraft().thePlayer);
            return new KnapsackGui(player.inventory, TProxyClient.knapsack);
        }
        return null;
    }

    @Override
    public void registerTickHandler ()
    {
        FMLCommonHandler.instance().bus().register(new ArmorTickHandler());
        new ArmorTickHandler();
    }

    /* Keybindings */
    public static TControls controlInstance;

    @Override
    public void registerKeys ()
    {
        controlInstance = new TControls();
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

    /* HUD */
    @SubscribeEvent
    public void renderHealthbar (RenderGameOverlayEvent.Pre event)
    {
        if (!Loader.isModLoaded("tukmc_Vz"))// Loader check to avoid conflicting
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

                PotionEffect potion = mc.thePlayer.getActivePotionEffect(Potion.wither);
                if (potion != null)
                    return;
                potion = mc.thePlayer.getActivePotionEffect(Potion.poison);
                if (potion != null)
                    return;

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
                        this.drawTexturedModalRect(xBasePos + 8 * i, yBasePos, 0 + 18 * iter, 0, 8, 8);
                    }
                    if (hp % 2 == 1 && renderHearts < 10)
                    {
                        this.drawTexturedModalRect(xBasePos + 8 * renderHearts, yBasePos, 9 + 18 * iter, 0, 8, 8);
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
        TPlayerStats stats = TPlayerStats.get(player);
        if (stats.armor.inventory[1] != null)
        {
            Item item = stats.armor.inventory[1].getItem();
            ModelBiped model = item.getArmorModel(player, stats.armor.inventory[1], 4);

            if (item instanceof IAccessoryModel)
            {
                this.mc.getTextureManager().bindTexture(((IAccessoryModel) item).getWearbleTexture(player, stats.armor.inventory[1], 1));
                model.setLivingAnimations(player, limbSwingMod, limbSwing, partialTick);
                model.render(player, limbSwingMod, limbSwing, pitch, yawRotation - yawOffset, bodyRotation, zeropointsixtwofive);
            }
        }

        if (stats.armor.inventory[3] != null)
        {
            Item item = stats.armor.inventory[3].getItem();
            ModelBiped model = item.getArmorModel(player, stats.armor.inventory[3], 5);

            if (item instanceof IAccessoryModel)
            {
                this.mc.getTextureManager().bindTexture(((IAccessoryModel) item).getWearbleTexture(player, stats.armor.inventory[1], 1));
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

    /* Abilities */

    ItemStack prevFeet;
    double prevMotionY;
    boolean morphed;
    float prevMouseSensitivity;
    boolean sprint;

    @EventHandler
    public void playerTick (TickEvent.PlayerTickEvent event)
    {
        System.out.println("Client Tick!");
        EntityPlayer player = event.player;
        TPlayerStats stats = TPlayerStats.get(player);
        if (mc.thePlayer.onGround)
        {
            controlInstance.landOnGround();
        }
        if (stats.climbWalls && player.isCollidedHorizontally && !player.isSneaking())
        {
            player.motionY = 0.1176D;
            player.fallDistance = 0.0f;
        }

        //Feet changes
        ItemStack feet = player.getCurrentArmor(0);
        if (feet != null)
        {
            if (feet.getItem() instanceof TravelGear && player.stepHeight < 1.0f)
            {
                player.stepHeight += 0.6f;
            }
            if (feet.getItem() instanceof IModifyable && !player.isSneaking())
            {
                NBTTagCompound tag = feet.getTagCompound().getCompoundTag(((IModifyable) feet.getItem()).getBaseTagName());
                int sole = tag.getInteger("Slimy Soles");
                if (sole > 0)
                {
                    if (!player.isSneaking() && player.onGround && prevMotionY < -0.4)
                        player.motionY = -prevMotionY * (Math.min(0.99, sole * 0.2));
                }
            }
            prevMotionY = player.motionY;
        }
        if (feet != prevFeet)
        {
            if (prevFeet != null && prevFeet.getItem() instanceof TravelGear)
                player.stepHeight -= 0.6f;
            if (feet != null && feet.getItem() instanceof TravelGear)
                player.stepHeight += 0.6f;
            prevFeet = feet;
        }

        //Legs or wing changes
        /*ItemStack legs = player.getCurrentArmor(1);
        if (legs != null && legs.getItem() instanceof IModifyable)
        {
            NBTTagCompound tag = legs.getTagCompound().getCompoundTag(((IModifyable) legs.getItem()).getBaseTagName());
            if (player.isSprinting())
            {
                if (!sprint)
                {
                    sprint = true;
                    int sprintboost = tag.getInteger("Sprint Assist");
                    if (player.isSprinting() && sprintboost > 0)
                    {
                        prevMouseSensitivity = gs.mouseSensitivity;
                        gs.mouseSensitivity *= 1 - (0.15 * sprintboost);
                    }
                }
            }
            else if (sprint)
            {
                sprint = false;
                gs.mouseSensitivity = prevMouseSensitivity;
            }
        }*/
        if (!player.isPlayerSleeping() && !morphed)
        {
            ItemStack chest = player.getCurrentArmor(2);
            if (chest == null || !(chest.getItem() instanceof IModifyable))
            {
                PlayerAbilityHelper.setEntitySize(player, 0.6F, 1.8F);
            }
            else
            {
                NBTTagCompound tag = chest.getTagCompound().getCompoundTag(((IModifyable) chest.getItem()).getBaseTagName());
                int dodge = tag.getInteger("Perfect Dodge");
                if (dodge > 0)
                {
                    PlayerAbilityHelper.setEntitySize(player, Math.max(0.15F, 0.6F - (dodge * 0.09f)), 1.8F - (dodge * 0.04f));
                }
            }
        }
    }

    EntityPlayer getPlayer ()
    {
        return mc.thePlayer;
    }
}
