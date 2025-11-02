package Project2.q2.program;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.function.Function;




public class Makeimages {
    private class Image {
        private int[][] data;

        public Image() {
            this.data = new int[16][16];
        }

        public int[][] getData() {
            return data;
        }

        public void setData(int[][] data) {
            this.data = data;
        }

        public void display() {
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[i].length; j++) {
                    System.out.print(data[i][j] + " ");
                }
                System.out.println();
            }
        }

        public void save(String path) {

        }

        public void load(String path) {

        }

        public void clear() {

        }

        public void makeZero() {

        }

        public void makeOne() {

        }
    }

    public static void main(String[] args) {
        ArrayList<Option> options = new ArrayList<>();

        Scanner scanner = new Scanner(System.in);
        String input = "";
        while (true) {


            input = scanner.nextLine();
            break;
        }
    }


}
