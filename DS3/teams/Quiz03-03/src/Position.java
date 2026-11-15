public record Position(int row, int column){ /*Position representa una casilla específica de la matriz/tablero donde se mueve la snake. 
												Una snake es un conjunto ordenado de positions p0,p1,...,pk-1 		
												Al usar record en vez de class, se crean automáticamente los atributos row y column, se crea automáticamente el método constructor y métodos para leer los valores row y column de un objeto de record Position.
												Sin embargo, una vez creados los atributos de un objeto basado en records, dichos atributos son inmutables. Aunque lo que sí puede hacerse es reasignar un objeto a una variable ya utilizada.
													*/
}

