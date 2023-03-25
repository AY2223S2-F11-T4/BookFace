package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.*;

import java.util.List;
import java.util.function.Predicate;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.*;
import seedu.address.model.person.predicates.*;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {

    private Prefix[] possiblePrefixes = {
            PREFIX_NAME,
            PREFIX_STATUS,
            PREFIX_PHONE,
            PREFIX_EMAIL,
            PREFIX_ADDRESS,
            PREFIX_TAG
    };

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public FindCommand parse(String args) throws ParseException {

        if (args.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, possiblePrefixes);

        Predicate<Person> predicate = null;
        for (Prefix p : possiblePrefixes) {
            List<String> prefixArguments = argMultimap.getAllValues(p);
            if (prefixArguments.isEmpty()) {
                continue;
            }
            for (String arg : prefixArguments) {
                if (!isValidArgument(arg)) {
                    throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
                }
                Predicate<Person> currentPredicate = null;
                if (p == PREFIX_NAME) {
                    currentPredicate = new NameContainsKeywordsPredicate(arg);
                } else if (p == PREFIX_STATUS) {
                    currentPredicate = new StatusContainsKeywordsPredicate(arg);
                } else if (p == PREFIX_PHONE) {
                    currentPredicate = new PhoneContainsKeywordsPredicate(arg);
                } else if (p == PREFIX_EMAIL) {
                    currentPredicate = new EmailContainsKeywordsPredicate(arg);
                } else if (p == PREFIX_ADDRESS) {
                    currentPredicate = new AddressContainsKeywordsPredicate(arg);
                } else if (p == PREFIX_TAG) {
                    currentPredicate = new TagContainsKeywordsPredicate(arg);
                }
                assert currentPredicate != null;
                if (predicate == null) {
                    predicate = currentPredicate;
                } else {
                    predicate = predicate.and(currentPredicate);
                }
            }
        }
        if (predicate == null) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        return new FindCommand(predicate);
    }

    private static boolean isValidArgument(String argument) {
        String preppedWord = argument.trim().toLowerCase();
        if (preppedWord.isEmpty() || preppedWord.split("\\s+").length != 1) {
            return false;
        }
        return true;
    }
}
