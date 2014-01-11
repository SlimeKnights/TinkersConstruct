package tconstruct.library;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import tconstruct.library.crafting.Detailing;
import tconstruct.library.crafting.LiquidCasting;
import tconstruct.library.crafting.ToolBuilder;
import tconstruct.library.tools.ArrowMaterial;
import tconstruct.library.tools.BowMaterial;
import tconstruct.library.tools.BowstringMaterial;
import tconstruct.library.tools.CustomMaterial;
import tconstruct.library.tools.FletchingMaterial;
import tconstruct.library.tools.ToolCore;
import tconstruct.library.tools.TToolMaterial;
import tconstruct.library.util.TabTools;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/** A registry to store any relevant API work
 * 
 * @author mDiyo
 */

public class TConstructRegistry
{
    public static TConstructRegistry instance = new TConstructRegistry();

    public static Logger logger = Logger.getLogger("TCon-API");

    /* Creative tabs */
    public static TabTools toolTab;
    public static TabTools materialTab;
    public static TabTools blockTab;

    /* Items */

    /** A directory of crafting items and tools used by the mod.
     * 
     * Tools:
     * pickaxe, shovel, hatchet, broadsword, longsword, rapier, dagger, cutlass
     * frypan, battlesign, mattock, chisel
     * lumberaxe, cleaver, scythe, excavator, hammer, battleaxe
     * 
     * Patterns:
     * blankPattern, woodPattern, metalPattern
     * 
     * Tool crafting parts:
     * toolRod, toolShard, binding, toughBinding, toughRod, heavyPlate
     * pickaxeHead, shovelhead, hatchetHead, swordBlade, wideguard, handGuard, crossbar, knifeBlade,
     * fullGuard, frypanHead, signHead, chiselHead
     * scytheBlade, broadAxeHead, excavatorHead, largeSwordBlade, hammerHead
     * bowstring, fletching, arrowhead
     */
    public static HashMap<String, Item> itemDirectory = new HashMap<String, Item>();

    /** Adds an item to the directory
     * 
     * @param name Associates the name with the stack
     * @param itemstack The stack to add to the directory
     */

    public static void addItemToDirectory (String name, Item itemstack)
    {
        Item add = itemDirectory.get(name);
        if (add != null)
            logger.warning(name + " is already present in the Item directory");

        itemDirectory.put(name, itemstack);
    }

    /** Retrieves an itemstack from the directory
     * 
     * @param name The name of the item to get
     * @return Item associated with the name, or null if not present.
     */

    public static Item getItem (String name)
    {
        Item ret = itemDirectory.get(name);
        if (ret == null)
            logger.warning("Could not find " + name + " in the Item directory");

        return ret;
    }

    /** A directory of ItemStacks. Contains mostly crafting items
     * 
     * Materials:
     * paperStack, greenSlimeCrystal, blueSlimeCrystal, searedBrick, mossBall, lavaCrystal, necroticBone, silkyCloth, silkyJewel
     * ingotCobalt, ingotArdite, ingotManyullyn, ingotCopper, ingotTin, ingotAluminum, rawAluminum,
     * ingotBronze, ingotAluminumBrass, ingotAlumite, ingotSteel, ingotObsidian
     * nuggetIron, nuggetCopper, nuggetTin, nuggetAluminum, nuggetSilver, nuggetAluminumBrass
     * oreberryIron, oreberryGold, oreberryCopper, oreberryTin, oreberryTin, oreberrySilver, 
     * diamondApple, blueSlimeFood, canisterEmpty, miniRedHeart, canisterRedHeart
     * 
     * Patterns - These have a suffix of Pattern or Cast. ex: hatchetHeadPattern
     * ingot, toolRod, pickaxeHead, shovelHead, hatchetHead, swordBlade, wideGuard, handGuard, crossbar, binding, frypanHead, signHead, 
     * knifeBlade, chiselHead, toughRod, toughBinding, largePlate, broadAxeHead, scytheHead, excavatorHead, largeBlade, hammerHead, fullGuard, bowstring
     */
    static HashMap<String, ItemStack> itemstackDirectory = new HashMap<String, ItemStack>();

