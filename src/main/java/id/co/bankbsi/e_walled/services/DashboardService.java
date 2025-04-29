package id.co.bankbsi.e_walled.services;

import id.co.bankbsi.e_walled.dto.response.Response;
import id.co.bankbsi.e_walled.dto.response.TransactionStatsResponse;
import id.co.bankbsi.e_walled.models.PeriodType;
import id.co.bankbsi.e_walled.models.Users;
import id.co.bankbsi.e_walled.models.Wallets;
import id.co.bankbsi.e_walled.repositories.CustomTransactionRepository;
import id.co.bankbsi.e_walled.repositories.TransactionRepository;
import id.co.bankbsi.e_walled.repositories.DashboardRepository;
import id.co.bankbsi.e_walled.repositories.WalletRepository;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@AllArgsConstructor
public class DashboardService {
    @Autowired
    private final CustomTransactionRepository customTransactionRepository;
    @Autowired
    private final DashboardRepository transactionStatRepository;
    @Autowired
    private final WalletRepository walletRepository;
    @Autowired
    private final TransactionRepository transactionRepository;

    public Response getPieChart(Users user, String range) {
        LocalDateTime nowDate = LocalDateTime.now().with(LocalTime.MIN);

        range = range == null? "" : range;
        LocalDateTime startDate = switch (range) {
            case "weekly" -> nowDate.minusWeeks(1);
            case "monthly" -> nowDate.minusMonths(1);
            case "quarterly" -> nowDate.minusMonths(3);
            case "yearly" -> nowDate.minusYears(1);
            default -> nowDate.minusMonths(1);
        };

        LocalDateTime endDate = nowDate.with(LocalTime.MAX);

        List<TransactionStatsResponse.ChartData> mappedTrans = new ArrayList<TransactionStatsResponse.ChartData>();
        List<Object[]> total = transactionStatRepository.getIncomeExpenseStatistic(
                user,
                null,
                startDate,
                endDate
        );

        mappedTrans.add(extractIncomeExpense("Total", total));
        List<Wallets> wallets = walletRepository.findByUserIdOrderByIsMainDescCreatedAtAsc(user.getId());

        for (Wallets wallet : wallets) {
            List<Object[]> results = transactionStatRepository.getIncomeExpenseStatistic(
                    user,
                    wallet,
                    startDate,
                    endDate
            );

            mappedTrans.add(extractIncomeExpense(wallet.getName(), results));
        }

        return TransactionStatsResponse.success(Arrays.asList("Income", "Expense", "Internal"), mappedTrans);
    }

    public TransactionStatsResponse getBalanceGrowthByPeriod(
            Users user,
            PeriodType period,
            @Nullable LocalDateTime startDate,
            @Nullable LocalDateTime endDate
    ) {
        List<Wallets> wallets = walletRepository.findByUserId(user.getId());

        // Step 1: Determine the period range (labels)
        LocalDateTime start = startDate == null ? user.getCreatedAt().toLocalDate().atStartOfDay() : startDate;
        LocalDateTime end = endDate == null ? LocalDateTime.now() : endDate;

        String periodString = period.toTruncFormat();
        List<List<Object[]>> rawTransactions = new ArrayList<>();
        List<String> transactionLabel = new ArrayList<>();
        List<String> transactionName = new ArrayList<>();
        Map<String, Long> walletInitialBalances = new HashMap<>();
        Long totalBalance = 0L;
        for (Wallets wallet : wallets) {
            rawTransactions.add(transactionStatRepository.getWalletTransactionGrowth(wallet.getId(), user.getId(), periodString, start, end));
            transactionLabel.add(wallet.getNumber());
            transactionName.add(wallet.getName());
            Long tempBalance = customTransactionRepository.sumAmountsFromStartDate(wallet, start);
            totalBalance += (wallet.getBalance() + tempBalance);

            walletInitialBalances.put(wallet.getNumber(), (wallet.getBalance() + tempBalance));
        }
        rawTransactions.add(transactionStatRepository.getTransactionGrowth(user.getId(), periodString, start, end));
        transactionLabel.add("total");
        transactionName.add("total");
        Long tempBalance = customTransactionRepository.sumAmountsFromStartDate(null, start);
        tempBalance = tempBalance < 0? 0 : tempBalance;
        walletInitialBalances.put("total", (totalBalance + tempBalance));

        return getAggregated(rawTransactions, transactionLabel, walletInitialBalances, periodString, transactionName, start, end);
    }

