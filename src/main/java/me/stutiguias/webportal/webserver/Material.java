/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver;

/**
 *
 * @author Daniel
 */
public class Material {
    
    public static String getItemName(int id, short meta){
        switch(id){
            default: return "Item " + id + ":" + meta;
            case 0: return "Air";
            case 1: return "Stone";
            case 2: return "Grass";
            case 3: return "Dirt";
            case 4: return "Cobblestone";
            case 5:
                switch(meta){
                    case 0: return "Oak Wood Planks";
                    case 1: return "Spruce Wood Planks";
                    case 2: return "Birch Wood Planks";
                    case 3: return "Jungle Wood Planks";
                    default: return "Wooden Planks";
                }
            case 6:
                switch(meta){
                    case 0: return "Oak Sapling";
                    case 1: return "Spruce Sapling";
                    case 2: return "Birch Sapling";
                    case 3: return "Jungle Tree Sapling";
                    default: return "Sapling";
                }
            case 7: return "Bedrock";
            case 8: return "Water";
            case 9: return "Stationary Water";
            case 10: return "Lava";
            case 11: return "Stationary Lava";
            case 12: return "Sand";
            case 13: return "Gravel";
            case 14: return "Gold Ore";
            case 15: return "Iron Ore";
            case 16: return "Coal Ore";
            case 17:
                switch(meta){
                    case 0: return "Oak Wood";
                    case 1: return "Spruce Wood";
                    case 2: return "Birch Wood";
                    case 3: return "Jungle Wood";
                    default: return "Wood";
                }
            case 18:
                switch(meta){
                    case 0: return "Oak Leaves";
                    case 1: return "Spruce Leaves";
                    case 2: return "Birch Leaves";
                    case 3: return "Jungle Leaves";
                    default: return "Leaves";
                }
            case 19: return "Sponge";
            case 20: return "Glass";
            case 21: return "Lapis Lazuli Ore";
            case 22: return "Lapis Lazuli Block";
            case 23: return "Dispenser";
            case 24: 
                switch(meta){
                    case 0: return "Sandstone";
                    case 1: return "Hieroglyphic Sandstone";
                    case 2: return "Smooth Sandstone";
                    default: return "Sandstone";
                }
            case 25: return "Noteblock";
            case 26: return "Bed";
            case 27: return "Powered Rail";
            case 28: return "Detector Rail";
            case 29: return "Sticky Piston";
            case 30: return "Cobweb";
            case 31:
                switch(meta){
                    case 0: return "Dead Shrub";
                    case 1: return "Tall Grass";
                    case 2: return "Fern";
                    default: return "Dead Shrub";
                }
            case 32: return "Dead Bush";
            case 33: return "Piston";
            case 34: return "Piston Extension";
            case 35:
                switch(meta){
                    case 0: return "White Wool";
                    case 1: return "Orange Wool";
                    case 2: return "Magenta Wool";
                    case 3: return "Light Blue Wool";
                    case 4: return "Yellow Wool";
                    case 5: return "Light Green Wool";
                    case 6: return "Pink Wool";
                    case 7: return "Gray Wool";
                    case 8: return "Light Gray Wool";
                    case 9: return "Cyan Wool";
                    case 10: return "Purple Wool";
                    case 11: return "Blue Wool";
                    case 12: return "Brown Wool";
                    case 13: return "Dark Green Wool";
                    case 14: return "Red Wool";
                    case 15: return "Black Wool";
                    default: return "Wool";
                }
            case 36: return "Block Moved By Piston";
            case 37: return "Dandelion";
            case 38: return "Rose";
            case 39: return "Brown Mushroom";
            case 40: return "Red Mushroom";
            case 41: return "Gold Block";
            case 42: return "Iron Block";
            case 43:
                switch(meta){
                    case 0: return "Stone Double-Slab";
                    case 1: return "Sandstone Double-Slab";
                    case 2: return "Wodden Double-Slab";
                    case 3: return "Cobblestone Double-Slab";
                    case 4: return "Brick Double-Slab";
                    case 5: return "Stone Brick Double-Slab";
                    default: return "Stone Double-Slab";
                }
            case 44:
                switch(meta){
                    case 0: return "Stone Slab";
                    case 1: return "Sandstone Slab";
                    case 2: return "Wodden Slab";
                    case 3: return "Cobblestone Slab";
                    case 4: return "Brick Slab";
                    case 5: return "Stone Brick Slab";
                    default: return "Stone Slab";
                }
            case 45: return "Brick Block";
            case 46: return "TNT";
            case 47: return "Bookshelf";
            case 48: return "Moss Stone";
            case 49: return "Obsidian";
            case 50: return "Torch";
            case 51: return "Fire";
            case 52: return "Monster Spawner";
            case 53: return "Wooden Stairs";
            case 54: return "Chest";
            case 55: return "Redstone Wire";
            case 56: return "Diamond Ore";
            case 57: return "Diamond Block";
            case 58: return "Crafting Table";
            case 59: return "Seeds";
            case 60: return "Farmland";
            case 61: return "Furnace";
            case 62: return "Burning Furnace";
            case 63: return "Sign Post";
            case 64: return "Wooden Door";
            case 65: return "Ladders";
            case 66: return "Rails";
            case 67: return "Cobblestone Stairs";
            case 68: return "Wall Sign";
            case 69: return "Lever";
            case 70: return "Stone Pressure Plate";
            case 71: return "Iron Door";
            case 72: return "Wooden Pressure Plate";
            case 73: return "Redstone Ore";
            case 74: return "Glowing Redstone Ore";
            case 75: return "Redstone Torch (off)";
            case 76: return "Redstone Torch";
            case 77: return "Stone Button";
            case 78: return "Snow";
            case 79: return "Ice";
            case 80: return "Snow Block";
            case 81: return "Cactus";
            case 82: return "Clay Block";
            case 83: return "Sugar Cane";
            case 84:
                switch(meta){
                    case 0: return "Empty Jukebox";
                    case 1: return "Jukebox Playing Gold Disc";
                    case 2: return "Jukebox Playing Green Disc";
                    case 3: return "Jukebox Playing Orange Disc";
                    case 4: return "Jukebox Playing Red Disc";
                    case 5: return "Jukebox Playing Lime Green Disc";
                    case 6: return "Jukebox Playing Purple Disc";
                    case 7: return "Jukebox Playing Violet Disc";
                    case 8: return "Jukebox Playing Black Disc";
                    case 9: return "Jukebox Playing White Disc";
                    case 10: return "Jukebox Playing Sea Green Disc";
                    case 11: return "Jukebox Playing Broken Disc";
                    default: return "Jukebox";
                }
            case 85: return "Fence";
            case 86: return "Pumpkin";
            case 87: return "Netherrack";
            case 88: return "Soul Sand";
            case 89: return "Glowstone Block";
            case 90: return "Portal";
            case 91: return "Jack-O-Lantern";
            case 92: return "Cake Block";
            case 93: return "Redstone Repeater (off)";
            case 94: return "Redstone Repeater (on)";
            case 95: return "Locked Chest";
            case 96: return "Trapdoor";
            case 97:
                switch(meta){
                    case 0: return "Hidden Silverfish - Stone Block";
                    case 1: return "Hidden Silverfish - Cobblestone Block";
                    case 2: return "Hidden Silverfish - Stone Brick";
                    default: return "Hidden Silverfish";
                }
            case 98:
                switch(meta){
                    case 0: return "Stone Brick";
                    case 1: return "Mossy Stone Brick";
                    case 2: return "Cracked Stone Brick";
                    case 3: return "Circle Stone Brick";
                    default: return "Stone Brick";
                }
            case 99: return "Huge Brown Mushroom";
            case 100: return "Huge Red Mushroom";
            case 101: return "Iron Bars";
            case 102: return "Glass Pane";
            case 103: return "Melon";
            case 104: return "Pumpkin Stem";
            case 105: return "Melon Stem";
            case 106: return "Vines";
            case 107: return "Fence Gate";
            case 108: return "Brick Stairs";
            case 109: return "Stone Brick Stairs";
            case 110: return "Mycelium";
            case 111: return "Lily Pad";
            case 112: return "Nether Brick";
            case 113: return "Nether Brick Fence";
            case 114: return "Nether Brick Stairs";
            case 115: return "Nether Wart";
            case 116: return "Enchantment Table";    
            case 117: return "Brewing Stand";
            case 118: return "Cauldron";
            case 119: return "End Portal";
            case 120: return "End Portal Frame";
            case 121: return "End Stone";
            case 122: return "Dragon Egg";
            case 123: return "Redstone Lamp (off)";
            case 124: return "Redstone Lamp (on)";
            case 129: return "Emerald Block";    

        //ITEMS:
            case 256: return "Iron Shovel";
            case 257: return "Iron Pickaxe";
            case 258: return "Iron Axe";
            case 259: return "Flint and Steel";
            case 260: return "Apple";
            case 261: return "Bow";
            case 262: return "Arrow";
            case 263:
                switch(meta){
                    case 0: return "Coal";
                    case 1: return "Charcoal";
                    default: return "Coal";
                }
            case 264: return "Diamond";
            case 265: return "Iron Ingot";
            case 266: return "Gold Ingot";
            case 267: return "Iron Sword";
            case 268: return "Wooden Sword";
            case 269: return "Wooden Shovel";
            case 270: return "Wooden Pickaxe";
            case 271: return "Wooden Axe";
            case 272: return "Stone Sword";
            case 273: return "Stone Shovel";
            case 274: return "Stone Pickaxe";
            case 275: return "Stone Axe";
            case 276: return "Diamond Sword";
            case 277: return "Diamond Shovel";
            case 278: return "Diamond Pickaxe";
            case 279: return "Diamond Axe";
            case 280: return "Stick";
            case 281: return "Bowl";
            case 282: return "Mushroom Soup";
            case 283: return "Gold Sword";
            case 284: return "Gold Shovel";
            case 285: return "Gold Pickaxe";
            case 286: return "Gold Axe";
            case 287: return "String";
            case 288: return "Feather";
            case 289: return "Gunpowder";
            case 290: return "Wooden Hoe";
            case 291: return "Stone Hoe";
            case 292: return "Iron Hoe";
            case 293: return "Diamond Hoe";
            case 294: return "Gold Hoe";
            case 295: return "Seeds";
            case 296: return "Wheat";
            case 297: return "Bread";
            case 298: return "Leather Cap";
            case 299: return "Leather Tunic";
            case 300: return "Leather Pants";
            case 301: return "Leather Boots";
            case 302: return "Chain Helmet";
            case 303: return "Chain Chestplate";
            case 304: return "Chain Leggings";
            case 305: return "Chain Boots";
            case 306: return "Iron Helmet";
            case 307: return "Iron Chestplate";
            case 308: return "Iron Leggings";
            case 309: return "Iron Boots";
            case 310: return "Diamond Helmet";
            case 311: return "Diamond Chestplate";
            case 312: return "Diamond Leggings";
            case 313: return "Diamond Boots";
            case 314: return "Gold Helmet";
            case 315: return "Gold Chestplate";
            case 316: return "Gold Leggings";
            case 317: return "Gold Boots";
            case 318: return "Flint";
            case 319: return "Raw Porkchop";
            case 320: return "Cooked Porkchop";
            case 321: return "Paintings";
            case 322: return "Golden Apple";
            case 323: return "Sign";
            case 324: return "Wooden Door";
            case 325: return "Bucket";
            case 326: return "Water Bucket";
            case 327: return "Lava Bucket";
            case 328: return "Minecart";
            case 329: return "Saddle";
            case 330: return "Iron Door";
            case 331: return "Redstone";
            case 332: return "Snowball";
            case 333: return "Boat";
            case 334: return "Leather";
            case 335: return "Milk";
            case 336: return "Clay Brick";
            case 337: return "Clay";
            case 338: return "Sugar Cane";
            case 339: return "Paper";
            case 340: return "Book";
            case 341: return "Slimeball";
            case 342: return "Storage Minecart";
            case 343: return "Powered Minecart";
            case 344: return "Egg";
            case 345: return "Compass";
            case 346: return "Fishing Rod";
            case 347: return "Clock";
            case 348: return "Glowstone Dust";
            case 349: return "Raw Fish";
            case 350: return "Cooked Fish";
            case 351: switch (meta){
                    case 0: return "Ink Sac (Black Dye)";
                    case 1: return "Rose Red (Red Dye)";
                    case 2: return "Cactus Green (Green Dye)";
                    case 3: return "Cocoa Beans (Brown Dye)";
                    case 4: return "Lapis Lazuli (Blue Dye)";
                    case 5: return "Purple Dye";
                    case 6: return "Cyan Dye";
                    case 7: return "Light Gray Dye";
                    case 8: return "Gray Dye";
                    case 9: return "Pink Dye";
                    case 10: return "Lime Dye";
                    case 11: return "Dandelion Yellow (Yellow Dye)";
                    case 12: return "Light Blue Dye";
                    case 13: return "Magenta Dye";
                    case 14: return "Orange Dye";
                    case 15: return "Bonemeal (White Dye)";
                    default: return "Ink Sac (Black Dye)";
            }
            case 352: return "Bone";
            case 353: return "Sugar";
            case 354: return "Cake";
            case 355: return "Bed";
            case 356: return "Redstone Repeater";
            case 357: return "Cookie";
            case 358: return "Map";
            case 359: return "Shears";
            case 360: return "Melon (Slice)";
            case 361: return "Pumpkin Seeds";
            case 362: return "Melon Seeds";
            case 363: return "Raw Beef";
            case 364: return "Steak";
            case 365: return "Raw Chicken";
            case 366: return "Cooked Chicken";
            case 367: return "Rotten Flesh";
            case 368: return "Ender Pearl";
            case 369: return "Blaze Rod";
            case 370: return "Ghast Tear";
            case 371: return "Gold Nugget";
            case 372: return "Nether Wart";
            case 373: switch (meta){
                case 0: return "Water Bottle";
                case 6: return "Clear Potion";
                case 7: return "Clear Potion";
                case 11: return "Diffuse Potion";
                case 13: return "Artless Potion";
                case 14: return "Thin Potion";
                case 15: return "Thin Potion";
                case 16: return "Awkward Potion (No Effects)";
                case 22: return "Bungling Potion";
                case 23: return "Bungling Potion";
                case 27: return "Smooth Potion";
                case 29: return "Suave Potion";
                case 30: return "Debonair Potion";
                case 31: return "Debonair Potion";
                case 32: return "Thick Potion (No Effects)";
                case 38: return "Charming Potion";
                case 39: return "Charming Potion";
                case 43: return "Refined Potion";
                case 45: return "Cordial Potion";
                case 46: return "Sparkling Potion";
                case 47: return "Sparkling Potion";
                case 48: return "Potent Potion";
                case 54: return "Rank Potion";
                case 55: return "Rank Potion";
                case 59: return "Acrid Potion";
                case 61: return "Gross Potion";
                case 62: return "Stinky Potion";
                case 63: return "Stinky Potion";
                case 64: return "Mundane Potion (extended) (No Effects)";

                case 8192: return "Mundane Potion (No Effects)"; 

                case 8193: return "Potion of Regeneration (0:45)";
                case 8194: return "Potion of Swiftness (3:00)";
                case 8195: return "Potion of Fire Resistance (3:00)";
                case 8196: return "Potion of Poison (0:45)"; 
                case 8197: return "Potion of Healing";
                case 8200: return "Potion of Weakness (1:30)";
                case 8201: return "Potion of Strength (3:00)";
                case 8202: return "Potion of Slowness (1:30)"; 
                case 8204: return "Potion of Harming";

                case 8225: return "Potion of Regeneration II (0:22)";
                case 8226: return "Potion of Swiftness II (1:30)";
                case 8227: return "Potion of Fire Resistance (3:00) (reverted)";    
                case 8228: return "Potion of Poison II (0:22)";
                case 8229: return "Potion of Healing II"; 
                case 8232: return "Potion of Weakness (reverted)";
                case 8233: return "Potion of Strength II (1:30)";
                case 8234: return "Potion of Slowness (1:30) (reverted)";
                case 8236: return "Potion of Harming II";

                case 8257: return "Potion of Regeneration (2:00)";
                case 8258: return "Potion of Swiftness (8:00)"; 
                case 8259: return "Potion of Fire Resistance (8:00)";
                case 8260: return "Potion of Poison (2:00)";
                case 8261: return "Potion of Healing (reverted)";
                case 8264: return "Potion of Weakness (4:00)";
                case 8265: return "Potion of Strength (8:00)";
                case 8266: return "Potion of Slowness (4:00)";
                case 8268: return "Potion of Harming (reverted)";   

                case 16384: return "Splash Mundane Potion";
                  
                case 16371: return "Fire Resistance (8:00)";
                    
                case 16385: return "Splash Potion of Regeneration (0:33)";
                case 16386: return "Splash Potion of Swiftness (2:15)";
                case 16387: return "Splash Potion of Fire Resistance (2:15)";
                case 16388: return "Splash Potion of Poison (0:33)"; 
                case 16389: return "Splash Potion of Healing";
                case 16392: return "Splash Potion of Weakness (1:07)";
                case 16393: return "Splash Potion of Strength (2:15)";
                case 16394: return "Splash Potion of Slowness (1:07)"; 
                case 16396: return "Splash Potion of Harming";
                        
                case 16417: return "Splash Potion of Regeneration II (0:16)";
                case 16418: return "Splash Potion of Swiftness II (1:07)";
                case 16419: return "Splash Potion of Fire Resistance (2:15) (reverted)";
                case 16420: return "Splash Potion of Poison II (0:16)";
                case 16421: return "Splash Potion of Healing II";
                case 16424: return "Splash Potion of Weakness (1:07) (reverted)";
                case 16425: return "Splash Potion of Strength II (1:07)";
                case 16426: return "Splash Potion of Slowness (1:07) (reverted)";
                case 16428: return "Splash Potion of Harming II";

                case 16449: return "Splash Potion of Regeneration (1:30)";
                case 16450: return "Splash Potion of Swiftness (6:00)"; 
                case 16451: return "Splash Potion of Fire Resistance (6:00)";
                case 16452: return "Splash Potion of Poison (1:30)";
                case 16453: return "Splash Potion of Healing (reverted)";
                case 16456: return "Splash Potion of Weakness (3:00)";
                case 16457: return "Splash Potion of Strength (6:00)";
                case 16458: return "Splash Potion of Slowness (3:00)";
                case 16460: return "Splash Potion of Harming (reverted)";
                    
                case 32658: return "Speed (2:15)";
                case 32660: return "Poison(0:33)";
                    
                default: return "Potion";
            }   
            case 374: return "Glass Bottle";
            case 375: return "Spider Eye"; 
            case 376: return "Fermented Spider Eye"; 
            case 377: return "Blaze Powder"; 
            case 378: return "Magma Cream"; 
            case 379: return "Brewing Stand"; 
            case 380: return "Cauldron"; 
            case 381: return "Eye of Ender"; 
            case 382: return "Glistering Melon";
            case 383: switch (meta){
                case 50: return "Creeper Egg";
                case 51: return "Skeleton Egg";
                case 52: return "Spider Egg";
                case 53: return "Giant Egg";
                case 54: return "Zombie Egg";
                case 55: return "Slime Egg";
                case 56: return "Ghast Egg";
                case 57: return "Zombie Pigman Egg";
                case 58: return "Enderman Egg";
                case 59: return "Cave Spider Egg";
                case 60: return "Silverfish Egg";
                case 61: return "Blaze Egg";
                case 62: return "Magma Cube Egg";
                case 63: return "Enderdragon Egg";
                case 90: return "Pig Egg";
                case 91: return "Sheep Egg";
                case 92: return "Cow Egg";
                case 93: return "Chicken Egg";
                case 94: return "Squid Egg";
                case 95: return "Wolf Egg";
                case 96: return "Mooshroom Egg";
                case 97: return "Snow Golem Egg";
                case 98: return "Ocelot Egg";
                case 99: return "Iron Golem Egg";
                case 120: return "Villager Egg";
                case 200: return "Ender Crystal";

                default: return "Spawn Egg"; 
            }
            case 384: return "Bottle o'Enchanting";
            case 385: return "Fire Charge";

            case 2256: return "13 Music Disc";
            case 2257: return "Cat Music Disc";
            case 2258: return "Blocks Music Disc";
            case 2259: return "Chirp Music Disc";
            case 2260: return "Far Music Disc";
            case 2261: return "Mall Music Disc";
            case 2262: return "Melohi Music Disc";
            case 2263: return "Stal Music Disc";
            case 2264: return "Strad Music Disc";
            case 2265: return "Ward Music Disc";
            case 2266: return "11 Music Disc";
        }
    }
}