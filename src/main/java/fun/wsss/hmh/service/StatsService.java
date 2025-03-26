package fun.wsss.hmh.service;

import java.util.Map;
import java.util.List;

public interface StatsService {
    Map<String, Object> getSummary();
    List<Map<String, Object>> getExpenseTrend(String timeRange);
    List<Map<String, Object>> getExpenseTypes();
} 