    public List<Map<String, Object>> getTopExpenseByCategory(List<Object[]> rawData, int topN) {
        Map<String, List<Map<String, Object>>> categoryMap = new HashMap<>();
        Map<String, Long> categoryTotals = new HashMap<>();

        for (Object[] row : rawData) {
            String category = (String) row[0];
            String title = (String) row[1];
            Long amount = ((Number) row[2]).longValue();

            // Add transaction to its category
            categoryMap.computeIfAbsent(category, k -> new ArrayList<>())
                    .add(Map.of("title", title, "amount", amount));

            // Add up total per category
            categoryTotals.put(category, categoryTotals.getOrDefault(category, 0L) + amount);
        }

        // Sort by total amount descending
        List<Map.Entry<String, Long>> sortedCategories = categoryTotals.entrySet()
                .stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(topN)
                .toList();

        // Final result
        List<Map<String, Object>> result = new ArrayList<>();
        for (var entry : sortedCategories) {
            String category = entry.getKey();
            Long total = entry.getValue();

            result.add(Map.of(
                    "category", category,
                    "total", total,
                    "transactions", categoryMap.get(category)
            ));
        }

        return result;
    }

    public TransactionStatsResponse.TotalBalance getTotalBalance(
            Users user
    ) {
        Long value = walletRepository.sumBalance(user.getId());

        return TransactionStatsResponse.TotalBalance.success(value);
    }


    public TransactionStatsResponse.TotalBalance getTopExpense(
            Users user
    ) {
        Long value = walletRepository.sumBalance(user.getId());

        return TransactionStatsResponse.TotalBalance.success(value);
    }


    private TransactionStatsResponse.ChartData extractIncomeExpense(String wallet, List<Object[]> results) {

        Long income = (long) 0;
        Long expense = (long) 0;
        Long internal = (long) 0;
        for (Object[] row : results) {
            String type = (String) row[0];
            Long sum = (Long) row[1];

            if ("INCOME".equalsIgnoreCase(type)) {
                income = sum;
            } else if ("EXPENSE".equalsIgnoreCase(type)) {
                expense = sum;
            } else {
                internal = sum;
            }
        }
        return new TransactionStatsResponse.ChartData(wallet, Arrays.asList(income, expense, internal), "", "", 0F, true);
    }

