package slimeknights.tconstruct.library.tools;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.ExtraMaterialStats;
import slimeknights.tconstruct.library.materials.HandleMaterialStats;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.materials.IMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tinkering.TinkersItem;
import slimeknights.tconstruct.library.traits.ITrait;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.library.utils.TooltipBuilder;
import slimeknights.tconstruct.tools.TinkerMaterials;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.traits.InfiTool;
import slimeknights.tconstruct.tools.traits.ToolGrowth;

/**
 * Intermediate abstraction layer for all tools/melee weapons. This class has all the callbacks for blocks and enemies
 * so tools and weapons can share behaviour.
 */
public abstract class ToolCore extends TinkersItem {

  protected final static int DEFAULT_MODIFIERS = 3;
  protected final static ToolGrowth toolGrowth;

  static {
    toolGrowth = new ToolGrowth();
    TinkerRegistry.addTrait(toolGrowth);
  }

  public ToolCore(PartMaterialType... requiredComponents) {
    super(requiredComponents);

    this.setCreativeTab(TinkerRegistry.tabTools);
    this.setNoRepair(); // >_>

    TinkerRegistry.registerTool(this);
    addCategory(Category.TOOL);
  }

  @Override
  public int getMaxDamage(ItemStack stack) {
    return ToolHelper.getDurabilityStat(stack);
  }

  @Override
  public void setDamage(ItemStack stack, int damage) {
    super.setDamage(stack, damage);

    if(getDamage(stack) == getMaxDamage(stack)) {
      ToolHelper.breakTool(stack, null);
    }
  }

  @Override
  public boolean isDamageable() {
    return true;
  }

  /* Tool and Weapon specific properties */

  /** Multiplier applied to the actual mining speed of the tool
   *  Internally a hammer and pick have the same speed, but a hammer is 2/3 slower
   */
  public float miningSpeedModifier() {
    return 1f;
  }

  /** Multiplier for damage from materials. Should be fixed per tool. */
  public abstract float damagePotential();

  /**
   * A fixed damage value where the calculations start to apply dimishing returns.
   * Basically if you'd hit more than that damage with this tool, the damage is gradually reduced depending on how much the cutoff is exceeded.
   */
  public float damageCutoff() {
    return 15.0f; // in general this should be sufficient and only needs increasing if it's a stronger weapon
    // fun fact: diamond sword with sharpness V has 15 damage
  }

  /**
   * Allows you set the attack speed. Equivalent to the vanilla attack speed.
   * Default speed is 4, which is equal to any standard item. Value has to be greater than zero.
   */
  public double attackSpeed() {
    return 4;
  }

  /**
   * Knockback modifier. Basically this takes the vanilla knockback on hit and modifies it by this factor.
   */
  public float knockback() {
    return 1.0f;
  }

