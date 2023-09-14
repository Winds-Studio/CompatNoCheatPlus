package me.asofold.bpl.cncp.utils;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.entity.Player;

import fr.neatmonster.nocheatplus.checks.CheckType;
import fr.neatmonster.nocheatplus.hooks.NCPExemptionManager;

public class TickTask2 implements Runnable {

	
	protected static final Map<CheckType, Set<Player>> exemptions = new LinkedHashMap<>(40);

	/**
	 * Quick fix, meant for sync access (!).
	 * @param player
	 * @param checkTypes
	 */
	public static void addUnexemptions(final Player player, final CheckType[] checkTypes){
        for (final CheckType type : checkTypes) {
            Set<Player> set = exemptions.computeIfAbsent(type, k -> new HashSet<>());
            set.add(player);
        }
	}
	
	@Override
	public void run() {
		for (final Entry<CheckType, Set<Player>> entry : exemptions.entrySet()){
			final Set<Player> set = entry.getValue();
			final CheckType type = entry.getKey();
			for (final Player player : set){
				NCPExemptionManager.unexempt(player, type);
			}
		}
		exemptions.clear();
	}

}
