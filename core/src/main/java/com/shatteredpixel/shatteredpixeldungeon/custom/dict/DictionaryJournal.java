package com.shatteredpixel.shatteredpixeldungeon.custom.dict;

import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

import java.util.Collection;
import java.util.LinkedHashMap;

public enum DictionaryJournal {

    ARMORS,
    ARTIFACTS,
    ALCHEMY,
    WEAPONS,
    WANDS,
    RINGS,
    TRINKETS,
    MOBS,
    DOCUMENTS,
    UNCLASSIFIED;

    private LinkedHashMap<String, Integer> d = new LinkedHashMap<>();

    public Collection<String> keyList(){return d.keySet();}

    public Collection<Integer> imageList() {return d.values();}

    static {
        //armors
        ARMORS.d.put("armor_cloth",         ItemSpriteSheet.ARMOR_CLOTH);
        ARMORS.d.put("armor_leather",       ItemSpriteSheet.ARMOR_LEATHER);
        ARMORS.d.put("armor_mail",          ItemSpriteSheet.ARMOR_MAIL);
        ARMORS.d.put("armor_scale",         ItemSpriteSheet.ARMOR_SCALE);
        ARMORS.d.put("armor_plate",         ItemSpriteSheet.ARMOR_PLATE);
        ARMORS.d.put("armor_epic",          ItemSpriteSheet.ARMOR_WARRIOR);
        ARMORS.d.put("armor_glyph_1",       ItemSpriteSheet.STYLUS);
        ARMORS.d.put("armor_glyph_2",       ItemSpriteSheet.STYLUS);
        ARMORS.d.put("armor_glyph_3",       ItemSpriteSheet.STYLUS);
        ARMORS.d.put("armor_glyph_4",       ItemSpriteSheet.CURSE_INFUSE);

        //artifacts
        ARTIFACTS.d.put("artifact_toolkit",         ItemSpriteSheet.ARTIFACT_TOOLKIT);
        ARTIFACTS.d.put("artifact_chalice",         ItemSpriteSheet.ARTIFACT_CHALICE1);
        ARTIFACTS.d.put("artifact_cloak",           ItemSpriteSheet.ARTIFACT_CLOAK);
        ARTIFACTS.d.put("artifact_rose",            ItemSpriteSheet.ARTIFACT_ROSE1);
        ARTIFACTS.d.put("artifact_chain",           ItemSpriteSheet.ARTIFACT_CHAINS);
        ARTIFACTS.d.put("artifact_horn",            ItemSpriteSheet.ARTIFACT_HORN1);
        ARTIFACTS.d.put("artifact_sandal",          ItemSpriteSheet.ARTIFACT_SANDALS);
        ARTIFACTS.d.put("artifact_talisman",        ItemSpriteSheet.ARTIFACT_TALISMAN);
        ARTIFACTS.d.put("artifact_armband",         ItemSpriteSheet.ARTIFACT_ARMBAND);
        ARTIFACTS.d.put("artifact_hourglass",       ItemSpriteSheet.ARTIFACT_HOURGLASS);
        ARTIFACTS.d.put("artifact_book",            ItemSpriteSheet.ARTIFACT_SPELLBOOK);
        ARTIFACTS.d.put("artifact_tome",            ItemSpriteSheet.ARTIFACT_TOME);

        //alchemy, so many...
        //bomb
        ALCHEMY.d.put("bomb",               ItemSpriteSheet.BOMB);
        ALCHEMY.d.put("bomb_frost",         ItemSpriteSheet.FROST_BOMB);
        ALCHEMY.d.put("bomb_flame",         ItemSpriteSheet.FIRE_BOMB);
        ALCHEMY.d.put("bomb_wool",          ItemSpriteSheet.WOOLY_BOMB);
        ALCHEMY.d.put("bomb_noise",         ItemSpriteSheet.NOISEMAKER);
        ALCHEMY.d.put("bomb_flash",         ItemSpriteSheet.FLASHBANG);
//        ALCHEMY.d.put("bomb_shocking",      ItemSpriteSheet.SHOCK_BOMB);
        ALCHEMY.d.put("bomb_smoke",          ItemSpriteSheet.SMOKE_BOMB);
        ALCHEMY.d.put("bomb_regrowth",      ItemSpriteSheet.REGROWTH_BOMB);
        ALCHEMY.d.put("bomb_holy",          ItemSpriteSheet.HOLY_BOMB);
        ALCHEMY.d.put("bomb_arcane",        ItemSpriteSheet.ARCANE_BOMB);
        ALCHEMY.d.put("bomb_shrapnel",      ItemSpriteSheet.SHRAPNEL_BOMB);

        //food
        ALCHEMY.d.put("food_ration",        ItemSpriteSheet.RATION);
        ALCHEMY.d.put("food_frozen",        ItemSpriteSheet.CARPACCIO);
        ALCHEMY.d.put("food_pie",           ItemSpriteSheet.MEAT_PIE);
        ALCHEMY.d.put("food_pasty",         ItemSpriteSheet.PASTY);
        ALCHEMY.d.put("food_fruit",         ItemSpriteSheet.BLANDFRUIT);
        ALCHEMY.d.put("food_chunk",         ItemSpriteSheet.BLAND_CHUNKS);
        ALCHEMY.d.put("food_berry",         ItemSpriteSheet.BERRY);
        ALCHEMY.d.put("phantom_meat",       ItemSpriteSheet.PHANTOM_MEAT);
        ALCHEMY.d.put("supply_ration",       ItemSpriteSheet.SUPPLY_RATION);

        //potions, enhanced ones implemented
        ALCHEMY.d.put("potion_exp",         ItemSpriteSheet.POTION_INDIGO);
        ALCHEMY.d.put("potion_frost",       ItemSpriteSheet.POTION_INDIGO);
        ALCHEMY.d.put("potion_haste",       ItemSpriteSheet.POTION_INDIGO);
        ALCHEMY.d.put("potion_healing",     ItemSpriteSheet.POTION_AZURE);
        ALCHEMY.d.put("potion_inv",         ItemSpriteSheet.POTION_AZURE);
        ALCHEMY.d.put("potion_fly",         ItemSpriteSheet.POTION_AZURE);
        ALCHEMY.d.put("potion_fire",        ItemSpriteSheet.POTION_JADE);
        ALCHEMY.d.put("potion_vision",      ItemSpriteSheet.POTION_JADE);
        ALCHEMY.d.put("potion_paralyse",    ItemSpriteSheet.POTION_JADE);
        ALCHEMY.d.put("potion_purify",      ItemSpriteSheet.POTION_CRIMSON);
        ALCHEMY.d.put("potion_str",         ItemSpriteSheet.POTION_CRIMSON);
        ALCHEMY.d.put("potion_gas",         ItemSpriteSheet.POTION_CRIMSON);

        //scrolls,exotics,stones
        ALCHEMY.d.put("scroll_identify",    ItemSpriteSheet.SCROLL_GYFU);
        ALCHEMY.d.put("scroll_sleep",       ItemSpriteSheet.SCROLL_GYFU);
        ALCHEMY.d.put("scroll_image",       ItemSpriteSheet.SCROLL_GYFU);
        ALCHEMY.d.put("scroll_mapping",     ItemSpriteSheet.SCROLL_ISAZ);
        ALCHEMY.d.put("scroll_challenge",   ItemSpriteSheet.SCROLL_ISAZ);
        ALCHEMY.d.put("scroll_uncurse",     ItemSpriteSheet.SCROLL_ISAZ);
        ALCHEMY.d.put("scroll_recharging",  ItemSpriteSheet.SCROLL_KAUNAN);
        ALCHEMY.d.put("scroll_retribution", ItemSpriteSheet.SCROLL_KAUNAN);
        ALCHEMY.d.put("scroll_terror",      ItemSpriteSheet.SCROLL_KAUNAN);
        ALCHEMY.d.put("scroll_tp",          ItemSpriteSheet.SCROLL_ODAL);
        ALCHEMY.d.put("scroll_alter",       ItemSpriteSheet.SCROLL_ODAL);
        ALCHEMY.d.put("scroll_upgrade",     ItemSpriteSheet.SCROLL_ODAL);

        //brew,elixir,2 to show all
        ALCHEMY.d.put("potion_brews",       ItemSpriteSheet.BREW_INFERNAL);
        ALCHEMY.d.put("potion_elixirs",     ItemSpriteSheet.ELIXIR_AQUA);
        //spells, 2, 1 general//useless, 1 detail
        ALCHEMY.d.put("spell_spells",       ItemSpriteSheet.PHASE_SHIFT);
        ALCHEMY.d.put("enforcers_details",  ItemSpriteSheet.LIQUID_METAL);
        //MOBS
        //ch1
        MOBS.d.put("ch1",                   DictSpriteSheet.AREA_SEWER);
        MOBS.d.put("mob_rat",               DictSpriteSheet.RAT);
        MOBS.d.put("mob_albino",            DictSpriteSheet.ALBINO);
        MOBS.d.put("mob_snake",             DictSpriteSheet.SNAKE);
        MOBS.d.put("mob_gnoll",             DictSpriteSheet.GNOLL);
        MOBS.d.put("mob_frat",              DictSpriteSheet.F_RAT);
        MOBS.d.put("mob_crab",              DictSpriteSheet.CRAB);
        MOBS.d.put("mob_gnoll_darter",      DictSpriteSheet.GNOLL_DARTER);
        MOBS.d.put("mob_swarm",             DictSpriteSheet.SWARM);
        MOBS.d.put("mob_slime",             DictSpriteSheet.SLIME);
        MOBS.d.put("mob_caustic_slime",     DictSpriteSheet.CAUSTIC_SLIME);
        MOBS.d.put("mob_great_crab",        DictSpriteSheet.GREAT_CRAB);
        MOBS.d.put("ch1_div",               ItemSpriteSheet.RAT_SKULL);
        MOBS.d.put("mob_goo",               DictSpriteSheet.BOSS_CHAPTER1);

        //ch2
        MOBS.d.put("ch2",                   DictSpriteSheet.AREA_PRISON);
        MOBS.d.put("mob_thief",             DictSpriteSheet.THIEF);
        MOBS.d.put("mob_bandit",            DictSpriteSheet.BANDIT);
        MOBS.d.put("mob_skeleton",          DictSpriteSheet.SKELETON);
        MOBS.d.put("mob_dm100",             DictSpriteSheet.DM100);
        MOBS.d.put("mob_guard",             DictSpriteSheet.GUARD);
        MOBS.d.put("mob_necro",             DictSpriteSheet.NECROMANCER);
        MOBS.d.put("mob_spe_necro",         DictSpriteSheet.SPECTRAL_NECROMANCER);
        MOBS.d.put("mob_rot_heart",         DictSpriteSheet.ROT_HEART);
        MOBS.d.put("mob_rot_lash",          DictSpriteSheet.ROT_LASHER);
        MOBS.d.put("mob_new_ele_fire",      DictSpriteSheet.NEW_FIRE_ELE);
        MOBS.d.put("ch2_div",               ItemSpriteSheet.RAT_SKULL);
        MOBS.d.put("mob_tengu",             DictSpriteSheet.BOSS_CHAPTER2);

        //ch3
        MOBS.d.put("ch3",                   DictSpriteSheet.AREA_CAVE);
        MOBS.d.put("mob_bat",               DictSpriteSheet.BAT);
        MOBS.d.put("mob_brute",             DictSpriteSheet.BRUTE);
        MOBS.d.put("mob_armored_brute",     DictSpriteSheet.ARMORED_BRUTE);
        MOBS.d.put("mob_shaman",            DictSpriteSheet.SHAMAN);
        MOBS.d.put("mob_spinner",           DictSpriteSheet.SPINNER);
        MOBS.d.put("mob_dm200",             DictSpriteSheet.DM200);
        MOBS.d.put("mob_dm201",             DictSpriteSheet.DM201);
        MOBS.d.put("ch3_div",               ItemSpriteSheet.RAT_SKULL);
        MOBS.d.put("mob_pylon",             DictSpriteSheet.PYLON);
        MOBS.d.put("mob_dm300",             DictSpriteSheet.BOSS_CHAPTER3);

        //ch4
        MOBS.d.put("ch4",                   DictSpriteSheet.AREA_CITY);
        MOBS.d.put("mob_ghoul",             DictSpriteSheet.GHOUL);
        MOBS.d.put("mob_warlock",           DictSpriteSheet.WARLOCK);
        MOBS.d.put("mob_ele_fire",          DictSpriteSheet.ELEMENTAL_FIRE);
        MOBS.d.put("mob_ele_frost",         DictSpriteSheet.ELEMENTAL_FROST);
        MOBS.d.put("mob_ele_shock",         DictSpriteSheet.ELEMENTAL_SHOCK);
        MOBS.d.put("mob_ele_chaos",         DictSpriteSheet.ELEMENTAL_CHAOS);
        MOBS.d.put("mob_monk",              DictSpriteSheet.MONK);
        MOBS.d.put("mob_senior",            DictSpriteSheet.SENIOR);
        MOBS.d.put("mob_golem",             DictSpriteSheet.GOLEM);
        MOBS.d.put("ch4_div",               ItemSpriteSheet.RAT_SKULL);
        MOBS.d.put("mob_king",              DictSpriteSheet.BOSS_CHAPTER4);

        //ch5
        MOBS.d.put("ch5",                   DictSpriteSheet.AREA_HALL);
        MOBS.d.put("mob_spawner",           DictSpriteSheet.SPAWNER);
        MOBS.d.put("mob_ripper",            DictSpriteSheet.RIPPER);
        MOBS.d.put("mob_succubus",          DictSpriteSheet.SUCCUBUS);
        MOBS.d.put("mob_eye",               DictSpriteSheet.EYE);
        MOBS.d.put("mob_scorpio",           DictSpriteSheet.SCORPIO);
        MOBS.d.put("mob_acidic",            DictSpriteSheet.AICDIC);
        MOBS.d.put("ch5_div",               ItemSpriteSheet.RAT_SKULL);
        MOBS.d.put("mob_fist1",             DictSpriteSheet.FIST_1);
        MOBS.d.put("mob_fist2",             DictSpriteSheet.FIST_2);
        MOBS.d.put("mob_fist3",             DictSpriteSheet.FIST_3);
        MOBS.d.put("mob_larva",             DictSpriteSheet.LARVA);
        MOBS.d.put("mob_yog",               DictSpriteSheet.BOSS_CHAPTER5);

        //normal
        MOBS.d.put("chn",                   ItemSpriteSheet.CHEST);
        MOBS.d.put("mob_statue",            DictSpriteSheet.STATUE);
        MOBS.d.put("mob_armored_statue",    DictSpriteSheet.ARMORED_STATUE);
        MOBS.d.put("mob_fish",              DictSpriteSheet.FISH);
        MOBS.d.put("mob_mimic",             DictSpriteSheet.MIMIC);
        MOBS.d.put("mob_mimic_golden",      DictSpriteSheet.MIMIC_GOLDEN);
        MOBS.d.put("mob_mimic_crystal",     DictSpriteSheet.MIMIC_CRYSTAL);
        MOBS.d.put("mob_wraith",            DictSpriteSheet.WRAITH);
        MOBS.d.put("mob_bee",               DictSpriteSheet.BEE);
        MOBS.d.put("mob_tormented_spirit",  DictSpriteSheet.TORMENTED_SPIRIT);
        MOBS.d.put("mob_phantom_piranha",   DictSpriteSheet.PHANTOM_PIRANHA);
        MOBS.d.put("mob_ghost",             DictSpriteSheet.SAD_GHOST);
        MOBS.d.put("mob_wandmaker",         DictSpriteSheet.WAND_MAKER);
        MOBS.d.put("mob_blacksmith",        DictSpriteSheet.BLACKSMITH);
        MOBS.d.put("mob_imp",               DictSpriteSheet.IMP);
        MOBS.d.put("mob_image",             DictSpriteSheet.IMAGE);
        MOBS.d.put("mob_pris_image",        DictSpriteSheet.PRISMATIC_IMAGE);
        MOBS.d.put("mob_ratking",           DictSpriteSheet.RAT_KING);
        MOBS.d.put("mob_sheep",             DictSpriteSheet.SHEEP);
        MOBS.d.put("mob_red_sentry",        DictSpriteSheet.RED_SENTRY);

        //mine
        MOBS.d.put("mob_crystal_spire",     DictSpriteSheet.CRYSTAL_SPIRE);
        MOBS.d.put("mob_crystal_guardian",  DictSpriteSheet.CRYSTAL_GUARDIAN);
        MOBS.d.put("mob_crystal_wisp",      DictSpriteSheet.CRYSTAL_WISP);
        MOBS.d.put("mob_gnoll_geomancer",   DictSpriteSheet.GNOLL_GEOMANCER);
        MOBS.d.put("mob_gnoll_guard",       DictSpriteSheet.GNOLL_GUARD);
        MOBS.d.put("mob_gnoll_sapper",      DictSpriteSheet.GNOLL_SAPPER);


        //ring
        RINGS.d.put("ring_accuracy",        ItemSpriteSheet.RING_AGATE);
        RINGS.d.put("ring_element",         ItemSpriteSheet.RING_AGATE);
        RINGS.d.put("ring_arcana",          ItemSpriteSheet.RING_AGATE);
        RINGS.d.put("ring_energy",          ItemSpriteSheet.RING_DIAMOND);
        RINGS.d.put("ring_evasion",         ItemSpriteSheet.RING_DIAMOND);
        RINGS.d.put("ring_force",           ItemSpriteSheet.RING_DIAMOND);
        RINGS.d.put("ring_furor",           ItemSpriteSheet.RING_EMERALD);
        RINGS.d.put("ring_haste",           ItemSpriteSheet.RING_EMERALD);
        RINGS.d.put("ring_might",           ItemSpriteSheet.RING_EMERALD);
        RINGS.d.put("ring_shoot",           ItemSpriteSheet.RING_QUARTZ);
        RINGS.d.put("ring_tenacity",        ItemSpriteSheet.RING_QUARTZ);
        RINGS.d.put("ring_wealth",          ItemSpriteSheet.RING_QUARTZ);

        //wand
        WANDS.d.put("wand_blastwave",       ItemSpriteSheet.WAND_BLAST_WAVE);
        WANDS.d.put("wand_corrosion",       ItemSpriteSheet.WAND_CORROSION);
        WANDS.d.put("wand_corruption",      ItemSpriteSheet.WAND_CORRUPTION);
        WANDS.d.put("wand_beam",            ItemSpriteSheet.WAND_DISINTEGRATION);
        WANDS.d.put("wand_fireblast",       ItemSpriteSheet.WAND_FIREBOLT);
        WANDS.d.put("wand_frost",           ItemSpriteSheet.WAND_FROST);
        WANDS.d.put("wand_lightning",       ItemSpriteSheet.WAND_LIGHTNING);
        WANDS.d.put("wand_soil",            ItemSpriteSheet.WAND_LIVING_EARTH);
        WANDS.d.put("wand_magicmissile",    ItemSpriteSheet.WAND_MAGIC_MISSILE);
        WANDS.d.put("wand_light",           ItemSpriteSheet.WAND_PRISMATIC_LIGHT);
        WANDS.d.put("wand_grass",           ItemSpriteSheet.WAND_REGROWTH);
        WANDS.d.put("wand_transfusion",     ItemSpriteSheet.WAND_TRANSFUSION);
        WANDS.d.put("wand_warding",         ItemSpriteSheet.WAND_WARDING);
        //weapon, melee
        WEAPONS.d.put("melee_wornsword",    ItemSpriteSheet.WORN_SHORTSWORD);
        WEAPONS.d.put("melee_gloves",       ItemSpriteSheet.GLOVES);
        WEAPONS.d.put("melee_dagger",       ItemSpriteSheet.DAGGER);
        WEAPONS.d.put("melee_staff",        ItemSpriteSheet.MAGES_STAFF);
        WEAPONS.d.put("melee_rapier",       ItemSpriteSheet.RAPIER);
        WEAPONS.d.put("melee_shortsword",   ItemSpriteSheet.SHORTSWORD);
        WEAPONS.d.put("melee_handaxe",      ItemSpriteSheet.HAND_AXE);
        WEAPONS.d.put("melee_spear",        ItemSpriteSheet.SPEAR);
        WEAPONS.d.put("melee_quarterstaff", ItemSpriteSheet.QUARTERSTAFF);
        WEAPONS.d.put("melee_dirk",         ItemSpriteSheet.DIRK);
        WEAPONS.d.put("melee_sickle",       ItemSpriteSheet.SICKLE);
        WEAPONS.d.put("melee_sword",        ItemSpriteSheet.SWORD);
        WEAPONS.d.put("melee_mace",         ItemSpriteSheet.MACE);
        WEAPONS.d.put("melee_scimitar",     ItemSpriteSheet.SCIMITAR);
        WEAPONS.d.put("melee_roundshield",  ItemSpriteSheet.ROUND_SHIELD);
        WEAPONS.d.put("melee_sai",          ItemSpriteSheet.SAI);
        WEAPONS.d.put("melee_whip",         ItemSpriteSheet.WHIP);
        WEAPONS.d.put("melee_katana",       ItemSpriteSheet.KATANA);
        WEAPONS.d.put("melee_longsword",    ItemSpriteSheet.LONGSWORD);
        WEAPONS.d.put("melee_battleaxe",    ItemSpriteSheet.BATTLE_AXE);
        WEAPONS.d.put("melee_flail",        ItemSpriteSheet.FLAIL);
        WEAPONS.d.put("melee_runicblade",   ItemSpriteSheet.RUNIC_BLADE);
        WEAPONS.d.put("melee_assassin",     ItemSpriteSheet.ASSASSINS_BLADE);
        WEAPONS.d.put("melee_bow",          ItemSpriteSheet.CROSSBOW);
        WEAPONS.d.put("melee_war_scythe",   ItemSpriteSheet.WAR_SCYTHE);
        WEAPONS.d.put("melee_greatsword",   ItemSpriteSheet.GREATSWORD);
        WEAPONS.d.put("melee_hammer",       ItemSpriteSheet.WAR_HAMMER);
        WEAPONS.d.put("melee_glaive",       ItemSpriteSheet.GLAIVE);
        WEAPONS.d.put("melee_greataxe",     ItemSpriteSheet.GREATAXE);
        WEAPONS.d.put("melee_greatshield",  ItemSpriteSheet.GREATSHIELD);
        WEAPONS.d.put("melee_gauntlet",     ItemSpriteSheet.GAUNTLETS);
        WEAPONS.d.put("melee_cudgel",       ItemSpriteSheet.CUDGEL);
        //duelist ability
        WEAPONS.d.put("duelist_ability",    ItemSpriteSheet.WEAPON_HOLDER);
        //ench & curse
        WEAPONS.d.put("weapon_ench1",       ItemSpriteSheet.STONE_ENCHANT);
        WEAPONS.d.put("weapon_ench2",       ItemSpriteSheet.STONE_ENCHANT);
        WEAPONS.d.put("weapon_ench3",       ItemSpriteSheet.STONE_ENCHANT);
        WEAPONS.d.put("weapon_ench4",       ItemSpriteSheet.STONE_ENCHANT);
        //missile
        WEAPONS.d.put("missile_dart",       ItemSpriteSheet.DART);
        WEAPONS.d.put("missile_darttipped", ItemSpriteSheet.PARALYTIC_DART);
        WEAPONS.d.put("missile_stone",      ItemSpriteSheet.THROWING_STONE);
        WEAPONS.d.put("missile_knife",      ItemSpriteSheet.THROWING_KNIFE);
        WEAPONS.d.put("missile_fishing",    ItemSpriteSheet.FISHING_SPEAR);
        WEAPONS.d.put("missile_shuriken",   ItemSpriteSheet.SHURIKEN);
        WEAPONS.d.put("missile_club",       ItemSpriteSheet.THROWING_CLUB);
        WEAPONS.d.put("missile_spear",      ItemSpriteSheet.THROWING_SPEAR);
        WEAPONS.d.put("missile_kunai",      ItemSpriteSheet.KUNAI);
        WEAPONS.d.put("missile_bolas",      ItemSpriteSheet.BOLAS);
        WEAPONS.d.put("missile_javelin",    ItemSpriteSheet.JAVELIN);
        WEAPONS.d.put("missile_boomerang",  ItemSpriteSheet.BOOMERANG);
        WEAPONS.d.put("missile_axe",        ItemSpriteSheet.TOMAHAWK);
        WEAPONS.d.put("missile_hammer",     ItemSpriteSheet.THROWING_HAMMER);
        WEAPONS.d.put("missile_trident",    ItemSpriteSheet.TRIDENT);
        WEAPONS.d.put("missile_cube",       ItemSpriteSheet.FORCE_CUBE);
        WEAPONS.d.put("missile_spiritbow",  ItemSpriteSheet.SPIRIT_BOW);
        //trinkets
        TRINKETS.d.put("trinket_trinket_catalyst",ItemSpriteSheet.TRINKET_CATA);
        TRINKETS.d.put("trinket_dimensional_sundial",ItemSpriteSheet.SUNDIAL);
        TRINKETS.d.put("trinket_exotic_crystals",ItemSpriteSheet.EXOTIC_CRYSTALS);
        TRINKETS.d.put("trinket_eye_of_newt",ItemSpriteSheet.EYE_OF_NEWT);
        TRINKETS.d.put("trinket_mimic_tooth",ItemSpriteSheet.MIMIC_TOOTH);
        TRINKETS.d.put("trinket_mossy_clump",ItemSpriteSheet.MOSSY_CLUMP);
        TRINKETS.d.put("trinket_parchment_scrap",ItemSpriteSheet.PARCHMENT_SCRAP);
        TRINKETS.d.put("trinket_petrified_seed",ItemSpriteSheet.PETRIFIED_SEED);
        TRINKETS.d.put("trinket_rat_skull",ItemSpriteSheet.RAT_SKULL);
        TRINKETS.d.put("trinket_clover",ItemSpriteSheet.CLOVER);
        TRINKETS.d.put("trinket_trap_mechanism",ItemSpriteSheet.TRAP_MECHANISM);
        TRINKETS.d.put("trinket_wondrous_resin",ItemSpriteSheet.WONDROUS_RESIN);
        TRINKETS.d.put("trinket_salt_cube",ItemSpriteSheet.SALT_CUBE);
        TRINKETS.d.put("trinket_vial_of_blood",ItemSpriteSheet.BLOOD_VIAL);
        TRINKETS.d.put("trinket_shard_of_oblivion",ItemSpriteSheet.OBLIVION_SHARD);
        TRINKETS.d.put("trinket_chaotic_censer",ItemSpriteSheet.CHAOTIC_CENSER);
        //Documents
        DOCUMENTS.d.put("info_intro",       ItemSpriteSheet.EBONY_CHEST);
        DOCUMENTS.d.put("info_tier",        ItemSpriteSheet.GLAIVE);
        DOCUMENTS.d.put("info_melee",       ItemSpriteSheet.GREATAXE);
        DOCUMENTS.d.put("info_ranged",      ItemSpriteSheet.TOMAHAWK);
        DOCUMENTS.d.put("info_armor",       ItemSpriteSheet.ARMOR_PLATE);
        DOCUMENTS.d.put("info_wand",        ItemSpriteSheet.WAND_LIVING_EARTH);
        DOCUMENTS.d.put("info_wand_cursed", ItemSpriteSheet.CURSE_INFUSE);
        DOCUMENTS.d.put("info_ring",        ItemSpriteSheet.RING_AMETHYST);
        DOCUMENTS.d.put("info_artifact",    ItemSpriteSheet.ARTIFACT_CHALICE3);
        DOCUMENTS.d.put("info_scroll",      ItemSpriteSheet.SCROLL_ISAZ);
        DOCUMENTS.d.put("info_potion",      ItemSpriteSheet.POTION_BISTRE);
        DOCUMENTS.d.put("info_lim_drop",    ItemSpriteSheet.POTION_CRIMSON);
        DOCUMENTS.d.put("info_food",        ItemSpriteSheet.RATION);
        DOCUMENTS.d.put("info_priority",    ItemSpriteSheet.ARTIFACT_HOURGLASS);
        DOCUMENTS.d.put("info_hero",        DictSpriteSheet.HERO);
        DOCUMENTS.d.put("info_mob",         DictSpriteSheet.RAT);
        DOCUMENTS.d.put("info_property",    DictSpriteSheet.ELEMENTAL_CHAOS);
        DOCUMENTS.d.put("info_spawn",       DictSpriteSheet.SPAWNER);
        DOCUMENTS.d.put("info_shop",        ItemSpriteSheet.GOLD);
        DOCUMENTS.d.put("info_feeling",     DictSpriteSheet.AREA_HALL);
        DOCUMENTS.d.put("info_chasm",       DictSpriteSheet.CHASM);
        DOCUMENTS.d.put("info_lck_floor",   DictSpriteSheet.LOCKED_FLOOR);
        DOCUMENTS.d.put("info_ranking",     ItemSpriteSheet.AMULET);
        DOCUMENTS.d.put("info_bones",       ItemSpriteSheet.REMAINS);
        DOCUMENTS.d.put("fight_mech",       ItemSpriteSheet.DAGGER);
//        DOCUMENTS.d.put("quest_ghost",      DictSpriteSheet.SAD_GHOST);
//        DOCUMENTS.d.put("quest_windmaker",  DictSpriteSheet.WAND_MAKER);
        DOCUMENTS.d.put("quest_blacksmith", DictSpriteSheet.BLACKSMITH);
        DOCUMENTS.d.put("quest_blacksmith1", DictSpriteSheet.CRYSTAL_SPIRE);
//        DOCUMENTS.d.put("quest_imp",        DictSpriteSheet.IMP);

        //unclassified
        //buffs
        UNCLASSIFIED.d.put("buff_neg1",     DictSpriteSheet.BUFF_NEGATIVE);
        UNCLASSIFIED.d.put("buff_pos1",     DictSpriteSheet.BUFF_POSITIVE);
        //traps
        UNCLASSIFIED.d.put("trap_1",        DictSpriteSheet.TRAP_GREEN_RECT);

        UNCLASSIFIED.d.put("misc_amulet_curse",  DictSpriteSheet.BUFF_AMULET_CURSE);
        UNCLASSIFIED.d.put("misc_sac_fire", DictSpriteSheet.BUFF_SAC_FIRE);
        //miscs
        UNCLASSIFIED.d.put("misc_ankh",  ItemSpriteSheet.ANKH);
        UNCLASSIFIED.d.put("misc_seal",  ItemSpriteSheet.SEAL);
        UNCLASSIFIED.d.put("misc_gold",  ItemSpriteSheet.GOLD);
        UNCLASSIFIED.d.put("misc_dust",  ItemSpriteSheet.DUST);
        UNCLASSIFIED.d.put("misc_ore",  ItemSpriteSheet.ORE);
        UNCLASSIFIED.d.put("misc_soon",  ItemSpriteSheet.SOMETHING);
    }

}
