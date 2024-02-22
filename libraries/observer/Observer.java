package libraries.observer;

import java.util.function.Consumer;

/**
 * Observers represent a class that invokes its callback once updated
 * (preferrably) by a subject.
 */
public class Observer<T> {
    /**
     * Does nothing.
     * 
     * @param state This state will be ignored.
     */
    public static void doNothing(Object state) { }
    
    /** The function to call when update is invoked. */
    private Consumer<T> myCallback;
    
    /**
     * Constructs an observer with a default callback of Observer.doNothing()
     * which does nothing.
     */
    public Observer() {
        this.myCallback = Observer::doNothing;
    }
    
    /**
     * Constructs an observer with the given callback.
     */
    public Observer(Consumer<T> callback) {
        this.myCallback = callback;
    }
    
    /**
     * Triggers the callback of this observer with the passed state.
     * 
     * @param state The state to pass into the callback.
     */
    public void update(T state) { 
        this.myCallback.accept(state);
    }

    /**
     * Triggers the callback of this observer with null as the state.
     */
    public void update() {
        this.update(null);
    }
}
