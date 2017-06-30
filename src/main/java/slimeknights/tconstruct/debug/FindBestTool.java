package slimeknights.tconstruct.debug;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialTypes;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.harvest.TinkerHarvestTools;

public class FindBestTool extends CommandBase {

  @Override
  public int getRequiredPermissionLevel() {
    return 0;
  }

  @Override
  public String getName() {
    return "findBestTool";
  }

  @Override
  public String getUsage(ICommandSender sender) {
    return "/findBestTool [number of tools to display] <filter type>";
  }

  @Override
  public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args)
      throws CommandException {
    int num, filtertype;
    if(args.length < 1) {
      throw new CommandException("Too few arguments");
    }
    else if(args.length < 2) {
      num = 100;
      filtertype = Integer.valueOf(args[0]);
    }
    else {
      num = Integer.valueOf(args[0]);
      filtertype = Integer.valueOf(args[1]);
    }

    Predicate<Triple<ItemStack, ImmutableList<Material>, Object[]>> filter[] = new Predicate[3];

    if(num < 0 || filtertype < 0 || filtertype > filter.length) {
      throw new CommandException("Inavlid arguments");
    }

    ToolCore tool = TinkerHarvestTools.pickaxe;
    List<Triple<ItemStack, ImmutableList<Material>, Object[]>> results = Lists.newArrayList();

    @SuppressWarnings("unchecked")
    Function<ItemStack, ?> functions[] = new Function[]{
        new Function<ItemStack, Integer>() {
          @Override
          public Integer apply(ItemStack itemStack) {
            return ToolHelper.getDurabilityStat(itemStack);
          }
        },
        new Function<ItemStack, Float>() {
          @Override
          public Float apply(ItemStack itemStack) {
            return ToolHelper.getMiningSpeedStat(itemStack);
          }
        },
        new Function<ItemStack, Float>() {
          @Override
          public Float apply(ItemStack itemStack) {
            return ToolHelper.getAttackStat(itemStack);
          }
        }
    };

    recurse(tool, ImmutableList.of(), results, functions);

    //Collections.sort(results, Comp.INSTANCE);

    List<Integer> durabilities = Lists.transform(results, new com.google.common.base.Function<Triple<ItemStack, ImmutableList<Material>, Object[]>, Integer>() {
      @Nullable
      @Override
      public Integer apply(Triple<ItemStack, ImmutableList<Material>, Object[]> input) {
        return (Integer) input.getRight()[0];
      }
    });

    List<Float> speeds = Lists.transform(results, new com.google.common.base.Function<Triple<ItemStack, ImmutableList<Material>, Object[]>, Float>() {
      @Nullable
      @Override
      public Float apply(Triple<ItemStack, ImmutableList<Material>, Object[]> input) {
        return (Float) input.getRight()[1];
      }
    });
    List<Float> attacks = Lists.transform(results, new com.google.common.base.Function<Triple<ItemStack, ImmutableList<Material>, Object[]>, Float>() {
      @Nullable
      @Override
      public Float apply(Triple<ItemStack, ImmutableList<Material>, Object[]> input) {
        return (Float) input.getRight()[2];
      }
    });

    Collection<Triple<ItemStack, ImmutableList<Material>, Object[]>> best;
    float percentile = 0.5f;

    int durPercentile = (int) getPercentile(Ordering.natural().reverse().sortedCopy(durabilities), percentile);
    float speedPercentile = (float) getPercentile(Ordering.natural().reverse().sortedCopy(speeds), percentile);
    float attackPercentile = (float) getPercentile(Ordering.natural().reverse().sortedCopy(attacks), percentile);

    do {
      percentile /= 2f;
      // calculate upper quartile of durability
      final int durPercentile2 = (int) getPercentile(Ordering.natural().reverse().sortedCopy(durabilities), percentile);
      final float speedPercentile2 = (float) getPercentile(Ordering.natural().reverse().sortedCopy(speeds), percentile);
      final float attackPercentile2 = (float) getPercentile(Ordering.natural().reverse().sortedCopy(attacks), percentile);
      durPercentile = durPercentile2;
      speedPercentile = speedPercentile2;
      attackPercentile = attackPercentile2;

      filter[0] = new Predicate<Triple<ItemStack, ImmutableList<Material>, Object[]>>() {

        @Override
        public boolean apply(@Nullable Triple<ItemStack, ImmutableList<Material>, Object[]> entry) {
          return ((Integer) entry.getRight()[0]) > durPercentile2
                 && ((Float) entry.getRight()[1]) > speedPercentile2;
        }
      };

      filter[1] = new Predicate<Triple<ItemStack, ImmutableList<Material>, Object[]>>() {

        @Override
        public boolean apply(@Nullable Triple<ItemStack, ImmutableList<Material>, Object[]> entry) {
          return ((Integer) entry.getRight()[0]) > durPercentile2
                 && ((Float) entry.getRight()[2]) > attackPercentile2;
        }
      };

      filter[2] = new Predicate<Triple<ItemStack, ImmutableList<Material>, Object[]>>() {

        @Override
        public boolean apply(@Nullable Triple<ItemStack, ImmutableList<Material>, Object[]> entry) {
          return ((Integer) entry.getRight()[0]) > durPercentile2
                 && ((Float) entry.getRight()[1]) > speedPercentile2
                 && ((Float) entry.getRight()[2]) > attackPercentile2;
        }
      };

      // get all tools that are above in both quartiles
      best = Collections2.filter(results, filter[filtertype]);
    } while(best.size() > num);

    sender.sendMessage(new TextComponentString(String.format("%d are in the top %d percentile of stats (%d; %f; %f)", best.size(), (int) (percentile * 100f), durPercentile, speedPercentile, attackPercentile)));

    Collection<Triple<ItemStack, ImmutableList<Material>, Object[]>> sortedDurability = new Ordering<Triple<ItemStack, ImmutableList<Material>, Object[]>>() {

      @Override
      public int compare(@Nullable Triple<ItemStack, ImmutableList<Material>, Object[]> left, @Nullable Triple<ItemStack, ImmutableList<Material>, Object[]> right) {
        return (Integer) right.getRight()[0] - (Integer) left.getRight()[0];
      }
    }.sortedCopy(best);

    Collection<Triple<ItemStack, ImmutableList<Material>, Object[]>> sortedSpeed = new Ordering<Triple<ItemStack, ImmutableList<Material>, Object[]>>() {

      @Override
      public int compare(@Nullable Triple<ItemStack, ImmutableList<Material>, Object[]> left, @Nullable Triple<ItemStack, ImmutableList<Material>, Object[]> right) {
        return (int) ((Float) right.getRight()[1] * 10f) - (int) ((Float) left.getRight()[1] * 10f);
      }
    }.sortedCopy(best);

    Collection<Triple<ItemStack, ImmutableList<Material>, Object[]>> sortedAttack = new Ordering<Triple<ItemStack, ImmutableList<Material>, Object[]>>() {

      @Override
      public int compare(@Nullable Triple<ItemStack, ImmutableList<Material>, Object[]> left, @Nullable Triple<ItemStack, ImmutableList<Material>, Object[]> right) {
        return (int) ((Float) right.getRight()[2] * 10f) - (int) ((Float) left.getRight()[2] * 10f);
      }
    }.sortedCopy(best);

    for(Triple<ItemStack, ImmutableList<Material>, Object[]> foo : best) {
      StringBuilder text = new StringBuilder();
      text.append("Materials: ");
      for(Material mat : foo.getMiddle()) {
        text.append(mat.getIdentifier());
        text.append(" ");
      }

      text.append("- ");
      text.append("Dur: ");
      text.append(foo.getRight()[0]);
      text.append(" Speed: ");
      text.append((Float) foo.getRight()[1] * tool.miningSpeedModifier());
      text.append(" Dmg: ");
      text.append((Float) foo.getRight()[2] * tool.damagePotential());

      sender.sendMessage(foo.getLeft().getTextComponent().appendSibling(new TextComponentString(text.toString())));
      //System.out.println(text.toString());
    }

    sender.sendMessage(new TextComponentString("Top 5 Durability:"));
    Iterator<Triple<ItemStack, ImmutableList<Material>, Object[]>> iter = sortedDurability.iterator();
    for(int i = 0; i < 5 && iter.hasNext(); i++) {
      Triple<ItemStack, ImmutableList<Material>, Object[]> foo = iter.next();
      StringBuilder text = new StringBuilder();

      text.append(foo.getRight()[0]);
      text.append(" - ");
      for(Material mat : foo.getMiddle()) {
        text.append(mat.getIdentifier());
        text.append(" ");
      }
      sender.sendMessage(foo.getLeft().getTextComponent().appendSibling(new TextComponentString(text.toString())));
    }

    sender.sendMessage(new TextComponentString("Top 5 Speed:"));
    iter = sortedSpeed.iterator();
    for(int i = 0; i < 5 && iter.hasNext(); i++) {
      Triple<ItemStack, ImmutableList<Material>, Object[]> foo = iter.next();
      StringBuilder text = new StringBuilder();

      text.append(foo.getRight()[1]);
      text.append(" - ");
      for(Material mat : foo.getMiddle()) {
        text.append(mat.getIdentifier());
        text.append(" ");
      }
      sender.sendMessage(foo.getLeft().getTextComponent().appendSibling(new TextComponentString(text.toString())));
    }

    sender.sendMessage(new TextComponentString("Top 5 Attack:"));
    iter = sortedAttack.iterator();
    for(int i = 0; i < 5 && iter.hasNext(); i++) {
      Triple<ItemStack, ImmutableList<Material>, Object[]> foo = iter.next();
      StringBuilder text = new StringBuilder();

      text.append(foo.getRight()[2]);
      text.append(" - ");
      for(Material mat : foo.getMiddle()) {
        text.append(mat.getIdentifier());
        text.append(" ");
      }
      sender.sendMessage(foo.getLeft().getTextComponent().appendSibling(new TextComponentString(text.toString())));
    }
  }

  public void recurse(ToolCore tool, ImmutableList<Material> materials, List<Triple<ItemStack, ImmutableList<Material>, Object[]>> results, Function<ItemStack, ?> fns[]) {
    // not enough materials yet, recurse
    if(tool.getRequiredComponents().size() > materials.size()) {
      for(Material mat : TinkerRegistry.getAllMaterials()) {
        if(!mat.hasStats(MaterialTypes.HEAD)) {
          continue;
        }
        ImmutableList.Builder<Material> mats = ImmutableList.builder();
        mats.addAll(materials);
        mats.add(mat);
        recurse(tool, mats.build(), results, fns);
      }
    }
    // enough materials, build it and do stuff with it!
    else {
      ItemStack stack = tool.buildItem(materials);
      Object[] values = new Object[fns.length];
      for(int i = 0; i < fns.length; i++) {
        values[i] = fns[i].apply(stack);
      }
      results.add(Triple.of(stack, materials, values));
    }
  }

  private enum Comp implements Comparator<Pair<ImmutableList<Material>, Integer>> {
    INSTANCE;

    @Override
    public int compare(Pair<ImmutableList<Material>, Integer> o1, Pair<ImmutableList<Material>, Integer> o2) {
      return o2.getRight() - o1.getRight();
    }
  }

  private <T extends Number> double getPercentile(List<T> entries, float percentile) {
    int coeff = (int) (1f / percentile);
    if(entries.size() % 2 == 1) {
      return entries.get(entries.size() / coeff).doubleValue();
    }

    T v1 = entries.get(entries.size() / coeff);
    T v2 = entries.get(entries.size() / coeff + 1);
    return ((v1.doubleValue() + v2.doubleValue()) / 2d);
  }
}
