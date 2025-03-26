package fun.wsss.hmh.controller;

import fun.wsss.hmh.common.Result;
import fun.wsss.hmh.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stats")
public class StatsController {
    
    @Autowired
    private StatsService statsService;
    
    @GetMapping("/summary")
    public Result getSummary() {
        return Result.success(statsService.getSummary());
    }
    
    @GetMapping("/trend")
    public Result getExpenseTrend(@RequestParam String timeRange) {
        return Result.success(statsService.getExpenseTrend(timeRange));
    }
    
    @GetMapping("/types")
    public Result getExpenseTypes() {
        return Result.success(statsService.getExpenseTypes());
    }
} 