    /** Adds an itemstack to the directory
     * 
     * @param name Associates the name with the stack
     * @param itemstack The stack to add to the directory
     */

    public static void addItemStackToDirectory (String name, ItemStack itemstack)
    {
        ItemStack add = itemstackDirectory.get(name);
        if (add != null)
            logger.warning(name + " is already present in the ItemStack directory");

        itemstackDirectory.put(name, itemstack);
    }

    /** Retrieves an itemstack from the directory
     * 
     * @param name The name of the item to get
     * @return Item associated with the name, or null if not present.
     */

    public static ItemStack getItemStack (String name)
    {
        ItemStack ret = itemstackDirectory.get(name);
        if (ret == null)
            logger.warning("Could not find " + name + " in the ItemStack directory");

        return ret;
    }

    public static ArrayList<ToolCore> tools = new ArrayList<ToolCore>(20);

    //Parts

    /** List: Item ID, metadata, material ID
     *  ItemStack: Output. Ex: Cactus Binding
     */
    public static HashMap<List, ItemStack> patternPartMapping = new HashMap<List, ItemStack>();

    /** Maps an item and a material ID to an output part
     * 
     * @param woodPattern ID to check against
     * @param patternMeta Metadata to check against
     * @param materialID Material that goes with the item
     * @param output The resulting part
     */
    public static void addPartMapping (Item woodPattern, int patternMeta, int materialID, ItemStack output)
    {
        patternPartMapping.put(Arrays.asList(woodPattern, patternMeta, materialID), output);
    }

    public static ItemStack getPartMapping (Item item, int metadata, int materialID)
    {
        ItemStack stack = patternPartMapping.get(Arrays.asList(item, metadata, materialID));
        if (stack != null)
            return stack.copy();
        return null;
    }

    //Tools

    /** Internal tool mapping, used for adding textures
     * 
     * @param tool
     */

    public static void addToolMapping (ToolCore tool)
    {
        tools.add(tool);
    }

    /** Internal tool mapping, used for adding textures
     * 
     * @return List of tools
     */

    public static ArrayList<ToolCore> getToolMapping ()
    {
        return tools;
    }

    /** Registers a tool to its crafting parts.
     * If an output is registered multiple times the parts are added to the recipe's input list
     * Valid part amounts are 2, 3, and 4.
     * 
     * @see ToolBuidler
     * @param output The ToolCore to craft
     * @param parts Pieces to make the tool with
     */
    public static void addToolRecipe (ToolCore output, Item... parts)
    {
        ToolBuilder tb = ToolBuilder.instance;
        if (parts.length < 2 || parts.length > 4)
            logger.warning("Wrong amount of items to craft into a tool");

        tb.addToolRecipe(output, parts);
    }

    //Materials
    public static HashMap<Integer, TToolMaterial> toolMaterials = new HashMap<Integer, TToolMaterial>(40);
    public static HashMap<String, TToolMaterial> toolMaterialStrings = new HashMap<String, TToolMaterial>(40);

    /** Adds a tool material to the registry
     * 
     * @param materialID Unique ID, stored for each part
     * @exception materialID must be unique
     * @param materialName Unique name for data lookup purposes
     * @param harvestLevel The materials which the tool can harvest. Pickaxe levels - 0: Wood, 1: Stone, 2: Redstone/Diamond, 3: Obsidian, 4: Cobalt/Ardite, 5: Manyullyn
     * @param durability Base durability of the tool, affects tool heads.
     * @param miningspeed Base mining speed, divided by 100 in use
     * @param attack Base attack
     * @param handleModifier Durability multiplier on the tool
     * @param reinforced Reinforced level
     * @param stonebound Amount of Stonebound to put on the tool. Negative numbers are Spiny.
     */

