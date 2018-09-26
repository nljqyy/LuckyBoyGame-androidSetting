package com.jhz.luckyboyunity;

import java.util.List;

/**
 * Created by wang on 2018/9/18.
 */

public class GameData {
    private int code;//0，表不存在；1，表存在数据为空；2，有数据
    private String message;
    private List<GameAnswer> gameAnswer;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<GameAnswer> getGameAnswer() {
        return gameAnswer;
    }

    public void setGameAnswer(List<GameAnswer> gameAnswer) {
        this.gameAnswer = gameAnswer;
    }
}
