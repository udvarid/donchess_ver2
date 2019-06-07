package com.donat.donchess.service;

import com.donat.donchess.domain.Challenge;
import com.donat.donchess.domain.User;
import com.donat.donchess.dto.challange.ChallengeActionDto;
import com.donat.donchess.dto.challange.ChallengeCreateDto;
import com.donat.donchess.dto.challange.ChallengeDto;
import com.donat.donchess.repository.ChallengeRepository;
import com.donat.donchess.repository.UserRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class ChallengeService {

    private ChallengeRepository challengeRepository;
    private UserRepository userRepository;

    public ChallengeService(ChallengeRepository challengeRepository, UserRepository userRepository) {
        this.challengeRepository = challengeRepository;
        this.userRepository = userRepository;
    }

    public Set<ChallengeDto> findAll() {

        List<Challenge> challenges = challengeRepository.findAll();
        Set<ChallengeDto> challengeDtos = new HashSet<>();

        challenges.forEach(challenge -> {
            ChallengeDto challengeDto = new ChallengeDto();
            if (challenge.getChallenged() != null) {
                challengeDto.setChallengedId(challenge.getChallenged().getId());
            }
            challengeDto.setChallengerId(challenge.getChallenger().getId());
            challengeDto.setId(challenge.getId());
        });

        return challengeDtos;
    }

    public void create(ChallengeCreateDto challengeCreateDto) throws Exception {

        //TODO security check: a beküldő id-ja megegyezik a Challenged id-jával?

        User challenger = userRepository.findById(challengeCreateDto.getChallengerId())
                .orElseThrow(() -> new Exception("Not valid challenger id"));

        User challenged = null;

        if (challengeCreateDto.getChallengedId() != null) {
            if (challengeCreateDto.getChallengedId().equals(challengeCreateDto.getChallengerId())) {
                throw new Exception("Same Id at challenger and challenged");
            }

            challenged = userRepository.findById(challengeCreateDto.getChallengedId())
                    .orElseThrow(() -> new Exception("Not valid challenged id"));

            for (Challenge challenge : challengeRepository.findAll()) {
                if (activeAndSameChallange(challenge, challengeCreateDto)) {
                    throw new Exception("There is a same challenge!");
                }
            }
        }

        Challenge newChallenge = new Challenge();
        newChallenge.setChallenged(challenged);
        newChallenge.setChallenger(challenger);

        challengeRepository.saveAndFlush(newChallenge);
    }

    private boolean activeAndSameChallange(Challenge challenge, ChallengeCreateDto challengeCreateDto) {
        return !challenge.getChallenger().getId().equals(challengeCreateDto.getChallengerId()) &&
                !challenge.getChallenged().getId().equals(challengeCreateDto.getChallengedId());
    }

    public void manageAnswer(ChallengeActionDto challengeActionDto) throws Exception {

        Challenge challenge = challengeRepository.findById(challengeActionDto.getChallengeId())
                .orElseThrow(() -> new Exception("Not valid challenge id"));

        //TODO validálni a Action parancsokat (DELETE, ACCEPT, DECLINE)
        //TODO kiszedni a beküldő User-t, mert ha nyitott kihívást fogadott el, be kell írni
        //TODO security check : Ha az action DELETE, akkor a beküldő ID-ja megegyezik e challenger id-jával
        //TODO security check : Ha az action DECLINE, akkor a beküldő ID-ja megegyezik e challenged id-jával ill. ilyen esetben van e challenged user
        //TODO security check: Ha az action DECLINE vagy ACCEPT, akkor a beküldő ID-ja nem egyezik meg a chellenger id-jával

        if (challengeActionDto.getChallengeAction().equals("ACCEPT")) {
            //TODO új játék létrehozása
        }
        challengeRepository.delete(challenge);

    }
}
