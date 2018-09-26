package com.jhz.luckyboyunity;

/**
 * Created by wang on 2018/9/18.
 */

public class GameAnswer {
    private String question;//问题
    private String rightAnswer;//正确答案
    private String wrongAnswer;//错误答案

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getRightAnswer() {
        return rightAnswer;
    }

    public void setRightAnswer(String rightAnswer) {
        this.rightAnswer = rightAnswer;
    }

    public String getWrongAnswer() {
        return wrongAnswer;
    }

    public void setWrongAnswer(String wrongAnswer) {
        this.wrongAnswer = wrongAnswer;
    }
}
