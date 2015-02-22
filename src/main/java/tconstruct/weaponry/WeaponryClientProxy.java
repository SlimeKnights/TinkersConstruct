package tconstruct.weaponry;

import mantle.lib.client.MantleClientRegistry;
import net.minecraft.init.Items;
import tconstruct.client.AmmoItemRenderer;
import tconstruct.library.crafting.ToolBuilder;
import tconstruct.library.entity.ProjectileBase;
import tconstruct.weaponry.client.AmmoSlotHandler;
import tconstruct.weaponry.client.CrosshairHandler;
import tconstruct.weaponry.client.RenderEventHandler;
import tconstruct.weaponry.client.entity.ArrowEntityRenderer;
import tconstruct.weaponry.client.entity.JavelinEntityRenderer;
import tconstruct.weaponry.client.entity.ProjectileBaseRenderer;
import tconstruct.weaponry.client.entity.ShurikenEntityRenderer;
import tconstruct.weaponry.client.item.*;
import tconstruct.weaponry.entity.*;
import tconstruct.util.Reference;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import tconstruct.library.client.TConstructClientRegistry;
import tconstruct.library.crafting.StencilBuilder;
import tconstruct.library.tools.ToolCore;
import tconstruct.tools.TinkerTools;

import static tconstruct.weaponry.TinkerWeaponry.*;

public class WeaponryClientProxy extends WeaponryCommonProxy {
    public static AmmoItemRenderer renderer;

    @Override
    public void init() {
        super.init();

        MinecraftForge.EVENT_BUS.register(new CrosshairHandler());
        MinecraftForge.EVENT_BUS.register(new AmmoSlotHandler());
        MinecraftForge.EVENT_BUS.register(new RenderEventHandler());

        registerRenderers();
        registerEntityRendering();
        registerMaterialRendering();

        buttons();
        registerManualIcons();
    }

    private void registerRenderers()
    {
        renderer = new AmmoItemRenderer();
        MinecraftForgeClient.registerItemRenderer(TinkerWeaponry.shuriken, renderer);
        MinecraftForgeClient.registerItemRenderer(TinkerWeaponry.throwingknife, new ThrowingKnifeRenderer());
        MinecraftForgeClient.registerItemRenderer(TinkerWeaponry.javelin, new JavelinRenderer());

        MinecraftForgeClient.registerItemRenderer(TinkerWeaponry.shortbow, new BowRenderer());
        MinecraftForgeClient.registerItemRenderer(TinkerWeaponry.longbow, new BowRenderer());
        MinecraftForgeClient.registerItemRenderer(TinkerWeaponry.crossbow, new CrossbowRenderer());

        MinecraftForgeClient.registerItemRenderer(TinkerWeaponry.arrowAmmo, renderer);
        MinecraftForgeClient.registerItemRenderer(TinkerWeaponry.boltAmmo, renderer);

        //MinecraftForgeClient.registerItemRenderer(TinkerWeaponry.boneana, );
    }

    private void registerEntityRendering()
    {
        ProjectileBaseRenderer defaultRenderer = new ProjectileBaseRenderer<ProjectileBase>();
        RenderingRegistry.registerEntityRenderingHandler(ShurikenEntity.class, new ShurikenEntityRenderer());
        RenderingRegistry.registerEntityRenderingHandler(ThrowingKnifeEntity.class, defaultRenderer);
        RenderingRegistry.registerEntityRenderingHandler(JavelinEntity.class, new JavelinEntityRenderer());
        RenderingRegistry.registerEntityRenderingHandler(ArrowEntity.class, new ArrowEntityRenderer());
        RenderingRegistry.registerEntityRenderingHandler(BoltEntity.class, new ArrowEntityRenderer(0.8f));
    }

    private void registerMaterialRendering()
    {
        ToolCore arrow = TinkerWeaponry.arrowAmmo;
        String pre = Reference.resource(arrow.getDefaultFolder()) + "/";

        String[] shaft = {"wood", "bone", "reed", "blaze", null};
        String[] fletching = { "feather", "leaf", "slime", "blueslime", "slimeleaf" };

        // we register different textures for the different parts per index
        for(int i = 0; i < 5; i++) {
            String handletex = pre + shaft[i] + arrow.getIconSuffix(2);
            String acctex = pre + fletching[i] + arrow.getIconSuffix(3);
            arrow.registerAlternatePartPaths(i, new String[]{null, null, handletex, acctex});
            TinkerWeaponry.boltAmmo.registerAlternatePartPaths(i, new String[]{null, null, null, acctex});
        }

        // for bolts too
        pre = Reference.resource(TinkerWeaponry.boltAmmo.getDefaultFolder()) + "/";
        for(int i = 0; i < 5; i++) {
            String acctex = pre + fletching[i] + TinkerWeaponry.boltAmmo.getIconSuffix(3);
            TinkerWeaponry.boltAmmo.registerAlternatePartPaths(i, new String[]{null, null, null, acctex});
        }


        // bowstring
        String[] bowstringTypes = { "string", "magicfabric", "flamestring" };
        for (int bowIter = 0; bowIter < bowstringTypes.length; bowIter++)
        {
            TConstructClientRegistry.addAlternateMaterialRenderMapping(TinkerWeaponry.shortbow, bowIter, Reference.RESOURCE, bowstringTypes[bowIter], true);
        }
    }

