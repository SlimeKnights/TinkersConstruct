/*      */ package net.bjculk.magic;
/*      */ 
/*      */ /*      */ import java.util.List;

/*      */ import net.minecraft.block.Block;
/*      */ import net.minecraft.block.BlockDoor;
/*      */ import net.minecraft.block.BlockPane;
/*      */ import net.minecraft.block.material.Material;
/*      */ import net.minecraft.creativetab.CreativeTabs;
/*      */ import net.minecraft.item.ItemStack;
/*      */ import net.minecraft.util.AxisAlignedBB;
/*      */ import net.minecraft.world.IBlockAccess;
/*      */ import net.minecraft.world.World;
/*      */ 
/*      */ public class MagicSlabBase extends Block
/*      */ {
/*      */   public MagicSlabBase(int i, Material material)
/*      */   {
/*   23 */     super(i, material);
/*   24 */     setCreativeTab(CreativeTabs.tabDecorations);
/*      */   }
/*      */ 
/*      */   public boolean canPlaceBlockAt(World world, int i, int j, int k)
/*      */   {
/*   29 */     return super.canPlaceBlockAt(world, i, j, k);
/*      */   }
/*      */ 
/*      */   public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
/*      */   {
/*   34 */     boolean east = canConnectSlabTo(world, x, y, z - 1);
/*   35 */     boolean west = canConnectSlabTo(world, x, y, z + 1);
/*   36 */     boolean south = canConnectSlabTo(world, x - 1, y, z);
/*   37 */     boolean north = canConnectSlabTo(world, x + 1, y, z);
/*   38 */     boolean below = canConnectSlabTo(world, x, y - 1, z);
/*   39 */     boolean above = canConnectSlabTo(world, x, y + 1, z);
/*      */ 
/*   41 */     boolean eastSlab = isSlab(world, x, y, z - 1);
/*   42 */     boolean westSlab = isSlab(world, x, y, z + 1);
/*   43 */     boolean southSlab = isSlab(world, x - 1, y, z);
/*   44 */     boolean northSlab = isSlab(world, x + 1, y, z);
/*   45 */     boolean belowSlab = isSlab(world, x, y - 1, z);
/*   46 */     boolean aboveSlab = isSlab(world, x, y + 1, z);
/*      */ 
/*   48 */     byte slabsEast = countNearbyBlocks(world, x, y, z - 1);
/*   49 */     byte slabsWest = countNearbyBlocks(world, x, y, z + 1);
/*   50 */     byte slabsSouth = countNearbyBlocks(world, x - 1, y, z);
/*   51 */     byte slabsNorth = countNearbyBlocks(world, x + 1, y, z);
/*   52 */     byte slabsBelow = countNearbyBlocks(world, x, y - 1, z);
/*   53 */     byte slabsAbove = countNearbyBlocks(world, x, y + 1, z);
/*      */ 
/*   55 */     boolean eastTube = checkForTube(world, x, y, z - 1);
/*   56 */     boolean westTube = checkForTube(world, x, y, z + 1);
/*   57 */     boolean southTube = checkForTube(world, x - 1, y, z);
/*   58 */     boolean northTube = checkForTube(world, x + 1, y, z);
/*   59 */     boolean belowTube = checkForTube(world, x, y - 1, z);
/*   60 */     boolean aboveTube = checkForTube(world, x, y + 1, z);
/*      */ 
/*   62 */     boolean eastSlabShape = checkForSlabShape(world, x, y, z - 1);
/*   63 */     boolean westSlabShape = checkForSlabShape(world, x, y, z + 1);
/*   64 */     boolean southSlabShape = checkForSlabShape(world, x - 1, y, z);
/*   65 */     boolean northSlabShape = checkForSlabShape(world, x + 1, y, z);
/*   66 */     boolean belowSlabShape = checkForSlabShape(world, x, y - 1, z);
/*   67 */     boolean aboveSlabShape = checkForSlabShape(world, x, y + 1, z);
/*      */ 
/*   69 */     float bottomHeight = 0.0F;
/*   70 */     float middleHeight = 0.5F;
/*   71 */     float topHeight = 1.0F;
/*   72 */     float lowOffset = 0.3125F;
/*   73 */     float highOffset = 0.6875F;
/*      */ 
/*   75 */     float bX = bottomHeight;
/*   76 */     float bY = bottomHeight;
/*   77 */     float bZ = bottomHeight;
/*   78 */     float tX = topHeight;
/*   79 */     float tY = topHeight;
/*   80 */     float tZ = topHeight;
/*      */ 
/*   82 */     byte num = 0;
/*   83 */     num = (byte)(num + (east ? 1 : 0));
/*   84 */     num = (byte)(num + (west ? 1 : 0));
/*   85 */     num = (byte)(num + (south ? 1 : 0));
/*   86 */     num = (byte)(num + (north ? 1 : 0));
/*   87 */     num = (byte)(num + (above ? 1 : 0));
/*   88 */     num = (byte)(num + (below ? 1 : 0));
/*      */ 
/*   90 */     byte numSlab = 0;
/*   91 */     numSlab = (byte)(numSlab + (eastSlab ? 1 : 0));
/*   92 */     numSlab = (byte)(numSlab + (westSlab ? 1 : 0));
/*   93 */     numSlab = (byte)(numSlab + (southSlab ? 1 : 0));
/*   94 */     numSlab = (byte)(numSlab + (northSlab ? 1 : 0));
/*   95 */     numSlab = (byte)(numSlab + (aboveSlab ? 1 : 0));
/*   96 */     numSlab = (byte)(numSlab + (belowSlab ? 1 : 0));
/*      */ 
/*   98 */     if (num == 0) {
/*   99 */       bX = 0.25F;
/*  100 */       bY = 0.25F;
/*  101 */       bZ = 0.25F;
/*  102 */       tX = 0.75F;
/*  103 */       tY = 0.75F;
/*  104 */       tZ = 0.75F;
/*      */     }
/*  107 */     else if (num == 1)
/*      */     {
/*  109 */       if (below)
/*  110 */         tY = middleHeight;
/*  111 */       if (above)
/*  112 */         bY = middleHeight;
/*  113 */       if (south)
/*  114 */         tX = middleHeight;
/*  115 */       if (north)
/*  116 */         bX = middleHeight;
/*  117 */       if (east)
/*  118 */         tZ = middleHeight;
/*  119 */       if (west) {
/*  120 */         bZ = middleHeight;
/*      */       }
/*      */ 
/*      */     }
/*  124 */     else if (num == 2)
/*      */     {
/*  127 */       if ((below) && (above)) {
/*  128 */         bX = lowOffset;
/*  129 */         bZ = lowOffset;
/*  130 */         tX = highOffset;
/*  131 */         tZ = highOffset;
/*      */       }
/*  134 */       else if ((east) && (west)) {
/*  135 */         bX = lowOffset;
/*  136 */         bY = lowOffset;
/*  137 */         tX = highOffset;
/*  138 */         tY = highOffset;
/*      */       }
/*  141 */       else if ((north) && (south)) {
/*  142 */         bY = lowOffset;
/*  143 */         bZ = lowOffset;
/*  144 */         tY = highOffset;
/*  145 */         tZ = highOffset;
/*      */       }
/*  148 */       else if (below)
/*      */       {
/*  150 */         if (south) {
/*  151 */           if (southTube) {
/*  152 */             tY = highOffset;
/*  153 */             bZ = lowOffset;
/*  154 */             tZ = highOffset;
/*      */           }
/*      */           else {
/*  157 */             tY = middleHeight;
/*  158 */           }if (belowTube) {
/*  159 */             tX = highOffset;
/*  160 */             bZ = lowOffset;
/*  161 */             tZ = highOffset;
/*      */           } else {
/*  163 */             tX = middleHeight;
/*      */           }
/*      */         }
/*      */ 
/*  167 */         if (north) {
/*  168 */           if (northTube) {
/*  169 */             tY = highOffset;
/*  170 */             bZ = lowOffset;
/*  171 */             tZ = highOffset;
/*      */           } else {
/*  173 */             tY = middleHeight;
/*  174 */           }if (belowTube) {
/*  175 */             bX = lowOffset;
/*  176 */             bZ = lowOffset;
/*  177 */             tZ = highOffset;
/*      */           } else {
/*  179 */             bX = middleHeight;
/*      */           }
/*      */         }
/*  182 */         if (east) {
/*  183 */           if (eastTube) {
/*  184 */             tY = highOffset;
/*  185 */             bX = lowOffset;
/*  186 */             tX = highOffset;
/*      */           } else {
/*  188 */             tY = middleHeight;
/*  189 */           }if (belowTube) {
/*  190 */             tZ = highOffset;
/*  191 */             bX = lowOffset;
/*  192 */             tX = highOffset;
/*      */           } else {
/*  194 */             tZ = middleHeight;
/*      */           }
/*      */         }
/*  197 */         if (west) {
/*  198 */           if (westTube) {
/*  199 */             tY = highOffset;
/*  200 */             bX = lowOffset;
/*  201 */             tX = highOffset;
/*      */           } else {
/*  203 */             tY = middleHeight;
/*  204 */           }if (belowTube) {
/*  205 */             bZ = lowOffset;
/*  206 */             bX = lowOffset;
/*  207 */             tX = highOffset;
/*      */           } else {
/*  209 */             bZ = middleHeight;
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*  214 */       else if (above)
/*      */       {
/*  216 */         if (south) {
/*  217 */           if (southTube) {
/*  218 */             bY = lowOffset;
/*  219 */             bZ = lowOffset;
/*  220 */             tZ = highOffset;
/*      */           }
/*      */           else {
/*  223 */             bY = middleHeight;
/*  224 */           }if (aboveTube) {
/*  225 */             tX = highOffset;
/*  226 */             bZ = lowOffset;
/*  227 */             tZ = highOffset;
/*      */           } else {
/*  229 */             tX = middleHeight;
/*      */           }
/*      */         }
/*      */ 
/*  233 */         if (north) {
/*  234 */           if (northTube) {
/*  235 */             bY = lowOffset;
/*  236 */             bZ = lowOffset;
/*  237 */             tZ = highOffset;
/*      */           } else {
/*  239 */             bY = middleHeight;
/*  240 */           }if (aboveTube) {
/*  241 */             bX = lowOffset;
/*  242 */             bZ = lowOffset;
/*  243 */             tZ = highOffset;
/*      */           } else {
/*  245 */             bX = middleHeight;
/*      */           }
/*      */         }
/*  248 */         if (east) {
/*  249 */           if (eastTube) {
/*  250 */             bY = lowOffset;
/*  251 */             bX = lowOffset;
/*  252 */             tX = highOffset;
/*      */           } else {
/*  254 */             bY = middleHeight;
/*  255 */           }if (aboveTube) {
/*  256 */             tZ = highOffset;
/*  257 */             bX = lowOffset;
/*  258 */             tX = highOffset;
/*      */           } else {
/*  260 */             tZ = middleHeight;
/*      */           }
/*      */         }
/*  263 */         if (west) {
/*  264 */           if (westTube) {
/*  265 */             bY = lowOffset;
/*  266 */             bX = lowOffset;
/*  267 */             tX = highOffset;
/*      */           } else {
/*  269 */             bY = middleHeight;
/*  270 */           }if (aboveTube) {
/*  271 */             bZ = lowOffset;
/*  272 */             bX = lowOffset;
/*  273 */             tX = highOffset;
/*      */           } else {
/*  275 */             bZ = middleHeight;
/*      */           }
/*      */         }
/*      */       }
/*  279 */       else if ((west) && (north)) {
/*  280 */         if (westTube) {
/*  281 */           bX = lowOffset;
/*  282 */           bY = lowOffset;
/*  283 */           tY = highOffset;
/*      */         } else {
/*  285 */           bX = middleHeight;
/*  286 */         }if (northTube) {
/*  287 */           bZ = lowOffset;
/*  288 */           bY = lowOffset;
/*  289 */           tY = highOffset;
/*      */         } else {
/*  291 */           bZ = middleHeight;
/*      */         }
/*      */       }
/*      */ 
/*  295 */       if ((west) && (south)) {
/*  296 */         if (westTube) {
/*  297 */           tX = highOffset;
/*  298 */           bY = lowOffset;
/*  299 */           tY = highOffset;
/*      */         } else {
/*  301 */           tX = middleHeight;
/*  302 */         }if (southTube) {
/*  303 */           bZ = lowOffset;
/*  304 */           bY = lowOffset;
/*  305 */           tY = highOffset;
/*      */         } else {
/*  307 */           bZ = middleHeight;
/*      */         }
/*      */ 
/*      */       }
/*  311 */       else if ((east) && (north)) {
/*  312 */         if (eastTube) {
/*  313 */           bX = lowOffset;
/*  314 */           bY = lowOffset;
/*  315 */           tY = highOffset;
/*      */         } else {
/*  317 */           bX = middleHeight;
/*  318 */         }if (northTube) {
/*  319 */           tZ = highOffset;
/*  320 */           bY = lowOffset;
/*  321 */           tY = highOffset;
/*      */         } else {
/*  323 */           tZ = middleHeight;
/*      */         }
/*      */ 
/*      */       }
/*  327 */       else if ((east) && (south)) {
/*  328 */         if (eastTube) {
/*  329 */           tX = highOffset;
/*  330 */           bY = lowOffset;
/*  331 */           tY = highOffset;
/*      */         } else {
/*  333 */           tX = middleHeight;
/*  334 */         }if (southTube) {
/*  335 */           tZ = highOffset;
/*  336 */           bY = lowOffset;
/*  337 */           tY = highOffset;
/*      */         } else {
/*  339 */           tZ = middleHeight;
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*  344 */     else if (num == 3)
/*      */     {
/*  346 */       if ((below) && (above))
/*      */       {
/*  348 */         if (north) {
/*  349 */           if ((belowTube) || (aboveTube))
/*  350 */             bX = lowOffset;
/*      */           else
/*  352 */             bX = middleHeight;
/*  353 */           bZ = lowOffset;
/*  354 */           tZ = highOffset;
/*      */         }
/*  357 */         else if (south) {
/*  358 */           if ((belowTube) || (aboveTube))
/*  359 */             tX = highOffset;
/*      */           else
/*  361 */             tX = middleHeight;
/*  362 */           bZ = lowOffset;
/*  363 */           tZ = highOffset;
/*      */         }
/*      */ 
/*  366 */         if (west) {
/*  367 */           bX = lowOffset;
/*  368 */           tX = highOffset;
/*  369 */           if ((belowTube) || (aboveTube))
/*  370 */             bZ = lowOffset;
/*      */           else {
/*  372 */             bZ = middleHeight;
/*      */           }
/*      */         }
/*  375 */         if (east) {
/*  376 */           bX = lowOffset;
/*  377 */           tX = highOffset;
/*  378 */           if ((belowTube) || (aboveTube))
/*  379 */             tZ = highOffset;
/*      */           else {
/*  381 */             tZ = middleHeight;
/*      */           }
/*      */         }
/*      */       }
/*  385 */       else if ((north) && (south))
/*      */       {
/*  387 */         if (above) {
/*  388 */           if ((northTube) || (southTube))
/*  389 */             bY = lowOffset;
/*      */           else
/*  391 */             bY = middleHeight;
/*  392 */           bZ = lowOffset;
/*  393 */           tZ = highOffset;
/*      */         }
/*  396 */         else if (below) {
/*  397 */           if ((northTube) || (southTube))
/*  398 */             tY = highOffset;
/*      */           else
/*  400 */             tY = middleHeight;
/*  401 */           bZ = lowOffset;
/*  402 */           tZ = highOffset;
/*      */         }
/*      */ 
/*  405 */         if (west) {
/*  406 */           bY = lowOffset;
/*  407 */           tY = highOffset;
/*  408 */           if ((northTube) || (southTube))
/*  409 */             bZ = lowOffset;
/*      */           else {
/*  411 */             bZ = middleHeight;
/*      */           }
/*      */         }
/*  414 */         if (east) {
/*  415 */           bY = lowOffset;
/*  416 */           tY = highOffset;
/*  417 */           if ((northTube) || (southTube))
/*  418 */             tZ = highOffset;
/*      */           else {
/*  420 */             tZ = middleHeight;
/*      */           }
/*      */         }
/*      */       }
/*  424 */       else if ((east) && (west))
/*      */       {
/*  426 */         if (north) {
/*  427 */           if ((eastTube) || (westTube))
/*  428 */             bX = lowOffset;
/*      */           else
/*  430 */             bX = middleHeight;
/*  431 */           bY = lowOffset;
/*  432 */           tY = highOffset;
/*      */         }
/*  435 */         else if (south) {
/*  436 */           if ((eastTube) || (westTube))
/*  437 */             tX = highOffset;
/*      */           else
/*  439 */             tX = middleHeight;
/*  440 */           bY = lowOffset;
/*  441 */           tY = highOffset;
/*      */         }
/*      */ 
/*  444 */         if (above) {
/*  445 */           bX = lowOffset;
/*  446 */           tX = highOffset;
/*  447 */           if ((eastTube) || (westTube))
/*  448 */             bY = lowOffset;
/*      */           else {
/*  450 */             bY = middleHeight;
/*      */           }
/*      */         }
/*  453 */         if (below) {
/*  454 */           bX = lowOffset;
/*  455 */           tX = highOffset;
/*  456 */           if ((eastTube) || (westTube))
/*  457 */             tY = highOffset;
/*      */           else {
/*  459 */             tY = middleHeight;
/*      */           }
/*      */         }
/*      */       }
/*  463 */       else if (above)
/*      */       {
/*  465 */         bY = middleHeight;
/*  466 */         if (north) {
/*  467 */           bX = middleHeight;
/*  468 */           if (east)
/*  469 */             tZ = middleHeight;
/*  470 */           if (west) {
/*  471 */             bZ = middleHeight;
/*      */           }
/*      */         }
/*  474 */         else if (south) {
/*  475 */           tX = middleHeight;
/*  476 */           if (east)
/*  477 */             tZ = middleHeight;
/*  478 */           if (west) {
/*  479 */             bZ = middleHeight;
/*      */           }
/*      */         }
/*      */       }
/*  483 */       else if (below)
/*      */       {
/*  485 */         tY = middleHeight;
/*  486 */         if (north) {
/*  487 */           bX = middleHeight;
/*  488 */           if (east)
/*  489 */             tZ = middleHeight;
/*  490 */           if (west) {
/*  491 */             bZ = middleHeight;
/*      */           }
/*      */         }
/*  494 */         else if (south) {
/*  495 */           tX = middleHeight;
/*  496 */           if (east)
/*  497 */             tZ = middleHeight;
/*  498 */           if (west) {
/*  499 */             bZ = middleHeight;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  504 */     else if (num == 4)
/*      */     {
/*  506 */       if ((above) && (below)) {
/*  507 */         if ((east) && (west))
/*      */         {
/*  509 */           bX = lowOffset;
/*  510 */           tX = highOffset;
/*      */         }
/*  513 */         else if ((north) && (south)) {
/*  514 */           bZ = lowOffset;
/*  515 */           tZ = highOffset;
/*      */         }
/*  518 */         else if (north)
/*      */         {
/*  520 */           bX = middleHeight;
/*  521 */           if (east)
/*  522 */             tZ = middleHeight;
/*  523 */           if (west) {
/*  524 */             bZ = middleHeight;
/*      */           }
/*      */         }
/*  527 */         else if (south)
/*      */         {
/*  529 */           tX = middleHeight;
/*  530 */           if (east)
/*  531 */             tZ = middleHeight;
/*  532 */           if (west) {
/*  533 */             bZ = middleHeight;
/*      */           }
/*      */         }
/*      */       }
/*  537 */       else if ((north) && (south))
/*      */       {
/*  539 */         if ((!above) && (!below)) {
/*  540 */           bY = lowOffset;
/*  541 */           tY = highOffset;
/*      */         }
/*      */ 
/*  544 */         if (above)
/*      */         {
/*  546 */           bY = middleHeight;
/*  547 */           if (east)
/*  548 */             tZ = middleHeight;
/*  549 */           if (west) {
/*  550 */             bZ = middleHeight;
/*      */           }
/*      */         }
/*  553 */         else if (below)
/*      */         {
/*  555 */           tY = middleHeight;
/*  556 */           if (east)
/*  557 */             tZ = middleHeight;
/*  558 */           if (west) {
/*  559 */             bZ = middleHeight;
/*      */           }
/*      */         }
/*      */       }
/*  563 */       else if ((east) && (west)) {
/*  564 */         if (above)
/*      */         {
/*  566 */           bY = middleHeight;
/*  567 */           if (south)
/*  568 */             tX = middleHeight;
/*  569 */           if (north) {
/*  570 */             bX = middleHeight;
/*      */           }
/*      */         }
/*  573 */         else if (below)
/*      */         {
/*  575 */           tY = middleHeight;
/*  576 */           if (south)
/*  577 */             tX = middleHeight;
/*  578 */           if (north) {
/*  579 */             bX = middleHeight;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  584 */     else if (num == 5)
/*      */     {
/*  586 */       if (!below)
/*      */       {
/*  588 */         if ((northTube) || (southTube) || (eastTube) || (westTube) || (northSlabShape) || (southSlabShape) || (eastSlabShape) || (westSlabShape))
/*      */         {
/*  590 */           bY = lowOffset;
/*      */         }
/*      */         else {
/*  593 */           bY = middleHeight;
/*      */         }
/*      */ 
/*  596 */         if (slabsAbove == 2)
/*      */         {
/*  598 */           tY = highOffset;
/*      */ 
/*  602 */           bX = lowOffset;
/*  603 */           tX = highOffset;
/*  604 */           bZ = lowOffset;
/*  605 */           tZ = highOffset;
/*  606 */           bY = highOffset;
/*  607 */           tY = topHeight;
/*      */         }
/*      */       }
/*  610 */       if (!above)
/*      */       {
/*  612 */         if ((northTube) || (southTube) || (eastTube) || (westTube) || (northSlabShape) || (southSlabShape) || (eastSlabShape) || (westSlabShape))
/*      */         {
/*  614 */           tY = highOffset;
/*      */         }
/*      */         else {
/*  617 */           tY = middleHeight;
/*      */         }
/*      */ 
/*  620 */         if (slabsBelow == 2)
/*      */         {
/*  622 */           bY = lowOffset;
/*      */ 
/*  627 */           bX = lowOffset;
/*  628 */           tX = highOffset;
/*  629 */           bZ = lowOffset;
/*  630 */           tZ = highOffset;
/*  631 */           tY = lowOffset;
/*  632 */           bY = bottomHeight;
/*      */         }
/*      */       }
/*  635 */       if (!south)
/*      */       {
/*  637 */         if ((aboveTube) || (belowTube) || (eastTube) || (westTube) || (aboveSlabShape) || (belowSlabShape) || (eastSlabShape) || (westSlabShape))
/*      */         {
/*  639 */           bX = lowOffset;
/*      */         }
/*      */         else {
/*  642 */           bX = middleHeight;
/*      */         }
/*      */ 
/*  645 */         if (slabsNorth == 2)
/*      */         {
/*  647 */           tX = highOffset;
/*      */ 
/*  651 */           bY = lowOffset;
/*  652 */           tY = highOffset;
/*  653 */           bZ = lowOffset;
/*  654 */           tZ = highOffset;
/*  655 */           bX = highOffset;
/*  656 */           tX = topHeight;
/*      */         }
/*      */       }
/*  659 */       if (!north)
/*      */       {
/*  662 */         if ((aboveTube) || (belowTube) || (eastTube) || (westTube) || (aboveSlabShape) || (belowSlabShape) || (eastSlabShape) || (westSlabShape))
/*      */         {
/*  664 */           tX = highOffset;
/*      */         }
/*      */         else {
/*  667 */           tX = middleHeight;
/*      */         }
/*      */ 
/*  670 */         if (slabsSouth == 2)
/*      */         {
/*  672 */           bX = lowOffset;
/*      */ 
/*  676 */           bY = lowOffset;
/*  677 */           tY = highOffset;
/*  678 */           bZ = lowOffset;
/*  679 */           tZ = highOffset;
/*  680 */           tX = lowOffset;
/*  681 */           bX = bottomHeight;
/*      */         }
/*      */       }
/*  684 */       if (!east)
/*      */       {
/*  687 */         if ((northTube) || (southTube) || (aboveTube) || (belowTube) || (northSlabShape) || (southSlabShape) || (aboveSlabShape) || (belowSlabShape))
/*      */         {
/*  689 */           bZ = lowOffset;
/*      */         }
/*      */         else {
/*  692 */           bZ = middleHeight;
/*      */         }
/*      */ 
/*  695 */         if (slabsWest == 2)
/*      */         {
/*  697 */           tZ = highOffset;
/*      */ 
/*  701 */           bY = lowOffset;
/*  702 */           tY = highOffset;
/*  703 */           bX = lowOffset;
/*  704 */           tX = highOffset;
/*  705 */           bZ = highOffset;
/*  706 */           tZ = topHeight;
/*      */         }
/*      */       }
/*  709 */       if (!west)
/*      */       {
/*  711 */         if ((northTube) || (southTube) || (aboveTube) || (belowTube) || (northSlabShape) || (southSlabShape) || (aboveSlabShape) || (belowSlabShape))
/*      */         {
/*  713 */           tZ = highOffset;
/*      */         }
/*      */         else {
/*  716 */           tZ = middleHeight;
/*      */         }
/*      */ 
/*  719 */         if (slabsEast == 2)
/*      */         {
/*  721 */           bZ = lowOffset;
/*      */ 
/*  725 */           bX = lowOffset;
/*  726 */           tX = highOffset;
/*  727 */           bY = lowOffset;
/*  728 */           tY = highOffset;
/*  729 */           bZ = bottomHeight;
/*  730 */           tZ = lowOffset;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  735 */     return AxisAlignedBB.getBoundingBox(x + bX, y + bY, z + bZ, x + tX, y + tY, z + tZ);
/*      */   }
/*      */ 
/*      */   public void setBlockBoundsBasedOnState(IBlockAccess iblockaccess, int x, int y, int z)
/*      */   {
/*  741 */     boolean east = canConnectSlabTo(iblockaccess, x, y, z - 1);
/*  742 */     boolean west = canConnectSlabTo(iblockaccess, x, y, z + 1);
/*  743 */     boolean south = canConnectSlabTo(iblockaccess, x - 1, y, z);
/*  744 */     boolean north = canConnectSlabTo(iblockaccess, x + 1, y, z);
/*  745 */     boolean below = canConnectSlabTo(iblockaccess, x, y - 1, z);
/*  746 */     boolean above = canConnectSlabTo(iblockaccess, x, y + 1, z);
/*      */ 
/*  748 */     boolean eastSlab = isSlab(iblockaccess, x, y, z - 1);
/*  749 */     boolean westSlab = isSlab(iblockaccess, x, y, z + 1);
/*  750 */     boolean southSlab = isSlab(iblockaccess, x - 1, y, z);
/*  751 */     boolean northSlab = isSlab(iblockaccess, x + 1, y, z);
/*  752 */     boolean belowSlab = isSlab(iblockaccess, x, y - 1, z);
/*  753 */     boolean aboveSlab = isSlab(iblockaccess, x, y + 1, z);
/*      */ 
/*  755 */     byte slabsEast = countNearbyBlocks(iblockaccess, x, y, z - 1);
/*  756 */     byte slabsWest = countNearbyBlocks(iblockaccess, x, y, z + 1);
/*  757 */     byte slabsSouth = countNearbyBlocks(iblockaccess, x - 1, y, z);
/*  758 */     byte slabsNorth = countNearbyBlocks(iblockaccess, x + 1, y, z);
/*  759 */     byte slabsBelow = countNearbyBlocks(iblockaccess, x, y - 1, z);
/*  760 */     byte slabsAbove = countNearbyBlocks(iblockaccess, x, y + 1, z);
/*      */ 
/*  762 */     boolean eastTube = checkForTube(iblockaccess, x, y, z - 1);
/*  763 */     boolean westTube = checkForTube(iblockaccess, x, y, z + 1);
/*  764 */     boolean southTube = checkForTube(iblockaccess, x - 1, y, z);
/*  765 */     boolean northTube = checkForTube(iblockaccess, x + 1, y, z);
/*  766 */     boolean belowTube = checkForTube(iblockaccess, x, y - 1, z);
/*  767 */     boolean aboveTube = checkForTube(iblockaccess, x, y + 1, z);
/*      */ 
/*  769 */     boolean eastSlabShape = checkForSlabShape(iblockaccess, x, y, z - 1);
/*  770 */     boolean westSlabShape = checkForSlabShape(iblockaccess, x, y, z + 1);
/*  771 */     boolean southSlabShape = checkForSlabShape(iblockaccess, x - 1, y, z);
/*  772 */     boolean northSlabShape = checkForSlabShape(iblockaccess, x + 1, y, z);
/*  773 */     boolean belowSlabShape = checkForSlabShape(iblockaccess, x, y - 1, z);
/*  774 */     boolean aboveSlabShape = checkForSlabShape(iblockaccess, x, y + 1, z);
/*      */ 
/*  776 */     float bottomHeight = 0.0F;
/*  777 */     float middleHeight = 0.5F;
/*  778 */     float topHeight = 1.0F;
/*  779 */     float lowOffset = 0.3125F;
/*  780 */     float highOffset = 0.6875F;
/*      */ 
/*  782 */     float bX = bottomHeight;
/*  783 */     float bY = bottomHeight;
/*  784 */     float bZ = bottomHeight;
/*  785 */     float tX = topHeight;
/*  786 */     float tY = topHeight;
/*  787 */     float tZ = topHeight;
/*      */ 
/*  789 */     byte num = 0;
/*  790 */     num = (byte)(num + (east ? 1 : 0));
/*  791 */     num = (byte)(num + (west ? 1 : 0));
/*  792 */     num = (byte)(num + (south ? 1 : 0));
/*  793 */     num = (byte)(num + (north ? 1 : 0));
/*  794 */     num = (byte)(num + (above ? 1 : 0));
/*  795 */     num = (byte)(num + (below ? 1 : 0));
/*      */ 
/*  797 */     byte numSlab = 0;
/*  798 */     numSlab = (byte)(numSlab + (eastSlab ? 1 : 0));
/*  799 */     numSlab = (byte)(numSlab + (westSlab ? 1 : 0));
/*  800 */     numSlab = (byte)(numSlab + (southSlab ? 1 : 0));
/*  801 */     numSlab = (byte)(numSlab + (northSlab ? 1 : 0));
/*  802 */     numSlab = (byte)(numSlab + (aboveSlab ? 1 : 0));
/*  803 */     numSlab = (byte)(numSlab + (belowSlab ? 1 : 0));
/*      */ 
/*  805 */     if (num == 0) {
/*  806 */       bX = 0.25F;
/*  807 */       bY = 0.25F;
/*  808 */       bZ = 0.25F;
/*  809 */       tX = 0.75F;
/*  810 */       tY = 0.75F;
/*  811 */       tZ = 0.75F;
/*      */     }
/*  814 */     else if (num == 1)
/*      */     {
/*  816 */       if (below)
/*  817 */         tY = middleHeight;
/*  818 */       if (above)
/*  819 */         bY = middleHeight;
/*  820 */       if (south)
/*  821 */         tX = middleHeight;
/*  822 */       if (north)
/*  823 */         bX = middleHeight;
/*  824 */       if (east)
/*  825 */         tZ = middleHeight;
/*  826 */       if (west) {
/*  827 */         bZ = middleHeight;
/*      */       }
/*      */ 
/*      */     }
/*  831 */     else if (num == 2)
/*      */     {
/*  834 */       if ((below) && (above)) {
/*  835 */         bX = lowOffset;
/*  836 */         bZ = lowOffset;
/*  837 */         tX = highOffset;
/*  838 */         tZ = highOffset;
/*      */       }
/*  841 */       else if ((east) && (west)) {
/*  842 */         bX = lowOffset;
/*  843 */         bY = lowOffset;
/*  844 */         tX = highOffset;
/*  845 */         tY = highOffset;
/*      */       }
/*  848 */       else if ((north) && (south)) {
/*  849 */         bY = lowOffset;
/*  850 */         bZ = lowOffset;
/*  851 */         tY = highOffset;
/*  852 */         tZ = highOffset;
/*      */       }
/*  855 */       else if (below)
/*      */       {
/*  857 */         if (south) {
/*  858 */           if (southTube) {
/*  859 */             tY = highOffset;
/*  860 */             bZ = lowOffset;
/*  861 */             tZ = highOffset;
/*      */           }
/*      */           else {
/*  864 */             tY = middleHeight;
/*  865 */           }if (belowTube) {
/*  866 */             tX = highOffset;
/*  867 */             bZ = lowOffset;
/*  868 */             tZ = highOffset;
/*      */           } else {
/*  870 */             tX = middleHeight;
/*      */           }
/*      */         }
/*      */ 
/*  874 */         if (north) {
/*  875 */           if (northTube) {
/*  876 */             tY = highOffset;
/*  877 */             bZ = lowOffset;
/*  878 */             tZ = highOffset;
/*      */           } else {
/*  880 */             tY = middleHeight;
/*  881 */           }if (belowTube) {
/*  882 */             bX = lowOffset;
/*  883 */             bZ = lowOffset;
/*  884 */             tZ = highOffset;
/*      */           } else {
/*  886 */             bX = middleHeight;
/*      */           }
/*      */         }
/*  889 */         if (east) {
/*  890 */           if (eastTube) {
/*  891 */             tY = highOffset;
/*  892 */             bX = lowOffset;
/*  893 */             tX = highOffset;
/*      */           } else {
/*  895 */             tY = middleHeight;
/*  896 */           }if (belowTube) {
/*  897 */             tZ = highOffset;
/*  898 */             bX = lowOffset;
/*  899 */             tX = highOffset;
/*      */           } else {
/*  901 */             tZ = middleHeight;
/*      */           }
/*      */         }
/*  904 */         if (west) {
/*  905 */           if (westTube) {
/*  906 */             tY = highOffset;
/*  907 */             bX = lowOffset;
/*  908 */             tX = highOffset;
/*      */           } else {
/*  910 */             tY = middleHeight;
/*  911 */           }if (belowTube) {
/*  912 */             bZ = lowOffset;
/*  913 */             bX = lowOffset;
/*  914 */             tX = highOffset;
/*      */           } else {
/*  916 */             bZ = middleHeight;
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*  921 */       else if (above)
/*      */       {
/*  923 */         if (south) {
/*  924 */           if (southTube) {
/*  925 */             bY = lowOffset;
/*  926 */             bZ = lowOffset;
/*  927 */             tZ = highOffset;
/*      */           }
/*      */           else {
/*  930 */             bY = middleHeight;
/*  931 */           }if (aboveTube) {
/*  932 */             tX = highOffset;
/*  933 */             bZ = lowOffset;
/*  934 */             tZ = highOffset;
/*      */           } else {
/*  936 */             tX = middleHeight;
/*      */           }
/*      */         }
/*      */ 
/*  940 */         if (north) {
/*  941 */           if (northTube) {
/*  942 */             bY = lowOffset;
/*  943 */             bZ = lowOffset;
/*  944 */             tZ = highOffset;
/*      */           } else {
/*  946 */             bY = middleHeight;
/*  947 */           }if (aboveTube) {
/*  948 */             bX = lowOffset;
/*  949 */             bZ = lowOffset;
/*  950 */             tZ = highOffset;
/*      */           } else {
/*  952 */             bX = middleHeight;
/*      */           }
/*      */         }
/*  955 */         if (east) {
/*  956 */           if (eastTube) {
/*  957 */             bY = lowOffset;
/*  958 */             bX = lowOffset;
/*  959 */             tX = highOffset;
/*      */           } else {
/*  961 */             bY = middleHeight;
/*  962 */           }if (aboveTube) {
/*  963 */             tZ = highOffset;
/*  964 */             bX = lowOffset;
/*  965 */             tX = highOffset;
/*      */           } else {
/*  967 */             tZ = middleHeight;
/*      */           }
/*      */         }
/*  970 */         if (west) {
/*  971 */           if (westTube) {
/*  972 */             bY = lowOffset;
/*  973 */             bX = lowOffset;
/*  974 */             tX = highOffset;
/*      */           } else {
/*  976 */             bY = middleHeight;
/*  977 */           }if (aboveTube) {
/*  978 */             bZ = lowOffset;
/*  979 */             bX = lowOffset;
/*  980 */             tX = highOffset;
/*      */           } else {
/*  982 */             bZ = middleHeight;
/*      */           }
/*      */         }
/*      */       }
/*  986 */       else if ((west) && (north)) {
/*  987 */         if (westTube) {
/*  988 */           bX = lowOffset;
/*  989 */           bY = lowOffset;
/*  990 */           tY = highOffset;
/*      */         } else {
/*  992 */           bX = middleHeight;
/*  993 */         }if (northTube) {
/*  994 */           bZ = lowOffset;
/*  995 */           bY = lowOffset;
/*  996 */           tY = highOffset;
/*      */         } else {
/*  998 */           bZ = middleHeight;
/*      */         }
/*      */       }
/*      */ 
/* 1002 */       if ((west) && (south)) {
/* 1003 */         if (westTube) {
/* 1004 */           tX = highOffset;
/* 1005 */           bY = lowOffset;
/* 1006 */           tY = highOffset;
/*      */         } else {
/* 1008 */           tX = middleHeight;
/* 1009 */         }if (southTube) {
/* 1010 */           bZ = lowOffset;
/* 1011 */           bY = lowOffset;
/* 1012 */           tY = highOffset;
/*      */         } else {
/* 1014 */           bZ = middleHeight;
/*      */         }
/*      */ 
/*      */       }
/* 1018 */       else if ((east) && (north)) {
/* 1019 */         if (eastTube) {
/* 1020 */           bX = lowOffset;
/* 1021 */           bY = lowOffset;
/* 1022 */           tY = highOffset;
/*      */         } else {
/* 1024 */           bX = middleHeight;
/* 1025 */         }if (northTube) {
/* 1026 */           tZ = highOffset;
/* 1027 */           bY = lowOffset;
/* 1028 */           tY = highOffset;
/*      */         } else {
/* 1030 */           tZ = middleHeight;
/*      */         }
/*      */ 
/*      */       }
/* 1034 */       else if ((east) && (south)) {
/* 1035 */         if (eastTube) {
/* 1036 */           tX = highOffset;
/* 1037 */           bY = lowOffset;
/* 1038 */           tY = highOffset;
/*      */         } else {
/* 1040 */           tX = middleHeight;
/* 1041 */         }if (southTube) {
/* 1042 */           tZ = highOffset;
/* 1043 */           bY = lowOffset;
/* 1044 */           tY = highOffset;
/*      */         } else {
/* 1046 */           tZ = middleHeight;
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/* 1051 */     else if (num == 3)
/*      */     {
/* 1053 */       if ((below) && (above))
/*      */       {
/* 1055 */         if (north) {
/* 1056 */           if ((belowTube) || (aboveTube))
/* 1057 */             bX = lowOffset;
/*      */           else
/* 1059 */             bX = middleHeight;
/* 1060 */           bZ = lowOffset;
/* 1061 */           tZ = highOffset;
/*      */         }
/* 1064 */         else if (south) {
/* 1065 */           if ((belowTube) || (aboveTube))
/* 1066 */             tX = highOffset;
/*      */           else
/* 1068 */             tX = middleHeight;
/* 1069 */           bZ = lowOffset;
/* 1070 */           tZ = highOffset;
/*      */         }
/*      */ 
/* 1073 */         if (west) {
/* 1074 */           bX = lowOffset;
/* 1075 */           tX = highOffset;
/* 1076 */           if ((belowTube) || (aboveTube))
/* 1077 */             bZ = lowOffset;
/*      */           else {
/* 1079 */             bZ = middleHeight;
/*      */           }
/*      */         }
/* 1082 */         if (east) {
/* 1083 */           bX = lowOffset;
/* 1084 */           tX = highOffset;
/* 1085 */           if ((belowTube) || (aboveTube))
/* 1086 */             tZ = highOffset;
/*      */           else {
/* 1088 */             tZ = middleHeight;
/*      */           }
/*      */         }
/*      */       }
/* 1092 */       else if ((north) && (south))
/*      */       {
/* 1094 */         if (above) {
/* 1095 */           if ((northTube) || (southTube))
/* 1096 */             bY = lowOffset;
/*      */           else
/* 1098 */             bY = middleHeight;
/* 1099 */           bZ = lowOffset;
/* 1100 */           tZ = highOffset;
/*      */         }
/* 1103 */         else if (below) {
/* 1104 */           if ((northTube) || (southTube))
/* 1105 */             tY = highOffset;
/*      */           else
/* 1107 */             tY = middleHeight;
/* 1108 */           bZ = lowOffset;
/* 1109 */           tZ = highOffset;
/*      */         }
/*      */ 
/* 1112 */         if (west) {
/* 1113 */           bY = lowOffset;
/* 1114 */           tY = highOffset;
/* 1115 */           if ((northTube) || (southTube))
/* 1116 */             bZ = lowOffset;
/*      */           else {
/* 1118 */             bZ = middleHeight;
/*      */           }
/*      */         }
/* 1121 */         if (east) {
/* 1122 */           bY = lowOffset;
/* 1123 */           tY = highOffset;
/* 1124 */           if ((northTube) || (southTube))
/* 1125 */             tZ = highOffset;
/*      */           else {
/* 1127 */             tZ = middleHeight;
/*      */           }
/*      */         }
/*      */       }
/* 1131 */       else if ((east) && (west))
/*      */       {
/* 1133 */         if (north) {
/* 1134 */           if ((eastTube) || (westTube))
/* 1135 */             bX = lowOffset;
/*      */           else
/* 1137 */             bX = middleHeight;
/* 1138 */           bY = lowOffset;
/* 1139 */           tY = highOffset;
/*      */         }
/* 1142 */         else if (south) {
/* 1143 */           if ((eastTube) || (westTube))
/* 1144 */             tX = highOffset;
/*      */           else
/* 1146 */             tX = middleHeight;
/* 1147 */           bY = lowOffset;
/* 1148 */           tY = highOffset;
/*      */         }
/*      */ 
/* 1151 */         if (above) {
/* 1152 */           bX = lowOffset;
/* 1153 */           tX = highOffset;
/* 1154 */           if ((eastTube) || (westTube))
/* 1155 */             bY = lowOffset;
/*      */           else {
/* 1157 */             bY = middleHeight;
/*      */           }
/*      */         }
/* 1160 */         if (below) {
/* 1161 */           bX = lowOffset;
/* 1162 */           tX = highOffset;
/* 1163 */           if ((eastTube) || (westTube))
/* 1164 */             tY = highOffset;
/*      */           else {
/* 1166 */             tY = middleHeight;
/*      */           }
/*      */         }
/*      */       }
/* 1170 */       else if (above)
/*      */       {
/* 1172 */         bY = middleHeight;
/* 1173 */         if (north) {
/* 1174 */           bX = middleHeight;
/* 1175 */           if (east)
/* 1176 */             tZ = middleHeight;
/* 1177 */           if (west) {
/* 1178 */             bZ = middleHeight;
/*      */           }
/*      */         }
/* 1181 */         else if (south) {
/* 1182 */           tX = middleHeight;
/* 1183 */           if (east)
/* 1184 */             tZ = middleHeight;
/* 1185 */           if (west) {
/* 1186 */             bZ = middleHeight;
/*      */           }
/*      */         }
/*      */       }
/* 1190 */       else if (below)
/*      */       {
/* 1192 */         tY = middleHeight;
/* 1193 */         if (north) {
/* 1194 */           bX = middleHeight;
/* 1195 */           if (east)
/* 1196 */             tZ = middleHeight;
/* 1197 */           if (west) {
/* 1198 */             bZ = middleHeight;
/*      */           }
/*      */         }
/* 1201 */         else if (south) {
/* 1202 */           tX = middleHeight;
/* 1203 */           if (east)
/* 1204 */             tZ = middleHeight;
/* 1205 */           if (west) {
/* 1206 */             bZ = middleHeight;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1211 */     else if (num == 4)
/*      */     {
/* 1213 */       if ((above) && (below)) {
/* 1214 */         if ((east) && (west))
/*      */         {
/* 1216 */           bX = lowOffset;
/* 1217 */           tX = highOffset;
/*      */         }
/* 1220 */         else if ((north) && (south)) {
/* 1221 */           bZ = lowOffset;
/* 1222 */           tZ = highOffset;
/*      */         }
/* 1225 */         else if (north)
/*      */         {
/* 1227 */           bX = middleHeight;
/* 1228 */           if (east)
/* 1229 */             tZ = middleHeight;
/* 1230 */           if (west) {
/* 1231 */             bZ = middleHeight;
/*      */           }
/*      */         }
/* 1234 */         else if (south)
/*      */         {
/* 1236 */           tX = middleHeight;
/* 1237 */           if (east)
/* 1238 */             tZ = middleHeight;
/* 1239 */           if (west) {
/* 1240 */             bZ = middleHeight;
/*      */           }
/*      */         }
/*      */       }
/* 1244 */       else if ((north) && (south))
/*      */       {
/* 1246 */         if ((!above) && (!below)) {
/* 1247 */           bY = lowOffset;
/* 1248 */           tY = highOffset;
/*      */         }
/*      */ 
/* 1251 */         if (above)
/*      */         {
/* 1253 */           bY = middleHeight;
/* 1254 */           if (east)
/* 1255 */             tZ = middleHeight;
/* 1256 */           if (west) {
/* 1257 */             bZ = middleHeight;
/*      */           }
/*      */         }
/* 1260 */         else if (below)
/*      */         {
/* 1262 */           tY = middleHeight;
/* 1263 */           if (east)
/* 1264 */             tZ = middleHeight;
/* 1265 */           if (west) {
/* 1266 */             bZ = middleHeight;
/*      */           }
/*      */         }
/*      */       }
/* 1270 */       else if ((east) && (west)) {
/* 1271 */         if (above)
/*      */         {
/* 1273 */           bY = middleHeight;
/* 1274 */           if (south)
/* 1275 */             tX = middleHeight;
/* 1276 */           if (north) {
/* 1277 */             bX = middleHeight;
/*      */           }
/*      */         }
/* 1280 */         else if (below)
/*      */         {
/* 1282 */           tY = middleHeight;
/* 1283 */           if (south)
/* 1284 */             tX = middleHeight;
/* 1285 */           if (north) {
/* 1286 */             bX = middleHeight;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1291 */     else if (num == 5)
/*      */     {
/* 1293 */       if (!below)
/*      */       {
/* 1295 */         if ((northTube) || (southTube) || (eastTube) || (westTube) || (northSlabShape) || (southSlabShape) || (eastSlabShape) || (westSlabShape))
/*      */         {
/* 1297 */           bY = lowOffset;
/*      */         }
/*      */         else {
/* 1300 */           bY = middleHeight;
/*      */         }
/*      */ 
/* 1303 */         if (slabsAbove == 2)
/*      */         {
/* 1305 */           tY = highOffset;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1318 */       if (!above)
/*      */       {
/* 1320 */         if ((northTube) || (southTube) || (eastTube) || (westTube) || (northSlabShape) || (southSlabShape) || (eastSlabShape) || (westSlabShape))
/*      */         {
/* 1322 */           tY = highOffset;
/*      */         }
/*      */         else {
/* 1325 */           tY = middleHeight;
/*      */         }
/*      */ 
/* 1328 */         if (slabsBelow == 2)
/*      */         {
/* 1330 */           bY = lowOffset;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1344 */       if (!south)
/*      */       {
/* 1346 */         if ((aboveTube) || (belowTube) || (eastTube) || (westTube) || (aboveSlabShape) || (belowSlabShape) || (eastSlabShape) || (westSlabShape))
/*      */         {
/* 1348 */           bX = lowOffset;
/*      */         }
/*      */         else {
/* 1351 */           bX = middleHeight;
/*      */         }
/*      */ 
/* 1354 */         if (slabsNorth == 2)
/*      */         {
/* 1356 */           tX = highOffset;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1369 */       if (!north)
/*      */       {
/* 1372 */         if ((aboveTube) || (belowTube) || (eastTube) || (westTube) || (aboveSlabShape) || (belowSlabShape) || (eastSlabShape) || (westSlabShape))
/*      */         {
/* 1374 */           tX = highOffset;
/*      */         }
/*      */         else {
/* 1377 */           tX = middleHeight;
/*      */         }
/*      */ 
/* 1380 */         if (slabsSouth == 2)
/*      */         {
/* 1382 */           bX = lowOffset;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1395 */       if (!east)
/*      */       {
/* 1398 */         if ((northTube) || (southTube) || (aboveTube) || (belowTube) || (northSlabShape) || (southSlabShape) || (aboveSlabShape) || (belowSlabShape))
/*      */         {
/* 1400 */           bZ = lowOffset;
/*      */         }
/*      */         else {
/* 1403 */           bZ = middleHeight;
/*      */         }
/*      */ 
/* 1406 */         if (slabsWest == 2)
/*      */         {
/* 1408 */           tZ = highOffset;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1421 */       if (!west)
/*      */       {
/* 1423 */         if ((northTube) || (southTube) || (aboveTube) || (belowTube) || (northSlabShape) || (southSlabShape) || (aboveSlabShape) || (belowSlabShape))
/*      */         {
/* 1425 */           tZ = highOffset;
/*      */         }
/*      */         else {
/* 1428 */           tZ = middleHeight;
/*      */         }
/*      */ 
/* 1431 */         if (slabsEast == 2)
/*      */         {
/* 1433 */           bZ = lowOffset;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1448 */     setBlockBounds(bX, bY, bZ, tX, tY, tZ);
/*      */   }
/*      */ 
/*      */   public boolean isOpaqueCube()
/*      */   {
/* 1453 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean renderAsNormalBlock()
/*      */   {
/* 1458 */     return false;
/*      */   }
/*      */ 
/*      */   public int getRenderType()
/*      */   {
/* 1463 */     InfiBlocks.getContentInstance(); return InfiBlockContent.magicSlabModel;
/*      */   }
/*      */ 
/*      */   public int damageDropped(int md)
/*      */   {
/* 1468 */     return md;
/*      */   }
/*      */ 
/*      */   public boolean canConnectSlabTo(IBlockAccess iblockaccess, int i, int j, int k)
/*      */   {
/* 1473 */     int bID = iblockaccess.getBlockId(i, j, k);
/* 1474 */     if (((Block.blocksList[bID] instanceof MagicSlabBase)) || (bID == Block.stoneSingleSlab.blockID) || ((Block.blocksList[bID] instanceof BlockDoor)) || ((Block.blocksList[bID] instanceof BlockPane)))
/*      */     {
/* 1478 */       return true;
/*      */     }
/* 1480 */     Block block = Block.blocksList[bID];
/* 1481 */     if ((block != null) && (block.blockMaterial.isOpaque()) && (block.renderAsNormalBlock()))
/*      */     {
/* 1483 */       return block.blockMaterial != Material.pumpkin;
/*      */     }
/*      */ 
/* 1487 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isSlab(IBlockAccess iblockaccess, int x, int y, int z)
/*      */   {
/* 1493 */     int bID = iblockaccess.getBlockId(x, y, z);
/* 1494 */     if (((Block.blocksList[bID] instanceof MagicSlabBase)) || (bID == Block.stoneSingleSlab.blockID))
/*      */     {
/* 1496 */       return true;
/*      */     }
/*      */ 
/* 1500 */     return false;
/*      */   }
/*      */ 
/*      */   public byte countNearbyBlocks(IBlockAccess iblockaccess, int x, int y, int z)
/*      */   {
/* 1506 */     boolean east = canConnectSlabTo(iblockaccess, x, y, z - 1);
/* 1507 */     boolean west = canConnectSlabTo(iblockaccess, x, y, z + 1);
/* 1508 */     boolean south = canConnectSlabTo(iblockaccess, x - 1, y, z);
/* 1509 */     boolean north = canConnectSlabTo(iblockaccess, x + 1, y, z);
/* 1510 */     boolean below = canConnectSlabTo(iblockaccess, x, y - 1, z);
/* 1511 */     boolean above = canConnectSlabTo(iblockaccess, x, y + 1, z);
/*      */ 
/* 1513 */     byte num = 0;
/* 1514 */     num = (byte)(num + (east ? 1 : 0));
/* 1515 */     num = (byte)(num + (west ? 1 : 0));
/* 1516 */     num = (byte)(num + (south ? 1 : 0));
/* 1517 */     num = (byte)(num + (north ? 1 : 0));
/* 1518 */     num = (byte)(num + (above ? 1 : 0));
/* 1519 */     num = (byte)(num + (below ? 1 : 0));
/*      */ 
/* 1521 */     return num;
/*      */   }
/*      */ 
/*      */   public boolean checkForTube(IBlockAccess iblockaccess, int x, int y, int z)
/*      */   {
/* 1526 */     boolean isTube = false;
/* 1527 */     byte num = countNearbyBlocks(iblockaccess, x, y, z);
/*      */ 
/* 1529 */     if (num == 2)
/*      */     {
/* 1531 */       if ((canConnectSlabTo(iblockaccess, x, y, z - 1)) && (isSlab(iblockaccess, x, y, z - 1)) && (canConnectSlabTo(iblockaccess, x, y, z + 1)) && (isSlab(iblockaccess, x, y, z + 1)))
/*      */       {
/* 1533 */         isTube = true;
/*      */       }
/* 1535 */       else if ((canConnectSlabTo(iblockaccess, x - 1, y, z)) && (isSlab(iblockaccess, x - 1, y, z)) && (canConnectSlabTo(iblockaccess, x + 1, y, z)) && (isSlab(iblockaccess, x + 1, y, z)))
/*      */       {
/* 1537 */         isTube = true;
/*      */       }
/* 1539 */       else if ((canConnectSlabTo(iblockaccess, x, y - 1, z)) && (isSlab(iblockaccess, x, y - 1, z)) && (canConnectSlabTo(iblockaccess, x, y + 1, z)) && (isSlab(iblockaccess, x, y + 1, z)))
/*      */       {
/* 1541 */         isTube = true;
/*      */       }
/*      */     }
/*      */ 
/* 1545 */     return isTube;
/*      */   }
/*      */ 
/*      */   public boolean checkForSlabShape(IBlockAccess iblockaccess, int x, int y, int z)
/*      */   {
/* 1551 */     byte connections = 0;
/* 1552 */     byte num = countNearbyBlocks(iblockaccess, x, y, z);
/*      */ 
/* 1554 */     if (num == 4) {
/* 1555 */       if ((canConnectSlabTo(iblockaccess, x, y, z - 1)) && (canConnectSlabTo(iblockaccess, x, y, z + 1))) {
/* 1556 */         connections = (byte)(connections + 1);
/*      */       }
/* 1558 */       if ((canConnectSlabTo(iblockaccess, x - 1, y, z)) && (canConnectSlabTo(iblockaccess, x + 1, y, z))) {
/* 1559 */         connections = (byte)(connections + 1);
/*      */       }
/* 1561 */       if ((canConnectSlabTo(iblockaccess, x, y - 1, z)) && (canConnectSlabTo(iblockaccess, x, y + 1, z))) {
/* 1562 */         connections = (byte)(connections + 1);
/*      */       }
/*      */     }
/*      */ 
/* 1566 */     if (connections == 2) {
/* 1567 */       return true;
/*      */     }
/* 1569 */     return false;
/*      */   }
/*      */ 
/*      */   public void getSubBlocks(int id, CreativeTabs tab, List list)
/*      */   {
/* 1576 */     for (int iter = 0; iter < 16; iter++)
/*      */     {
/* 1578 */       list.add(new ItemStack(id, 1, iter));
/*      */     }
/*      */   }
/*      */ }

/* Location:           /Users/benadam/Desktop/infiblocks.zip
 * Qualified Name:     inficraft.infiblocks.magicslabs.MagicSlabBase
 * JD-Core Version:    0.6.2
 */