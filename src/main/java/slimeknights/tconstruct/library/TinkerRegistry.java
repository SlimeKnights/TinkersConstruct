package slimeknights.tconstruct.library;

import com.google.common.base.CharMatcher;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import gnu.trove.set.hash.TLinkedHashSet;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.oredict.OreDictionary;

import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import slimeknights.mantle.client.CreativeTab;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.events.MaterialEvent;
import slimeknights.tconstruct.library.events.TinkerRegisterEvent;
import slimeknights.tconstruct.library.materials.IMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialTypes;
import slimeknights.tconstruct.library.materials.ProjectileMaterialStats;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.smeltery.AlloyRecipe;
import slimeknights.tconstruct.library.smeltery.CastingRecipe;
import slimeknights.tconstruct.library.smeltery.ICastingRecipe;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.IPattern;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.library.tools.Shard;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.traits.ITrait;

public final class TinkerRegistry {

  // the logger for the library
  public static final Logger log = Util.getLogger("API");

  private TinkerRegistry() {
  }

  /*---------------------------------------------------------------------------
  | CREATIVE TABS                                                             |
  ---------------------------------------------------------------------------*/
  public static CreativeTab tabGeneral = new CreativeTab("TinkerGeneral", new ItemStack(Items.SLIME_BALL));
  public static CreativeTab tabTools = new CreativeTab("TinkerTools", new ItemStack(Items.IRON_PICKAXE));
  public static CreativeTab tabParts = new CreativeTab("TinkerToolParts", new ItemStack(Items.STICK));
  public static CreativeTab tabSmeltery = new CreativeTab("TinkerSmeltery", new ItemStack(Item.getItemFromBlock(Blocks.STONEBRICK)));
  public static CreativeTab tabWorld = new CreativeTab("TinkerWorld", new ItemStack(Item.getItemFromBlock(Blocks.SLIME_BLOCK)));
  public static CreativeTab tabGadgets = new CreativeTab("TinkerGadgets", new ItemStack(Blocks.TNT));

  /*---------------------------------------------------------------------------
  | MATERIALS                                                                 |
  ---------------------------------------------------------------------------*/

  // Identifier to Material mapping. Hashmap so we can look it up directly without iterating
  private static final Map<String, Material> materials = Maps.newLinkedHashMap();
  private static final Map<String, ITrait> traits = new THashMap<>();
  // traceability information who registered what. Used to find errors.
  private static final Map<String, ModContainer> materialRegisteredByMod = new THashMap<>();
  private static final Map<String, Map<String, ModContainer>> statRegisteredByMod = new THashMap<>();
  private static final Map<String, Map<String, ModContainer>> traitRegisteredByMod = new THashMap<>();

  // contains all cancelled materials, allows us to eat calls regarding the material silently
  private static final Set<String> cancelledMaterials = new THashSet<>();

  public static void addMaterial(Material material, IMaterialStats stats, ITrait trait) {
    addMaterial(material, stats);
    addMaterialTrait(material.identifier, trait, null);
  }

  public static void addMaterial(Material material, ITrait trait) {
    addMaterial(material);
    addMaterialTrait(material.identifier, trait, null);
  }

  public static void addMaterial(Material material, IMaterialStats stats) {
    addMaterial(material);
    addMaterialStats(material.identifier, stats);
  }

  /**
   * Registers a material. The materials identifier has to be lowercase and not contain any spaces.
   * Identifiers have to be globally unique!
   */
  public static void addMaterial(Material material) {
    // ensure material identifiers are safe
    if(CharMatcher.whitespace().matchesAnyOf(material.getIdentifier())) {
      error("Could not register Material \"%s\": Material identifier must not contain any spaces.", material.identifier);
      return;
    }
    if(CharMatcher.javaUpperCase().matchesAnyOf(material.getIdentifier())) {
      error("Could not register Material \"%s\": Material identifier must be completely lowercase.", material.identifier);
      return;
    }

    // duplicate material
    if(materials.containsKey(material.identifier)) {
      ModContainer registeredBy = materialRegisteredByMod.get(material.identifier);
      error(String.format(
          "Could not register Material \"%s\": It was already registered by %s",
          material.identifier,
          registeredBy.getName()));
      return;
    }

    MaterialEvent.MaterialRegisterEvent event = new MaterialEvent.MaterialRegisterEvent(material);

    if(MinecraftForge.EVENT_BUS.post(event)) {
      // event cancelled
      log.trace("Addition of material {} cancelled by event", material.getIdentifier());
      cancelledMaterials.add(material.getIdentifier());
      return;
    }

    // register material
    materials.put(material.identifier, material);
    putMaterialTrace(material.identifier);
  }