    private void buttons()
    {
        final String tex = "textures/gui/icons.png";

        // Stencil Table
        TConstructClientRegistry.addStencilButton2(11, 3, 21, Reference.RESOURCE, tex); // arrow head
        TConstructClientRegistry.addStencilButton2(12, 3, 22, Reference.RESOURCE, tex); // fletchling
        TConstructClientRegistry.addStencilButton2(0,0, -1, null, null);
        TConstructClientRegistry.addStencilButton2(0,0, -1, null, null);

        TConstructClientRegistry.addStencilButton2(3, 4, 27, Reference.RESOURCE, tex); // bow limb
        TConstructClientRegistry.addStencilButton2(10, 3, 23, Reference.RESOURCE, tex); // bowstring
        TConstructClientRegistry.addStencilButton2(1, 4, 25, Reference.RESOURCE, tex); // crossbow limb
        TConstructClientRegistry.addStencilButton2(2, 4, 26, Reference.RESOURCE, tex); // crossbow body

        TConstructClientRegistry.addStencilButton2(0, 4, 24, Reference.RESOURCE, tex); // shuriken
        //TConstructClientRegistry.addStencilButton2(4, 4, index, Reference.RESOURCE, "textures/gui/icons.png"); // bolt


        // Tool Station
        ToolCore[] tools = {shortbow, arrowAmmo, throwingknife, javelin};
        int[][] icons = {
                new int[] {3,  9, 1}, // shortbow
                new int[] {7, 10, 1}, // arrow
                new int[] {2,  1, 5}, // throwingknife
                new int[] {1,  2, 5}  // javelin
        };
        int[][] coords = {
                new int[] {  3, 10,  3, 13 }, new int[] { 4, 3,  4, 13 }, // shortbow
                new int[] { 11,  0, 12, 13 }, new int[] { 3, 3,  3, 13 }, // arrow
                new int[] {  7,  0, 13, 13 }, new int[] { 2, 3, 13, 13 }, // throwingknife
                new int[] { 11,  8,  8, 13 }, new int[] { 3, 3,  3, 13 }  // javelin
        };

        for (int i = 0; i < tools.length; i++)
        {
            String locString = String.format("gui.toolstation.%s.desc", tools[i].getToolName().toLowerCase());
            TConstructClientRegistry.addToolButton(icons[i][0], icons[i][1], icons[i][2], coords[i*2], coords[i*2+1], tools[i].getLocalizedToolName(), locString, Reference.RESOURCE, tex);
        }

        // Tool Forge
        tools = new ToolCore[] {longbow, crossbow, boltAmmo, shuriken};
        icons = new int[][] {
                new int[] {8, 11, 1}, // longbow
                new int[] {9, 12, 1}, // crossbow
                new int[] {2, 13, 1}, // bolt
                new int[] {4,  0, 5}  // shuriken
        };
        coords = new int[][] {
                new int[] { 3, 10,  3,  9 }, new int[] { 4, 3,  4,  2 }, // longbow
                new int[] { 1,  2, 10,  9 }, new int[] { 4, 4,  3,  3 }, // crossbow
                new int[] { 4, 12, 13, 13 }, new int[] { 4, 3, 13, 13 }, // bolt
                new int[] { 0,  0,  0,  0 }, new int[] { 4, 4,  4,  4 }  // shuriken
        };

        for (int i = 0; i < tools.length; i++)
        {
            String locString = String.format("gui.toolstation.%s.desc", tools[i].getToolName().toLowerCase());
            TConstructClientRegistry.addTierTwoButton(icons[i][0], icons[i][1], icons[i][2], coords[i*2], coords[i*2+1], tools[i].getLocalizedToolName(), locString, Reference.RESOURCE, tex);
        }
    }


    /* Manual Stuff */
    public void registerManualIcons ()
    {
        MantleClientRegistry.registerManualIcon("throwingknifeIcon", ToolBuilder.instance.buildTool(new ItemStack(TinkerTools.knifeBlade, 1, TinkerTools.MaterialID.Iron), new ItemStack(TinkerTools.toolRod, 1, 11), null, ""));
        MantleClientRegistry.registerManualIcon("shortbowIcon", ToolBuilder.instance.buildTool(new ItemStack(TinkerWeaponry.partBowLimb, 1, TinkerTools.MaterialID.Wood), new ItemStack(TinkerWeaponry.bowstring, 1, 0), new ItemStack(TinkerWeaponry.partBowLimb, 1, TinkerTools.MaterialID.Wood), ""));
        MantleClientRegistry.registerManualIcon("arrowIcon", ToolBuilder.instance.buildTool(new ItemStack(TinkerWeaponry.arrowhead, 1, TinkerTools.MaterialID.Flint), new ItemStack(Items.stick), new ItemStack(TinkerWeaponry.fletching, 1, 0), ""));

        MantleClientRegistry.registerManualIcon("weaponrybook", new ItemStack(TinkerTools.manualBook, 1, 4));
    }
}