    public static void addToolMaterial (int materialID, String materialName, int harvestLevel, int durability, int miningspeed, int attack, float handleModifier, int reinforced, float stonebound,
            String style, String ability)
    {
        TToolMaterial mat = toolMaterials.get(materialID);
        if (mat == null)
        {
            mat = new TToolMaterial(materialName, harvestLevel, durability, miningspeed, attack, handleModifier, reinforced, stonebound, style, ability);
            toolMaterials.put(materialID, mat);
            toolMaterialStrings.put(materialName, mat);
        }
        else
            throw new IllegalArgumentException("[TCon API] Material ID " + materialID + " is already occupied by " + mat.materialName);
    }

    /** Adds a tool material to the registry
     * 
     * @param materialID Unique ID, stored for each part
     * @exception materialID must be unique
     * @param materialName Unique name for data lookup purposes
     * @param displayName Prefix for creative mode tools
     * @param harvestLevel The materials which the tool can harvest. Pickaxe levels - 0: Wood, 1: Stone, 2: Redstone/Diamond, 3: Obsidian, 4: Cobalt/Ardite, 5: Manyullyn
     * @param durability Base durability of the tool, affects tool heads.
     * @param miningspeed Base mining speed, divided by 100 in use
     * @param attack Base attack
     * @param handleModifier Durability multiplier on the tool
     * @param reinforced Reinforced level
     * @param stonebound Amount of Stonebound to put on the tool. Negative numbers are Spiny.
     */

    public static void addToolMaterial (int materialID, String materialName, String displayName, int harvestLevel, int durability, int miningspeed, int attack, float handleModifier, int reinforced,
            float stonebound, String style, String ability)
    {
        TToolMaterial mat = toolMaterials.get(materialID);
        if (mat == null)
        {
            mat = new TToolMaterial(materialName, displayName, harvestLevel, durability, miningspeed, attack, handleModifier, reinforced, stonebound, style, ability);
            toolMaterials.put(materialID, mat);
            toolMaterialStrings.put(materialName, mat);
        }
        else
            throw new IllegalArgumentException("[TCon API] Material ID " + materialID + " is already occupied by " + mat.materialName);
    }

    /** Adds a tool material to the registry
     * 
     * @param materialID Unique ID, stored for each part
     * @exception materialID must be unique
     * @param material Complete tool material to add. Uses the name in the material for lookup purposes.
     */

    public static void addtoolMaterial (int materialID, TToolMaterial material)
    {
        TToolMaterial mat = toolMaterials.get(materialID);
        if (mat == null)
        {
            toolMaterials.put(materialID, mat);
            toolMaterialStrings.put(material.name(), mat);
        }
        else
            throw new IllegalArgumentException("[TCon API] Material ID " + materialID + " is already occupied by " + mat.materialName);
    }

    /** Looks up a tool material by ID
     * 
     * @param key The ID to look up
     * @return Tool Material
     */

    public static TToolMaterial getMaterial (int key)
    {
        return (toolMaterials.get(key));
    }

    /** Looks up a tool material by name
     * 
     * @param key the name to look up
     * @return Tool Material
     */

    public static TToolMaterial getMaterial (String key)
    {
        return (toolMaterialStrings.get(key));
    }

    //Bow materials
    public static HashMap<Integer, BowMaterial> bowMaterials = new HashMap<Integer, BowMaterial>(40);

    public static void addBowMaterial (int materialID, int durability, int drawSpeed, float speedMax)
    {
        BowMaterial mat = bowMaterials.get(materialID);
        if (mat == null)
        {
            mat = new BowMaterial(durability, drawSpeed, speedMax);
            bowMaterials.put(materialID, mat);
        }
        else
            throw new IllegalArgumentException("[TCon API] Bow Material ID " + materialID + " is already occupied");
    }

    public static boolean validBowMaterial (int materialID)
    {
        return bowMaterials.containsKey(materialID);
    }

    public static BowMaterial getBowMaterial (int materialID)
    {
        return bowMaterials.get(materialID);
    }

    public static HashMap<Integer, ArrowMaterial> arrowMaterials = new HashMap<Integer, ArrowMaterial>(40);

