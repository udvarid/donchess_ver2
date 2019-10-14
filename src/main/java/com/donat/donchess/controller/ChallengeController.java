package com.donat.donchess.controller;

import com.donat.donchess.dto.challange.ChallengeActionDto;
import com.donat.donchess.dto.challange.ChallengeCreateDto;
import com.donat.donchess.dto.challange.ChallengeDto;
import com.donat.donchess.service.ChallengeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    //TODO Mappert bekapcsolni
    //TODO entity_ cuccot használni a specificationok miatt
    @GetMapping("/listForTheRequester")
    public Set<ChallengeDto> listForTheRequester() {
        return challengeService.findAllForTheRequester();
    }


    @PostMapping("/create")
    public ResponseEntity create(@RequestBody ChallengeCreateDto challengeCreateDto) {
        challengeService.create(challengeCreateDto);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/answer")
    public ResponseEntity answer(@RequestBody ChallengeActionDto challengeActionDto) {
        challengeService.manageAnswer(challengeActionDto);
        return new ResponseEntity(HttpStatus.OK);
    }


}
