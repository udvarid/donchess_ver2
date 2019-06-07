package com.donat.donchess.controller;

import com.donat.donchess.dto.challange.ChallengeActionDto;
import com.donat.donchess.dto.challange.ChallengeCreateDto;
import com.donat.donchess.dto.challange.ChallengeDto;
import com.donat.donchess.service.ChallengeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/challenge")
public class ChallengeController {

    private ChallengeService challengeService;

    public ChallengeController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    @GetMapping("/list")
    public Set<ChallengeDto> list() {
        return challengeService.findAll();
    }

    //TODO challenge kreálása
    @GetMapping("/create")
    public ResponseEntity create(@RequestBody ChallengeCreateDto challengeCreateDto) throws Exception {
        challengeService.create(challengeCreateDto);
        return new ResponseEntity(HttpStatus.OK);
    }

    //TODO challenge válasz
    @GetMapping("/answer")
    public ResponseEntity answer(@RequestBody ChallengeActionDto challengeActionDto) throws Exception {
        challengeService.manageAnswer(challengeActionDto);
        return new ResponseEntity(HttpStatus.OK);
    }


}
