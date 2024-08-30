package imagenesPGM;

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

	private static int[][] leerPGMP2(String tipo, int filas, int columnas, String pathEntrada) throws IOException {
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

	private static int[][] calcularMedianaBackUp(int[][] matriz, int filas, int columnas, int k) {
		int[][] res = new int[filas][columnas];

		int count = 0, acum = 0;

		for (int fila = 0; fila < filas; fila++) {
			for (int col = 0; col < columnas; col++) {
				for (int fmed = fila - (k / 2) < 0 ? fila : fila - (k / 2); fmed <= (fila + (k / 2) >= filas ? fila
						: fila + (k / 2)); fmed++) {
					for (int cmed = col - (k / 2) < 0 ? col : col - (k / 2); cmed <= (col + (k / 2) >= columnas ? col
							: col + (k / 2)); cmed++) {
						acum += matriz[fmed][cmed];
						count++;
					}
				}
				res[fila][col] = acum / count;
				acum = 0;
				count = 0;
			}
		}

		return res;
	}

	private static int[][] calcularMediana(int[][] matriz, int filas, int columnas, int k) {
        int height = matriz.length;
        int width = matriz[0].length;
        int[][] result = new int[height][width];

        // Para cada píxel en la imagen
        for (int i = 1; i < height - 1; i++) {
            for (int j = 1; j < width - 1; j++) {
                // Extraer la vecindad 3x3
                int[] neighborhood = new int[9];
                int index = 0;
                for (int p = -1; p <= 1; p++) {
                    for (int l = -1; l <= 1; l++) {
                        neighborhood[index++] = matriz[i + p][j + l];
                    }
                }
                // Ordenar la vecindad y encontrar la mediana
                Arrays.sort(neighborhood);
                result[i][j] = neighborhood[4];  // La mediana es el elemento central (índice 4)
            }
        }
        
        return result;
	}

	private static void grabarPGMP2(String tipo, int filas, int columnas, String comentario, int grisMax,
			String pathSalida, int[][] matriz) throws IOException {

		try (PrintWriter writer = new PrintWriter(new FileWriter(pathSalida))) {
			// Escribir el encabezado del archivo PGM
			writer.println("P2"); // Identificador del formato P2
			if (!comentario.equals("")) {
				writer.println(comentario);
			}
			writer.println(columnas + " " + filas); // Dimensiones de la imagen
			writer.println(grisMax); // Valor máximo de gris

			// Escribir los datos de los píxeles
			for (int y = 0; y < filas; y++) {
				for (int x = 0; x < columnas; x++) {
					writer.print(matriz[y][x] + " ");
				}
				writer.println(); // Nueva línea después de cada fila de píxeles
			}
		}
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String pathEntrada = "D:\\Unlam\\ProgramacionAvanzada\\imagenesPGM\\src\\salt-pgmSalida.pgm";
		String pathSalida = "D:\\Unlam\\ProgramacionAvanzada\\imagenesPGM\\src\\salt-pgmSalida23.pgm";
		PGMtest(3, pathEntrada, pathSalida);
	}

}
