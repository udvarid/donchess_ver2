package com.donat.donchess.dto.challange;

public class ChallengeActionDto {

    private Long challengeId;

    private ChallengeAction challengeAction;

    public Long getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(Long challengeId) {
        this.challengeId = challengeId;
    }

    public ChallengeAction getChallengeAction() {
        return challengeAction;
    }

    public void setChallengeAction(ChallengeAction challengeAction) {
        this.challengeAction = challengeAction;
    }
}