    public static void addArrowMaterial (int materialID, float mass, float breakChance, float accuracy)
    {
        ArrowMaterial mat = arrowMaterials.get(materialID);
        if (mat == null)
        {
            mat = new ArrowMaterial(mass, breakChance, accuracy);
            arrowMaterials.put(materialID, mat);
        }
        else
            throw new IllegalArgumentException("[TCon API] Arrow Material ID " + materialID + " is already occupied");
    }

    public static boolean validArrowMaterial (int materialID)
    {
        return arrowMaterials.containsKey(materialID);
    }

    public static ArrowMaterial getArrowMaterial (int materialID)
    {
        return arrowMaterials.get(materialID);
    }

    //Custom materials - bowstrings, fletching, etc
    public static ArrayList<CustomMaterial> customMaterials = new ArrayList<CustomMaterial>();

    public static void addCustomMaterial (CustomMaterial mat)
    {
        if (mat != null)
            customMaterials.add(mat);
    }

    public static void addBowstringMaterial (int materialID, int value, ItemStack input, ItemStack craftingMaterial, float durability, float drawSpeed, float flightSpeed)
    {
        BowstringMaterial mat = new BowstringMaterial(materialID, value, input, craftingMaterial, durability, drawSpeed, flightSpeed);
        customMaterials.add(mat);
    }

    public static void addFletchingMaterial (int materialID, int value, ItemStack input, ItemStack craftingMaterial, float accuracy, float breakChance, float mass)
    {
        FletchingMaterial mat = new FletchingMaterial(materialID, value, input, craftingMaterial, accuracy, breakChance, mass);
        customMaterials.add(mat);
    }

    public static CustomMaterial getCustomMaterial (int materialID, Class<? extends CustomMaterial> clazz)
    {
        for (CustomMaterial mat : customMaterials)
        {
            if (mat.getClass().equals(clazz) && mat.materialID == materialID)
                return mat;
        }
        return null;
    }

    public static CustomMaterial getCustomMaterial (ItemStack input, Class<? extends CustomMaterial> clazz)
    {
        for (CustomMaterial mat : customMaterials)
        {
            if (mat.getClass().equals(clazz) && input.isItemEqual(mat.input))
                return mat;
        }
        return null;
    }

    /*public static CustomMaterial getCustomMaterial(ItemStack input, ItemStack pattern)
    {
        for (CustomMaterial mat : customMaterials)
        {
            if (mat.matches(input, pattern))
                return mat;
        }
        return null;
    }*/

    /*public static ItemStack craftBowString(ItemStack stack)
    {
        if (stack.stackSize < 3)
            return null;
        
        for (BowstringMaterial mat : bowstringMaterials)
        {
            if (stack.isItemEqual(mat.input))
                return mat.craftingItem.copy();
        }
        return null;
    }
    
    public static BowstringMaterial getBowstringMaterial(ItemStack stack)
    {
        if (stack.stackSize < 3)
            return null;
        
        for (BowstringMaterial mat : bowstringMaterials)
        {
            if (stack.isItemEqual(mat.input))
                return mat;
        }
        return null;
    }*/

    public static LiquidCasting getTableCasting ()
    {
        return instance.tableCasting();
    }

    LiquidCasting tableCasting ()
    {
        try
        {
            Class clazz = Class.forName("tconstruct.TConstruct");
            Method method = clazz.getMethod("getTableCasting");
            LiquidCasting lc = (LiquidCasting) method.invoke(this);
            return lc;
        }
        catch (Exception e)
        {
            logger.warning("Could not find casting table recipes.");
            return null;
        }
    }

    public static LiquidCasting getBasinCasting ()
    {
        return instance.basinCasting();
    }

    LiquidCasting basinCasting ()
    {
        try
        {
            Class clazz = Class.forName("tconstruct.TConstruct");
            Method method = clazz.getMethod("getBasinCasting");
            LiquidCasting lc = (LiquidCasting) method.invoke(this);
            return lc;
        }
        catch (Exception e)
        {
            logger.warning("Could not find casting basin recipes.");
            return null;
        }
    }

    public static Detailing getChiselDetailing ()
    {
        return instance.chiselDetailing();
    }

