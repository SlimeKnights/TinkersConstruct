package tconstruct.client;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import mantle.client.MProxyClient;
import mantle.client.SmallFontRenderer;
import mantle.lib.client.MantleClientRegistry;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.particle.EntityAuraFX;
import net.minecraft.client.particle.EntityBreakingFX;
import net.minecraft.client.particle.EntityBubbleFX;
import net.minecraft.client.particle.EntityCloudFX;
import net.minecraft.client.particle.EntityCritFX;
import net.minecraft.client.particle.EntityDropParticleFX;
import net.minecraft.client.particle.EntityEnchantmentTableParticleFX;
import net.minecraft.client.particle.EntityExplodeFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntityFireworkSparkFX;
import net.minecraft.client.particle.EntityFlameFX;
import net.minecraft.client.particle.EntityFootStepFX;
import net.minecraft.client.particle.EntityHeartFX;
import net.minecraft.client.particle.EntityHugeExplodeFX;
import net.minecraft.client.particle.EntityLargeExplodeFX;
import net.minecraft.client.particle.EntityLavaFX;
import net.minecraft.client.particle.EntityNoteFX;
import net.minecraft.client.particle.EntityPortalFX;
import net.minecraft.client.particle.EntityReddustFX;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.client.particle.EntitySnowShovelFX;
import net.minecraft.client.particle.EntitySpellParticleFX;
import net.minecraft.client.particle.EntitySplashFX;
import net.minecraft.client.particle.EntitySuspendFX;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.RenderBlockFluid;

import org.w3c.dom.Document;

import tconstruct.TConstruct;
import tconstruct.armor.TinkerArmor;
import tconstruct.armor.ArmorProxyCommon;
import tconstruct.armor.modelblock.DryingRackRender;
import tconstruct.armor.modelblock.DryingRackSpecialRender;
import tconstruct.blocks.logic.DryingRackLogic;
import tconstruct.client.entity.item.ExplosiveRender;
import tconstruct.client.entity.projectile.ArrowRenderCustom;
import tconstruct.client.entity.projectile.DaggerRenderCustom;
import tconstruct.client.entity.projectile.LaunchedItemRender;
import tconstruct.client.gui.AdaptiveSmelteryGui;
import tconstruct.client.gui.ArmorExtendedGui;
import tconstruct.client.gui.CraftingStationGui;
import tconstruct.client.gui.FrypanGui;
import tconstruct.client.gui.FurnaceGui;
import tconstruct.client.gui.GuiLandmine;
import tconstruct.client.gui.KnapsackGui;
import tconstruct.client.gui.PartCrafterGui;
import tconstruct.client.gui.PatternChestGui;
import tconstruct.client.gui.SmelteryGui;
import tconstruct.client.gui.StencilTableGui;
import tconstruct.client.gui.ToolForgeGui;
import tconstruct.client.gui.ToolStationGui;
import tconstruct.client.pages.BlockCastPage;
import tconstruct.client.pages.MaterialPage;
import tconstruct.client.pages.ModifierPage;
import tconstruct.client.pages.ToolPage;
import tconstruct.client.tabs.InventoryTabArmorExtended;
import tconstruct.client.tabs.InventoryTabKnapsack;
import tconstruct.client.tabs.InventoryTabVanilla;
import tconstruct.client.tabs.TabRegistry;
import tconstruct.common.TProxyCommon;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.client.TConstructClientRegistry;
import tconstruct.library.client.ToolGuiElement;
import tconstruct.library.crafting.ToolBuilder;
import tconstruct.library.tools.ToolCore;
import tconstruct.mechworks.MechworksProxyCommon;
import tconstruct.mechworks.entity.item.EntityLandmineFirework;
import tconstruct.mechworks.entity.item.ExplosivePrimed;
import tconstruct.mechworks.inventory.ContainerLandmine;
import tconstruct.mechworks.logic.TileEntityLandmine;
import tconstruct.mechworks.model.CartRender;
import tconstruct.smeltery.SmelteryProxyCommon;
import tconstruct.smeltery.TinkerSmeltery;
import tconstruct.smeltery.logic.AdaptiveSmelteryLogic;
import tconstruct.smeltery.logic.CastingBasinLogic;
import tconstruct.smeltery.logic.CastingTableLogic;
import tconstruct.smeltery.logic.SmelteryLogic;
import tconstruct.smeltery.model.BlockRenderCastingChannel;
import tconstruct.smeltery.model.CastingBasinSpecialRender;
import tconstruct.smeltery.model.CastingTableSpecialRenderer;
import tconstruct.smeltery.model.PaneConnectedRender;
import tconstruct.smeltery.model.PaneRender;
import tconstruct.smeltery.model.SearedRender;
import tconstruct.smeltery.model.SmelteryRender;
import tconstruct.smeltery.model.TankAirRender;
import tconstruct.smeltery.model.TankRender;
import tconstruct.tools.TinkerTools;
import tconstruct.tools.ToolProxyCommon;
import tconstruct.tools.entity.ArrowEntity;
import tconstruct.tools.entity.DaggerEntity;
import tconstruct.tools.entity.FancyEntityItem;
import tconstruct.tools.entity.LaunchedPotion;
import tconstruct.tools.items.ManualInfo;
import tconstruct.tools.logic.CraftingStationLogic;
import tconstruct.tools.logic.FrypanLogic;
import tconstruct.tools.logic.FurnaceLogic;
import tconstruct.tools.logic.PartBuilderLogic;
import tconstruct.tools.logic.PatternChestLogic;
import tconstruct.tools.logic.StencilTableLogic;
import tconstruct.tools.logic.ToolForgeLogic;
import tconstruct.tools.logic.ToolStationLogic;
import tconstruct.tools.model.FancyItemRender;
import tconstruct.tools.model.FrypanRender;
import tconstruct.tools.model.TableRender;
import tconstruct.tools.model.TableRender;
import tconstruct.util.config.PHConstruct;
import tconstruct.util.player.ArmorExtended;
import tconstruct.util.player.KnapsackInventory;

