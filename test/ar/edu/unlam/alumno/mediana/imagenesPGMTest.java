package ar.edu.unlam.alumno.mediana;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

class imagenesPGMTest {

	@Test
	public void testCalcularMedianaEjemploWikipedia() {
		int[][] matrizOriginal = { { 1, 2, 3 }, 
								   { 4, 5, 6 }, 
								   { 7, 8, 9 } };

		int[][] matrizEsperada = { { 2, 3, 3 }, 
								   { 4, 5, 6 }, 
								   { 7, 7, 8 } };

		int k = 3;

		int[][] resultado = imagenesPGM.calcularMediana(matrizOriginal, 3, 3, k);

		assertArrayEquals(resultado, matrizEsperada);
	}

	@Test
	public void lecturaP2() throws IOException {
		
		int[][] matrizEsperada = { 
				{ 10, 20, 30, 40, 50 }, 
				{ 60, 70, 80, 90, 100 }, 
				{ 110, 120, 130, 140, 150 },
				{ 160, 170, 180, 190, 200 }, 
				{ 210, 220, 230, 240, 250 } };
		String pathEntrada = "D:\\Proyectos\\unlam\\FiltroMediana\\assets\\testLecturaP2.pgm";
		int[][] resultado = imagenesPGM.leerPGMP2("P2", 5, 5, pathEntrada);

		assertArrayEquals(resultado, matrizEsperada);
	}
}
