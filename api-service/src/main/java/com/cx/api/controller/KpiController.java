package com.cx.api.controller;

import com.cx.api.entity.ExperienceKpi;
import com.cx.api.repo.ExperienceKpiRepo;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/kpis")
@CrossOrigin(origins = "*")
public class KpiController {
  private final ExperienceKpiRepo repo;

  public KpiController(ExperienceKpiRepo repo) {
    this.repo = repo;
  }

  @GetMapping("/latest")
  public List<ExperienceKpi> latest(
      @RequestParam(defaultValue = "50") int limit
  ) {
    return repo.findAll(PageRequest.of(0, limit)).getContent()
        .stream()
        .sorted((a,b) -> b.getWindowEnd().compareTo(a.getWindowEnd()))
        .toList();
  }

  @GetMapping("/range")
  public List<ExperienceKpi> range(
      @RequestParam Instant from,
      @RequestParam Instant to
  ) {
    return repo.findAll().stream()
        .filter(k -> !k.getWindowStart().isBefore(from) && !k.getWindowEnd().isAfter(to))
        .toList();
  }
}