  public static Material getMaterial(String identifier) {
    return materials.containsKey(identifier) ? materials.get(identifier) : Material.UNKNOWN;
  }

  public static Collection<Material> getAllMaterials() {
    return ImmutableList.copyOf(materials.values());
  }

  /**
   * Called by TinkerIntegrtion at the end of postInit to remove any materials that are still hidden (unused)
   * For internal use, should not need to be called by other mods
   */
  public static void removeHiddenMaterials() {
    materials.entrySet().removeIf(entry->entry.getValue().isHidden());
  }

  public static Collection<Material> getAllMaterialsWithStats(String statType) {
    ImmutableList.Builder<Material> mats = ImmutableList.builder();
    for(Material material : materials.values()) {
      if(material.hasStats(statType)) {
        mats.add(material);
      }
    }

    return mats.build();
  }

  /*---------------------------------------------------------------------------
  | TRAITS & STATS                                                            |
  ---------------------------------------------------------------------------*/

  public static void addTrait(ITrait trait) {
    // Trait might already have been registered since modifiers and materials share traits
    if(traits.containsKey(trait.getIdentifier())) {
      return;
    }

    traits.put(trait.getIdentifier(), trait);

    ModContainer activeMod = Loader.instance().activeModContainer();
    putTraitTrace(trait.getIdentifier(), trait, activeMod);
  }

  public static void addMaterialStats(String materialIdentifier, IMaterialStats stats) {
    if(cancelledMaterials.contains(materialIdentifier)) {
      return;
    }
    if(!materials.containsKey(materialIdentifier)) {
      error(String.format("Could not add Stats \"%s\" to \"%s\": Unknown Material", stats.getIdentifier(),
                          materialIdentifier));
      return;
    }

    Material material = materials.get(materialIdentifier);
    addMaterialStats(material, stats);
  }

  public static void addMaterialStats(Material material, IMaterialStats stats, IMaterialStats... stats2) {
    addMaterialStats(material, stats);
    for(IMaterialStats stat : stats2) {
      addMaterialStats(material, stat);
    }
  }

  public static void addMaterialStats(Material material, IMaterialStats stats) {
    if(material == null) {
      error(String.format("Could not add Stats \"%s\": Material is null", stats.getIdentifier()));
      return;
    }
    if(cancelledMaterials.contains(material.identifier)) {
      return;
    }

    String identifier = material.identifier;
    // duplicate stats
    if(material.getStats(stats.getIdentifier()) != null) {
      String registeredBy = "Unknown";
      Map<String, ModContainer> matReg = statRegisteredByMod.get(identifier);
      if(matReg != null) {
        registeredBy = matReg.get(stats.getIdentifier()).getName();
      }

      error(String.format(
          "Could not add Stats to \"%s\": Stats of type \"%s\" were already registered by %s. Use the events to modify stats.",
          identifier, stats.getIdentifier(), registeredBy));
      return;
    }

    // ensure there are default stats present
    if(Material.UNKNOWN.getStats(stats.getIdentifier()) == null) {
      error("Could not add Stat of type \"%s\": Default Material does not have default stats for said type. Please add default-values to the default material \"unknown\" first.", stats
          .getIdentifier());
      return;
    }

    MaterialEvent.StatRegisterEvent<?> event = new MaterialEvent.StatRegisterEvent<>(material, stats);
    MinecraftForge.EVENT_BUS.post(event);

    // overridden stats from event
    if(event.getResult() == Event.Result.ALLOW) {
      stats = event.newStats;
    }

    material.addStats(stats);

    ModContainer activeMod = Loader.instance().activeModContainer();
    putStatTrace(identifier, stats, activeMod);

    if(Objects.equals(stats.getIdentifier(), MaterialTypes.HEAD) && !material.hasStats(MaterialTypes.PROJECTILE)) {
      addMaterialStats(material, new ProjectileMaterialStats());
    }
  }

