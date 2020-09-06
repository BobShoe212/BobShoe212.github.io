/*
 * Copyright (c) 2020 BobShoe212
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.BobShoe212.antighost;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;  //BobShoe212 added import for BlockPlacedEvent
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class AntiGhost extends JavaPlugin implements Listener, CommandExecutor {
	boolean active = true;

    public void onEnable(){
        Bukkit.getPluginManager().registerEvents(this, this);
    }

	//when a player damages a block while not on the ground or in creative flight, 
	//sends a block update to the client after a delay to prevent client side ghost blocks 
	//when the client thinks it should have instamine
    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)		//BobShoe212 This one could be removed if it causes issues having 2 event handlers
    public void onBlockDamage(BlockDamageEvent event){
        if(!active) return;
        Player p = event.getPlayer();
        Location l = event.getBlock().getLocation();
        if(p.isOnGround()) return;		//BobShoe212 activated the on ground and creative flying checks to prevent lag from unneccessary tasks
        if(p.isFlying()) return;
        //Block block = event.getBlock();
        Bukkit.getScheduler().runTaskLater(this, updateBlock(p, l) , 3);  //sends a block update to the client after a delay, to remove ghost blocks
    }
	
	//when a player places a block, when not on the ground or in creative flight,
	//sends a block update to the client after a delay
	//may need to increase or decrease the delay depending on the delay of the claim system removing the placed block
	@EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)		//BobShoe212 added another event for BlockPlacedEvent to prevent ghost blocks from players placing blocks 
    public void onBlockPlaced(BlockPlaceEvent event){
        if(!active) return;
        Player p = event.getPlayer();
        Location l = event.getBlock().getLocation();
        if(p.isOnGround()) return;		//BobShoe212 activated the on ground and creative flying checks to prevent lag from unneccessary tasks
        if(p.isFlying()) return;  
        //Block block = event.getBlock();		moving this into the scheduled task, as it needs to get the block details after the claim system removes the block
        Bukkit.getScheduler().runTaskLater(this, updateBlock(p, l) , 3); //sends a block update to the client after a delay, to remove ghost blocks
    }
	
	
	@SuppressWarnings("deprecation")
	private Runnable updateBlock(Player p, Location l) {
		Block b = l.getBlock();
		p.sendBlockChange(l, b.getType(), b.getData());
		return null;
	}

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(!(sender instanceof Player)){
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        if(args != null && args.length > 0){
            if(args[0].equalsIgnoreCase("toggle")){
                active = !active;
                for(Player p: Bukkit.getOnlinePlayers()){
                    if(p.isOp()){
                        p.sendMessage(String.format("Ghost Block protection is now %s", active ? "active" : "disabled"));
                    }
                }
                return true;
            }
            return false;
        }
        Player player = (Player)sender;									//BobShoe212 these commands below are for testing use and could be removed to prevent OP's from getting free gear. 
        ItemStack pic = new ItemStack(Material.DIAMOND_PICKAXE);		//BobShoe212 Or could be used as a benefit for them
        ItemMeta meta = pic.getItemMeta();
        meta.setUnbreakable(true);
        meta.addEnchant(Enchantment.DIG_SPEED, 5, true);
        pic.setItemMeta(meta);
        player.getInventory().addItem(pic);
        player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 60000, 2, true, false), true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 60000, 1, true, false), true);
        return true;
    }
}
