package ca.richasf.textui;

import java.io.PrintStream;
import java.util.Scanner;
import java.util.function.Consumer;

/**
 * A prompt for the user to input a value.
 * 
 * @param <T> The type of value this prompt asks for.
 */
public class Prompt<T> {

    /**
     * Parses a string to an integer.
     * 
     * @param raw The raw string.
     * @return The parsed int.
     * @throws ParseException If the string is not an int.
     */
    private static Integer parseInt(String raw) throws ParseException {
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            throw new ParseException();
        }
    }

    /**
     * Get a prompt that asks for an integer.
     * 
     * @return The prompt.
     */
    public static Prompt<Integer> integer() {
        return new Prompt<>(Prompt::parseInt);
    }

    /**
     * Get a prompt that asks for a string.
     * 
     * @return The prompt.
     */
    public static Prompt<String> string() {
        return new Prompt<>((v) -> v);
    }

    private String message, error;
    private Parser<T> parser;
    private Validator<T> validator = Validator.pass();

    /**
     * Creates a prompt.
     * 
     * @param parser The parser to parse an input line into a value.
     */
    Prompt(Parser<T> parser) {
        this.parser = parser;
    }

    /**
     * Fluent API for setting the request message.
     * 
     * @param message The request message.
     * @return This prompt.
     */
    public Prompt<T> message(String message) {
        this.message = message;
        return this;
    }

    /**
     * Fluent API for setting the error message.
     * 
     * @param error The error message.
     * @return This prompt.
     */
    public Prompt<T> error(String error) {
        this.error = error;
        return this;
    }

    /**
     * Fluent API for setting the validator.
     * 
     * @param validator The validator.
     * @return This prompt.
     */
    public Prompt<T> validator(Validator<T> validator) {
        this.validator = validator;
        return this;
    }

    /**
     * Prompts the user and returns the value.
     * 
     * @param in  The input to read from.
     * @param out The output to write to.
     * @return Value given by user.
     */
    public T run(Scanner in, PrintStream out) {
        boolean shouldRead = true;
        T value = null;
        while (shouldRead) {
            out.print(message == null ? this : message);
            try {
                value = parser.parse(in.nextLine());
                validator.validate(value);
                shouldRead = false;
            } catch (ParseException | ValidateException e) {
                out.println(error == null ? e : error);
            } catch (Exception e) {
                out.printf("An unknown error occurred: %s\n", e);
            }
        }
        return value;
    }

    /**
     * Prompts the user with callback.
     * 
     * @param in       The input to read from.
     * @param out      The output to write to.
     * @param callback The callback to accept the value.
     */
    public void run(Scanner in, PrintStream out, Consumer<T> callback) {
        var value = run(in, out);
        callback.accept(value);
    }
}
