package me.asofold.bpl.cncp.utils;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

/**
 * Simple plugin fetching.
 * @author mc_dev
 *
 * @param <T>
 */
public final class PluginGetter<T extends Plugin> implements Listener{
	private T plugin = null;
	private final String pluginName;
	public PluginGetter(final String pluginName){
		this.pluginName = pluginName;
		fetchPlugin();
	}
	
	/**
	 * Fetch from Bukkit and set , might set to null, though.
	 */
	@SuppressWarnings("unchecked")
	public void fetchPlugin() {
		final Plugin ref = Bukkit.getPluginManager().getPlugin(pluginName);
		plugin = (T) ref;
	}
	
	@SuppressWarnings("unchecked")
    void onPluginEnable(final PluginEnableEvent event){
		final Plugin ref = event.getPlugin();
		if (plugin.getName().equals(pluginName)) plugin = (T) ref;
	}
	
	/**
	 * For convenience with chaining: getX = new PluginGetter<X>("X").registerEvents(this);
	 * @param other
	 * @return
	 */
	public PluginGetter<T> registerEvents(final Plugin other){
		Bukkit.getPluginManager().registerEvents(this, plugin);
		return this;
	}
	
	public T getPlugin(){
		return plugin;
	}
}
