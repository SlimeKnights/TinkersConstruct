package slimeknights.tconstruct.library.tools.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import lombok.Getter;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.IndestructibleItemEntity;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.helper.TooltipUtil;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.TooltipFlag;
import slimeknights.tconstruct.library.utils.TooltipKey;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

// note for self later, we do need to extend armor item. looks like we need it for:
// * mobs exchanging equiptment
// * rendering, I see...
public class ModifiableArmorItem extends ArmorItem implements IModifiableDisplay {
  // TODO: AT this
  private static final UUID[] ARMOR_MODIFIERS = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};

  /** Volatile modifier tag to make piglins neutal when worn */
  public static final ResourceLocation PIGLIN_NEUTRAL = TConstruct.getResource("piglin_neutral");

  @Getter
  private final ToolDefinition toolDefinition;
  /** Cache of the tool built for rendering */
  private ItemStack toolForRendering = null;
  public ModifiableArmorItem(IArmorMaterial materialIn, EquipmentSlotType slot, Properties builderIn, ToolDefinition toolDefinition) {
    super(materialIn, slot, builderIn);
    this.toolDefinition = toolDefinition;
  }

  public ModifiableArmorItem(ModifiableArmorMaterial material, EquipmentSlotType slotType, Properties properties) {
    this(material, slotType, properties, Objects.requireNonNull(material.getArmorDefinition(slotType), "Missing tool definition for " + slotType));
  }

  /* Basic properties */

  @Override
  public int getItemStackLimit(ItemStack stack) {
    return 1;
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
  public boolean makesPiglinsNeutral(ItemStack stack, LivingEntity wearer) {
    return ToolStack.from(stack).getVolatileData().getBoolean(PIGLIN_NEUTRAL);
  }


  /* Loading */

  @Nullable
  @Override
  public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
    return new ToolCapabilityProvider(stack);
  }

  @Override
  public boolean updateItemStackNBT(CompoundNBT nbt) {
    // when the itemstack is loaded from NBT we recalculate all the data
    // stops things from being wrong if modifiers or materials change
    ToolStack.from(this, getToolDefinition(), nbt.getCompound("tag")).rebuildStats();
    // return value shouldn't matter since it's never checked
    return true;
  }

  @Override
  public void onCreated(ItemStack stack, World worldIn, PlayerEntity playerIn) {
    ToolStack.ensureInitialized(stack, getToolDefinition());
  }


  /* Display */

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


  /* Indestructible items */

  @Override
  public boolean hasCustomEntity(ItemStack stack) {
    return ToolStack.from(stack).getVolatileData().getBoolean(INDESTRUCTIBLE_ENTITY);
  }

  @Override
  public Entity createEntity(World world, Entity original, ItemStack stack) {
    if (ToolStack.from(stack).getVolatileData().getBoolean(INDESTRUCTIBLE_ENTITY)) {
      IndestructibleItemEntity entity = new IndestructibleItemEntity(world, original.getPosX(), original.getPosY(), original.getPosZ(), stack);
      entity.setPickupDelayFrom(original);
      return entity;
    }
    return null;
  }


  /* Damage/Durability */

  @Override
  public boolean isRepairable(ItemStack stack) {
    // handle in the tinker station
    return false;
  }

  @Override
  public boolean isDamageable() {
    return true;
  }

  @Override
  public int getMaxDamage(ItemStack stack) {
    if (!isDamageable()) {
      return 0;
    }
    ToolStack tool = ToolStack.from(stack);
    int durability = tool.getStats().getInt(ToolStats.DURABILITY);
    // vanilla deletes tools if max damage == getDamage, so tell vanilla our max is one higher when broken
    return tool.isBroken() ? durability + 1 : durability;
  }

  @Override
  public int getDamage(ItemStack stack) {
    if (!isDamageable()) {
      return 0;
    }
    return ToolStack.from(stack).getDamage();
  }

  @Override
  public void setDamage(ItemStack stack, int damage) {
    if (isDamageable()) {
      ToolStack.from(stack).setDamage(damage);
    }
  }

  @Override
  public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T damager, Consumer<T> onBroken) {
    // We basically emulate Itemstack.damageItem here. We always return 0 to skip the handling in ItemStack.
    // If we don't tools ignore our damage logic
    if (isDamageable() && ToolDamageUtil.damage(ToolStack.from(stack), amount, damager, stack)) {
      onBroken.accept(damager);
    }

    return 0;
  }


  /* Durability display */

  @Override
  public boolean showDurabilityBar(ItemStack stack) {
    return ToolDamageUtil.showDurabilityBar(stack);
  }

  @Override
  public double getDurabilityForDisplay(ItemStack stack) {
    return ToolDamageUtil.getDamageForDisplay(stack);
  }

  @Override
  public int getRGBDurabilityForDisplay(ItemStack stack) {
    return ToolDamageUtil.getRGBDurabilityForDisplay(stack);
  }


  /* Armor properties */

  @Override
  public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
    return false;
  }

  @Override
  public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
    CompoundNBT nbt = stack.getTag();
    if (slot != getEquipmentSlot() || nbt == null || nbt.getBoolean(TooltipUtil.KEY_DISPLAY)) {
      return ImmutableMultimap.of();
    }

    ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
    ToolStack tool = ToolStack.from(stack);
    if (!tool.isBroken()) {
      // base stats
      StatsNBT statsNBT = tool.getStats();
      UUID uuid = ARMOR_MODIFIERS[slot.getIndex()];
      builder.put(Attributes.ARMOR, new AttributeModifier(uuid, "tconstruct.armor.armor", statsNBT.getFloat(ToolStats.ARMOR), AttributeModifier.Operation.ADDITION));
      builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "tconstruct.armor.toughness", statsNBT.getFloat(ToolStats.ARMOR_TOUGHNESS), AttributeModifier.Operation.ADDITION));
      double knockbackResistance = statsNBT.getFloat(ToolStats.KNOCKBACK_RESISTANCE);
      if (knockbackResistance != 0) {
        builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(uuid, "tconstruct.armor.knockback_resistance", knockbackResistance, AttributeModifier.Operation.ADDITION));
      }
      // grab attributes from modifiers
      BiConsumer<Attribute,AttributeModifier> attributeConsumer = builder::put;
      for (ModifierEntry entry : tool.getModifierList()) {
        entry.getModifier().addAttributes(tool, entry.getLevel(), slot, attributeConsumer);
      }
    }

    return builder.build();
  }


  /* Ticking */

  @Override
  public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
    super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);

    // don't care about non-living, they skip most tool context
    if (entityIn instanceof LivingEntity) {
      ToolStack tool = ToolStack.from(stack);
      List<ModifierEntry> modifiers = tool.getModifierList();
      if (!modifiers.isEmpty()) {
        LivingEntity living = (LivingEntity) entityIn;
        boolean isCorrectSlot = living.getItemStackFromSlot(slot) == stack;
        // we pass in the stack for most custom context, but for the sake of armor its easier to tell them that this is the correct slot for effects
        for (ModifierEntry entry : modifiers) {
          entry.getModifier().onInventoryTick(tool, entry.getLevel(), worldIn, living, itemSlot, isSelected, isCorrectSlot, stack);
        }
      }
    }
  }


  /* Tooltips */

  @Override
  public ITextComponent getDisplayName(ItemStack stack) {
    return TooltipUtil.getDisplayName(stack, getToolDefinition());
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    TooltipUtil.addInformation(this, stack, tooltip, TooltipKey.fromScreen(), flagIn == TooltipFlags.ADVANCED);
  }

  @Override
  public List<ITextComponent> getStatInformation(IModifierToolStack tool, List<ITextComponent> tooltips, TooltipFlag tooltipFlag) {
    return TooltipUtil.getArmorStats(tool, tooltips, tooltipFlag);
  }

  /* Display items */

  @Override
  public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
    if (this.isInGroup(group)) {
      ToolBuildHandler.addDefaultSubItems(this, items);
    }
  }

  @Override
  public ItemStack getRenderTool() {
    if (toolForRendering == null) {
      toolForRendering = ToolBuildHandler.buildToolForRendering(this, this.getToolDefinition());
    }
    return toolForRendering;
  }
}