import com.google.common.collect.Lists;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.VillagerRegistry;
import tconstruct.util.player.TPlayerStats;
import tconstruct.world.TinkerWorld;
import tconstruct.world.entity.BlueSlime;
import tconstruct.world.entity.CartEntity;
import tconstruct.world.entity.Crystal;
import tconstruct.world.model.BarricadeRender;
import tconstruct.world.model.CrystalRender;
import tconstruct.world.model.OreberryRender;
import tconstruct.world.model.PunjiRender;
import tconstruct.world.model.RenderLandmine;
import tconstruct.world.model.SlimeChannelRender;
import tconstruct.world.model.SlimePadRender;
import tconstruct.world.model.SlimeRender;

public class TProxyClient extends TProxyCommon
{
    /* TODO: Split this class up into its respective parts */
    public static SmallFontRenderer smallFontRenderer;
    public static IIcon metalBall;
    public static Minecraft mc;
    public static RenderItem itemRenderer = new RenderItem();

    public static ArmorExtended armorExtended = new ArmorExtended();
    public static KnapsackInventory knapsack = new KnapsackInventory();
    
    public void initialize()
    {
        registerRenderer();
        readManuals();
    }
    
    /* Registers any rendering code. */
    public void registerRenderer ()
    {
        Minecraft mc = Minecraft.getMinecraft();
        smallFontRenderer = new SmallFontRenderer(mc.gameSettings, new ResourceLocation("textures/font/ascii.png"), mc.renderEngine, false);
    }

    public static Document diary;
    public static Document volume1;
    public static Document volume2;
    public static Document smelter;
    public static ManualInfo manualData;

    public void readManuals ()
    {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        diary = readManual("/assets/tinker/manuals/diary.xml", dbFactory);
        volume1 = readManual("/assets/tinker/manuals/firstday.xml", dbFactory);
        volume2 = readManual("/assets/tinker/manuals/materials.xml", dbFactory);
        smelter = readManual("/assets/tinker/manuals/smeltery.xml", dbFactory);
        initManualIcons();
        initManualRecipes();
        initManualPages();
        manualData = new ManualInfo();
    }

    Document readManual (String location, DocumentBuilderFactory dbFactory)
    {
        try
        {
            InputStream stream = TConstruct.class.getResourceAsStream(location);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(stream);
            doc.getDocumentElement().normalize();
            return doc;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public void initManualIcons ()
    {
        MantleClientRegistry.registerManualIcon("torch", new ItemStack(Blocks.torch));
        MantleClientRegistry.registerManualIcon("sapling", new ItemStack(Blocks.sapling));
        MantleClientRegistry.registerManualIcon("workbench", new ItemStack(Blocks.crafting_table));
        MantleClientRegistry.registerManualIcon("coal", new ItemStack(Items.coal));

        MantleClientRegistry.registerManualIcon("woodplanks", new ItemStack(Blocks.planks));
        MantleClientRegistry.registerManualIcon("stoneblock", new ItemStack(Blocks.stone));
        MantleClientRegistry.registerManualIcon("ironingot", new ItemStack(Items.iron_ingot));
        MantleClientRegistry.registerManualIcon("flint", new ItemStack(Items.flint));
        MantleClientRegistry.registerManualIcon("cactus", new ItemStack(Blocks.cactus));
        MantleClientRegistry.registerManualIcon("bone", new ItemStack(Items.bone));
        MantleClientRegistry.registerManualIcon("obsidian", new ItemStack(Blocks.obsidian));
        MantleClientRegistry.registerManualIcon("netherrack", new ItemStack(Blocks.netherrack));
    }

    public void initManualRecipes ()
    {
    }

    void initManualPages ()
    {

    }

    public static Document getManualFromStack (ItemStack stack)
    {
        switch (stack.getItemDamage())
        {
        case 0:
            return volume1;
        case 1:
            return volume2;
        case 2:
            return smelter;
        case 3:
            return diary;
        }

        return null;
    }

    public void recalculateHealth ()
    {
        armorExtended.recalculateHealth(mc.thePlayer, TPlayerStats.get(mc.thePlayer));
    }

}
