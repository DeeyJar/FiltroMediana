package ar.edu.unlam.alumno.mediana;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;

public class imagenesPGM {

	public static void PGMtest(int k, String pathEntrada, String pathSalida) throws IOException {

		if (k % 2 == 0) {
			System.out.println("Error, el k debe ser impar");
			return;
		}

		int columnas = 0, filas = 0, grisMax = 0;
		String comentario = "", tipo = "";

		try (FileInputStream fin = new FileInputStream(pathEntrada)) {
			// read first line of ImageHeader
			String temp = "";
			int c = fin.read();
			temp += (char) c;
			c = fin.read();
			temp += (char) c;
			tipo = temp;

			// read second line of ImageHeader
			c = fin.read(); // read Lf (linefeed)
			c = fin.read(); // read '#'
			temp = "";
			boolean escomentario = false;
			while ((char) c == '#') // read comment
			{
				escomentario = true;
				temp += (char) c;
				while (c != 10 && c != 13) {
					c = fin.read();
					temp += (char) c;
				}
				c = fin.read(); // read next '#'
			}
			if (temp.equals("") == false) {
				comentario = temp.substring(0, temp.length() - 1);
				fin.skip(-1);
			}

			// read third line of ImageHeader
			// read columns
			temp = "";
			if (escomentario == true)
				c = fin.read();
			temp += (char) c;
			while (c != 32 && c != 10 && c != 13) {
				c = fin.read();
				temp += (char) c;
			}
			temp = temp.substring(0, temp.length() - 1);
			columnas = Integer.parseInt(temp);

			// read rows
			c = fin.read();
			temp = "";
			temp += (char) c;
			while (c != 32 && c != 10 && c != 13) {
				c = fin.read();
				temp += (char) c;
			}
			temp = temp.substring(0, temp.length() - 1);
			filas = Integer.parseInt(temp);

			// read maxgray
			c = fin.read();
			temp = "";
			temp += (char) c;
			while (c != 32 && c != 10 && c != 13) {
				c = fin.read();
				temp += (char) c;
			}
			temp = temp.substring(0, temp.length() - 1);
			grisMax = Integer.parseInt(temp);

			int[][] Pixels = new int[filas][columnas];

			Pixels = tipo.equals("P5") ? leerPGMP5(tipo, filas, columnas, pathEntrada, fin)
					: leerPGMP2(tipo, filas, columnas, pathEntrada);

			Pixels = calcularMediana(Pixels, filas, columnas, k);

			if (tipo.equals("P5") == true) {
				grabarPGMP5(tipo, filas, columnas, comentario, grisMax, pathSalida, Pixels);
			} else {
				grabarPGMP2(tipo, filas, columnas, comentario, grisMax, pathSalida, Pixels);
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Terminado");
	}

	private static int[][] leerPGMP5(String tipo, int filas, int columnas, String pathEntrada, FileInputStream fin) {
		int[][] res = new int[filas][columnas];
		int c = 0;
		try { // read pixels from ImageData
			for (int tr = 0; tr < filas; tr++) {
				for (int tc = 0; tc < columnas; tc++) {
					c = (int) fin.read();
					res[tr][tc] = c;
				}
			}

			fin.close();
		} catch (Exception err) {
			System.out.println("Error: " + err);
			System.exit(-1);
		}

		return res;
	}

	static int[][] leerPGMP2(String tipo, int filas, int columnas, String pathEntrada) throws IOException {
		try {
			FileInputStream fis = new FileInputStream(pathEntrada);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

			String magicNumber = reader.readLine();

			// Leer comentarios
			String line;
			while ((line = reader.readLine()).startsWith("#")) {
				// Es un comentario, ignorar
			}

			// Leer dimensiones y valor máximo
			String[] dimensions = line.split(" ");
			int width = Integer.parseInt(dimensions[0]);
			int height = Integer.parseInt(dimensions[1]);
			int maxGrayValue = Integer.parseInt(reader.readLine());

			int cantElementos = width * height, count = 0;

			int[][] matriz2D = new int[height][width];

			while (count < cantElementos || line != null) {
				line = reader.readLine();
				if (line != null) {
					String[] linea = line.split(" ");
					for (int i = 0; i < linea.length; i++) {
						matriz2D[count / width][count % width] = Integer.parseInt(linea[i]);
						count++;
					}
				}
			}

			reader.close();
			return matriz2D;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static void grabarPGMP5(String tipo, int filas, int columnas, String comentario, int maxValor,
			String pathSalida, int[][] matriz) {
		try {
			FileOutputStream fout = new FileOutputStream(pathSalida);

			// write image header
			// write PGM magic value 'P5'
			String tstr = tipo + "\n";
			fout.write(tstr.getBytes());

			// write comment
			comentario = comentario + "\n";
			// fout.write(comment.getBytes());

			// write columns
			tstr = Integer.toString(columnas);
			fout.write(tstr.getBytes());
			fout.write(32); // write blank space

			// write rows
			tstr = Integer.toString(filas);
			fout.write(tstr.getBytes());
			fout.write(32); // write blank space

			// write maxgray
			tstr = Integer.toString(maxValor);
			tstr = tstr + "\n";
			fout.write(tstr.getBytes());
			for (int r = 0; r < filas; r++) {
				for (int j = 0; j < columnas; j++) {
					fout.write(matriz[r][j]);
				}
			}

			fout.close();
		} catch (Exception err) {
			System.out.println("Error: " + err);
			System.exit(-1);
		}
	}

	static int[][] calcularMediana(int[][] matriz, int filas, int columnas, int k) {
        int height = matriz.length;
        int width = matriz[0].length;
        int[][] result = new int[height][width];
        int offset = k / 2;
        // Para cada píxel en la imagen
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                int[] neighborhood = new int[k*k];
                int index = 0;
                for (int p = -offset; p <= offset; p++) {
                    for (int l = -offset; l <= offset; l++) {
                    	int neighborRow = Math.min(Math.max(i + p, 0), height - 1);
                        int neighborCol = Math.min(Math.max(j + l, 0), width - 1);
                        neighborhood[index++] = matriz[neighborRow][neighborCol];
                    }
                }
                // Ordenar la vecindad y encontrar la mediana
                Arrays.sort(neighborhood);
                result[i][j] = neighborhood[neighborhood.length / 2];  // La mediana es el elemento central (índice 4)
            }
        }
        
        return result;
	}

	private static void grabarPGMP2(String tipo, int filas, int columnas, String comentario, int grisMax,
			String pathSalida, int[][] matriz) throws IOException {

		try (PrintWriter writer = new PrintWriter(new FileWriter(pathSalida))) {
			// Escribir el encabezado del archivo PGM
			writer.print("P2\n"); // Identificador del formato P2
			if (!comentario.equals("")) {
				writer.print(comentario + "\n");
			}
			writer.print(columnas + " " + filas + "\n"); // Dimensiones de la imagen
			writer.print(grisMax + "\n"); // Valor máximo de gris

			// Escribir los datos de los píxeles
			for (int y = 0; y < filas; y++) {
				for (int x = 0; x < columnas; x++) {
					writer.print(matriz[y][x] + " ");
				}
				writer.print("\n"); // Nueva línea después de cada fila de píxeles
			}
		}
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
//		String pathEntrada = "D:\\Unlam\\ProgramacionAvanzada\\ojoSerpiente.pgm";
//		String pathSalida = "D:\\Unlam\\ProgramacionAvanzada\\ojoSerpienteFiltroMediana.pgm";
//		
		String pathEntrada = "D:\\Proyectos\\unlam\\FiltroMediana\\assets\\globosFiltro2.pgm";
		String pathSalida = "D:\\Proyectos\\unlam\\FiltroMediana\\assets\\globosFiltro3.pgm";
		
//		String pathEntrada = "D:\\Unlam\\ProgramacionAvanzada\\imagenesPGM\\src\\cat.pgm";
//		String pathSalida = "D:\\Unlam\\ProgramacionAvanzada\\imagenesPGM\\src\\catFiltroMediana.pgm";
		PGMtest(5, pathEntrada, pathSalida);
	}

}
