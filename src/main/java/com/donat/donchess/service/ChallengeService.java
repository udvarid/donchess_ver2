package com.donat.donchess.service;

import com.donat.donchess.domain.Challenge;
import com.donat.donchess.domain.ChessGame;
import com.donat.donchess.domain.QChallenge;
import com.donat.donchess.domain.User;
import com.donat.donchess.domain.enums.ChessGameStatus;
import com.donat.donchess.domain.enums.ChessGameType;
import com.donat.donchess.domain.enums.Result;
import com.donat.donchess.dto.challange.ChallengeAction;
import com.donat.donchess.dto.challange.ChallengeActionDto;
import com.donat.donchess.dto.challange.ChallengeCreateDto;
import com.donat.donchess.dto.challange.ChallengeDto;
import com.donat.donchess.exceptions.InvalidException;
import com.donat.donchess.exceptions.NotFoundException;
import com.donat.donchess.model.enums.Color;
import com.donat.donchess.repository.ChallengeRepository;
import com.donat.donchess.repository.ChessGameRepository;
import com.donat.donchess.repository.UserRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.inject.Provider;
import javax.persistence.EntityManager;
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
    private ChessGameRepository chessGameRepository;

    @Autowired
    private Provider<EntityManager> entityManager;

    public ChallengeService(ChallengeRepository challengeRepository, UserRepository userRepository,
                            SecurityService securityService, ChessGameRepository chessGameRepository) {
        this.challengeRepository = challengeRepository;
        this.userRepository = userRepository;
        this.securityService = securityService;
        this.chessGameRepository = chessGameRepository;
    }

    public Set<ChallengeDto> findAll() {

        JPAQueryFactory query = new JPAQueryFactory(entityManager);
        QChallenge challengeFromQ = QChallenge.challenge;


        List<Challenge> challenges = query
                .selectFrom(challengeFromQ)
                .orderBy(challengeFromQ.creatonTime.asc())
                .fetch();
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

    public void create(ChallengeCreateDto challengeCreateDto) {

        User challenger = securityService.getChallenger();

        User challenged = null;

        if (challengeCreateDto.getChallengedId() != null) {
            if (challengeCreateDto.getChallengedId().equals(challenger.getId())) {
                throw new InvalidException("Same Id at challenger and challenged with id: ", challenger.getId());
            }

            challenged = userRepository.findById(challengeCreateDto.getChallengedId())
                    .orElseThrow(() -> new NotFoundException("Not valid challenged id: ", challengeCreateDto.getChallengedId()));

            if (!challenged.isEnabled()) {
                throw new InvalidException("Challenged user is not activated yet with id: ", challenged.getId());
            }

            for (Challenge challenge : challengeRepository.findAll()) {
                if (activeAndSameChallange(challenge, challenger, challenged)) {
                    throw new InvalidException("There is a same challenge, challenge id: ", challenge.getId());
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
        return challenge.getChallenger().equals(challenger) &&
                challenge.getChallenged().equals(challenged);
    }

    public void manageAnswer(ChallengeActionDto challengeActionDto) {

        Challenge challenge = challengeRepository.findById(challengeActionDto.getChallengeId())
                .orElseThrow(() -> new NotFoundException("Not valid challenge id: ", challengeActionDto.getChallengeId()));

        if (!validActions(challengeActionDto)) {
            throw new InvalidException("Not valid action!");
        }
        User answerGiver = securityService.getChallenger();

        if (challengeActionDto.getChallengeAction().equals(ChallengeAction.DELETE.name())) {
            if (!challenge.getChallenger().equals(answerGiver)) {
                throw new InvalidException("Only the creator of challenger can delete it!");
            }
        } else {
            if (challenge.getChallenger().equals(answerGiver)) {
                throw new InvalidException("Only the challenger can decline or accept it!");
            }
            if (challengeActionDto.getChallengeAction().equals(ChallengeAction.DECLINE.name()) &&
                    !answerGiver.equals(challenge.getChallenged())) {
                throw new InvalidException("Only the challenged User can decline!");
            }
            if (challengeActionDto.getChallengeAction().equals(ChallengeAction.ACCEPT.name())) {
                challenge.setChallenged(answerGiver);
                //TODO új játék létrehozása
                createNewGame(challenge.getChallenger(), challenge.getChallenged());
                System.out.println("New game has been created!");
            }
        }
        challengeRepository.delete(challenge);

    }

    private void createNewGame(User challenger, User challenged) {
        ChessGame chessGame = new ChessGame();
        chessGame.setChessGameStatus(ChessGameStatus.OPEN);
        chessGame.setChessGameType(ChessGameType.NORMAL);
        chessGame.setLastMoveId(0);
        chessGame.setResult(Result.OPEN);
        chessGame.setNextMove(Color.WHITE);
        if (Math.random() < 0.5) {
            chessGame.setUserOne(challenger);
            chessGame.setUserTwo(challenged);
        } else {
            chessGame.setUserOne(challenged);
            chessGame.setUserTwo(challenger);
        }

        chessGameRepository.save(chessGame);
    }

    private boolean validActions(ChallengeActionDto challengeActionDto) {
        return EnumUtils.isValidEnum(ChallengeAction.class, challengeActionDto.getChallengeAction());
    }
}
