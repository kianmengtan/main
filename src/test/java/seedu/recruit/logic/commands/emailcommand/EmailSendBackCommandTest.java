package seedu.recruit.logic.commands.emailcommand;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import seedu.recruit.logic.CommandHistory;
import seedu.recruit.logic.LogicManager;
import seedu.recruit.model.ModelManager;
import seedu.recruit.model.UserPrefs;

public class EmailSendBackCommandTest {

    private CommandHistory commandHistory = new CommandHistory();
    private UserPrefs userPrefs = new UserPrefs();
    private ModelManager model = new ModelManager();

    @Test
    public void execute_emailSendBackCommand() {
        LogicManager logic = new LogicManager(model, userPrefs);
        new EmailSendBackCommand().execute(model, commandHistory, userPrefs);
        Assertions.assertEquals(EmailContentsCommand.COMMAND_LOGIC_STATE, logic.getState().nextCommand);
    }
}
