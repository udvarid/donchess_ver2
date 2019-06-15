package com.donat.donchess.user;

import com.donat.donchess.AbstractApiTest;
import com.donat.donchess.domain.Challenge;
import com.donat.donchess.domain.QChallenge;
import com.donat.donchess.dto.challange.ChallengeActionDto;
import com.donat.donchess.dto.challange.ChallengeCreateDto;
import com.donat.donchess.dto.challange.ChallengeDto;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import java.util.List;

import static org.junit.Assert.*;

public class challengeTest extends AbstractApiTest {

    @Autowired
    private Provider<EntityManager> entityManager;

    @Test
    public void createNewChallenge() {
        JPAQueryFactory query = new JPAQueryFactory(entityManager);
        QChallenge challengeFromQ = QChallenge.challenge;

        ChallengeCreateDto challengeCreateDto = new ChallengeCreateDto();
        challengeCreateDto.setChallengedId(2l);

        Challenge challenge = query
                .selectFrom(challengeFromQ)
                .where(challengeFromQ.challenger.id.eq(1l)
                        .and(challengeFromQ.challenged.id.eq(2l)))
                .fetchOne();

        assertNull(challenge);

        List<ChallengeDto> challengesCurrent = challengeApi.getAll();

        loginAsDonat1();
        challengeApi.create(challengeCreateDto);

        Challenge challenge2 = query
                .selectFrom(challengeFromQ)
                .where(challengeFromQ.challenger.id.eq(1l)
                        .and(challengeFromQ.challenged.id.eq(2l)))
                .fetchOne();

        assertNotNull(challenge2);
        assertTrue(challenge2.getChallenger().getId().equals(1l));
        assertTrue(challenge2.getChallenged().getId().equals(2l));

        List<ChallengeDto> challengesAfter = challengeApi.getAll();

        assertTrue(challengesCurrent.size() + 1 == challengesAfter.size());

        ChallengeActionDto challengeActionDto = new ChallengeActionDto();
        challengeActionDto.setChallengeId(challenge2.getId());
        challengeActionDto.setChallengeAction("DELETE");

        challengeApi.answer(challengeActionDto);
    }

    @Test
    public void sameChallengerIdAndChallengedIdDeniedTest() {
        JPAQueryFactory query = new JPAQueryFactory(entityManager);
        QChallenge challengeFromQ = QChallenge.challenge;
        loginAsDonat1();
        ChallengeCreateDto challengeCreateDto = new ChallengeCreateDto();
        challengeCreateDto.setChallengedId(1l);
        shouldFail(() -> challengeApi.create(challengeCreateDto));
    }

    @Test
    public void notValidChallengedIdTest() {
        JPAQueryFactory query = new JPAQueryFactory(entityManager);
        QChallenge challengeFromQ = QChallenge.challenge;
        loginAsDonat1();
        ChallengeCreateDto challengeCreateDto = new ChallengeCreateDto();
        challengeCreateDto.setChallengedId(10000l);
        shouldFail(() -> challengeApi.create(challengeCreateDto));
    }

    @Test
    public void duplicatedChallengeIsDeniedTest() {
        JPAQueryFactory query = new JPAQueryFactory(entityManager);
        QChallenge challengeFromQ = QChallenge.challenge;
        loginAsDonat1();
        ChallengeCreateDto challengeCreateDto = new ChallengeCreateDto();
        challengeCreateDto.setChallengedId(2l);
        challengeApi.create(challengeCreateDto);

        ChallengeCreateDto challengeCreateDto2 = new ChallengeCreateDto();
        challengeCreateDto2.setChallengedId(2l);
        shouldFail(() -> challengeApi.create(challengeCreateDto2));

        Challenge challenge = query
                .selectFrom(challengeFromQ)
                .where(challengeFromQ.challenger.id.eq(1l)
                        .and(challengeFromQ.challenged.id.eq(2l)))
                .fetchOne();

        ChallengeActionDto challengeActionDto = new ChallengeActionDto();
        challengeActionDto.setChallengeId(challenge.getId());
        challengeActionDto.setChallengeAction("DELETE");

        challengeApi.answer(challengeActionDto);
    }

    @Test
    public void notValidChallengeIdTest() {
        JPAQueryFactory query = new JPAQueryFactory(entityManager);
        QChallenge challengeFromQ = QChallenge.challenge;
        loginAsDonat1();

        List<Challenge> challenges = query.selectFrom(challengeFromQ)
                .orderBy(challengeFromQ.id.desc())
                .fetch();

        Long idMax = challenges.size() == 0 ? 1l : challenges.get(0).getId() + 1l;

        System.out.println(idMax);

        ChallengeCreateDto challengeCreateDto = new ChallengeCreateDto();
        challengeCreateDto.setChallengedId(idMax);
        shouldFail(() -> challengeApi.create(challengeCreateDto));
    }

    @Test
    public void invalidActionsDeniedTest() {

    }

    @Test
    public void deleteTest() {

    }

    @Test
    public void ownerOfChallengeNotAbleToDeleteTest() {

    }

    @Test
    public void onlyChallengedCanDeclineOrAcceptTest() {

    }
}

