package seedu.duke.utils;

import seedu.duke.objects.AlertList;
import seedu.duke.objects.Inventory;
import seedu.duke.types.Types;
import seedu.duke.utils.parsers.AddParser;
import seedu.duke.utils.parsers.AlertParser;
import seedu.duke.utils.parsers.AutoSaveParser;
import seedu.duke.utils.parsers.EditParser;
import seedu.duke.utils.parsers.FilterParser;
import seedu.duke.utils.parsers.HelpParser;
import seedu.duke.utils.parsers.HistoryParser;
import seedu.duke.utils.parsers.ListParser;
import seedu.duke.utils.parsers.RemoveParser;
import seedu.duke.utils.parsers.SearchParser;

import java.util.Scanner;

public class ParserHandler {
    private static Scanner in = new Scanner(System.in);
    private Inventory inventory;
    private AlertList alertList;

    public ParserHandler(Inventory inventory, AlertList alertList) {
        this.inventory = inventory;
        this.alertList = alertList;
    }

    public void run() {
        String command = in.nextLine().trim();
        String[] splitCommand = command.split(" ", 2);
        String commandWord = splitCommand[0];
        String commandInfo;
        if (splitCommand.length > 1) {
            commandInfo = splitCommand[1];
        } else {
            commandInfo = "";
        }
        switch (commandWord.toLowerCase()) {
        case "bye":
        case "exit":
            Ui.printExitMessage();
            System.exit(0);
            break;
        case "add":
            AddParser addParser = new AddParser(commandInfo, inventory);
            addParser.run();
            break;
        case "edit":
            EditParser editParser = new EditParser(commandInfo, inventory);
            editParser.run();
            break;
        case "list":
            ListParser listParser = new ListParser(inventory);
            listParser.run();
            break;
        case "search":
            SearchParser searchParser = new SearchParser(commandInfo, inventory, Types.SearchType.KEYWORD);
            searchParser.run();
            break;
        case "searchupc":
            SearchParser searchParserUPC = new SearchParser(commandInfo, inventory, Types.SearchType.UPC);
            searchParserUPC.run();
            break;
        case "filter":
            FilterParser filterParser = new FilterParser(commandInfo, inventory);
            filterParser.run();
            break;
        case "remove":
            RemoveParser removeParser = new RemoveParser(commandInfo, inventory);
            removeParser.run();
            break;
        case "alert":
            AlertParser alertParser = new AlertParser(commandInfo, inventory, alertList);
            alertParser.run();
            break;
        case "help":
            HelpParser helpParser = new HelpParser();
            helpParser.run();
            break;
        case "autosave":
            AutoSaveParser autoSaveParser = new AutoSaveParser(commandInfo, inventory);
            autoSaveParser.run();
            break;
        case "db":
            Ui.printDashboard(inventory, alertList);
            break;
        case "history":
            HistoryParser historyParser = new HistoryParser(commandInfo, inventory);
            historyParser.run();
            break;
        default:
            Ui.printUnknownCommand();
            break;
        }
    }
}
