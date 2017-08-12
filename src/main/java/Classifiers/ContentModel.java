package Classifiers;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sugeesh Chandraweera
 */
public class ContentModel{
    boolean passedCC;
    boolean passedCF;
    boolean passedDA;
    boolean passedDB;
    boolean passedEC;
    boolean passedEO;
    boolean passedPK;
    boolean passedUC;
    boolean passedWD;

    public ContentModel() {
    }

    public boolean isPassedCC() {
        return passedCC;
    }

    public void setPassedCC(boolean passedCC) {
        this.passedCC = passedCC;
    }

    public boolean isPassedCF() {
        return passedCF;
    }

    public void setPassedCF(boolean passedCF) {
        this.passedCF = passedCF;
    }

    public boolean isPassedDA() {
        return passedDA;
    }

    public void setPassedDA(boolean passedDA) {
        this.passedDA = passedDA;
    }

    public boolean isPassedDB() {
        return passedDB;
    }

    public void setPassedDB(boolean passedDB) {
        this.passedDB = passedDB;
    }

    public boolean isPassedEC() {
        return passedEC;
    }

    public void setPassedEC(boolean passedEC) {
        this.passedEC = passedEC;
    }

    public boolean isPassedEO() {
        return passedEO;
    }

    public void setPassedEO(boolean passedEO) {
        this.passedEO = passedEO;
    }

    public boolean isPassedPK() {
        return passedPK;
    }

    public void setPassedPK(boolean passedPK) {
        this.passedPK = passedPK;
    }

    public boolean isPassedUC() {
        return passedUC;
    }

    public void setPassedUC(boolean passedUC) {
        this.passedUC = passedUC;
    }

    public boolean isPassedWD() {
        return passedWD;
    }

    public void setPassedWD(boolean passedWD) {
        this.passedWD = passedWD;
    }
}