package seedu.recruit.logic.commands.emailcommand;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import seedu.recruit.logic.CommandHistory;
import seedu.recruit.logic.LogicManager;
import seedu.recruit.model.Model;
import seedu.recruit.model.ModelManager;
import seedu.recruit.model.UserPrefs;

public class EmailContentsBackCommandTest {

    private CommandHistory commandHistory = new CommandHistory();
    private UserPrefs userPrefs = new UserPrefs();
    private Model model = new ModelManager();

    @Test
    public void execute_emailContentsBackCommand() {
        LogicManager logic = new LogicManager(model, userPrefs);
        new EmailContentsBackCommand().execute(model, commandHistory, userPrefs);
        Assertions.assertEquals(EmailRecipientsCommand.COMMAND_LOGIC_STATE, logic.getState().nextCommand);
    }
}
