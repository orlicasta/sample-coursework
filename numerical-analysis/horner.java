public class horner {
    public static void main(String[] args) {

        //Check for valid input
        if(args.length % 2 == 0 || args.length < 2) {
            System.out.printf("Invalid input detected. Please check input arguments and try again.\n");
            System.exit(0);
        }

        //determine degree
        int degree = Integer.parseInt(args[1]);

        //create arrays
        float[] coef = new float[degree + 1];
        float[] expo = new float[degree + 1];

        //Populate arrays
        float curr = degree;
        int term = 0;
        for(int i = 0; i < args.length - 1; i++) {
            if (curr == Float.parseFloat(args[i + 1])) {
                coef[term] = Float.parseFloat(args[i]);
                expo[term] = curr;
            }
            else {
                coef[term] = 0;
                expo[term] = 0;
                i--;
                i--;
            }
            term++;
            curr--;
            i++;
        }

        //Check grouping and order of terms
        for(int k = 0; k < expo.length; k++) {
            float max = expo[k];
            if (max != 0) {
                for(int p = k + 1; p < expo.length; p++) {
                    //System.out.printf("max: %f\texpo[p]: %f\n", max, expo[p]);
                    if(expo[p] != 0 && expo[p] >= max) {
                        System.out.printf("Invalid input detected. Please check that your polynomial terms are grouped and in order from highest to lowest.\n");
                    }
                } 
            }
        }

        float x = Float.parseFloat(args[args.length - 1]);
        float x0;

        //Display input
        for(int j = 0; j < coef.length; j++) {
            System.out.printf("%7.4f * x^%.0f\n", coef[j], expo[j]);
        }
        System.out.printf("x0 = %2.0f\n\n", x);

        //determine P(x0) and coefficients for Q(x0)
        //Q(x0) will have one less term than P(x0)
        float[] qcoef = new float[degree]; 

        //loop this for each iteration up to 10
        int iteration = 0;
        while(iteration < 10) {
            System.out.printf(".......................[iteration %2d]........................\n", iteration + 1);

            //first column result is simply the first coefficient 
            float presult = coef[0];
            System.out.printf("P(x%d) result: %8.4f ", iteration, presult);
            qcoef[0] = presult;

            //calculate the remaining column results
            for(int i = 1; i < degree + 1; i++) {
                presult = (x * presult) + coef[i];
                //don't put the last result in q(x0)
                if (i < degree) {
                    qcoef[i] = presult;
                }
                System.out.printf("%8.4f ", presult);
            }
            System.out.printf("\nP(%3.2f) = %8.4f\n", x, presult);

            //determine q(x0) using coefficients array populated in previous for loop
            float qresult = qcoef[0];
            System.out.printf("\nQ(x%d) result: %8.4f ", iteration, qresult);
            
            for(int i = 1; i < degree + 1 - 1; i++) {
                qresult = (x * qresult) + qcoef[i];
                System.out.printf("%8.4f ", qresult);
            }
            System.out.printf("\nQ(%3.2f) = %8.4f\n", x, qresult);

            //calculate iteration x value
            x0 = x;
            x = x - (presult / qresult);
            float relerror = (x0 - x) / x;

            //display iteration x and relative error
            System.out.printf("\nx%d = %8.4f\nrelative error = %8.4f\n\n", ++iteration, x, relerror);

            //exit the while loop if relative error is less than 10^-4
            if(relerror < Math.pow(10, -4)) {
                break;
            }

        }
        
    }
}