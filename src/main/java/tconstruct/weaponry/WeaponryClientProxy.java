package tconstruct.weaponry;

import tconstruct.client.AmmoItemRenderer;
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
        int base = StencilBuilder.getIndex(new ItemStack(TinkerWeaponry.woodPattern, 0, 0));
        TConstructClientRegistry.addStencilButton2(0, 4, base + 0, Reference.RESOURCE, "textures/gui/icons.png"); // shuriken
        TConstructClientRegistry.addStencilButton2(1, 4, base + 1, Reference.RESOURCE, "textures/gui/icons.png"); // crossbow limb
        TConstructClientRegistry.addStencilButton2(2, 4, base + 2, Reference.RESOURCE, "textures/gui/icons.png"); // crossbow body
        TConstructClientRegistry.addStencilButton2(3, 4, base + 3, Reference.RESOURCE, "textures/gui/icons.png"); // bow limb
        //TConstructClientRegistry.addStencilButton2(4, 4, index, Reference.RESOURCE, "textures/gui/icons.png"); // bolt
    }
}
