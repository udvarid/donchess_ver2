package com.donat.donchess.user;

import com.donat.donchess.AbstractApiTest;
import com.donat.donchess.domain.Challenge;
import com.donat.donchess.domain.QChallenge;
import com.donat.donchess.dto.challange.ChallengeCreateDto;
import com.donat.donchess.dto.challange.ChallengeDto;
import com.donat.donchess.repository.ChallengeRepository;
import com.donat.donchess.repository.UserRepository;
import com.donat.donchess.service.SecurityService;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import java.util.List;

import static org.junit.Assert.*;

public class challengeTest extends AbstractApiTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private SecurityService securityService;

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
    }

}

