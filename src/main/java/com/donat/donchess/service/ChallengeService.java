package com.donat.donchess.service;

import com.donat.donchess.domain.Challenge;
import com.donat.donchess.dto.ChallengeDto;
import com.donat.donchess.repository.ChallengeRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ChallengeService {

    private ChallengeRepository challengeRepository;

    public ChallengeService(ChallengeRepository challengeRepository) {

        this.challengeRepository = challengeRepository;
    }

    public Set<ChallengeDto> findAll() {

        List<Challenge> challenges = challengeRepository.findAll();
        Set<ChallengeDto> challengeDtos = new HashSet<>();

        challenges.forEach(challenge -> {
            if (challenge.getStatus().equals("OPEN")) {
                ChallengeDto challengeDto = new ChallengeDto();
                if (challenge.getChallenged() != null) {
                    challengeDto.setChallengedId(challenge.getChallenged().getId());
                }
                challengeDto.setChallengerId(challenge.getChallenger().getId());
                challengeDto.setId(challenge.getId());
            }
        });

        return challengeDtos;
    }
}
