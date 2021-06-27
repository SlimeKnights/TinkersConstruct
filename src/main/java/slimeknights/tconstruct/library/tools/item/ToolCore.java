package slimeknights.tconstruct.library.tools.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.EquipmentSlotType.Group;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Rarity;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.capability.ToolCapabilityProvider;
import slimeknights.tconstruct.library.tinkering.ITinkerStationDisplay;
import slimeknights.tconstruct.library.tinkering.IndestructibleEntityItem;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.library.tools.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.TooltipBuilder;
import slimeknights.tconstruct.library.utils.TooltipType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * An indestructible item constructed from different parts.
 * This class handles how all the data for items made out of different
 * The NBT representation of tool stats, what the tool is made of, which modifier have been applied, etc.
 */
public class ToolCore extends Item implements ITinkerStationDisplay, IModifiableWeapon, IModifiableHarvest {
  protected static final UUID REACH_MODIFIER = UUID.fromString("9b26fa32-5774-4b4e-afc3-b4055ecb1f6a");
  /** Modifier key to make a tool spawn an indestructable entity */
  public static final ResourceLocation INDESTRUCTIBLE_ENTITY = Util.getResource("indestructible");
  /** Modifier key to make a tool spawn an indestructable entity */
  public static final ResourceLocation SHINY = Util.getResource("shiny");
  /** Modifier key to make a tool spawn an indestructable entity */
  public static final ResourceLocation RARITY = Util.getResource("rarity");

  protected static final ITextComponent TOOLTIP_HOLD_SHIFT;
  private static final ITextComponent TOOLTIP_HOLD_CTRL;
  static {
    ITextComponent shift = Util.makeTranslation("key", "shift").mergeStyle(TextFormatting.YELLOW, TextFormatting.ITALIC);
    TOOLTIP_HOLD_SHIFT = new TranslationTextComponent(Util.makeTranslationKey("tooltip", "hold_shift"), shift);
    ITextComponent ctrl = Util.makeTranslation("key", "ctrl").mergeStyle(TextFormatting.AQUA, TextFormatting.ITALIC);
    TOOLTIP_HOLD_CTRL = new TranslationTextComponent(Util.makeTranslationKey("tooltip", "hold_ctrl"), ctrl);
  }


  /** Tool definition for the given tool */
  @Getter
  private final ToolDefinition toolDefinition;

  /** Cached tool for rendering on UIs */
  private ItemStack toolForRendering;

  protected ToolCore(Properties properties, ToolDefinition toolDefinition) {
    super(properties);
    this.toolDefinition = toolDefinition;
  }

  @Override
  public boolean hasEffect(ItemStack stack) {
    // we use enchantments to handle some modifiers, so don't glow from them
    // however, if a modifier wants to glow let them
    return ToolStack.from(stack).getVolatileData().getBoolean(SHINY);
  }

  @Override
  public Rarity getRarity(ItemStack stack) {
    int rarity = ToolStack.from(stack).getVolatileData().getInt(RARITY);
    return Rarity.values()[MathHelper.clamp(rarity, 0, 3)];
  }

  /**
   * Sets the rarity of the stack
   * @param volatileData     NBT
   * @param rarity  Rarity, only supports vanilla values
   */
  public static void setRarity(ModDataNBT volatileData, Rarity rarity) {
    int current = volatileData.getInt(RARITY);
    if (rarity.ordinal() > current) {
      volatileData.putInt(RARITY, rarity.ordinal());
    }
  }

  @Override
  public int getItemStackLimit(ItemStack stack) {
    return 1;
  }

  @Override
  public boolean isRepairable(ItemStack stack) {
    return false;
  }


  /* Item Entity -> INDESTRUCTIBLE */

  @Override
  public boolean hasCustomEntity(ItemStack stack) {
    return ToolStack.from(stack).getVolatileData().getBoolean(INDESTRUCTIBLE_ENTITY);
  }

  @Override
  public Entity createEntity(World world, Entity original, ItemStack stack) {
    if (ToolStack.from(stack).getVolatileData().getBoolean(INDESTRUCTIBLE_ENTITY)) {
      IndestructibleEntityItem entity = new IndestructibleEntityItem(world, original.getPosX(), original.getPosY(), original.getPosZ(), stack);
      entity.setPickupDelayFrom(original);
      return entity;
    }
    return null;
  }

