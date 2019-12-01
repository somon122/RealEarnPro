package com.real_cash.earning_app;

public class Questions {


    public String mQuestions [] = {

            "What is 10 * 10 =?",
            "What is 100 /10 =?",
            "What is 50-50 =?",
            "What is 0*0 =?",
            "What is 5+5 =?",
            "What is 10+5 =?",
            "What is 100+5 =?",
            "What is 10+50 =?",
            "What is 100+50 =?",
            "What is 100 * 100 =?",
            "What is 15+10+5 =?",
            "What is 50-{(10+5)*2} =?",
            "What is 10+5+0+10 =?",
            "What is 100*10% =?",
            "What is 999+1 =?"

    };

private String mChoices [][]={

        {"Ans 200","Ans 300","Ans 100","Ans 10"},
        {"Ans 10","Ans 100","Ans 20","Ans 50"},
        {"Ans 50","Ans 0","Ans 100","Ans nothing"},
        {"Ans 0","Ans 100","Ans 10","Ans 00"},
        {"Ans 12","Ans 15","Ans 20","Ans 10"},
        {"Ans 12","Ans 15","Ans 20","Ans 10"},
        {"Ans 105","Ans 1005","Ans 120","Ans 110"},
        {"Ans 60","Ans 50","Ans 40","Ans 0"},
        {"Ans 100","Ans 1500","Ans 200","Ans 150"},
        {"Ans 100000","Ans 1000","Ans 10000","Ans 100"},
        {"Ans 30","Ans 20","Ans 35","Ans 25"},
        {"Ans 50","Ans 20","Ans 100","Ans 200"},
        {"Ans 30","Ans 15","Ans 25","Ans 10"},
        {"Ans 12","Ans 15","Ans 20","Ans 10"},
        {"Ans 1200","Ans 1500","Ans 200","Ans 1000"}
};

private String mCarrectAnswer []={

       "Ans 100","Ans 10","Ans 0","Ans 0","Ans 10","Ans 15","Ans 105","Ans 60","Ans 150","Ans 10000","Ans 30","Ans 20","Ans 25","Ans 10","Ans 1000"};

public String getQuestion (int a)
{
    String question = mQuestions[a] ;
    return question;
}

public String getChoices1 (int a){
    String choice = mChoices[a][0];
    return choice;
}
public String getChoices2 (int a){
    String choice = mChoices[a][1];
    return choice;
}
public String getChoices3 (int a){
    String choice = mChoices[a][2];
    return choice;
}
public String getChoices4 (int a){
    String choice = mChoices[a][3];
    return choice;
}

public String getCarrectAnswer (int a){

    String answer = mCarrectAnswer[a];
    return answer;
}



}
