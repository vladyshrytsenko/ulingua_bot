package bot.telegram.umelon.ulingua.model.enums;

public enum UserState {
    AWAITING_NEW_WORD,
    AWAITING_CONVERSATION,
    AWAITING_SENTENCE,
    AWAITING_SENTENCE_DISCUSS;

    private String customDescription;

    public String getDescription() {
        return customDescription == null ? null : customDescription;
    }

    public void setDescription(String customDescription) {
        this.customDescription = customDescription;
    }
}
