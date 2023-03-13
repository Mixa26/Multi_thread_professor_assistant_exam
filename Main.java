import threads.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.*;

public class Main {
    public static int checkNumOfStudentsInput(){
        //Checking the input for students
        boolean valid = false;
        int N = 0;

        while(!valid) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Please enter the number of students.");
            try {
                if (scanner.hasNext()) {
                    N = Integer.parseInt(scanner.nextLine());
                    if (N > 0) {
                        valid = true;
                    } else {
                        System.out.println("There must be at least 1 students!");
                    }
                }
            }
            catch (NumberFormatException e)
            {
                System.out.println("Input is not a number try again!");
            }
        }
        return N;
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        int numberOfStudents = 0;

        //Making sure numberOfStudents > 0
        numberOfStudents = checkNumOfStudentsInput();
        if (numberOfStudents <= 0){
            System.out.println("Something went wrong!");
            return;
        }

        //The professor can check only 2 students exams concurrently(not one)
        ExecutorService professor = Executors.newFixedThreadPool(2);
        //The assistant checks only 1 student exam concurrently
        ExecutorService assistant = Executors.newFixedThreadPool(1);
        //This pool is responsible for creating our students
        ExecutorService studentCreator = Executors.newCachedThreadPool();
        //This latch will make sure that all students start at the same time
        //and get their delay later when they enter the Student run function
        CountDownLatch latch = new CountDownLatch(numberOfStudents);
        //This is a barrier for the professor since he needs 2 students exactly
        CyclicBarrier cyclicBarrier = new CyclicBarrier(2);

        List<Future<Integer>> grades = new ArrayList<>();

        Future<Integer> grade;
        for (int i = 0; i < numberOfStudents; i++){
            int profOrAssistant = (new Random()).nextInt(2);
            //Creates a student and randomly assigns him to do the exam with the professor or assistant (50% for both)
            grade = studentCreator.submit(new Student(i, profOrAssistant == 0 ? professor : assistant, profOrAssistant, latch, cyclicBarrier));
            grades.add(grade);
        }
        Thread.sleep(5000);
        professor.shutdownNow();
        assistant.shutdownNow();
        studentCreator.shutdownNow();

        int averageGrade = 0;

        for (int i = 0; i < grades.size(); i++)
        {
            averageGrade += grades.get(i).get();
        }

        System.out.println("Average grade: " + (float)averageGrade/numberOfStudents);
    }
}
