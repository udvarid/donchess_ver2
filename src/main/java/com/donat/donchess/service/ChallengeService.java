package com.donat.donchess.service;

import com.donat.donchess.domain.Challenge;
import com.donat.donchess.domain.User;
import com.donat.donchess.dto.challange.ChallengeAction;
import com.donat.donchess.dto.challange.ChallengeActionDto;
import com.donat.donchess.dto.challange.ChallengeCreateDto;
import com.donat.donchess.dto.challange.ChallengeDto;
import com.donat.donchess.repository.ChallengeRepository;
import com.donat.donchess.repository.UserRepository;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class ChallengeService {

    private ChallengeRepository challengeRepository;
    private UserRepository userRepository;
    private SecurityService securityService;

    public ChallengeService(ChallengeRepository challengeRepository, UserRepository userRepository,
                            SecurityService securityService) {
        this.challengeRepository = challengeRepository;
        this.userRepository = userRepository;
        this.securityService = securityService;
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
            challengeDtos.add(challengeDto);
        });

        return challengeDtos;
    }

    public void create(ChallengeCreateDto challengeCreateDto) throws Exception {

        User challenger = securityService.getChallenger();

        User challenged = null;

        if (challengeCreateDto.getChallengedId() != null) {
            if (challengeCreateDto.getChallengedId().equals(challenger.getId())) {
                throw new Exception("Same Id at challenger and challenged");
            }

            challenged = userRepository.findById(challengeCreateDto.getChallengedId())
                    .orElseThrow(() -> new Exception("Not valid challenged id"));

            if (!challenged.isEnabled()) {
                throw new Exception("Challegned user is not activated yet");
            }

            for (Challenge challenge : challengeRepository.findAll()) {
                if (activeAndSameChallange(challenge, challenger, challenged)) {
                    throw new Exception("There is a same challenge!");
                }
            }
        }

        Challenge newChallenge = new Challenge();
        newChallenge.setChallenged(challenged);
        newChallenge.setChallenger(challenger);
        newChallenge.setCreatonTime(LocalDateTime.now());

        challengeRepository.saveAndFlush(newChallenge);
    }

    private boolean activeAndSameChallange(Challenge challenge, User challenger, User challenged) {
        return !challenge.getChallenger().equals(challenger) &&
                !challenge.getChallenged().equals(challenged);
    }

    public void manageAnswer(ChallengeActionDto challengeActionDto) throws Exception {

        Challenge challenge = challengeRepository.findById(challengeActionDto.getChallengeId())
                .orElseThrow(() -> new Exception("Not valid challenge id"));

        if (!validActions(challengeActionDto)) {
            throw new Exception("Not valid action!");
        }
        User answerGiver = securityService.getChallenger();

        if (challengeActionDto.getChallengeAction().equals(ChallengeAction.DELETE)) {
            if (!challenge.getChallenger().equals(answerGiver)) {
                throw new Exception("Only the creator of challenger can delete it!");
            }
        } else {
            if (challenge.getChallenger().equals(answerGiver)) {
                throw new Exception("The creator of challenge can't decline or accept it!");
            }
            if (challengeActionDto.getChallengeAction().equals(ChallengeAction.DECLINE)) {
                if (!answerGiver.equals(challenge.getChallenged())) {
                    throw new Exception("Only the challenged User can decline!");
                }
            }
            if (challengeActionDto.getChallengeAction().equals(ChallengeAction.ACCEPT)) {
                if (challenge.getChallenged().equals(null)) {
                    challenge.setChallenged(answerGiver);
                }
                //TODO új játék létrehozása
                System.out.println("New game has been created!");
            }
        }
        challengeRepository.delete(challenge);

    }

    private boolean validActions(ChallengeActionDto challengeActionDto) {
        return EnumUtils.isValidEnum(ChallengeAction.class, challengeActionDto.getChallengeAction().name());
    }
}
