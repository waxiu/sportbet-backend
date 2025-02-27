package com.example.sportbet.controller;

import com.example.sportbet.dto.ApiResponse;
import com.example.sportbet.dto.response.UserRankingResponseDto;
import com.example.sportbet.service.LeaderboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/leaderboard")
public class LeaderboardController {

    private static final Logger logger = LoggerFactory.getLogger(LeaderboardController.class);

    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @GetMapping("/winners")
    public ResponseEntity<ApiResponse<List<UserRankingResponseDto>>> getLeaderboard(@RequestParam(defaultValue = "10") int top) {
        logger.info("Fetching top {} users from the leaderboard", top);

        Set<ZSetOperations.TypedTuple<Long>> leaderboard = leaderboardService.getTopUsers(top);

        List<UserRankingResponseDto> rankings = leaderboard.stream()
                .map(entry -> new UserRankingResponseDto(
                        entry.getValue(),
                        entry.getScore()
                ))
                .collect(Collectors.toList());

        logger.info("Leaderboard fetched successfully, returning {} entries", rankings.size());
        return ResponseEntity.ok(new ApiResponse<>(true, "Leaderboard fetched successfully", rankings));
    }
}
