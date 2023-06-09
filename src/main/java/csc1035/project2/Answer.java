package csc1035.project2;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Answer {

    /**
     * This method uses a for each loop to iterate through each question and then asks the user to answer it.
     * If the answer matches the user input they score a point.
     * @returns Score
     */
    public void answerSAQ(){
        List<Question> correct = new ArrayList<>();
        List<Question> incorrect = new ArrayList<>();

        //Sets Scanner as sc
        Scanner sc = new Scanner(System.in);
        //Open hibernate session
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            session.beginTransaction();

            System.out.println("What quiz would you like to do?");
            //Query prints out the name and ID of quiz
            Query query = session.createQuery("SELECT q.ID, q.name FROM Quiz q");

            //Stores query in a list
            List<Object[]> quizzes = query.getResultList();
            System.out.println("Quizzes:");
            for (Object[] q : quizzes) {
                System.out.println("ID: " + q[0] + "\tName: " + q[1]);
            }

            //scanner for user to input a QuizID
            int quizID = sc.nextInt();
            sc.nextLine();
            Quiz targetQuiz = session.get(Quiz.class, quizID);

            //Count which is initially set to 0
            int count = 0;
            //For each loop to print out every question matching ID
            for (Question question : targetQuiz.getQuestions()) {
                System.out.println(question.getQuestion());
                String userAnswer = sc.nextLine();
                //Checks answer matches user input
                if (userAnswer.equalsIgnoreCase(question.getAnswer())) {
                    System.out.println();
                    count++;
                    correct.add(question);
                }else {
                    incorrect.add(question);
                }
            }
            //Prints out the user score
            System.out.println("Correct answers " + correct);
            System.out.println("Incorrect answers " + incorrect);
            System.out.println("You scored " + count);

            correct.clear();
            incorrect.clear();


            session.close();
        }catch (HibernateException e){
            if(session!=null) session.getTransaction().rollback();
            e.printStackTrace();
        }

    }


    /**
     * This method prints out a question and 4 possible answers, the user needs to type in which
     * answer they think is correct.
     */
    public void answerMCQ(){
        List<MCQ> correct = new ArrayList<>();
        List<MCQ> incorrect = new ArrayList<>();
        Scanner sc = new Scanner(System.in);
        Session session = HibernateUtil.getSessionFactory().openSession();

        try {
            session.beginTransaction();

            System.out.println("What quiz would you like to do?");
            //Query prints out the name and ID of quiz
            Query query = session.createQuery("SELECT q.ID, q.name FROM Quiz q");

            //Stores query in a list
            List<Object[]> quizzes = query.getResultList();
            System.out.println("Quizzes:");
            for (Object[] q : quizzes) {
                System.out.println("ID: " + q[0] + "\tName: " + q[1]);
            }

            int quizID = sc.nextInt();
            sc.nextLine();
            Quiz targetQuiz = session.get(Quiz.class, quizID);
            int count = 0;
            //For each loop to print out every question matching ID
            for (MCQ mcq : targetQuiz.getMCQ()) {
                System.out.println("Question: " + mcq.getQuestion() + '\n' + '\n' + " Please type in the answer you think it is.");
                System.out.println(mcq.answer3 + '\n' + mcq.answer1 + '\n' + mcq.answer2);
                String userAnswer = sc.nextLine();
                //Checks answer matches user input
                if (userAnswer.equalsIgnoreCase(mcq.getActualAnswer())) {
                    System.out.println();
                    count++;
                    correct.add(mcq);

                }else {
                    incorrect.add(mcq);
                }
            }
            //Prints out the user score
            System.out.println("Correct answers " + correct);
            System.out.println("Incorrect answers " + incorrect);
            System.out.println("You scored " + count);

            correct.clear();
            incorrect.clear();
            session.close();
        }catch (HibernateException e){
            if(session!=null) session.getTransaction().rollback();
            e.printStackTrace();
        }

    }
}
