package ru.florestdev.taxes;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.florestdev.passportManager.PassportData;

import java.util.ArrayList;
import java.util.List;

// КЛАСС ДЛЯ РАБОТЫ С КОМАНДОЙ!
public class CommandActivity implements CommandExecutor, TabCompleter {

    private static Taxes plugin;

    public CommandActivity(Taxes plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length < 3) {
            commandSender.sendMessage(ChatColor.RED + "Неправильное исполнение команды! Нужно: /tax <country/person> <название страны> <количество денег для списания> <ник игрока (если снимается налог у конкретного лица).");
            return true;
        } else if (strings.length == 3) {
            String country = strings[1];
            String count = strings[2];

            if (!plugin.getPassportManager().getConfigManager().countryExists(country)) {
                commandSender.sendMessage(ChatColor.RED + "Вы не можете взять налоги с несуществующей страны!");
                return  true;
            }

            if (!plugin.getPassportManager().getConfigManager().getLeaders(country).contains(commandSender.getName())) {
                commandSender.sendMessage(ChatColor.RED + "Вы не являетесь администратором страны %s и не можете брать налоги.".formatted(country));
                return true;
            }

            commandSender.sendMessage(ChatColor.YELLOW + "Начинаю сбор налогов ваших граждан...");
            int players_gave = 0;
            for (List<PassportData> data:plugin.getPassportManager().getConfigManager().getDatabase().values()) {
                for (PassportData passportData : data) {
                    if (!passportData.getCountry().equals(country)) {
                        continue;
                    }
                    String nickname = passportData.getNickname();
                    if (EconomyManager.canAfford(plugin.getServer().getOfflinePlayer(nickname), Double.parseDouble(count))) {
                        EconomyManager.withdraw(plugin.getServer().getOfflinePlayer(nickname), Double.parseDouble(count));
                        EconomyManager.depositPlayer(plugin.getServer().getOfflinePlayer(commandSender.getName()), Double.parseDouble(count));
                        players_gave += 1;
                    }
                }
            }
            int formatted = players_gave * Integer.parseInt(count);
            commandSender.sendMessage(ChatColor.GREEN + "Сбор налогов выполнен. Вы получили около: %s FMC.".formatted(String.valueOf(formatted)));
        } else if (strings.length == 4) {
            String country = strings[1];
            String count = strings[2];
            String username = strings[3];

            if (!plugin.getPassportManager().getConfigManager().countryExists(country)) {
                commandSender.sendMessage(ChatColor.RED + "Вы не можете взять налоги с несуществующей страны!");
                return  true;
            }

            if (!plugin.getPassportManager().getConfigManager().getLeaders(country).contains(commandSender.getName())) {
                commandSender.sendMessage(ChatColor.RED + "Вы не являетесь администратором страны %s и не можете брать налоги.".formatted(country));
                return true;
            }

            commandSender.sendMessage(ChatColor.YELLOW + "Пытаемся списать с %s сумму %s.".formatted(username, count));
            PassportData human = null;

            for (List<PassportData> data:plugin.getPassportManager().getConfigManager().getDatabase().values()) {
                for (PassportData passportData : data) {
                    if (!passportData.getCountry().equals(country)) {
                        continue;
                    }
                    String nickname = passportData.getNickname();
                    if (!nickname.equals(username)) {
                        continue;
                    }
                    human = passportData;
                }
            }

            if (human == null) {
                commandSender.sendMessage(ChatColor.RED + "%s не является гражданином вашей страны.".formatted(username));
                return  true;
            }

            if (EconomyManager.canAfford(plugin.getServer().getOfflinePlayer(username), Double.parseDouble(count))) {
                EconomyManager.withdraw(plugin.getServer().getOfflinePlayer(username), Double.parseDouble(count));
                EconomyManager.depositPlayer(plugin.getServer().getOfflinePlayer(commandSender.getName()), Double.parseDouble(count));
                commandSender.sendMessage(ChatColor.GREEN + "%s заплатил налог в размере %s.".formatted(username, count));
                return true;
            }

            commandSender.sendMessage(ChatColor.RED + "%s не может заплатить вам эту сумму.".formatted(username));


        } else {commandSender.sendMessage(ChatColor.RED + "Слишком много аргументов!");}
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("country");
            completions.add("person");
        } else if (args.length == 2) {
            completions.addAll(plugin.countries);
        } else if (args.length == 3) {
            completions.add("<сумма>");
        } else if (args.length == 4) {
            String country = args[1];
            for (List<PassportData> data : plugin.getPassportManager().getConfigManager().getDatabase().values()) {
                for (PassportData passportData : data) {
                    if (passportData.getCountry().equals(country)) {
                        completions.add(passportData.getNickname());
                    }
                }
            }
        }

        // Фильтрация по текущему вводу
        List<String> result = new ArrayList<>();
        String last = args[args.length - 1].toLowerCase();
        for (String completion : completions) {
            if (completion.toLowerCase().startsWith(last)) {
                result.add(completion);
            }
        }
        return result;
    }
}