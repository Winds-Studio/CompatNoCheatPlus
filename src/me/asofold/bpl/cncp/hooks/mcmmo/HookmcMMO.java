package me.asofold.bpl.cncp.hooks.mcmmo;

import me.asofold.bpl.cncp.config.compatlayer.CompatConfig;
import me.asofold.bpl.cncp.config.compatlayer.CompatConfigFactory;
import me.asofold.bpl.cncp.config.compatlayer.ConfigUtil;
import me.asofold.bpl.cncp.hooks.AbstractHook;
import me.asofold.bpl.cncp.hooks.generic.ConfigurableHook;
import me.asofold.bpl.cncp.utils.PluginGetter;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.events.fake.FakeBlockBreakEvent;
import com.gmail.nossr50.events.fake.FakeBlockDamageEvent;
import com.gmail.nossr50.events.fake.FakeEntityDamageByEntityEvent;

import fr.neatmonster.nocheatplus.checks.CheckType;
import fr.neatmonster.nocheatplus.hooks.NCPHook;

public final class HookmcMMO extends AbstractHook implements Listener, ConfigurableHook {
	
	/**
	 * To let the listener access this.
	 * @author mc_dev
	 *
	 */
	public interface HookFacade{
		void damageLowest(Player player);
		void damageMonitor(Player player);
		void blockDamageLowest(Player player);
		void blockDamageMonitor(Player player);
		/**
		 * If to cancel the event.
		 * @param player
		 * @return
		 */
        boolean blockBreakLowest(Player player);
		void blockBreakMontitor(Player player);
	}
	
	private HookFacade ncpHook = null;
	
	private boolean enabled = true;
	
	private final String configPrefix = "mcmmo.";
	
	private boolean useInstaBreakHook = true;

	private final PluginGetter<mcMMO> fetch = new PluginGetter<>("mcMMO");

	private int blocksPerSecond = 30;
	

	
	@Override
	public String getHookName() {
		return "mcMMO(default)";
	}

	@Override
	public String getHookVersion() {
		return "2.1";
	}

	@Override
	public CheckType[] getCheckTypes() {
		return new CheckType[]{
				CheckType.BLOCKBREAK_FASTBREAK, CheckType.BLOCKBREAK_NOSWING, // old ones
				
//				CheckType.BLOCKBREAK_DIRECTION, CheckType.BLOCKBREAK_FREQUENCY,
//				CheckType.BLOCKBREAK_WRONGBLOCK, CheckType.BLOCKBREAK_REACH,
//				
//				CheckType.FIGHT_ANGLE, CheckType.FIGHT_SPEED, // old ones
//				
//				CheckType.FIGHT_DIRECTION, CheckType.FIGHT_NOSWING,
//				CheckType.FIGHT_REACH, 
			};
	}
	
	@Override
	public Listener[] getListeners() {
		fetch.fetchPlugin();
		return new Listener[]{this, fetch};
	}
	
	@Override
	public NCPHook getNCPHook() {
		if (ncpHook == null){
			ncpHook = new HookFacadeImpl(useInstaBreakHook, blocksPerSecond);
		}
		return (NCPHook) ncpHook;
	}
	
	///////////////////////////
	// Damage (fight)
	//////////////////////////
	
	@EventHandler(priority=EventPriority.LOWEST)
    void onDamageLowest(final FakeEntityDamageByEntityEvent event){
		final Entity entity = event.getDamager();
		if (entity instanceof Player)
			ncpHook.damageLowest((Player) entity);
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
    void onDamageMonitor(final FakeEntityDamageByEntityEvent event){
		final Entity entity = event.getDamager();
		if (entity instanceof Player)
			ncpHook.damageMonitor((Player) entity);
	}
	
	///////////////////////////
	// Block damage
	//////////////////////////
	
	@EventHandler(priority=EventPriority.LOWEST)
    void onBlockDamageLowest(final FakeBlockDamageEvent event){
		ncpHook.blockDamageLowest(event.getPlayer());
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
    void onBlockDamageMonitor(final FakeBlockDamageEvent event){
		ncpHook.blockDamageMonitor(event.getPlayer());
	}
	
	///////////////////////////
	// Block break
	//////////////////////////
	
	@EventHandler(priority=EventPriority.LOWEST)
    void onBlockBreakLowest(final FakeBlockBreakEvent event){
		if (ncpHook.blockBreakLowest(event.getPlayer())){
			event.setCancelled(true);
//			System.out.println("Cancelled for frequency.");
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
    void onBlockBreakLMonitor(final FakeBlockBreakEvent event){
		ncpHook.blockBreakMontitor(event.getPlayer());
	}
	
	/////////////////////////////////
	// Config
	/////////////////////////////////
	
	@Override
	public void applyConfig(CompatConfig cfg, String prefix) {
		enabled = cfg.getBoolean(prefix + configPrefix + "enabled",  true);
		useInstaBreakHook = cfg.getBoolean(prefix + configPrefix + "use-insta-break-hook",  true);
		blocksPerSecond  = cfg.getInt(prefix + configPrefix + "clickspersecond", 20);
	}

	@Override
	public boolean updateConfig(CompatConfig cfg, String prefix) {
		CompatConfig defaults = CompatConfigFactory.getConfig(null);
		defaults.set(prefix + configPrefix + "enabled",  true);
		defaults.set(prefix + configPrefix + "use-insta-break-hook",  true);
		defaults.set(prefix + configPrefix + "clickspersecond", 20);
		return ConfigUtil.forceDefaults(defaults, cfg);
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

}