  public static void addMaterialTrait(String materialIdentifier, ITrait trait, String stats) {
    if(cancelledMaterials.contains(materialIdentifier)) {
      return;
    }
    if(!materials.containsKey(materialIdentifier)) {
      error(String.format("Could not add Trait \"%s\" to \"%s\": Unknown Material",
                          trait.getIdentifier(), materialIdentifier));
      return;
    }

    Material material = materials.get(materialIdentifier);
    addMaterialTrait(material, trait, stats);
  }

  public static void addMaterialTrait(Material material, ITrait trait, String stats) {
    if(checkMaterialTrait(material, trait, stats)) {
      material.addTrait(trait);
    }
  }

  /**
   * Call before adding a trait to a material. Checks consistency and takes care everything is in a consistent state.
   * Registers the trait if it's not registered, takes events into account.
   */
  public static boolean checkMaterialTrait(Material material, ITrait trait, String stats) {
    if(material == null) {
      error(String.format("Could not add Trait \"%s\": Material is null", trait.getIdentifier()));
      return false;
    }
    if(cancelledMaterials.contains(material.identifier)) {
      return false;
    }

    String identifier = material.identifier;
    // duplicate traits
    if(material.hasTrait(trait.getIdentifier(), stats)) {
      String registeredBy = "Unknown";
      Map<String, ModContainer> matReg = traitRegisteredByMod.get(identifier);
      if(matReg != null) {
        registeredBy = matReg.get(trait.getIdentifier()).getName();
      }

      error(String.format(
          "Could not add Trait to \"%s\": Trait \"%s\" was already registered by %s",
          identifier, trait.getIdentifier(), registeredBy));
      return false;
    }

    MaterialEvent.TraitRegisterEvent<?> event = new MaterialEvent.TraitRegisterEvent<>(material, trait);
    if(MinecraftForge.EVENT_BUS.post(event)) {
      // cancelled
      log.trace("Trait {} on {} cancelled by event", trait.getIdentifier(), material.getIdentifier());
      return false;
    }

    addTrait(trait);

    return true;
  }

  public static ITrait getTrait(String identifier) {
    return traits.get(identifier);
  }

  /*---------------------------------------------------------------------------
  | TOOLS & WEAPONS & Crafting                                                |
  ---------------------------------------------------------------------------*/

  /** This set contains all known tools */
  private static final Set<ToolCore> tools = new TLinkedHashSet<>();
  private static final Set<IToolPart> toolParts = new TLinkedHashSet<>();
  private static final Set<ToolCore> toolStationCrafting = Sets.newLinkedHashSet();
  private static final Set<ToolCore> toolForgeCrafting = Sets.newLinkedHashSet();
  private static final List<ItemStack> stencilTableCrafting = Lists.newLinkedList();
  private static final Set<Item> patternItems = Sets.newHashSet();
  private static final Set<Item> castItems = Sets.newHashSet();
  private static Shard shardItem;

  /**
   * Register a tool, making it known to tinkers' systems.
   * All toolparts used to craft the tool will be registered as well.
   */
  public static void registerTool(ToolCore tool) {
    tools.add(tool);

    for(PartMaterialType pmt : tool.getRequiredComponents()) {
      for(IToolPart tp : pmt.getPossibleParts()) {
        registerToolPart(tp);
      }
    }
  }

  public static Set<ToolCore> getTools() {
    return ImmutableSet.copyOf(tools);
  }

  /**
   * Used for the sharpening kit. Allows to register a toolpart that is not part of a tool.
   */
  public static void registerToolPart(IToolPart part) {
    toolParts.add(part);
    if(part instanceof Item) {
      if(part.canBeCrafted()) {
        addPatternForItem((Item) part);
      }
      if(part.canBeCasted()) {
        addCastForItem((Item) part);
      }
    }
  }

  public static Set<IToolPart> getToolParts() {
    return ImmutableSet.copyOf(toolParts);
  }

  /** Adds a tool to the Crafting UI of both the Tool Station as well as the Tool Forge */
  public static void registerToolCrafting(ToolCore tool) {
    registerToolStationCrafting(tool);
    registerToolForgeCrafting(tool);
  }

  /** Adds a tool to the Crafting UI of the Tool Station */
  public static void registerToolStationCrafting(ToolCore tool) {
    toolStationCrafting.add(tool);
  }

  public static Set<ToolCore> getToolStationCrafting() {
    return ImmutableSet.copyOf(toolStationCrafting);
  }

