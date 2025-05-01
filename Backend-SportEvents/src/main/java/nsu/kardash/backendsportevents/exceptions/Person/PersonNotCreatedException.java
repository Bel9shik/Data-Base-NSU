package nsu.kardash.backendsportevents.exceptions.Person;

public class PersonNotCreatedException extends RuntimeException {

    private final String message;

    public PersonNotCreatedException(String message) {
        this.message = message;
    }

}
