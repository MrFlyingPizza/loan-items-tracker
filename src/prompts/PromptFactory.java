package prompts;

import java.io.PrintStream;
import java.util.Scanner;

public interface PromptFactory {
    @FunctionalInterface
    public interface Requester {
        void request(PrintStream out);
    }

    @FunctionalInterface
    public interface Receiver<T> {
        T receive(Scanner in) throws BadUserInputException;
    }

    public static <T> Prompt<T> create(Requester requester, Receiver<T> receiver) {
        return new Prompt<T>() {
            @Override
            public void request(PrintStream out) {
                requester.request(out);
            }

            @Override
            public T receive(Scanner in) throws BadUserInputException {
                return receiver.receive(in);
            }
        };
    }
}