  /** Adds a tool to the Crafting UI of the Tool Forge */
  public static void registerToolForgeCrafting(ToolCore tool) {
    toolForgeCrafting.add(tool);
  }

  public static Set<ToolCore> getToolForgeCrafting() {
    return ImmutableSet.copyOf(toolForgeCrafting);
  }

  /** Adds a new pattern to craft to the stenciltable. NBT sensitive. Has to be a Pattern. */
  public static void registerStencilTableCrafting(ItemStack stencil) {
    if(!(stencil.getItem() instanceof IPattern)) {
      error(String.format(
          "Stencil Table Crafting has to be a pattern (%s)", stencil.toString()));
      return;
    }
    stencilTableCrafting.add(stencil);
  }

  public static List<ItemStack> getStencilTableCrafting() {
    return ImmutableList.copyOf(stencilTableCrafting);
  }

  public static void setShardItem(Shard shard) {
    if(shard == null) {
      return;
    }
    shardItem = shard;
  }

  public static Shard getShard() {
    return shardItem;
  }

  public static ItemStack getShard(Material material) {
    ItemStack out = material.getShard();
    if(out.isEmpty()) {
      out = shardItem.getItemstackWithMaterial(material);
    }
    return out;
  }

  /** Registers a pattern for the given item */
  public static void addPatternForItem(Item item) {
    patternItems.add(item);
  }

  /** Registers a cast for the given item */
  public static void addCastForItem(Item item) {
    castItems.add(item);
  }

  /** All items that have a pattern */
  public static Collection<Item> getPatternItems() {
    return ImmutableList.copyOf(patternItems);
  }

  /** All items that have a cast */
  public static Collection<Item> getCastItems() {
    return ImmutableList.copyOf(castItems);
  }

  /*---------------------------------------------------------------------------
  | Modifiers                                                                 |
  ---------------------------------------------------------------------------*/
  private static final Map<String, IModifier> modifiers = new THashMap<>();
  private static final Map<Class<? extends EntityLivingBase>, Function<EntityLivingBase,ItemStack>> headDrops = new THashMap<>();

  public static void registerModifier(IModifier modifier) {
    registerModifierAlias(modifier, modifier.getIdentifier());
  }

  /** Registers an alternate name for a modifier. This is used for multi-level modifiers/traits where multiple exist, but one specific is needed for access */
  public static void registerModifierAlias(IModifier modifier, String alias) {
    if(modifiers.containsKey(alias)) {
      throw new TinkerAPIException("Trying to register a modifier with the name " + alias + " but it already is registered");
    }
    if(new TinkerRegisterEvent.ModifierRegisterEvent(modifier).fire()) {
      modifiers.put(alias, modifier);
    }
    else {
      log.debug("Registration of modifier " + alias + " has been cancelled by event");
    }
  }

  public static IModifier getModifier(String identifier) {
    return modifiers.get(identifier);
  }

  public static Collection<IModifier> getAllModifiers() {
    return ImmutableList.copyOf(modifiers.values());
  }

  /**
   * Registers a beheading head drop for an entity
   * @param clazz     Entity class
   * @param callback  Callback function, takes entity as a parameter and returns an item stack
   */
  public static void registerHeadDrop(Class<? extends EntityLivingBase> clazz, Function<EntityLivingBase,ItemStack> callback) {
    headDrops.put(clazz, callback);
  }

  /**
   * Registers a beheading head drop for an entity
   * @param clazz  Entity class
   * @param head   Head that drops from that entity
   */
  public static void registerHeadDrop(Class<? extends EntityLivingBase> clazz, ItemStack head) {
    final ItemStack safeStack = head.copy();
    registerHeadDrop(clazz, (e) -> safeStack);
  }

  /**
   * Gets the head that would be dropped by an entity
   * @param entity  Entity to check
   * @return  The entity's head
   */
  public static ItemStack getHeadDrop(EntityLivingBase entity) {
    Function<EntityLivingBase, ItemStack> callback = headDrops.get(entity.getClass());
    if(callback != null) {
      return callback.apply(entity).copy();
    }
    return ItemStack.EMPTY;
  }

