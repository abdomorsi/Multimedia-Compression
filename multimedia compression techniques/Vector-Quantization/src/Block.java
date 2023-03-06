public class Block {
    double[][] array;
    int size = 0, rows = 0 , columns=0;

    Block(int[][] array){

    }
    Block(int r,int c) {
        array = new double[r][c];
        this.rows=r;
        this.columns=c;
        size = r*c;
    }

    /*public void fillBlock(double[][] arr, int indexI, int indexJ) {
        for (int i = indexI , bi = -1 ; i < indexI+rows; i++) {
            bi++;
            for (int j = indexJ , bj=0; j < indexJ+columns; j++ , bj++) {
                //System.out.println(arr[i][j]);
                this.array[bi][bj] = arr[i][j];
            }
        }
    }*/

    public void fillBlock(double[][] arr) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                this.array[i][j] = arr[i][j];
            }
        }
    }

    public void printBlock() {
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.columns; j++) {
                System.out.print(this.array[i][j]+" ");
            }
            System.out.println();
        }
    }



}
