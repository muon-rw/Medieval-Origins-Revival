package dev.muon.medievalorigins.util;

import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.apoli.util.HarvestContext;
import io.github.apace100.apoli.util.SavedBlockPosition;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Stack-based helper to preserve HarvestContext across nested block breaks (e.g., radial mining).
 * Includes safeguards against memory leaks from exceptions or unexpected behavior.
 */
public class HarvestContextStack {
    
    /**
     * Vibe check
     * If higher than this, clear the stack to prevent leaks
     */
    private static final int MAX_DEPTH = 1024;
    
    private static final ThreadLocal<Deque<SavedContext>> CONTEXT_STACK = 
            ThreadLocal.withInitial(ArrayDeque::new);

    /**
     * Store only the essential data, not the full SavedBlockPosition which holds world references.
     */
    private record SavedContext(ServerLevel level, BlockPos pos, boolean canHarvest) {}

    /**
     * Saves current HarvestContext to the stack if it exists.
     * Call this before setting new context values.
     */
    public static void pushCurrentContext() {
        SavedBlockPosition currentBlockPos = HarvestContext.getBlockPosition();
        Boolean currentCanHarvest = HarvestContext.getCanHarvest();
        
        if (currentBlockPos != null && currentCanHarvest != null) {
            Deque<SavedContext> stack = CONTEXT_STACK.get();
            
            // Vibe check: if stack is suspiciously deep, clear it to prevent memory issues
            if (stack.size() >= MAX_DEPTH) {
                MedievalOrigins.LOG.warn("HarvestContextStack exceeded max depth ({}), clearing to prevent memory leak. " +
                        "This may indicate an issue with nested block breaking.", MAX_DEPTH);
                stack.clear();
                return;
            }
            
            // Store minimal data needed to reconstruct context
            stack.push(new SavedContext(
                    (ServerLevel) currentBlockPos.getLevel(),
                    currentBlockPos.getPos(),
                    currentCanHarvest
            ));
        }
    }

    /**
     * Restores the previous HarvestContext from the stack if one exists.
     * Call this after clearing current context values.
     */
    public static void popAndRestoreContext() {
        Deque<SavedContext> stack = CONTEXT_STACK.get();
        if (!stack.isEmpty()) {
            SavedContext previous = stack.pop();
            HarvestContext.setBlockPosition(previous.level(), previous.pos());
            HarvestContext.setCanHarvest(previous.canHarvest());
        }
    }
    
    /**
     * Emergency clear for use if needed (e.g., on player disconnect or dimension change).
     */
    public static void clear() {
        CONTEXT_STACK.get().clear();
    }
}
