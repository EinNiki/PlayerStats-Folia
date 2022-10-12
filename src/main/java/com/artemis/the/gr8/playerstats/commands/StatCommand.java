package com.artemis.the.gr8.playerstats.commands;

import com.artemis.the.gr8.playerstats.ThreadManager;
import com.artemis.the.gr8.playerstats.enums.StandardMessage;
import com.artemis.the.gr8.playerstats.enums.Target;
import com.artemis.the.gr8.playerstats.msg.OutputManager;
import com.artemis.the.gr8.playerstats.statistic.InternalStatRequest;
import com.artemis.the.gr8.playerstats.statistic.StatRequest;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class StatCommand implements CommandExecutor {

    private static ThreadManager threadManager;
    private static OutputManager outputManager;

    public StatCommand(OutputManager m, ThreadManager t) {
        threadManager = t;
        outputManager = m;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {  //in case of less than 1 argument or "help", display the help message
            outputManager.sendHelp(sender);
        }
        else if (args[0].equalsIgnoreCase("examples") ||
                args[0].equalsIgnoreCase("example")) {  //in case of "statistic examples", show examples
            outputManager.sendExamples(sender);
        }
        else {
            StatRequest<TextComponent> request = new InternalStatRequest(sender, args);
            if (request.isValid()) {
                threadManager.startStatThread(request);
            } else {
                sendFeedback(sender, request);
                return false;
            }
        }
        return true;
    }

    /**
     * If a given {@link StatRequest} object does not result in a valid
     * statistic look-up, this will send a feedback message to the CommandSender
     * that made the request. The following is checked:
     * <ul>
     * <li>Is a <code>statistic</code> set?
     * <li>Is a <code>subStatEntry</code> needed, and if so, is a corresponding Material/EntityType present?
     * <li>If the <code>target</code> is Player, is a valid <code>playerName</code> provided?
     * </ul>
     *
     * @param sender the CommandSender to send feedback to
     * @param request the StatRequest to give feedback on
     */
    private void sendFeedback(CommandSender sender, StatRequest<?> request) {
        StatRequest.Settings settings = request.getSettings();

        if (settings.getStatistic() == null) {
            outputManager.sendFeedbackMsg(sender, StandardMessage.MISSING_STAT_NAME);
        }
        else if (settings.getTarget() == Target.PLAYER && settings.getPlayerName() == null) {
            outputManager.sendFeedbackMsg(sender, StandardMessage.MISSING_PLAYER_NAME);
        }
        else {
            Statistic.Type type = settings.getStatistic().getType();
            if (type != Statistic.Type.UNTYPED && settings.getSubStatEntryName() == null) {
                outputManager.sendFeedbackMsgMissingSubStat(sender, type);
            } else {
                outputManager.sendFeedbackMsgWrongSubStat(sender, type, settings.getSubStatEntryName());
            }
        }
    }
}