  /**
   * Actually deal damage to the entity we hit. Can be overridden for special behaviour
   *
   * @return True if the entity was hit. Usually the return value of {@link Entity#attackEntityFrom(DamageSource, float)}
   */
  public boolean dealDamage(ItemStack stack, EntityLivingBase player, EntityLivingBase entity, float damage) {
    if(player instanceof EntityPlayer) {
      return entity.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) player), damage);
    }
    return entity.attackEntityFrom(DamageSource.causeMobDamage(player), damage);
  }

  /**
   * Called when an entity is getting damaged with the tool.
   * Reduce the tools durability accordingly
   * player can be null!
   */
  public void reduceDurabilityOnHit(ItemStack stack, EntityPlayer player, float damage) {
    damage = Math.max(1f, damage/10f);
    if(!hasCategory(Category.WEAPON)) {
      damage *= 2;
    }
    ToolHelper.damageTool(stack, (int)damage, player);
  }

  @Override
  public float getStrVsBlock(ItemStack stack, IBlockState state) {
    if(isEffective(state) || ToolHelper.isToolEffective(stack, state)) {
      return ToolHelper.calcDigSpeed(stack, state);
    }
    return super.getStrVsBlock(stack, state);
  }

  public boolean isEffective(IBlockState block) {
    return false;
  }

  @Override
  public boolean canHarvestBlock(IBlockState state, ItemStack stack) {
    return isEffective(state);
  }

  @Override
  public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player) {
    if(this instanceof IAoeTool && ((IAoeTool) this).isAoeHarvestTool() && ToolHelper.isToolEffective2(itemstack, player.worldObj.getBlockState(pos))) {
      for(BlockPos extraPos : ((IAoeTool) this).getAOEBlocks(itemstack, player.worldObj, player, pos)) {
        ToolHelper.breakExtraBlock(itemstack, player.worldObj, player, extraPos, pos);
      }
    }

    return super.onBlockStartBreak(itemstack, pos, player);
  }

  @Override
  public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
    return ToolHelper.attackEntity(stack, this, player, entity);
  }

  @Override
  public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
    /*if(attackSpeed() > 0) {
      int speed = Math.min(5, attackSpeed());
      ToolHelper.swingItem(speed, entityLiving);
      return true;
    }*/
    return super.onEntitySwing(entityLiving, stack);
  }

  @Override
  public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
    // todo: potentially reenable the hurtresistance thing if attackspeeds go below 1
    /*
    if(attackSpeed() > 0) {
      target.hurtResistantTime -= attackSpeed();
      target.hurtTime -= attackSpeed();
    }*/
    return super.hitEntity(stack, target, attacker);
  }

  @Override
  public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
    Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);

    if (slot == EntityEquipmentSlot.MAINHAND)
    {
      multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getAttributeUnlocalizedName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", (double)ToolHelper.getActualAttack(stack), 0));
      multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getAttributeUnlocalizedName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", attackSpeed() - 4d, 0));
    }

    return multimap;
  }

  @Override
  public List<String> getInformation(ItemStack stack) {
    return getInformation(stack, true);
  }

  @Override
  public void getTooltip(ItemStack stack, List<String> tooltips) {
    if(ToolHelper.isBroken(stack)) {
      tooltips.add("" + TextFormatting.DARK_RED + TextFormatting.BOLD + Util.translate("tooltip.tool.broken"));
    }
    super.getTooltip(stack, tooltips);
  }

  @Override
  public void getTooltipDetailed(ItemStack stack, List<String> tooltips) {
    tooltips.addAll(getInformation(stack, false));
  }

  public List<String> getInformation(ItemStack stack, boolean detailed) {
    TooltipBuilder info = new TooltipBuilder(stack);

    info.addDurability(!detailed);
    if(hasCategory(Category.HARVEST)) {
      info.addHarvestLevel();
      info.addMiningSpeed();
    }
    info.addAttack();

    if(ToolHelper.getFreeModifiers(stack) > 0) {
      info.addFreeModifiers();
    }

    if(detailed) {
      info.addModifierInfo();
    }

    return info.getTooltip();
  }

  @Override
  public void getTooltipComponents(ItemStack stack, List<String> tooltips) {
    List<Material> materials = TinkerUtil.getMaterialsFromTagList(TagUtil.getBaseMaterialsTagList(stack));
    List<PartMaterialType> component = getRequiredComponents();

    if(materials.size() < component.size()) {
      return;
    }

    for(int i = 0; i < component.size(); i++) {
      PartMaterialType pmt = component.get(i);
      Material material = materials.get(i);

      // get (one possible) toolpart used to craft the thing
      Iterator<IToolPart> partIter = pmt.getPossibleParts().iterator();
      if(!partIter.hasNext()) {
        continue;
      }

      IToolPart part = partIter.next();
      ItemStack partStack = part.getItemstackWithMaterial(material);
      if(partStack != null) {
        // we have the part, add it
        tooltips.add(material.getTextColor() + TextFormatting.UNDERLINE + partStack.getDisplayName());

        // find out which stats and traits it contributes and add it to the tooltip
        for(IMaterialStats stats : material.getAllStats()) {
          if(pmt.usesStat(stats.getIdentifier())) {
            tooltips.addAll(stats.getLocalizedInfo());
            for(ITrait trait : pmt.getApplicableTraitsForMaterial(material)) {
              tooltips.add(material.getTextColor() + trait.getLocalizedName());
            }
          }
        }
        tooltips.add("");
      }
    }
  }

  @SideOnly(Side.CLIENT)
  @Override
  public FontRenderer getFontRenderer(ItemStack stack) {
    return ClientProxy.fontRenderer;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public boolean hasEffect(ItemStack stack) {
    return TagUtil.hasEnchantEffect(stack);
  }

  @Override
  public String getItemStackDisplayName(ItemStack stack) {
    // if the tool is not named we use the repair tools for a prefix like thing
    List<Material> materials = TinkerUtil.getMaterialsFromTagList(TagUtil.getBaseMaterialsTagList(stack));
    // we save all the ones for the name in a set so we don't have the same material in it twice
    Set<Material> nameMaterials = Sets.newLinkedHashSet();

    for(int index : getRepairParts()) {
      if(index < materials.size()) {
        nameMaterials.add(materials.get(index));
      }
    }

    String itemName = super.getItemStackDisplayName(stack);

    // no material
    if(nameMaterials.isEmpty()) {
      return itemName;
    }
    // only one material - prefix
    if(nameMaterials.size() == 1) {
      return nameMaterials.iterator().next().getLocalizedItemName(itemName);
    }

    // multiple materials. we'll have to combine
    StringBuilder sb = new StringBuilder();
    Iterator<Material> iter = nameMaterials.iterator();
    Material material = iter.next();
    sb.append(material.getLocalizedName());
    while(iter.hasNext()) {
      material = iter.next();
      sb.append("-");
      sb.append(material.getLocalizedName());
    }
    sb.append(" ");
    sb.append(itemName);

    return sb.toString();
  }

  // Creative tab items
  @Override
  public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
    addDefaultSubItems(subItems);
  }

  protected void addDefaultSubItems(List<ItemStack> subItems) {
    for(Material head : TinkerRegistry.getAllMaterials()) {
      if(!head.hasStats(HeadMaterialStats.TYPE)) {
        continue;
      }

      List<Material> mats = new ArrayList<Material>(requiredComponents.length);

      for(int i = 0; i < requiredComponents.length; i++) {
        // todo: check for applicability with stats
        mats.add(head);
      }

      ItemStack tool = buildItem(mats);
      // only valid ones
      if(hasValidMaterials(tool)) {
        subItems.add(tool);
      }
    }
  }

  protected void addInfiTool(List<ItemStack> subitems, String name) {
    ItemStack tool = getInfiTool(name);
    if(hasValidMaterials(tool)) {
      subitems.add(tool);
    }
  }

  protected ItemStack getInfiTool(String name) {
    // The InfiHarvester!
    List<Material> materials = ImmutableList.of(TinkerMaterials.slime, TinkerMaterials.cobalt, TinkerMaterials.ardite, TinkerMaterials.ardite);
    materials = materials.subList(0, requiredComponents.length);
    ItemStack tool = buildItem(materials);
    InfiTool.INSTANCE.apply(tool);
    tool.setStackDisplayName(name);

    return tool;
  }

  @Override
  public int getHarvestLevel(ItemStack stack, String toolClass) {
    if(this.getToolClasses(stack).contains(toolClass)) {
      NBTTagCompound tag = TagUtil.getToolTag(stack);
      // will return 0 if the tag has no info anyway
      return ToolHelper.getHarvestLevelStat(stack);
    }
    return super.getHarvestLevel(stack, toolClass);
  }

  /** A simple string identifier for the tool, used for identification in texture generation etc. */
  public String getIdentifier() {
    return Util.getItemLocation(this).getResourcePath();
  }

  /** The tools name completely without material information */
  public String getLocalizedToolName() {
    return Util.translate(getUnlocalizedName() + ".name");
  }

  /** The tools name with the given material. e.g. "Wooden Pickaxe" */
  public String getLocalizedToolName(Material material) {
    return material.getLocalizedItemName(getLocalizedToolName());
  }

  /** Returns info about the Tool. Displayed in the tool stations etc. */
  public String getLocalizedDescription() {
    return Util.translate(getUnlocalizedName() + ".desc");
  }

  /* Additional Trait callbacks */

  @Override
  public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
    super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);

    NBTTagList list = TagUtil.getTraitsTagList(stack);
    for(int i = 0; i < list.tagCount(); i++) {
      ITrait trait = TinkerRegistry.getTrait(list.getStringTagAt(i));
      if(trait != null) {
        trait.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
      }
    }
  }

  @Override
  public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
    if(ToolHelper.isBroken(stack)) {
      return false;
    }

    boolean effective = isEffective(state) || ToolHelper.isToolEffective(stack, worldIn.getBlockState(pos));
    int damage = effective ? 1 : 2;

    afterBlockBreak(stack, worldIn, state, pos, entityLiving, damage, effective);

    return hasCategory(Category.TOOL);
  }

  public void afterBlockBreak(ItemStack stack, World world, IBlockState state, BlockPos pos, EntityLivingBase player, int damage, boolean wasEffective) {
    NBTTagList list = TagUtil.getTraitsTagList(stack);
    for(int i = 0; i < list.tagCount(); i++) {
      ITrait trait = TinkerRegistry.getTrait(list.getStringTagAt(i));
      if(trait != null) {
        trait.afterBlockBreak(stack, world, state, pos, player, wasEffective);
      }
    }

    ToolHelper.damageTool(stack, damage, player);
  }

  // elevate to public
  @Override
  public RayTraceResult getMovingObjectPositionFromPlayer(World worldIn, EntityPlayer playerIn, boolean useLiquids) {
    return super.getMovingObjectPositionFromPlayer(worldIn, playerIn, useLiquids);
  }

  protected void preventSlowDown(Entity entityIn, float originalSpeed) {
    TinkerTools.proxy.preventPlayerSlowdown(entityIn, originalSpeed, this);
  }

  @Override
  public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
    return !ItemStack.areItemStacksEqual(oldStack, newStack) || slotChanged;
  }

  /**
   * Builds a default tool from:
   * 1. Handle
   * 2. Head
   * 3. Accessoire (if present)
   */
  protected ToolNBT buildDefaultTag(List<Material> materials) {
    ToolNBT data = new ToolNBT();

    if(materials.size() >= 2) {
      HandleMaterialStats handle = materials.get(0).getStatsOrUnknown(HandleMaterialStats.TYPE);
      HeadMaterialStats head = materials.get(1).getStatsOrUnknown(HeadMaterialStats.TYPE);
      // start with head
      data.head(head);

      // add in accessoires if present
      if(materials.size() >= 3) {
        ExtraMaterialStats binding = materials.get(2).getStatsOrUnknown(ExtraMaterialStats.TYPE);
        data.extra(binding);
      }

      // calculate handle impact
      data.handle(handle);
    }

    // 3 free modifiers
    data.modifiers = DEFAULT_MODIFIERS;

    return data;
  }
}