    Detailing chiselDetailing ()
    {
        try
        {
            Class clazz = Class.forName("tconstruct.TConstruct");
            Method method = clazz.getMethod("getChiselDetailing");
            Detailing lc = (Detailing) method.invoke(this);
            return lc;
        }
        catch (Exception e)
        {
            logger.warning("Could not find chisel detailing recipes.");
            return null;
        }
    }

    public static ArrayList<ActiveToolMod> activeModifiers = new ArrayList<ActiveToolMod>();

    public static void registerActiveToolMod (ActiveToolMod mod)
    {
        activeModifiers.add(mod);
    }

    /* Used to determine how blocks are laid out in the drawbridge
     * 0: Metadata has to match
     * 1: Metadata has no meaning
     * 2: Should not be placed
     * 3: Has rotational metadata
     * 4: Rails
     * 5: Has rotational TileEntity data
     * 6: Custom placement logic
     */
    public static Block[] drawbridgeState = new Block[Integer.MAX_VALUE];
    /** Blocks that are interchangable with each other. Ex: Still and flowing water */
    public static ItemStack[] interchangableBlockMapping = new ItemStack[Integer.MAX_VALUE];
    /** Blocks that place items, and vice versa */
    public static Item[] blockToItemMapping = new Item[Integer.MAX_VALUE];

