package libraries.observer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A Subject is a special kind of Observer that invokes other Observers
 * rather than just any callback.
 */
public class Subject<T> extends Observer<T> {
    /** A list of observers to update when this subject is updated. */
    private List<Observer<T>> myObservers;

    /**
     * Constructs a subject class with no observers.
     */
    public Subject() {
        this.myObservers = new ArrayList<>();
    }
    
    /**
     * Converts a callback into an observer and adds it to the observer list.
     * 
     * @param callback The callback to convert.
     */
    public void addObserver(Consumer<T> callback) {
        this.addObserver(new Observer<T>(callback));
    }
    
    /**
     * Adds the given observer to the observer list.
     * 
     * @param observer The observer to add.
     */
    public void addObserver(Observer<T> observer) {
        this.myObservers.add(observer);
    }

    /**
     * Adds the given subject to the observerlist.
     * 
     * @param subject The subject to add.
     */
    public void addObserver(Subject<T> subject) {
        this.addObserver(subject);
    }

    /**
     * Creates a subject that is triggered based on the state passed when this
     * subject is updated.
     * 
     * @param trigger The function that decides whether or not to trigger the 
     * derived subject.
     * @return The derived subject.
     */
    public Subject<T> getDerivedSubject(Function<T, Boolean> trigger) {
        Subject<T> subject = new Subject<>();

        this.addObserver((T state) -> {
            if (trigger.apply(state)) {
                subject.update(state);
            }
        });

        return subject;
    }

    /**
     * Creates a subject that updates its observers with an alternative version
     * of the state passed into this subject.
     * 
     * @param <U> The new state's type.
     * @param conversion The function that converts the state from type T to 
     * type U.
     * @return The new subject.
     */
    public <U> Subject<U> getConvertedSubject(Function<T, U> conversion) {
        Subject<U> subject = new Subject<>();

        this.addObserver((T state) -> {
            subject.update(conversion.apply(state));
        });
        
        return subject;
    }
    
    /**
     * Triggers every observer with the passed state.
     */
    @Override
    public void update(T state) {
        for (final Observer<T> observer : this.myObservers) {
            observer.update(state);
        }
    }
}
