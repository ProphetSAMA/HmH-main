package fun.wsss.hmh.controller;

import fun.wsss.hmh.common.Result;
import fun.wsss.hmh.entity.Announcement;
import fun.wsss.hmh.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/announcement")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping("/list")
    public Result getList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer rows,
            @RequestParam(required = false) String title) {
        var pageResult = announcementService.getList(page, rows, title);
        return Result.success(pageResult);
    }

    @PostMapping("/save")
    public Result save(@RequestBody Announcement announcement) {
        announcementService.saveAnnouncement(announcement);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return Result.success();
    }
}
