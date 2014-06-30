package tconstruct.armor;

import mantle.pulsar.pulse.IPulse;
import mantle.pulsar.pulse.Pulse;
import mantle.pulsar.pulse.PulseProxy;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.oredict.ShapedOreRecipe;
import tconstruct.TConstruct;
import tconstruct.armor.blocks.DryingRack;
import tconstruct.armor.items.ArmorBasic;
import tconstruct.armor.items.DiamondApple;
import tconstruct.armor.items.ExoArmor;
import tconstruct.armor.items.HeartCanister;
import tconstruct.armor.items.Jerky;
import tconstruct.armor.items.Knapsack;
import tconstruct.blocks.logic.DryingRackLogic;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.armor.EnumArmorPart;
import tconstruct.library.crafting.DryingRackRecipes;
import tconstruct.library.crafting.LiquidCasting;
import tconstruct.library.crafting.ToolBuilder;
import tconstruct.tools.TinkerTools;
import tconstruct.util.config.PHConstruct;
import tconstruct.world.TinkerWorld;
import tconstruct.world.items.GoldenHead;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(TConstruct.modID)
@Pulse(id = TConstruct.modID)
public class TinkerArmor implements IPulse //TODO: Remove IPulse implementation, keep annotation
{
    @Instance("TinkerArmor")
    public static TinkerArmor instance;
    @PulseProxy(client = "tconstruct.armor.ArmorProxyClient", server = "tconstruct.armor.ArmorProxyCommon")
    public static ArmorProxyCommon proxy;

    public static Item diamondApple;
    public static Item jerky;
    // public static Item stonePattern;
    // public static Item netherPattern;
    public static Block dryingRack;
    // Wearables
    public static Item heavyHelmet;
    public static Item heavyChestplate;
    public static Item heavyPants;
    public static Item heavyBoots;
    public static Item glove;
    public static Item knapsack;
    public static Item heartCanister;
    // Armor - basic
    public static Item helmetWood;
    public static Item chestplateWood;
    public static Item leggingsWood;
    // Armor - exosuit
    public static Item exoGoggles;
    public static Item exoChest;
    public static Item exoPants;
    public static Item exoShoes;
    public static Item bootsWood;
    public static ArmorMaterial materialWood;

    public TinkerArmor()
    {
        MinecraftForge.EVENT_BUS.register(new TinkerArmorEvents());
    }

