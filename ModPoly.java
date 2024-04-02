//jfree
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
//file list jframe
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.JFrame;

// Classe ModPoly pour modéliser un polynôme et effectuer des opérations telles que l'ajustement par moindres carrés
public class ModPoly {
    private double[] coefficients; // Stocke les coefficients du polynôme

    // Constructeur
    public ModPoly(int degree) {
        this.coefficients = new double[degree + 1]; // Inclut le terme constant
    }

    // Méthode pour déterminer les coefficients du polynôme par la méthode des moindres carrés
    public void identifie(double[] x, double[] y) {
        int n = coefficients.length;
        double[][] A = new double[n][n];
        double[] B = new double[n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < x.length; k++) {
                    A[i][j] += Math.pow(x[k], i + j);
                }
            }
        }

        for (int i = 0; i < n; i++) {
            for (int k = 0; k < x.length; k++) {
                B[i] += Math.pow(x[k], i) * y[k];
            }
        }

        // Résolution du système linéaire A * coefficients = B
        this.coefficients = resoudreSystemeLineaire(A, B);
    }

    // Méthode pour évaluer le polynôme à une valeur x donnée
    public double evaluer(double x) {
        double resultat = 0.0;
        for (int i = 0; i < this.coefficients.length; i++) {
            resultat += this.coefficients[i] * Math.pow(x, i);
        }
        return resultat;
    }
    // Méthode pour créer et afficher un graphique des données et du polynôme ajusté
    public void createAndShowPlot(double[] x, double[] y) {
        // Création des séries de données
        XYSeries series1 = new XYSeries("Points Originaux");
        for (int i = 0; i < x.length; i++) {
            series1.add(x[i], y[i]);
        }

        XYSeries series2 = new XYSeries("Courbe du Polynôme");
        for (double xi = x[0]; xi <= x[x.length - 1]; xi += 0.01) {
            series2.add(xi, evaluer(xi));
        }

        // Ajout des séries à la collection
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series1);
        dataset.addSeries(series2);

        // Création du graphique
        JFreeChart chart = ChartFactory.createXYLineChart(
                "ModPoly Approximation",
                "x",
                "y",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);

        // Affichage dans un JFrame
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
        JFrame frame = new JFrame("ModPoly Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(chartPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }





    // Méthode stub pour résoudre le système linéaire (à remplacer par une implémentation réelle)
    private double[] resoudreSystemeLineaire(double[][] A, double[] B) {
        int n = B.length;
        double[] X = new double[n]; // Le vecteur de solution que nous allons calculer

        // Effectuer la décomposition LU sur A
        // Pour simplifier, ce pseudo-code ne gère pas les cas où la matrice A nécessite un pivotage
        for (int k = 0; k < n; k++) {
            for (int i = k + 1; i < n; i++) {
                A[i][k] /= A[k][k];
                for (int j = k + 1; j < n; j++) {
                    A[i][j] -= A[i][k] * A[k][j];
                }
            }
        }

        // Résoudre L*Y = B (où Y est temporairement stocké dans X)
        for (int i = 0; i < n; i++) {
            X[i] = B[i];
            for (int k = 0; k < i; k++) {
                X[i] -= A[i][k] * X[k];
            }
            X[i] /= A[i][i];
        }

        // Résoudre U*X = Y
        for (int i = n - 1; i >= 0; i--) {
            for (int k = i + 1; k < n; k++) {
                X[i] -= A[i][k] * X[k];
            }
            X[i] /= A[i][i];
        }

        return X;
    }



    // Méthode principale pour tester
    public static void main(String[] args) {
        // Exemple d'utilisation de la classe ModPoly
        // Lecture des points à partir d'un fichier
        String nomFichier = "fichier/donnee.txt"; // Remplacez par le chemin vers votre fichier de données
        List<Double> xList = new ArrayList<>();
        List<Double> yList = new ArrayList<>();
        System.out.println("Début de la lecture du fichier...");

        try (Scanner scanner = new Scanner(new File(nomFichier))) {
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().trim().split("\\s+");
                if (parts.length == 2) {
                    double x = Double.parseDouble(parts[0]);
                    double y = Double.parseDouble(parts[1]);
                    xList.add(x);
                    yList.add(y);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Fichier non trouvé: " + nomFichier);
            return;
        }


        System.out.println("Fin de la lecture du fichier. Points lus : " + xList.size());

        // Conversion des listes en tableaux
        double[] x = xList.stream().mapToDouble(Double::doubleValue).toArray();
        double[] y = yList.stream().mapToDouble(Double::doubleValue).toArray();

        // Création de l'objet ModPoly et calcul des coefficients
        ModPoly modPoly = new ModPoly(x.length - 1); // Le degré est basé sur la taille de l'échantillon - ajustez selon votre besoin
        modPoly.identifie(x, y);

        // Affichage des résultats
        System.out.println("Coefficients du polynôme :");
        for (double coeff : modPoly.coefficients) {
            System.out.println(coeff);
        }

        modPoly.createAndShowPlot(x, y);



                System.out.println("\nÉvaluation du polynôme aux points originaux :");
        for (double xi : x) {
            double yi = modPoly.evaluer(xi);
            System.out.printf("x = %.2f, p(x) = %.2f\n", xi, yi);
        }

        System.out.println("\nComparaison entre les valeurs originales et les valeurs calculées par le polynôme :");
        for (int i = 0; i < x.length; i++) {
            double yi = modPoly.evaluer(x[i]);
            System.out.printf("x = %.2f, y original = %.2f, p(x) = %.2f, Différence = %.2f\n", x[i], y[i], yi, Math.abs(y[i] - yi));
        }

    }



}
