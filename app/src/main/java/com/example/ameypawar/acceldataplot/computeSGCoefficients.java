package com.example.ameypawar.acceldataplot;

import org.apache.commons.math.linear.RealMatrixImpl;

import static java.lang.Math.pow;


public class computeSGCoefficients {
    public static double[] computeSGCoefficients(int nl, int nr, int degree) {
        if (nl < 0 || nr < 0 || nl + nr < degree)
            throw new IllegalArgumentException("Bad arguments");
        RealMatrixImpl matrix = new RealMatrixImpl(degree + 1, degree + 1);
        double[][] a = matrix.getDataRef();
        double sum;
        for (int i = 0; i <= degree; i++) {
            for (int j = 0; j <= degree; j++) {
                sum = (i == 0 && j == 0) ? 1 : 0;
                for (int k = 1; k <= nr; k++)
                    sum += pow(k, i + j);
                for (int k = 1; k <= nl; k++)
                    sum += pow(-k, i + j);
                a[i][j] = sum;
            }
        }
        double[] b = new double[degree + 1];
        b[0] = 1;
        b = matrix.solve(b);
        double[] coeffs = new double[nl + nr + 1];
        for (int n = -nl; n <= nr; n++) {
            sum = b[0];
            for (int m = 1; m <= degree; m++)
                sum += b[m] * pow(n, m);
            coeffs[n + nl] = sum;
        }
        return coeffs;
    }
}
