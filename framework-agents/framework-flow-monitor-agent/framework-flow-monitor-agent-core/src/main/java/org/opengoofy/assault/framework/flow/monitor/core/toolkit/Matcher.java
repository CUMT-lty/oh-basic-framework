package org.opengoofy.assault.framework.flow.monitor.core.toolkit;

/**
 * Matcher.
 */
@FunctionalInterface
public interface Matcher<T> {
    
    /**
     * Returns {@code true} if this matches {@code t}, {@code false} otherwise.
     */
    boolean match(T t);
}