    static void initializeDrawbridgeState ()
    {
        //TODO fix this mess and move to TMech
       /* drawbridgeState[Blocks.stone] = 1;
        drawbridgeState[Blocks.grass] = 1;
        drawbridgeState[Blocks.dirt] = 1;
        drawbridgeState[Blocks.cobblestone] = 1;
        drawbridgeState[Blocks.bedrock] = 2;
        drawbridgeState[Blocks.water] = 1;
        drawbridgeState[Blocks.lava] = 1;
        drawbridgeState[Blocks.sand] = 1;
        drawbridgeState[Blocks.gravel] = 1;
        drawbridgeState[Blocks.gold_ore] = 1;
        drawbridgeState[Blocks.iron_ore] = 1;
        drawbridgeState[Blocks.coal_ore] = 1;
        drawbridgeState[Blocks.sponge] = 1;
        drawbridgeState[Blocks.lapis_ore] = 1;
        drawbridgeState[Blocks.lapis_block] = 1;
        drawbridgeState[Blocks.dispenser] = 3;
        drawbridgeState[Blocks.music] = 1;
        drawbridgeState[Blocks.bed] = 2;
        drawbridgeState[Blocks.railPowered] = 4;
        drawbridgeState[Blocks.railDetector] = 4;
        drawbridgeState[Blocks.sticky_piston] = 3;
        drawbridgeState[Blocks.web] = 1;
        drawbridgeState[Blocks.piston] = 3;
        drawbridgeState[Blocks.piston_extension] = 2;
        drawbridgeState[Blocks.plantYellow] = 1;
        drawbridgeState[Blocks.plantRed] = 1;
        drawbridgeState[Blocks.mushroomBrown] = 1;
        drawbridgeState[Blocks.mushroomRed] = 1;
        drawbridgeState[Blocks.gold_block] = 1;
        drawbridgeState[Blocks.iron_block] = 1;
        drawbridgeState[Blocks.brick_block] = 1;
        drawbridgeState[Blocks.tnt] = 1;
        drawbridgeState[Blocks.bookshelf] = 1;
        drawbridgeState[Blocks.mossy_cobblestone] = 1;
        drawbridgeState[Blocks.obsidian] = 1;
        drawbridgeState[Blocks.torchWood] = 1;
        drawbridgeState[Blocks.fire] = 1;
        drawbridgeState[Blocks.mobSpawner] = 2;
        drawbridgeState[Blocks.stairsWoodOak] = 3;
        drawbridgeState[Blocks.chest] = 5;
        drawbridgeState[Blocks.redstoneWire] = 1;
        blockToItemMapping[Blocks.redstoneWire] = Items.redstone;
        blockToItemMapping[Items.redstone] = Blocks.redstoneWire;
        drawbridgeState[Blocks.diamond_ore] = 1;
        drawbridgeState[Blocks.diamond_block] = 1;
        drawbridgeState[Blocks.crafting_table] = 1;
        drawbridgeState[Blocks.crops] = 2;
        drawbridgeState[Blocks.tilledField] = 1;
        drawbridgeState[Blocks.furnaceIdle] = 3;
        drawbridgeState[Blocks.furnaceBurning] = 3;
        interchangableBlockMapping[Blocks.furnaceIdle] = Blocks.furnaceBurning;
        interchangableBlockMapping[Blocks.furnaceBurning] = Blocks.furnaceIdle;
        drawbridgeState[Blocks.tilledField] = 1;
        drawbridgeState[Blocks.signPost] = 3;
        drawbridgeState[Blocks.doorWood] = 2;
        drawbridgeState[Blocks.ladder] = 1;
        drawbridgeState[Blocks.rail] = 4;
        drawbridgeState[Blocks.stairsCobblestone] = 3;
        drawbridgeState[Blocks.signWall] = 3;
        drawbridgeState[Blocks.lever] = 3;
        drawbridgeState[Blocks.pressurePlateStone] = 1;
        drawbridgeState[Blocks.doorIron] = 2;
        drawbridgeState[Blocks.pressurePlatePlanks] = 1;
        drawbridgeState[Blocks.oreRedstone] = 1;
        drawbridgeState[Blocks.oreRedstoneGlowing] = 1;
        drawbridgeState[Blocks.torchRedstoneIdle] = 1;
        drawbridgeState[Blocks.torchRedstoneActive] = 1;
        drawbridgeState[Blocks.stoneButton] = 3;
        drawbridgeState[Blocks.snow] = 1;
        drawbridgeState[Blocks.ice] = 1;
        drawbridgeState[Blocks.blockSnow] = 1;
        drawbridgeState[Blocks.cactus] = 2;
        drawbridgeState[Blocks.blockClay] = 1;
        drawbridgeState[Blocks.reed] = 1;
        drawbridgeState[Blocks.jukebox] = 1;
        drawbridgeState[Blocks.fence] = 1;
        drawbridgeState[Blocks.pumpkin] = 1;
        drawbridgeState[Blocks.netherrack] = 1;
        drawbridgeState[Blocks.slowSand] = 1;
        drawbridgeState[Blocks.glowStone] = 1;
        drawbridgeState[Blocks.portal] = 2;
        drawbridgeState[Blocks.pumpkinLantern] = 1;
        drawbridgeState[Blocks.cake] = 2;
        drawbridgeState[Blocks.redstoneRepeaterIdle] = 3;
        drawbridgeState[Blocks.redstoneRepeaterActive] = 3;
        interchangableBlockMapping[Blocks.redstoneRepeaterIdle] = Blocks.redstoneRepeaterActive;
        interchangableBlockMapping[Blocks.redstoneRepeaterActive] = Blocks.redstoneRepeaterIdle;
        blockToItemMapping[Blocks.redstoneRepeaterIdle] = Items.redstoneRepeater.itemID;
        blockToItemMapping[Blocks.redstoneRepeaterActive] = Items.redstoneRepeater.itemID;
        blockToItemMapping[Items.redstoneRepeater] = Blocks.redstoneRepeaterIdle;
        drawbridgeState[Blocks.lockedChest] = 5;
        drawbridgeState[Blocks.trapdoor] = 3;
        drawbridgeState[Blocks.mushroomCapBrown] = 1;
        drawbridgeState[Blocks.mushroomCapRed] = 1;
        drawbridgeState[Blocks.fenceIron] = 1;
        drawbridgeState[Blocks.thinGlass] = 1;
        drawbridgeState[Blocks.melon] = 1;
        drawbridgeState[Blocks.pumpkinStem] = 2;
        drawbridgeState[Blocks.melonStem] = 2;
        drawbridgeState[Blocks.vine] = 3;
        drawbridgeState[Blocks.fenceGate] = 3;
        drawbridgeState[Blocks.stairsBrick] = 3;
        drawbridgeState[Blocks.stairsStoneBrick] = 3;
        drawbridgeState[Blocks.mycelium] = 1;
        drawbridgeState[Blocks.waterlily] = 1;
        drawbridgeState[Blocks.netherBrick] = 1;
        drawbridgeState[Blocks.netherFence] = 1;
        drawbridgeState[Blocks.netherFence] = 3;
        drawbridgeState[Blocks.netherStalk] = 2;
        drawbridgeState[Blocks.enchantmentTable] = 1;
        drawbridgeState[Blocks.brewingStand] = 1;
        drawbridgeState[Blocks.cauldron] = 1;
        drawbridgeState[Blocks.endPortal] = 2;
        drawbridgeState[Blocks.dragonEgg] = 1;
        drawbridgeState[Blocks.redstoneLampIdle] = 1;
        drawbridgeState[Blocks.redstoneLampActive] = 1;
        drawbridgeState[Blocks.cocoaPlant] = 2;
        drawbridgeState[Blocks.stairsSandStone] = 3;
        drawbridgeState[Blocks.oreEmerald] = 1;
        drawbridgeState[Blocks.enderChest] = 5;
        drawbridgeState[Blocks.tripWireSource] = 1;
        drawbridgeState[Blocks.tripWire] = 1;
        drawbridgeState[Blocks.blockEmerald] = 1;
        drawbridgeState[Blocks.stairsWoodSpruce] = 3;
        drawbridgeState[Blocks.stairsWoodBirch] = 3;
        drawbridgeState[Blocks.stairsWoodJungle] = 3;
        drawbridgeState[Blocks.commandBlock] = 1;
        drawbridgeState[Blocks.beacon] = 1;
        drawbridgeState[Blocks.cobblestoneWall] = 1;
        drawbridgeState[Blocks.flowerPot] = 1;
        drawbridgeState[Blocks.carrot] = 2;
        drawbridgeState[Blocks.potato] = 1;
        drawbridgeState[Blocks.woodenButton] = 3;
        drawbridgeState[Blocks.skull] = 2;
        drawbridgeState[Blocks.chestTrapped] = 5;
        drawbridgeState[Blocks.pressurePlateGold] = 1;
        drawbridgeState[Blocks.pressurePlateIron] = 1;
        drawbridgeState[Blocks.redstoneComparatorIdle] = 1;
        drawbridgeState[Blocks.redstoneComparatorActive] = 1;
        interchangableBlockMapping[Blocks.redstoneComparatorIdle] = Blocks.redstoneComparatorActive;
        interchangableBlockMapping[Blocks.redstoneComparatorActive] = Blocks.redstoneComparatorIdle;
        blockToItemMapping[Blocks.redstoneComparatorIdle] = Items.comparator.itemID;
        blockToItemMapping[Blocks.redstoneComparatorActive] = Items.comparator.itemID;
        blockToItemMapping[Items.comparator] = Blocks.redstoneComparatorIdle;
        drawbridgeState[Blocks.daylightSensor] = 1;
        drawbridgeState[Blocks.blockRedstone] = 1;
        drawbridgeState[Blocks.oreNetherQuartz] = 1;
        drawbridgeState[Blocks.hopperBlock] = 3;
        drawbridgeState[Blocks.blockNetherQuartz] = 1;
        drawbridgeState[Blocks.stairsNetherQuartz] = 3;
        drawbridgeState[Blocks.railActivator] = 4;
        drawbridgeState[Blocks.dropper] = 3;
        interchangableBlockMapping[Blocks.dirt] = Blocks.grass;
        interchangableBlockMapping[Blocks.grass] = Blocks.dirt;
    */
    }

    static
    {
        initializeDrawbridgeState();
    }

    /** Default Material Index
     * 0:  Wood
     * 1:  Stone
     * 2:  Iron
     * 3:  Flint
     * 4:  Cactus
     * 5:  Bone
     * 6:  Obsidian
     * 7:  Netherrack
     * 8:  Green Slime
     * 9:  Paper
     * 10: Cobalt
     * 11: Ardite
     * 12: Manyullyn
     * 13: Copper
     * 14: Bronze
     * 15: Alumite
     * 16: Steel
     * 17: Blue Slime
     */
}