  /*---------------------------------------------------------------------------
  | Smeltery                                                                  |
  ---------------------------------------------------------------------------*/
  private static List<MeltingRecipe> meltingRegistry = Lists.newLinkedList();
  private static List<ICastingRecipe> tableCastRegistry = Lists.newLinkedList();
  private static List<ICastingRecipe> basinCastRegistry = Lists.newLinkedList();
  private static List<AlloyRecipe> alloyRegistry = Lists.newLinkedList();
  private static Map<FluidStack, Integer> smelteryFuels = Maps.newHashMap();
  private static Map<ResourceLocation, FluidStack> entityMeltingRegistry = Maps.newHashMap();

  /** Registers this item with all its metadatas to melt into amount of the given fluid. */
  public static void registerMelting(Item item, Fluid fluid, int amount) {
    ItemStack stack = new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE);
    registerMelting(new MeltingRecipe(new RecipeMatch.Item(stack, 1, amount), fluid));
  }

  /** Registers this block with all its metadatas to melt into amount of the given fluid. */
  public static void registerMelting(Block block, Fluid fluid, int amount) {
    ItemStack stack = new ItemStack(block, 1, OreDictionary.WILDCARD_VALUE);
    registerMelting(new MeltingRecipe(new RecipeMatch.Item(stack, 1, amount), fluid));
  }

  /** Registers this itemstack NBT-SENSITIVE to melt into amount of the given fluid. */
  public static void registerMelting(ItemStack stack, Fluid fluid, int amount) {
    registerMelting(new MeltingRecipe(new RecipeMatch.ItemCombination(amount, stack), fluid));
  }

  public static void registerMelting(String oredict, Fluid fluid, int amount) {
    registerMelting(new MeltingRecipe(new RecipeMatch.Oredict(oredict, 1, amount), fluid));
  }

  public static void registerMelting(MeltingRecipe recipe) {
    if(new TinkerRegisterEvent.MeltingRegisterEvent(recipe).fire()) {
      meltingRegistry.add(recipe);
    }
    else {
      try {
        String input = recipe.input.getInputs().stream().findFirst().map(ItemStack::getUnlocalizedName).orElse("?");
        log.debug("Registration of melting recipe for " + recipe.getResult().getUnlocalizedName() + " from " + input + " has been cancelled by event");
      } catch(Exception e) {
        log.error("Error when logging melting event", e);
      }
    }
  }

  public static MeltingRecipe getMelting(ItemStack stack) {
    for(MeltingRecipe recipe : meltingRegistry) {
      if(recipe.matches(stack)) {
        return recipe;
      }
    }

    return null;
  }

  public static List<MeltingRecipe> getAllMeltingRecipies() {
    return ImmutableList.copyOf(meltingRegistry);
  }

  public static void registerAlloy(FluidStack result, FluidStack... inputs) {
    if(result.amount < 1) {
      error("Alloy Recipe: Resulting alloy %s has to have an amount (%d)", result.getLocalizedName(), result.amount);
    }
    if(inputs.length < 2) {
      error("Alloy Recipe: Alloy for %s must consist of at least 2 liquids", result.getLocalizedName());
    }

    registerAlloy(new AlloyRecipe(result, inputs));
  }

  public static void registerAlloy(AlloyRecipe recipe) {
    if(new TinkerRegisterEvent.AlloyRegisterEvent(recipe).fire()) {
      alloyRegistry.add(recipe);
    }
    else {
      try {
        String input = recipe.getFluids().stream().map(FluidStack::getUnlocalizedName).collect(Collectors.joining(", "));
        String output = recipe.getResult().getUnlocalizedName();
        log.debug("Registration of alloy recipe for " + output + " from [" + input + "] has been cancelled by event");
      } catch(Exception e) {
        log.error("Error when logging alloy event", e);
      }
    }
  }

  public static List<AlloyRecipe> getAlloys() {
    return ImmutableList.copyOf(alloyRegistry);
  }

  /** Registers a casting recipe for casting table */
  public static void registerTableCasting(ItemStack output, ItemStack cast, Fluid fluid, int amount) {
    RecipeMatch rm = null;
    if(cast != ItemStack.EMPTY) {
      rm = RecipeMatch.ofNBT(cast);
    }
    registerTableCasting(new CastingRecipe(output, rm, fluid, amount));
  }

  public static void registerTableCasting(ICastingRecipe recipe) {
    if(new TinkerRegisterEvent.TableCastingRegisterEvent(recipe).fire()) {
      tableCastRegistry.add(recipe);
    }
    else {
      try {
        String output = Optional.ofNullable(recipe.getResult(ItemStack.EMPTY, FluidRegistry.WATER)).map(ItemStack::getUnlocalizedName).orElse("Unknown");
        log.debug("Registration of table casting recipe for " + output + " has been cancelled by event");
      } catch(Exception e) {
        log.error("Error when logging table casting event", e);
      }
    }
  }

  public static ICastingRecipe getTableCasting(ItemStack cast, Fluid fluid) {
    for(ICastingRecipe recipe : tableCastRegistry) {
      if(recipe.matches(cast, fluid)) {
        return recipe;
      }
    }
    return null;
  }

  public static List<ICastingRecipe> getAllTableCastingRecipes() {
    return ImmutableList.copyOf(tableCastRegistry);
  }

  /** Registers a casting recipe for the casting basin */
  public static void registerBasinCasting(ItemStack output, ItemStack cast, Fluid fluid, int amount) {
    RecipeMatch rm = null;
    if(!cast.isEmpty()) {
      rm = RecipeMatch.ofNBT(cast);
    }
    registerBasinCasting(new CastingRecipe(output, rm, fluid, amount));
  }

  public static void registerBasinCasting(ICastingRecipe recipe) {
    if(new TinkerRegisterEvent.BasinCastingRegisterEvent(recipe).fire()) {
      basinCastRegistry.add(recipe);
    }
    else {
      try {
        String output = Optional.ofNullable(recipe.getResult(ItemStack.EMPTY, FluidRegistry.WATER)).map(ItemStack::getUnlocalizedName).orElse("Unknown");
        log.debug("Registration of basin casting recipe for " + output + " has been cancelled by event");
      } catch(Exception e) {
        log.error("Error when logging basin casting event", e);
      }
    }
  }

  public static ICastingRecipe getBasinCasting(ItemStack cast, Fluid fluid) {
    for(ICastingRecipe recipe : basinCastRegistry) {
      if(recipe.matches(cast, fluid)) {
        return recipe;
      }
    }
    return null;
  }

  public static List<ICastingRecipe> getAllBasinCastingRecipes() {
    return ImmutableList.copyOf(basinCastRegistry);
  }

  /**
   * Registers a liquid to be used as smeltery fuel.
   * Temperature is derived from fluid temperature.
   *
   * @param fluidStack   The fluid. Amount is the minimal increment that is consumed at once.
   * @param fuelDuration How many ticks the consumtpion of the fluidStack lasts.
   */
  public static void registerSmelteryFuel(FluidStack fluidStack, int fuelDuration) {
    if(new TinkerRegisterEvent.SmelteryFuelRegisterEvent(fluidStack, fuelDuration).fire()) {
      smelteryFuels.put(fluidStack, fuelDuration);
    }
    else {
      try {
        String input = fluidStack.getUnlocalizedName();
        log.debug("Registration of smeltery fuel " + input + " has been cancelled by event");
      } catch(Exception e) {
        log.error("Error when logging smeltery fuel event", e);
      }
    }
  }

  /** Checks if the given fluidstack can be used as smeltery fuel */
  public static boolean isSmelteryFuel(FluidStack in) {
    for(Map.Entry<FluidStack, Integer> entry : smelteryFuels.entrySet()) {
      if(entry.getKey().isFluidEqual(in)) {
        return true;
      }
    }

    return false;
  }

  /** Reduces the fluidstack by one increment of the fuel and returns how much fuel duration it gives. */
  public static int consumeSmelteryFuel(FluidStack in) {
    for(Map.Entry<FluidStack, Integer> entry : smelteryFuels.entrySet()) {
      if(entry.getKey().isFluidEqual(in)) {
        FluidStack fuel = entry.getKey();
        int out = entry.getValue();
        if(in.amount < fuel.amount) {
          float coeff = (float) in.amount / (float) fuel.amount;
          out = Math.round(coeff * in.amount);
          in.amount = 0;
        }
        else {
          in.amount -= fuel.amount;
        }

        return out;
      }
    }

    return 0;
  }

  /** Returns all registered smeltery fuels */
  public static Collection<FluidStack> getSmelteryFuels() {
    return ImmutableSet.copyOf(smelteryFuels.keySet());
  }

  /** Register an entity to melt into the given fluidstack. The fluidstack is returned for 1 heart damage */
  public static void registerEntityMelting(Class<? extends Entity> clazz, FluidStack liquid) {
    ResourceLocation name = EntityList.getKey(clazz);

    if(name == null) {
      error("Entity Melting: Entity %s is not registered in the EntityList", clazz.getSimpleName());
    }

    TinkerRegisterEvent.EntityMeltingRegisterEvent event = new TinkerRegisterEvent.EntityMeltingRegisterEvent(clazz, liquid);
    if(event.fire()) {
      entityMeltingRegistry.put(name, event.getNewFluidStack());
    }
    else {
      try {
        String output = liquid.getUnlocalizedName();
        log.debug("Registration of entity melting for " + clazz.getName() + " into " + output + " has been cancelled by event");
      } catch(Exception e) {
        log.error("Error when logging entity melting event", e);
      }
    }
  }

  public static FluidStack getMeltingForEntity(Entity entity) {
    ResourceLocation name = EntityList.getKey(entity);
    FluidStack fluidStack = entityMeltingRegistry.get(name);
    // check if the fluid is the correct one to use
    return Optional.ofNullable(fluidStack)
                   .map(slimeknights.tconstruct.library.utils.FluidUtil::getValidFluidStackOrNull)
                   .orElse(null);
  }

  /*---------------------------------------------------------------------------
  | Drying Rack                                                               |
  ---------------------------------------------------------------------------*/
  private static List<DryingRecipe> dryingRegistry = Lists.newLinkedList();

  /**
   * @return The list of all drying rack recipes
   */
  public static List<DryingRecipe> getAllDryingRecipes() {
    return ImmutableList.copyOf(dryingRegistry);
  }

  /**
   * Adds a new drying recipe
   *
   * @param input  Input ItemStack
   * @param output Output ItemStack
   * @param time   Recipe time in ticks
   */
  public static void registerDryingRecipe(ItemStack input, ItemStack output, int time) {
    if(output.isEmpty() || input.isEmpty()) {
      return;
    }
    addDryingRecipe(new DryingRecipe(new RecipeMatch.Item(input, 1), output, time));
  }

  /**
   * Adds a new drying recipe
   *
   * @param input  Input Item
   * @param output Output ItemStack
   * @param time   Recipe time in ticks
   */
  public static void registerDryingRecipe(Item input, ItemStack output, int time) {
    if(output.isEmpty() || input == null) {
      return;
    }

    ItemStack stack = new ItemStack(input, 1, OreDictionary.WILDCARD_VALUE);
    addDryingRecipe(new DryingRecipe(new RecipeMatch.Item(stack, 1), output, time));
  }

  /**
   * Adds a new drying recipe
   *
   * @param input  Input Item
   * @param output Output Item
   * @param time   Recipe time in ticks
   */
  public static void registerDryingRecipe(Item input, Item output, int time) {
    if(output == null || input == null) {
      return;
    }

    ItemStack stack = new ItemStack(input, 1, OreDictionary.WILDCARD_VALUE);
    addDryingRecipe(new DryingRecipe(new RecipeMatch.Item(stack, 1), new ItemStack(output), time));
  }

  /**
   * Adds a new drying recipe
   *
   * @param input  Input Block
   * @param output Output Block
   * @param time   Recipe time in ticks
   */
  public static void registerDryingRecipe(Block input, Block output, int time) {
    if(output == null || input == null) {
      return;
    }

    ItemStack stack = new ItemStack(input, 1, OreDictionary.WILDCARD_VALUE);
    addDryingRecipe(new DryingRecipe(new RecipeMatch.Item(stack, 1), new ItemStack(output), time));
  }

  /**
   * Adds a new drying recipe
   *
   * @param oredict Input ore dictionary entry
   * @param output  Output ItemStack
   * @param time    Recipe time in ticks
   */
  public static void registerDryingRecipe(String oredict, ItemStack output, int time) {
    if(output.isEmpty() || oredict == null) {
      return;
    }

    addDryingRecipe(new DryingRecipe(new RecipeMatch.Oredict(oredict, 1), output, time));
  }

  public static void addDryingRecipe(DryingRecipe recipe) {
    if(new TinkerRegisterEvent.DryingRackRegisterEvent(recipe).fire()) {
      dryingRegistry.add(recipe);
    }
    else {
      try {
        String input = recipe.input.getInputs().stream().findFirst().map(ItemStack::getUnlocalizedName).orElse("?");
        String output = recipe.getResult().getUnlocalizedName();
        log.debug("Registration of drying rack recipe for " + output + " from " + input + " has been cancelled by event");
      } catch(Exception e) {
        log.error("Error when logging drying rack event", e);
      }
    }
  }

  /**
   * Gets the drying time for a drying recipe
   *
   * @param input Input ItemStack
   * @return Output drying time, or -1 if no recipe is found
   */
  public static int getDryingTime(ItemStack input) {
    for(DryingRecipe r : dryingRegistry) {
      if(r.matches(input)) {
        return r.getTime();
      }
    }

    return -1;
  }

  /**
   * Gets the result for a drying recipe
   *
   * @param input Input ItemStack
   * @return Output A copy of the output ItemStack, or Itemstack.EMPTY if no recipe is found
   */
  public static ItemStack getDryingResult(ItemStack input) {
    for(DryingRecipe r : dryingRegistry) {
      if(r.matches(input)) {
        return r.getResult();
      }
    }

    return ItemStack.EMPTY;
  }

  /*---------------------------------------------------------------------------
  | MATERIAL INTEGRATION                                                      |
  ---------------------------------------------------------------------------*/

  private static List<MaterialIntegration> materialIntegrations = new ArrayList<>();

  public static MaterialIntegration integrate(Material material) {
    return integrate(new MaterialIntegration(material));
  }

  public static MaterialIntegration integrate(Material material, Fluid fluid) {
    return integrate(new MaterialIntegration(material, fluid));
  }

  public static MaterialIntegration integrate(Material material, String oreRequirement) {
    MaterialIntegration materialIntegration = new MaterialIntegration(oreRequirement, material, null, null);
    materialIntegration.setRepresentativeItem(oreRequirement);
    return integrate(materialIntegration);
  }

  public static MaterialIntegration integrate(Material material, Fluid fluid, String oreSuffix) {
    return integrate(new MaterialIntegration(material, fluid, oreSuffix));
  }

  public static MaterialIntegration integrate(Fluid fluid, String oreSuffix) {
    return integrate(new MaterialIntegration(null, fluid, oreSuffix));
  }

  /**
   * Causes a material to be default-integrated with the provided information.
   * Includes fluids, recipes and oredict integration
   *
   * Can be done during preInit and Init
   */
  public static MaterialIntegration integrate(MaterialIntegration materialIntegration) {
    MaterialEvent.IntegrationEvent event = new MaterialEvent.IntegrationEvent(materialIntegration.material, materialIntegration);
    if(MinecraftForge.EVENT_BUS.post(event)) {
      // cancelled
      log.debug("Registration of material integration for material " + materialIntegration.material + " has been cancelled by event");
    }
    else {
      materialIntegrations.add(materialIntegration);
    }

    return materialIntegration;
  }

  public static List<MaterialIntegration> getMaterialIntegrations() {
    return ImmutableList.copyOf(materialIntegrations);
  }

  /*---------------------------------------------------------------------------
  | Traceability & Internal stuff                                             |
  ---------------------------------------------------------------------------*/

  static void putMaterialTrace(String materialIdentifier) {
    ModContainer activeMod = Loader.instance().activeModContainer();
    materialRegisteredByMod.put(materialIdentifier, activeMod);
  }

  static void putStatTrace(String materialIdentifier, IMaterialStats stats, ModContainer trace) {
    if(!statRegisteredByMod.containsKey(materialIdentifier)) {
      statRegisteredByMod.put(materialIdentifier, new HashMap<>());
    }
    statRegisteredByMod.get(materialIdentifier).put(stats.getIdentifier(), trace);
  }

  static void putTraitTrace(String materialIdentifier, ITrait trait, ModContainer trace) {
    if(!traitRegisteredByMod.containsKey(materialIdentifier)) {
      traitRegisteredByMod.put(materialIdentifier, new HashMap<>());
    }
    traitRegisteredByMod.get(materialIdentifier).put(trait.getIdentifier(), trace);
  }

  public static ModContainer getTrace(Material material) {
    return materialRegisteredByMod.get(material.identifier);
  }

  private static void error(String message, Object... params) {
    throw new TinkerAPIException(String.format(message, params));
  }
}
