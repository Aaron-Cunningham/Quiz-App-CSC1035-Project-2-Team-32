package csc1035.project2;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class QuestionStore {

    /**
     * Method for adding SAQs to database
     * - Input question, category, answer
     * - Assign it to an existing quiz ID
     * - If typed quiz ID does not exist then prompt error
     * - Otherwise successfully assign the SAQ to the Question table
     */
    public void addSAQ() {

        IO IO = new IO();
        Session session = HibernateUtil.getSessionFactory().openSession();

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();

            Scanner sc = new Scanner(System.in);

            // Takes the input from the user for question
            System.out.println("\nEnter the question: ");
            String question = sc.nextLine();
            question = question.toLowerCase(); // such that the question entered converts to lower case characters

            // Takes the input from the user for the questions category
            System.out.println("\nEnter the category: ");
            String category = sc.nextLine();

            // Takes the input from the user for the answer
            System.out.println("\nEnter the answer: ");
            String answer = sc.nextLine();
            answer = answer.toLowerCase(); // such that the answer entered converts to lower case characters


            Query query = session.createQuery("SELECT ID, name FROM Quiz");


            List<Object[]> quizzes = query.getResultList();
            System.out.println("Enter ID of Quiz you wish to add question to: ");
            for (Object[] q : quizzes) {
                System.out.println("ID: " + q[0] + "\tName: " + q[1]);
            }

            // Asking the user to choose from the following quizID's
            int quizID = sc.nextInt();

            // Check if quizID exists in the database (table Quiz)
            TypedQuery<Quiz> query1 = session.createQuery("FROM Quiz WHERE ID = :ID", Quiz.class);
            query1.setParameter("ID", quizID);
            List<Quiz> quizList = query1.getResultList();

            // If statement such that if the quiz list is empty, then prompt an error that the quiz ID doesn't exist
            if (quizList.isEmpty()) {
                System.out.println("\nError: Quiz with quizID " + quizID + " does not exist.");
                session.close(); // close the session
                IO.IOSystem(); // print the IO system
            } else {
                // Set a new question with the elements (question, category, answer, quizID used in Question class
                Question Q = new Question(question, category, answer, quizID);

                // Setting the question using the inputted elements
                Q.setQuestion(question);
                Q.setCategory(category);
                Q.setAnswer(answer);
                Q.setQuiz_id(quizID);

                // Save and commit message
                session.save(Q);
                session.getTransaction().commit();
                System.out.println("\nQuestion added successfully to quizID: " + quizID);
            }

            session.close();

        } catch (HibernateException e) {
            // if error roll back
            if (session != null) session.getTransaction().rollback(); // if the session is null then roll back
            e.printStackTrace(); // handles the exception and errors

        } finally {
            //Close session
            assert session != null; // verifies variable session is not null
            session.close(); // close session
        }
    }

    /**
     * Method for adding an MCQ to MCQ table
     * - Takes the user input's for the following elements:
     * - question, category, possible answer1, possible answer2, possible answer 3, actual answer
     * - These are then assigned to the selected quiz ID entered by the user
     */
    public void addMCQ() {

        IO io = new IO();
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();

            Scanner sc = new Scanner(System.in);

            System.out.println("\nEnter the question: ");
            String question = sc.nextLine();
            question = question.toLowerCase(); // such that the question entered converts to lower case characters

            System.out.println("\nEnter the category: ");
            String category = sc.nextLine();

            System.out.println("Enter a wrong answer");
            String wrongAnswer1 = sc.nextLine();

            System.out.println("Enter the next wrong answer");
            String wrongAnswer2 = sc.nextLine();

            System.out.println("Enter the final wrong answer");
            String wrongAnswer3 = sc.nextLine();

            System.out.println("\nEnter the actual answer: ");
            String rightAnswer = sc.nextLine();


            rightAnswer = rightAnswer.toLowerCase(); // such that the answer entered converts to lower case characters

            System.out.println("\nEnter which of the following quizID's you would like to link this question with: ");

            // Get a list of existing quizIDs from the database
            TypedQuery<Object[]> query = session.createQuery("SELECT q.ID, q.name FROM Quiz q", Object[].class);
            List<Object[]> quizIDs = query.getResultList();


            // Print a list of existing quizIDs
            System.out.println("\nExisting quiz IDs:");
            System.out.println("\nExisting questions:");
            for (Object[] quiz : quizIDs) {
                System.out.println("ID: " + quiz[0] + ", name: " + quiz[1]);
            }


            // Asking the user to choose from the following quizID's
            int quizID = sc.nextInt();

            // Check if quizID exists in the database
            TypedQuery<Quiz> query1 = session.createQuery("FROM Quiz WHERE ID = :quiz_ID", Quiz.class);
            query1.setParameter("quiz_ID", quizID);
            List<Quiz> quizList = query1.getResultList();

            // If the quiz List is empty, then there should be a prompted error... that the quizID does not exist
            if (quizList.isEmpty()) {
                System.out.println("\nError: Quiz with quizID " + quizID + " does not exist.");
                session.close();
                io.IOSystem();

            } else {
                MCQ Q = new MCQ(question, category, wrongAnswer1, wrongAnswer2, wrongAnswer3, rightAnswer, quizID);

                // Using the setters from the MCQ class to set the MCQs into the database
                Q.setQuestion(question);
                Q.setCategory(category);
                Q.setAnswer1(wrongAnswer1);
                Q.setAnswer2(wrongAnswer2);
                Q.setAnswer3(wrongAnswer3);
                Q.setActualAnswer(rightAnswer);
                Q.setQuiz_id(quizID);

                session.save(Q);
                session.getTransaction().commit();
                System.out.println("\nQuestion added successfully to quizID: " + quizID);
            }
            session.close();

        } catch (HibernateException e) {
            //if error roll back
            if (session != null) session.getTransaction().rollback(); // if the session is null then roll back
            e.printStackTrace();

        } finally {
            //Close session
            assert session != null; // verifies variable session is not null
            session.close();
        }
    }

    public void deleteSAQ() {
        IO io = new IO();
        Session session = HibernateUtil.getSessionFactory().openSession();

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();

            Scanner sc = new Scanner(System.in);

            System.out.println("Enter the question ID that you would like to delete (choose from the options provided below): ");

            // Get a list of existing questions from the database
            TypedQuery<Object[]> query = session.createQuery("SELECT q.ID, q.question FROM Question q", Object[].class);
            List<Object[]> questions = query.getResultList();

            // Print a list of existing question ID and question text
            System.out.println("\nExisting questions:");
            for (Object[] question : questions) {
                System.out.println("ID: " + question[0] + ", question: " + question[1]);
            }

            int question_ID = sc.nextInt();

            // Check if the question ID is in the list of questions
            boolean found = false;
            for (Object[] question : questions) {
                if ((int) question[0] == question_ID) {
                    found = true;
                    break;
                }
            }

            // If the question ID is not found, prompt the user with an error message
            if (!found) {
                System.out.println("Error: There is no question with ID " + question_ID);
                session.close();
                io.IOSystem();
            }

            // Otherwise, delete the question with the given ID
            Question Qs = session.get(Question.class, question_ID);
            session.delete(Qs);

            // Commit the transaction and close the session
            session.getTransaction().commit();
            session.close();

        } catch (HibernateException e) {
            //if error roll back
            if (session != null) session.getTransaction().rollback(); // if the session is null then roll back
            e.printStackTrace();

        } finally {
            //Close session
            assert session != null; // verifies variable session is not null
            System.out.println("The SAQ has been successfully deleted... ");
            session.close();

        }
    }

    public void deleteMCQ() {
        IO io = new IO();
        Session session = HibernateUtil.getSessionFactory().openSession();

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();

            Scanner sc = new Scanner(System.in);

            System.out.println("Enter the question ID that you would like to delete (choose from the options provided below): ");

            // Get a list of existing MCQ IDs from the database (put in an object to read 2 entities from the database)
            TypedQuery<Object[]> query = session.createQuery("SELECT ID,question FROM MCQ ", Object[].class);
            List<Object[]> MCQs = query.getResultList();

            // Print a list of existing question ID and question (for question table)
            System.out.println("\nExisting MCQ ID and questions:");
            for (Object[] mcq : MCQs) {
                System.out.println(Arrays.toString(mcq));
            }

            System.out.println("Enter an appropriate MCQ ID that you would like to delete: ");
            int mcq_ID = sc.nextInt();

            // Check if the MCQ ID is in the list of questions
            boolean found = false;
            for (Object[] mcq : MCQs) {
                if ((int) mcq[0] == mcq_ID) {
                    found = true;
                    break;
                }
            }

            // If the MCQ ID is not found, prompt the user with an error message
            if (!found) {
                System.out.println("Error: There is no MCQ  with ID " + mcq_ID);
                session.close();
                io.IOSystem();
            }
            // Otherwise, delete the MCQ with the given ID
            MCQ mCq = session.get(MCQ.class, mcq_ID);
            session.delete(mCq);

            // Commit the transaction and close the session
            session.getTransaction().commit();
            session.close();

        } catch (HibernateException e) {
            //if error roll back
            if (session != null) session.getTransaction().rollback(); // if the session is null then roll back
            e.printStackTrace();

        } finally {
            //Close session
            assert session != null; // verifies variable session is not null
            System.out.println("The MCQ has been successfully deleted... ");
            session.close();
        }
    }

    public void updateSAQ() {
        IO io = new IO();
        Session session = HibernateUtil.getSessionFactory().openSession();

        try {
            session.beginTransaction();

            Scanner sc = new Scanner(System.in);
            System.out.println("Enter the question ID you would like to update from the following options:");

            // Get a list of existing question IDs from the database
            TypedQuery<Integer> query = session.createQuery("SELECT ID FROM Question", Integer.class);
            List<Integer> questionIds = query.getResultList();

            // Print a list of existing question IDs
            System.out.println("\nExisting question IDs:");
            System.out.println(questionIds);

            System.out.println("Please enter a valid question ID you would like to edit: ");
            int qID = sc.nextInt();

            // Check if the question ID is in the list of questions
            if (!questionIds.contains(qID)) {
                System.out.println("Error: There is no question with ID " + qID);
                return;
            }

            Question question = session.get(Question.class, qID);

            // Printing the inputted row
            System.out.println("This is the row of the question ID you wanted to update: ");
            System.out.printf("[%d, %s, %s, %s, %d]%n", question.getID(), question.getQuestion(), question.getCategory(), question.getAnswer(), question.getQuiz_id());

            int option;
            do {
                System.out.println("Choose an option on which element you would like to update from the above row displayed: \n"
                        + "1: Update Question\n"
                        + "2: Update Category\n"
                        + "3: Update Answer\n"
                        + "4: Update Quiz ID\n"
                        + "0: Exit update menu");

                while (!sc.hasNextInt()) {
                    System.out.println("Please enter a valid option (1-5 or 0 to exit)");
                    sc.next();
                }
                option = sc.nextInt();

                switch (option) {
                    case 1:
                        System.out.println("Please enter a new question:");
                        sc.nextLine(); // consume the newline character left by nextInt
                        String newQuestion = sc.nextLine();
                        question.setQuestion(newQuestion);
                        break;

                    case 2:
                        System.out.println("Please enter a new category:");
                        sc.nextLine(); // consume the newline character left by nextInt
                        String newCategory = sc.nextLine();
                        question.setCategory(newCategory);
                        break;

                    case 3:
                        System.out.println("Please enter a new answer:");
                        sc.nextLine(); // consume the newline character left by nextInt
                        String newAnswer = sc.nextLine();
                        question.setAnswer(newAnswer);
                        break;

                    case 4:
                        System.out.println("Please enter a new quiz ID:");
                        int newQuizID = sc.nextInt();
                        question.setQuiz_id(newQuizID);
                        break;

                    case 0:
                        break;

                    default:
                        System.out.println("Please enter a valid option (1-5 or 0 to exit)");
                        break;
                }

                session.update(question);
                session.getTransaction().commit();

                io.IOSystem();

            } while (option != 0);

        } catch (HibernateException e) {
            //if error roll back
            session.getTransaction().rollback(); // if the session is null then roll back
            e.printStackTrace();

        } finally {
            //Close session
            session.close();
        }
    }

    public void updateMCQ() {
        // Ask the user which MCQ_ID with the MCQ also displayed they would like to update
        // After that display the row of that particular MCQ_ID
        // Ask what they would like to edit either the ans1, ans2, ans3, MCQ, category, actual answer
        // After update, ask the user if they want to updater anything else, otherwise return to the main menu
        // Display print method saying the question has been successfully updated...
    }

}