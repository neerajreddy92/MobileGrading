package com.mobile.bolt.Model;

/**
 * Created by Neeraj on 3/3/2016.
 */
public class QrCode {
    private int ID;
    private String QUESTION;
    private String QuestionSolution;
    private String VALUES;
    private float MaxGrade;

    public float getMaxGrade() {
        return MaxGrade;
    }

    public void setMaxGrade(float maxGrade) {
        MaxGrade = maxGrade;
    }

    public String getQuestionSolution() {
        return QuestionSolution;
    }

    public void setQuestionSolution(String questionSolution) {
        QuestionSolution = questionSolution;
    }

    public String getQUESTION() {
        return QUESTION;
    }

    public void setQUESTION(String QUESTION) {
        this.QUESTION = QUESTION;
    }

    public String getVALUES() {
        return VALUES;
    }

    public void setVALUES(String VALUES) {
        this.VALUES = VALUES;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    @Override
    public String toString(){
        return ID+" "+QUESTION+" "+QuestionSolution+" "+VALUES+" "+String.valueOf(MaxGrade);
    }
}