  /* Damage/Durability */

  @Override
  public boolean isDamageable() {
    return true;
  }

  @Override
  public int getMaxDamage(ItemStack stack) {
    ToolStack tool = ToolStack.from(stack);
    int durability = tool.getStats().getInt(ToolStats.DURABILITY);
    // vanilla deletes tools if max damage == getDamage, so tell vanilla our max is one higher when broken
    return tool.isBroken() ? durability + 1 : durability;
  }

  @Override
  public int getDamage(ItemStack stack) {
    return ToolStack.from(stack).getDamage();
  }

  @Override
  public void setDamage(ItemStack stack, int damage) {
    ToolStack.from(stack).setDamage(damage);
  }

  @Override
  public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T damager, Consumer<T> onBroken) {
    // We basically emulate Itemstack.damageItem here. We always return 0 to skip the handling in ItemStack.
    // If we don't tools ignore our damage logic
    if (ToolDamageUtil.damage(ToolStack.from(stack), amount, damager, stack)) {
      onBroken.accept(damager);
    }

    return 0;
  }

  @Override
  public boolean showDurabilityBar(ItemStack stack) {
    ToolStack tool = ToolStack.from(stack);
    // if any modifier wishes to show when undamaged, let them
    for (ModifierEntry entry : tool.getModifierList()) {
      Boolean show = entry.getModifier().showDurabilityBar(tool, entry.getLevel());
      if (show != null) {
        return show;
      }
    }
    return tool.getDamage() > 0;
  }

  /**
   * Helper to avoid unneeded tool stack parsing
   * @param tool  Tool stack
   * @return  Durability for display
   */
  private double getDamagePercentage(ToolStack tool) {
    // first modifier who wishs to handle it wins
    for (ModifierEntry entry : tool.getModifierList()) {
      double display = entry.getModifier().getDamagePercentage(tool, entry.getLevel());
      if (!Double.isNaN(display)) {
        return display;
      }
    }

    // no one took it? just use regular durability
    return (double) tool.getDamage() / tool.getStats().getInt(ToolStats.DURABILITY);
  }

  @Override
  public double getDurabilityForDisplay(ItemStack stack) {
    ToolStack tool = ToolStack.from(stack);
    if (tool.isBroken()) {
      return 1;
    }
    // always show at least 5% when not broken
    return 0.95 * getDamagePercentage(tool);
  }

  @Override
  public int getRGBDurabilityForDisplay(ItemStack stack) {
    ToolStack tool = ToolStack.from(stack);

    // first modifier who wishs to handle it wins
    for (ModifierEntry entry : tool.getModifierList()) {
      int rgb = entry.getModifier().getDurabilityRGB(tool, entry.getLevel());
      // not a problem to check against -1, the top 16 bits are unused
      if (rgb != -1) {
        return rgb;
      }
    }
    return MathHelper.hsvToRGB(Math.max(0.0f, (float) (1.0f - getDamagePercentage(tool))) / 3.0f, 1.0f, 1.0f);
  }

  /* Mining */

  @Override
  public Set<ToolType> getToolTypes(ItemStack stack) {
    // no classes if broken
    if (ToolDamageUtil.isBroken(stack)) {
      return Collections.emptySet();
    }

    return super.getToolTypes(stack);
  }

  @Override
  public int getHarvestLevel(ItemStack stack, ToolType toolClass, @Nullable PlayerEntity player, @Nullable BlockState blockState) {
    // brokenness is calculated in by the toolTypes check
    if (this.getToolTypes(stack).contains(toolClass)) {
      return ToolStack.from(stack).getStats().getInt(ToolStats.HARVEST_LEVEL);
    }

    return -1;
  }

  @Override
  public boolean onBlockDestroyed(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
    ToolStack tool = ToolStack.from(stack);
    if (tool.isBroken()) {
      return false;
    }

    if (!worldIn.isRemote && worldIn instanceof ServerWorld) {
      boolean isEffective = getToolHarvestLogic().isEffective(tool, stack, state);
      ToolHarvestContext context = new ToolHarvestContext((ServerWorld) worldIn, entityLiving, state, pos, Direction.UP, true, isEffective);
      for (ModifierEntry entry : tool.getModifierList()) {
        entry.getModifier().afterBlockBreak(tool, entry.getLevel(), context);
      }
      ToolDamageUtil.damageAnimated(tool, getToolHarvestLogic().getDamage(tool, stack, worldIn, pos, state), entityLiving);
    }

    return true;
  }

  @Override
  public final boolean canHarvestBlock(ItemStack stack, BlockState state) {
    return this.getToolHarvestLogic().isEffective(ToolStack.from(stack), stack, state);
  }

  @Override
  public final float getDestroySpeed(ItemStack stack, BlockState state) {
    return this.getToolHarvestLogic().getDestroySpeed(stack, state);
  }


  /* Attacking */

  @Override
  public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
    return ToolAttackUtil.attackEntity(stack, this, player, entity);
  }

  @Override
  public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
    CompoundNBT nbt = stack.getTag();
    if (nbt == null || nbt.getBoolean(ToolBuildHandler.KEY_DISPLAY_TOOL)) {
      return ImmutableMultimap.of();
    }

    ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
    ToolStack tool = ToolStack.from(stack);
    if (!tool.isBroken()) {
      // base stats
      if (slot == EquipmentSlotType.MAINHAND) {
        StatsNBT statsNBT = tool.getStats();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "tconstruct.tool.attack_damage", statsNBT.getFloat(ToolStats.ATTACK_DAMAGE), AttributeModifier.Operation.ADDITION));
        // base attack speed is 4, but our numbers start from 4
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(ATTACK_SPEED_MODIFIER, "tconstruct.tool.attack_speed", statsNBT.getFloat(ToolStats.ATTACK_SPEED) - 4d, AttributeModifier.Operation.ADDITION));
        // base value is 5, but our number start from 5
        double reach = statsNBT.getFloat(ToolStats.REACH) - 5d;
        if (reach != 0) {
          builder.put(ForgeMod.REACH_DISTANCE.get(), new AttributeModifier(REACH_MODIFIER, "tconstruct.tool.reach", reach, AttributeModifier.Operation.ADDITION));
        }
      }

      // grab attributes from modifiers, only do for hands (other slots would just be weird)
      if (slot.getSlotType() == Group.HAND) {
        BiConsumer<Attribute,AttributeModifier> attributeConsumer = builder::put;
        for (ModifierEntry entry : tool.getModifierList()) {
          entry.getModifier().addAttributes(tool, entry.getLevel(), slot, attributeConsumer);
        }
      }
    }

    return builder.build();
  }

  /* World interaction */

  @Override
  public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, PlayerEntity player) {
    return getToolHarvestLogic().handleBlockBreak(stack, pos, player);

    // TODO: consider taking over PlayerInteractionManager#tryHarvestBlock and PlayerController#onPlayerDestroyBlock
    // will grant better AOE control, https://github.com/mekanism/Mekanism/blob/1.16.x/src/main/java/mekanism/common/item/gear/ItemMekaTool.java#L238

    /*// this is a really dumb hack.
    // Basically when something with silktouch harvests a block from the offhand
    // the game can't detect that. so we have to switch around the items in the hands for the break call
    // it's switched back in onBlockDestroyed
    if (DualToolHarvestUtil.shouldUseOffhand(player, pos, player.getHeldItemMainhand())) {
      ItemStack off = player.getHeldItemOffhand();

      this.switchItemsInHands(player);
      // remember, off is in the mainhand now
      CompoundNBT tag = off.getOrCreateTag();
      tag.putLong(TAG_SWITCHED_HAND_HAX, player.getEntityWorld().getGameTime());
      off.setTag(tag);
    }*/

    //return this.breakBlock(stack, pos, player);
  }


  /* Modifier interactions */

  @Override
  public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
    super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);

    // don't care about non-living, they skip most tool context
    if (entityIn instanceof LivingEntity) {
      ToolStack tool = ToolStack.from(stack);
      List<ModifierEntry> modifiers = tool.getModifierList();
      if (!modifiers.isEmpty()) {
        LivingEntity living = (LivingEntity) entityIn;
        // we pass in the stack for most custom context, but for the sake of armor its easier to tell them that this is the correct slot for effects
        boolean isHeld = isSelected || living.getHeldItemOffhand() == stack;
        for (ModifierEntry entry : modifiers) {
          entry.getModifier().onInventoryTick(tool, entry.getLevel(), worldIn, living, itemSlot, isSelected, isHeld, stack);
        }
      }
    }
  }
  
  /* Right click hooks */
  
  @Override
  public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
    ToolStack tool = ToolStack.from(stack);
    for (ModifierEntry entry : tool.getModifierList()) {
      ActionResultType result = entry.getModifier().beforeBlockUse(tool, entry.getLevel(), context);
      if (result.isSuccessOrConsume()) {
        return result;
      }
    }
    return super.onItemUseFirst(stack, context);
  }

  @Override
  public ActionResultType onItemUse(ItemUseContext context) {
    ItemStack stack = context.getItem();
    ToolStack tool = ToolStack.from(stack);
    for (ModifierEntry entry : tool.getModifierList()) {
      ActionResultType result = entry.getModifier().afterBlockUse(tool, entry.getLevel(), context);
      if (result.isSuccessOrConsume()) {
        return result;
      }
    }
    return super.onItemUseFirst(stack, context);
  }

  @Override
  public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
    ToolStack tool = ToolStack.from(stack);
    for (ModifierEntry entry : tool.getModifierList()) {
      ActionResultType result = entry.getModifier().onEntityUse(tool, entry.getLevel(), playerIn, target, hand);
      if (result.isSuccessOrConsume()) {
        return result;
      }
    }
    return super.itemInteractionForEntity(stack, playerIn, target, hand);
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
    ItemStack stack = playerIn.getHeldItem(handIn);
    ToolStack tool = ToolStack.from(playerIn.getHeldItem(handIn));
    for (ModifierEntry entry : tool.getModifierList()) {
      ActionResultType result = entry.getModifier().onToolUse(tool, entry.getLevel(), worldIn, playerIn, handIn);
      if (result.isSuccessOrConsume()) {
        return new ActionResult<ItemStack>(result, stack);
      }
    }
    return super.onItemRightClick(worldIn, playerIn, handIn);
  }

  @Override
  public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
    ToolStack tool = ToolStack.from(stack);
    for (ModifierEntry entry : tool.getModifierList()) {
      if (entry.getModifier().onFinishUsing(tool, entry.getLevel(), worldIn, entityLiving)) {
        return stack;
      }
    }
    return super.onItemUseFinish(stack, worldIn, entityLiving);
  }

  @Override
  public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
    ToolStack tool = ToolStack.from(stack);
    for (ModifierEntry entry : tool.getModifierList()) {
      boolean result = entry.getModifier().onStoppedUsing(tool, entry.getLevel(), worldIn, entityLiving, timeLeft);
      if (result) {
        return;
      }
    }
    super.onPlayerStoppedUsing(stack, worldIn, entityLiving, timeLeft);
  }

  @Override
  public int getUseDuration(ItemStack stack) {
    ToolStack tool = ToolStack.from(stack);
    for (ModifierEntry entry : tool.getModifierList()) {
      int result = entry.getModifier().getUseDuration(tool, entry.getLevel());
      if (result > 0) {
        return result;
      }
    }
    return super.getUseDuration(stack);
  }

  @Override
  public UseAction getUseAction(ItemStack stack) {
    ToolStack tool = ToolStack.from(stack);
    for (ModifierEntry entry : tool.getModifierList()) {
      UseAction result = entry.getModifier().getUseAction(tool, entry.getLevel());
      if (result != UseAction.NONE) {
        return result;
      }
    }
     return super.getUseAction(stack);
  }

  /* Information */

  @Override
  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    CompoundNBT tag = stack.getTag();
    boolean isAdvanced = flagIn == TooltipFlags.ADVANCED;
    // if the display tag is set, hide material info
    if (tag != null && tag.getBoolean(ToolBuildHandler.KEY_DISPLAY_TOOL)) {
      ToolStack tool = ToolStack.from(stack);
      for (ModifierEntry entry : tool.getModifierList()) {
        if (entry.getModifier().shouldDisplay(false)) {
          tooltip.add(entry.getModifier().getDisplayName(tool, entry.getLevel()));
        }
      }
    } else if (Screen.hasShiftDown()) {
      // component data
      this.getTooltip(stack, tooltip, TooltipType.SHIFT, isAdvanced);
    } else if (Screen.hasControlDown()) {
      // modifiers
      this.getTooltip(stack, tooltip, TooltipType.CONTROL, isAdvanced);
    } else {
      this.getTooltip(stack, tooltip, TooltipType.NORMAL, isAdvanced);
      tooltip.add(StringTextComponent.EMPTY);
      tooltip.add(TOOLTIP_HOLD_SHIFT);
      tooltip.add(TOOLTIP_HOLD_CTRL);
    }
  }

  /**
   * The tooltip for the item
   *
   * Displays different information based on the tooltip type
   * If the SHIFT key is held, the detailed information is displayed
   * If CONTROL key is held, the materials the tool is made out of is displayed
   * @param stack        the given itemstack
   * @param tooltips     the list of tooltips to add to
   * @param tooltipType  the tooltip type to display
   * @param isAdvanced   if true, this is an advanced tooltip
   */
  public void getTooltip(ItemStack stack, List<ITextComponent> tooltips, TooltipType tooltipType, boolean isAdvanced) {
    switch (tooltipType) {
      case NORMAL: {
        ToolStack tool = ToolStack.from(stack);
        // shows as broken when broken, hold shift for proper durability
        tooltips.add(TooltipBuilder.formatDurability(tool.getCurrentDurability(), tool.getStats().getInt(ToolStats.DURABILITY), true));
        // modifier tooltip
        for (ModifierEntry entry : tool.getModifierList()) {
          if (entry.getModifier().shouldDisplay(false)) {
            tooltips.add(entry.getModifier().getDisplayName(tool, entry.getLevel()));
          }
        }
        break;
      }

      case SHIFT:
        this.getStatInformation(ToolStack.from(stack), tooltips, isAdvanced, false);
        break;

      case CONTROL: {
        ToolStack tool = ToolStack.from(stack);
        List<IMaterial> materials = tool.getMaterialsList();
        if (materials.isEmpty()) {
          tooltips.add(new StringTextComponent("No tool data. NBT missing."));
          return;
        }

        List<IToolPart> components = this.getToolDefinition().getRequiredComponents();
        if (materials.size() < components.size()) {
          return;
        }
        int max = components.size() - 1;
        for (int i = 0; i <= max; i++) {
          IToolPart requirement = components.get(i);
          IMaterial material = materials.get(i);
          ItemStack partStack = requirement.withMaterial(material);
          tooltips.add(partStack.getDisplayName().deepCopy().mergeStyle(TextFormatting.UNDERLINE).modifyStyle(style -> style.setColor(material.getColor())));
          MaterialRegistry.getInstance().getMaterialStats(material.getIdentifier(), requirement.getStatType()).ifPresent(stat -> tooltips.addAll(stat.getLocalizedInfo()));
          if (i != max) {
            tooltips.add(StringTextComponent.EMPTY);
          }
        }
        break;
      }
    }
  }

  @Override
  public ITextComponent getLocalizedName() {
    return new TranslationTextComponent(this.getTranslationKey());
  }

  @Override
  public List<ITextComponent> getInformation(ItemStack stack) {
    return this.getStatInformation(ToolStack.from(stack), new ArrayList<>(), false, true);
  }

  /**
   * Gets the information for the given tool stack
   *
   * @param tool      the tool stack
   * @param isAdvanced  if true, advanced tooltip
   * @param detailed  If true, should show detailed info
   * @return the information for the given stack
   */
  public List<ITextComponent> getStatInformation(ToolStack tool, List<ITextComponent> tooltip, boolean isAdvanced, boolean detailed) {
    TooltipBuilder builder = new TooltipBuilder(tool, tooltip);
    builder.addDurability();
    if (TinkerTags.Items.MELEE.contains(tool.getItem())) {
      builder.addWithAttribute(ToolStats.ATTACK_DAMAGE, Attributes.ATTACK_DAMAGE);
      builder.add(ToolStats.ATTACK_SPEED);
    }
    if (TinkerTags.Items.HARVEST.contains(tool.getItem())) {
      builder.add(ToolStats.HARVEST_LEVEL);
      builder.add(ToolStats.MINING_SPEED);
    }

    builder.addFreeUpgrades();
    builder.addFreeAbilities();

    for (ModifierEntry entry : tool.getModifierList()) {
      entry.getModifier().addInformation(tool, entry.getLevel(), tooltip, isAdvanced, detailed);
    }

    return builder.getTooltips();
  }

  @Override
  public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
    if (this.isInGroup(group)) {
      this.addDefaultSubItems(items);
    }
  }

  /** Adds all default sub items */
  protected void addDefaultSubItems(List<ItemStack> items, IMaterial... fixedMaterials) {
    if (MaterialRegistry.isFullyLoaded()) {
      // if a specific material is set, show just that
      String showOnlyId = Config.COMMON.showOnlyToolMaterial.get();
      boolean added = false;
      if (!showOnlyId.isEmpty()) {
        MaterialId materialId = MaterialId.tryCreate(showOnlyId);
        if (materialId != null) {
          IMaterial material = MaterialRegistry.getMaterial(materialId);
          if (material != IMaterial.UNKNOWN) {
            if (addSubItem(items, material, fixedMaterials)) {
              added = true;
            }
          }
        }
      }
      // if the material was not applicable or we do not have a filter set, search the rest
      if (!added) {
        for (IMaterial material : MaterialRegistry.getInstance().getVisibleMaterials()) {
          // if we added it and we want a single material, we are done
          if (addSubItem(items, material, fixedMaterials) && !showOnlyId.isEmpty()) {
            break;
          }
        }
      }
    }
  }

  /** Makes a single sub item for the given materials */
  protected boolean addSubItem(List<ItemStack> items, IMaterial material, IMaterial[] fixedMaterials) {
    List<IToolPart> required = this.getToolDefinition().getRequiredComponents();
    List<IMaterial> materials = new ArrayList<>(required.size());
    for (int i = 0; i < required.size(); i++) {
      if (fixedMaterials.length > i && fixedMaterials[i] != null && required.get(i).canUseMaterial(fixedMaterials[i])) {
        materials.add(fixedMaterials[i]);
      }
      else if (required.get(i).canUseMaterial(material)) {
        materials.add(material);
      } else {
        return false;
      }
    }
    items.add(ToolBuildHandler.buildItemFromMaterials(this, materials));
    return true;
  }

  /**
   * Checks if the list of materials are valid for the stack
   * @param stack  Tool stack instance
   * @return  True if the stack has valid materials
   */
  public boolean hasValidMaterials(ItemStack stack) {
    // checks if the materials used support all stats needed
    List<IMaterial> materials = ToolStack.from(stack).getMaterialsList();

    // something went wrong
    if (materials.size() != this.getToolDefinition().getRequiredComponents().size()) {
      return false;
    }

    // check if all materials used have the stats needed
    List<IToolPart> requirements = getToolDefinition().getRequiredComponents();
    for (int i = 0; i < materials.size(); i++) {
      IMaterial material = materials.get(i);
      if (!requirements.get(i).canUseMaterial(material)) {
        return false;
      }
    }

    return true;
  }
  
  @Override
  public boolean isEnchantable(ItemStack stack) {
    return false;
  }

  @Override
  public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
    return false;
  }

  @Override
  public ITextComponent getDisplayName(ItemStack stack) {
    // if the tool is not named we use the repair tools for a prefix like thing
    List<IMaterial> materials = ToolStack.from(stack).getMaterialsList();
    List<IToolPart> components = getToolDefinition().getRequiredComponents();
    // we save all the ones for the name in a set so we don't have the same material in it twice
    Set<IMaterial> nameMaterials = Sets.newLinkedHashSet();

    if (materials.size() == components.size()) {
      for (int i = 0; i < components.size(); i++) {
        if (i < materials.size() && MaterialRegistry.getInstance().canRepair(components.get(i).getStatType())) {
          nameMaterials.add(materials.get(i));
        }
      }
    }

    return ToolCore.getCombinedItemName(super.getDisplayName(stack), nameMaterials);
  }

  /**
   * Combines the given display name with the material names to form the new given name
   *
   * @param itemName the standard display name
   * @param materials the list of materials
   * @return the combined item name
   */
  public static ITextComponent getCombinedItemName(ITextComponent itemName, Collection<IMaterial> materials) {
    if (materials.isEmpty() || materials.stream().allMatch(IMaterial.UNKNOWN::equals)) {
      return itemName;
    }

    if (materials.size() == 1) {
      IMaterial material = materials.iterator().next();

      if (Util.canTranslate(material.getTranslationKey() + ".format")) {
        return new TranslationTextComponent(material.getTranslationKey() + ".format", itemName);
      }

      return new TranslationTextComponent(materials.iterator().next().getTranslationKey()).append(new StringTextComponent(" ")).append(itemName);
    }

    // multiple materials. we'll have to combine
    StringTextComponent name = new StringTextComponent("");

    Iterator<IMaterial> iter = materials.iterator();

    IMaterial material = iter.next();
    name.append(new TranslationTextComponent(material.getTranslationKey()));

    while (iter.hasNext()) {
      material = iter.next();
      name.appendString("-").append(new TranslationTextComponent(material.getTranslationKey()));
    }

    name.appendString(" ").append(itemName);

    return name;
  }

  /**
   * Builds a tool meant for rendering in a screen
   *
   * @return the tool to use for rendering
   */
  public ItemStack buildToolForRendering() {
    if (toolForRendering == null) {
      toolForRendering = ToolBuildHandler.buildToolForRendering(this, this.getToolDefinition());
    }
    return toolForRendering;
  }


  /* NBT loading */

  @Override
  public boolean updateItemStackNBT(CompoundNBT nbt) {
    // when the itemstack is loaded from NBT we recalculate all the data
    // stops things from being wrong if modifiers or materials change
    ToolStack tool = ToolStack.from(this, getToolDefinition(), nbt.getCompound("tag"));
    tool.rebuildStats();

    // return value shouldn't matter since it's never checked
    return true;
  }

  /* Misc */

  /**
   * Creates a raytrace and casts it to a BlockRayTraceResult
   *
   * @param worldIn the world
   * @param player the given player
   * @param fluidMode the fluid mode to use for the raytrace event
   *
   * @return  Raytrace
   */
  public static BlockRayTraceResult blockRayTrace(World worldIn, PlayerEntity player, RayTraceContext.FluidMode fluidMode) {
    return Item.rayTrace(worldIn, player, fluidMode);
  }

  @Override
  public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
    return shouldCauseReequipAnimation(oldStack, newStack, false);
  }

  @Override
  public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
    if (oldStack == newStack) {
      return false;
    }
    // basic changes
    if (slotChanged || oldStack.getItem() != newStack.getItem()) {
      return true;
    }

    // if the tool props changed,
    ToolStack oldTool = ToolStack.from(oldStack);
    ToolStack newTool = ToolStack.from(newStack);

    // check if modifiers or materials changed
    if (!oldTool.getMaterialsList().equals(newTool.getMaterialsList())) {
      return true;
    }
    if (!oldTool.getModifierList().equals(newTool.getModifierList())) {
      return true;
    }

    // if the attributes changed, reequip
    Multimap<Attribute, AttributeModifier> attributesNew = newStack.getAttributeModifiers(EquipmentSlotType.MAINHAND);
    Multimap<Attribute, AttributeModifier> attributesOld = oldStack.getAttributeModifiers(EquipmentSlotType.MAINHAND);
    if (attributesNew.size() != attributesOld.size()) {
      return true;
    }
    for (Attribute attribute : attributesOld.keySet()) {
      if (!attributesNew.containsKey(attribute)) {
        return true;
      }
      Iterator<AttributeModifier> iter1 = attributesNew.get(attribute).iterator();
      Iterator<AttributeModifier> iter2 = attributesOld.get(attribute).iterator();
      while (iter1.hasNext() && iter2.hasNext()) {
        if (!iter1.next().equals(iter2.next())) {
          return true;
        }
      }
    }
    // no changes, no reequip
    return false;
  }

  @Nullable
  @Override
  public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
    return new ToolCapabilityProvider(stack);
  }
}