    private TransactionStatsResponse getAggregated(List<List<Object[]>> allRawTransactions, List<String> transactionLabel, Map<String, Long> walletInitialBalances, String periodString, List<String> transactionName, LocalDateTime minDate, LocalDateTime maxDate) {
        Map<String, TreeMap<String, Long>> walletPeriodBalances = new LinkedHashMap<>();
        TreeSet<String> allPeriods = new TreeSet<>();

        for (List<Object[]> walletTxs : allRawTransactions) {
            for (Object[] tx : walletTxs) {
                Timestamp ts = (Timestamp) tx[0];
                LocalDateTime dt = ts.toLocalDateTime();
                if (minDate == null || dt.isBefore(minDate)) minDate = dt;
                if (maxDate == null || dt.isAfter(maxDate)) maxDate = dt;
            }
        }
        Map<String, LocalDateTime> periodToDate = new LinkedHashMap<>();

        ChronoUnit chrono = switch (periodString) {
            case "hour" -> ChronoUnit.HOURS;
            case "day" -> ChronoUnit.DAYS;
            case "week" -> ChronoUnit.WEEKS;
            case "month" -> ChronoUnit.MONTHS;
            case "quarter" -> ChronoUnit.MONTHS;
            case "year" -> ChronoUnit.YEARS;
            default -> throw new IllegalArgumentException("Unsupported period: " + periodString);
        };

        if (periodString.equals("quarter")) {
            minDate = minDate.withMonth(((minDate.getMonthValue() - 1) / 3) * 3 + 1).withDayOfMonth(1);
        }

        LocalDateTime current = minDate;

        while (!current.isAfter(maxDate)) {
            String periodKey = getPeriod(current, periodString);
            allPeriods.add(periodKey);
            periodToDate.put(periodKey, current);
            current = periodString.equals("quarter") ? current.plusMonths(3) : current.plus(1, chrono);
        }

        for (int i = 0; i < allRawTransactions.size(); i++) {
            List<Object[]> rawTransactions = allRawTransactions.get(i);
            String labelName = transactionLabel.get(i);

            TreeMap<String, Long> periodSums = new TreeMap<>();
            Long initialBalance = walletInitialBalances.getOrDefault(labelName, 0L);

            if (!allPeriods.isEmpty()) {
                String firstPeriod = allPeriods.first();
                periodSums.put(firstPeriod, initialBalance);
            }

            for (Object[] tx : rawTransactions) {
                Timestamp ts = (Timestamp) tx[0];
                Long amount = (Long) tx[1];
                boolean isDebit = (boolean) tx[2];

                LocalDateTime dateTime = ts.toLocalDateTime();
                String formattedPeriod = getPeriod(dateTime, periodString);

                long delta = isDebit ? -amount : amount;

                periodSums.put(formattedPeriod, periodSums.getOrDefault(formattedPeriod, 0L) + delta);
                allPeriods.add(formattedPeriod);
            }

// 4. Accumulate balances starting from initialBalance
            long acc = 0;
            for (String key : new TreeSet<>(allPeriods)) {
                acc += periodSums.getOrDefault(key, 0L) == null ? 0L : periodSums.getOrDefault(key, 0L);
                periodSums.put(key, acc);
            }

            walletPeriodBalances.put(labelName, periodSums);
        }

        List<String> sortedPeriods = new ArrayList<>(allPeriods);
        List<TransactionStatsResponse.ChartData> datasets = new ArrayList<>();

        // Fill dataset for each wallet
        int count = 0;
        for (String labelName : transactionLabel) {
            TreeMap<String, Long> periodMap = walletPeriodBalances.get(labelName);

            String walletName = transactionName.get(count);

            List<Long> values = new ArrayList<>();

            long lastVal = 0;
            for (String periodKey : sortedPeriods) {
                if (periodMap.containsKey(periodKey)) {
                    lastVal = periodMap.get(periodKey);
                }
                values.add(lastVal);
            }
            datasets.add(new TransactionStatsResponse.ChartData(walletName, values, "", "", 0.5F, true));
            count++;
        }


        return TransactionStatsResponse.success(sortedPeriods, datasets);
    }

    private List<String> generateTimeLabels(PeriodType period, LocalDateTime start, LocalDateTime end) {
        List<String> labels = new ArrayList<>();
        LocalDateTime current = start.truncatedTo(ChronoUnit.HOURS);


        while (!current.isAfter(end)) {
            labels.add(current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            current = switch (period) {
                case HOURLY -> current.plusHours(1);
                case DAILY -> current.plusDays(1);
                case WEEKLY -> current.plusWeeks(1);
                case MONTHLY -> current.plusMonths(1);
                case QUARTERLY -> current.plusMonths(3);
                case YEARLY -> current.plusYears(1);
            };
        }
        return labels;
    }

    private String formatPeriod(LocalDateTime dt, String period, DateTimeFormatter formatter) {
        return dt.format(formatter);
    }

    private String getPeriod(LocalDateTime dateTime, String truncUnit) {
        DateTimeFormatter formatter;
        switch (truncUnit) {
            case "hour":
                formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00");
                break;
            case "day":
                formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                break;
            case "week":
                formatter = DateTimeFormatter.ofPattern("IYYY-IW");
                break;
            case "month":
                formatter = DateTimeFormatter.ofPattern("yyyy-MM");
                break;
            case "quarter":
                int quarter = (dateTime.getMonthValue() - 1) / 3 + 1;
                formatter = DateTimeFormatter.ofPattern("yyyy-'Q'" + quarter);
                break;
            case "year":
                formatter = DateTimeFormatter.ofPattern("yyyy");
                break;
            default:
                throw new IllegalArgumentException("Unsupported truncUnit: " + truncUnit);
        }
        return dateTime.format(formatter);
    }
}
