/* Code for ctl and shift down from TicTooltips by squeek502
 * https://github.com/squeek502/TiC-Tooltips/blob/1.7.10/java/squeek/tictooltips/helpers/KeyHelper.java
 */

package slimeknights.tconstruct.library.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeI18n;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import slimeknights.mantle.util.DataLoadedConditionContext;
import slimeknights.tconstruct.TConstruct;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Util {
  public static final Marker TCONSTRUCT = MarkerManager.getMarker("TCONSTRUCT");

  public static final DecimalFormat COMMA_FORMAT = new DecimalFormat("#,###,###.##", DecimalFormatSymbols.getInstance(Locale.US));
  public static final DecimalFormat PERCENT_FORMAT = new DecimalFormat("#%");
  public static final DecimalFormat BONUS_FORMAT = new DecimalFormat("#.##");
  public static final DecimalFormat MULTIPLIER_FORMAT = new DecimalFormat("#.##x");
  public static final DecimalFormat PERCENT_BOOST_FORMAT = new DecimalFormat("#%");
  static {
    BONUS_FORMAT.setPositivePrefix("+");
    PERCENT_BOOST_FORMAT.setPositivePrefix("+");
  }

  /** Gets a logger for the given name */
  public static Logger getLogger(String type) {
    return LogManager.getLogger(TConstruct.MOD_ID + "-" + type);
  }

  /**
   * Gets the currently active mod, assuming its not Tinkers
   * @return  Currently active mod ID
   */
  public static Optional<String> getCurrentlyActiveExternalMod() {
    return Optional.ofNullable(ModLoadingContext.get().getActiveContainer().getModId())
      .filter(activeModId -> !TConstruct.MOD_ID.equals(activeModId));
  }

  /**
   * Checks if the given key can be translated
   * @param key  Key to check
   * @return  True if it can be translated
   */
  public static boolean canTranslate(String key) {
    return !ForgeI18n.getPattern(key).equals(key);
  }

  /**
   * Makes a translation key for the given name, redirect to the vanilla method
   * @param base  Base name, such as "block" or "gui"
   * @param name  Object name
   * @return  Translation key
   */
  public static String makeTranslationKey(String base, @Nullable ResourceLocation name) {
    return net.minecraft.Util.makeDescriptionId(base, name);
  }

  /**
   * Makes a translatable component for the given name, using {@link #makeTranslationKey(String, ResourceLocation)}.
   * @param base       Base name, such as "block" or "gui"
   * @param name       Object name
   * @param arguments  Arguments for translated component
   * @return  Translated component
   */
  public static Component makeTranslation(String base, @Nullable ResourceLocation name, Object... arguments) {
    return Component.translatable(makeTranslationKey(base, name), arguments);
  }

  /** Same as {@link net.minecraft.Util#make(Supplier)} */
  public static <T> T make(Supplier<T> supplier) {
    return supplier.get();
  }

  /** Same as {@link net.minecraft.Util#make(Object, Consumer)} */
  public static <T> T make(T object, Consumer<T> consumer) {
    consumer.accept(object);
    return object;
  }

  /**
   * Helper to create a indented list of entries in a single message.
   * Takes a list of objects, and converts them into a string with one entry on each line, prefixed by a tab for indentation.
   * The strings are created using the objects toString representation.
   *
   * @param list A list of objects to create a list of lines from
   * @return A single string with all entries seperated into a new line, and indented.
   */
  public static String toIndentedStringList(Collection<?> list) {
    return list.stream()
      .map(Object::toString)
      .collect(Collectors.joining("\n\t", "\n\t", ""));
  }

  /**
   * Gets the sign of a number
   * @param value  Number
   * @return  Sign
   */
  public static int sign(int value) {
    if (value == 0) {
      return 0;
    }
    return value > 0 ? 1 : -1;
  }

  /** Creates a new array by copying the first and appending all elements in the second. */
  @SafeVarargs
  public static <T> T[] append(T[] first, T... second) {
    T[] result = Arrays.copyOf(first, first.length + second.length);
    System.arraycopy(second, 0, result, first.length, second.length);
    return result;
  }

  /** Creates a new array by copying the second array and appending all elements in the first. */
  @SuppressWarnings("unchecked")
  @SafeVarargs
  public static <T> T[] prepend(T[] first, T... second) {
    T[] result = (T[]) Arrays.copyOf(second, first.length + second.length, first.getClass());
    System.arraycopy(first, 0, result, second.length, first.length);
    return result;
  }

  /**
   * Obtains a direction based on the difference between two positions
   * @param pos       Tile position
   * @param neighbor  Position of offset
   * @return  Direction, or down if missing
   */
  public static Direction directionFromOffset(BlockPos pos, BlockPos neighbor) {
    BlockPos offset = neighbor.subtract(pos);
    for (Direction direction : Direction.values()) {
      if (direction.getNormal().equals(offset)) {
        return direction;
      }
    }
    TConstruct.LOG.error("No direction for position {} and neighbor {}", pos, neighbor);
    return Direction.DOWN;
  }

  /** Converts an ARGB color to a ABGR color or vice versa */
  public static int translateColorBGR(int color) {
    return (color & 0xFF00FF00) | (((color & 0x00FF0000) >> 16) & 0x000000FF) | (((color & 0x000000FF) << 16) & 0x00FF0000);
  }

  /** Gets the slot type from a hand */
  public static EquipmentSlot getSlotType(InteractionHand hand) {
    return hand == InteractionHand.OFF_HAND ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
  }

  /** Gets the opposite hand of the given hand */
  public static InteractionHand getOpposite(InteractionHand hand) {
    return hand == InteractionHand.OFF_HAND ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
  }

  /** Converts a position and a side hit into a hit vector */
  public static Vec3 toHitVec(BlockPos pos, Direction sideHit) {
    return new Vec3(
      pos.getX() + 0.5D + sideHit.getStepX() * 0.5D,
      pos.getY() + 0.5D + sideHit.getStepY() * 0.5D,
      pos.getZ() + 0.5D + sideHit.getStepZ() * 0.5D
    );
  }

  /** Creates a block raytrace from the given position and side, targets the block center */
  public static BlockHitResult createTraceResult(BlockPos pos, Direction sideHit, boolean empty) {
    return new BlockHitResult(toHitVec(pos, empty ? sideHit.getOpposite() : sideHit), sideHit, pos, false);
  }

  /** Offsets a hit result to the given position. Comparable to {@link BlockHitResult#withPosition(BlockPos)} except it also offsets the hit location. */
  public static BlockHitResult offset(BlockHitResult hit, BlockPos offset) {
    BlockPos pos = hit.getBlockPos();
    if (pos.equals(offset)) {
      return hit;
    }
    return new BlockHitResult(hit.getLocation().add(offset.getX() - pos.getX(), offset.getY() - pos.getY(), offset.getZ() - pos.getZ()), hit.getDirection(), offset, hit.isInside());
  }

  /** Offsets a use context to the given position. */
  public static UseOnContext offset(UseOnContext context, BlockPos offset) {
    BlockPos pos = context.getClickedPos();
    if (pos.equals(offset)) {
      return context;
    }
    return new UseOnContext(context.getLevel(), context.getPlayer(), context.getHand(), context.getItemInHand(), offset(context.getHitResult(), offset));
  }

  /** Tests the given list of conditions using {@link DataLoadedConditionContext#INSTANCE} to see if all pass. */
  public static boolean testConditions(ICondition[] conditions) {
    for (ICondition condition : conditions) {
      if (!condition.test(DataLoadedConditionContext.INSTANCE)) {
        return false;
      }
    }
    return true;
  }

  /** Creates a new client block entity data packet with better generics than the vanilla method */
  public static <B extends BlockEntity> ClientboundBlockEntityDataPacket createBEPacket(B be, Function<? super B,CompoundTag> tagFunction) {
    return new ClientboundBlockEntityDataPacket(be.getBlockPos(), be.getType(), tagFunction.apply(be));
  }

  /** Cache of neo forge status, to make lookups faster in hot code */
  private static Boolean IS_NEO_FORGE = null;

  /** Checks if we are currently running on NeoForge as opposed to Forge. Allows branching solutions for each loader if needed */
  public static boolean isNeo() {
    if (IS_NEO_FORGE == null) {
      IS_NEO_FORGE = ModList.get().getModContainerById("forge").filter(mod -> mod.getModInfo().getDisplayName().equals("NeoForge")).isPresent();
    }
    return IS_NEO_FORGE;
  }

  /** Checks if we are currently running on Forge as opposed to NeoForge. Allows branching solutions for each loader if needed */
  public static boolean isForge() {
    return !isNeo();
  }
}
