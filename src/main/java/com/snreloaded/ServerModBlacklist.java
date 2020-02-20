package com.snreloaded;

import java.util.ArrayList;

public class ServerModBlacklist {

    private static ArrayList<String> blacklist = new ArrayList<>();

    public static void initBlacklist()
    {
        blacklist.add("297038");  //CraftPresence (Client only)
        blacklist.add("353051");  //GameMenuModOption (Client Only)
        blacklist.add("331965");  //Enchanted Tooltips
        blacklist.add("272515");  //Better Advancements - Client Only
        blacklist.add("254441");  //Fancy Block Particles
        blacklist.add("309318");  //Dynamic Surroundings HUDs
        blacklist.add("289953");  //Client-side only
        blacklist.add("332668");  //Enemyz - Skull above mobs
        blacklist.add("247111");  //Requires The One Probe, which is already blacklisted.
        blacklist.add("325492");  //Light Overlay
        blacklist.add("334853");  //Discord Rich Presence
        blacklist.add("320204");  //Mappy (1.14) - Client map mod.
        blacklist.add("285684");  //Coloured Tooltips
        blacklist.add("299080");  //Proportional Destruction Particles
        blacklist.add("319264");  //Loading Screens
        blacklist.add("285742");  //Smooth Fonts
        blacklist.add("238747");  //Mod Name Tooltip, extends client-side tooltips.
        blacklist.add("296468");  //NoFog - Removes client-side fog graphics.
        blacklist.add("79287");   //Waila Harvestability
        blacklist.add("254144");  //Client-side mod
        blacklist.add("314002");  //Overloaded Armor Bar (Not req. Server Side)
        blacklist.add("313252");  //Wasaila?
        blacklist.add("284183");  //WaitingTime
        blacklist.add("225125");  //Audio Death
        blacklist.add("282313");  //TipTheScales
        blacklist.add("271740");  //Toast Control
        blacklist.add("282743");  //DiscordSuite
        blacklist.add("236396");  //Mad-help - Client only
        blacklist.add("292992");  //Modpack Configuration Checker - Tries to open a swing UI on the server
        blacklist.add("226188");  //Default world generator - Client Only
        blacklist.add("223094");  //Inventory Tweaks - Client only
        blacklist.add("59489");   //Damage Indicators mod - Client only
        blacklist.add("239236");  //Step Up - Client only
        blacklist.add("225957");  //Hardcore Darkness - Client only
        blacklist.add("227874");  //Dynamic Lights - Client only
        blacklist.add("236484");  //Chunk animator - Client only
        blacklist.add("238120");  //Ambience - Client only
        blacklist.add("231404");  //Advanced capes - Client only
        blacklist.add("226670");  //LLOverlayReloaded - [Overlays] Client-side-mod.
        blacklist.add("229302");  //BetterLoadingScreen - [Loading Screen] Client-side-mod.
        blacklist.add("232791");  //ITIT - It's the little things - [Java version checking, window resizing] Client-side-mod.
        blacklist.add("243478");  //More Overlays - [NEI overlays] Client-side-mod.
        blacklist.add("268324");  //Blur -[Rendering Effect] Client-side mod.
        blacklist.add("238891");  //Dynamic Surroundings - Audio/rendering mod for clients.
        blacklist.add("278476");  //Custom Cursor Mod - Changes the cursor on the client.
        blacklist.add("228529");  //BetterFoliage: Changes the rendering of trees, leaves etc. Client-side-only.
        blacklist.add("241964");  //Java Version Enforcer - Client side only, will cause player logon crashes if enabled server side.
        blacklist.add("225928");  //Custom Backgrounds - Client Only
        blacklist.add("263420");  //Xaero's Minimap - Client Only
        blacklist.add("233577");  //AutoRun - Older versions cause crash
        blacklist.add("226064");  //Twitch Notifier - Client Only
        blacklist.add("247759");  //AutoWalk - Client only
        blacklist.add("221658");  //WTP - What's This Pack - Client Only
        blacklist.add("238372");  //Neat - Client Only
        blacklist.add("237701");  //ReAuth - Client Only
        blacklist.add("229625");  //WAILA-features - Client Only
        blacklist.add("2269420"); //Shadow Tweaks - Client Only
        blacklist.add("2268525"); //Neat - Client Only
        blacklist.add("2242214"); //Chat Flow - Client only
        blacklist.add("256087");  //Notes - Client Only
        blacklist.add("236767");  //ServerInfoViewer - Client Only
        blacklist.add("225565");  //DirectionHUD - client only
        blacklist.add("225566");  //StatusEffectHUD - client only
        blacklist.add("225564");  //ArmorStatusHUD - client only
        blacklist.add("2246472"); //TiC ToolTips - client only
        blacklist.add("2289031"); //aa_java_enforcer - client only
        blacklist.add("2218859"); //Armor Status Hud
        blacklist.add("2260741"); //InGameInfo-XML
        blacklist.add("227979");  //Baby Animals Model Swapper - Client Only
        blacklist.add("254692");  //WorldEditCUI - Client Only
        blacklist.add("242932");  //WorldEditCUI Forge Edition - Client Only
        blacklist.add("261019");  //WorldEditCUI Forge Edition 2 - Client Only
        blacklist.add("228742");  //Quick Hotbar - Client Only
        blacklist.add("229876");  //BetterFps - Client Only
        blacklist.add("250398");  //Controlling - Client Only
        blacklist.add("226099");  //Durability Show - Client Only
        blacklist.add("238371");  //Neat - Client Only
        blacklist.add("232131");  //Default Options - Client Only
        blacklist.add("222789");  //Sound Filters - Client Only
        blacklist.add("60089");   //Mouse Tweaks - Client Only
        blacklist.add("227441");  //Fullscreen Windowed - Client Only
        blacklist.add("251407");  //Client Tweaks - Client Only
        blacklist.add("224223");  //Better Title Screen - Client Only
        blacklist.add("226447");  //Resource Loader - Client Only
        blacklist.add("226406");  //Custom Main Menu - Client Only
        blacklist.add("57829");   //TabbyChat - Client Only
        blacklist.add("232962");  //TabbyChat 2 - Client Only
        blacklist.add("292899");  //ShulkerTooltip - Client Only
        blacklist.add("356821");  //DynamicSurroundings: MobEffects - Client Only
        blacklist.add("355671");  //DynamicSurroundings: SoundControl - Client Only
        blacklist.add("284904");  //NoRecipeBook - Client Only
    }

    public static boolean contains(String id)
    {
        return blacklist.contains(id);
    }
}