    @EventHandler
    public void preInit (FMLPreInitializationEvent event)
    {
        TinkerArmor.dryingRack = new DryingRack().setBlockName("Armor.DryingRack");
        GameRegistry.registerBlock(TinkerArmor.dryingRack, "Armor.DryingRack");
        GameRegistry.registerTileEntity(DryingRackLogic.class, "Armor.DryingRack");
        TinkerArmor.diamondApple = new DiamondApple().setUnlocalizedName("tconstruct.apple.diamond");
        GameRegistry.registerItem(TinkerArmor.diamondApple, "diamondApple");
        boolean foodOverhaul = false;
        if (Loader.isModLoaded("HungerOverhaul") || Loader.isModLoaded("fc_food"))
        {
            foodOverhaul = true;
        }

        TinkerArmor.jerky = new Jerky(foodOverhaul).setUnlocalizedName("tconstruct.jerky");
        GameRegistry.registerItem(TinkerArmor.jerky, "jerky");

        // Wearables
        // heavyHelmet = new TArmorBase(PHConstruct.heavyHelmet,
        // 0).setUnlocalizedName("tconstruct.HeavyHelmet");
        TinkerArmor.heartCanister = new HeartCanister().setUnlocalizedName("tconstruct.canister");
        // heavyBoots = new TArmorBase(PHConstruct.heavyBoots,
        // 3).setUnlocalizedName("tconstruct.HeavyBoots");
        // glove = new
        // Glove(PHConstruct.glove).setUnlocalizedName("tconstruct.Glove");
        TinkerArmor.knapsack = new Knapsack().setUnlocalizedName("tconstruct.storage");
        TinkerTools.goldHead = new GoldenHead(4, 1.2F, false).setAlwaysEdible().setPotionEffect(Potion.regeneration.id, 10, 0, 1.0F).setUnlocalizedName("goldenhead");
        // GameRegistry.registerItem(TRepo.heavyHelmet, "heavyHelmet");
        GameRegistry.registerItem(TinkerArmor.heartCanister, "heartCanister");
        // GameRegistry.registerItem(TRepo.heavyBoots, "heavyBoots");
        // GameRegistry.registerItem(TRepo.glove, "glove");
        GameRegistry.registerItem(TinkerArmor.knapsack, "knapsack");

        LiquidCasting basinCasting = TConstruct.getBasinCasting();
        TinkerArmor.materialWood = EnumHelper.addArmorMaterial("WOOD", 2, new int[] { 1, 2, 2, 1 }, 3);
        TinkerArmor.helmetWood = new ArmorBasic(TinkerArmor.materialWood, 0, "wood").setUnlocalizedName("tconstruct.helmetWood");
        TinkerArmor.chestplateWood = new ArmorBasic(TinkerArmor.materialWood, 1, "wood").setUnlocalizedName("tconstruct.chestplateWood");
        TinkerArmor.leggingsWood = new ArmorBasic(TinkerArmor.materialWood, 2, "wood").setUnlocalizedName("tconstruct.leggingsWood");
        TinkerArmor.bootsWood = new ArmorBasic(TinkerArmor.materialWood, 3, "wood").setUnlocalizedName("tconstruct.bootsWood");
        GameRegistry.registerItem(TinkerArmor.helmetWood, "helmetWood");
        GameRegistry.registerItem(TinkerArmor.chestplateWood, "chestplateWood");
        GameRegistry.registerItem(TinkerArmor.leggingsWood, "leggingsWood");
        GameRegistry.registerItem(TinkerArmor.bootsWood, "bootsWood");

        TinkerArmor.exoGoggles = new ExoArmor(EnumArmorPart.HELMET, "exosuit").setUnlocalizedName("tconstruct.exoGoggles");
        TinkerArmor.exoChest = new ExoArmor(EnumArmorPart.CHEST, "exosuit").setUnlocalizedName("tconstruct.exoChest");
        TinkerArmor.exoPants = new ExoArmor(EnumArmorPart.PANTS, "exosuit").setUnlocalizedName("tconstruct.exoPants");
        TinkerArmor.exoShoes = new ExoArmor(EnumArmorPart.SHOES, "exosuit").setUnlocalizedName("tconstruct.exoShoes");

        GameRegistry.registerItem(TinkerArmor.exoGoggles, "helmetExo");
        GameRegistry.registerItem(TinkerArmor.exoChest, "chestplateExo");
        GameRegistry.registerItem(TinkerArmor.exoPants, "leggingsExo");
        GameRegistry.registerItem(TinkerArmor.exoShoes, "bootsExo");

        TConstructRegistry.addItemStackToDirectory("diamondApple", new ItemStack(TinkerArmor.diamondApple, 1, 0));

        TConstructRegistry.addItemStackToDirectory("canisterEmpty", new ItemStack(TinkerArmor.heartCanister, 1, 0));
        TConstructRegistry.addItemStackToDirectory("miniRedHeart", new ItemStack(TinkerArmor.heartCanister, 1, 1));
        TConstructRegistry.addItemStackToDirectory("canisterRedHeart", new ItemStack(TinkerArmor.heartCanister, 1, 2));
    }

    @EventHandler
    public void init (FMLInitializationEvent event)
    {
        craftingTableRecipes();
        addRecipesForDryingRack();
    }

    @EventHandler
    public void postInit (FMLPostInitializationEvent evt)
    {

    }

