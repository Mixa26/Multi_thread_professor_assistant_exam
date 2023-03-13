package threads;

import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Student implements Callable<Integer> {

    private int studentId;
    private ExecutorService examController;
    private int profOrAssistant;
    private CountDownLatch latch;
    private CyclicBarrier cyclicBarrier;

    public Student(int studentId, ExecutorService examController, int profOrAssistant, CountDownLatch latch, CyclicBarrier cyclicBarrier) {
        this.studentId = studentId;
        this.examController = examController;
        this.profOrAssistant = profOrAssistant;
        this.latch = latch;
        this.cyclicBarrier = cyclicBarrier;
    }

    @Override
    public Integer call(){
        latch.countDown();
        try {
            latch.await();
            int timeToArrive = (new Random()).nextInt(1001);
            Thread.sleep(timeToArrive);

            if (profOrAssistant == 0) {
                //professor
                cyclicBarrier.await();
                Future<Integer> gradeRes = examController.submit(() -> {
                    long startOfExamTime = System.currentTimeMillis();
                    int grade = 5;

                    try {
                        Thread.sleep((new Random()).nextInt(500,1001));
                        grade = (new Random()).nextInt(5, 11);
                    } catch (InterruptedException e) {
                        System.out.println("Suspended Thread: " + studentId + " Arrival: " + timeToArrive + " Prof: professor TTC: " + (System.currentTimeMillis()-startOfExamTime) + ":" + startOfExamTime + " in miliseconds Score: " + grade);
                        return 5;
                    }
                    System.out.println("Thread: " + studentId + " Arrival: " + timeToArrive + " Prof: professor TTC: " + (System.currentTimeMillis()-startOfExamTime) + ":" + startOfExamTime + " in miliseconds Score: " + grade);
                    return grade;
                });
                return gradeRes.get();
            }
            else {
                //assistant
                Future<Integer> gradeRes = examController.submit(() -> {
                    long startOfExamTime = System.currentTimeMillis();
                    int grade = 5;
                    try {
                        Thread.sleep((new Random()).nextInt(500,1001));
                        grade = (new Random()).nextInt(5, 11);
                    } catch (InterruptedException e) {
                        System.out.println("Suspended Thread: " + studentId + " Arrival: " + timeToArrive + " Prof: assistant TTC: " + (System.currentTimeMillis()-startOfExamTime) + ":" + startOfExamTime + " in miliseconds Score: " + grade);
                        return 5;
                    }
                    System.out.println("Thread: " + studentId + " Arrival: " + timeToArrive + " Prof: assistant TTC: " + (System.currentTimeMillis()-startOfExamTime) + ":" + startOfExamTime + " in miliseconds Score: " + grade);
                    return grade;
                });
                return gradeRes.get();
            }

        } catch (InterruptedException | BrokenBarrierException | RuntimeException | ExecutionException e) {
            System.out.println("Suspended Thread: " + studentId + " Arrival: student was waiting for other student's Prof: professor TTC: unknown:unknown in miliseconds Score: 5");
        }
        //System.out.println(grade);
        return 5;
    }
}
