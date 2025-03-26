package fun.wsss.hmh.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import fun.wsss.hmh.dao.ReimburseDao;
import fun.wsss.hmh.entity.Reimburse;
import fun.wsss.hmh.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.time.ZoneId;

@Service
public class StatsServiceImpl implements StatsService {

    @Autowired
    private ReimburseDao reimburseDao;

    @Override
    public Map<String, Object> getSummary() {
        Map<String, Object> result = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        String currentMonth = now.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        
        // 本月报销总额
        LambdaQueryWrapper<Reimburse> monthlyTotalQuery = new LambdaQueryWrapper<>();
        monthlyTotalQuery.apply("DATE_FORMAT(create_time, '%Y-%m') = {0}", currentMonth)
                        .ne(Reimburse::getStatus, 0);
        Double monthlyTotal = reimburseDao.selectList(monthlyTotalQuery)
                .stream()
                .mapToDouble(Reimburse::getMoney)
                .sum();
        
        // 待审批报销数
        LambdaQueryWrapper<Reimburse> pendingQuery = new LambdaQueryWrapper<>();
        pendingQuery.eq(Reimburse::getStatus, 1);
        Long pendingCount = reimburseDao.selectCount(pendingQuery);
        
        // 本月报销人数
        long monthlyUserCount = reimburseDao.selectList(monthlyTotalQuery)
                .stream()
                .map(Reimburse::getSqUserId)
                .distinct()
                .count();
        
        // 同比增长率
        String lastMonth = now.minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM"));
        LambdaQueryWrapper<Reimburse> lastMonthQuery = new LambdaQueryWrapper<>();
        lastMonthQuery.apply("DATE_FORMAT(create_time, '%Y-%m') = {0}", lastMonth)
                     .ne(Reimburse::getStatus, 0);
        
        Double lastMonthTotal = reimburseDao.selectList(lastMonthQuery)
                .stream()
                .mapToDouble(Reimburse::getMoney)
                .sum();
        
        double growthRate = lastMonthTotal == 0 ? 0 : 
                           ((monthlyTotal - lastMonthTotal) / lastMonthTotal * 100);
        
        result.put("monthlyTotal", monthlyTotal);
        result.put("pendingCount", pendingCount);
        result.put("monthlyUserCount", monthlyUserCount);
        result.put("growthRate", String.format("%.1f", growthRate));
        
        return result;
    }

    @Override
    public List<Map<String, Object>> getExpenseTrend(String timeRange) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = switch (timeRange) {
            case "week" -> endDate.minusDays(7);
            case "month" -> endDate.minusDays(30);
            case "year" -> endDate.minusMonths(12);
            default -> throw new IllegalArgumentException("Invalid time range");
        };
        
        LambdaQueryWrapper<Reimburse> query = new LambdaQueryWrapper<>();
        query.ge(Reimburse::getCreateTime, startDate)
             .le(Reimburse::getCreateTime, endDate)
             .ne(Reimburse::getStatus, 0)
             .orderByAsc(Reimburse::getCreateTime);
        
        List<Reimburse> reimburses = reimburseDao.selectList(query);
        
        String pattern = timeRange.equals("year") ? "yyyy-MM" : "yyyy-MM-dd";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        
        return reimburses.stream()
                .collect(Collectors.groupingBy(
                    reimburse -> {
                        // 转换 Date 为 LocalDateTime
                        LocalDateTime localDateTime = reimburse.getCreateTime()
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime();
                        return localDateTime.format(formatter);
                    },
                    HashMap::new,
                    Collectors.summingDouble(Reimburse::getMoney)
                ))
                .entrySet().stream()
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("date", entry.getKey());
                    map.put("amount", entry.getValue());
                    return map;
                })
                .sorted(Comparator.comparing(m -> m.get("date").toString()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getExpenseTypes() {
        LambdaQueryWrapper<Reimburse> query = new LambdaQueryWrapper<>();
        query.ne(Reimburse::getStatus, 0);
        
        List<Reimburse> reimburses = reimburseDao.selectList(query);
        
        Map<String, Long> typeCount = reimburses.stream()
                .collect(Collectors.groupingBy(
                    Reimburse::getTypeName,
                    Collectors.counting()
                ));
        
        return typeCount.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", entry.getKey());
                    map.put("value", entry.getValue());
                    return map;
                })
                .collect(Collectors.toList());
    }
} 