    private void craftingTableRecipes ()
    {

        // Armor Recipes
        Object[] helm = new String[] { "www", "w w" };
        Object[] chest = new String[] { "w w", "www", "www" };
        Object[] pants = new String[] { "www", "w w", "w w" };
        Object[] shoes = new String[] { "w w", "w w" };
        GameRegistry.addRecipe(new ShapedOreRecipe(TinkerArmor.helmetWood, helm, 'w', "logWood"));
        GameRegistry.addRecipe(new ShapedOreRecipe(TinkerArmor.chestplateWood, chest, 'w', "logWood"));
        GameRegistry.addRecipe(new ShapedOreRecipe(TinkerArmor.leggingsWood, pants, 'w', "logWood"));
        GameRegistry.addRecipe(new ShapedOreRecipe(TinkerArmor.bootsWood, shoes, 'w', "logWood"));

        ItemStack exoGoggleStack = new ItemStack(TinkerArmor.exoGoggles);
        ItemStack exoChestStack = new ItemStack(TinkerArmor.exoChest);
        ItemStack exoPantsStack = new ItemStack(TinkerArmor.exoPants);
        ItemStack exoShoesStack = new ItemStack(TinkerArmor.exoShoes);
        if (PHConstruct.exoCraftingEnabled)
        {
            ToolBuilder.instance.addArmorTag(exoGoggleStack);
            ToolBuilder.instance.addArmorTag(exoChestStack);
            ToolBuilder.instance.addArmorTag(exoPantsStack);
            ToolBuilder.instance.addArmorTag(exoShoesStack);
            GameRegistry.addShapedRecipe(exoGoggleStack, helm, 'w', new ItemStack(TinkerTools.largePlate, 1, 14));
            GameRegistry.addShapedRecipe(exoChestStack, chest, 'w', new ItemStack(TinkerTools.largePlate, 1, 14));
            GameRegistry.addShapedRecipe(exoPantsStack, pants, 'w', new ItemStack(TinkerTools.largePlate, 1, 14));
            GameRegistry.addShapedRecipe(exoShoesStack, shoes, 'w', new ItemStack(TinkerTools.largePlate, 1, 14));
        }

        // Accessories
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TinkerArmor.heartCanister, 1, 0), "##", "##", '#', "ingotAluminum"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TinkerArmor.heartCanister, 1, 0), "##", "##", '#', "ingotAluminium"));
        // GameRegistry.addRecipe(new ShapedOreRecipe(new
        // ItemStack(TRepo.heartCanister, 1, 0), "##", "##", '#',
        // "ingotNaturalAluminum"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TinkerArmor.heartCanister, 1, 0), " # ", "#B#", " # ", '#', "ingotTin", 'B', Items.bone));

        GameRegistry.addRecipe(new ItemStack(TinkerArmor.diamondApple), " d ", "d#d", " d ", 'd', new ItemStack(Items.diamond), '#', new ItemStack(Items.apple));
        GameRegistry.addShapelessRecipe(new ItemStack(TinkerArmor.heartCanister, 1, 2), new ItemStack(TinkerArmor.diamondApple), new ItemStack(TinkerTools.materials, 1, 8), new ItemStack(
                TinkerArmor.heartCanister, 1, 0), new ItemStack(TinkerArmor.heartCanister, 1, 1));
        GameRegistry.addShapelessRecipe(new ItemStack(TinkerArmor.heartCanister, 1, 4), new ItemStack(TinkerArmor.heartCanister, 1, 2), new ItemStack(TinkerArmor.heartCanister, 1, 3), new ItemStack(
                Items.golden_apple, 1, 1));
        //GameRegistry.addShapelessRecipe(new ItemStack(heartCanister, 1, 6), new ItemStack(heartCanister, 1, 0), new ItemStack(heartCanister, 1, 4), new ItemStack(heartCanister, 1, 5));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TinkerArmor.knapsack, 1, 0), "###", "rmr", "###", '#', new ItemStack(Items.leather), 'r', new ItemStack(TinkerTools.toughRod, 1, 2),
                'm', "ingotGold"));
        ItemStack aluBrass = new ItemStack(TinkerTools.materials, 1, 14);
        GameRegistry.addRecipe(new ItemStack(TinkerArmor.knapsack, 1, 0), "###", "rmr", "###", '#', new ItemStack(Items.leather), 'r', new ItemStack(TinkerTools.toughRod, 1, 2), 'm', aluBrass);
        // Drying Rack Recipes
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TinkerArmor.dryingRack, 1, 0), "bbb", 'b', "slabWood"));
    }

    protected static void addRecipesForDryingRack ()
    {
        // Drying rack
        DryingRackRecipes.addDryingRecipe(Items.beef, 20 * 60 * 5, new ItemStack(TinkerArmor.jerky, 1, 0));
        DryingRackRecipes.addDryingRecipe(Items.chicken, 20 * 60 * 5, new ItemStack(TinkerArmor.jerky, 1, 1));
        DryingRackRecipes.addDryingRecipe(Items.porkchop, 20 * 60 * 5, new ItemStack(TinkerArmor.jerky, 1, 2));
        // DryingRackRecipes.addDryingRecipe(Item.muttonRaw, 20 * 60 * 5, new
        // ItemStack(TRepo.jerky, 1, 3));
        DryingRackRecipes.addDryingRecipe(Items.fish, 20 * 60 * 5, new ItemStack(TinkerArmor.jerky, 1, 4));
        DryingRackRecipes.addDryingRecipe(Items.rotten_flesh, 20 * 60 * 5, new ItemStack(TinkerArmor.jerky, 1, 5));
        DryingRackRecipes.addDryingRecipe(new ItemStack(TinkerWorld.strangeFood, 1, 0), 20 * 60 * 5, new ItemStack(TinkerArmor.jerky, 1, 6));
        DryingRackRecipes.addDryingRecipe(new ItemStack(TinkerWorld.strangeFood, 1, 1), 20 * 60 * 5, new ItemStack(TinkerArmor.jerky, 1, 7));

        // DryingRackRecipes.addDryingRecipe(new ItemStack(TRepo.jerky, 1, 5),
        // 20 * 60 * 10, Item.leather);
    }
}
